package com.maktoday.views.forgotpassword;

import com.maktoday.model.ApiResponse;
import com.maktoday.utils.Constants;
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
 * Created by cbl81 on 2/12/17.
 */

public class ForgotPasswordPresenter implements ForgotPasswordContract.Presenter {

    private ForgotPasswordContract.View view;

    @Override
    public void apiForgotPassword(String email) {
        if (view != null) {
            view.setLoading(true);
            RestClient.getModalApiService().apiForgetPassword(Constants.UNIQUE_APP_KEY, email).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    GeneralFunction.dismissProgress();
                    if (response.isSuccessful()) {
                        if (view != null) {
                            view.forgotPasswordSuccess(response.body());
                        }
                    } else {
                        if (response.code() == UnAuthorized) {
                            if (view != null) {
                                view.sessionExpired();
                            }
                        } else {
                            try {
                                if (view != null) {
                                    view.forgotPasswordError(new JSONObject(response.errorBody().string()).getString("message"));
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
                        GeneralFunction.dismissProgress();
                        view.forgotPasswordFailure(t.getMessage());
                    }
                }

            });
        }
    }

    @Override
    public void attachView(ForgotPasswordContract.View view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        view = null;
    }
}
