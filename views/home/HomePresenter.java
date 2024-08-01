package com.maktoday.views.home;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.maktoday.model.ApiResponse;
import com.maktoday.model.BookServiceModel;
import com.maktoday.model.FullAddress;
import com.maktoday.model.GoogleTimeZoneResponse;
import com.maktoday.model.MaidData;
import com.maktoday.model.NotiCountResponse;
import com.maktoday.model.PojoMyBooking;
import com.maktoday.model.PojoServiceNew;
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

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.maktoday.utils.Constants.UnAuthorized;

/**
 * Created by cbl81 on 15/11/17.
 */

public class HomePresenter implements HomeContract.Presenter {
    private static final String TAG = "HomePresenter";

    private HomeContract.View view;

    @Override
    public void apiSearchMsi(HashMap<String, String> map) {

    }

    @Override
    public void apiServicelist() {

    }

    @Override
    public void apiBookService(PojoMyBooking.Datum bookingData, BookServiceModel bookServiceModel,
                               final String accessToken, final String reschduleStatus, final String SERVICE_ID) {
        if (view != null) {

            if (reschduleStatus.equalsIgnoreCase("yes")) {
                BookServiceModel bookServiceModel1 = new BookServiceModel();
                bookServiceModel1.bookingId = bookServiceModel.bookingId;
                bookServiceModel1.serviceData = bookServiceModel.serviceData;
                bookServiceModel1.locationName = bookingData.locationName;
                bookServiceModel1.hour = bookServiceModel.hour;
                bookServiceModel1.deviceTimeZone = bookServiceModel.deviceTimeZone;
                bookServiceModel1.lat = bookingData.bookingLocation.get(1);
                bookServiceModel1.maidId = bookServiceModel.maidId;
                bookServiceModel1.lng = bookingData.bookingLocation.get(0);
                bookServiceModel1.uniquieAppKey = bookServiceModel.uniquieAppKey;
                bookServiceModel1.timeZone = bookServiceModel.timeZone;
                FullAddress fullAddress = new FullAddress();
             /*   if(bookServiceModel.address!=null)
                {
                fullAddress.streetName=bookServiceModel.address.streetName;}
                fullAddress.city=bookServiceModel.address.city;
                fullAddress.buildingName=bookServiceModel.address.buildingName;
                fullAddress.country=bookServiceModel.address.country;
                fullAddress.villaName=bookServiceModel.address.villaName;*/
                //  Log.e(TAG,"book service params",new Gson().toJson(bookingData));
                if (bookingData.address != null) {
                    bookingData.address.lat = Double.valueOf(bookingData.bookingLocation.get(1));
                    bookingData.address.lng = Double.valueOf(bookingData.bookingLocation.get(0));
                    bookServiceModel1.address = bookingData.address;
                }
                bookServiceModel = new BookServiceModel();
                bookServiceModel = bookServiceModel1;

            }
            bookServiceModel.address.id = null;
            Log.e(TAG, " ------- book service params = " + new Gson().toJson(bookServiceModel));
            view.setLoading(true);
            BookServiceModel finalBookServiceModel = bookServiceModel;
            RestClient.getModalApiService().apiBookService(accessToken, bookServiceModel)
                    .enqueue(new Callback<PojoServiceNew>() {
                        @Override
                        public void onResponse(Call<PojoServiceNew> call, Response<PojoServiceNew> response) {
                            if (view == null)
                                return;
                            Log.e(TAG, " ---------- book service = " + new Gson().toJson(response));



                            GeneralFunction.dismissProgress();
                            if (response.isSuccessful()) {
                                if (reschduleStatus.equalsIgnoreCase("yes")) {
                                    finalBookServiceModel.serviceId = SERVICE_ID;
                                    Log.e(TAG, " --------- book service again params = " + new Gson().toJson(finalBookServiceModel));
                                    //    apiBookServiceAgain(finalBookServiceModel,accessToken,reschduleStatus);
                                    //view.reschduleError("yes");
                                } else {
                                    view.bookServiceSuccessNew(response.body());
                                }

                            } else {
                                if (response.code() == UnAuthorized) {
                                    view.sessionExpired();
                                } else {
                                    try {
                                        JSONObject errorJson = new JSONObject(response.errorBody().string());
                                        String errorMessage = errorJson.optString("message");

                                        final String responseType = errorJson.optString("responseType");
                                        Log.e(TAG, " --------- response type = " + responseType + "");
                                        if (responseType.equals("ALREADY_BOOK") ||
                                                responseType.equals("MAID_BUSY") ||
                                                responseType.equals("MAID_NOT_AVAILABLE")) {

                                            if (reschduleStatus.equalsIgnoreCase("yes")) {
                                                view.reschduleError("error");
                                            } else {
                                                getTimeSlots(accessToken, finalBookServiceModel, errorMessage);
                                            }

                                        } else {
                                            view.signupError(errorMessage);
                                        }
                                    } catch (JSONException | IOException e) {
                                        e.printStackTrace();
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
    public void apiCheckMaidAvailable(PojoMyBooking.Datum bookingData, BookServiceModel bookServiceModel,
                                      HashMap<String,String> map, final String accessToken, final String reschduleStatus,
                                      final String SERVICE_ID) {

        Log.e(TAG, " ----------- in apiCheckMaidAvailable() ");
        if (view != null) {
            view.setLoading(true);

            Log.e(TAG, " ----------- Access Token = " + Prefs.get().getString(Constants.ACCESS_TOKEN, ""));
            Log.e(TAG, " ----------- Check Maid Available params = = " + map);

            Log.e(TAG, " ------------- booking id = " + map.get("bookingId"));

            RestClient.getModalApiService().apiCheckMaidAvailable(
                    Prefs.get().getString(Constants.ACCESS_TOKEN, ""),
                    map.get("serviceId"),
                    map.get("bookingId"),
                    map.get("uniquieAppKey"),
                    map.get("workDate"),
                    map.get("startTime"),
                    map.get("hour"),
                    map.get("duration"),
                    map.get("timeZone"),
                    map.get("maidId")
            ).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            Log.e(TAG, " ----------- api Check Maid Available res = " + new Gson().toJson(response));

                            if (response.isSuccessful()) {
                                Log.e(TAG, " ----------- response is Successful  ");

                                if (response.body() != null) {
                                    Log.e(TAG, " ----------- response body != null  ");

                                    map.put("serviceId", SERVICE_ID);
                                    map.put("deviceTimeZone", TimeZone.getDefault().getID());

                                    bookServiceModel.bookingId = map.get("bookingId");
                                    bookServiceModel.serviceId = SERVICE_ID;

                                    Log.e(TAG, " ----------- calling api book service again  ");
                                    Log.e(TAG, " --------- book service again params " + new Gson().toJson(map));

                                    apiBookServiceAgain(bookServiceModel, accessToken, reschduleStatus);
                                }
                            } else {
                                Log.e(TAG, " ----------- else response not success ");

                                GeneralFunction.dismissProgress();
                                if (response.code() == UnAuthorized) {
                                    Log.e(TAG, " ----------- response code is unauthorized ");

                                    view.sessionExpired();
                                } else {
                                    Log.e(TAG, " ----------- response code is authorized ");
                                    try {
                                        JSONObject errorJson = new JSONObject(response.errorBody().string());
                                        if (errorJson.has("responseType") &&
                                                (errorJson.optString("responseType").equals("NOT_ABLE_TO_BOOK") ||
                                                        errorJson.optString("responseType").equals("TIME_ERROR"))) {
                                            view.reschduleError("error");
                                        } else {
                                            view.reschduleError("error");

                                        }
                                    } catch (JSONException | IOException e) {
                                        Log.e(TAG, " ----------- error : " + e);
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Log.e(TAG, " ----------- onFailure : " + t.getStackTrace());

                            if (view != null) {
                                GeneralFunction.dismissProgress();
                                // view.setRefreshing(false);
                                view.signupFailure(t.getMessage());
                            }

                        }//onFailure

                    });
        }
    }


    @Override
    public void apiSearchBulkMaidAgain(PojoMyBooking.Datum bookingData, HashMap<String, String> map, final String accessToken, final String reschduleStatus, final String SERVICE_ID) {
        if (view != null) {

            view.setLoading(true);

            Log.e(TAG, " -------- params " + Prefs.get().getString(Constants.ACCESS_TOKEN, ""));
            Log.e(TAG, " -------- params " + map + "");

            RestClient.getModalApiService().apiSearchBulkMaidAgain(Prefs.get().getString(Constants.ACCESS_TOKEN, ""), map)
                    .enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                            //view.setRefreshing(false);
                            if (response.isSuccessful()) {
                                if (response.body() != null) {

                                    HashMap<String, String> hashMap = new HashMap<>();

                                    hashMap.put("agencyId", String.valueOf(bookingData.agencyId._id));
                                    hashMap.put("hour", map.get("hour"));
                                    hashMap.put("uniquieAppKey", bookingData.uniquieAppKey);
                                    hashMap.put("startTime", map.get("startTime"));
                                    hashMap.put("serviceId", SERVICE_ID);
                                    hashMap.put("workDate", map.get("workDate"));
                                    Log.d("dddddddddddddddddddddd11", hashMap.toString());
                                    apiRescheduleBulk(hashMap, accessToken, reschduleStatus);
                                }
                            } else {
                                if (response.code() == UnAuthorized) {
                                    view.sessionExpired();
                                } else {
                                    try {
                                        JSONObject errorJson = new JSONObject(response.errorBody().string());
                                        if (errorJson.has("responseType") &&
                                                (errorJson.optString("responseType").equals("NOT_ABLE_TO_BOOK") ||
                                                        errorJson.optString("responseType").equals("TIME_ERROR"))) {
                                            view.reschduleError("error");
                                        } else {
                                            view.reschduleError("error");

                                        }
                                    } catch (JSONException | IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            if (view != null) {
                                // view.setRefreshing(false);
                                view.signupFailure(t.getMessage());
                            }
                        }

                    });
        }
    }

    @Override
    public void apiBookServiceAgain(final BookServiceModel bookServiceModel, final String accessToken, final String reschduleStatus) {
        Log.e(TAG, " ----------- in api BookServiceAgain() ");

        if (view != null) {

            Log.e(TAG, " -------- book again params = " + new Gson().toJson(bookServiceModel));

            RestClient.getModalApiService().apiBookServiceAgain(accessToken, bookServiceModel)
                    .enqueue(new Callback<PojoServiceNew>() {
                        @Override
                        public void onResponse(Call<PojoServiceNew> call, Response<PojoServiceNew> response) {
                            if (view == null) return;

                            Log.e(TAG, " --------- onResponse, book again = " + new Gson().toJson(response));

                            GeneralFunction.dismissProgress();
                            if (response.isSuccessful()) {
                                if (reschduleStatus.equalsIgnoreCase("yes")) {
                                    view.reschduleError("done");
                                } else {
                                    view.bookServiceSuccessNew(response.body());
                                }

                            } else {
                                if (response.code() == UnAuthorized) {
                                    view.sessionExpired();
                                } else {
                                    try {
                                        JSONObject errorJson = new JSONObject(response.errorBody().string());
                                        String errorMessage = errorJson.optString("message");
                                        /*
                                         * {"statusCode":400,"error":"Bad Request","message":"This maid is already booked.","responseType":"ALREADY_BOOK"}
                                         * */
                                        final String responseType = errorJson.optString("responseType");
                                        Log.e(TAG, " ---------- response type = " + responseType + "");
                                        if (responseType.equals("ALREADY_BOOK") ||
                                                responseType.equals("MAID_BUSY") ||
                                                responseType.equals("MAID_NOT_AVAILABLE")) {

                                            if (reschduleStatus.equalsIgnoreCase("yes")) {
                                                view.reschduleError("error");
                                            } else {
                                                // getTimeSlots(accessToken, map, errorMessage);
                                            }

                                        } else {
                                            view.signupFailure(errorMessage);
                                        }
                                    } catch (JSONException | IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<PojoServiceNew> call, Throwable t) {
                            Log.e(TAG, " --------- onFailure, t  = " + t.getMessage());

                            if (view != null) {
                                GeneralFunction.dismissProgress();
                                view.signupFailure(t.getMessage());
                            }
                        }

                    });
        }
    }


    @Override
    public void apiRescheduleBulk(HashMap<String, String> map, final String accessToken, final String reschduleStatus) {
        if (view != null) {

            view.setLoading(true);
            Log.e(TAG, " ---------- book againnnn = " + new Gson().toJson(map));
            RestClient.getModalApiService().apiRescheduleBulk(accessToken, map)
                    .enqueue(new Callback<PojoServiceNew>() {
                        @Override
                        public void onResponse(Call<PojoServiceNew> call, Response<PojoServiceNew> response) {
                            if (view == null)
                                return;
                            Log.e(TAG, " ----------- book again = " + new Gson().toJson(response));
                            GeneralFunction.dismissProgress();
                            if (response.isSuccessful()) {
                                if (reschduleStatus.equalsIgnoreCase("yes")) {
                                    view.reschduleError("done");
                                } else {
                                    view.bookServiceSuccessNew(response.body());
                                }

                            } else {
                                if (response.code() == UnAuthorized) {
                                    view.sessionExpired();
                                } else {
                                    try {
                                        JSONObject errorJson = new JSONObject(response.errorBody().string());
                                        String errorMessage = errorJson.optString("message");
                                        /*
                                         * {"statusCode":400,"error":"Bad Request","message":"This maid is already booked.","responseType":"ALREADY_BOOK"}
                                         * */
                                        final String responseType = errorJson.optString("responseType");
                                        Log.e(TAG, " ------ responsetype = " + responseType + "");
                                        if (responseType.equals("ALREADY_BOOK") ||
                                                responseType.equals("MAID_BUSY") ||
                                                responseType.equals("MAID_NOT_AVAILABLE")) {
                                            GeneralFunction.dismissProgress();

                                            if (reschduleStatus.equalsIgnoreCase("yes")) {
                                                view.reschduleError("error");
                                            } else {
                                                // getTimeSlots(accessToken, bookServiceModel, errorMessage);
                                            }

                                        } else {
                                            view.signupFailure(errorMessage);
                                        }
                                    } catch (JSONException | IOException e) {
                                        e.printStackTrace();
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
    public void getTimeZoneFromLatLong(double latitude, double longitude, String googleApiKey) {
        view.setLoading(true);

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

    public void getTimeSlots(String accessToken, BookServiceModel bookServiceModel, final String errorMessage) {
        view.setLoading(true);
        RestClient.getModalApiService().apiGetSlots(accessToken, Constants.UNIQUE_APP_KEY, bookServiceModel.maidId, bookServiceModel.timeZone)
                .enqueue(new Callback<ApiResponse<MaidData>>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiResponse<MaidData>> call, @NonNull Response<ApiResponse<MaidData>> response) {
                        if (view == null)
                            return;

                        GeneralFunction.dismissProgress();
                        if (response.isSuccessful()) {
                            view.displayTimeSlots(errorMessage, response.body().getData().timeSlot);
                        } else {
                            if (response.code() == UnAuthorized) {
                                view.sessionExpired();
                            } else {
                                try {
                                    JSONObject errorJson = new JSONObject(response.errorBody().string());
                                    String errorMessage = errorJson.optString("message");
                                    view.signupFailure(errorMessage);
                                } catch (JSONException | IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiResponse<MaidData>> call, @NonNull Throwable t) {
                        if (view != null) {
                            GeneralFunction.dismissProgress();
                            view.signupFailure(t.getMessage());
                        }
                    }
                });
    }

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
                            try {
                                view.notiResopnse(response.body().getData().unreadCount);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        } else {
                            if (response.code() == UnAuthorized) {
                                view.sessionExpired();
                            } else {
                                try {
                                    JSONObject errorJson = new JSONObject(response.errorBody().string());
                                    String errorMessage = errorJson.optString("message");
                                    view.signupFailure(errorMessage);
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
                            view.signupFailure(t.getMessage());
                        }
                    }
                });
    }

    @Override
    public void apilogout() {
        if (view != null) {
            view.setLoading(true);
            RestClient.getModalApiService().apiUserLogout(Prefs.get().getString(Constants.ACCESS_TOKEN, ""), Constants.UNIQUE_APP_KEY)
                    .enqueue(new Callback<ApiResponse>() {
                        @Override
                        public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                            GeneralFunction.dismissProgress();
                            if (response.isSuccessful()) {
                                if (view != null) {
                                    view.logoutSuccess();
                                }
                            } else {
                                if (response.code() == UnAuthorized) {
                                    if (view != null) {
                                        view.sessionExpired();
                                    }
                                } else {
                                    try {
                                        if (view != null) {
                                            view.signupFailure(new JSONObject(response.errorBody().string()).getString("message"));
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
                                view.signupFailure(t.getMessage());
                            }
                        }

                    });
        }
    }

    @Override
    public void attachView(HomeContract.View view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        view = null;
    }


}
