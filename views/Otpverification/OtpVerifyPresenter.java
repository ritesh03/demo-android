package com.maktoday.views.Otpverification;

import com.google.gson.Gson;
import com.maktoday.model.ApiResponse;
import com.maktoday.model.OtpVerifiyModel;
import com.maktoday.model.PojoLogin;
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

import android.util.Log;

public class OtpVerifyPresenter implements OtpVerifyContact.Presenter {
     private OtpVerifyContact.View view;
    private static final String TAG = "OtpVerifyPresenter";
    @Override
    public void apiOtpVerify(HashMap<String, String> map) {
        if (view != null) {
            view.setLoading(true);
            RestClient.getModalApiService().verifyOTP(Prefs.get().getString(Constants.ACCESS_TOKEN, ""), map)
                    .enqueue(new Callback<OtpVerifiyModel>() {
                        @Override
                        public void onResponse(Call<OtpVerifiyModel> call, Response<OtpVerifiyModel> response) {
                            GeneralFunction.dismissProgress();
                            if (response.isSuccessful()) {
                                if (view != null) {
                                    Log.e(TAG, "onResponse: verify Otp :"+ new Gson().toJson(response.body()));
                                    view.OtpveifySuccess(response.body());
                                }
                            } else {
                                if (response.code() == UnAuthorized) {
                                    if (view != null) {
                                        view.sessionExpired();
                                    }
                                } else {
                                    try {
                                        if (view != null) {
                                            view.OtpveifyError(new JSONObject(response.errorBody().string()).getString("message"));
                                        }
                                    } catch (JSONException | IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<OtpVerifiyModel> call, Throwable t) {
                            if (view != null) {
                                GeneralFunction.dismissProgress();
                                view.OtpveifyFailure(t.getMessage());
                            }
                        }

                    });
        }
    }

    @Override
    public void apiOtpResend(HashMap<String, String> map) {
        if (view != null) {
          //  view.setLoading(false);
            RestClient.getModalApiService().resendOTP(Prefs.get().getString(Constants.ACCESS_TOKEN, ""), map)
                    .enqueue(new Callback<OtpVerifiyModel>() {
                        @Override
                        public void onResponse(Call<OtpVerifiyModel> call, Response<OtpVerifiyModel> response) {
                            GeneralFunction.dismissProgress();
                            if (response.isSuccessful()) {
                                if (view != null) {
                                    view.ResendOtpSuccess(response.body());
                                }
                            } else {
                                if (response.code() == UnAuthorized) {
                                    if (view != null) {
                                        view.sessionExpired();
                                    }
                                } else {
                                    try {
                                        if (view != null) {
                                            view.OtpveifyError(new JSONObject(response.errorBody().string()).getString("message"));
                                        }
                                    } catch (JSONException | IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<OtpVerifiyModel> call, Throwable t) {
                            if (view != null) {
                                GeneralFunction.dismissProgress();
                                view.OtpveifyFailure(t.getMessage());
                            }
                        }

                    });
        }
    }

    @Override
    public void attachView(OtpVerifyContact.View view) {
         this.view=view;
    }

    @Override
    public void detachView() {
      view=null;
    }
}
