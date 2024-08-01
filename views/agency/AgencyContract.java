package com.maktoday.views.agency;

import com.maktoday.model.PojoAgencyList;

import java.util.HashMap;

/**
 * Created by cbl81 on 15/11/17.
 */

public class AgencyContract {

    interface View {
        void setLoading(boolean isLoading);

        void sessionExpired();

        void signupSuccess(PojoAgencyList data);

        void signupError(String failureMessage);

        void signupFailure(String failureMessage);
    }

    interface Presenter {

        void apiAgencyList(HashMap<String, String> map, Boolean isSearch);

        void attachView(AgencyContract.View view);

        void detachView();

    }
}
