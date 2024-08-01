package com.maktoday.views.maidprofile;

import com.google.gson.Gson;
import com.maktoday.model.ApiResponse;
import com.maktoday.model.BookServiceModel;
import com.maktoday.model.PojoMaidProfile;
import com.maktoday.model.PojoServiceNew;
import com.maktoday.utils.Constants;
import com.maktoday.utils.GeneralFunction;
import com.maktoday.utils.Log;
import com.maktoday.utils.Prefs;
import com.maktoday.webservices.RestClient;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.maktoday.utils.Constants.UnAuthorized;

/**
 * Created by cbl81 on 17/11/17.
 */

public class MaidProfilePresenter implements MaidProfileContract.Presenter {

    private static final String TAG = "MaidProfilePresenter";

    private MaidProfileContract.View view;

    @Override
    public void attachView(MaidProfileContract.View view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        view = null;
    }

    @Override
    public void apiSearchMaid(String maidID) {
        if (view != null) {

            Log.e("ppppparam", Prefs.get().getString(Constants.ACCESS_TOKEN, "") + "," + Constants.UNIQUE_APP_KEY);
            Log.e("ppppparam", Prefs.get().getString(Constants.ACCESS_TOKEN, "") + "," + Constants.UNIQUE_APP_KEY);

            RestClient.getModalApiService().apiGetMaidProfile(Prefs.get().getString(Constants.ACCESS_TOKEN, ""),
                    Constants.UNIQUE_APP_KEY, maidID).enqueue(new Callback<ApiResponse<PojoMaidProfile>>() {
                @Override
                public void onResponse(Call<ApiResponse<PojoMaidProfile>> call, Response<ApiResponse<PojoMaidProfile>> response) {

                    if (response.isSuccessful()) {
                        if (view != null && response.body().getData()
                                != null) {

                            view.getMaidSuccess(response.body().getData());
                        }
                    } else {
                        if (response.code() == UnAuthorized) {
                            if (view != null) {
                                view.sessionExpired();
                            }
                        } else {
                            try {
                                if (view != null) {
                                    view.getMaidError(new JSONObject(response.errorBody().string()).getString("message"));
                                }
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<PojoMaidProfile>> call, Throwable t) {
                    if (view != null) {
                        view.getMaidFailure(t.getMessage());
                    }
                }
            });
        }
    }


    @Override
    public void apiAddFavouriteMaid(String maidID) {
        if (view != null) {
            RestClient.getModalApiService().addFavouriteMaid(Prefs.get().getString(Constants.ACCESS_TOKEN, ""),
                    Constants.UNIQUE_APP_KEY, maidID).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    if (response.isSuccessful()) {
                        if (view != null) {
                            view.successAddMaid();
                        }
                    } else {
                        if (response.code() == UnAuthorized) {
                            if (view != null) {
                                view.sessionExpired();
                            }
                        } else {
                            try {
                                if (view != null) {
                                    view.getMaidError(new JSONObject(response.errorBody().string()).getString("message"));
                                }
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    if (view != null) {
                        view.getMaidFailure(t.getMessage());
                    }
                }

            });
        }
    }

    @Override
    public void apiRemoveFavouriteMaid(String maidID) {
        if (view != null) {
            RestClient.getModalApiService().removeFavouriteMaid(Prefs.get().getString(Constants.ACCESS_TOKEN, ""),
                    Constants.UNIQUE_APP_KEY, maidID).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    if (response.isSuccessful()) {
                        if (view != null) {
                            view.successRemoveMaid();
                        }
                    } else {
                        if (response.code() == UnAuthorized) {
                            if (view != null) {
                                view.sessionExpired();
                            }
                        } else {
                            try {
                                if (view != null) {
                                    view.getMaidError(new JSONObject(response.errorBody().string()).getString("message"));
                                }
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    if (view != null) {
                        view.getMaidFailure(t.getMessage());
                    }
                }
            });
        }
    }

    @Override
    public void apiBookServiceAgain(final BookServiceModel bookServiceModel, final String accessToken, final String reschduleStatus) {
        Log.e(TAG, " ----------- in apiBookServiceAgain() ");

        if (view != null) {

            Log.e(TAG, " ----------- params = " + new Gson().toJson(bookServiceModel));

            view.setLoading(true);

            RestClient.getModalApiService().apiBookServiceAgain(accessToken, bookServiceModel)
                    .enqueue(new Callback<PojoServiceNew>() {
                        @Override
                        public void onResponse(Call<PojoServiceNew> call, Response<PojoServiceNew> response) {
                            Log.e(TAG, " ----------- in onResponse() " + response.body());

                            if (view == null)
                                return;

                            GeneralFunction.dismissProgress();
                            if (response.isSuccessful()) {
                                if (reschduleStatus.equalsIgnoreCase("yes")) {
                                    view.reschduleError("done");
                                } else {
                                    //  view.bookServiceSuccessNew(response.body());
                                }


                            } else {
                                if (response.code() == UnAuthorized) {
                                    view.sessionExpired();
                                } else {
                                    try {
                                        JSONObject errorJson = new JSONObject(response.errorBody().string());
                                        String errorMessage = errorJson.optString("message");
                                        /*
                                         * {"statusCode":400,"error":"Bad Request","message":"This maid is already booked.","responseType":"ALREADY_BOOK"}
                                         * */
                                        final String responseType = errorJson.optString("responseType");
                                        Log.e("responsetype", responseType + "");
                                        if (responseType.equals("ALREADY_BOOK") ||
                                                responseType.equals("MAID_BUSY") ||
                                                responseType.equals("MAID_NOT_AVAILABLE")) {

                                            if (reschduleStatus.equalsIgnoreCase("yes")) {
                                                view.reschduleError("error");
                                            } else {
                                                //    getTimeSlots(accessToken, bookServiceModel, errorMessage);
                                            }

                                        } else {
                                            view.getMaidFailure(errorMessage);
                                        }
                                    } catch (JSONException | IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<PojoServiceNew> call, Throwable t) {
                            Log.e(TAG, " ----------- in onFailure() , t = " + t.getMessage());
                            if (view != null) {
                                GeneralFunction.dismissProgress();
                                view.getMaidFailure(t.getMessage());
                            }
                        }

                    });
        }
    }


    @Override
    public void apiRescheduleBulk(HashMap<String, String> map, final String accessToken, final String reschduleStatus) {
        if (view != null) {

            view.setLoading(true);
            Log.e("book again", new Gson().toJson(map));
            RestClient.getModalApiService().apiRescheduleBulk(accessToken, map)
                    .enqueue(new Callback<PojoServiceNew>() {
                        @Override
                        public void onResponse(Call<PojoServiceNew> call, Response<PojoServiceNew> response) {
                            if (view == null)
                                return;
                            Log.e("book again", new Gson().toJson(response));
                            GeneralFunction.dismissProgress();
                            if (response.isSuccessful()) {
                                if (reschduleStatus.equalsIgnoreCase("yes")) {
                                    view.reschduleError("done");
                                } else {
                                    // view.bookServiceSuccessNew(response.body());
                                }


                            } else {
                                if (response.code() == UnAuthorized) {
                                    view.sessionExpired();
                                } else {
                                    try {
                                        JSONObject errorJson = new JSONObject(response.errorBody().string());
                                        String errorMessage = errorJson.optString("message");
                                        /*
                                         * {"statusCode":400,"error":"Bad Request","message":"This maid is already booked.","responseType":"ALREADY_BOOK"}
                                         * */
                                        final String responseType = errorJson.optString("responseType");
                                        Log.e("responsetype", responseType + "");
                                        if (responseType.equals("ALREADY_BOOK") ||
                                                responseType.equals("MAID_BUSY") ||
                                                responseType.equals("MAID_NOT_AVAILABLE")) {

                                            if (reschduleStatus.equalsIgnoreCase("yes")) {
                                                view.reschduleError("error");
                                            } else {
                                                // getTimeSlots(accessToken, bookServiceModel, errorMessage);
                                            }

                                        } else {
                                            view.getMaidFailure(errorMessage);
                                        }
                                    } catch (JSONException | IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<PojoServiceNew> call, Throwable t) {
                            if (view != null) {
                                GeneralFunction.dismissProgress();
                                view.getMaidFailure(t.getMessage());
                            }
                        }

                    });
        }
    }

}
