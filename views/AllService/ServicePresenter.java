package com.maktoday.views.AllService;

import static com.maktoday.utils.Constants.UnAuthorized;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.maktoday.model.ApiResponse;
import com.maktoday.model.NotiCountResponse;
import com.maktoday.model.ServicelistResponse;
import com.maktoday.utils.Constants;
import com.maktoday.utils.GeneralFunction;
import com.maktoday.utils.Log;
import com.maktoday.webservices.RestClient;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServicePresenter implements ServiceContract.Presenter{
    private static final String TAG = "ServicePresenter";
    private ServiceContract.View view;
    @Override
    public void apiServicelist() {
        view.setLoading(true);

        RestClient.getModalApiService().getAllNormalService(Constants.UNIQUE_APP_KEY)
                .enqueue(new Callback<ServicelistResponse>() {
                    @Override
                    public void onResponse(Call<ServicelistResponse> call, Response<ServicelistResponse> response) {
                        if (view == null)
                            return;
                        GeneralFunction.dismissProgress();
                        Log.e(TAG, " ---------- service list = " + new Gson().toJson(response));
                        if (response.isSuccessful()){
                            view.ServiceListSuccess(response.body());
                        }else {
                            if (response.code() == UnAuthorized) {
                                view.sessionExpired();
                            } else {
                                try {
                                    JSONObject errorJson = new JSONObject(response.errorBody().string());
                                    String errorMessage = errorJson.optString("message");
                                    view.apiFailure(errorMessage);

                                } catch (JSONException | IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ServicelistResponse> call, Throwable t) {
                        if (view != null) {
                            GeneralFunction.dismissProgress();
                            Log.e("service list ===",""+t.getMessage());
                            view.apiFailure(t.getMessage());
                        }
                    }
                });
    }

    @Override
    public void getNotiCount(String accessToken) {
        //   view.setLoading(true);
        RestClient.getModalApiService().apiGetNotiCount(accessToken, Constants.UNIQUE_APP_KEY)
                .enqueue(new Callback<ApiResponse<NotiCountResponse>>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiResponse<NotiCountResponse>> call, @NonNull Response<ApiResponse<NotiCountResponse>> response) {
                        if (view == null)
                            return;

                        // GeneralFunction.dismissProgress();
                        if (response.isSuccessful()) {

                        Log.e(TAG, "onResponse: notification response  "+new Gson().toJson(response.body()));
                            view.notiResopnse(response.body().getData().unreadCount);
                        } else {
                            if (response.code() == UnAuthorized) {
                                view.sessionExpired();
                            } else {
                                try {
                                    JSONObject errorJson = new JSONObject(response.errorBody().string());
                                    String errorMessage = errorJson.optString("message");
                                    view.apiFailure(errorMessage);
                                } catch (JSONException | IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiResponse<NotiCountResponse>> call, @NonNull Throwable t) {
                        if (view != null) {
                            GeneralFunction.dismissProgress();
                            android.util.Log.d(TAG, "onFailure: notification"+t.getMessage());
                            view.apiFailure(t.getMessage());
                        }
                    }
                });
    }

    @Override
    public void attachView(ServiceContract.View view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        view = null;
    }
}
