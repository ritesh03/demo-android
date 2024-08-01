package com.maktoday.views.authenticate;

import static com.maktoday.utils.Constants.USER_COUNTRY;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;

import com.facebook.GraphResponse;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.maktoday.R;
import com.maktoday.databinding.ActivityAuthenticateBinding;
import com.maktoday.model.ApiResponse;
import com.maktoday.model.FacebookModel;
import com.maktoday.model.PojoLogin;
import com.maktoday.utils.BaseActivity;
import com.maktoday.utils.Constants;
import com.maktoday.utils.DataVariable;
import com.maktoday.utils.DialogPopup;
import com.maktoday.utils.GeneralFunction;
import com.maktoday.utils.Log;
import com.maktoday.utils.Prefs;
import com.maktoday.utils.facebook.FacebookLogin;
import com.maktoday.utils.facebook.FacebookLoginListener;
import com.maktoday.views.changelanguage.ChangeLanguageActivity;
import com.maktoday.views.confirmbook.ConfirmBookFragment;
import com.maktoday.views.login.LoginFragment;
import com.maktoday.views.main.Main2Activity;
import com.maktoday.views.signupstep1.SignUp1Fragment;
import com.maktoday.views.signupstep2.SignUp2Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

/**
 * Created by cbl81 on 24/10/17.
 */

public class AuthenticateActivity extends BaseActivity implements AuthenticateContract.View, View.OnClickListener {

    private static final String TAG = "AuthenticateActivity";
    public static GoogleSignInClient mGoogleSignInClient;
    public static String user_type = "native";
    public static String user_type_TEMP = "";
    public FacebookLogin facebookLogin;
    public FacebookLoginListener fbListener;
    private int RC_SIGN_IN = 32;
    private ActivityAuthenticateBinding binding;
    private AuthenticateContract.Presenter presenter;
    //  public static LatLng latLng;
    //  private FusedLocationProviderClient mFusedLocationClient;
    // LocationManager locationManager;
    // private LocationRequest locationRequest;
//    private LocationCallback mLocationCallback;
//  public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    //    boolean GpsStatus;
    private String token = "";


//
//    ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
//            new ActivityResultContracts.RequestPermission(),
//            new ActivityResultCallback<Boolean>() {
//
//                @Override
//                public void onActivityResult(Boolean result) {
//                    android.util.Log.e(TAG, "onActivityResult: notification Permisstion result :-- "+ result );
//                    if(!result){
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                            askNotificationPermission();
//                        }
//                    }
//                }
//            });

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(
                this, R.layout.activity_authenticate);
        Log.d(TAG, "onCreate: StartActivity");
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(AuthenticateActivity.this, gso);
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed" + task.getException().toString());
                            token = "Fetching FCM registration token failed" + Objects.requireNonNull(task.getException()).toString();
                            return;
                        }
                        // Get new FCM registration token
                        token = task.getResult();
                    }
                });

        Log.d(TAG, "init: device token" + token);
        // checkLocationPermission();
        init();

        if (ConfirmBookFragment.latLng != null) {
            ConfirmBookFragment.latLng = null;
        }
        setData();
        setListener();
