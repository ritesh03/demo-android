package com.maktoday.views.favourite;

import com.maktoday.model.ApiResponse;
import com.maktoday.model.PojoFavourite;
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

import static com.maktoday.utils.Constants.UnAuthorized;

/**
 * Created by cbl1005 on 8/2/18.
 */

public class FavouritePresenter implements FavouriteContract.Presenter {

    private FavouriteContract.View view;

    @Override
    public void apiFavouriteList(HashMap<String, String> map) {
        if (view != null) {
            view.setLoading(true);
            RestClient.getModalApiService().apiListFavouriteMaid(Prefs.get().getString(Constants.ACCESS_TOKEN, ""), map)
                    .enqueue(new Callback<PojoFavourite>() {
                        @Override
                        public void onResponse(Call<PojoFavourite> call, Response<PojoFavourite> response) {
                            GeneralFunction.dismissProgress();
                            if (response.isSuccessful()) {
                                if (view != null) {
                                    view.favouriteSuccess(response.body().data);
                                }
                            } else {
                                if (response.code() == UnAuthorized) {
                                    if (view != null) {
                                        view.sessionExpired();
                                    }
                                } else {
                                    try {
                                        if (view != null) {
                                            view.favouriteError(new JSONObject(response.errorBody().string()).getString("message"));
                                        }
                                    } catch (JSONException | IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<PojoFavourite> call, Throwable t) {
                            if (view != null) {
                                GeneralFunction.dismissProgress();
                                view.favouriteFailure(t.getMessage());
                                Log.e("favorite",t.getMessage());
                            }
                        }

                    });
        }
    }

    @Override
    public void apiRemoveFavourite(String maidId) {
        if (view != null) {
            view.setLoading(true);
            RestClient.getModalApiService().removeFavouriteMaid(Prefs.get().getString(Constants.ACCESS_TOKEN, ""), Constants.UNIQUE_APP_KEY, maidId)
                    .enqueue(new Callback<ApiResponse>() {
                        @Override
                        public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                            GeneralFunction.dismissProgress();
                            if (response.isSuccessful()) {
                                if (view != null) {
                                    view.removeFavouriteSuccess();
                                }
                            } else {
                                if (response.code() == UnAuthorized) {
                                    if (view != null) {
                                        view.sessionExpired();
                                    }
                                } else {
                                    try {
                                        if (view != null) {
                                            view.favouriteError(new JSONObject(response.errorBody().string()).getString("message"));
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
                                GeneralFunction.dismissProgress();
                                view.favouriteFailure(t.getMessage());
                            }
                        }

                    });
        }
    }

    @Override
    public void attachView(FavouriteContract.View view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        view = null;
    }
}
