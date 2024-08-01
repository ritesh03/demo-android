package com.maktoday.views.splash;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;

import com.maktoday.utils.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.applinks.AppLinkData;
import com.google.firebase.FirebaseApp;
import com.google.gson.Gson;
import com.maktoday.R;
import com.maktoday.model.PojoLogin;
import com.maktoday.model.ProfilePicURL;
import com.maktoday.utils.Constants;
import com.maktoday.utils.Prefs;
import com.maktoday.views.TitleScreen.TitleScreenActivity;
import com.maktoday.views.authenticate.AuthenticateActivity;
import com.maktoday.views.main.Main2Activity;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.Objects;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
       Log.d(TAG, "onCreate: StartActivity");

        FirebaseApp.initializeApp(this);

        getSignatures();

        Locale locale = new Locale("uk");
        Locale.setDefault(locale);

        init();

        AppLinkData.fetchDeferredAppLinkData(this,
                appLinkData -> {
                    // Process app link data
                }
        );

    }//onCreate

    private void init() {
        new Handler().postDelayed(() -> {

            Intent intent = null;
            Prefs prefs = Prefs.with(SplashActivity.this);
            PojoLogin loginData = prefs.getObject(Constants.DATA, PojoLogin.class);
            Log.e("token splash", prefs.getString(Constants.ACCESS_TOKEN, ""));
/*----------------------------------IF ACCESS TOKEN EMPTY-----------------------------------------------------*/
            if (prefs.getString(Constants.ACCESS_TOKEN, "").isEmpty()) {
                Log.d(TAG, "init:  if (prefs.getString(Constants.ACCESS_TOKEN, \"\").isEmpty()) ");
                intent = new Intent(SplashActivity.this, TitleScreenActivity.class);
            } 
          /*  else if (prefs.getString(Constants.ACCESS_TOKEN, "").contains("bearer") && loginData.profileComplete) {
                Log.d(TAG, "init: profile complete -n"+loginData.profileComplete);
                Log.d(TAG, "init:  else if (prefs.getString(Constants.ACCESS_TOKEN, \"\").contains(\"bearer\") && loginData.profileComplete)");
                intent = new Intent(SplashActivity.this, Main2Activity.class);
            } */else if (loginData != null && loginData.isGuestFlag && loginData.isFirstBookingDone()) {
                Log.d(TAG, "init: else if (loginData != null && loginData.isGuestFlag && loginData.isFirstBookingDone())");
                intent = new Intent(SplashActivity.this, Main2Activity.class);
            } else if (!prefs.getString(Constants.ACCESS_TOKEN, "").isEmpty() && !Objects.requireNonNull(loginData).profileComplete) {
                Log.d(TAG, "init: else if (!prefs.getString(Constants.ACCESS_TOKEN, \"\").isEmpty() && !Objects.requireNonNull(loginData).profileComplete)");
                intent = new Intent(SplashActivity.this, AuthenticateActivity.class);
            } else if (loginData != null && !loginData.profileComplete) {
                Log.d(TAG, "init: else if (loginData != null && !loginData.profileComplete)");
                intent = new Intent(SplashActivity.this, AuthenticateActivity.class);
                intent.putExtra(Constants.COMPLETE_PROFILE, false);
            } else {
                Log.d(TAG, "init: else  :: ");
                intent = new Intent(SplashActivity.this, Main2Activity.class);
                Bundle bundle = getIntent().getExtras();


                if (bundle != null && !bundle.getString(Constants.BODY, "").isEmpty()) {
                    android.util.Log.e(TAG, "init: fcm type  "+ bundle.getString(Constants.FCM_TYPE) );
                    intent.putExtra(Constants.Notification_DATA, true);
                    intent.putExtra(Constants.FCM_TYPE, bundle.getString(Constants.FCM_TYPE));
                    intent.putExtra(Constants.Notification_DATA, true);
                    intent.putExtra(Constants.BODY, bundle.getString(Constants.BODY));
                    intent.putExtra(Constants.FCM_TYPE, bundle.getString(Constants.FCM_TYPE));
                    if(bundle.containsKey("link")){
                        intent.putExtra(Constants.NOTI_LINK,bundle.getString("link"));
                    }
                    intent.putExtra(Constants.MAID_ID, bundle.getString(Constants.MAID_ID));
                    intent.putExtra(Constants.REQ_ID, bundle.getString(Constants.REQ_ID));
                    intent.putExtra(Constants.MAID_NAME, bundle.getString(Constants.MAID_NAME));
                    ProfilePicURL profilePicURL = new Gson().fromJson(bundle.getString(Constants.MAID_PIC), ProfilePicURL.class);
                    if (profilePicURL != null) {
                        intent.putExtra(Constants.MAID_PIC, profilePicURL.getOriginal());
                    }
                    intent.putExtra(Constants.RECEIVER_ID, bundle.getString("senderId"));
                    intent.putExtra(Constants.SERVICE_ID, bundle.getString(Constants.SERVICE_ID));
                    intent.putExtra(Constants.FULL_NAME, bundle.getString(Constants.FULL_NAME));
                }
            }

            startActivity(intent);
            finish();
        }, 2000);

    }//init

    private void getSignatures() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.maktoday",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.d(TAG, "getSignatures: exception"+e.getMessage());

        } catch (NoSuchAlgorithmException e) {
            Log.d(TAG, "getSignatures: exception2"+e.getMessage());

        }

    }//getSignatures

}//splashActivity
