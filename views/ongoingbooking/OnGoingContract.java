package com.maktoday.views.ongoingbooking;

import com.maktoday.model.ApiResponse;
import com.maktoday.model.PojoMyBooking;

import java.util.HashMap;

/**
 * Created by cbl81 on 21/11/17.
 */

public class OnGoingContract {

    interface View {
        void setLoading(boolean isLoading);

        void sessionExpired();

        void onGoingSuccess(ApiResponse<PojoMyBooking> data);

        void error(String failureMessage);

        void failure(String failureMessage);
    }

    interface Presenter {
        void apiOnGoing(HashMap<String, String> map);

        void attachView(OnGoingContract.View view);

        void detachView();
    }
}
