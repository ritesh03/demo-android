package com.maktoday.views.confirmbook;

import com.google.gson.Gson;
import com.maktoday.model.ApiResponse;
import com.maktoday.model.FullAddress;
import com.maktoday.model.PojoLogin;
import com.maktoday.model.PojoSearchMaid;
import com.maktoday.model.PojoServiceNew;
import com.maktoday.model.PromoResponse;
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
 * Created by cbl81 on 12/12/17.
 */

public class ConfirmBookPresenter implements ConfirmBookContract.Presenter {

    private static final String TAG = "ConfirmBookPresenter";

    private ConfirmBookContract.View view;

    @Override
    public void attachView(ConfirmBookContract.View view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        view = null;
    }

    @Override
    public void apiSearchBulkMaid(HashMap<String, String> map) {
        Log.e(TAG, " ----------- apiSearchMaidBulk() ");

        if (view != null) {

            view.setLoading(true);

            Log.e(TAG, " ------------ bulk auth = " + Prefs.get().getString(Constants.ACCESS_TOKEN, ""));
            Log.e(TAG, " ----------- params = " + new Gson().toJson(map));

            RestClient.getModalApiService().apiAllListMaidBulk(Prefs.get().getString(Constants.ACCESS_TOKEN, ""), map).enqueue(new Callback<PojoSearchMaid>() {
                @Override
                public void onResponse(Call<PojoSearchMaid> call, Response<PojoSearchMaid> response) {
                    Log.e("checking test", new Gson().toJson(response));
                    // view.setRefreshing(false);
                    if (response.isSuccessful()) {
                        view.setLoading(false);

                        Log.e("1", "1");

                        if (response.body() != null) {
                            Log.e("2", "2");
                            if (view != null) {
                                android.util.Log.d(TAG, "onResponse: bulk:--"+new Gson().toJson(response.body()));
                                view.searchMaidSuccessBulk(response.body());
                            } else {

                            }
                        } else {

                        }
                    } else {
                        if (response.code() == UnAuthorized) {
                            view.setLoading(false);
                            view.sessionExpired();
                        } else {
                            view.setLoading(false);
                            try {
                                JSONObject errorJson = new JSONObject(response.errorBody().string());
                                if (errorJson.has("responseType") &&
                                        (errorJson.optString("responseType").equals("NOT_ABLE_TO_BOOK") ||
                                                errorJson.optString("responseType").equals("TIME_ERROR"))) {
                                    // view.saveAddressFailure(errorJson.optString("message"));
                                    Log.e(TAG, "time error");
                                    view.maidNotAvailable(errorJson.optString("message"));
                                } else {
                                    Log.e(TAG, "save address faliure");
                                        view.saveAddressFailure(errorJson.optString("message"));

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
                        view.setLoading(false);
                        //  view.setRefreshing(false);
                        view.saveAddressError(t.getMessage());
                    }
                }

            });
        }
    }

    @Override
    public void apiSaveAddress(FullAddress fullAddress) {

        Log.d("FullAddress", "dfdf");
        if (view != null) {

            view.setLoading(true);
            Log.d("FullAddress", new Gson().toJson(fullAddress) + " ");
            Log.d("header", Prefs.get().getString(Constants.ACCESS_TOKEN, "") + " ");
            RestClient.getModalApiService().apiSaveAddress(Prefs.get().getString(Constants.ACCESS_TOKEN, ""), fullAddress)
                    .enqueue(new Callback<ApiResponse<PojoLogin>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<PojoLogin>> call, Response<ApiResponse<PojoLogin>> response) {
                            GeneralFunction.dismissProgress();
                            if (response.isSuccessful()) {
                                if (view != null) {
                                    view.saveAddressSuccess(response.body());
                                }
                            } else {
                                if (response.code() == UnAuthorized) {
                                    if (view != null) {
                                        view.sessionExpired();
                                    }
                                } else {
                                    try {
                                        if (view != null) {
                                            view.saveAddressError(new JSONObject(response.errorBody().string()).getString("message"));
                                        }
                                    } catch (JSONException | IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<PojoLogin>> call, Throwable t) {
                            if (view != null) {
                                GeneralFunction.dismissProgress();
                                view.saveAddressFailure(t.getMessage());
                            }
                        }

                    });
        }
    }

    @Override
    public void apiApplyPromo(HashMap<String, String> map) {
        if (view != null) {

            view.setLoading(true);
            Log.e("params1", map + "");
            RestClient.getModalApiService().apiApplyPromo(Prefs.get().getString(Constants.ACCESS_TOKEN, ""), map).enqueue(new Callback<PromoResponse>() {
                @Override
                public void onResponse(Call<PromoResponse> call, Response<PromoResponse> response) {
                    GeneralFunction.dismissProgress();
                    Log.e("checking test", new Gson().toJson(response.body()));
                    // view.setRefreshing(false);
                    if (response.isSuccessful()) {
                        Log.e("1", "1");

                        if (response.body() != null) {
                            Log.e("2", "2");
                            if (view != null) {
                                view.applyPromoSuccess(response.body());
                            } else {

                            }
                        } else {
                            Log.e("3", "3");
                        }
                    } else {
                        Log.e("4", "4");
                        if (response.code() == UnAuthorized) {
                            Log.e("5", "5");
                            view.sessionExpired();
                        } else {
                            Log.e("6", "6");
                            try {
                                JSONObject errorJson = new JSONObject(response.errorBody().string());
                                view.promoError(errorJson.optString("message"));
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<PromoResponse> call, Throwable t) {
                    if (view != null) {
                        GeneralFunction.dismissProgress();
                        //  view.setRefreshing(false);
                        view.promoError(t.getMessage());
                    }
                }

            });
        }
    }

    @Override
    public void apiDeleteAddress(HashMap<String, String> map) {
        if (view != null) {
            view.setLoading(true);
            Log.e("delete params", map.toString());

            RestClient.getModalApiService().apiDelete(Prefs.get().getString(Constants.ACCESS_TOKEN, ""), map)
                    .enqueue(new Callback<ApiResponse<PojoLogin>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<PojoLogin>> call, Response<ApiResponse<PojoLogin>> response) {
                            GeneralFunction.dismissProgress();
                            if (response.isSuccessful()) {
                                if (view != null) {
                                    view.deleteAddressSuccess(response.body());
                                }
                            } else {
                                if (response.code() == UnAuthorized) {
                                    if (view != null) {
                                        view.sessionExpired();
                                    }
                                } else {
                                    try {
                                        if (view != null) {
                                            view.saveAddressError(new JSONObject(response.errorBody().string()).getString("message"));
                                        }
                                    } catch (JSONException | IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<PojoLogin>> call, Throwable t) {
                            if (view != null) {
                                GeneralFunction.dismissProgress();
                                view.saveAddressFailure(t.getMessage());
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
                                }

                            } else {
                                if (response.code() == UnAuthorized) {
                                    view.sessionExpired();
                                } else {
                                    try {
                                        JSONObject errorJson = new JSONObject(response.errorBody().string());
                                        String errorMessage = errorJson.optString("message");

                                        final String responseType = errorJson.optString("responseType");
                                        Log.e("responsetype", responseType + "");
                                        if (responseType.equals("ALREADY_BOOK") ||
                                                responseType.equals("MAID_BUSY") ||
                                                responseType.equals("MAID_NOT_AVAILABLE")) {

                                            if (reschduleStatus.equalsIgnoreCase("yes")) {
                                                view.reschduleError("error");
                                            }

                                        } else {
                                            view.promoError(errorMessage);
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
                                view.promoError(t.getMessage());
                            }
                        }

                    });
        }
    }

}
