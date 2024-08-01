package com.maktoday.views.ratingdialog;

import com.maktoday.model.ApiResponse;
import com.maktoday.model.PojoAddReview;

/**
 * Created by cbl81 on 4/12/17.
 */

public class RatingContract {

    interface View {
        void setLoading(boolean isLoading);

        void sessionExpired();

        void ratingSuccess(ApiResponse data);

        void ratingError(String failureMessage);

        void ratingFailure(String failureMessage);
    }

    interface Presenter {

        void apiRating(PojoAddReview hashMap);

        void attachView(RatingContract.View view);

        void detachView();

    }
}
