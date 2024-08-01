package com.maktoday.views.chat;

import com.maktoday.model.ApiResponse;
import com.maktoday.model.LatestMessage;
import com.maktoday.model.PojoChatData;
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

/**
 * Created by cbl1005 on 24/1/18.
 */

public class ChatPresenter implements ChatContract.Presenter {
    private ChatContract.View view;
    private static final String TAG = "ChatPresenter";

    @Override
    public void apiGetChatHistory(HashMap<String, String> map) {
        if (view != null) {
            RestClient.getModalApiService().apiGetChatHistory(Prefs.get().getString(Constants.ACCESS_TOKEN, ""), map)
                    .enqueue(new Callback<ApiResponse<PojoChatData>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<PojoChatData>> call, Response<ApiResponse<PojoChatData>> response) {
                            GeneralFunction.dismissProgress();
                            if (response.isSuccessful()) {
                                if (view != null) {
                                    view.successChatHistory(response.body().getData());
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
                        public void onFailure(Call<ApiResponse<PojoChatData>> call, Throwable t) {
                            if (view != null) {
                                GeneralFunction.dismissProgress();
                             //   view.chatFailure(t.getMessage());
                                Log.e(TAG, " get chat onFailure: "+t.getMessage());
                            }
                        }
                    });
        }
    }

    @Override
    public void apiCreateChat(HashMap<String, String> map) {
        if (view != null) {
            RestClient.getModalApiService().apiCreateChat(Prefs.get().getString(Constants.ACCESS_TOKEN, ""), map)
                    .enqueue(new Callback<ApiResponse<LatestMessage>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<LatestMessage>> call, Response<ApiResponse<LatestMessage>> response) {
                            GeneralFunction.dismissProgress();
                            if (response.isSuccessful()) {
                                if (view != null) {
                                    view.successCreateChat(response.body().getData());
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
                        public void onFailure(Call<ApiResponse<LatestMessage>> call, Throwable t) {
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
