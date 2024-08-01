package com.maktoday.views.extendservice;

import com.maktoday.model.ApiResponse;
import com.maktoday.utils.Constants;
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
 * Created by cbl81 on 3/12/17.
 */

public class ExtendServicePresenter implements ExtendServiceContract.Presenter {

    ExtendServiceContract.View view;

    @Override
    public void attachView(ExtendServiceContract.View view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        view = null;
    }

    @Override
    public void apiExtendService(HashMap<String, String> map) {
        if (view != null) {

            view.setLoading(true);
            RestClient.getModalApiService().apiExtendService(Prefs.get().getString(Constants.ACCESS_TOKEN, ""), map).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    view.setLoading(false);
                    if (response.isSuccessful()) {
                        if (view != null) {
                            view.extendServiceSuccess(response.body());
                        }
                    } else {
                        if (response.code() == UnAuthorized) {
                            if (view != null) {
                                view.sessionExpired();
                            }
                        } else {
                            try {
                                if (view != null) {
                                    view.extendServiceError(new JSONObject(response.errorBody().string()).getString("message"));
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
                        view.setLoading(false);
                        view.extendServiceFailure(t.getMessage());
                    }
                }

            });
        }
    }

}
