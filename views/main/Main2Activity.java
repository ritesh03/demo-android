package com.maktoday.views.main;

import static com.maktoday.utils.AppGlobal.sAnalytics;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.maktoday.BuildConfig;
import com.maktoday.R;
import com.maktoday.model.ApiResponse;
import com.maktoday.model.PojoLogin;
import com.maktoday.utils.BaseActivity;
import com.maktoday.utils.Constants;
import com.maktoday.utils.DialogPopup;
import com.maktoday.utils.GeneralFunction;
import com.maktoday.utils.Log;
import com.maktoday.utils.Prefs;
import com.maktoday.utils.UpdateChecker;
import com.maktoday.utils.dialog.IOSAlertDialog;
import com.maktoday.views.AllService.ServiceFragment;
import com.maktoday.views.authenticate.AuthenticateActivity;
import com.maktoday.views.booking.BookingFragment;
import com.maktoday.views.chat.ChatActivity;
import com.maktoday.views.chatlist.ChatFragment;
import com.maktoday.views.faq.FAQFragment;
import com.maktoday.views.favourite.FavouriteFragment;
import com.maktoday.views.issue.IssueFragment;
import com.maktoday.views.maidprofile.MaidProfileFragment;
import com.maktoday.views.notification.NotificationActivity;
import com.maktoday.views.privacyPolicy.PrivacyPolicyActivity;
import com.maktoday.views.ratingdialog.RatingDialogFragment;
import com.maktoday.views.setting.IntroFragment;
import com.maktoday.views.setting.SettingFragment;
import com.maktoday.views.terms.TermsActivity;
import com.maktoday.views.updateprofile.UpdateProfileActivity;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import java.util.List;
import java.util.Objects;

//import me.leolin.shortcutbadger.ShortcutBadger;

public class Main2Activity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, Main2Contract.View, UpdateChecker.Listener{

    public static FrameLayout redCircle;
    public static TextView countTextView;
    private static final String TAG = "Main2Activity";
    // tags used to attach the fragments
    private static final String TAG_HOME = "home";
    private static final String TAG_BOOKING = "booking";
    private static final String TAG_FAVOURITE = "favourite";
    private  static final String TAG_CHAT = "chat";
    private static final String TAG_ISSUE = "issue";
    private static final String TAG_CONTACTUS = "contactus";
    private static final String TAG_SETTINGS = "settings";
    private static final String TAG_FAQ = "faq";
    private static final String TAG_HELP = "help";
    private static final String TAG_LOGOUT = "logout";
    public static String CURRENT_TAG = TAG_HOME;
    private View navHeader;
    private TextView v_txt, tvVersion, tvName, tvEmail, tvViewProfile, tvTitle;
    private ImageView ivBackground;
    private int navItemIndex;
    private DrawerLayout drawer;
    private Handler mHandler;
    private NavigationView navigationView;
    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;
    private Toolbar toolbar;
    private boolean shouldLoadHomeFragOnBackPress = true;
 //   private AlertDialog dialog;
    private Main2Contract.Presenter presenter;
    private ImageView ivMAK,ivNoti;
    public static Menu menu_temp;
    public static ImageView ivBack;
    private UpdateChecker updateChecker;



    ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {

                @Override
                public void onActivityResult(Boolean result) {
                    android.util.Log.e(TAG, "onActivityResult: notification Permisstion result :-- "+ result );
                    if(!result){
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            askNotificationPermission();
                        }
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Log.d(TAG, "onCreate: StartActivity");
        updateChecker = new UpdateChecker();
        updateChecker.setListener(this);
      //  askLocationPermission();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            askNotificationPermission();
        }
        init();
        Log.d(TAG, "onCreate: push message type "+ getIntent().getStringExtra(Constants.TYPE));
        if(getIntent().hasExtra(Constants.TYPE)) {
            if(getIntent().getStringExtra(Constants.TYPE)!= null) {
                pushNotification();
            }
        }
        setData();

        Bundle bundle = new Bundle();
        try {
            bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, Integer.parseInt(Prefs.with(this).getString(Constants.USER_ID, "-1")));
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, Prefs.with(this).getString(Constants.FULL_NAME, "full name"));
        }catch (Exception e ){
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, String.valueOf(Prefs.with(this).getString(Constants.USER_ID, "-1")));
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, Prefs.with(this).getString(Constants.FULL_NAME, "full name"));
            Log.d(TAG, "onCreate: exceiption Bundle bundle = new Bundle();" +e.getLocalizedMessage());
        }
        //Logs an app event.
        sAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        setListener();
        ivNoti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Main2Activity.this, NotificationActivity.class));
            }
        });
    }
    //onCreate
    /*
     * Getting application version
     * */
    private void getVersionName() {
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = "Version " + pInfo.versionName;
            tvVersion.setText(version);
            v_txt.setText(version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void init() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mHandler = new Handler();
        drawer = findViewById(R.id.drawer_layout);
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);
        navigationView = findViewById(R.id.nav_view);
        navHeader = navigationView.getHeaderView(0);
        tvName = navHeader.findViewById(R.id.tvName);
        v_txt = findViewById(R.id.v_txt);
        tvVersion = navHeader.findViewById(R.id.tvVersion);
        getVersionName();
        tvEmail = navHeader.findViewById(R.id.tvEmail);
        tvViewProfile = navHeader.findViewById(R.id.tvViewProfile);
        ivBackground = navHeader.findViewById(R.id.ivBackground);
        navItemIndex = 0;
        CURRENT_TAG = TAG_HOME;
        presenter = new Main2Presenter();
        presenter.attachView(this);
        Prefs prefs = Prefs.with(this);
        String accessToken = prefs.getString(Constants.ACCESS_TOKEN, "");
        if (accessToken == null || accessToken.isEmpty()) {
            prefs.save(Constants.ACCESS_TOKEN, "bearer");
        }
    }
    private void setData() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        loadNavHeader();
        loadHomeFragment();
        manageMenuItem();
