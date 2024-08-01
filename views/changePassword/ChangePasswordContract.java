package com.maktoday.views.changePassword;

import com.maktoday.model.ApiResponse;

import java.util.HashMap;

/**
 * Created by cbl81 on 2/12/17.
 */

public interface ChangePasswordContract {

    interface View {
        void setLoading(boolean isLoading);

        void sessionExpired();

        void changePasswordSuccess(ApiResponse data);

        void changePasswordError(String errorMessage);

        void changePasswordFailure(String failureMessage);
    }

    interface Presenter {

        void apiChangePassword(HashMap<String, String> map);

        void attachView(ChangePasswordContract.View view);

        void detachView();

    }
}
