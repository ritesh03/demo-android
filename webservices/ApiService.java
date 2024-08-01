package com.maktoday.webservices;

import com.maktoday.model.ApiResponse;
import com.maktoday.model.BookServiceBulkModel;
import com.maktoday.model.BookServiceModel;
import com.maktoday.model.CancelServiceModel;
import com.maktoday.model.ChargeDetailsResponse;
import com.maktoday.model.CheckVersionResponse;
import com.maktoday.model.ContactusResponse;
import com.maktoday.model.CreatepaymentRespose;
import com.maktoday.model.DeleteAccountResponse;
import com.maktoday.model.FacebookModel;
import com.maktoday.model.FullAddress;
import com.maktoday.model.GoogleTimeZoneResponse;
import com.maktoday.model.LatestMessage;
import com.maktoday.model.MaidData;
import com.maktoday.model.NotiCountResponse;
import com.maktoday.model.OtpVerifiyModel;
import com.maktoday.model.PaytabTransactionVerificationResponse;
import com.maktoday.model.PojoAddCard;
import com.maktoday.model.PojoAddReview;
import com.maktoday.model.PojoAgencyList;
import com.maktoday.model.PojoCardList;
import com.maktoday.model.PojoChatData;
import com.maktoday.model.PojoChatList;
import com.maktoday.model.PojoCreatePayment;
import com.maktoday.model.PojoFavourite;
import com.maktoday.model.PojoFilter;
import com.maktoday.model.PojoFilterAgency;
import com.maktoday.model.PojoFilterLanguage;
import com.maktoday.model.PojoLogin;
import com.maktoday.model.PojoMaidProfile;
import com.maktoday.model.PojoMyBooking;
import com.maktoday.model.PojoNotification;
import com.maktoday.model.PojoSearchMaid;
import com.maktoday.model.PojoService;
import com.maktoday.model.PojoServiceNew;
import com.maktoday.model.PromoResponse;
import com.maktoday.model.ServicelistResponse;
import com.maktoday.model.SignUpModel;
import com.maktoday.model.StripeChargeApi;

import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by cbl81 on 8/11/17.
 */

