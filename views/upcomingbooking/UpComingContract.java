package com.maktoday.views.upcomingbooking;

import com.maktoday.model.ApiResponse;
import com.maktoday.model.PojoMyBooking;

import java.util.HashMap;

/**
 * Created by cbl81 on 27/11/17.
 */

public class UpComingContract {

    interface View{
        void  setLoading(boolean isLoading);
        void sessionExpired();
        void upComingSuccess(ApiResponse<PojoMyBooking> data);
        void error(String failureMessage);
        void failure(String failureMessage);
    }

    interface Presenter {
        void apiUpComing(HashMap<String,String> map);
        void attachView(UpComingContract.View view);
        void detachView();

    }
}
