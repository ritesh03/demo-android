package com.maktoday.views.cardlist;

import com.maktoday.model.PaytabTransactionVerificationResponse;
import com.maktoday.model.PojoCardList;
import com.maktoday.model.PojoCreatePayment;

import java.util.HashMap;

/**
 * Created by cbl81 on 23/11/17.
 */

public interface CardListContract {

    interface View {
        void setLoading(boolean isLoading);

        void sessionExpired();

        void payTabTransactionSuccess(PaytabTransactionVerificationResponse body);

        void cardListSuccess(PojoCardList data);

        void createPaymentSuccess(PojoCreatePayment.Data data);

        void successPaymentPaytabs(PojoCreatePayment.Data data);

        void deleteCardSuccess();

        void error(String failureMessage);

        void failure(String failureMessage);

        void addCardAddedSuccess(String cardToken);

        void successLanguageChange(String language);
    }

    interface Presenter {

        void apiCardList();

        void apiCreatePayment(HashMap<String, String> map);

        void apiCardDelete(HashMap<String, String> map);

        void apiCardPaymentPaytabs(HashMap<String, String> map);

        void apiCardPaymentPaytabsBulk(HashMap<String, String> map);

        void apiPaytabTransactionVerification(HashMap<String, String> map);

        void attachView(CardListContract.View view);

        void apiAddPayment(HashMap<String, String> map);

        void detachView();

        void apiChangeLanguage(String language);

    }
}
