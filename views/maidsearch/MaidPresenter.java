package com.maktoday.views.maidsearch;


import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.maktoday.model.ApiResponse;
import com.maktoday.model.GoogleTimeZoneResponse;
import com.maktoday.model.MaidData;
import com.maktoday.model.PojoSearchMaid;
import com.maktoday.utils.Constants;
import com.maktoday.utils.Log;
import com.maktoday.utils.Prefs;
import com.maktoday.webservices.RestClient;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.maktoday.utils.Constants.UnAuthorized;

/**
 * Created by cbl81 on 16/11/17.
 */

public class MaidPresenter implements MaidContract.Presenter {
    private static final String TAG = "MaidPresenter";

    Call<PojoSearchMaid> call;
    private MaidContract.View view;

    @Override
    public void attachView(MaidContract.View view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        view = null;
    }

    @Override
    public void apiSearchMaid(HashMap<String, String> map, boolean isSearch) {
        Log.e(TAG, " ----------- apiSearchMaid() ");
        if (view != null) {

            view.setRefreshing(!isSearch);
            if (call != null) {
                call = null;
            }

            Log.e(TAG, " ----------- token = " + Prefs.get().getString(Constants.ACCESS_TOKEN, ""));
            Log.e(TAG, " ----------- params = " + new Gson().toJson(map));
            Log.e(TAG, " --------again--- params = " + new Gson().toJson(map));

            call = RestClient.getModalApiService().apiAllListMaid(Prefs.get().getString(Constants.ACCESS_TOKEN, ""), map);
            call.enqueue(new Callback<PojoSearchMaid>() {
                @Override
                public void onResponse(Call<PojoSearchMaid> call, Response<PojoSearchMaid> response) {
                    if (view == null)
                        return;

                    view.setRefreshing(false);
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            view.searchMaidSuccess(response.body());
                        }
                    } else {
                        if (response.code() == UnAuthorized) {
                            view.sessionExpired();
                        } else {
                            try {
                                JSONObject errorJson = new JSONObject(response.errorBody().string());
                                if (errorJson.has("responseType") &&
                                        (errorJson.optString("responseType").equals("NOT_ABLE_TO_BOOK") ||
                                                errorJson.optString("responseType").equals("TIME_ERROR"))) {
                                    view.maidNotAvailable(errorJson.optString("message"));
                                } else {
                                    view.signupFailure(errorJson.optString("message"));
                                }
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<PojoSearchMaid> call, Throwable t) {
                    if (view != null) {
                        view.setRefreshing(false);
                        view.signupFailure(t.getMessage());
                    }
                }

            });
        }
    }

    @Override
    public void apiSearchBulkMaid(HashMap<String, String> map) {
        Log.e(TAG, " ------------ apiSearchMaidBuld() ");

        if (view != null) {

            if (call != null) {
                call = null;
            }

            Log.e(TAG, " ------------ token = " + Prefs.get().getString(Constants.ACCESS_TOKEN, ""));
            Log.e(TAG, " ------------ params = " + map);

            call = RestClient.getModalApiService().apiAllListMaidBulk(Prefs.get().getString(Constants.ACCESS_TOKEN, ""), map);
            call.enqueue(new Callback<PojoSearchMaid>() {
                @Override
                public void onResponse(Call<PojoSearchMaid> call, Response<PojoSearchMaid> response) {

                    //view.setRefreshing(false);
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            view.searchMaidSuccess(response.body());
                        }
                    } else {
                        if (response.code() == UnAuthorized) {
                            view.sessionExpired();
                        } else {
                            try {
                                JSONObject errorJson = new JSONObject(response.errorBody().string());
                                if (errorJson.has("responseType") &&
                                        (errorJson.optString("responseType").equals("NOT_ABLE_TO_BOOK") ||
                                                errorJson.optString("responseType").equals("TIME_ERROR"))) {
                                    view.maidNotAvailable(errorJson.optString("message"));
                                } else {
                                    view.signupFailure(errorJson.optString("message"));
                                }
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<PojoSearchMaid> call, Throwable t) {
                    if (view != null) {
                        view.setRefreshing(false);
                        view.signupFailure(t.getMessage());
                    }
                }

            });
        }
    }

    @Override
    public void getTimeZoneFromLatLong(double latitude, double longitude, String googleApiKey) {
        view.setLoading(true);

        final HashMap<String, String> params = new HashMap<>();
        params.put("location", latitude + "," + longitude);
        params.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
        params.put("key", googleApiKey);

        RestClient.getModalApiService()
                .getTimeZoneFromLatLng(params)
                .enqueue(new Callback<GoogleTimeZoneResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<GoogleTimeZoneResponse> call, @NonNull Response<GoogleTimeZoneResponse> response) {
                        if (view == null)
                            return;
                        view.setLoading(false);
                        final GoogleTimeZoneResponse timeZoneResponse = response.body();
                        Log.d("TimeZone", timeZoneResponse == null ? "" : timeZoneResponse.toString());
                        if (response.isSuccessful() && timeZoneResponse != null && timeZoneResponse.timeZoneId != null) {
                            view.bookingTimeZoneReceived(TimeZone.getTimeZone(timeZoneResponse.timeZoneId));
                        } else {
                            view.bookingTimeZoneReceived(null);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<GoogleTimeZoneResponse> call, @NonNull Throwable t) {
                        if (view != null) {
                            view.setLoading(false);
                            view.signupFailure(t.getMessage());
                        }
                    }
                });
    }

    @Override
    public void getTimeSlots(String maidId, String bookingTimeZone) {
        view.setLoading(true);
        String accessToken = Prefs.get().getString(Constants.ACCESS_TOKEN, "");
        RestClient.getModalApiService().apiGetSlots(accessToken, Constants.UNIQUE_APP_KEY, maidId, bookingTimeZone)
                .enqueue(new Callback<ApiResponse<MaidData>>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiResponse<MaidData>> call, @NonNull Response<ApiResponse<MaidData>> response) {
                        if (view == null)
                            return;

                        view.setLoading(false);
                        if (response.isSuccessful()) {
                            view.displayTimeSlots(response.body().getData().timeSlot);
                        } else {
                            if (response.code() == UnAuthorized) {
                                view.sessionExpired();
                            } else {
                                try {
                                    JSONObject errorJson = new JSONObject(response.errorBody().string());
                                    String errorMessage = errorJson.optString("message");
                                    view.signupFailure(errorMessage);
                                } catch (JSONException | IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiResponse<MaidData>> call, @NonNull Throwable t) {
                        if (view != null) {
                            view.setLoading(false);
                            view.signupFailure(t.getMessage());
                        }
                    }
             });
    }
}
