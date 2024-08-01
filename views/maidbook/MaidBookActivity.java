package com.maktoday.views.maidbook;

import android.content.DialogInterface;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.view.Menu;
import android.widget.Toast;

import com.facebook.GraphResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.maktoday.R;
import com.maktoday.databinding.ActivityMaidbookBinding;
import com.maktoday.model.FacebookModel;
import com.maktoday.model.PojoLogin;
import com.maktoday.model.PojoMyBooking;
import com.maktoday.model.SearchMaidBulkModel;
import com.maktoday.model.SearchMaidModel;
import com.maktoday.utils.BaseActivity;
import com.maktoday.utils.Constants;
import com.maktoday.utils.DataVariable;
import com.maktoday.utils.DialogPopup;
import com.maktoday.utils.GeneralFunction;
import com.maktoday.utils.Log;
import com.maktoday.utils.Prefs;
import com.maktoday.utils.dialog.IOSAlertDialog;
import com.maktoday.utils.facebook.FacebookLogin;
import com.maktoday.utils.facebook.FacebookLoginListener;
import com.maktoday.views.authenticate.AuthenticateActivity;
import com.maktoday.views.confirmbook.ConfirmBookFragment;
import com.maktoday.views.maidsearch.MaidFragment;
import com.maktoday.views.signupstep2.SignUp2Fragment;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by cbl81 on 31/10/17.
 */

