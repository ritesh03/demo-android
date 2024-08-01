package com.maktoday.views.AllService;

import com.maktoday.model.ServicelistResponse;
import com.maktoday.views.home.HomeContract;

public class ServiceContract {

    interface View {
        void setLoading(boolean isLoading);
        void sessionExpired();
        void ServiceListSuccess(ServicelistResponse body);
        void apiFailure(String failureMessage);
        void notiResopnse(Integer errorMessage);
    }
    interface Presenter {
        void apiServicelist();
        void getNotiCount(String accessToken);
        void attachView(ServiceContract.View view);

        void detachView();
    }
}
