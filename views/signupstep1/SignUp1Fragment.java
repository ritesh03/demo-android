package com.maktoday.views.signupstep1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.maktoday.Config.Config;
import com.maktoday.R;
import com.maktoday.databinding.FragmentSignup1Binding;
import com.maktoday.model.ApiResponse;
import com.maktoday.model.FacebookModel;
import com.maktoday.model.PojoLogin;
import com.maktoday.model.SearchMaidModel;
import com.maktoday.utils.BaseFragment;
import com.maktoday.utils.Constants;
import com.maktoday.utils.GeneralFunction;
import com.maktoday.utils.Log;
import com.maktoday.utils.Prefs;
import com.maktoday.utils.TextUtils;
import com.maktoday.views.Otpverification.OtpVerifyActivity;
import com.maktoday.views.authenticate.AuthenticateActivity;
import com.maktoday.views.maidbook.MaidBookActivity;
import com.maktoday.views.main.Main2Activity;
import com.maktoday.views.privacyPolicy.PrivacyPolicyActivity;
import com.maktoday.views.signupstep2.SignUp2Fragment;
import com.maktoday.views.terms.TermsActivity;

import java.util.HashMap;
import java.util.StringTokenizer;

import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;

/**
 * Created by cbl81 on 25/10/17.
 */

