package com.maktoday.utils;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.maktoday.BuildConfig;
import com.maktoday.views.main.Main2Activity;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

/**
 * Created by cbl81 on 24/10/17.
 */

public class BaseActivity extends AppCompatActivity {

    public FirebaseAnalytics mFirebaseAnalytics;
    private static final String TAG = "BaseActivity";
    private final int MY_UPDATE_REQUEST_CODE = 1001;
    private AppUpdateManager appUpdateManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        CheckFOrUpdate();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(LocaleHelper.onAttach(newBase,Prefs.with(newBase).getString(Constants.LANGUAGE_CODE,"en"))));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //App Update
        if(requestCode == MY_UPDATE_REQUEST_CODE && resultCode == RESULT_CANCELED){
            Log.d(TAG, "onActivityResult: cancel ");
            CheckFOrUpdate();
        }
        else {
            Log.d(TAG, "onActivityResult: App updated");
        }

    }

    private  void CheckFOrUpdate(){
        if(!BuildConfig.DEBUG) {
            //App update
            appUpdateManager = AppUpdateManagerFactory.create(BaseActivity.this);

// Returns an intent object that you use to check for an update.
            Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

// Checks that the platform will allow the specified type of update.
            appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
                Log.d(TAG, "onResume: app update info :--  " + new Gson().toJson(appUpdateInfo.updateAvailability()));
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    // This example applies an immediate update. To apply a flexible update
                    // instead, pass in AppUpdateType.FLEXIBLE
                    /*   && appUpdateInfo.updatePriority() >= 4 *//* high priority *//*
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)*/)
                {
                    Log.d(TAG, "onResume: true 146  ");
                    try {
                        appUpdateManager.startUpdateFlowForResult(
                                // Pass the intent that is returned by 'getAppUpdateInfo()'.
                                appUpdateInfo,
                                // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                                AppUpdateType.IMMEDIATE,
                                // The current activity making the update request.
                                this,
                                // Include a request code to later monitor this update request.
                                MY_UPDATE_REQUEST_CODE

                        );
                    }
                    catch (IntentSender.SendIntentException e) {
                        Log.d(TAG, "onCreate: error app update :-- " + e.getMessage());
                        e.printStackTrace();
                    }
                    // Request the update.
                }
            });
        }

    }



}
