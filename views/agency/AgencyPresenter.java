package com.maktoday.views.agency;

import com.maktoday.model.PojoAgencyList;
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
 * Created by cbl81 on 15/11/17.
 */

public class AgencyPresenter implements AgencyContract.Presenter {

    private AgencyContract.View view;
    private Call<PojoAgencyList> call;

    @Override
    public void attachView(AgencyContract.View view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        view = null;
    }


    @Override
    public void apiAgencyList(HashMap<String, String> map, Boolean isSearch) {
        if (view != null) {

            view.setLoading(!isSearch);
            if (call != null) {
                call = null;
            }
            call = RestClient.getModalApiService().apiAllAgency(Prefs.get().getString(Constants.ACCESS_TOKEN, ""), map);
            call.enqueue(new Callback<PojoAgencyList>() {
                @Override
                public void onResponse(Call<PojoAgencyList> call, Response<PojoAgencyList> response) {
                    GeneralFunction.dismissProgress();
                    if (response.isSuccessful()) {
                        if (view != null) {
                            view.signupSuccess(response.body());
                        }
                    } else {
                        if (response.code() == UnAuthorized) {
                            if (view != null) {
                                view.sessionExpired();
                            }
                        } else {
                            try {
                                if (view != null) {
                                    view.signupError(new JSONObject(response.errorBody().string()).getString("message"));
                                }
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<PojoAgencyList> call, Throwable t) {
                    if (view != null) {
                        GeneralFunction.dismissProgress();
                        view.signupFailure(t.getMessage());
                    }
                }

            });
        }
    }

}
