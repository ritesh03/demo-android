package com.maktoday.views.cancelbooking;

import com.google.gson.Gson;
import com.maktoday.model.ApiResponse;
import com.maktoday.model.CancelServiceModel;
import com.maktoday.utils.Constants;
import com.maktoday.utils.Log;
import com.maktoday.utils.Prefs;
import com.maktoday.webservices.RestClient;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.maktoday.utils.Constants.UnAuthorized;

/**
 * Created by cbl81 on 3/12/17.
 */

public class CancelBookingPresenter implements CancelBookingContract.Presenter {

    CancelBookingContract.View view;

    @Override
    public void apiCancelBooking(CancelServiceModel map) {
        if (view != null) {

            view.setLoading(true);
            Log.e("Cancel param", new Gson().toJson(map));
            RestClient.getModalApiService().apiCancelService(Prefs.get().getString(Constants.ACCESS_TOKEN, ""), map)
                    .enqueue(new Callback<ApiResponse>() {
                        @Override
                        public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                            view.setLoading(false);
                            if (response.isSuccessful()) {
                                if (view != null) {
                                    view.cancelBookingSuccess(response.body());
                                }
                            } else {
                                if (response.code() == UnAuthorized) {
                                    if (view != null) {
                                        view.sessionExpired();
                                    }
                                } else {
                                    try {
                                        if (view != null) {
                                            view.cancelBookingError(new JSONObject(response.errorBody().string()).getString("message"));
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
                                view.cancelBookingFailure(t.getMessage());
                            }
                        }

                    });
        }
    }

    @Override
    public void attachView(CancelBookingContract.View view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        view = null;
    }
}
