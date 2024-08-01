package com.maktoday.views.pastbooking;

import com.maktoday.model.ApiResponse;
import com.maktoday.model.PojoMyBooking;
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

/**
 * Created by cbl81 on 27/11/17.
 */

public class PastBookingPresenter implements PastBookingContract.Presenter {

    private PastBookingContract.View view;

    @Override
    public void apiPastBooking(HashMap<String, String> map) {
        if (view != null) {

            view.setLoading(true);
            RestClient.getModalApiService().apiOnGoing(Prefs.get().getString(Constants.ACCESS_TOKEN, ""), map).enqueue(new Callback<ApiResponse<PojoMyBooking>>() {
                @Override
                public void onResponse(Call<ApiResponse<PojoMyBooking>> call, Response<ApiResponse<PojoMyBooking>> response) {
                    GeneralFunction.dismissProgress();
                    if (response.isSuccessful()) {
                        if (view != null) {
                            view.pastBookingSuccess(response.body());
                        }
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
                public void onFailure(Call<ApiResponse<PojoMyBooking>> call, Throwable t) {
                    if (view != null) {
                        GeneralFunction.dismissProgress();
                        view.failure(t.getMessage());
                    }
                }

            });
        }
    }

    @Override
    public void attachView(PastBookingContract.View view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        view = null;
    }
}
