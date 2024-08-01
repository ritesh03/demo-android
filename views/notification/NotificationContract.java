package com.maktoday.views.notification;

import com.maktoday.model.PojoNotification;

public interface NotificationContract {

    interface View {
        void setLoading(boolean isLoading);

        void sessionExpired();

        void notificationSuccess(PojoNotification data);

        void notificationError(String errorMessage);

        void notificationFailure(String failureMessage);
    }

    interface Presenter {

        void apiNotification();

        void attachView(NotificationContract.View view);

        void detachView();

    }
}
