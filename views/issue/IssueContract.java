package com.maktoday.views.issue;

import com.maktoday.model.ApiResponse;

/**
 * Created by cbl81 on 2/12/17.
 */

public interface IssueContract {

    interface View {
        void setLoading(boolean isLoading);

        void sessionExpired();

        void issueSuccess(ApiResponse data);

        void issueError(String failureMessage);

        void issueFailure(String failureMessage);
    }

    interface Presenter {

        void apiIssue(String issue);

        void attachView(IssueContract.View view);

        void detachView();

    }

}
