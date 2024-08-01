package com.maktoday.views.notification;

import com.maktoday.model.PojoNotification;
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
 * Created by cbl81 on 30/11/17.
 */

public class NotificationPresenter implements NotificationContract.Presenter {
    NotificationContract.View view;

    @Override
    public void apiNotification() {
        if (view != null) {
            view.setLoading(true);
            RestClient.getModalApiService().apiGetNotification(Prefs.get().getString(Constants.ACCESS_TOKEN, ""), Constants.UNIQUE_APP_KEY)
                    .enqueue(new Callback<PojoNotification>() {
                        @Override
                        public void onResponse(Call<PojoNotification> call, Response<PojoNotification> response) {
                            GeneralFunction.dismissProgress();
                            if (response.isSuccessful()) {
                                if (view != null) {
                                    view.notificationSuccess(response.body());
                                }
                            } else {
                                if (response.code() == UnAuthorized) {
                                    if (view != null) {
                                        view.sessionExpired();
                                    }
                                } else {
                                    try {
                                        if (view != null) {
                                            view.notificationError(new JSONObject(response.errorBody().string()).getString("message"));
                                        }
                                    } catch (JSONException | IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<PojoNotification> call, Throwable t) {
                            if (view != null) {
                                GeneralFunction.dismissProgress();
                                view.notificationFailure(t.getMessage());
                            }
                        }

                    });
        }
    }

    @Override
    public void attachView(NotificationContract.View view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        view = null;
    }
}