public class SignUp1Fragment extends BaseFragment implements SignUp1Contract.View, View.OnClickListener {
    private FragmentSignup1Binding binding;
    private String type;
    private static final String TAG = "SignUp1Fragment";
    private SignUp1Contract.Presenter presenter;
    private boolean isClose;
    boolean isPasswordVisible = false;
    FacebookModel facebookModel;
    PhoneNumberUtil phoneNumberUtil;
   FirebaseAuth firebaseAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private int RC_SIGN_IN = 32;
    private String token = "";
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Toast.makeText(getActivity(), "Sign up Successfully", Toast.LENGTH_SHORT).show();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                    .replace(R.id.childframeLayout, SignUp2Fragment.newInstance(type, (SearchMaidModel) getArguments().getParcelable(Constants.SEARCH_MAID_DATA)), "SignUp2Fragment").addToBackStack("SignUp2Fragment").commit();

        }
    };

    public static SignUp1Fragment newInstance(String profileOpen) {
        return newInstance(profileOpen, false);
    }

    public static SignUp1Fragment newInstance(String profileOpen, boolean isClose) {
        return newInstance(profileOpen, isClose, null);
    }
    public static SignUp1Fragment newInstance(String profileOpen, FacebookModel facebookModel) {
        SignUp1Fragment fragment = new SignUp1Fragment();
        Bundle args = new Bundle();
        args.putString(Constants.PROFILE_OPEN, profileOpen);
        args.putBoolean(Constants.IS_CLOSE,false);
        args.putParcelable(Constants.LOGIN_TYPE_DATA, facebookModel);
        fragment.setArguments(args);
        return fragment;


    }

    public static SignUp1Fragment newInstance(String profileOpen, boolean isClose, SearchMaidModel searchMaidModel) {
        SignUp1Fragment fragment = new SignUp1Fragment();
        Bundle args = new Bundle();
        args.putString(Constants.PROFILE_OPEN, profileOpen);
        args.putBoolean(Constants.IS_CLOSE, isClose);
        args.putParcelable(Constants.SEARCH_MAID_DATA, searchMaidModel);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        Config.TRANSACTION_ID = "";
        Config.TAP_ID = "";
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isClose = getArguments().getBoolean(Constants.IS_CLOSE);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                mMessageReceiver, new IntentFilter("custom-event-name"));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSignup1Binding.inflate(inflater, container, false);
        //set variables in Binding
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: StartActivity");
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            android.util.Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            token =    "Fetching FCM registration token failed"+ task.getException().toString();
                            return;
                        }

                        // Get new FCM registration token
                        token = task.getResult();

                    }
                });

        android.util.Log.d(TAG, "init: device token"+ token);
        init();
        setData();
        setListeners();
        initTermsAndPolicyView();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
    }


    private void init() {
        presenter = new SignUp1Presenter();
        presenter.attachView(this);
        firebaseAuth= FirebaseAuth.getInstance();
        phoneNumberUtil = PhoneNumberUtil.createInstance(requireContext());

        if (getArguments() != null) {
            type = getArguments().getString(Constants.PROFILE_OPEN, "");
        }
        if (type.equals("Normal")) {

            binding.back.setVisibility(View.VISIBLE);
            binding.tvTerms.setVisibility(View.VISIBLE);
            binding.topLogoo.setVisibility(View.VISIBLE);
            binding.tvSubTitle.setVisibility(View.GONE);
            binding.tvTitle.setVisibility(View.VISIBLE);
            binding.tvPassword.setVisibility(View.VISIBLE);
            binding.rlPassword.setVisibility(View.VISIBLE);

        } else if (type.equalsIgnoreCase("Social")){
            facebookModel=getArguments().getParcelable(Constants.LOGIN_TYPE_DATA);
            binding.back.setVisibility(View.VISIBLE);
            binding.tvTerms.setVisibility(View.VISIBLE);
            binding.topLogoo.setVisibility(View.VISIBLE);
            binding.tvSubTitle.setVisibility(View.GONE);
            binding.tvTitle.setVisibility(View.VISIBLE);
            binding.tvPassword.setVisibility(View.GONE);
            binding.rlPassword.setVisibility(View.GONE);
            binding.etName.setText(facebookModel.firstName);
            binding.etLName.setText(facebookModel.lastName);
            binding.etEmail.setText(facebookModel.email);
        } else if (type.equals("Payment")) {
            binding.back.setVisibility(View.GONE);
            binding.tvTerms.setVisibility(View.GONE);
            binding.topLogoo.setVisibility(View.GONE);
            binding.tvSubTitle.setVisibility(View.VISIBLE);
            binding.tvTitle.setVisibility(View.GONE);
        }
        binding.etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        binding.imageViewEye.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_visibility_off_24));
       // binding.ccp.getSelectedCountryName();
       // binding.etPhoneNo.getText().toString().trim();
    }
    private void setData() {
    }
    private void setListeners() {
        binding.back.setOnClickListener(this);
        binding.tvContinue.setOnClickListener(this);
        binding.llFacebook.setOnClickListener(this);
        binding.llGoogle.setOnClickListener(this);
        binding.etName.setOnClickListener(this);
        binding.etEmail.setOnClickListener(this);
        binding.etPassword.setOnClickListener(this);
        binding.imageViewEye.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvContinue:
                mFirebaseAnalytics.logEvent("signscr_click_cont", null);
                AuthenticateActivity.user_type_TEMP = "native";
                AuthenticateActivity.user_type = "native";
                if (GeneralFunction.isNetworkConnected(getActivity(), binding.tvContinue)) {
                    if (checkValidation()) {
                        apiSignUpCall();
                    }
                }
                break;
            case R.id.back:
                if (isClose)
                    getActivity().finish();
                else
                    getActivity().onBackPressed();
                break;
            case R.id.llFacebook:
                AuthenticateActivity.user_type = "social";
                if (GeneralFunction.isNetworkConnected(getActivity(), binding.parent)) {
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
                        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                        startActivityForResult(signInIntent, RC_SIGN_IN);
                    } else {
                        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                        startActivityForResult(signInIntent, RC_SIGN_IN);
                    }
                } else {
                    GeneralFunction.showSnackBar(getActivity(), binding.parent, getString(R.string.check_connection));
                }
                break;
            case R.id.etName:
                mFirebaseAnalytics.logEvent("signscr_fnf_complete", null);
                break;
            case R.id.etEmail:
                mFirebaseAnalytics.logEvent("signscr_email_complete", null);
                break;
            case R.id.etPassword:
                mFirebaseAnalytics.logEvent("signscr_passw_complete", null);
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
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {

            Log.e("check1", "InSide");
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);

        }
    }

    public void initTermsAndPolicyView() {
        String terms = getString(R.string.term_of_service);
        String privacyPolicy = getString(R.string.privacy_policy_2);
        SpannableStringBuilder spanTxt = new SpannableStringBuilder(
                getString(R.string.i_agree_to));
        spanTxt.append(terms);
        spanTxt.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Prefs.with(getActivity()).save(Constants.LOGIN_COUNTRY,binding.ccp.getSelectedCountryNameCode());
                startActivity(new Intent(getActivity(), TermsActivity.class));
            }
        }, spanTxt.length() - terms.length(), spanTxt.length(), 0);
        spanTxt.append(getString(R.string.and));
        spanTxt.append(privacyPolicy);
        spanTxt.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Prefs.with(getActivity()).save(Constants.LOGIN_COUNTRY,binding.ccp.getSelectedCountryNameCode());
                startActivity(new Intent(getActivity(), PrivacyPolicyActivity.class));
            }
        }, spanTxt.length() - privacyPolicy.length(), spanTxt.length(), 0);
        spanTxt.setSpan(new ForegroundColorSpan(Color.BLACK), 0, spanTxt.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.tvTerms.setMovementMethod(LinkMovementMethod.getInstance());
        binding.tvTerms.setText(spanTxt, TextView.BufferType.SPANNABLE);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            //Toast.makeText(getActivity(), "yes", Toast.LENGTH_SHORT).show();
            Log.e("data", "" + new Gson().toJson(account));

            StringTokenizer tokens = new StringTokenizer(account.getDisplayName(), " ");
            String firstname = tokens.nextToken();// this will contain "Fruit"
            String lastname = tokens.nextToken();
            FacebookModel facebookModel = new FacebookModel(Constants.UNIQUE_APP_KEY,
                    account.getId(), account.getDisplayName(), account.getEmail(), null, token, Constants.DEVICE_TYPE,"GOOGLE",firstname,lastname);
            presenter.apiFacebook(facebookModel);
            // Signed in successfully, show authenticated UI.
            //   updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e("sd", "signInResult:failed code=" + e.getLocalizedMessage());
            // updateUI(null);
        }
    }

    public void apiSignUpCall() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uniquieAppKey", Constants.UNIQUE_APP_KEY);
        hashMap.put("firstName",binding.etName.getText().toString().trim());
        hashMap.put("lastName",binding.etLName.getText().toString().trim());
        hashMap.put("fullName", binding.etName.getText().toString().trim()+" "+binding.etLName.getText().toString().trim());
        hashMap.put("email", binding.etEmail.getText().toString());
        hashMap.put("countryENCode", binding.ccp.getSelectedCountryNameCode());
        hashMap.put("countryCode", binding.ccp.getSelectedCountryCodeWithPlus());
        hashMap.put("phoneNo", binding.etPhoneNo.getText().toString());
        hashMap.put("password", binding.etPassword.getText().toString());
        hashMap.put("deviceToken", token);
        hashMap.put("optForEmail",binding.checkBoxMarketing.isChecked());
        hashMap.put("deviceType", Constants.DEVICE_TYPE);
      /*  if (type.equalsIgnoreCase("Social")){
            hashMap.put("loginType",facebookModel.loginType);
        }else {

        }*/

        Log.e("signup params", "" + hashMap);
         presenter.apiSignup(hashMap);
    }

    public boolean checkValidation() {
        String name = binding.etName.getText().toString().trim();
        String lname = binding.etLName.getText().toString().trim();

        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        String phone=binding.etPhoneNo.getText().toString().trim();
        String namePattern = "[a-zA-Z0-9\\s]{3,20}";
        if (name.isEmpty()) {
            GeneralFunction.showSnackBar(getActivity(), binding.parent, getString(R.string.name_empty_validation));
            binding.etName.requestFocus();
            return false;
        } else if(lname.isEmpty()){
            GeneralFunction.showSnackBar(getActivity(), binding.parent, getString(R.string.lname_empty_validation));
            binding.etName.requestFocus();
            return false;
        } else if (email.isEmpty()) {
            GeneralFunction.showSnackBar(getActivity(), binding.parent, getString(R.string.email_empty_validation));
            binding.etEmail.requestFocus();
            return false;
        } else if (!TextUtils.isCharacterOrSpaceOnly(name)) {
            GeneralFunction.showSnackBar(getActivity(), binding.parent, getString(R.string.name_should_not_contain_number));
            binding.etName.requestFocus();
            return false;
        }  else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            GeneralFunction.showSnackBar(getActivity(), binding.parent, getString(R.string.email_valid_validation));
            binding.etEmail.requestFocus();
            return false;
        }else if (password.isEmpty()) {
            GeneralFunction.showSnackBar(getActivity(), binding.parent, getString(R.string.password_empty_validation));
            binding.etPassword.requestFocus();

            return false;
        }

  /*     else if(!name.matches(namePattern))
        {
            GeneralFunction.showSnackBar(getActivity(),binding.parent,getString(R.string.name_valid_validation));
            binding.etName.requestFocus();
            return false;

        }*/

      /*  else if()
        {

        }*/
        else if (password.length() < 6 || password.length() > 16) {
            GeneralFunction.showSnackBar(getActivity(), binding.parent, getString(R.string.password_valid_validation));
            binding.etPassword.requestFocus();
            return false;
        }else if (phone.isEmpty()) {
            GeneralFunction.showSnackBar(getActivity(), binding.parent, getString(R.string.phoneno_empty_validation));
            binding.etPhoneNo.requestFocus();

            return false;
        }
        else if(!(givenPhoneNumber_whenValid())){
            Log.d(TAG, "validateData: givenPhoneNumber_whenValid() "+givenPhoneNumber_whenValid());
            GeneralFunction.showSnackBar(requireActivity(),binding.parent,getString(R.string.valid_phoneno));
            binding.etPhoneNo.requestFocus();
            return  false;
        }

        /*else if(phone.length() <6 || phone.length() >15){
            GeneralFunction.showSnackBar(getActivity(),binding.parent,getString(R.string.contact_number_validate));
            return false;
        }*/
        else if (!binding.checkBox1.isChecked()){
            GeneralFunction.showSnackBar(getActivity(), binding.parent,getString(R.string.please_agree_to_terms));
            return false;
        }


        return true;
    }



    public boolean givenPhoneNumber_whenValid()  {
        try {
            Phonenumber.PhoneNumber phone = phoneNumberUtil.parse(binding.ccp.getSelectedCountryCodeWithPlus() + binding.etPhoneNo.getText().toString(),
                    Phonenumber.PhoneNumber.CountryCodeSource.UNSPECIFIED.name());
            android.util.Log.d(TAG, "givenPhoneNumber_whenValid: region"+binding.ccp.getSelectedCountryNameCode());
            android.util.Log.d(TAG, "givenPhoneNumber_whenValid: "+(phoneNumberUtil.isValidNumber(phone) && phoneNumberUtil.isValidNumberForRegion(phone, binding.ccp.getSelectedCountryNameCode())));

            return  (phoneNumberUtil.isValidNumber(phone) && phoneNumberUtil.isValidNumberForRegion(phone, binding.ccp.getSelectedCountryNameCode()));

        }catch (Exception e){
            android.util.Log.d(TAG, "givenPhoneNumber_whenValid_thenOK: "+ e.getMessage());
            return false;
        }
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
    public void signupSuccess(PojoLogin data) {
        android.util.Log.e(TAG, "signupSuccess: signUp success :" + new Gson().toJson(data));
         Prefs.with(getActivity()).save(Constants.ACCESS_TOKEN, "bearer " + data.getAccessToken());
        Prefs.with(getActivity()).save(Constants.COMPLETE_PROFILE, data.isProfileComplete());
        Prefs.with(getActivity()).save(Constants.EMAIL_ID, data.email);
        Prefs.with(getActivity()).save(Constants.LOGIN_COUNTRY,data.getCountryENCode());
        Prefs.with(getActivity()).save(Constants.DATA, data);
        if(data.isProfileComplete()){
            startActivity(new Intent(getContext(), Main2Activity.class));
            if(!data.emailVerified) {
                Toast.makeText(requireContext(), getString(R.string.msg_please_email), Toast.LENGTH_LONG).show();
            }
            getActivity().finishAffinity();
        }else {
            presenter.verifyPhoneNumberWithFirebase(data.getPhoneNo(), data.getCountryCode(), firebaseAuth, requireActivity());
        }
    }



    @Override
    public void signupError(String errorMsg) {
        android.util.Log.e(TAG, "signupError: "+errorMsg );
        Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void signupSuccess(ApiResponse<PojoLogin> data) {
        Prefs pref = Prefs.with(getActivity());
        pref.save(Constants.ACCESS_TOKEN, "bearer " + data.getData().getAccessToken());
        Prefs.with(getActivity()).save(Constants.EMAIL_ID, data.getData().email);
        pref.save(Constants.LOGIN_COUNTRY,data.getData().getCountryENCode());
        pref.save(Constants.DATA, data.getData());

        if (data.getData().isProfileComplete()) {

            pref.save(Constants.COMPLETE_PROFILE, true);
            startActivity(new Intent(getActivity(), Main2Activity.class));
        } else {
            SignUp2Fragment fragment = (SignUp2Fragment) getActivity().getSupportFragmentManager().findFragmentByTag("SignUp2Fragment");
            if (fragment != null) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                        .replace(android.R.id.content, fragment, "SignUp2Fragment").commit();

            } else {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                        .replace(android.R.id.content, SignUp2Fragment.newInstance("Normal"), "SignUp2Fragment").addToBackStack("SignUp2Fragment").commit();

            }
        }
    }

    @Override
    public void signupFailure(String failureMsg) {
        Toast.makeText(getActivity(), getString(R.string.check_connection), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onOtpSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token,String countrycode,String phoneNumber) {
        Log.d(TAG, "onCodeSent:" + verificationId);
        Prefs.with(requireContext()).save(Constants.FIREBASE_AUTH_VERIFICATION_ID, verificationId);
        Prefs.with(requireContext()).save(Constants.FIREBASE_AUTH_RESEND_TOKEN, token);
        Intent intent = new Intent(getActivity(), OtpVerifyActivity.class);
        intent.putExtra("PhoneNumber", countrycode + phoneNumber);
        android.util.Log.e(TAG, "onCodeSent: setLoading(false); 3");
        setLoading(false);
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(
                mMessageReceiver);
        presenter.detachView();
    }
}
