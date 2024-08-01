package com.maktoday.views.signupstep2;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.maktoday.model.ApiResponse;
import com.maktoday.model.BookServiceBulkModel;
import com.maktoday.model.BookServiceModel;
import com.maktoday.model.GoogleTimeZoneResponse;
import com.maktoday.model.PojoLogin;
import com.maktoday.model.PojoServiceNew;
import com.maktoday.model.SignUpModel;
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
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.maktoday.utils.Constants.UnAuthorized;

/**
 * Created by cbl81 on 13/11/17.
 */

public class SignUp2Presenter implements SignUp2Contract.Presenter {

    private static final String TAG = "SignUp2Presenter";

    private SignUp2Contract.View view;

    @Override
    public void attachView(SignUp2Contract.View view) {
        this.view = view;
    }

    @Override
    public void apiSignup(SignUpModel signupModel) {
        if (view != null) {
            view.setLoading(true);
            RestClient.getModalApiService().apiSignUpTwo(Prefs.get().getString(Constants.ACCESS_TOKEN, ""), signupModel)
                    .enqueue(new Callback<ApiResponse<PojoLogin>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<PojoLogin>> call, Response<ApiResponse<PojoLogin>> response) {
                            GeneralFunction.dismissProgress();
                            if (response.isSuccessful()) {
                                if (view != null) {
                                    view.setLoading(false);
                                    view.signupSuccess(response.body());
                                }
                            } else {
                                if (response.code() == UnAuthorized) {
                                    if (view != null) {
                                        view.setLoading(false);
                                        view.sessionExpired();
                                    }
                                } else {
                                    try {
                                        if (view != null) {
                                            view.setLoading(false);
                                            view.signupError(new JSONObject(response.errorBody().string()).getString("message"));
                                        }
                                    } catch (JSONException | IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<PojoLogin>> call, Throwable t) {
                            if (view != null) {
                                view.setLoading(false);
                                GeneralFunction.dismissProgress();
                                view.signupFailure(t.getMessage());
                            }
                        }

                    });
        }
    }

    @Override
    public void apiAddBillingInfo(SignUpModel signUpModel) {
        if (view != null) {

            view.setLoading(true);
            RestClient.getModalApiService().apiAddBillingInfo(Prefs.get().getString(Constants.ACCESS_TOKEN, ""), signUpModel).enqueue(new Callback<ApiResponse<PojoLogin>>() {
                @Override
                public void onResponse(Call<ApiResponse<PojoLogin>> call, Response<ApiResponse<PojoLogin>> response) {
                    GeneralFunction.dismissProgress();
                    if (response.isSuccessful()) {
                        view.setLoading(false);
                        view.billingInfoSuccess(response.body());
                    } else {
                        if (response.code() == UnAuthorized) {
                            view.setLoading(false);
                            view.sessionExpired();
                        } else {
                            try {
                                view.setLoading(false);
                                view.signupError(new JSONObject(response.errorBody().string()).getString("message"));
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<PojoLogin>> call, Throwable t) {
                    if (view != null) {
                        view.setLoading(false);
                        GeneralFunction.dismissProgress();
                        view.signupFailure(t.getMessage());
                    }
                }

            });
        }
    }

    @Override
    public void getTimeZoneFromLatLong(double latitude, double longitude, String googleApiKey) {
        if (view != null) {
            view.setLoading(true);
        }
        final HashMap<String, String> params = new HashMap<>();
        params.put("location", latitude + "," + longitude);
        params.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
        params.put("key", googleApiKey);

        RestClient.getModalApiService()
                .getTimeZoneFromLatLng(params)
                .enqueue(new Callback<GoogleTimeZoneResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<GoogleTimeZoneResponse> call, @NonNull Response<GoogleTimeZoneResponse> response) {
                        if (view == null)
                            return;
                        GeneralFunction.dismissProgress();
                        final GoogleTimeZoneResponse timeZoneResponse = response.body();
                        Log.d("TimeZone", timeZoneResponse == null ? "" : timeZoneResponse.toString());
                        if (response.isSuccessful() && timeZoneResponse != null && timeZoneResponse.timeZoneId != null) {
                            view.bookingTimeZoneReceived(TimeZone.getTimeZone(timeZoneResponse.timeZoneId));
                        } else {
                            view.bookingTimeZoneReceived(null);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<GoogleTimeZoneResponse> call, @NonNull Throwable t) {
                        if (view != null) {
                            GeneralFunction.dismissProgress();
                            view.signupFailure(t.getMessage());
                        }
                    }
                });
    }

    @Override
    public void apiUpdateProfile(SignUpModel signUpModel) {
        if (view != null) {
            view.setLoading(true);

            Log.e("update profile params", new Gson().toJson(signUpModel));
            RestClient.getModalApiService().apiUpdateProfile(Prefs.get().getString(Constants.ACCESS_TOKEN, ""), signUpModel)
                    .enqueue(new Callback<ApiResponse<PojoLogin>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<PojoLogin>> call, Response<ApiResponse<PojoLogin>> response) {
                            GeneralFunction.dismissProgress();

                            Log.e("reree", "" + new Gson().toJson(response));
                            if (response.isSuccessful()) {
                                if (view != null) {
                                    view.updateProfileSuccess(response.body().getData());
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
                        public void onFailure(Call<ApiResponse<PojoLogin>> call, Throwable t) {
                            if (view != null) {
                                GeneralFunction.dismissProgress();
                                view.signupFailure(t.getMessage());
                            }
                        }

                    });
        }
    }

    @Override
    public void apiBookService(BookServiceModel bookServiceModel) {
        Log.e(TAG, " ------- book service params = " + new Gson().toJson(bookServiceModel));
        if (view != null) {
            view.setLoading(true);
            RestClient.getModalApiService().apiBookService(Prefs.get().getString(Constants.ACCESS_TOKEN, ""), bookServiceModel)
                    .enqueue(new Callback<PojoServiceNew>() {
                        @Override
                        public void onResponse(Call<PojoServiceNew> call, Response<PojoServiceNew> response) {
                            GeneralFunction.dismissProgress();
                            if (view != null) {
                                if (response.isSuccessful()) {
                                    view.bookServiceSuccessNew(response.body());
                                } else {
                                    if (response.code() == UnAuthorized) {
                                        view.sessionExpired();
                                    } else {
                                        try {
                                            view.signupError(new JSONObject(response.errorBody().string()).getString("message"));
                                        } catch (JSONException | IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<PojoServiceNew> call, Throwable t) {
                            if (view != null) {
                                GeneralFunction.dismissProgress();
                                view.signupFailure(t.getMessage());
                            }
                        }

                    });
        }
    }


    @Override
    public void apiBulkBookService(BookServiceBulkModel bookServiceModel) {
        if (view != null) {

            view.setLoading(true);

            Log.e("bookkk", "" + new Gson().toJson(bookServiceModel));
            RestClient.getModalApiService().apiBulkBookService(Prefs.get().getString(Constants.ACCESS_TOKEN, ""), bookServiceModel)
                    .enqueue(new Callback<PojoServiceNew>() {
                        @Override
                        public void onResponse(Call<PojoServiceNew> call, Response<PojoServiceNew> response) {
                            GeneralFunction.dismissProgress();
                            if (view != null) {
                                if (response.isSuccessful()) {
                                    view.bookServiceSuccessBulk(response.body());
                                } else {
                                    if (response.code() == UnAuthorized) {
                                        view.sessionExpired();
                                    } else {
                                        try {
                                            view.signupError(new JSONObject(response.errorBody().string()).getString("message"));
                                        } catch (JSONException | IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<PojoServiceNew> call, Throwable t) {
                            if (view != null) {
                                GeneralFunction.dismissProgress();
                                view.signupFailure(t.getMessage());
                            }
                        }

                    });
        }
    }

    @Override
    public void detachView() {
        view = null;
    }

}
