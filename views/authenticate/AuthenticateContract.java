package com.maktoday.views.authenticate;

import com.maktoday.model.ApiResponse;
import com.maktoday.model.FacebookModel;
import com.maktoday.model.PojoLogin;

/**
 * Created by cbl81 on 15/11/17.
 */

public class AuthenticateContract {

    interface View {
        void setLoading(boolean isLoading);

        void sessionExpired();

        void signupSuccess(ApiResponse<PojoLogin> data);

        void signupError(String failureMessage);

        void signupFailure(String failureMessage);
    }

    interface Presenter {

        void apiFacebook(FacebookModel facebookModel);

        void attachView(AuthenticateContract.View view);

        void detachView();

    }
}
