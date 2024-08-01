package com.maktoday.views.maidsearch;

import androidx.annotation.Nullable;

import com.maktoday.model.PojoSearchMaid;
import com.maktoday.model.TimeSlot;

import java.util.HashMap;
import java.util.TimeZone;

/**
 * Created by cbl81 on 16/11/17.
 */

public class MaidContract {

    public interface View {
        void setRefreshing(boolean isRefreshing);

        void setLoading(boolean isLoading);

        void sessionExpired();

        void searchMaidSuccess(PojoSearchMaid searchMaidData);

        void bookingTimeZoneReceived(@Nullable TimeZone timeZone);

        void maidNotAvailable(String failureMessage);

        void signupFailure(String failureMessage);

        void displayTimeSlots(TimeSlot timeSlot);
    }

    public interface Presenter {

        void apiSearchMaid(HashMap<String, String> map, boolean isSearch);

        void apiSearchBulkMaid(HashMap<String, String> map);

        void getTimeSlots(String maidId, String bookingTimeZone);

        void getTimeZoneFromLatLong(final double latitude, final double longitude, final String googleApiKey);

        void attachView(MaidContract.View view);

        void detachView();

    }

}
