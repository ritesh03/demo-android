package com.maktoday.views.setting;


import static com.maktoday.utils.Constants.UnAuthorized;

import com.maktoday.model.DeleteAccountResponse;
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

public class SettingPresenter implements SettingContract.Presenter {
    private SettingContract.View view;

    @Override
    public void attachView(SettingContract.View view) {
        this.view = view;
    }

    @Override
    public void apiDeleteAccount(HashMap<String,String> map) {
        RestClient.getModalApiService().
                apiDeleteAccount(Prefs.get().getString(Constants.ACCESS_TOKEN, ""), map)
                .enqueue(new Callback<DeleteAccountResponse>() {
                    @Override
                    public void onResponse(Call<DeleteAccountResponse> call, Response<DeleteAccountResponse> response) {
                        if(response.isSuccessful()){
                            view.successDeleteAccount(response.body());
                        } else {
                            if (response.code() == UnAuthorized) {
                                if (view != null) {
                                    view.sessionExpired();
                                }
                            } else {
                                try {
                                    if (view != null) {
                                        view.error(new JSONObject(response.errorBody().string()).getString("message"));
                                    }
                                } catch (JSONException | IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<DeleteAccountResponse> call, Throwable t) {
                        if (view != null) {
                            GeneralFunction.dismissProgress();
                            view.failure(t.getMessage());
                            Log.e("deleteAccount:: ",t.getMessage());
                        }
                    }
                });
    }

    @Override
    public void detachView() {
       view = null;
    }
}