//        String language_click_status = Prefs.with(AuthenticateActivity.this).getString(Constants.LANGUAGE_Click_Status, "no");
//        if (language_click_status.equalsIgnoreCase("yes")) {
//
//        } else {
//            //  startActivity(new Intent(AuthenticateActivity.this, ChangeLanguageActivity.class).putExtra("type","login"));
//        }

    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onResume() {
        super.onResume();
        String language_click_status = Prefs.with(AuthenticateActivity.this).getString(Constants.LANGUAGE_Click_Status, "no");
        if (language_click_status.equalsIgnoreCase("yes")) {

            init();
            setData();
            setListener();

        } else {
            if (Prefs.get().getString(USER_COUNTRY, "").equalsIgnoreCase("Bahrain")) {
                startActivity(new Intent(AuthenticateActivity.this, ChangeLanguageActivity.class).putExtra("type", "login"));
            } else {
                Prefs.with(this).save(Constants.LANGUAGE_Click_Status, "yes");
                Prefs.with(this).save(Constants.LANGUAGE_CODE, "en");

            }

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void init() {
        // overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        DataVariable.hideSoftKeyboard(this);
        presenter = new AuthenticatePresenter();
        presenter.attachView(this);
        facebookLogin = new FacebookLogin(this, this);
        // mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

    }

    private void setData() {

        if (getIntent().hasExtra(Constants.AUTHENTICATE_TYPE)) {
            binding.parent.setVisibility(View.GONE);
            switch (getIntent().getStringExtra(Constants.AUTHENTICATE_TYPE)) {
                case "SignIn":
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right)
                            .add(android.R.id.content, LoginFragment.newInstance("Normal", true), "LoginFragment").commit();

                    break;

                case "SignUp":
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right)
                            .replace(android.R.id.content, SignUp1Fragment.newInstance("Normal", true), "SignUp1Fragment").commit();

                    break;
            }
        }

        fbListener = new FacebookLoginListener() {
            @Override
            public void onFbLoginSuccess() {
                if (GeneralFunction.isNetworkConnected(AuthenticateActivity.this, binding.parent))
                    facebookLogin.getUserProfile();
                else {
                    GeneralFunction.showSnackBar(AuthenticateActivity.this, getCurrentFocus(), getString(R.string.check_connection));
                }
            }

            @Override
            public void onFbLoginCancel() {
                Log.e(TAG, "FbLogin " + "Cancel");
            }

            @Override
            public void onFbLoginError() {
                Log.e(TAG, "FbLogin " + "Error");
            }

            @Override
            public void onGetprofileSuccess(JSONObject object, GraphResponse response) {
                Log.d(TAG, "onGetprofileSuccess: call ");
                try {
//                    if (GeneralFunction.isNetworkConnected(AuthenticateActivity.this, binding.parent)) {
                    Log.d(TAG, "onGetprofileSuccess: " + new Gson().toJson(object));
                    String id, name, email, firstName = "", lastName = "";
                    if (object.has("first_name") && object.has("last_name")) {
                        name = ((String) object.get("first_name")) + " " + ((String) object.get("last_name"));
                        firstName = (String) object.get("first_name");
                        lastName = (String) object.get("last_name");
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
                    //Facebook Login
                    FacebookModel facebookModel = new FacebookModel(Constants.UNIQUE_APP_KEY,
                            id, name, email, null, token, "ANDROID", "FACEBOOK", firstName, lastName);

                    presenter.apiFacebook(facebookModel);
                                    /*getSupportFragmentManager().beginTransaction()
                                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                                .add(android.R.id.content, SignUp1Fragment.newInstance("Social",facebookModel), "SignUp1Fragment").addToBackStack("SignUp1Fragment").commit();*/


                } catch (JSONException e) {
                    Log.d(TAG, "onGetprofileSuccess: json exception" + e.getMessage());
                    e.printStackTrace();
                } catch (Exception e) {
                    Log.d(TAG, "onGetprofileSuccess: exception " + e.getMessage());
                }

            }
        };
    }

    private void setListener() {
        binding.tvSignIn.setOnClickListener(this);
        binding.btnFacebook.setOnClickListener(this);
        binding.tvSignUp.setOnClickListener(this);
        binding.btnGoogle.setOnClickListener(this);
        binding.tvGuest.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvSignIn:
                android.util.Log.e(TAG, "onClick: btn signin ");

                mFirebaseAnalytics.logEvent("REGSCR_signin", null);
                user_type = "native";
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                        .replace(android.R.id.content, LoginFragment.newInstance("Normal"), "LoginFragment").addToBackStack("LoginFragment").commit();
                break;
            case R.id.btnFacebook:
                android.util.Log.e(TAG, "onClick: btn facebook ");

                mFirebaseAnalytics.logEvent("REGSCR_fb_signin", null);
                user_type = "social";
                if (GeneralFunction.isNetworkConnected(AuthenticateActivity.this, binding.parent)) {
                    facebookLogin.setFacebookLoginListener(fbListener);
                    facebookLogin.performLogin();
                } else {
                    GeneralFunction.showSnackBar(AuthenticateActivity.this, binding.parent, getString(R.string.check_connection));
                }
                break;
            case R.id.btnGoogle:
                android.util.Log.e(TAG, "onClick: btn google ");
                mFirebaseAnalytics.logEvent("REGSCR_gl_signin", null);
                user_type = "social";
                if (GeneralFunction.isNetworkConnected(AuthenticateActivity.this, binding.parent)) {

                    if (AuthenticateActivity.mGoogleSignInClient != null) {
                        AuthenticateActivity.mGoogleSignInClient.signOut()
                                .addOnCompleteListener(AuthenticateActivity.this, new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                    }
                                });
                    }

                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, RC_SIGN_IN);
                } else {
                    GeneralFunction.showSnackBar(AuthenticateActivity.this, binding.parent, getString(R.string.check_connection));
                }
                break;
            case R.id.tvSignUp:
                mFirebaseAnalytics.logEvent("REGSCR_signup", null);
                user_type = "native";
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                        .add(android.R.id.content, SignUp1Fragment.newInstance("Normal"), "SignUp1Fragment").addToBackStack("SignUp1Fragment").commit();

                break;
            case R.id.tvGuest:
                Prefs prefs = Prefs.with(AuthenticateActivity.this);
                PojoLogin data = new PojoLogin();
                data.setGuestFlag(true);
                prefs.save(Constants.DATA, data);

                finishAffinity();
                Prefs.with(AuthenticateActivity.this).save(Constants.ACCESS_TOKEN, "bearer");
                startActivity(new Intent(AuthenticateActivity.this, Main2Activity.class));
                break;

        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            //Toast.makeText(getActivity(), "yes", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "data212" + "" + new Gson().toJson(account));
            Log.e(TAG, "namee" + account.getDisplayName() + "," + account.getAccount().name);
            // StringTokenizer tokens = new StringTokenizer(account.getDisplayName(), " ");
            String firstname = account.getGivenName(); //tokens.nextToken();// this will contain "Fruit"
            String lastname = account.getFamilyName();//tokens.nextToken();
            Log.e(TAG, "first name" + firstname + "," + lastname);
            //Google Login
            FacebookModel facebookModel = new FacebookModel(Constants.UNIQUE_APP_KEY,
                    account.getId(), account.getDisplayName(), account.getEmail(), null, token, Constants.DEVICE_TYPE, "GOOGLE", firstname, lastname);

            Log.e(TAG, "data212" + new Gson().toJson(facebookModel));

            presenter.apiFacebook(facebookModel);
