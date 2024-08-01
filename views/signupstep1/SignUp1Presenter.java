package com.maktoday.views.signupstep1;

import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.gson.Gson;
import com.maktoday.R;
import com.maktoday.model.ApiResponse;
import com.maktoday.model.FacebookModel;
import com.maktoday.model.PojoLogin;
import com.maktoday.utils.Constants;
import com.maktoday.utils.GeneralFunction;
import com.maktoday.utils.Log;
import com.maktoday.utils.Prefs;
import com.maktoday.views.Otpverification.OtpVerifyActivity;
import com.maktoday.webservices.RestClient;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.maktoday.utils.Constants.UnAuthorized;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;

/**
 * Created by cbl81 on 8/11/17.
 */

public class SignUp1Presenter implements SignUp1Contract.Presenter {

    private static final String TAG = "SignUp1Presenter";

    SignUp1Contract.View view;

    @Override
    public void attachView(SignUp1Contract.View view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        view = null;
    }

    @Override
    public void apiFacebook(FacebookModel facebookModel) {
        if (view != null) {

            view.setLoading(true);
            RestClient.getModalApiService().apiFacebookLogin(facebookModel).enqueue(new Callback<ApiResponse<PojoLogin>>() {
                @Override
                public void onResponse(Call<ApiResponse<PojoLogin>> call, Response<ApiResponse<PojoLogin>> response) {
                    GeneralFunction.dismissProgress();
                    if (response.isSuccessful()) {
                        if (view != null) {
                            view.signupSuccess(response.body());
                        }
                    } else {
                        if (response.code() == UnAuthorized) {
                            if (view != null) {
                                view.sessionExpired();
                            }
                        } else {
                            try {
                                view.signupError(new JSONObject(response.errorBody().string()).getString("message"));
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<PojoLogin>> call, Throwable t) {
                    if (view != null) {
                        GeneralFunction.dismissProgress();
                        view.signupFailure(t.getMessage());
                    }
                }

            });
        }
    }


@Override
public void verifyPhoneNumberWithFirebase(String phoneNumber, String countrycode, FirebaseAuth firebaseAuth, Activity context) {
        if(view != null) {
        view.setLoading(false);
           view.setLoading(true);
            PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                    Log.e(TAG, "onVerificationCompleted: setLoading(false); 1");
                    view.setLoading(false);
                    Log.d(TAG, "onVerificationCompleted:" + credential);
                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    android.util.Log.e(TAG, "onVerificationFailed: setLoading(false); 2");
                  view.setLoading(false);
                    if (e instanceof FirebaseAuthInvalidCredentialsException) {
                        android.util.Log.e(TAG, "onVerificationFailed: ", e);
                       Toast.makeText(context,context.getString(R.string.invalid_otp), Toast.LENGTH_SHORT).show();
                        // Invalid request
                    } else if (e instanceof FirebaseTooManyRequestsException) {
                        android.util.Log.e(TAG, "onVerificationFailed: FirebaseTooManyRequestsException ", e);
                        Toast.makeText(context, context.getString(R.string.multiple_time_submit_otp), Toast.LENGTH_SHORT).show();
                    }

                    // Show a message and update the UI
                }

                @Override
                public void onCodeSent(@NonNull String verificationId,
                                       @NonNull PhoneAuthProvider.ForceResendingToken token) {
                    // The SMS verification code has been sent to the provided phone number, we
                    // now need to ask the user to enter the code and then construct a credential
                    // by combining the code with a verification ID.

                  view.onOtpSent(verificationId,token,countrycode,phoneNumber);
                }
            };

            Log.e(TAG, "verifyPhoneNumber: " + phoneNumber + " Country code " + countrycode);

            PhoneAuthOptions options =
                    PhoneAuthOptions.newBuilder(firebaseAuth)
                            .setPhoneNumber(countrycode + phoneNumber)       // Phone number to verify
                            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                            .setActivity(context)
                            .setForceResendingToken(Prefs.with(context).getObject(Constants.FIREBASE_AUTH_RESEND_TOKEN, PhoneAuthProvider.ForceResendingToken.class))// OnVerificationStateChangedCallbacks
                            .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                            .build();
            PhoneAuthProvider.verifyPhoneNumber(options);
        }
    }



    @Override
    public void apiSignup(HashMap<String, Object> map) {
        Log.e(TAG, " ----------- apiSignUp() ");
        Log.e(TAG, " ----------- map = " + map);

        if (view != null) {

            view.setLoading(true);
            RestClient.getModalApiService().apiSignUp(map).enqueue(new Callback<ApiResponse<PojoLogin>>() {
                @Override
                public void onResponse(Call<ApiResponse<PojoLogin>> call, Response<ApiResponse<PojoLogin>> response) {
                    Log.e(TAG, " ----------- apiSignUp() "  + new Gson().toJson(response));


                    if (response.isSuccessful()) {

                        if (view != null) {
                            view.signupSuccess(response.body().getData());
                        }

                    } else {
                        view.setLoading(false);
                        if (response.code() == UnAuthorized) {
                            if (view != null) {
                                view.sessionExpired();
                            }
                        } else {
                            try {
                                if (view != null) {
                                    view.signupError(new JSONObject(response.errorBody().string()).getString("message"));
                                }
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<PojoLogin>> call, Throwable t) {
                    Log.e(TAG, " ----------- onFailure, t = " + t.getMessage());
                    t.printStackTrace();

                    if (view != null) {
                       view.setLoading(false);
                        view.signupFailure(t.getMessage());
                    }
                }

            });
        }
    }

}
