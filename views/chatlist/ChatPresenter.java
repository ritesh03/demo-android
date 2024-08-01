package com.maktoday.views.chatlist;

import com.maktoday.model.ApiResponse;
import com.maktoday.model.PojoChatList;
import com.maktoday.utils.Constants;
import com.maktoday.utils.GeneralFunction;
import com.maktoday.utils.Prefs;
import com.maktoday.webservices.RestClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by cbl1005 on 24/1/18.
 */

public class ChatPresenter implements ChatContract.Presenter {

    private ChatContract.View view;

    @Override
    public void apiGetAllChat() {
        if (view != null) {
            RestClient.getModalApiService().apiGetAllChat(Prefs.get().getString(Constants.ACCESS_TOKEN, ""), Constants.UNIQUE_APP_KEY, "USER")
                    .enqueue(new Callback<ApiResponse<List<PojoChatList>>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<List<PojoChatList>>> call, Response<ApiResponse<List<PojoChatList>>> response) {
                            GeneralFunction.dismissProgress();
                            if (response.isSuccessful()) {
                                if (view != null) {
                                    view.successGetAllChat(response.body().getData());
                                }
                            } else {
                                if (response.code() == Constants.UnAuthorized) {
                                    if (view != null) {
                                        view.sessionExpired();
                                    }
                                } else {
                                    try {
                                        if (view != null) {
                                            view.chatError(new JSONObject(response.errorBody().string()).getString("message"));
                                        }
                                    } catch (JSONException | IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<List<PojoChatList>>> call, Throwable t) {
                            if (view != null) {
                                GeneralFunction.dismissProgress();
                                view.chatFailure(t.getMessage());
                            }
                        }
                    });
        }
    }

    @Override
    public void attachView(ChatContract.View view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        view = null;
    }
}
