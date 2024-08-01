package com.maktoday.views.cardlist;

import com.google.gson.Gson;
import com.maktoday.model.ApiResponse;
import com.maktoday.model.PaytabTransactionVerificationResponse;
import com.maktoday.model.PojoAddCard;
import com.maktoday.model.PojoCardList;
import com.maktoday.model.PojoCreatePayment;
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
 * Created by cbl81 on 23/11/17.
 */

public class CardListPresenter implements CardListContract.Presenter {

    private static final String TAG = "CardListPresenter";

    private CardListContract.View view;

    @Override
    public void apiCardList() {
        if (view != null) {

            view.setLoading(true);
            RestClient.getModalApiService().apiGetCards(Prefs.get().getString(Constants.ACCESS_TOKEN, ""), Constants.UNIQUE_APP_KEY)
                    .enqueue(new Callback<PojoCardList>() {
                        @Override
                        public void onResponse(Call<PojoCardList> call, Response<PojoCardList> response) {
                            GeneralFunction.dismissProgress();
                            if (response.isSuccessful()) {
                                if (view != null) {
                                    view.cardListSuccess(response.body());
                                }
                            } else {
                                if (response.code() == UnAuthorized) {
                                    if (view != null) {
                                        view.sessionExpired();
                                    }
                                } else {
                                    try {
                                        if (view != null) {
                                            view.error(new JSONObject(response.errorBody().string()).getString("message"));
                                        }
                                    } catch (JSONException | IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<PojoCardList> call, Throwable t) {
                            if (view != null) {
                                GeneralFunction.dismissProgress();
                                view.failure(t.getMessage());
                            }
                        }

                    });
        }
    }

    @Override
    public void apiCreatePayment(HashMap<String, String> map) {
        Log.e(TAG, " ---------- api CreatePayment() ");
        if (view != null) {

            Log.e(TAG, " -------- params = " + map);

            view.setLoading(true);
            RestClient.getModalApiService().apiCreatePayment(Prefs.get().getString(Constants.ACCESS_TOKEN, ""), map)
                    .enqueue(new Callback<PojoCreatePayment>() {
                        @Override
                        public void onResponse(Call<PojoCreatePayment> call, Response<PojoCreatePayment> response) {
                            Log.e(TAG, " ---------- onResponse : \n" + new Gson().toJson(response));

                            GeneralFunction.dismissProgress();
                            if (response.isSuccessful()) {
                                view.createPaymentSuccess(response.body().data);

                            } else {
                                if (response.code() == UnAuthorized) {
                                    view.sessionExpired();
                                } else {
                                    try {
                                        view.error(new JSONObject(response.errorBody().string()).getString("message"));
                                    } catch (JSONException | IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<PojoCreatePayment> call, Throwable t) {
                            Log.e(TAG, " -------- onFailure : t = " + t.getMessage());
                            t.printStackTrace();

                            if (view != null) {
                                GeneralFunction.dismissProgress();
                                view.failure(t.getMessage());
                            }
                        }

                    });
        }
    }


    @Override
    public void apiPaytabTransactionVerification(HashMap<String, String> map) {
        if (view != null) {

            view.setLoading(true);
            RestClient.getPayTabApiService().apiPayTabTransactionVerification(Prefs.get().getString(Constants.ACCESS_TOKEN, ""), map)
                    .enqueue(new Callback<PaytabTransactionVerificationResponse>() {
                        @Override
                        public void onResponse(Call<PaytabTransactionVerificationResponse> call, Response<PaytabTransactionVerificationResponse> response) {
                            GeneralFunction.dismissProgress();
                            if (response.isSuccessful()) {
                                view.payTabTransactionSuccess(response.body());

                            } else {
                                if (response.code() == UnAuthorized) {
                                    view.sessionExpired();
                                } else {
                                    try {
                                        view.error(new JSONObject(response.errorBody().string()).getString("message"));
                                    } catch (JSONException | IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<PaytabTransactionVerificationResponse> call, Throwable t) {
                            if (view != null) {
                                GeneralFunction.dismissProgress();
                                view.failure(t.getMessage());
                            }
                        }

                    });
        }
    }

    @Override
    public void apiCardDelete(HashMap<String, String> map) {
        if (view != null) {

            view.setLoading(true);
            RestClient.getModalApiService().apiDeleteCard(Prefs.get().getString(Constants.ACCESS_TOKEN, ""), map)
                    .enqueue(new Callback<ApiResponse>() {
                        @Override
                        public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                            GeneralFunction.dismissProgress();
                            if (response.isSuccessful()) {

                                view.deleteCardSuccess();
                            } else {
                                if (response.code() == UnAuthorized) {
                                    view.sessionExpired();
                                } else {
                                    try {
                                        view.error(new JSONObject(response.errorBody().string()).getString("message"));
                                    } catch (JSONException | IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse> call, Throwable t) {
                            if (view != null) {
                                GeneralFunction.dismissProgress();
                                view.failure(t.getMessage());
                            }
                        }

                    });
        }
    }

    @Override
    public void apiCardPaymentPaytabs(HashMap<String, String> map) {
        Log.e(TAG, " ---------- api CardPaymentPaytabs() ");

        if (view != null) {
            Log.e(TAG, " -------- params = " + map);

            view.setLoading(true);

            RestClient.getModalApiService().apiCreatePaymentPayTabs(Prefs.get().getString(Constants.ACCESS_TOKEN, ""), map)
                    .enqueue(new Callback<PojoCreatePayment>() {

                        @Override
                        public void onResponse(Call<PojoCreatePayment> call, Response<PojoCreatePayment> response) {
                            Log.e(TAG, " ---------- onResponse : \n" + new Gson().toJson(response));

                            GeneralFunction.dismissProgress();
                            if (response.isSuccessful()) {

                                view.successPaymentPaytabs(response.body().data);
                            } else {
                                if (response.code() == UnAuthorized) {
                                    view.sessionExpired();
                                } else {
                                    try {
                                        view.error(new JSONObject(response.errorBody().string()).getString("message"));
                                    } catch (JSONException | IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<PojoCreatePayment> call, Throwable t) {
                            Log.e(TAG, " -------- onFailure : t = " + t.getMessage());
                            t.printStackTrace();

                            if (view != null) {
                                GeneralFunction.dismissProgress();
                                view.failure(t.getMessage());
                            }
                        }

                    });
        }
    }

    @Override
    public void apiCardPaymentPaytabsBulk(HashMap<String, String> map) {
        Log.e(TAG, " ---------- api CardPaymentPaytabsBulk() ");
        if (view != null) {
            Log.e(TAG, " -------- params = " +new Gson().toJson(map));
            view.setLoading(true);
            RestClient.getModalApiService().apiCreatePaymentPayTabsBulk(Prefs.get().getString(Constants.ACCESS_TOKEN, ""), map)
                    .enqueue(new Callback<PojoCreatePayment>() {
                        @Override
                        public void onResponse(Call<PojoCreatePayment> call, Response<PojoCreatePayment> response) {
                            Log.e(TAG, " ---------- onResponse : \n" + new Gson().toJson(response));
                            GeneralFunction.dismissProgress();
                            if (response.isSuccessful()) {

                                view.successPaymentPaytabs(response.body().data);
                            } else {
                                if (response.code() == UnAuthorized) {
                                    view.sessionExpired();
                                } else {
                                    try {
                                        view.error(new JSONObject(response.errorBody().string()).getString("message"));
                                    } catch (JSONException | IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<PojoCreatePayment> call, Throwable t) {
                            Log.e(TAG, " -------- onFailure : t = " + t.getMessage());
                            t.printStackTrace();

                            if (view != null) {
                                GeneralFunction.dismissProgress();
                                view.failure(t.getMessage());
                            }
                        }

                    });
        }
    }

    @Override
    public void apiAddPayment(HashMap<String, String> map) {
        Log.e(TAG, " ---------- api AddPayment() ");

        if (view != null) {
            Log.e(TAG, " -------- params = " + map);

            view.setLoading(true);
            RestClient.getModalApiService().apiAddCard(Prefs.get().getString(Constants.ACCESS_TOKEN, ""), map)
                    .enqueue(new Callback<ApiResponse<PojoAddCard>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<PojoAddCard>> call, Response<ApiResponse<PojoAddCard>> response) {
                            Log.e(TAG, " ---------- onResponse : \n" + new Gson().toJson(response));

                            GeneralFunction.dismissProgress();
                            if (response.isSuccessful()) {
                                if (view != null) {
                                    view.addCardAddedSuccess(response.body().getData().cardToken);
                                }
                            } else {
                                if (response.code() == UnAuthorized) {
                                    if (view != null) {
                                        view.sessionExpired();
                                    }
                                } else {
                                    try {
                                        if (view != null) {
                                            view.error(new JSONObject(response.errorBody().string()).getString("message"));

                                        }
                                    } catch (JSONException | IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<PojoAddCard>> call, Throwable t) {
                            Log.e(TAG, " -------- onFailure : t = " + t.getMessage());
                            t.printStackTrace();
                            if (view != null) {

                                GeneralFunction.dismissProgress();
                                view.failure(t.getMessage());
                            }
                        }

                    });
        }
    }

    public void apiChangeLanguage(final String language) {
        Log.e(TAG, " -------- apiChangeLanguage() ");

        if (view != null) {
            Log.e(TAG, " -------- unique key = " + Constants.UNIQUE_APP_KEY);
            Log.e(TAG, " -------- language = " + language);

            view.setLoading(true);
            RestClient.getModalApiService().apiChangeLanguage(Prefs.get().getString(Constants.ACCESS_TOKEN, ""),
                    Constants.UNIQUE_APP_KEY, language)
                    .enqueue(new Callback<ApiResponse>() {
                        @Override
                        public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                            Log.e(TAG, " ---------- onResponse : \n" + new Gson().toJson(response));
                            GeneralFunction.dismissProgress();
                            if (response.isSuccessful()) {
                                if (view != null) {
                                    view.successLanguageChange(language);
                                }
                            } else {
                                if (response.code() == UnAuthorized) {
                                    if (view != null) {
                                        view.sessionExpired();
                                    }
                                } else {
                                    try {
                                        if (view != null) {
                                            view.error(new JSONObject(response.errorBody().string()).getString("message"));

                                        }
                                    } catch (JSONException | IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse> call, Throwable t) {
                            Log.e(TAG, " -------- onFailure : t = " + t.getMessage());
                            t.printStackTrace();

                            if (view != null) {
                                GeneralFunction.dismissProgress();
                                view.failure(t.getMessage());
                            }

                        }

                    });
        }
    }


    @Override
    public void attachView(CardListContract.View view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        this.view = null;
    }
}
