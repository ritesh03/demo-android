package com.maktoday.views.ratingdialog;

import com.google.gson.Gson;
import com.maktoday.model.ApiResponse;
import com.maktoday.model.PojoAddReview;
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
 * Created by cbl81 on 4/12/17.
 */

public class RatingPresenter implements RatingContract.Presenter {

    RatingContract.View view;

    @Override
    public void apiRating(PojoAddReview addReview) {
        if (view != null) {
            view.setLoading(true);

            Log.e("add review params", new Gson().toJson(addReview));
            RestClient.getModalApiService().apiAddReview(Prefs.get().getString(Constants.ACCESS_TOKEN, ""), addReview)
                    .enqueue(new Callback<ApiResponse>() {
                        @Override
                        public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                            view.setLoading(false);
                            Log.e("add review res", new Gson().toJson(response));
                            if (response.isSuccessful()) {
                                if (view != null) {
                                    view.ratingSuccess(response.body());
                                }
                            } else {
                                if (response.code() == UnAuthorized) {
                                    if (view != null) {
                                        view.sessionExpired();
                                    }
                                } else {
                                    try {
                                        if (view != null) {
                                            view.ratingError(new JSONObject(response.errorBody().string()).getString("message"));
                                        }
                                    } catch (JSONException | IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse> call, Throwable t) {

                            Log.e("add review failure", call.toString());
                            Log.e("add review failure", t.getMessage().toString());
                            if (view != null) {
                                view.setLoading(false);
                                view.ratingFailure(t.getMessage());
                            }
                        }

                    });
        }
    }

    @Override
    public void attachView(RatingContract.View view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        view = null;
    }
}
