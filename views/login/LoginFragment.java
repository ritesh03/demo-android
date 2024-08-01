package com.maktoday.views.login;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.maktoday.Config.Config;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

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
import com.maktoday.databinding.FragmentLoginBinding;
import com.maktoday.interfaces.UpdateProgressStatus;
import com.maktoday.model.ApiResponse;
import com.maktoday.model.FacebookModel;
import com.maktoday.model.PojoLogin;
import com.maktoday.model.SearchMaidBulkModel;
import com.maktoday.model.SearchMaidModel;
import com.maktoday.utils.Constants;
import com.maktoday.utils.DataVariable;
import com.maktoday.utils.DialogPopup;
import com.maktoday.utils.GeneralFunction;
import com.maktoday.utils.Log;
import com.maktoday.utils.Prefs;
import com.maktoday.views.authenticate.AuthenticateActivity;
import com.maktoday.views.forgotpassword.ForgotPasswordActivity;
import com.maktoday.views.maidbook.MaidBookActivity;
import com.maktoday.views.main.Main2Activity;
import com.maktoday.views.signupstep1.SignUp1Fragment;
import com.maktoday.views.signupstep2.SignUp2Fragment;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.TimeZone;

import static com.maktoday.views.home.HomeFragment.booking_type;

/**
 * Created by cbl81 on 24/10/17.
 */

public class LoginFragment extends Fragment implements LoginContract.View, View.OnClickListener {

    private static final String TAG = "LoginFragment";

    private FragmentLoginBinding binding;
    private String type;
    private LoginContract.Presenter presenter;
    private boolean isClose;
    boolean isPasswordVisible = false;
    private String token = "";


    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            UpdateProgressStatus updateProgressStatus = (UpdateProgressStatus) getActivity()
                    .getSupportFragmentManager().findFragmentByTag("PaymentStateFragment");
            updateProgressStatus.changeProgressStatus(1);
            SearchMaidModel searchMaidModel = getArguments().getParcelable(Constants.SEARCH_MAID_DATA);
            getFragmentManager().beginTransaction().
                    replace(R.id.childframeLayout, SignUp2Fragment.newInstance("Payment", searchMaidModel), "CardListFragment").addToBackStack("CardListFragment").commit();

        }
    };
    private GoogleSignInClient mGoogleSignInClient;
    private int RC_SIGN_IN = 32;

    public static LoginFragment newInstance(String profileOpen) {
        return newInstance(profileOpen, false);
    }

    public static LoginFragment newInstance(String profileOpen, Boolean isClose) {
        return newInstance(profileOpen, isClose, (SearchMaidModel) null);
    }

    public static LoginFragment newInstance(String profileOpen, Boolean isClose, SearchMaidModel searchMaidModel) {
        Bundle args = new Bundle();
        LoginFragment fragment = new LoginFragment();
        args.putString(Constants.PROFILE_OPEN, profileOpen);
        args.putBoolean(Constants.IS_CLOSE, isClose);
        args.putParcelable(Constants.SEARCH_MAID_DATA, searchMaidModel);
        fragment.setArguments(args);
        return fragment;
    }

    public static LoginFragment newInstance(String profileOpen, Boolean isClose, SearchMaidBulkModel searchMaidModel) {
        Bundle args = new Bundle();
        LoginFragment fragment = new LoginFragment();
        args.putString(Constants.PROFILE_OPEN, profileOpen);
        args.putBoolean(Constants.IS_CLOSE, isClose);
        args.putParcelable(Constants.SEARCH_MAID_DATA, searchMaidModel);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isClose = getArguments().getBoolean(Constants.IS_CLOSE);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                mMessageReceiver, new IntentFilter("custom-event-name"));


        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {

                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed"+task.getException().toString());
                            token =  "Fetching FCM registration token failed"+ task.getException().toString();
                           Log.d(TAG, "onComplete: not sucess token"+ token);
                            return;
                        }

                        // Get new FCM registration token
                        token = task.getResult();
                        Prefs.with(getContext()).save(Constants.DEVICE_TOKEN,token);

                       Log.d(TAG, "onComplete: token"+token);

                    }
                });

Log.d(TAG, "init: device token"+ token);

    }//onCreate

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e(TAG, " --------- onViewCreated() ");
       Log.d(TAG, "onViewCreated: StartActivity");
        init();
        setData();
        setListeners();

        Log.e(TAG, " --------- Config.appMode.name() = " + Config.appMode.name());

