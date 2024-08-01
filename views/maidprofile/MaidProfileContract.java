package com.maktoday.views.maidprofile;

import com.maktoday.model.BookServiceModel;
import com.maktoday.model.PojoMaidProfile;

import java.util.HashMap;

/**
 * Created by cbl81 on 17/11/17.
 */

public class MaidProfileContract {

    interface View {
        void setLoading(boolean isLoading);

        void sessionExpired();

        void getMaidSuccess(PojoMaidProfile profileData);

        void successAddMaid();

        void successRemoveMaid();

        void getMaidError(String failureMessage);

        void getMaidFailure(String failureMessage);

        void reschduleError(String failureMessage);
    }

    interface Presenter {
        void apiSearchMaid(String maidID);

        void apiAddFavouriteMaid(String maidID);

        void apiRemoveFavouriteMaid(String maidID);

        void attachView(MaidProfileContract.View view);

        void detachView();

        void apiRescheduleBulk(HashMap<String, String> map, String accessToken, String reschduleStatus);

        void apiBookServiceAgain(BookServiceModel bookServiceModel, String accessToken, String reschduleStatus);
    }
}
