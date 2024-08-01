package com.maktoday.views.login;

import com.maktoday.model.ApiResponse;
import com.maktoday.model.FacebookModel;
import com.maktoday.model.PojoLogin;
import com.maktoday.utils.GeneralFunction;
import com.maktoday.utils.Log;
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
 * Created by cbl81 on 13/11/17.
 */

public class LoginPresenter implements LoginContract.Presenter {

    private LoginContract.View view;

    @Override
    public void apiLogin(HashMap<String, String> map) {
        if (view != null) {
            Log.e("login params", map.toString());
            view.setLoading(true);
            RestClient.getModalApiService().apiEmailLogin(map).enqueue(new Callback<ApiResponse<PojoLogin>>() {
                @Override
                public void onResponse(Call<ApiResponse<PojoLogin>> call, Response<ApiResponse<PojoLogin>> response) {
                    GeneralFunction.dismissProgress();
                    if (response.isSuccessful()) {
                        if (view != null) {
                            view.signupSuccess(response.body());
                        }
                    } else {
                        if (response.code() == UnAuthorized) {
                            if (view != null) {
                                view.sessionExpired();
                            }
                        } else {
                            try {
                                if (view != null) {
                                    view.signupError(new JSONObject(response.errorBody().string()).getString("message"));
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
                        view.signupFailure(t.getMessage());
                    }
                }



            });
        }
    }

    @Override
    public void attachView(LoginContract.View view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        view = null;
    }

    @Override
    public void apiFacebook(FacebookModel facebookModel) {
        if (view != null) {

            view.setLoading(true);
            RestClient.getModalApiService().apiFacebookLogin(facebookModel).enqueue(new Callback<ApiResponse<PojoLogin>>() {
                @Override
                public void onResponse(Call<ApiResponse<PojoLogin>> call, Response<ApiResponse<PojoLogin>> response) {
                    GeneralFunction.dismissProgress();
                    if (response.isSuccessful()) {
                        if (view != null) {
                            view.signupSuccesss(response.body());
                        }
                    } else {
                        if (response.code() == UnAuthorized) {
                            if (view != null) {
                                view.sessionExpired();
                            }
                        } else {
                            try {
                                view.signupError(new JSONObject(response.errorBody().string()).getString("message"));
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
                        view.signupFailure(t.getMessage());
                    }
                }

            });
        }
    }

}