//        if (Config.appMode.name().equals("TEST")) {
////            // 35. server
////            binding.etEmail.setText(Config.testEmail);
////            binding.etPassword.setText(Config.testPass35);
////        }
////        if (Config.appMode.name().equals("DEV")) {
////            // 54. server
////            binding.etEmail.setText(Config.testEmail);
////            binding.etPassword.setText(Config.testPass54);
////        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
    }

    private void init() {
        presenter = new LoginPresenter();
        presenter.attachView(this);

        if (getArguments() != null) {
            type = getArguments().getString(Constants.PROFILE_OPEN, "");
        }

        if (type.equals("Normal")) {
            binding.btnFacebook.setVisibility(View.GONE);
            binding.tvSignUp.setVisibility(View.GONE);
            binding.tvGuest.setVisibility(View.GONE);
            binding.llSignup.setVisibility(View.VISIBLE);
            binding.tvSignIn.setText(getString(R.string.continue1));
            binding.tvSignIn.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_bg));
            binding.tvSignIn.setTextColor(ContextCompat.getColor(getActivity(), android.R.color.white));
            binding.back.setVisibility(View.VISIBLE);
            binding.tvOR.setVisibility(View.VISIBLE);
         //  binding.topLogo.setVisibility(View.VISIBLE);
            binding.llFacebook.setVisibility(View.VISIBLE);
            binding.tvTitle.setVisibility(View.GONE);
            binding.tvTitle1.setVisibility(View.VISIBLE);
            binding.tvSubTitle.setVisibility(View.VISIBLE);
            binding.divider.setVisibility(View.GONE);

        } else if (type.equals("Payment")) {
            binding.btnFacebook.setVisibility(View.VISIBLE);
            binding.tvSignUp.setVisibility(View.VISIBLE);
            binding.tvGuest.setVisibility(View.VISIBLE);
            binding.llSignup.setVisibility(View.GONE);
         //   binding.topLogo.setVisibility(View.GONE);
            binding.tvSignIn.setText(getString(R.string.signin));
            binding.tvTitle1.setVisibility(View.GONE);
            binding.tvSignIn.setTextColor(ContextCompat.getColor(getActivity(), R.color.app_color));
            binding.tvSignIn.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.rect_border_sky_white));
            binding.back.setVisibility(View.GONE);
            binding.tvOR.setVisibility(View.GONE);
            binding.llFacebook.setVisibility(View.GONE);
            binding.tvTitle.setVisibility(View.VISIBLE);
            binding.tvSubTitle.setVisibility(View.INVISIBLE);
            binding.divider.setVisibility(View.VISIBLE);
        }
        binding.etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        binding.imageViewEye.setImageDrawable(getContext().getDrawable(R.drawable.ic_baseline_visibility_off_24));
    }

    private void setData() {

        PojoLogin data = Prefs.with(getActivity()).getObject(Constants.DATA, PojoLogin.class);
        if (data != null && data.isGuestFlag() && data.isFirstBookingDone()) {
            binding.tvGuest.setVisibility(View.GONE);
        }
    }

    private void setListeners() {
        binding.tvSignIn.setOnClickListener(this);
        binding.tvSignup.setOnClickListener(this);
        binding.tvSignUp.setOnClickListener(this);
        binding.back.setOnClickListener(this);
        binding.tvForgotPassword.setOnClickListener(this);
        binding.tvGuest.setOnClickListener(this);
        binding.btnFacebook.setOnClickListener(this);
        binding.llFacebook.setOnClickListener(this);
        binding.llGoogle.setOnClickListener(this);
        binding.imageViewEye.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Bundle bundle = new Bundle();
        switch (view.getId()) {
            case R.id.btnFacebook:
            case R.id.llFacebook:
                AuthenticateActivity.user_type = "social";
                if (GeneralFunction.isNetworkConnected(getActivity(), binding.parent)) {
                    Log.e(TAG, "trtr");
                    if (type.equals("Payment")) {
                        MaidBookActivity activity = ((MaidBookActivity) getActivity());
                        activity.facebookLogin.setFacebookLoginListener(activity.fbListener);
                        activity.facebookLogin.performLogin();
                    } else {
                        AuthenticateActivity activity = ((AuthenticateActivity) getActivity());
                        activity.facebookLogin.setFacebookLoginListener(activity.fbListener);
                        activity.facebookLogin.performLogin();
                    }
                } else {
                    GeneralFunction.showSnackBar(getActivity(), binding.parent, getString(R.string.check_connection));
                }
                break;
            case R.id.llGoogle:
                AuthenticateActivity.user_type = "social";
                if (GeneralFunction.isNetworkConnected(getActivity(), binding.parent)) {
                    if (AuthenticateActivity.mGoogleSignInClient != null) {
                        AuthenticateActivity.mGoogleSignInClient.signOut()
                                .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                    }
                                });
                    }
                    if (type.equals("Payment")) {
                        MaidBookActivity activity = ((MaidBookActivity) getActivity());
                        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                        startActivityForResult(signInIntent, RC_SIGN_IN);
                       /* activity.facebookLogin.setFacebookLoginListener(activity.fbListener);
                        activity.facebookLogin.performLogin();*/
                    } else {
                        Log.e(TAG, "enter");
                        AuthenticateActivity activity = ((AuthenticateActivity) getActivity());
                        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                        startActivityForResult(signInIntent, RC_SIGN_IN);
                        /*activity.facebookLogin.setFacebookLoginListener(activity.fbListener);
                        activity.facebookLogin.performLogin();*/
                    }
                } else {
                    GeneralFunction.showSnackBar(getActivity(), binding.parent, getString(R.string.check_connection));
                }

                break;
            case R.id.tvSignIn:
                AuthenticateActivity.user_type = "native";
                if (GeneralFunction.isNetworkConnected(getActivity(), binding.tvSignIn)) {
                    if (checkValidation()) {
                        apiLoginData();
                    }
                }
                break;
            case R.id.tvSignup:
                AuthenticateActivity.user_type = "native";
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                        .replace(android.R.id.content, SignUp1Fragment.newInstance("Normal"), "SignUp1Fragment").addToBackStack("SignUp1Fragment").commit();
                break;

            case R.id.tvSignUp:
                AuthenticateActivity.user_type_TEMP = "native";
                AuthenticateActivity.user_type = "native";
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                        .add(R.id.childframeLayout, SignUp1Fragment.newInstance("Guest", false, (SearchMaidModel) getArguments().getParcelable(Constants.SEARCH_MAID_DATA)), "SignUp1Fragment").commit();

                break;
            case R.id.back:
                if (isClose) {
                    DataVariable.hideSoftKeyboard(getActivity());
                    getActivity().finish();
                } else {
                    DataVariable.hideSoftKeyboard(getActivity());
                    getActivity().onBackPressed();
                }
                break;
            case R.id.tvForgotPassword:
                startActivity(new Intent(getActivity(), ForgotPasswordActivity.class));
                break;

            case R.id.imageViewEye:
                String pass = binding.etPassword.getText().toString();
                if (!isPasswordVisible) {
                    binding.etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    binding.imageViewEye.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_visibility_24));
                    isPasswordVisible = true;
                    binding.etPassword.setSelection(pass.length());
                } else {
                    binding.etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    binding.imageViewEye.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_visibility_off_24));
                    isPasswordVisible = false;
                    binding.etPassword.setSelection(pass.length());
                }
                break;
            case R.id.tvGuest:

                AuthenticateActivity.user_type_TEMP = "guest";

                AuthenticateActivity.user_type = "native";
                UpdateProgressStatus updateProgressStatus = (UpdateProgressStatus) getActivity()
                        .getSupportFragmentManager().findFragmentByTag("PaymentStateFragment");
                ((MaidBookActivity) getActivity()).step = 1;
                updateProgressStatus.changeProgressStatus(1);

                if (booking_type == 3) {
                    SearchMaidBulkModel searchMaidModel = getArguments().getParcelable(Constants.SEARCH_MAID_DATA);
                    getFragmentManager().beginTransaction().
                            replace(R.id.childframeLayout, SignUp2Fragment.newInstance("Guest", searchMaidModel),
                                    "SignUp2Fragment").addToBackStack("SignUp2Fragment").commit();

                } else {
                    SearchMaidModel searchMaidModel = getArguments().getParcelable(Constants.SEARCH_MAID_DATA);
                    getFragmentManager().beginTransaction().
                            replace(R.id.childframeLayout, SignUp2Fragment.newInstance("Guest", searchMaidModel),
                                    "SignUp2Fragment").addToBackStack("SignUp2Fragment").commit();

                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "check1" + requestCode);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {

            Log.e("check1", "InSide");
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);

        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            //Toast.makeText(getActivity(), "yes", Toast.LENGTH_SHORT).show();
            Log.e("data", "" + new Gson().toJson(account));
            Log.e("data", "" + account.getDisplayName());
            Log.e("data", "" + account.getFamilyName());
            Log.e("data", "" + account.getGivenName());
            Log.e("data", "" + account.getAccount().name);
            StringTokenizer tokens = new StringTokenizer(account.getDisplayName(), " ");
            String firstname = tokens.nextToken();// this will contain "Fruit"
            String lastname = tokens.nextToken();
            FacebookModel facebookModel = new FacebookModel(Constants.UNIQUE_APP_KEY,
                    account.getId(), account.getDisplayName(), account.getEmail(), null, token, "ANDROID","GOOGLE",firstname,lastname);


            Log.e(TAG, "fb params"+new Gson().toJson(facebookModel));
            presenter.apiFacebook(facebookModel);
            // Signed in successfully, show authenticated UI.
            //   updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e(TAG,"sd"+ "signInResult:failed code=" + e.getLocalizedMessage());
            // updateUI(null);
        }
    }

    private void apiLoginData() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("uniquieAppKey", Constants.UNIQUE_APP_KEY);
        hashMap.put("email", binding.etEmail.getText().toString().trim());
        hashMap.put("password", binding.etPassword.getText().toString().trim());
        hashMap.put("timeZone", TimeZone.getDefault().getID());
       // hashMap.put("deviceToken", FirebaseInstanceId.getInstance().getToken());
        hashMap.put("deviceToken",token);
        hashMap.put("deviceType", "ANDROID");
       // hashMap.put("loginType", "GOOGLE");

        Log.e(TAG, "params"+hashMap + "");
        presenter.apiLogin(hashMap);

    }

    @Override
    public void setLoading(boolean isLoading) {
        if (isLoading)
            GeneralFunction.showProgress(getActivity());
        else
            GeneralFunction.dismissProgress();
    }

    @Override
    public void sessionExpired() {
        GeneralFunction.isUserBlocked(getActivity());
    }

    @Override
    public void signupSuccess(ApiResponse<PojoLogin> data) {

        if (type.equals("Payment")) {
            Log.d(TAG, "signupSuccess: if (type.equals(\"Payment\")) {");
            MaidBookActivity activity = ((MaidBookActivity) getActivity());

            activity.FBLoginSuccess(data.getData());
            // activity.facebookLogin.performLogin();
        } else {
           Log.d(TAG, "signupSuccess:  } else { payment");
            Log.e(TAG, "dataa"+new Gson().toJson(data));
            Prefs pref = Prefs.with(getActivity());
            pref.save(Constants.ACCESS_TOKEN, "bearer " + data.getData().getAccessToken());
            Prefs.with(getActivity()).save(Constants.EMAIL_ID, data.getData().email);
            pref.save(Constants.USER_IDs,data.getData()._id);
            pref.save(Constants.DATA, data.getData());

            if (!data.getData().profileComplete) {
              Log.d(TAG, "signupSuccess: if (!data.getData().profileComplete) {");
                // pref.save(Constants.DATA, data.getData());
                Prefs prefs = Prefs.with(getActivity());
                prefs.save(Constants.ACCESS_TOKEN, "bearer " + data.getData().getAccessToken());
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                        .replace(android.R.id.content, SignUp2Fragment.newInstance("LoginProfileIncomplete"), "SignUp2Fragment").addToBackStack("SignUp2Fragment").commit();

            }
            else {
              Log.d(TAG, "signupSuccess: 3 } else {");
                if (data.getData().isVerified) {
                    Prefs prefs = Prefs.with(getActivity());
                    prefs.save(Constants.ACCESS_TOKEN, "bearer " + data.getData().getAccessToken());
                    pref.save(Constants.USER_IDs,data.getData()._id);
                    prefs.save(Constants.COMPLETE_PROFILE, true);
                    prefs.save(Constants.LOGIN_COUNTRY, data.getData().getCountryENCode());
                    prefs.save(Constants.DATA, data.getData());
                    Log.e(TAG, "type :==: "+type);
                    if (type.equals("Normal")) {
                       Log.d(TAG, "signupSuccess: if (type.equals(\"Normal\")) {");
                        getActivity().finishAffinity();
                        GeneralFunction.showSnackBar(getContext(), binding.parent, getString(R.string.successfully_registered));
                        startActivity(new Intent(getActivity(), Main2Activity.class));
                    } else if (type.equals("Payment")) {
                        Log.d(TAG, "signupSuccess: } else if (type.equals(\"Payment\")) {");
                        UpdateProgressStatus updateProgressStatus = (UpdateProgressStatus) getActivity()
                                .getSupportFragmentManager().findFragmentByTag("PaymentStateFragment");
                        updateProgressStatus.changeProgressStatus(1);
                        SearchMaidModel searchMaidModel = getArguments().getParcelable(Constants.SEARCH_MAID_DATA);
                        getFragmentManager().beginTransaction().
                                replace(R.id.childframeLayout, SignUp2Fragment.newInstance("Payment", searchMaidModel), "CardListFragment").addToBackStack("CardListFragment").commit();

                    }
                } else {
                    Log.d(TAG, "signupSuccess: 5  } else {");
                    getActivity().finishAffinity();
                    Toast.makeText(getActivity(), getString(R.string.msg_email_not_verified), Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getActivity(), AuthenticateActivity.class));
                }
            }

        }
    }

    @Override
    public void signupSuccesss(ApiResponse<PojoLogin> data) {
        Prefs pref = Prefs.with(getActivity());
        pref.save(Constants.ACCESS_TOKEN, "bearer " + data.getData().getAccessToken());
        Prefs.with(getActivity()).save(Constants.EMAIL_ID, data.getData().email);
        pref.save(Constants.USER_IDs,data.getData()._id);
        pref.save(Constants.DATA, data.getData());

        if (data.getData().isProfileComplete()) {
            Log.d(TAG, "signupSuccesss:   if (data.getData().isProfileComplete()) {");

            pref.save(Constants.COMPLETE_PROFILE, true);

            if (type.equals("Payment")) {
                Log.d(TAG, "signupSuccesss:  if (type.equals(\"Payment\")) {");

                UpdateProgressStatus updateProgressStatus = (UpdateProgressStatus) getActivity()
                        .getSupportFragmentManager().findFragmentByTag("PaymentStateFragment");
                updateProgressStatus.changeProgressStatus(1);
                SearchMaidModel searchMaidModel = getArguments().getParcelable(Constants.SEARCH_MAID_DATA);
                getFragmentManager().beginTransaction().
                        replace(R.id.childframeLayout, SignUp2Fragment.newInstance("Payment", searchMaidModel), "CardListFragment").addToBackStack("CardListFragment").commit();

            } else {

                startActivity(new Intent(getActivity(), Main2Activity.class));
            }
        } else {
            SignUp2Fragment fragment = (SignUp2Fragment) getActivity().getSupportFragmentManager().findFragmentByTag("SignUp2Fragment");
            if (fragment != null) {
                Log.d(TAG, "signupSuccesss:  if (fragment != null) {");
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                        .replace(android.R.id.content, fragment, "SignUp2Fragment").commit();

            } else {
                Log.d(TAG, "signupSuccesss:   } else {");
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                        .replace(android.R.id.content, SignUp2Fragment.newInstance("Normal"), "SignUp2Fragment").addToBackStack("SignUp2Fragment").commit();

            }
        }
    }

    @Override
    public void signupError(String errorMessage) {
        new DialogPopup().alertPopup(getActivity(), getResources().getString(R.string.dialog_alert), errorMessage, "").show(requireActivity().getSupportFragmentManager(), "ios_dialog");
    }

    @Override
    public void signupFailure(String failureMessage) {
       Log.e(TAG, "signupFailure: "+failureMessage);
        new DialogPopup().alertPopup(getActivity(), getResources().getString(R.string.dialog_alert), getString(R.string.check_connection), "").show(requireActivity().getSupportFragmentManager(), "ios_dialog");
    }


    @Override
    public void onDetach() {
        super.onDetach();
        presenter.detachView();
    }

    public boolean checkValidation() {
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        if (email.isEmpty()) {
            GeneralFunction.showSnackBar(getActivity(), binding.parent, getString(R.string.email_empty_validation));
            binding.etEmail.requestFocus();
            return false;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            GeneralFunction.showSnackBar(getActivity(), binding.parent, getString(R.string.email_valid_validation));
            binding.etEmail.requestFocus();
            return false;
        }
        else if (password.isEmpty()) {
            GeneralFunction.showSnackBar(getActivity(), binding.parent, getString(R.string.password_empty_validation));
            binding.etPassword.requestFocus();

            return false;
        }
        else if (password.length() < 6 || password.length() > 16) {
            GeneralFunction.showSnackBar(getActivity(), binding.parent, getString(R.string.password_valid_validation));
            binding.etPassword.requestFocus();
            return false;
        }
        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(
                mMessageReceiver);
    }
}


