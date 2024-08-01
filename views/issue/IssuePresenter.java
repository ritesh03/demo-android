package com.maktoday.views.issue;

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
 * Created by cbl81 on 2/12/17.
 */

public class IssuePresenter implements IssueContract.Presenter {

    private IssueContract.View view;

    @Override
    public void apiIssue(String issue) {
        if (view != null) {
            view.setLoading(true);
            RestClient.getModalApiService().apiRaiseIssue(Prefs.get().getString(Constants.ACCESS_TOKEN, ""),
                    Constants.UNIQUE_APP_KEY, issue)
                    .enqueue(new Callback<ApiResponse>() {
                        @Override
                        public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                            GeneralFunction.dismissProgress();
                            if (response.isSuccessful()) {
                                if (view != null) {
                                    view.issueSuccess(response.body());
                                }
                            } else {
                                if (response.code() == UnAuthorized) {
                                    if (view != null) {
                                        view.sessionExpired();
                                    }
                                } else {
                                    try {
                                        if (view != null) {
                                            view.issueError(new JSONObject(response.errorBody().string()).getString("message"));
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
                                view.issueFailure(t.getMessage());
                            }
                        }

                    });
        }
    }

    @Override
    public void attachView(IssueContract.View view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        view = null;
    }
}