public class MaidBookActivity extends BaseActivity implements MaidBookContract.View {
    public FacebookLogin facebookLogin;
    private static final String TAG = "MaidBookActivity";
    public FacebookLoginListener fbListener;
    public int step = 0;
    private ActivityMaidbookBinding binding;
    private SearchMaidModel searchMaidModel;
    private SearchMaidBulkModel searchMaidBulkModel;
    private PojoMyBooking.Datum bookingDataModel;
    String booking_type = "";
    String token = "";
    private MaidBookContract.Presenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_maidbook);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
   Log.d(TAG, "onCreate: StartActivity");
        init();
        setData();
        setListener();
    }

    private void init() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                           Log.w(TAG, "Fetching FCM registration token failed"+task.getException());
                            token =  "Fetching FCM registration token failed"+ task.getException().toString();
                            return;
                        }

                        // Get new FCM registration token
                        token = task.getResult();

                    }
                });

       Log.d(TAG, "init: device token"+ token);
        presenter = new MaidBookPresenter();
        presenter.attachView(this);
        booking_type = getIntent().getStringExtra(Constants.BOOKING_TYPE);
        bookingDataModel = new Gson().fromJson(getIntent().getStringExtra(Constants.BOOKING_DATA), PojoMyBooking.Datum.class);
        if (booking_type.equalsIgnoreCase("3")) {
            searchMaidBulkModel = getIntent().getParcelableExtra(Constants.SEARCH_MAID_DATA);
        } else {
            searchMaidModel = getIntent().getParcelableExtra(Constants.SEARCH_MAID_DATA);

        }

        facebookLogin = new FacebookLogin(this, this);
    }

    private void setData() {
        if (booking_type.equalsIgnoreCase("3")) {

            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                    .add(R.id.frameLayout, ConfirmBookFragment.newInstance(bookingDataModel, searchMaidBulkModel, getIntent().getStringExtra(Constants.reschuleStatus), getIntent().getStringExtra(Constants.SERVICE_ID)), "ConfirmBookFragment").commit();
        } else {

            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                    .add(R.id.frameLayout, MaidFragment.newInstance(bookingDataModel, searchMaidModel, getIntent().getStringExtra(Constants.reschuleStatus), getIntent().getStringExtra(Constants.SERVICE_ID)), "MaidFragment").commit();
        }
    }

    private void setListener() {

        fbListener = new FacebookLoginListener() {
            @Override
            public void onFbLoginSuccess() {
                if (GeneralFunction.isNetworkConnected(MaidBookActivity.this, binding.parent))
                    facebookLogin.getUserProfile();
                else {
                    GeneralFunction.showSnackBar(MaidBookActivity.this, getCurrentFocus(), getString(R.string.check_connection));
                }
            }

            @Override
            public void onFbLoginCancel() {
                Log.e("FbLogin", "Cancel");
            }

            @Override
            public void onFbLoginError() {
                Log.e("FbLogin", "Error");
            }

            @Override
            public void onGetprofileSuccess(JSONObject object, GraphResponse response) {

                try {
                    if (GeneralFunction.isNetworkConnected(MaidBookActivity.this, binding.parent)) {
                        String id, name, email,firstname="",lastname="";
                        if (object.has("first_name") && object.has("last_name")) {
                            name = object.get("first_name") + " " + object.get("last_name");
                            firstname=object.getString("first_name");
                            lastname=object.getString("last_name");
                        } else {
                            name = null;
                        }
                        if (object.has("id")) {
                            id = (String) object.get("id");
                        } else {
                            id = null;
                        }

                        if (object.has("email")) {
                            email = (String) object.get("email");
                        } else {
                            email = null;
                        }

                        FacebookModel facebookModel = new FacebookModel(Constants.UNIQUE_APP_KEY,
                                id, name, email, null, token, "ANDROID","FACEBOOK",firstname,lastname);

                        presenter.apiFBLogin(facebookModel);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        facebookLogin.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {

        DataVariable.hideSoftKeyboard(MaidBookActivity.this);
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            super.onBackPressed();
        } else {
            String fragmentTag = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName();
            switch (fragmentTag) {
                case "PaymentInfoFragment":
                    return;

                case "PaymentStateFragment":
                    showCancelDialog();
                    break;

                default:
                    getSupportFragmentManager().popBackStack();
                    break;
            }

        }
    }

    private void showCancelDialog() {

        IOSAlertDialog dialog =  IOSAlertDialog.newInstance(
                MaidBookActivity.this,
                getString(R.string.cancel_booking),
                getString(R.string.msg_cancel_booking),
                getString(R.string.OK),
                getString(R.string.cancel1),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Prefs.with(getApplicationContext()).save(Constants.ISBACK, "true");
                        getSupportFragmentManager().popBackStack();
                    }
                },
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                },
                ContextCompat.getColor(MaidBookActivity.this, R.color.coral),
                ContextCompat.getColor(MaidBookActivity.this, R.color.app_color),
                false
        );

        dialog.show(getSupportFragmentManager(), "ios_dialog");

     /**   AlertDialog dialog = new AlertDialog.Builder(MaidBookActivity.this)
                .setCancelable(true)
                .setMessage(getString(R.string.msg_cancel_booking))
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // finish();
                dialog.cancel();
                Prefs.with(getApplicationContext()).save(Constants.ISBACK,"true");
                getSupportFragmentManager().popBackStack();
            }
        })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).show();
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(MaidBookActivity.this, R.color.appColor));
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(MaidBookActivity.this, R.color.appColor));
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setAllCaps(false);
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setAllCaps(false);
*/


    }//showCancelDialog

    @Override
    public void setLoading(boolean isLoading) {
        if (isLoading) {
            GeneralFunction.showProgress(MaidBookActivity.this);
        } else {
            GeneralFunction.dismissProgress();
        }
    }

    @Override
    public void sessionExpired() {
        GeneralFunction.isUserBlocked(MaidBookActivity.this);
    }

    @Override
    public void FBLoginSuccess(PojoLogin data) {
        Prefs pref = Prefs.with(MaidBookActivity.this);
        pref.save(Constants.ACCESS_TOKEN, "bearer " + data.getAccessToken());
        pref.save(Constants.DATA, data);


        if (data.isProfileComplete()) {
            pref.save(Constants.COMPLETE_PROFILE, true);
            //startActivity(new Intent(MaidBookActivity.this, Main2Activity.class));
            LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(MaidBookActivity.this);
            Intent localIntent = new Intent("custom-event-name");
            localBroadcastManager.sendBroadcast(localIntent);

        } else {
            Log.e("dataa", new Gson().toJson(data));

            pref.save(Constants.ACCESS_TOKEN, "bearer " + data.getAccessToken());
            pref.save(Constants.DATA, data);
            if (!data.profileComplete) {

                pref.save(Constants.ACCESS_TOKEN, "bearer " + data.getAccessToken());
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                        .replace(android.R.id.content, SignUp2Fragment.newInstance("LoginProfileIncomplete"), "SignUp2Fragment").addToBackStack("SignUp2Fragment").commit();

            } else {
                if (data.isVerified) {
                    pref.save(Constants.ACCESS_TOKEN, "bearer " + data.getAccessToken());
                    pref.save(Constants.COMPLETE_PROFILE, true);
                    pref.save(Constants.USER_COUNTRY, data.getUsersAddress().country);
                    pref.save(Constants.DATA, data);

                } else {
                    finishAffinity();
                    Toast.makeText(MaidBookActivity.this, getString(R.string.msg_email_not_verified), Toast.LENGTH_LONG).show();
                    startActivity(new Intent(MaidBookActivity.this, AuthenticateActivity.class));
                }
            }
        }
    }

    @Override
    public void FBLoginError(String errorMessage) {
        new DialogPopup().alertPopup(this, getResources().getString(R.string.dialog_alert), errorMessage, "").show(getSupportFragmentManager(), "ios_dialog");
    }

    @Override
    public void FBLoginFailure(String failureMessage) {
        Log.e(TAG, "FBLoginFailure: "+failureMessage);
        new DialogPopup().alertPopup(this, getResources().getString(R.string.dialog_alert), getString(R.string.check_connection), "").show(getSupportFragmentManager(), "ios_dialog");

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }
}
