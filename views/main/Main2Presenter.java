package com.maktoday.views.main;

import com.maktoday.model.ApiResponse;
import com.maktoday.utils.Constants;
import com.maktoday.utils.GeneralFunction;
import com.maktoday.utils.Prefs;
import com.maktoday.webservices.RestClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.maktoday.utils.Constants.UnAuthorized;

/**
 * Created by cbl81 on 6/12/17.
 */

public class Main2Presenter implements Main2Contract.Presenter {

    private Main2Contract.View view;

    @Override
    public void attachView(Main2Contract.View view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        view = null;
    }

    @Override
    public void apilogout() {
        if (view != null) {
            view.setLoading(true);
            RestClient.getModalApiService().apiUserLogout(Prefs.get().getString(Constants.ACCESS_TOKEN, ""), Constants.UNIQUE_APP_KEY)
                    .enqueue(new Callback<ApiResponse>() {
                        @Override
                        public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                            GeneralFunction.dismissProgress();
                            if (response.isSuccessful()) {
                                if (view != null) {
                                    view.logoutSuccess(response.body());
                                }
                            } else {
                                if (response.code() == UnAuthorized) {
                                    if (view != null) {
                                        view.sessionExpired();
                                    }
                                } else {
                                    try {
                                        if (view != null) {
                                            view.logoutError(new JSONObject(response.errorBody().string()).getString("message"));
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
                                view.logoutFailure(t.getMessage());
                            }
                        }

                    });
        }
    }

}
