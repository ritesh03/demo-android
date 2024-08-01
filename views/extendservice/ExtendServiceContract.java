package com.maktoday.views.extendservice;

import com.maktoday.model.ApiResponse;

import java.util.HashMap;

/**
 * Created by cbl81 on 3/12/17.
 */

public interface ExtendServiceContract {
    interface View {
        void setLoading(boolean isLoading);

        void sessionExpired();

        void extendServiceSuccess(ApiResponse data);

        void extendServiceError(String errorMessage);

        void extendServiceFailure(String failureMessage);
    }

    interface Presenter {

        void apiExtendService(HashMap<String, String> map);

        void attachView(ExtendServiceContract.View view);

        void detachView();

    }
}
