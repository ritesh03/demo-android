package com.maktoday.views.addNewCard;

import com.maktoday.model.ApiResponse;
import com.maktoday.model.PojoAddCard;
import com.maktoday.model.PojoCreatePayment;
import com.maktoday.utils.Constants;
import com.maktoday.utils.GeneralFunction;
import com.maktoday.utils.Prefs;
import com.maktoday.webservices.RestClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.maktoday.utils.Constants.UnAuthorized;

import com.maktoday.utils.Log;

/**
 * Created by cbl81 on 23/11/17.
 */

public class AddCardPresenter implements AddCardContract.Presenter {

    AddCardContract.View view;
    private static final String TAG = "AddCardPresenter";

    @Override
    public void apiCreatePayment(HashMap<String, String> map) {

        if (view != null) {

            view.setLoading(true);
            RestClient.getModalApiService().apiCreatePayment(Prefs.get().getString(Constants.ACCESS_TOKEN, ""), map)
                    .enqueue(new Callback<PojoCreatePayment>() {
                        @Override
                        public void onResponse(Call<PojoCreatePayment> call, Response<PojoCreatePayment> response) {
                            GeneralFunction.dismissProgress();
                            if (response.isSuccessful()) {
                                if (view != null) {
                                    view.createPaymentSuccess(response.body().data);
                                }
                            } else {
                                if (response.code() == UnAuthorized) {
                                    if (view != null) {
                                        view.sessionExpired();
                                    }
                                } else {
                                    try {
                                        if (view != null) {
                                            view.paymentError(new JSONObject(response.errorBody().string()).getString("message"));
                                        }
                                    } catch (JSONException | IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<PojoCreatePayment> call, Throwable t) {
                            if (view != null) {
                                GeneralFunction.dismissProgress();
                                Log.d(TAG, "onFailure: "+ t.getMessage());
                                view.paymentFailure(t.getMessage());
                            }
                        }

                    });
        }
    }

    @Override
    public void apiAddPayment(HashMap<String, String> map) {

        if (view != null) {

            view.setLoading(true);
            RestClient.getModalApiService().apiAddCard(Prefs.get().getString(Constants.ACCESS_TOKEN, ""), map)
                    .enqueue(new Callback<ApiResponse<PojoAddCard>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<PojoAddCard>> call, Response<ApiResponse<PojoAddCard>> response) {
                            GeneralFunction.dismissProgress();
                            if (response.isSuccessful()) {
                                if (view != null) {
                                    view.addCardSuccess(response.body().getData().cardToken);
                                }
                            } else {
                                if (response.code() == UnAuthorized) {
                                    if (view != null) {
                                        view.sessionExpired();
                                    }
                                } else {
                                    try {
                                        if (view != null) {
                                            view.paymentError(new JSONObject(response.errorBody().string()).getString("message"));

                                        }
                                    } catch (JSONException | IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<PojoAddCard>> call, Throwable t) {
                            if (view != null) {
                                GeneralFunction.dismissProgress();
                                view.paymentFailure(t.getMessage());
                            }
                        }

                    });
        }
    }


    @Override
    public void attachView(AddCardContract.View view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        view = null;
    }
}
