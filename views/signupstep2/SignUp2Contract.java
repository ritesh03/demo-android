package com.maktoday.views.signupstep2;

import androidx.annotation.Nullable;

import com.maktoday.model.ApiResponse;
import com.maktoday.model.BookServiceBulkModel;
import com.maktoday.model.BookServiceModel;
import com.maktoday.model.PojoLogin;
import com.maktoday.model.PojoService;
import com.maktoday.model.PojoServiceNew;
import com.maktoday.model.SignUpModel;

import java.util.TimeZone;

/**
 * Created by cbl81 on 13/11/17.
 */

public class SignUp2Contract {

    interface View {
        void setLoading(boolean isLoading);

        void sessionExpired();

        void signupSuccess(ApiResponse<PojoLogin> data);

        void billingInfoSuccess(ApiResponse<PojoLogin> data);

        void bookServiceSuccess(PojoService pojoService);

        void bookServiceSuccessNew(PojoServiceNew pojoService);

        void bookServiceSuccessBulk(PojoServiceNew pojoService);

        void updateProfileSuccess(PojoLogin apiRespone);

        void bookingTimeZoneReceived(@Nullable TimeZone timeZone);

        void signupError(String failureMessage);

        void signupFailure(String failureMessage);
    }

    interface Presenter {

        void apiSignup(SignUpModel signUpModel);

        void apiAddBillingInfo(SignUpModel signUpModel);

        void getTimeZoneFromLatLong(final double latitude, final double longitude, final String googleApiKey);

        void apiUpdateProfile(SignUpModel signUpModel);

        void apiBookService(BookServiceModel bookServiceModel);

        void apiBulkBookService(BookServiceBulkModel bookServiceModel);

        void attachView(SignUp2Contract.View view);

        void detachView();

    }
}
