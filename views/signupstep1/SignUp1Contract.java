package com.maktoday.views.signupstep1;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthProvider;
import com.maktoday.model.ApiResponse;
import com.maktoday.model.FacebookModel;
import com.maktoday.model.PojoLogin;

import java.util.HashMap;

/**
 * Created by cbl81 on 8/11/17.
 */

public interface SignUp1Contract {

    interface View {
        void setLoading(boolean isLoading);

        void sessionExpired();

        void signupSuccess(PojoLogin data);

        void signupError(String failureMessage);

        void signupSuccess(ApiResponse<PojoLogin> data);

        void signupFailure(String failureMessage);

        void onOtpSent(@NonNull String verificationId,
                       @NonNull PhoneAuthProvider.ForceResendingToken token,String countryCode, String phoneNumber);
    }

    interface Presenter {
        void apiFacebook(FacebookModel facebookModel);

        void apiSignup(HashMap<String, Object> hashMap);

        void attachView(SignUp1Contract.View view);

        void detachView();
        void verifyPhoneNumberWithFirebase(String phoneNumber, String countrycode, FirebaseAuth firebaseAuth, Activity context);

    }

}
