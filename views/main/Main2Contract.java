package com.maktoday.views.main;

import com.maktoday.model.ApiResponse;

/**
 * Created by cbl81 on 6/12/17.
 */

public interface Main2Contract {
    interface View {
        void setLoading(boolean isLoading);

        void sessionExpired();

        void logoutSuccess(ApiResponse data);

        void logoutError(String failureMessage);

        void logoutFailure(String failureMessage);
    }

    interface Presenter {

        void apilogout();

        void attachView(Main2Contract.View view);

        void detachView();

    }
}
