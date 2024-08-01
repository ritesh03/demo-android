package com.maktoday.views.maidbook;

import com.maktoday.model.FacebookModel;
import com.maktoday.model.PojoLogin;

/**
 * Created by cbl81 on 13/12/17.
 */

public class MaidBookContract {

    interface View {
        void setLoading(boolean isLoading);

        void sessionExpired();

        void FBLoginSuccess(PojoLogin data);

        void FBLoginError(String errorMessage);

        void FBLoginFailure(String failureMessage);
    }

    interface Presenter {

        void apiFBLogin(FacebookModel facebookModel);

        void attachView(MaidBookContract.View view);

        void detachView();

    }
}
