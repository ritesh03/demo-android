package com.maktoday.views.cancelbooking;

import com.maktoday.model.ApiResponse;
import com.maktoday.model.CancelServiceModel;

/**
 * Created by cbl81 on 3/12/17.
 */

public interface CancelBookingContract {
    interface View {
        void setLoading(boolean isLoading);

        void sessionExpired();

        void cancelBookingSuccess(ApiResponse data);

        void cancelBookingError(String errorMessage);

        void cancelBookingFailure(String failureMessage);
    }

    interface Presenter {

        void apiCancelBooking(CancelServiceModel map);

        void attachView(View view);

        void detachView();

    }
}
