package com.maktoday.views.Otpverification;

import com.maktoday.model.OtpVerifiyModel;
import com.maktoday.model.PojoAgencyList;


import java.util.HashMap;

public class OtpVerifyContact {
    interface View {
        void setLoading(boolean isLoading);

        void sessionExpired();

        void OtpveifySuccess(OtpVerifiyModel data);

        void OtpveifyError(String failureMessage);

        void OtpveifyFailure(String failureMessage);

        void ResendOtpSuccess(OtpVerifiyModel body);
    }

    interface Presenter {

        void apiOtpVerify(HashMap<String, String> map);
        void apiOtpResend(HashMap<String, String> map);
        void attachView(OtpVerifyContact.View view);

        void detachView();

    }
}