public interface ApiService {
    @FormUrlEncoded
    @POST("/user/signUp1")
    Call<ApiResponse<PojoLogin>> apiSignUpOne(@FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("/user/signUp")
    Call<ApiResponse<PojoLogin>> apiSignUp(@FieldMap HashMap<String, Object> map);
    @POST("/user/signUp2")
    Call<ApiResponse<PojoLogin>> apiSignUpTwo(@Header("authorization") String authorization,
                                              @Body SignUpModel signupModel);
    @FormUrlEncoded
    @POST("user/verifyOTP")
    Call<OtpVerifiyModel> verifyOTP(@Header("authorization") String authorization,
                                    @FieldMap HashMap<String, String> map);
    @FormUrlEncoded
    @POST("user/resendOTP")
    Call<OtpVerifiyModel> resendOTP(@Header("authorization") String authorization,
                                    @FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @PUT("/user/emailLogin")
    Call<ApiResponse<PojoLogin>> apiEmailLogin(@FieldMap HashMap<String, String> map);

    @PUT("/user/facebookLogin")
    Call<ApiResponse<PojoLogin>> apiFacebookLogin(@Body FacebookModel signupModel);

    @GET("/user/listAllAgencyUser")
    Call<PojoAgencyList> apiAllAgency(@Header("authorization") String authorization,
                                      @QueryMap HashMap<String, String> map);
    @GET("/user/listAllMaidsUser")
    Call<PojoSearchMaid> apiAllListMaid(@Header("authorization") String authorization,
                                        @QueryMap HashMap<String, String> map);

    @GET("/admin/getAllNormalService")
    Call<ServicelistResponse> getAllNormalService(@Query("uniquieAppKey") String uniquieAppKey );

    @GET("/user/listBulkMaidsUser")
    Call<PojoSearchMaid> apiAllListMaidBulk(@Header("authorization") String authorization,
                                            @QueryMap HashMap<String, String> map);

    @GET("user/searchBulkMaidAgain")
    Call<ResponseBody> apiSearchBulkMaidAgain(@Header("authorization") String authorization,
                                              @QueryMap HashMap<String, String> map);
    @GET("/user/getMaidProfileDetail")
    Call<ApiResponse<PojoMaidProfile>> apiGetMaidProfile(@Header("authorization") String authorization,
                                                         @Query("uniquieAppKey") String uniquieAppKey, @Query("maidId") String maidId);

    @POST("/user/addBillingInfo")
    Call<ApiResponse<PojoLogin>> apiAddBillingInfo(@Header("authorization") String authorization,
                                                   @Body SignUpModel signupModel);
    @POST("/user/bookService")
    Call<PojoServiceNew> apiBookService(@Header("authorization") String authorization,
                                        @Body BookServiceModel signupModel);

    @POST("/user/bookServiceAgain")
    Call<PojoServiceNew> apiBookServiceAgain(@Header("authorization") String authorization,
                                             @Body BookServiceModel signupModel);
    
    @FormUrlEncoded
    @POST("/user/updateBulkBookingAgain")
    Call<PojoServiceNew> apiRescheduleBulk(@Header("authorization") String authorization,
                                           @FieldMap HashMap<String, String> map);
    @GET("/user/checkMaidAvailable")
    Call<ResponseBody> apiCheckMaidAvailable(@Header("authorization") String authorization,
                                             @Query("serviceId") String serviceId ,
                                             @Query("bookingId") String bookingId ,
                                             @Query("uniquieAppKey") String uniquieAppKey,
                                             @Query("workDate") String workDate,
                                             @Query("startTime") String startTime,
                                             @Query("hour") String hour,
                                             @Query("duration") String duration,
                                             @Query("timeZone") String timeZone,
                                             @Query("maidId") String maidId);

    @POST("/user/bookServiceBulk")
    Call<PojoServiceNew> apiBulkBookService(@Header("authorization") String authorization,
                                            @Body BookServiceBulkModel signupModel);

    @FormUrlEncoded
    @POST("/user/chargeStripePayment")
    Call<StripeChargeApi> apiChargeStripePayment(@Header("authorization") String authorization,
                                                 @FieldMap HashMap<String, String> map);

    @GET("/user/getSlots")
    Call<ApiResponse<MaidData>> apiGetSlots(@Header("authorization") String authorization,
                                            @Query("uniquieAppKey") String uniquieAppKey,
                                            @Query("maidId") String maidId,
                                            @Query("timeZone") String timeZone);

    @GET("/user/getNotificationUnredCount")
    Call<ApiResponse<NotiCountResponse>> apiGetNotiCount(@Header("authorization") String authorization,
                                                         @Query("uniquieAppKey") String uniquieAppKey);

    @GET("/user/listAllNationalityUser")
    Call<PojoFilter> apiListNationality(@Header("authorization") String authorization,
                                        @QueryMap HashMap<String, String> map);

    @GET("/user/listAllAgency")
    Call<PojoFilterAgency> apiListAgency(@QueryMap HashMap<String, String> map);

    @GET("/user/listAllLanguagesUser")
    Call<PojoFilterLanguage> apiListLanguage(@Header("authorization") String authorization,
                                             @QueryMap HashMap<String, String> map);

    @GET("/user/listAllReligion")
    Call<PojoFilter> apiListReligion(@Header("authorization") String authorization,
                                     @QueryMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("/user/createPayment")
    Call<PojoCreatePayment> apiCreatePayment(@Header("authorization") String authorization,
                                             @FieldMap HashMap<String, String> map);


    @GET("/user/getCards")
    Call<PojoCardList> apiGetCards(@Header("authorization") String authorization,
                                   @Query("uniquieAppKey") String uniqueKey);


    @FormUrlEncoded
    @POST("/user/addCard")
    Call<ApiResponse<PojoAddCard>> apiAddCard(@Header("authorization") String authorization,
                                              @FieldMap HashMap<String, String> map);


    @FormUrlEncoded
    @PUT("/user/changeLanguageUser")
    Call<ApiResponse> apiChangeLanguage(@Header("authorization") String authorization,
                                        @Field("uniquieAppKey") String uniquieAppKey,
                                        @Field("language") String issue);


    @FormUrlEncoded
    @POST("/user/deleteCard")
    Call<ApiResponse> apiDeleteCard(@Header("authorization") String authorization,
                                    @FieldMap HashMap<String, String> map);

    @GET("/user/listAllServiceUser")
    Call<ApiResponse<PojoMyBooking>> apiOnGoing(@Header("authorization") String authorization,
                                                @QueryMap HashMap<String, String> map);

    @GET("/user/getNotifications")
    Call<PojoNotification> apiGetNotification(@Header("authorization") String authorization,
                                              @Query("uniquieAppKey") String uniquieAppKey);

    @FormUrlEncoded
    @POST("/user/raiseAnIssue")
    Call<ApiResponse> apiRaiseIssue(@Header("authorization") String authorization,
                                    @Field("uniquieAppKey") String uniquieAppKey,
                                    @Field("issue") String issue);

    @FormUrlEncoded
    @POST("/user/forgetPassword")
    Call<ApiResponse> apiForgetPassword(@Field("uniquieAppKey") String uniquieAppKey,
                                        @Field("email") String email);

    @FormUrlEncoded
    @PUT("/user/changePassword")
    Call<ApiResponse> apiChangePassword(@Header("authorization") String authorization,
                                        @FieldMap HashMap<String, String> map);

    @POST("/user/cancelService")
    Call<ApiResponse> apiCancelService(@Header("authorization") String authorization,
                                       @Body CancelServiceModel cancelModel);


    @FormUrlEncoded
    @POST("/user/requestForExtendService")
    Call<ApiResponse> apiExtendService(@Header("authorization") String authorization,
                                       @FieldMap HashMap<String, String> map);


    @POST("/user/addReview")
    Call<ApiResponse> apiAddReview(@Header("authorization") String authorization,
                                   @Body PojoAddReview pojoAddReview);


    @PUT("/user/updateUserProfile")
    Call<ApiResponse<PojoLogin>> apiUpdateProfile(@Header("authorization") String authorization,
                                                  @Body SignUpModel signupModel);

    @FormUrlEncoded
    @PUT("/user/userLogout")
    Call<ApiResponse> apiUserLogout(@Header("authorization") String authorization,
                                    @Field("uniquieAppKey") String uniqueAppKey);


    @POST("/user/saveAddress")
    Call<ApiResponse<PojoLogin>> apiSaveAddress(@Header("authorization") String authorization,
                                                @Body FullAddress fullAddress);
    @FormUrlEncoded
    @POST("/user/deleteAddress")
    Call<ApiResponse<PojoLogin>> apiDelete(@Header("authorization") String authorization,
                                           @FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("/user/createChat")
    Call<ApiResponse<LatestMessage>> apiCreateChat(@Header("authorization") String authorization, @FieldMap HashMap<String, String> map);

    @GET("/user/getAllChat")
    Call<ApiResponse<List<PojoChatList>>> apiGetAllChat(@Header("authorization") String authorization, @Query("uniquieAppKey") String uniqueAppKey, @Query("userType") String userType);

    @GET("/user/getChatHistory")
    Call<ApiResponse<PojoChatData>> apiGetChatHistory(@Header("authorization") String authorization,
                                                      @QueryMap HashMap<String, String> map);
    @FormUrlEncoded
    @POST("/user/createPaymentPaytabs")
    Call<PojoCreatePayment> apiCreatePaymentPayTabs(@Header("authorization") String authorization,
                                                    @FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("/user/createPaymentPaytabsBulk")
    Call<PojoCreatePayment> apiCreatePaymentPayTabsBulk(@Header("authorization") String authorization,
                                                        @FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("/user/addFavouriteMaid")
    Call<ApiResponse> addFavouriteMaid(@Header("authorization") String authorization,
                                       @Field("uniquieAppKey") String uniquieAppKey,
                                       @Field("maidId") String maidId);

    @FormUrlEncoded
    @POST("/user/removeFavouriteMaid")
    Call<ApiResponse> removeFavouriteMaid(@Header("authorization") String authorization,
                                          @Field("uniquieAppKey") String uniquieAppKey,
                                          @Field("maidId") String maidId);

    @FormUrlEncoded
    @POST("/user/contactUs")
    Call<ContactusResponse> contactUs(@Header("authorization") String authorization,
                                      @Field("uniquieAppKey") String uniquieAppKey,
                                      @Field("name") String name,
                                      @Field("type") String type,
                                      @Field("phoneNumber") String phoneNumber,
                                      @Field("comment") String comment,
                                      @Field("email") String email,
                                      @Field("countryCode") String countryCode,
                                      @Field("userId") String userId);

    @GET("/user/listFavouriteMaid")
    Call<PojoFavourite> apiListFavouriteMaid(@Header("authorization") String authorization,
                                             @QueryMap HashMap<String, String> map);

    @GET("https://maps.googleapis.com/maps/api/timezone/json")
    Call<GoogleTimeZoneResponse> getTimeZoneFromLatLng(@QueryMap HashMap<String, String> map);
    @FormUrlEncoded
    @POST("apiv2/verify_payment_transaction")
    Call<PaytabTransactionVerificationResponse> apiPayTabTransactionVerification(@Header("authorization") String authorization,
                                                                                 @FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("/user/applyPromoCode")
    Call<PromoResponse> apiApplyPromo(@Header("authorization") String authorization,
                                      @FieldMap HashMap<String, String> map);

    @POST("/user/tapCreateChargeApp")
    Call<CreatepaymentRespose> tapCreateCharge(@Header("authorization") String authorization,
                                               @Body HashMap<String, String> map);
    @POST("/user/tapChargeDetailApp")
    Call<ChargeDetailsResponse> tapChargeDetailApp(@Header("authorization") String authorization,
                                                  @Body HashMap<String, String> map);

    @PUT("/user/deleteUser")
    Call<DeleteAccountResponse> apiDeleteAccount(
            @Header("authorization") String authorization,
            @Body HashMap<String,String> params
    );
    @FormUrlEncoded
    @POST("/api/checkVersionT")
    Call<ApiResponse<CheckVersionResponse>> checkVersion(@Field("appType") String appType,
                                                         @Field("deviceType") String deviceType,
                                                         @Field("appVersion") Integer appVersion);
}
