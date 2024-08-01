package com.maktoday.views.Otpverification;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;

import com.facebook.appevents.AppEventsLogger;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.maktoday.utils.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.maktoday.R;
import com.maktoday.databinding.ActivityOtpVerifyBinding;
import com.maktoday.model.OtpVerifiyModel;
import com.maktoday.utils.BaseActivity;
import com.maktoday.utils.Constants;
import com.maktoday.utils.GeneralFunction;
import com.maktoday.utils.Prefs;
import com.maktoday.views.main.Main2Activity;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class OtpVerifyActivity extends BaseActivity implements OtpVerifyContact.View, View.OnClickListener {
    private ActivityOtpVerifyBinding binding;
    private OtpVerifyContact.Presenter presenter;
    private static final String TAG = "OtpVerifyActivity";
    AppEventsLogger fbLogger;
    FirebaseAuth firebaseAuth;
    String phoneNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_otp_verify);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_otp_verify);
        Log.d(TAG, "onCreate: StartActivity");
        init();
    }

    private void init() {
        presenter=new OtpVerifyPresenter();
        presenter.attachView(this);
        fbLogger = AppEventsLogger.newLogger(this);
        firebaseAuth= FirebaseAuth.getInstance();


        try {
            Log.e(TAG, "init: Phone NUmber :: " + getIntent().getStringExtra("PhoneNumber"));
            phoneNumber = getIntent().getStringExtra("PhoneNumber");
        }catch (Exception e){
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(e);
        }
        //////////set timer
        new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                binding.tvResend.setEnabled(false);
                binding.tvResend.setTextColor(Color.parseColor("#66000000"));
                if(millisUntilFinished<10000){
                    binding.tvTimer.setText("00:"+"0"+String.valueOf(millisUntilFinished / 1000));
                }else {
                    binding.tvTimer.setText("00:"+String.valueOf(millisUntilFinished / 1000));
                }

            }

            public void onFinish() {
                binding.tvResend.setEnabled(true);
                binding.tvResend.setTextColor(Color.parseColor("#FF000000"));
                binding.tvTimer.setVisibility(View.GONE);
               // txt_resend.setText("Didn't receive a text message? Resend code");
            }
        }.start();

        binding.back.setOnClickListener(this);
        binding.tvSubmit.setOnClickListener(this);
        binding.tvResend.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
       switch (view.getId()){
           case R.id.back:
               GeneralFunction.hideKeyboardFromActivity(this);
               finish();
               break;
           case R.id.tvSubmit:
               GeneralFunction.hideKeyboardFromActivity(this);
               if (binding.etotpnumber.getText().toString().trim().isEmpty()){
                   GeneralFunction.showSnackBar(this, binding.parent, getString(R.string.enter_your_mak_today_code));
                   binding.etotpnumber.requestFocus();
               }else {
                 if (GeneralFunction.isNetworkConnected(this,binding.tvSubmit)){

                     HashMap<String,String> hashMap=new HashMap<>();
                     hashMap.put("uniquieAppKey", Constants.UNIQUE_APP_KEY);
                     hashMap.put("otp",binding.etotpnumber.getText().toString());
                     Log.e("verify otp",new Gson().toJson(hashMap));
                    // presenter.apiOtpVerify(hashMap);
                     PhoneAuthCredential credential =  PhoneAuthProvider.getCredential(Prefs.with(this).getString(Constants.FIREBASE_AUTH_VERIFICATION_ID,""), binding.etotpnumber.getText().toString());
                   //   credential = PhoneAuthProvider.getCredential(Prefs.with(this).getString("verificationId",""),binding.etPhone.text.toString());
                     signInWithPhoneAuthCredential(credential);
                 }
               }
               break;
           case R.id.tvResend:
               if (GeneralFunction.isNetworkConnected(this,binding.tvResend)){
                   HashMap<String,String> hashMap=new HashMap<>();
                   hashMap.put("uniquieAppKey", Constants.UNIQUE_APP_KEY);
                  // presenter.apiOtpResend(hashMap);
                   resendOtpToPhoneNumber(phoneNumber);
                   //////////set timer
                   new CountDownTimer(60000, 1000) {

                       public void onTick(long millisUntilFinished) {
                           binding.tvTimer.setVisibility(View.VISIBLE);
                           binding.tvResend.setEnabled(false);
                           binding.tvResend.setTextColor(Color.parseColor("#66000000"));
                           if(millisUntilFinished<10000){
                               binding.tvTimer.setText("00:"+"0"+String.valueOf(millisUntilFinished / 1000));
                           }else {
                               binding.tvTimer.setText("00:"+String.valueOf(millisUntilFinished / 1000));
                           }
                       }

                       @SuppressLint("ResourceAsColor")
                       public void onFinish() {
                           binding.tvTimer.setVisibility(View.GONE);
                           binding.tvResend.setTextColor(Color.parseColor("#FF000000"));
                           binding.tvResend.setEnabled(true);

                       }
                   }.start();

               }
               break;
       }
    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        setLoading(true);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.e(TAG , "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();

                            HashMap<String,String> hashMap=new HashMap<>();
                            hashMap.put("uniquieAppKey", Constants.UNIQUE_APP_KEY);
                            hashMap.put("otp",binding.etotpnumber.getText().toString());
                            Log.e("verify otp",new Gson().toJson(hashMap));
                            presenter.apiOtpVerify(hashMap);
                            firebaseAuth.signOut();
                            //Firebase.auth.signOut()
                        } else {
                            setLoading(true);
                            // Sign in failed, display a message and update the UI
                            Log.e(TAG, "signInWithCredential:failure  "+ Objects.requireNonNull(task.getException()).getMessage());
                            Toast.makeText(OtpVerifyActivity.this, getString(R.string.invalid_otp), Toast.LENGTH_SHORT).show();
                            setLoading(false);
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException){

                            }

                        }

                    }
                });

    }



    private void resendOtpToPhoneNumber(String phoneNumber) {
        android.util.Log.e(TAG, "verifyPhoneNumber: "+phoneNumber );
        setLoading(true);

        PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                setLoading(false);
                Log.d(TAG, "onVerificationCompleted:" + credential);
            }
            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                setLoading(false);
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    android.util.Log.e(TAG, "onVerificationFailed: ",e );
                    Toast.makeText(OtpVerifyActivity.this, getString(R.string.invalid_otp), Toast.LENGTH_SHORT).show();
                    // Invalid request
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    android.util.Log.e(TAG, "onVerificationFailed: FirebaseTooManyRequestsException ",e );
                    Toast.makeText(OtpVerifyActivity.this,getString(R.string.multiple_time_submit_otp), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                setLoading(false);
                Log.d(TAG, "onCodeSent:" + verificationId);
                Prefs.with(OtpVerifyActivity.this).save(Constants.FIREBASE_AUTH_VERIFICATION_ID,verificationId);
                Prefs.with(OtpVerifyActivity.this).save(Constants.FIREBASE_AUTH_RESEND_TOKEN,token);
            }
        };


        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(OtpVerifyActivity.this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)
                        .setForceResendingToken(Prefs.with(OtpVerifyActivity.this).getObject(Constants.FIREBASE_AUTH_RESEND_TOKEN, PhoneAuthProvider.ForceResendingToken.class))// OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }


    @Override
    public void setLoading(boolean isLoading) {
        if(isLoading) {
            GeneralFunction.showProgress(this);
        }else{
            GeneralFunction.dismissProgress();
        }
    }

    @Override
    public void sessionExpired() {
        GeneralFunction.isUserBlocked(OtpVerifyActivity.this);
    }

    @Override
    public void OtpveifySuccess(OtpVerifiyModel data) {

      // Toast.makeText(this, getString(R.string.msg_please_email_verified), Toast.LENGTH_LONG).show();
       // Toast.makeText(this,data.message, Toast.LENGTH_LONG).show();

        if(!data.data.emailVerified) {
            Toast.makeText(this, getString(R.string.msg_please_email), Toast.LENGTH_LONG).show();
        }
        Prefs.with(this).save(Constants.DATA, data.data);
        Prefs.with(this).save(Constants.COMPLETE_PROFILE,data.data.isProfileComplete());
        mFirebaseAnalytics.logEvent("Verified_SignUp", null);
        fbLogger.logEvent("Verified_SignUp");
        startActivity(new Intent(this, Main2Activity.class));
        //startActivity(new Intent(this, AuthenticateActivity.class));
        finishAffinity();
    }

    @Override
    public void OtpveifyError(String failureMessage) {
 Log.e(TAG, "OtpveifyError: "+failureMessage);
        Toast.makeText(this, failureMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void OtpveifyFailure(String failureMessage) {
        Log.e(TAG, "OtpveifyFailure: "+failureMessage);
        Toast.makeText(this, getString(R.string.check_connection), Toast.LENGTH_LONG).show();
    }

    @Override
    public void ResendOtpSuccess(OtpVerifiyModel data) {
        //Toast.makeText(this,data.message, Toast.LENGTH_LONG).show();
    }


}