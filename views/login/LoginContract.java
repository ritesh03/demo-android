package com.maktoday.views.login;

import com.maktoday.model.ApiResponse;
import com.maktoday.model.FacebookModel;
import com.maktoday.model.PojoLogin;

import java.util.HashMap;

/**
 * Created by cbl81 on 13/11/17.
 */

public interface LoginContract {
    interface View {
        void setLoading(boolean isLoading);

        void sessionExpired();

        void signupSuccess(ApiResponse<PojoLogin> data);

        void signupSuccesss(ApiResponse<PojoLogin> data);

        void signupError(String failureMessage);

        void signupFailure(String failureMessage);
    }

    interface Presenter {
        void apiFacebook(FacebookModel facebookModel);

        void apiLogin(HashMap<String, String> map);

        void attachView(LoginContract.View view);

        void detachView();

    }

}
