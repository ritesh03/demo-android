package com.maktoday.views.filter;

import com.maktoday.model.PojoFilterLanguage;

import java.util.HashMap;
import java.util.List;

/**
 * Created by cbl81 on 21/11/17.
 */

public class FilterContract {

    interface View {
        void setLoading(boolean isLoading);

        void sessionExpired();

        void nationalitySuccess(List<PojoFilterLanguage.Data> data);

        void agencySuccess(List<PojoFilterLanguage.Data> data);

        void languageSuccess(List<PojoFilterLanguage.Data> data);

        void religionSuccess(List<PojoFilterLanguage.Data> data);

        void error(String failureMessage);

        void failure(String failureMessage);
    }

    interface Presenter {

        void apiNationality(HashMap<String, String> map);

        void apiLanguage(HashMap<String, String> map);

        void apiAgency(HashMap<String, String> map);

        void apiReligion(HashMap<String, String> map);

        void attachView(FilterContract.View view);

        void detachView();

    }
}
