package com.maktoday.views.forgotpassword;

import com.maktoday.model.ApiResponse;

/**
 * Created by cbl81 on 2/12/17.
 */

public interface ForgotPasswordContract {
    interface View {
        void setLoading(boolean isLoading);

        void sessionExpired();

        void forgotPasswordSuccess(ApiResponse data);

        void forgotPasswordError(String errorMessage);

        void forgotPasswordFailure(String failureMessage);
    }

    interface Presenter {

        void apiForgotPassword(String email);

        void attachView(ForgotPasswordContract.View view);

        void detachView();

    }
}
