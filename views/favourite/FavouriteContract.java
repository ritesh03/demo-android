package com.maktoday.views.favourite;

import com.maktoday.model.MaidData;

import java.util.HashMap;
import java.util.List;

/**
 * Created by cbl1005 on 8/2/18.
 */

public interface FavouriteContract {
    interface View {
        void setLoading(boolean isLoading);

        void sessionExpired();

        void favouriteSuccess(List<MaidData> data);

        void removeFavouriteSuccess();

        void favouriteError(String errorMessage);

        void favouriteFailure(String failureMessage);
    }

    interface Presenter {
        void apiFavouriteList(HashMap<String, String> map);

        void apiRemoveFavourite(String MaidId);

        void attachView(View view);

        void detachView();

    }
}
