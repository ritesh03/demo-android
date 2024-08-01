package com.maktoday.views.home;

import androidx.annotation.Nullable;

import com.maktoday.model.ApiResponse;
import com.maktoday.model.BookServiceModel;
import com.maktoday.model.PojoLogin;
import com.maktoday.model.PojoMyBooking;
import com.maktoday.model.PojoService;
import com.maktoday.model.PojoServiceNew;
import com.maktoday.model.ServicelistResponse;
import com.maktoday.model.TimeSlot;

import java.util.HashMap;
import java.util.TimeZone;


public class HomeContract {
    interface View {
        void setLoading(boolean isLoading);

        void sessionExpired();

        void signupSuccess(ApiResponse<PojoLogin> data);

        void signupError(String failureMessage);

        void signupFailure(String failureMessage);

        void bookServiceSuccess(PojoService body);

        void ServiceListSuccess(ServicelistResponse body);

        void bookServiceSuccessNew(PojoServiceNew body);

        void displayTimeSlots(String errorMessage, TimeSlot timeSlot);

        void reschduleError(String errorMessage);

        void notiResopnse(Integer errorMessage);

        void logoutSuccess();

        void bookingTimeZoneReceived(@Nullable TimeZone timeZone);
    }

    interface Presenter {

        void apiSearchMsi(HashMap<String, String> map);

        void apiServicelist();

        void apiSearchBulkMaidAgain(PojoMyBooking.Datum bookingData, HashMap<String, String> map, String accessToken, String reschduleStatus, String SERVICE_ID);

        void apiCheckMaidAvailable(PojoMyBooking.Datum bookingData, BookServiceModel bookServiceModel, HashMap<String, String> map, String accessToken, String reschduleStatus, String SERVICE_ID);

        void apiRescheduleBulk(HashMap<String, String> map, String accessToken, String reschduleStatus);

        void apiBookService(PojoMyBooking.Datum bookingData, BookServiceModel bookServiceModel, String accessToken, String reschduleStatus, String SERVICE_ID);

        void apiBookServiceAgain(BookServiceModel bookServiceModel, String accessToken, String reschduleStatus);

        void getNotiCount(String accessToken);

        void getTimeZoneFromLatLong(final double latitude, final double longitude, final String googleApiKey);

        void attachView(HomeContract.View view);

        void detachView();

        void apilogout();
    }
}
