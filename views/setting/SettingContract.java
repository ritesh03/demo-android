package com.maktoday.views.setting;

import com.maktoday.model.DeleteAccountResponse;

import java.util.HashMap;

public class SettingContract {


    interface View {
        void setLoading(boolean isLoading);
        void successDeleteAccount(DeleteAccountResponse deleteAccountResponse);
        // void sessionExpired();

        void error(String failureMessage);
        void sessionExpired();

        void failure(String failureMessage);
    }

    interface Presenter {

        void attachView(SettingContract.View view);
        void apiDeleteAccount(HashMap<String,String> map);
        void detachView();

    }

}
