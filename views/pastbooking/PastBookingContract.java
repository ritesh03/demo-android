package com.maktoday.views.pastbooking;

import com.maktoday.model.ApiResponse;
import com.maktoday.model.PojoMyBooking;

import java.util.HashMap;

/**
 * Created by cbl81 on 27/11/17.
 */

public class PastBookingContract {

    interface View {
        void setLoading(boolean isLoading);

        void sessionExpired();

        void pastBookingSuccess(ApiResponse<PojoMyBooking> data);

        void error(String failureMessage);

        void failure(String failureMessage);
    }

    interface Presenter {
        void apiPastBooking(HashMap<String, String> map);

        void attachView(PastBookingContract.View view);

        void detachView();

    }
}
