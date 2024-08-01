package com.maktoday.views.authenticate;

import com.maktoday.model.ApiResponse;
import com.maktoday.model.FacebookModel;
import com.maktoday.model.PojoLogin;
import com.maktoday.utils.GeneralFunction;
import com.maktoday.webservices.RestClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.maktoday.utils.Constants.UnAuthorized;

/**
 * Created by cbl81 on 15/11/17.
 */

public class AuthenticatePresenter implements AuthenticateContract.Presenter {

    private AuthenticateContract.View view;

    @Override
    public void attachView(AuthenticateContract.View view) {
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
                            view.signupSuccess(response.body());
                        }
                    } else {
                        if (response.code() == UnAuthorized) {
                            if (view != null) {
                                view.sessionExpired();
                            }
                        } else {
                            try {
                                view.signupError(new JSONObject(response.errorBody().string()).getString("message"));
                                //view.signupFailure(response.errorBody().string());
                            }

                            catch (JSONException | IOException e) {
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