//        dialog.dismiss();
    }
    private void setListener() {
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void pushNotification() {
        Log.e("noti home", "yes");
        Log.d(TAG, "pushNotification: intent::--  "+getIntent().getStringExtra(Constants.FCM_TYPE));
        if (getIntent().getBooleanExtra(Constants.Notification_DATA, false)) {
            if (getIntent() != null && getIntent().getStringExtra(Constants.TYPE) != null
                    && getIntent().getStringExtra(Constants.TYPE).equals("SERVICE_COMPLETE")) {
                Log.e("noti home if", "yes");
                Log.e("noti home if", getIntent().getStringExtra(Constants.REQ_ID));
                Log.e("noti home if", getIntent().getStringExtra(Constants.MAID_NAME));
                Log.e("noti home if", getIntent().getStringExtra(Constants.MAID_PIC));
                RatingDialogFragment.newInstance(getIntent().getStringExtra(Constants.REQ_ID),
                        getIntent().getStringExtra(Constants.MAID_NAME),
                        getIntent().getStringExtra(Constants.MAID_PIC)).show(getSupportFragmentManager(), "RatingDialog");
            } else if (Objects.requireNonNull(getIntent()).getStringExtra(Constants.TYPE).equals("MESSAGE")) {
                Intent intent = new Intent(Main2Activity.this, ChatActivity.class);
                intent.putExtra(Constants.USER_ID, getIntent().getStringExtra(Constants.RECEIVER_ID));
                intent.putExtra(Constants.SERVICE_ID, getIntent().getStringExtra(Constants.SERVICE_ID));
                intent.putExtra(Constants.NAME, getIntent().getStringExtra(Constants.FULL_NAME));
                navItemIndex = 3;
                CURRENT_TAG = TAG_CHAT;
                startActivity(intent);
            } else if (getIntent().getStringExtra(Constants.TYPE).equals("PLAY_STORE") || getIntent().getStringExtra(Constants.TYPE).equalsIgnoreCase("Feedback")) {
                String market_uri = "https://play.google.com/store/apps/details?id=com.maktoday&hl=en";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(market_uri));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }else if(getIntent().getStringExtra(Constants.TYPE).equals("LINK")){
                String link = getIntent().getStringExtra(Constants.NOTI_LINK);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(link));
                startActivity(i);
            }
            else {
                startActivity(new Intent(Main2Activity.this, NotificationActivity.class));
            }
        }
    }

    private void manageMenuItem() {
        Menu menu = navigationView.getMenu();
        MenuItem logoutItem = menu.findItem(R.id.nav_logout);
        menu.findItem(R.id.nav_home).setChecked(true);
        if (!Prefs.with(Main2Activity.this).getBoolean(Constants.COMPLETE_PROFILE, false)) {
            logoutItem.setVisible(false);
        } else {
            logoutItem.setVisible(true);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        }
        Log.e("qwqw", navItemIndex + "");

        MaidProfileFragment maidProfileFragment = (MaidProfileFragment) getSupportFragmentManager().findFragmentByTag("MaidProfileFragment");

        //maid profile back key fix
        if(maidProfileFragment != null && maidProfileFragment.isVisible()) {
            shouldLoadHomeFragOnBackPress = false;
        }else {
            shouldLoadHomeFragOnBackPress = true;
        }
        //end maid profile back key fix

        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home

            if (navItemIndex != 0) {
//                if (navItemIndex == 1 || navItemIndex == 2) {
//                    finishAffinity();
//                    startActivity(new Intent(Main2Activity.this, Main2Activity.class));

                //   } else {
                navigationView.getMenu().getItem(navItemIndex).setChecked(false);
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
                navigationView.getMenu().getItem(navItemIndex).setChecked(true);
                navigationView.getMenu().getItem(0).setChecked(true);
                loadHomeFragment();
                //       }
//                finishAffinity();
//                startActivity(new Intent(Main2Activity.this, Main2Activity.class));
                return;
            }
        }
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        menu.getItem(0).setVisible(false);
        //  menu.getItem(0).setIcon(R.drawable.com_facebook_send_button_icon);

        menu_temp = menu;
        //  action_notification
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_menu:
                if (!drawer.isDrawerOpen(GravityCompat.END)) {
                    drawer.openDrawer(GravityCompat.END);
                }
                return true;
            case R.id.action_notification:
                startActivity(new Intent(Main2Activity.this, NotificationActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        menu.getItem(1).setVisible(true);
        if (navItemIndex == 0) {
            menu.getItem(0).setVisible(true);
        } else {
            menu.getItem(0).setVisible(true);
        }
        final MenuItem alertMenuItem = menu.findItem(R.id.action_notification);


        FrameLayout rootView = (FrameLayout) menu.getItem(0).getActionView();

        redCircle = (FrameLayout) rootView.findViewById(R.id.view_alert_red_circle);
        countTextView = (TextView) rootView.findViewById(R.id.view_alert_count_textview);
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(alertMenuItem);
            }
        });


        return super.onPrepareOptionsMenu(menu);
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        switch (item.getItemId()) {
            case R.id.nav_home:
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
//                finishAffinity();
//                startActivity(new Intent(Main2Activity.this, Main2Activity.class));
                break;
            case R.id.nav_booking:
                navItemIndex = 1;
                CURRENT_TAG = TAG_BOOKING;
                break;
            case R.id.nav_favourite:
                navItemIndex = 2;
                CURRENT_TAG = TAG_FAVOURITE;
                break;
            case R.id.nav_chat:
                navItemIndex = 3;
                CURRENT_TAG = TAG_CHAT;
                break;
            case R.id.nav_contact_us:
                navItemIndex = 4;
                CURRENT_TAG = TAG_CONTACTUS;
                break;
         /*   case R.id.nav_contact_us:
                navItemIndex = 5;
                CURRENT_TAG = TAG_CONTACTUS;
                break;*/
            case R.id.nav_setting:
                navItemIndex = 5;
                CURRENT_TAG = TAG_SETTINGS;
                break;
            case R.id.nav_faq:
                navItemIndex = 6;
                CURRENT_TAG = TAG_FAQ;
                break;
            case R.id.nav_terms:
                CURRENT_TAG = TAG_CONTACTUS;
                startActivity(new Intent(this, TermsActivity.class));
                return true;
            case R.id.nav_privacy_policy:
                CURRENT_TAG = TAG_CONTACTUS;
                startActivity(new Intent(this, PrivacyPolicyActivity.class));
                return true;
            case R.id.nav_help:
                navItemIndex = 7;
                CURRENT_TAG = TAG_HELP;
                break;
            case R.id.nav_logout:
                navItemIndex = 0;
                ivNoti.setVisibility(View.GONE);
                CURRENT_TAG = TAG_LOGOUT;
                break;
        }
        if (item.isChecked()) {
            item.setChecked(false);
        } else {
            item.setChecked(true);
        }
        item.setChecked(true);
        loadHomeFragment();
        return true;
    }

    private void loadHomeFragment() {
        if (CURRENT_TAG != TAG_LOGOUT) {
            setToolbarTitle();
        }
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();
            if (CURRENT_TAG.equals(TAG_HOME) &&
                    getSupportFragmentManager().findFragmentByTag("ConfirmBookFragment") == null &&
                    getSupportFragmentManager().findFragmentByTag("MaidProfileFragment") == null &&
                    getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack();
            }
            return;
        }

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        if (!Objects.equals(CURRENT_TAG, TAG_LOGOUT)) { //  Current Target != TAG Logout
            Runnable mPendingRunnable = new Runnable() {
                @Override
                public void run() {
                    // update the main content by replacing fragments
                    if (CURRENT_TAG.equals(TAG_HOME)) {
                        try {
                            Fragment fragment = getHomeFragment();
                            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                                    android.R.anim.fade_out);
                            fragmentTransaction.replace(R.id.frameLayout, fragment, CURRENT_TAG);
                            fragmentTransaction.commitAllowingStateLoss();
                        }catch (Exception e){
                            e.printStackTrace();
                            try{//--- Record Detailed in FIrebase crashlitics --
                                FirebaseCrashlytics.getInstance().recordException(new Throwable("Exception when CURRENT_TAG equals TAG_HOME ex: ",e));
                            }catch (Exception ex){
                                ex.printStackTrace();
                            }
                        }
                    } else {
                        try {
                            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                                getSupportFragmentManager().popBackStack();
                            }
                            Fragment fragment = getHomeFragment();
                            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.add(R.id.frameLayout, fragment, CURRENT_TAG);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commitAllowingStateLoss();
                        }catch (Exception e){
                            e.printStackTrace();
                            try{//--- Record Detailed exception in firebase crashlitics------
                                FirebaseCrashlytics.getInstance().recordException(new Throwable("Exception when CURRENT_TAG  NOT equals TAG_HOME ex: ",e));

                            }catch (Exception ex ){
                                ex.printStackTrace();
                            }
                        }
                    }
                }
            };
            mHandler.postDelayed(mPendingRunnable, 220);
        } else {
            logoutPopup();
        }
        //change toolbar color
        toolbar.setBackgroundColor(ContextCompat.getColor(Main2Activity.this, R.color.appBlueColor));

        //Closing drawer on item click
        drawer.closeDrawers();
        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    private void loadNavHeader() {

        //String accessToken = Prefs.with(Main2Activity.this).getString(Constants.ACCESS_TOKEN, "");

        if (!Prefs.with(Main2Activity.this).getBoolean(Constants.COMPLETE_PROFILE, false)) {
            navHeader.findViewById(R.id.llAuthenticate).setVisibility(View.VISIBLE);
            navHeader.findViewById(R.id.llProfileData).setVisibility(View.GONE);
            navHeader.findViewById(R.id.tvSignIn).setOnClickListener(this);
            navHeader.findViewById(R.id.tvSignUp).setOnClickListener(this);
        } else {
            navHeader.findViewById(R.id.llAuthenticate).setVisibility(View.GONE);
            navHeader.findViewById(R.id.llProfileData).setVisibility(View.VISIBLE);
          /* if(profileData!=null && profileData.isProfileComplete()){
               tvViewProfile.setVisibility(View.VISIBLE);
           }else
           tvViewProfile.setVisibility(View.GONE);*/

            PojoLogin profileData = Prefs.with(Main2Activity.this).getObject(Constants.DATA, PojoLogin.class);
            if (profileData != null) {
                tvName.setText(profileData.firstName+" "+profileData.lastName);
                tvEmail.setText(profileData.getEmail());
                tvViewProfile.setOnClickListener(this);
            }
        }
    }



    private void setToolbarTitle() {
        tvTitle = findViewById(R.id.tvTitle);
        ivMAK = findViewById(R.id.ivMAK);
        ivNoti=findViewById(R.id.ivNoti);
        ivBack=findViewById(R.id.ivBack);
        if (navItemIndex == 0) {
            tvTitle.setVisibility(View.GONE);
            ivMAK.setVisibility(View.VISIBLE);
            ivNoti.setVisibility(View.GONE);
        } else {
            tvTitle.setVisibility(View.VISIBLE);
            ivMAK.setVisibility(View.GONE);
            ivNoti.setVisibility(View.GONE);
        }

        tvTitle.setText(activityTitles[navItemIndex]);

    }

    public Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                // return new HomeFragment();
                return new ServiceFragment();

            case 1:

                return new BookingFragment();

            case 2:

                return new FavouriteFragment();

            case 3:
                return new ChatFragment();

            case 4:
                return new IssueFragment();

           /* case 5:
                return new ContactFragment();*/

            case 5:
                return new SettingFragment();

            case 6:

                return new FAQFragment();
            case 7:
                return new IntroFragment();

            default:
                // return new HomeFragment();
                return new ServiceFragment();
        }
    }

    public void logoutPopup() {

        IOSAlertDialog dialog =  IOSAlertDialog.newInstance(
                Main2Activity.this,
                getString(R.string.signout),
              getString(R.string.logout_sure),
                getString(R.string.signout),
                getString(R.string.cancel1),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int idd) {
                        if (GeneralFunction.isNetworkConnected(Main2Activity.this, drawer)) {
                            dialog.dismiss();
                            presenter.apilogout();
                        }
                    }
                },
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int idd) {
                        CURRENT_TAG = TAG_HOME;
                        dialog.dismiss();
                    }
                },
                ContextCompat.getColor(Main2Activity.this, R.color.coral),
                ContextCompat.getColor(Main2Activity.this, R.color.app_color),
                true

        );
        dialog.show(getSupportFragmentManager(), "ios_dialog");

  /*      AlertDialog   dialog = new AlertDialog.Builder(Main2Activity.this)
                .setMessage(getResources().getString(R.string.logout_sure))
                .setCancelable(false)
                .setNegativeButton(R.string.cancel1, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int idd) {
                        CURRENT_TAG = TAG_HOME;
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(R.string.signout,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int idd) {
                                if (GeneralFunction.isNetworkConnected(Main2Activity.this, drawer)) {
                                    dialog.dismiss();
                                    presenter.apilogout();
                                }
                            }
                        }).show();
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(Main2Activity.this, R.color.coral));
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setAllCaps(false);
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(Main2Activity.this, R.color.appColor));
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setAllCaps(false);
*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e("ewe", "" + requestCode);
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateChecker.checkForUpdate();

        /*check for release */



        setData();
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.tvViewProfile:
                startActivityForResult(new Intent(Main2Activity.this, UpdateProfileActivity.class), 2);
                drawer.closeDrawers();
                break;
            case R.id.tvSignIn:
                intent = new Intent(Main2Activity.this, AuthenticateActivity.class);
                intent.putExtra(Constants.AUTHENTICATE_TYPE, "SignIn");
                startActivity(intent);
                break;
            case R.id.tvSignUp:
                intent = new Intent(Main2Activity.this, AuthenticateActivity.class);
                intent.putExtra(Constants.AUTHENTICATE_TYPE, "SignUp");
                startActivity(intent);
                break;
        }
    }
    @Override
    public void setLoading(boolean isLoading) {
        if (isLoading)
            GeneralFunction.showProgress(Main2Activity.this);
        else
            GeneralFunction.dismissProgress();
    }

    @Override
    public void sessionExpired() {
        // GeneralFunction.isUserBlocked(Main2Activity.this);
        Log.d(TAG, "SessonExpire");
    }

    @Override
    public void logoutSuccess(ApiResponse data) {
        String language = Prefs.with(this).getString(Constants.LANGUAGE_CODE, "en");
        String device_token = Prefs.with(this).getString(Constants.DEVICE_TOKEN, "");
        Prefs.with(Main2Activity.this).removeAll();
        Prefs.with(this).save(Constants.LANGUAGE_CODE, language);
        //  ShortcutBadger.applyCount(this, 0);
        //  Prefs.with(this).save(Constants.DEVICE_TOKEN, device_token);
        finishAffinity();
        if (AuthenticateActivity.mGoogleSignInClient != null) {
            AuthenticateActivity.mGoogleSignInClient.signOut()
                    .addOnCompleteListener(Main2Activity.this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    });
        }
        Prefs.with(Main2Activity.this).save(Constants.LANGUAGE_Click_Status, "yes");
        startActivity(new Intent(Main2Activity.this, AuthenticateActivity.class));
        // dialog.cancel();
    }

    @Override
    public void logoutError(String errorMessage) {
        new DialogPopup().alertPopup(Main2Activity.this, getResources().getString(R.string.dialog_alert), errorMessage, "Logout").show(getSupportFragmentManager(), "ios_dialog");

    }

    @Override
    public void logoutFailure(String failureMessage) {
        Log.e(TAG, "logoutFailure: "+failureMessage);
        new DialogPopup().alertPopup(Main2Activity.this, getResources().getString(R.string.dialog_alert), getString(R.string.check_connection), "").show(getSupportFragmentManager(), "ios_dialog");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if (requestCode == 2) {
            loadNavHeader();
        }
    }

    @Override
    public void appUpdateStatusReceived(int status) {
        updateChecker.checkAppUpdateStatus(this, status);
    }

    private void askNotificationPermission() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

                if (ContextCompat.checkSelfPermission(
                        this, Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED) {
                    android.util.Log.e(TAG, "onCreate: PERMISSION GRANTED");
                } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {


                    IOSAlertDialog dialog =  IOSAlertDialog.newInstance(
                      Main2Activity.this,
                      null,
                            getString(R.string.notification_permission_description),
                            getString(R.string.str_allow),
                            getString(R.string.cancel1),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    Uri uri = Uri.fromParts("package", "com.maktoday", null);
                                    intent.setData(uri);
                                    startActivity(intent);
                                }
                            },
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            },
                            ContextCompat.getColor(Main2Activity.this,R.color.app_color),
                            ContextCompat.getColor(Main2Activity.this,R.color.coral),
                            true

                    );

                    dialog.show(getSupportFragmentManager(), "ios_dialog");

              /*      final AlertDialog.Builder builder = new AlertDialog.Builder(Main2Activity.this);
                    builder.setTitle(getString(R.string.notification_permission_title));
                    builder.setMessage(getString(R.string.notification_permission_description));
                    builder.setPositiveButton(getString(R.string.str_allow), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            Uri uri = Uri.fromParts("package", "com.maktoday", null);
                            intent.setData(uri);
                            startActivity(intent);
                        }
                    });
                    builder.setNegativeButton(getString(R.string.cancel1), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.setCancelable(true);
                    AlertDialog dialog = builder.create();
                    dialog.show();*/


                } else {
                    requestPermissionLauncher.launch(
                            Manifest.permission.POST_NOTIFICATIONS
                    );
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            try {
                FirebaseCrashlytics.getInstance().recordException(e);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

}