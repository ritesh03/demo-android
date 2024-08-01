package com.maktoday.views.addNewCard;

import com.maktoday.model.PojoCreatePayment;

import java.util.HashMap;

/**
 * Created by cbl81 on 23/11/17.
 */

public interface AddCardContract {
    interface View {
        void setLoading(boolean isLoading);

        void sessionExpired();

        void createPaymentSuccess(PojoCreatePayment.Data data);

        void addCardSuccess(String cardToken);

        void paymentError(String failureMessage);

        void paymentFailure(String failureMessage);
    }

    interface Presenter {

        void apiCreatePayment(HashMap<String, String> map);

        void apiAddPayment(HashMap<String, String> map);

        void attachView(AddCardContract.View view);

        void detachView();

    }
}
