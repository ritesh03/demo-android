package com.maktoday.views.changePassword;

import com.maktoday.model.ApiResponse;
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

/**
 * Created by cbl81 on 2/12/17.
 */

public class ChangePasswordPresenter implements ChangePasswordContract.Presenter {

    private ChangePasswordContract.View view;

    @Override
    public void apiChangePassword(HashMap<String, String> map) {
        if (view != null) {
            view.setLoading(true);
            RestClient.getModalApiService().apiChangePassword(Prefs.get().getString(Constants.ACCESS_TOKEN, ""), map).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    GeneralFunction.dismissProgress();
                    if (response.isSuccessful()) {
                        if (view != null) {
                            view.changePasswordSuccess(response.body());
                        }
                    } else {
                        if (response.code() == UnAuthorized) {
                            if (view != null) {
                                view.sessionExpired();
                            }
                        } else {
                            try {
                                if (view != null) {
                                    view.changePasswordError(new JSONObject(response.errorBody().string()).getString("message"));
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
                        view.changePasswordFailure(t.getMessage());
                    }
                }

            });
        }
    }

    @Override
    public void attachView(ChangePasswordContract.View view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        view = null;
    }
}