/*            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                    .add(android.R.id.content, SignUp1Fragment.newInstance("Social",facebookModel), "SignUp1Fragment").addToBackStack("SignUp1Fragment").commit();*/
            // Signed in successfully, show authenticated UI.
            //   updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.s
            Log.e(TAG, "signInResult:failed code=" + e.getLocalizedMessage());
            // updateUI(null);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Log.e(TAG, "InSide");
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);

        } else {
            Log.d(TAG, "onActivityResult: faceboook");
            facebookLogin.onActivityResult(requestCode, resultCode, data);
        }
        Log.e(TAG, "Inside");
        Log.e(TAG, "check1sdd " + requestCode);
    }

    @Override
    public void setLoading(boolean isLoading) {

        if (isLoading)
            GeneralFunction.showProgress(AuthenticateActivity.this);
        else
            GeneralFunction.dismissProgress();

    }

    @Override
    public void sessionExpired() {
        GeneralFunction.isUserBlocked(AuthenticateActivity.this);
    }

    @Override
    public void signupSuccess(ApiResponse<PojoLogin> data) {
        /* if(data.getData().isVerified) {*/
        Log.e("fb res", new Gson().toJson(data));
        Prefs pref = Prefs.with(AuthenticateActivity.this);
        pref.save(Constants.ACCESS_TOKEN, "bearer " + data.getData().getAccessToken());
        Prefs.with(AuthenticateActivity.this).save(Constants.EMAIL_ID, data.getData().email);
        pref.save(Constants.USER_IDs, data.getData()._id);
        pref.save(Constants.DATA, data.getData());
        if (data.getData().isProfileComplete()) {
            pref.save(Constants.COMPLETE_PROFILE, data.getData().isProfileComplete());
            startActivity(new Intent(AuthenticateActivity.this, Main2Activity.class));
        } else {
            SignUp2Fragment fragment = (SignUp2Fragment) getSupportFragmentManager().findFragmentByTag("SignUp2Fragment");
            if (fragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                        .replace(android.R.id.content, fragment, "SignUp2Fragment").commit();
            } else {
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                        .replace(android.R.id.content, SignUp2Fragment.newInstance("Normal"), "SignUp2Fragment").addToBackStack("SignUp2Fragment").commit();

            }
        }
    }

    @Override
    public void signupError(String failureMessage) {
        new DialogPopup().alertPopup(this, getResources().getString(R.string.dialog_alert), failureMessage, "").show(getSupportFragmentManager(), "IOS_Dialog");
    }

    @Override
    public void signupFailure(String failureMessage) {
        Log.e(TAG, "signupFailure: " + failureMessage);
        new DialogPopup().alertPopup(this, getResources().getString(R.string.dialog_alert), getString(R.string.check_connection), "").show(getSupportFragmentManager(), "IOS_Dialog");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }




  /*  private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Toast.makeText(getActivity(), "Turn on location", Toast.LENGTH_SHORT).show();
            return;
        }

        if(mFusedLocationClient == null){
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        }

        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    // Logic to handle location object
                    android.util.Log.d("Latitude", String.valueOf(location.getLatitude()));
                    android.util.Log.d("Latitude", String.valueOf(location.getLongitude()));
                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                    latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    List<Address> addressList = null;
                    try {
                        addressList = geocoder.getFromLocation(
                                latLng.latitude, latLng.longitude, 1);
                    } catch (Exception ioe) {
                        ioe.printStackTrace();
                    }
                    if (addressList != null && addressList.size() > 0) {
                        Address address = addressList.get(0);
                        Log.e(TAG,"Address data"+ addressList.toString());
                        String country = address.getCountryName();
                        Prefs.with(getApplicationContext()).save(Constants.USER_COUNTRY,country);
                    }
                } else {
                    //   Toast.makeText(getActivity(), "Location not found", Toast.LENGTH_SHORT).show();
                    getLocatoinUpdate();
                }
            }
        });

    }
*/

/*
    public boolean GPSStatus() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void getLocatoinUpdate() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        try {
            if (mFusedLocationClient == null) {
                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            }
            mFusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, null);
        }catch (Exception e){
            e.printStackTrace();
            try{
                FirebaseCrashlytics.getInstance().recordException(e);
            }catch (Exception fe){
                e.printStackTrace();
            }
        }
    }

*/


/*
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e(TAG, "reeeeee"+requestCode + "");
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        //Request location updates:
                        if (GPSStatus()) {
                            try {
                                getCurrentLocation();
                            }catch (Exception e){
                                e.printStackTrace();
                                try{
                                    FirebaseCrashlytics.getInstance().recordException(e);
                                }catch (Exception fe){
                                    fe.printStackTrace();
                                }
                            }
                        } else {
                            // getCurrentLocation();
                            Intent intent1 = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent1);
                        }
                    }

                } else {
                    //  Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                break;
        }
    }

*/

/*

    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);

            } else {

                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

*/


}
