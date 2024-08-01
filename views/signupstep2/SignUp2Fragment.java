package com.maktoday.views.signupstep2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;

import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;

import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.maktoday.utils.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.maktoday.R;
import com.maktoday.databinding.FragmentSignup2Binding;
import com.maktoday.interfaces.UpdateProgressStatus;
import com.maktoday.model.ApiResponse;
import com.maktoday.model.BookServiceBulkModel;
import com.maktoday.model.BookServiceModel;
import com.maktoday.model.DocumentPicURL;
import com.maktoday.model.FullAddress;
import com.maktoday.model.PojoLogin;
import com.maktoday.model.PojoService;
import com.maktoday.model.PojoServiceNew;
import com.maktoday.model.SearchMaidBulkModel;
import com.maktoday.model.SearchMaidModel;
import com.maktoday.model.SignUpModel;
import com.maktoday.utils.AmazoneS3;
import com.maktoday.utils.Constants;
import com.maktoday.utils.DialogPopup;
import com.maktoday.utils.GeneralFunction;
import com.maktoday.utils.ImagePicker;
import com.maktoday.utils.ImageViewActivity;
import com.maktoday.utils.Prefs;
import com.maktoday.utils.TextUtils;
import com.maktoday.utils.dialog.IOSAlertDialog;
import com.maktoday.views.AllService.ServiceFragment;
import com.maktoday.views.Otpverification.OtpVerifyActivity;
import com.maktoday.views.authenticate.AuthenticateActivity;
import com.maktoday.views.bookagain.BookAgainActivity;
import com.maktoday.views.cardlist.CardListFragment;
import com.maktoday.views.confirmbook.ConfirmBookFragment;
import com.maktoday.views.home.HomeFragment;
import com.maktoday.views.maidbook.MaidBookActivity;
import com.maktoday.views.privacyPolicy.PrivacyPolicyActivity;
import com.maktoday.views.terms.TermsActivity;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static com.maktoday.views.cardlist.CardListFragment.referenceId;
import static com.maktoday.views.home.HomeFragment.booking_type;
import static com.maktoday.views.home.HomeFragment.selectedDatesList;
import static com.maktoday.views.maidsearch.MaidFragment.temp_id;
import static com.maktoday.views.maidsearch.MaidFragment.temp_name;

/**
 * Created by cbl81 on 25/10/17.
 * <p>
 * This fragment is used in four ways.One is Signup process,second one Edit Profile and
 * third one Payment for billing information, fourth one is when we go as guest in the app
 */

public class SignUp2Fragment extends Fragment implements SignUp2Contract.View, View.OnClickListener, ImagePicker.ImagePickerListener {

    private static final String TAG = "SignUp2Fragment";

    private static final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private static final int PAYMENT_REQ_CODE = 1;
    private final static int REQUEST_CODE = 0;
    private FragmentSignup2Binding binding;
    private String type;                //"Type" flag is used to differentiate between process."Payment" Type  is used as Billing info in payment module
    // "UpdateProfile" types is used as EditProfile and "Normal" Type is used as normal Signup process
    private SignUp2Contract.Presenter presenter;
    private UpdateProgressStatus updateProgressStatus;
    private SearchMaidModel searchMaidModel;
    private SearchMaidBulkModel searchMaidBulkModel;
    private ImagePicker imagePicker;
    private String url;
    FirebaseAuth firebaseAuth;
    private DatePickerDialog datePickerDialog;
    private boolean sameAddress = false;
    private boolean guestSignUp = false;
    private String facebookId = "";
    private  String serviceId="";
    private String token = "";

    String[] country = {"Select Country", "Bahrain", "London"};
    public static SignUp2Fragment newInstance(String profileOpen) {
        return newInstance(profileOpen, (SearchMaidModel) null);
    }

    public static SignUp2Fragment newInstance(String displyType, SearchMaidModel searchMaidModel, Boolean guestSignup) {
        Bundle args = new Bundle();
        SignUp2Fragment fragment = new SignUp2Fragment();
        args.putString(Constants.DISPLAY_TYPE, displyType);
        args.putParcelable(Constants.SEARCH_MAID_DATA, searchMaidModel);
        args.putBoolean(Constants.GUEST_SIGN_UP, guestSignup);
        fragment.setArguments(args);
        return fragment;
    }

    public static SignUp2Fragment newInstance(String displyType, SearchMaidModel searchMaidModel,String serviceId) {

        Bundle args = new Bundle();
        SignUp2Fragment fragment = new SignUp2Fragment();
        args.putString(Constants.DISPLAY_TYPE, displyType);
        args.putParcelable(Constants.SEARCH_MAID_DATA, searchMaidModel);
        args.putString(Constants.SERVICE_ID,serviceId);
        fragment.setArguments(args);
        return fragment;
    }


    public static SignUp2Fragment newInstance(String displyType, SearchMaidBulkModel searchMaidModel, Boolean guestSignup, String type) {

        Bundle args = new Bundle();
        SignUp2Fragment fragment = new SignUp2Fragment();
        args.putString(Constants.DISPLAY_TYPE, displyType);
        args.putParcelable(Constants.SEARCH_MAID_DATA, searchMaidModel);
        args.putBoolean(Constants.GUEST_SIGN_UP, guestSignup);
        fragment.setArguments(args);
        return fragment;
    }

    public static SignUp2Fragment newInstance(String displyType, SearchMaidModel searchMaidModel) {
        return newInstance(displyType, searchMaidModel, false);

    }

    public static SignUp2Fragment newInstance(String displyType, SearchMaidBulkModel searchMaidModel) {
        return newInstance(displyType, searchMaidModel, false, "");

    }

    private static void openAppSettings(Activity context) {
        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        context.startActivityForResult(intent, 25);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().getParcelable(Constants.SEARCH_MAID_DATA) != null) {
            if (booking_type == 3) {
                searchMaidBulkModel = getArguments().getParcelable(Constants.SEARCH_MAID_DATA);
            } else {
                searchMaidModel = getArguments().getParcelable(Constants.SEARCH_MAID_DATA);
            }
        }

        presenter = new SignUp2Presenter();
        presenter.attachView(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSignup2Binding.inflate(inflater, container, false);
        //set variables in Binalogng

        FirebaseApp.initializeApp(getActivity());
        ArrayAdapter aa = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, country);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        binding.spinnerr.setAdapter(aa);
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
                            Log.w(TAG, "Fetching FCM registration token failed"+ task.getException());
                            token =    "Fetching FCM registration token failed"+ task.getException().toString();
                            return;
                        }
                        // Get new FCM registration token
                        token = task.getResult();
                    }
                });
        Log.d(TAG, "init: device token"+ token);
        initTermsAndPolicyView();
        String token = Prefs.with(getActivity()).getString(Constants.ACCESS_TOKEN, "");
        if (getArguments() != null) {
            type = getArguments().getString(Constants.DISPLAY_TYPE, "");
            guestSignUp = getArguments().getBoolean(Constants.GUEST_SIGN_UP, false);
        }

        //updateProgressStatus used to handle the progress bar of payment flo
        updateProgressStatus = (UpdateProgressStatus) getActivity().getSupportFragmentManager()
                .findFragmentByTag("PaymentStateFragment");
        // firstly we hide all view and then after hit book service api.This changes received from client  after project completion so
        // below code is used to bypass billing info screen bcz type is "Payment"
        if (type.equalsIgnoreCase("Payment") && !token.isEmpty()) {
            binding.rl.setVisibility(View.GONE);

            final String googleApiKey = getString(R.string.google_api_key_mak);
            if (booking_type == 3) {
                Log.e("frfr", new Gson().toJson(searchMaidBulkModel));
                presenter.getTimeZoneFromLatLong(searchMaidBulkModel.lat, searchMaidBulkModel.lng, googleApiKey);
            } else {
                presenter.getTimeZoneFromLatLong(searchMaidModel.lat, searchMaidModel.lng, googleApiKey);
            }
        } else {
            binding.rl.setVisibility(View.VISIBLE);

            init();
            setData();
            setListeners();

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
                startActivity(new Intent(getActivity(), TermsActivity.class));
            }
        }, spanTxt.length() - terms.length(), spanTxt.length(), 0);
        spanTxt.append(getString(R.string.and));
        spanTxt.append(privacyPolicy);
        spanTxt.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                startActivity(new Intent(getActivity(), PrivacyPolicyActivity.class));
            }
        }, spanTxt.length() - privacyPolicy.length(), spanTxt.length(), 0);
        spanTxt.setSpan(new ForegroundColorSpan(Color.BLACK), 0, spanTxt.length(), 0);
        binding.tvTerms.setMovementMethod(LinkMovementMethod.getInstance());
        binding.tvTerms.setText(spanTxt, TextView.BufferType.SPANNABLE);
    }

    private void init() {

        imagePicker = new ImagePicker(this);
        firebaseAuth = FirebaseAuth.getInstance();

        PojoLogin profile = Prefs.with(getContext()).getObject(Constants.DATA, PojoLogin.class);


       Log.d(TAG, "init: type :--"+type);
        switch (type) {
            
            case "LoginProfileIncomplete":
                binding.tvTitle.setVisibility(View.VISIBLE);
                binding.tvSubTitle.setVisibility(View.VISIBLE);
                binding.tvTerms.setVisibility(View.VISIBLE);
                binding.checkBox1.setVisibility(View.VISIBLE);
                binding.checkBoxMarketing.setVisibility(View.VISIBLE);
                binding.tvMerketing.setVisibility(View.VISIBLE);
                binding.back.setVisibility(View.VISIBLE);
                binding.tvName.setVisibility(View.GONE);
                binding.etName.setVisibility(View.GONE);
                binding.tvLName.setVisibility(View.GONE);
                binding.etLName.setVisibility(View.GONE);
                binding.tvfullname.setVisibility(View.GONE);
                binding.etfullname.setVisibility(View.GONE);
                binding.tvSameAddress.setVisibility(View.GONE);
                binding.tvContinue.setText(getString(R.string.continue1));
                binding.tvEmail.setVisibility(View.VISIBLE);
                binding.etEmail.setVisibility(View.VISIBLE);
                binding.tvUploadImage.setVisibility(View.GONE);
                binding.ivUploadImage.setVisibility(View.GONE);
                binding.tvIDImage.setVisibility(View.GONE);
                binding.etIDNumber.setEnabled(true);
                break;
            case "Payment":
                Log.d(TAG, "init:  case \"Payment\":");
                binding.tvSameAddress.setVisibility(View.VISIBLE);

            case "Guest":
                Log.d(TAG, "init: case \"Guest\":");
                updateProgressStatus.changeProgressStatus(((MaidBookActivity) getActivity()).step);
                binding.tvEmail.setVisibility(View.VISIBLE);
                binding.etEmail.setVisibility(View.VISIBLE);
                if (guestSignUp && type.equals("Guest")) {
                    Log.d(TAG, "init: if (guestSignUp && type.equals(\"Guest\")) {");
                    binding.tvName.setVisibility(View.GONE);
                    binding.etName.setVisibility(View.GONE);
                    binding.tvLName.setVisibility(View.GONE);
                    binding.etLName.setVisibility(View.GONE);
                    binding.tvfullname.setVisibility(View.GONE);
                    binding.etfullname.setVisibility(View.GONE);

                } else {
                    Log.d(TAG, "init:  } else {2");
                    binding.tvName.setVisibility(View.VISIBLE);
                    binding.etName.setVisibility(View.VISIBLE);
                }

                binding.tvTitle.setVisibility(View.GONE);
                binding.tvSubTitle.setVisibility(View.GONE);
                binding.tvTerms.setVisibility(View.GONE);
                binding.tvMerketing.setVisibility(View.GONE);
                binding.checkBoxMarketing.setVisibility(View.GONE);
                binding.back.setVisibility(View.GONE);

                binding.etBlockNumber.setText("");
                binding.etCity.setText("");
                binding.etRoadNumber.setText("");
                binding.etCountry.setText("");
                binding.etApartment.setText("");
                binding.etAddressDescription.setText("");

                if (type.equals("Guest") && profile.isFirstBookingDone()) {
                    Log.d(TAG, "init:   if (type.equals(\"Guest\") && profile.isFirstBookingDone()) {");
                    binding.tvIDImage.setVisibility(View.GONE);
                    binding.tvUploadImage.setVisibility(View.VISIBLE);
                    binding.tvSameAddress.setVisibility(View.GONE);
                    binding.backLayyy.setVisibility(View.VISIBLE);
                    binding.back.setVisibility(View.VISIBLE);
                } else {
                    Log.d(TAG, "init: } else {04");
                    binding.tvIDImage.setVisibility(View.GONE);
                    binding.tvUploadImage.setVisibility(View.GONE);
                    if (AuthenticateActivity.user_type_TEMP.equalsIgnoreCase("native")) {
                        Log.d(TAG, "init:  if (AuthenticateActivity.user_type_TEMP.equalsIgnoreCase(\"native\")) {");
                        binding.etName.setVisibility(View.GONE);
                        binding.tvName.setVisibility(View.GONE);
                        binding.tvLName.setVisibility(View.GONE);
                        binding.etLName.setVisibility(View.GONE);
                        binding.tvfullname.setVisibility(View.GONE);
                        binding.etfullname.setVisibility(View.GONE);

                        binding.tvSameAddress.setVisibility(View.GONE);
                        binding.backLayyy.setVisibility(View.VISIBLE);
                        binding.back.setVisibility(View.VISIBLE);
                        binding.tvCity.setVisibility(View.GONE);
                        binding.tvBlockNumber.setVisibility(View.GONE);
                        binding.etBlockNumber.setVisibility(View.GONE);
                        binding.etCity.setVisibility(View.GONE);
                        binding.tvRoadNumber.setVisibility(View.GONE);
                        binding.etRoadNumber.setVisibility(View.GONE);
                        binding.tvCountry.setVisibility(View.GONE);
                        binding.etCountry.setVisibility(View.GONE);
                        binding.tvApartment.setVisibility(View.GONE);
                        binding.etApartment.setVisibility(View.GONE);
                        binding.tvAddressDescription.setVisibility(View.GONE);
                        binding.etAddressDescription.setVisibility(View.GONE);
                    } else {
                        Log.d(TAG, "init: } else {05");
                        binding.tvSameAddress.setVisibility(View.VISIBLE);
                        binding.backLayyy.setVisibility(View.VISIBLE);
                        binding.tvCity.setVisibility(View.VISIBLE);
                        binding.tvBlockNumber.setVisibility(View.VISIBLE);
                        binding.etBlockNumber.setVisibility(View.VISIBLE);
                        binding.etCity.setVisibility(View.VISIBLE);
                        binding.tvRoadNumber.setVisibility(View.VISIBLE);
                        binding.etRoadNumber.setVisibility(View.VISIBLE);
                        binding.tvCountry.setVisibility(View.VISIBLE);
                        binding.etCountry.setVisibility(View.VISIBLE);
                        binding.tvApartment.setVisibility(View.VISIBLE);
                        binding.etApartment.setVisibility(View.VISIBLE);
                        binding.tvAddressDescription.setVisibility(View.VISIBLE);
                        binding.etAddressDescription.setVisibility(View.VISIBLE);
                    }
                }
                binding.ivUploadImage.setVisibility(View.GONE);
                binding.tvContinue.setText(getString(R.string.continue1));
                binding.etIDNumber.setEnabled(true);
                break;

            case "UpdateProfile":
                Log.d(TAG, "init:   case \"UpdateProfile\":");
                binding.tvTitle.setVisibility(View.VISIBLE);
                binding.tvSubTitle.setVisibility(View.VISIBLE);
                binding.tvTitle.setText(getString(R.string.update_profile_title));
                binding.tvSubTitle.setText(getString(R.string.update_profile_sub_title));
                binding.tvContinue.setText(getString(R.string.update_profile_btn));
                binding.tvName.setVisibility(View.VISIBLE);
                binding.etName.setVisibility(View.VISIBLE);
                binding.tvLName.setVisibility(View.VISIBLE);
                binding.etLName.setVisibility(View.VISIBLE);
                binding.tvfullname.setVisibility(View.GONE);
                binding.etfullname.setVisibility(View.GONE);
                binding.tvSameAddress.setVisibility(View.GONE);
                binding.tvTerms.setVisibility(View.GONE);
                binding.checkBox1.setVisibility(View.GONE);
                binding.tvMerketing.setVisibility(View.GONE);
                binding.checkBoxMarketing.setVisibility(View.GONE);
                binding.tvEmail.setVisibility(View.GONE);
                binding.etEmail.setVisibility(View.GONE);
                binding.tvUploadImage.setVisibility(View.GONE);
                binding.ivUploadImage.setVisibility(View.GONE);
                binding.tvIDImage.setVisibility(View.GONE);
                binding.etIDNumber.setEnabled(false);
                binding.tvName.setText(getString(R.string.name_without_start));
                binding.tvContactNumber.setText(getString(R.string.contact_numberr));

                binding.tvUploadedNationalIdCard.setVisibility(View.GONE);
                binding.ivUploadedNationalIdCard.setVisibility(View.GONE);
                binding.tvYourEmailId.setVisibility(View.VISIBLE);
                binding.etEmailId.setVisibility(View.VISIBLE);
                break;

            default:
                Log.d(TAG, "init:  default:");
                Log.d(TAG, "init: default");
                binding.tvTitle.setVisibility(View.VISIBLE);
                binding.tvSubTitle.setVisibility(View.VISIBLE);
                binding.tvTerms.setVisibility(View.VISIBLE);
                binding.checkBox1.setVisibility(View.VISIBLE);
                binding.checkBoxMarketing.setVisibility(View.VISIBLE);
                binding.tvMerketing.setVisibility(View.VISIBLE);
                binding.back.setVisibility(View.VISIBLE);
                binding.tvName.setVisibility(View.GONE);
                binding.etName.setVisibility(View.GONE);
                binding.tvLName.setVisibility(View.GONE);
                binding.etLName.setVisibility(View.GONE);
                binding.tvfullname.setVisibility(View.GONE);
                binding.etfullname.setVisibility(View.GONE);
                binding.tvSameAddress.setVisibility(View.GONE);
                binding.tvContinue.setText(getString(R.string.continue1));
                binding.tvEmail.setVisibility(View.GONE);
                binding.etEmail.setVisibility(View.GONE);
                binding.tvUploadImage.setVisibility(View.GONE);
                binding.ivUploadImage.setVisibility(View.GONE);
                binding.tvIDImage.setVisibility(View.GONE);
                binding.etIDNumber.setEnabled(true);
                break;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setData() {
        PojoLogin data = Prefs.with(getContext()).getObject(Constants.DATA, PojoLogin.class);

        Log.e(TAG, " ---------- data = " + new Gson().toJson(data));
        Log.e(TAG, " ---------- type = " + type);

        switch (type) {

            case "LoginProfileIncomplete":
                binding.etEmail.setEnabled(false);
                binding.etEmail.setText(Prefs.with(getActivity()).getString(Constants.EMAIL_ID, ""));
                binding.etName.setText(data.firstName);
                binding.etLName.setText(data.lastName);
                binding.etfullname.setText(data.fullName);
                break;

            case "Guest":

                if (AuthenticateActivity.user_type_TEMP.equalsIgnoreCase("guest")) {
                     binding.etEmail.setEnabled(false);
                    binding.etEmail.setText(Prefs.with(getActivity()).getString(Constants.EMAIL_ID, ""));
                    binding.etName.setText(data.firstName);
                    binding.etLName.setText(data.lastName);
                    binding.etfullname.setText(data.fullName);
                } else {
                    binding.etEmail.setEnabled(false);
                    binding.etEmail.setText(Prefs.with(getActivity()).getString(Constants.EMAIL_ID, ""));
                    binding.etName.setText(data.firstName);
                    binding.etLName.setText(data.lastName);
                    binding.etfullname.setText(data.fullName);
                }

                break;
            case "UpdateProfile":
                if (data != null) {
                    //todo
                    if (data.fullName != null) {
                       // binding.etName.setText(data.firstName);
                        binding.etName.setSelection(binding.etName.getText().length());
                    }
                    if(!data.firstName.isEmpty()){
                        binding.etName.setText(data.firstName);
                        binding.etLName.setText(data.lastName);
                    }else {
                        StringTokenizer st = new StringTokenizer(data.fullName, " ");
                        String first = st.nextToken();
                        String last = st.nextToken();
                        binding.etName.setText(first);
                        binding.etLName.setText(last);
                    }


                    binding.etfullname.setText(data.firstName+" "+data.lastName);
                    Prefs.with(getActivity()).save(Constants.DATA,data);
                    if (data.getNationalId() != null)
                        binding.etIDNumber.setText(data.getNationalId());
                    if (data.getUsersAddress() != null && data.getUsersAddress().country != null)
                        binding.etCountry.setText(data.getUsersAddress().country);
                    if (data.getUsersAddress() != null && data.getUsersAddress().city != null)
                        binding.etCity.setText(data.getUsersAddress().city);
                    if (data.getUsersAddress() != null && data.getUsersAddress().streetName != null)
                        binding.etRoadNumber.setText(data.getUsersAddress().streetName);
                    if (data.getUsersAddress() != null && data.getUsersAddress().buildingName != null)
                        binding.etBlockNumber.setText(data.getUsersAddress().buildingName);
                    if (data.getUsersAddress() != null && data.getUsersAddress().villaName != null)
                        binding.etApartment.setText(data.getUsersAddress().villaName);
                    if (data.getUsersAddress() != null && data.getUsersAddress().moreDetailedaddress != null)
                        binding.etAddressDescription.setText(data.getUsersAddress().moreDetailedaddress);
                    if (data.getPhoneNo() != null) {
                        binding.etPhoneNo.setText(data.getPhoneNo());
                        binding.etPhoneNo.setEnabled(false);
                    }
                    else{
                        binding.etPhoneNo.setEnabled(true);
                    }
                    if (data.getCountryENCode() != null) {
                        binding.ccp.setCountryForNameCode(data.getCountryENCode());
                        binding.ccp.setCcpClickable(false);
                    }else{
                        binding.ccp.setCcpClickable(true);
                    }
                    if (data.getDocumentPicURL() != null && data.getDocumentPicURL().getOriginal() != null) {
                    Glide.with(getActivity())
                            .load(data.getDocumentPicURL().getOriginal())
                            .placeholder(R.drawable.cardplaceholder)
                            .error(R.drawable.cardplaceholder)
                            .centerCrop()
                            .into(binding.ivUploadedNationalIdCard);
                     }
                    if (data.getEmail() != null) {
                        binding.etEmailId.setText(data.getEmail());
                        binding.etEmailId.setEnabled(false);
                    }
                    else{
                        binding.etEmailId.setEnabled(true);
                    }
                }
                break;
            case "Normal":
                Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                Location location = Prefs.with(getContext()).getObject(Constants.LOCATION, Location.class);

                List<Address> addressList = null;
                if (location != null) {
                    try {
                        addressList = geocoder.getFromLocation(
                                location.getLatitude(), location.getLongitude(), 1);
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }
                if (addressList != null && addressList.size() > 0) {
                    Address address = addressList.get(0);
                    binding.etBlockNumber.setText("");
                    binding.etCity.setText(address.getLocality());
                    binding.etRoadNumber.setText(address.getSubLocality());
                    binding.etCountry.setText(address.getCountryName());
                    binding.etApartment.setText("");
                    binding.etAddressDescription.setText("");
                    binding.ccp.setCountryForNameCode(address.getCountryCode());
                }
                //   if (data != null && data.getFacebookId() != null && !data.getFacebookId().isEmpty()) {
                facebookId = data.facebookId;
                binding.tvEmail.setVisibility(View.VISIBLE);
                binding.etEmail.setVisibility(View.VISIBLE);
                if (data.getEmail() != null) {
                    binding.etEmail.setText(data.getEmail());
                    binding.etEmail.setEnabled(false);
                }else{
                    binding.etEmail.setEnabled(true);
                }
                // }
                break;
            default:
                FullAddress fullAddresss = Prefs.with(getContext()).getObject(Constants.MAP_FULL_ADDRESS, FullAddress.class);
                if (fullAddresss != null) {
                    binding.etBlockNumber.setText(fullAddresss.buildingName);
                    binding.etCity.setText(fullAddresss.city);
                    binding.etRoadNumber.setText(fullAddresss.streetName);
                    binding.etCountry.setText(fullAddresss.country);
                    binding.etApartment.setText(fullAddresss.villaName);
                    binding.etAddressDescription.setText(fullAddresss.moreDetailedaddress);
                }
                break;
        }
        binding.etAddressDescription.setMovementMethod(new ScrollingMovementMethod());
                binding.parent.setOnTouchListener(new View.OnTouchListener() {

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                binding.etAddressDescription.getParent().requestDisallowInterceptTouchEvent(false);
                return false;
            }
        });
        binding.etAddressDescription.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                binding.etAddressDescription.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
    }

    private void setListeners() {
        binding.back.setOnClickListener(this);
        binding.tvContinue.setOnClickListener(this);
        binding.tvUploadImage.setOnClickListener(this);
        binding.ivUploadImage.setOnClickListener(this);
        imagePicker.setImagePickerListener(this);
        binding.tvSameAddress.setOnClickListener(this);
        imagePicker.setImagePickerListener(this);
        // binding.ivUploadedNationalIdCard.setOnClickListener(this);
    }

    public boolean checkValidation() {
        PojoLogin profile = Prefs.with(getContext()).getObject(Constants.DATA, PojoLogin.class);

        String name = binding.etName.getText().toString().trim();
        String lastname = binding.etLName.getText().toString().trim();
        String fullname = binding.etfullname.getText().toString().trim();

        String email = binding.etEmailId.getText().toString().trim();
        String etEmailSocial = binding.etEmail.getText().toString().trim();
        Log.d(TAG, "checkValidation: email social:- " +etEmailSocial);
        Log.d(TAG, "checkValidation: email signup " +email);

        String IDNumber = binding.etIDNumber.getText().toString().trim();
        String city = binding.etCity.getText().toString().trim();
        //  String country = binding.etCountry.getText().toString().trim();
        // String country = binding.spinnerr.getSelectedItem().toString().trim();
        String country = "Bahrain";
        // Toast.makeText(getActivity(), ""+country, Toast.LENGTH_SHORT).show();
        String phoneNo = binding.etPhoneNo.getText().toString().trim();
        String area = binding.etRoadNumber.getText().toString().trim();
        String buildingName = binding.etBlockNumber.getText().toString().trim();
        String apartment = binding.etApartment.getText().toString().trim();
        String addressOther = binding.etApartment.getText().toString().trim();

        if (binding.etName.getVisibility() == View.VISIBLE && name.isEmpty()) {
            GeneralFunction.showSnackBar(getActivity(), binding.parent, getString(R.string.name_empty_validation));
            binding.etName.requestFocus();
            return false;
        } else if(binding.etLName.getVisibility() == View.VISIBLE && lastname.isEmpty()){
            GeneralFunction.showSnackBar(getActivity(), binding.parent, getString(R.string.lname_empty_validation));
            binding.etName.requestFocus();
            return  false;
        }/*else if(binding.etfullname.getVisibility() == View.VISIBLE && fullname.isEmpty()){
            GeneralFunction.showSnackBar(getActivity(), binding.parent,getString(R.string.fullname_empty_validation));
            binding.etName.requestFocus();
        }*/ else if (binding.etName.getVisibility() == View.VISIBLE && !TextUtils.isCharacterOrSpaceOnly(name)) {
            GeneralFunction.showSnackBar(getActivity(), binding.parent, getString(R.string.name_should_not_contain_number));
            binding.etName.requestFocus();
            return false;
        }

        //email validity with social login
        else if (etEmailSocial.isEmpty()  && !type.equalsIgnoreCase("UpdateProfile")) {
            GeneralFunction.showSnackBar(getActivity(), binding.parent, getString(R.string.email_empty_validation));
            binding.etEmail.requestFocus();
            return false;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(etEmailSocial).matches()  && !type.equalsIgnoreCase("UpdateProfile") ) {
            GeneralFunction.showSnackBar(getActivity(), binding.parent, getString(R.string.email_valid_validation));
            binding.etEmail.requestFocus();
            return false;
        }
        //end of email validity in social login

        /* else if (updateProgressStatus != null && updateProgressStatus.getProgressBarPosition() == 1 && email.isEmpty()) {
            GeneralFunction.showSnackBar(getActivity(), binding.parent, getString(R.string.address_empty_email));
            binding.etEmail.requestFocus();
            return false;
        }*/ else if (phoneNo.isEmpty()) {
            GeneralFunction.showSnackBar(getActivity(), binding.parent, getString(R.string.phoneno_empty_validation));
            binding.etPhoneNo.requestFocus();

            return false;
        } else if (phoneNo.length() < 6 || phoneNo.length() > 12) {
            GeneralFunction.showSnackBar(getActivity(), binding.parent, getString(R.string.phoneno_valid_validation));
            binding.etPhoneNo.requestFocus();

            return false;
        }
        else if (email.isEmpty() && type.equalsIgnoreCase("UpdateProfile")) {
            GeneralFunction.showSnackBar(getActivity(), binding.parent, getString(R.string.email_empty_validation));
            binding.etEmail.requestFocus();
            return false;
        } else if (country.equalsIgnoreCase("select country")) {
            GeneralFunction.showSnackBar(getActivity(), binding.parent, getString(R.string.country_empty_validation));
            // binding.etEmail.requestFocus();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches() && type.equalsIgnoreCase("UpdateProfile")) {
            GeneralFunction.showSnackBar(getActivity(), binding.parent, getString(R.string.email_valid_validation));
            binding.etEmail.requestFocus();
            return false;
        } else if (!binding.checkBox1.isChecked() && !type.equalsIgnoreCase("UpdateProfile")){
            GeneralFunction.showSnackBar(getActivity(), binding.parent,getString(R.string.please_agree_to_terms));
            return false;
        }

        return true;
    }

    public boolean validData() {
        String city = binding.etCity.getText().toString().trim();
        String area = binding.etRoadNumber.getText().toString().trim();
        String buildingName = binding.etBlockNumber.getText().toString().trim();
        String apartment = binding.etApartment.getText().toString().trim();


        if (city.isEmpty()) {
            GeneralFunction.showSnackBar(getActivity(), binding.parent, getString(R.string.city_empty_validation));
            binding.etCity.requestFocus();

            return false;
        } else if (area.isEmpty()) {
            GeneralFunction.showSnackBar(getActivity(), binding.parent, getString(R.string.area_empty_validation));
            binding.etRoadNumber.requestFocus();

            return false;
        } else if (buildingName.isEmpty()) {
            GeneralFunction.showSnackBar(getActivity(), binding.parent, getString(R.string.buildingName_empty_validation));
            binding.etBlockNumber.requestFocus();

            return false;
        } else if (apartment.isEmpty()) {
            GeneralFunction.showSnackBar(getActivity(), binding.parent, getString(R.string.apartment_empty_validation));
            binding.etApartment.requestFocus();

            return false;
        }

        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                if (type.equals("UpdateProfile")) {
                    Intent intent = new Intent();
                    getActivity().setResult(2, intent);
                    getActivity().finish();

                } else
                    getActivity().getSupportFragmentManager().popBackStack();
                break;
            case R.id.tvContinue:
                if (!GeneralFunction.isNetworkConnected(getActivity(), binding.parent)) {
                    GeneralFunction.showSnackBar(getActivity(), binding.parent, getString(R.string.check_connection));
                    return;
                }
                if (checkValidation()) {
                    if (type.equals("UpdateProfile")) {
                        Log.d(TAG, "onClick: if (type.equals(\"UpdateProfile\"))");
                        apiUpdateProfile();
                    } else if (type.equals("guest")) {
                        Log.d(TAG, "onClick:  else if (type.equals(\"guest\")) {");
                        PojoLogin profile = Prefs.with(getContext()).getObject(Constants.DATA, PojoLogin.class);
                        if (type.equals("Guest") && profile.isFirstBookingDone()) {
                            Log.d(TAG, "onClick: if (type.equals(\"Guest\") && profile.isFirstBookingDone()) { ");
                            apiSignup2Call();
                        } else {
                            Log.d(TAG, "onClick:  } else { ");
                            if (!validData()) {
                                Log.d(TAG, "onClick:  if (!validData()) {");
                                return;
                            }
                            apiSignup2Call();
                        }
                    } else {
                        Log.d(TAG, "onClick: 2  } else {");
                        apiSignup2Call();
                    }
                }
                break;
            case R.id.ivUploadImage:
            case R.id.tvUploadImage:
                checkForImagePermision();
                break;

            case R.id.ivUploadedNationalIdCard:
                PojoLogin data = Prefs.with(getContext()).getObject(Constants.DATA, PojoLogin.class);
                Intent intent = new Intent(getActivity(), ImageViewActivity.class);
                intent.putExtra("imageUrl", data.documentPicURL.getOriginal());
                startActivity(intent);
                break;

            case R.id.tvSameAddress:
                sameAddress = !sameAddress;
                if (sameAddress) {
                    binding.tvSameAddress.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.rect_border_sky_white));
                    binding.tvSameAddress.setCompoundDrawablesWithIntrinsicBounds(getContext().getDrawable(R.drawable.ic_tick), null, null, null);
                    FullAddress fullAddress = Prefs.with(getActivity()).getObject(Constants.MAP_FULL_ADDRESS, FullAddress.class);
                    binding.etCountry.setText(fullAddress.country);
                    binding.etCity.setText(fullAddress.city);
                    binding.etRoadNumber.setText(fullAddress.streetName);
                    binding.etBlockNumber.setText(fullAddress.buildingName);
                    binding.etApartment.setText(fullAddress.villaName);
                    binding.etAddressDescription.setText(fullAddress.moreDetailedaddress);
                } else {
                    binding.tvSameAddress.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.rect_white));
                    binding.tvSameAddress.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);

                    setData();
                }
                break;

        }
    }

    private void apiUpdateProfile() {
        String otherAddress, phoneNo, cuntryCode, countryCodePlus;
        if (binding.etAddressDescription.getText().toString().trim().isEmpty()) {
            otherAddress = null;
        } else {
            otherAddress = binding.etAddressDescription.getText().toString();
        }

        cuntryCode = binding.ccp.getSelectedCountryNameCode();
        countryCodePlus = binding.ccp.getSelectedCountryCodeWithPlus();
        phoneNo = binding.etPhoneNo.getText().toString().trim();

        SignUpModel signupModel = new SignUpModel(Constants.UNIQUE_APP_KEY,
                null,
                binding.checkBoxMarketing.isChecked(),
                binding.etRoadNumber.getText().toString().trim(),
                binding.etBlockNumber.getText().toString().trim(),
                binding.etApartment.getText().toString().trim(),
                otherAddress,
                binding.etCity.getText().toString().trim(),
                binding.etCountry.getText().toString().trim(),
                cuntryCode,
                countryCodePlus,
                phoneNo,
                TimeZone.getDefault().getID(),
                null,
                null
        );

        signupModel.setFullName(binding.etName.getText().toString().trim()+" "+binding.etLName.getText().toString().trim());
        Log.d(TAG, "apiUpdateProfile: email"+binding.etEmail.getText().toString());


        signupModel.email = binding.etEmailId.getText().toString();
        signupModel.firstName=binding.etName.getText().toString().trim();
        signupModel.lastName=binding.etLName.getText().toString().trim();
        presenter.apiUpdateProfile(signupModel);
    }

    private void apiSignup2Call() {
        FirebaseApp.initializeApp(getActivity());
        String otherAddress;
        if (binding.etAddressDescription.getText().toString().trim().isEmpty()) {
            otherAddress = null;
        } else {
            otherAddress = binding.etAddressDescription.getText().toString();
        }

        SignUpModel signupModel = new SignUpModel(Constants.UNIQUE_APP_KEY,
                binding.etIDNumber.getText().toString().trim(),
                binding.checkBoxMarketing.isChecked(),
                binding.etRoadNumber.getText().toString().trim(),
                binding.etBlockNumber.getText().toString().trim(),
                binding.etApartment.getText().toString().trim(),
                otherAddress,
                binding.etCity.getText().toString().trim(),
                binding.etCountry.getText().toString().trim(),
                binding.ccp.getSelectedCountryNameCode(),
                binding.ccp.getSelectedCountryCodeWithPlus(),
                binding.etPhoneNo.getText().toString().trim(),
                TimeZone.getDefault().getID(),
                 token,
                Constants.DEVICE_TYPE
        );

        signupModel.setEmail(Prefs.with(getActivity()).getString(Constants.EMAIL_ID, ""));

        if (updateProgressStatus != null && updateProgressStatus.getProgressBarPosition() >= 1) {
            String token = Prefs.with(getActivity()).getString(Constants.ACCESS_TOKEN, "");
            if (token.trim().equals("bearer")) {
                signupModel.setGuestFlag(true);
            } else {
                signupModel.setGuestFlag(false);
            }
            signupModel.setFullName(binding.etName.getText().toString());
            signupModel.setEmail(binding.etEmail.getText().toString());
            temp_name = signupModel.fullName;
            temp_id = signupModel.email;

            presenter.apiAddBillingInfo(signupModel);
        } else {

            DocumentPicURL profilePicURL = new DocumentPicURL();
            profilePicURL.setOriginal(url);
            profilePicURL.setThumbnail(url);
            signupModel.setDocumentPicURL(profilePicURL);
            temp_name = binding.etName.getText().toString();
            temp_id = binding.etEmail.getText().toString();
   /*edit */         signupModel.setEmail(binding.etEmail.getText().toString().trim());
            temp_name = signupModel.fullName;
            temp_id = signupModel.email;


            Log.e("signup params", new Gson().toJson(signupModel));
            presenter.apiSignup(signupModel);
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
    public void signupSuccess(ApiResponse<PojoLogin> data) {
        Log.d(TAG, "signupSuccess: updatedProfile :-- "+new Gson().toJson(data));
        PojoLogin profile = Prefs.with(getContext()).getObject(Constants.DATA, PojoLogin.class);
        Log.e("update dtaa", "" + new Gson().toJson(profile));
        if (type.equals("Guest") && profile.isFirstBookingDone()) {
            Log.d(TAG, "signupSuccess: "+ "  if (type.equals(\"Guest\") && profile.isFirstBookingDone())");
            Prefs.with(getActivity()).save(Constants.DATA, data.getData());
            Prefs.with(getActivity()).save(Constants.ACCESS_TOKEN, "bearer " + data.getData().getAccessToken());
            Prefs.with(getActivity()).save(Constants.COMPLETE_PROFILE, data.getData().isProfileComplete());
            Prefs.with(getActivity()).save(Constants.USER_IDs,data.getData()._id);
            Prefs.with(getActivity()).save(Constants.LOGIN_COUNTRY,data.getData().getCountryENCode());
            updateProgressStatus.changeProgressStatus(1);
            ((MaidBookActivity) getActivity()).step = 1;
            getFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                    .replace(R.id.childframeLayout, SignUp2Fragment.newInstance("Guest", searchMaidModel), "SignUp2Fragment").commit();

        } else if (data.getData().isVerified) {
            Log.d(TAG, "signupSuccess: "+" } else if (data.getData().isVerified) {");
            Prefs.with(getActivity()).save(Constants.DATA, data.getData());
            Prefs.with(getActivity()).save(Constants.ACCESS_TOKEN, "bearer " + data.getData().getAccessToken());
            Prefs.with(getActivity()).save(Constants.COMPLETE_PROFILE, data.getData().isProfileComplete());
            Prefs.with(getActivity()).save(Constants.USER_IDs,data.getData()._id);
            Prefs.with(getActivity()).save(Constants.LOGIN_COUNTRY,binding.ccp.getSelectedCountryNameCode());
            //Toast.makeText(getActivity(), getString(R.string.successfully_registered), Toast.LENGTH_SHORT).show();
            if (type.equals("Payment") || type.equals("Guest")) {
                Log.d(TAG, "signupSuccess: "+" if (type.equals(\"Payment\") || type.equals(\"Guest\")) {");
                updateProgressStatus.changeProgressStatus(1);
                ((MaidBookActivity) getActivity()).step = 1;
                getFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                        .replace(R.id.childframeLayout, SignUp2Fragment.newInstance("Payment", searchMaidModel), "SignUp2Fragment").commit();

            } else {
                Log.d(TAG, "signupSuccess: "+" } else {");
            Prefs.with(getActivity()).save(Constants.LOGIN_COUNTRY,binding.ccp.getSelectedCountryNameCode());
            //    startActivity(new Intent(getActivity(), OtpVerifyActivity.class));
                verifyPhoneNumber(data.getData().getPhoneNo(),data.getData().getCountryCode());
            }
        } else {
//            if (AuthenticateActivity.user_type.equalsIgnoreCase("social")) {
                Log.d(TAG, "signupSuccess: " + " } else {\n" +
                        "\n" +
                        "            if (AuthenticateActivity.user_type.equalsIgnoreCase(\"social\")) {");
                Prefs pref = Prefs.with(getActivity());
                pref.save(Constants.ACCESS_TOKEN, "bearer " + data.getData().getAccessToken());
                pref.save(Constants.DATA, data.getData());
                pref.save(Constants.USER_IDs, data.getData()._id);
                Prefs.with(getActivity()).save(Constants.LOGIN_COUNTRY, binding.ccp.getSelectedCountryNameCode());
                pref.save(Constants.COMPLETE_PROFILE, data.getData().isProfileComplete());
                // getActivity().finishAffinity();
               // startActivity(new Intent(getActivity(), OtpVerifyActivity.class));
                verifyPhoneNumber(data.getData().getPhoneNo(),data.getData().getCountryCode());
                //startActivity(new Intent(getActivity(), Main2Activity.class));

//            }
//            else if(type.equals("LoginProfileIncomplete")){
//                Prefs pref = Prefs.with(getActivity());
//                pref.save(Constants.ACCESS_TOKEN, "bearer " + data.getData().getAccessToken());
//                pref.save(Constants.DATA, data.getData());
//                pref.save(Constants.USER_IDs, data.getData()._id);
//                Prefs.with(getActivity()).save(Constants.LOGIN_COUNTRY, binding.ccp.getSelectedCountryNameCode());
//                pref.save(Constants.COMPLETE_PROFILE, data.getData().isProfileComplete());
//                // getActivity().finishAffinity();
//                startActivity(new Intent(getActivity(), OtpVerifyActivity.class));
//                 verifyPhoneNumber(data.getData().getPhoneNo(),data.getData().getCountryCode());
//                //startActivity(new Intent(getActivity(), Main2Activity.class));
//
//            }
//            else {
//                Log.d(TAG, "signupSuccess: "+"} else { 2");
//                Toast.makeText(getActivity(), getString(R.string.msg_please_email_verified), Toast.LENGTH_LONG).show();
//                // Open otp verify screen
//                getActivity().finish();
//                startActivity(new Intent(getActivity(), AuthenticateActivity.class));


//                if (type.equalsIgnoreCase("guest")) {
//                    Prefs pref = Prefs.with(getActivity());
//                    pref.save(Constants.ACCESS_TOKEN, "bearer " + data.getData().getAccessToken());
//                    pref.save(Constants.DATA, data.getData());
//                    pref.save(Constants.USER_IDs,data.getData()._id);
//                    pref.save(Constants.COMPLETE_PROFILE, true);
//                    MaidProfileFragment maidProfileFragment;
//                    if (booking_type == 3) {
//                        maidProfileFragment = MaidProfileFragment.newInstance(null, MaidFragment.maid_temp, searchMaidBulkModel, "", "");
//                    } else {
//                        maidProfileFragment = MaidProfileFragment.newInstance(null, MaidFragment.maid_temp, searchMaidModel, "", "");
//                    }
//
//                    DataVariable.hideSoftKeyboard(getActivity());
//
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                        maidProfileFragment.setSharedElementEnterTransition(new DetailsTransition());
//                        maidProfileFragment.setEnterTransition(new Fade());
//                        setExitTransition(new Fade());
//                        maidProfileFragment.setSharedElementReturnTransition(new DetailsTransition());
//                    }
//                    int backStackEntry = getActivity().getSupportFragmentManager().getBackStackEntryCount();
//                    if (backStackEntry > 0) {
//                        for (int i = 0; i < 2; i++) {
//                            getActivity().getSupportFragmentManager().popBackStackImmediate();
//                        }
//                    }
//                }
//                else {
//                    Log.d(TAG, "signupSuccess: "+"} else {");
//                      // startActivity(new Intent(getActivity(), OtpVerifyActivity.class));
          //  verifyPhoneNumber(data.getData().getPhoneNo(),data.getData().getCountryCode());
//                }
//            }
        }

    }



    private void verifyPhoneNumber(String phoneNumber, String countrycode) {
        setLoading(true);
        android.util.Log.e(TAG, "verifyPhoneNumber: "+phoneNumber +" Country code "+ countrycode);

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
                    Toast.makeText(requireContext(), getString(R.string.invalid_otp), Toast.LENGTH_SHORT).show();
                    // Invalid request
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    android.util.Log.e(TAG, "onVerificationFailed: FirebaseTooManyRequestsException ",e );
                    Toast.makeText(requireContext(),getString(R.string.multiple_time_submit_otp), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                setLoading(false);
                Log.d(TAG, "onCodeSent:" + verificationId);
                Prefs.with(requireContext()).save(Constants.FIREBASE_AUTH_VERIFICATION_ID,verificationId);
                Prefs.with(requireContext()).save(Constants.FIREBASE_AUTH_RESEND_TOKEN,token);
                Intent intent = new Intent(getActivity(), OtpVerifyActivity.class);
                intent.putExtra("PhoneNumber",countrycode+phoneNumber);
                startActivity(intent);
            }
        };

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(countrycode+phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(requireActivity())                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                .setForceResendingToken(Prefs.with(getContext()).getObject(Constants.FIREBASE_AUTH_RESEND_TOKEN, PhoneAuthProvider.ForceResendingToken.class))// OnVerificationStateChangedCallbacks

                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }




    @Override
    public void billingInfoSuccess(ApiResponse<PojoLogin> data) {
        Prefs.with(getContext()).save(Constants.DATA, data.getData());
        Prefs.with(getContext()).save(Constants.ACCESS_TOKEN, "bearer " + data.getData().accessToken);
        final String googleApiKey = getString(R.string.google_api_key_mak);
        if (booking_type == 3) {
            presenter.getTimeZoneFromLatLong(searchMaidBulkModel.lat, searchMaidBulkModel.lng, googleApiKey);
        } else {
            presenter.getTimeZoneFromLatLong(searchMaidModel.lat, searchMaidModel.lng, googleApiKey);
        }
    }

    @Override
    public void bookServiceSuccess(PojoService data) {

        ((MaidBookActivity) getActivity()).step = 2;
        updateProgressStatus.changeProgressStatus(2);
        if (booking_type == 3) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                    .add(R.id.childframeLayout, CardListFragment.newInstance("", "", data.data.serviceId.get(0), searchMaidBulkModel, null, type), "CardListFragment").commit();

        } else {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                    .add(R.id.childframeLayout, CardListFragment.newInstance("", "", data.data.serviceId.get(0), searchMaidModel, null, type), "CardListFragment").commit();

        }
    }

    @Override
    public void bookServiceSuccessNew(PojoServiceNew pojoService) {
        Log.e("piipip", "" + new Gson().toJson(pojoService));
        final String serviceId = getArguments().getString(Constants.SERVICE_ID);
        Log.e("booking type", "" + booking_type+" "+serviceId);

        try{
            if(pojoService.data.enable3ds == null) {
                pojoService.data.enable3ds = false;
            }

                android.util.Log.e(TAG, "bookServiceSuccessNew: pojoService.data.enable3ds : "+ (pojoService.data.enable3ds instanceof Boolean) + "  "+ pojoService.data.enable3ds );
                if (pojoService.data.enable3ds instanceof Boolean) {
                    Prefs.with(requireContext()).save(Constants.ENABLE_3DS,pojoService.data.enable3ds );
                }

        }catch (Exception e){
            e.printStackTrace();
        }
        if (serviceId!=null){
            ((BookAgainActivity) getActivity()).step = 2;
            updateProgressStatus.changeProgressStatus(2);
            if (referenceId != null) {
                referenceId = pojoService.data.referenceId;
            }
            getActivity().getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                    .add(R.id.childframeLayout, CardListFragment.newInstance("", pojoService.data.referenceId, pojoService.data.serviceId, searchMaidModel, null, type,"BookAgainActivity"), "CardListFragment").commit();
        }else {
            ((MaidBookActivity) getActivity()).step = 2;
            updateProgressStatus.changeProgressStatus(2);
            if (referenceId != null) {
                referenceId = pojoService.data.referenceId;
            }
/*            getActivity().getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                    .add(R.id.childframeLayout, CardListFragment.newInstance("", pojoService.data.referenceId, pojoService.data.serviceId, searchMaidModel, null, type), "CardListFragment").commit();*/
            getActivity().getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                    .add(R.id.childframeLayout, CardListFragment.newInstance("", pojoService.data.referenceId, pojoService.data.serviceId, searchMaidModel, null, type,""), "CardListFragment").commit();
        }


    }//bookServiceSuccessNew

    @Override
    public void bookServiceSuccessBulk(PojoServiceNew pojoService) {

        Log.e("piipip y aya", "" + new Gson().toJson(pojoService));
        ((MaidBookActivity) getActivity()).step = 2;
        updateProgressStatus.changeProgressStatus(2);
        if (referenceId != null) {
            referenceId = pojoService.data.referenceId;
        }
/*        getActivity().getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                .add(R.id.childframeLayout, CardListFragment.newInstance("", pojoService.data.referenceId, pojoService.data.serviceId, searchMaidBulkModel, null, type), "CardListFragment").commit();*/
        getActivity().getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                .add(R.id.childframeLayout, CardListFragment.newInstance("", pojoService.data.referenceId, pojoService.data.serviceId, searchMaidBulkModel, null, type,""), "CardListFragment").commit();
    }

    public static String convertUsingStringBuilder(List<String> names) {
        StringBuilder namesStr = new StringBuilder();
        for (String name : names) {
            namesStr = namesStr.length() > 0 ? namesStr.append(",").append(name) : namesStr.append(name);
        }
        return namesStr.toString();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        imagePicker.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void updateProfileSuccess(PojoLogin data) {
        Prefs.with(getActivity()).save(Constants.DATA, data);
        Prefs.with(getActivity()).save(Constants.LOGIN_COUNTRY,data.getCountryENCode());
        Toast.makeText(getActivity(), R.string.profile_update_successfully, Toast.LENGTH_SHORT).show();
        getActivity().finish();
    }

    @Override
    public void bookingTimeZoneReceived(@Nullable TimeZone timeZone) {
        if (timeZone == null) {
            Toast.makeText(getActivity(), R.string.unable_to_locate_time_zone, Toast.LENGTH_SHORT).show();
        } else {
            if (booking_type == 3) {
                bookServiceBulkData(timeZone);
            } else {
                bookServiceData(timeZone);
            }

        }
    }

    @Override
    public void signupError(String errorMessage) {

        if (errorMessage.equalsIgnoreCase(getActivity().getString(R.string.continue_with_your_booking))){

            IOSAlertDialog dialog =  IOSAlertDialog.newInstance(
                    getActivity(),
                    null,
                    errorMessage,
                    getString(R.string.ok),
                    null,
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Prefs.with(getActivity()).save(Constants.ISBACK,"true");
                            getActivity().getSupportFragmentManager().popBackStack();
                        }
                    },
                    null,
                     ContextCompat.getColor(getActivity(), R.color.app_color),
                    0,
                    false
            );

            dialog.show(requireActivity().getSupportFragmentManager(), "ios_dialog");


       /*     Dialog dialog  = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
            dialog.setContentView(R.layout.dialog_popup);
            WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
            lp.dimAmount = 0.6f;
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            TextView header = dialog.findViewById(R.id.header);
            TextView text = dialog.findViewById(R.id.text);
            Button ok = dialog.findViewById(R.id.ok);
            ok.setText(getString(R.string.ok));
            Button cancel = dialog.findViewById(R.id.cancel);
            cancel.setVisibility(View.GONE);
            text.setText(errorMessage);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    Prefs.with(getActivity()).save(Constants.ISBACK,"true");
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            });
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    Prefs.with(getActivity()).save(Constants.ISBACK,"true");
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            });
            dialog.show();
*/

        }else {
            new DialogPopup().alertPopup(getActivity(), getResources().getString(R.string.dialog_alert), errorMessage, "").show(requireActivity().getSupportFragmentManager(), "ios_dialog");
        }
    }

    @Override
    public void signupFailure(String failureMessage) {
        if (failureMessage.equalsIgnoreCase(getActivity().getString(R.string.continue_with_your_booking))){
            Dialog dialog  = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
            dialog.setContentView(R.layout.dialog_popup);
            WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
            lp.dimAmount = 0.6f;
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            TextView header = dialog.findViewById(R.id.header);
            TextView text = dialog.findViewById(R.id.text);
            Button ok = dialog.findViewById(R.id.ok);
            ok.setText(getString(R.string.ok));
            Button cancel = dialog.findViewById(R.id.cancel);
            cancel.setVisibility(View.GONE);
            text.setText(failureMessage);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    Prefs.with(getActivity()).save(Constants.ISBACK,"true");
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            });
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    Prefs.with(getActivity()).save(Constants.ISBACK,"true");
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            });
            dialog.show();
        }else {
            new DialogPopup().alertPopup(getActivity(), getResources().getString(R.string.dialog_alert), failureMessage, "").show(requireActivity().getSupportFragmentManager(), "ios_dialog");
        }


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.detachView();
    }

    public static List<Date> getDatesBetweenUsingJava7(Date startDate, Date endDate) {
        List<Date> datesInRange = new ArrayList<>();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(startDate);

        Calendar endCalendar = new GregorianCalendar();
        endCalendar.setTime(endDate);
        endCalendar.add(Calendar.DATE, 1);
        while (calendar.before(endCalendar)) {
            Date result = calendar.getTime();
            datesInRange.add(result);
            calendar.add(Calendar.DATE, 1);
        }
        return datesInRange;
    }

    private void bookServiceData(TimeZone bookingTimeZone) {
        Timestamp ts = new Timestamp(searchMaidModel.workDate);
        Date date = new Date(ts.getTime());
        System.out.println(date);
        Log.e("start date", date + "");

        Timestamp tss = new Timestamp(searchMaidModel.endDate);
        Date datee = new Date(tss.getTime());
        System.out.println(datee);
        Log.e("end date", datee + "");
        Log.e("dates", getDatesBetweenUsingJava7(date, datee) + "");
        Log.e("services",  ServiceFragment.servicesID+ "==="+searchMaidModel.servicesID);

        BookServiceModel bookServiceModel = new BookServiceModel();
        bookServiceModel.uniquieAppKey = Constants.UNIQUE_APP_KEY;
        bookServiceModel.lng = searchMaidModel.lng;
        bookServiceModel.lat = searchMaidModel.lat;

        if(!ServiceFragment.servicesID.isEmpty()){
           bookServiceModel.services=ServiceFragment.servicesID;
        }
        if (searchMaidModel.servicesID!=null){
            bookServiceModel.services=searchMaidModel.servicesID;
        }


        if (searchMaidModel.maidId != null) {
            bookServiceModel.maidId = searchMaidModel.maidId;
        }


        bookServiceModel.timeZone = bookingTimeZone.getID();
        bookServiceModel.deviceTimeZone = TimeZone.getDefault().getID();
        //bookServiceModel.amount = ConfirmBookFragment.Discounted_price;
        bookServiceModel.amount = ConfirmBookFragment.totalper;
        bookServiceModel.promoId = ConfirmBookFragment.Promo_id;
        bookServiceModel.promoDiscount = String.valueOf(ConfirmBookFragment.Promo_discount);
        BookServiceModel.ServiceData serviceData = null;

        if (booking_type == 2) {
            String mytime = GeneralFunction.getFormatFromDate(HomeFragment.selectedDatesList.get(HomeFragment.selectedDatesList.size() - 1), "dd MMMM yy") + " " + searchMaidModel.selectedTime;
            Log.e("mytimeff", mytime);

            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    "dd MMMM yy hh:mm a", Locale.ENGLISH);

            Date myDate = null;
            try {
                myDate = dateFormat.parse(mytime);

            } catch (ParseException e) {
                e.printStackTrace();
            }
            Log.e("f dateff", myDate.toString());
            Collections.reverse(selectedDatesList);
            for (int i = 0; i < selectedDatesList.size(); i++) {
                //   Timestamp tsss = new Timestamp(getDatesBetweenUsingJava7(date, datee).get(i).getTime());
                serviceData = new BookServiceModel.ServiceData();

                serviceData.workDate = String.valueOf(selectedDatesList.get(i).getTime());
                serviceData.duration = searchMaidModel.duration;

                String mytimee = GeneralFunction.getFormatFromDate(HomeFragment.selectedDatesList.get(i), "dd MMMM yy") + " " + searchMaidModel.selectedTime;
                Log.e("mytimeff", mytime);

                SimpleDateFormat dateFormatt = new SimpleDateFormat("dd MMMM yy hh:mm a", Locale.ENGLISH);

                Date myDatee = null;
                try {
                    myDatee = dateFormatt.parse(mytimee);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                //   serviceData.hour = String.valueOf(myDatee.getTime());
                serviceData.hour = String.valueOf(GeneralFunction.getTimeInTimeZone(myDatee, bookingTimeZone).getTime());

                serviceData.startTime = String.valueOf(myDatee.getTime());
                bookServiceModel.serviceData.add(serviceData);

            }
            // bookServiceModel.hour = String.valueOf(GeneralFunction.getTimeInTimeZone(selectedDatesList.get(0), bookingTimeZone).getTime());
            bookServiceModel.hour = String.valueOf(GeneralFunction.getTimeInTimeZone(myDate, bookingTimeZone).getTime());

            // bookServiceModel.hour = String.valueOf(myDate.getTime());
        } else if (booking_type == 1) {

            bookServiceModel.hour = String.valueOf(GeneralFunction.getTimeInTimeZone(new Date(searchMaidModel.startTime), bookingTimeZone).getTime());
            serviceData = new BookServiceModel.ServiceData();
            serviceData.startTime = String.valueOf(searchMaidModel.startTime);
            serviceData.workDate = String.valueOf(searchMaidModel.workDate);
            serviceData.duration = searchMaidModel.duration;

            serviceData.hour = String.valueOf(GeneralFunction.getTimeInTimeZone(new Date(searchMaidModel.startTime), bookingTimeZone).getTime());

            bookServiceModel.serviceData.add(serviceData);

        }

        if (searchMaidModel.additionAddress != null && !searchMaidModel.additionAddress.isEmpty()) {
            bookServiceModel.moreDetailedaddress = searchMaidModel.additionAddress;
        }

        if (Prefs.with(getActivity()).getString(Constants.COUNTRY_NAME, "").contains("United Arab Emirates")) {
            bookServiceModel.currency = "AED";
        } else {
            bookServiceModel.currency = "BHD";
        }

        FullAddress fullAddress = Prefs.with(getActivity()).getObject(Constants.MAP_FULL_ADDRESS, FullAddress.class);
        fullAddress.mapLatLng = null;
        fullAddress.address = null;
        fullAddress.lat = null;
        fullAddress.lng = null;
        fullAddress.id = null;
        if (fullAddress.moreDetailedaddress != null && fullAddress.moreDetailedaddress.isEmpty()) {
            fullAddress.moreDetailedaddress = null;
        }
        if (searchMaidModel.locationName.isEmpty()){
            bookServiceModel.locationName=fullAddress.streetName;
        }else {
            bookServiceModel.locationName = searchMaidModel.locationName;
        }
        bookServiceModel.address = fullAddress;
       /* Prefs.with(getActivity()).save(USER_COUNTRY,"India");*/
        Log.e("Final data param", "" + new Gson().toJson(bookServiceModel));
        presenter.apiBookService(bookServiceModel);

    }

    private void bookServiceBulkData(TimeZone bookingTimeZone) {
        Timestamp ts = new Timestamp(searchMaidBulkModel.workDate);
        Date date = new Date(ts.getTime());
        System.out.println(date);
        Log.e("start date", date + "");

        Timestamp tss = new Timestamp(searchMaidBulkModel.workDate);
        Date datee = new Date(tss.getTime());
        System.out.println(datee);
        Log.e("end date", datee + "");
        Log.e("dates", getDatesBetweenUsingJava7(date, datee) + "");

        BookServiceBulkModel bookServiceModel = new BookServiceBulkModel();
        bookServiceModel.uniquieAppKey = Constants.UNIQUE_APP_KEY;
        bookServiceModel.lng = searchMaidBulkModel.lng;
        bookServiceModel.lat = searchMaidBulkModel.lat;
        bookServiceModel.locationName = searchMaidBulkModel.locationName;

        if (searchMaidBulkModel.maidId != null) {
            bookServiceModel.maidId = searchMaidBulkModel.maidId;
        }
       /* if (ConfirmBookFragment.Promo_id.equalsIgnoreCase("")) {
            bookServiceModel.amount = searchMaidBulkModel.maidPrice * searchMaidBulkModel.duration * searchMaidBulkModel.maidCount;
        } else {
            bookServiceModel.amount = ConfirmBookFragment.Discounted_price;
        }*/
        Log.e("services",  ServiceFragment.servicesID+ " "+searchMaidBulkModel.servicesID);

        if(!ServiceFragment.servicesID.isEmpty()){
            bookServiceModel.services=ServiceFragment.servicesID;
        }
        bookServiceModel.amount=ConfirmBookFragment.totalper;
        bookServiceModel.promoId = ConfirmBookFragment.Promo_id;

        bookServiceModel.promoDiscount = String.valueOf(ConfirmBookFragment.Promo_discount);
        bookServiceModel.hour = String.valueOf(GeneralFunction.getTimeInTimeZone(new Date(searchMaidBulkModel.startTime), bookingTimeZone).getTime());
        bookServiceModel.timeZone = bookingTimeZone.getID();
        bookServiceModel.maidCount = String.valueOf(searchMaidBulkModel.maidCount);
        bookServiceModel.deviceTimeZone = TimeZone.getDefault().getID();
        BookServiceBulkModel.ServiceData serviceData = null;

        serviceData = new BookServiceBulkModel.ServiceData();
        serviceData.startTime = String.valueOf(searchMaidBulkModel.startTime);
        serviceData.workDate = String.valueOf(searchMaidBulkModel.startTime);
        serviceData.duration = searchMaidBulkModel.duration;

        bookServiceModel.serviceData.add(serviceData);
        if (searchMaidBulkModel.additionAddress != null && !searchMaidBulkModel.additionAddress.isEmpty()) {
            bookServiceModel.moreDetailedaddress = searchMaidBulkModel.additionAddress;
        }

        if (Prefs.with(getActivity()).getString(Constants.COUNTRY_NAME, "").contains("United Arab Emirates")) {
            bookServiceModel.currency = "AED";
        } else {
            bookServiceModel.currency = "BHD";
        }

        FullAddress fullAddress = Prefs.with(getActivity()).getObject(Constants.MAP_FULL_ADDRESS, FullAddress.class);
        fullAddress.mapLatLng = null;
        fullAddress.address = null;
        fullAddress.lat = null;
        fullAddress.lng = null;
        fullAddress.id = null;
        if (fullAddress.moreDetailedaddress != null && fullAddress.moreDetailedaddress.isEmpty()) {
            fullAddress.moreDetailedaddress = null;
        }

        bookServiceModel.address = fullAddress;
        Log.e("Final data param", "" + new Gson().toJson(bookServiceModel));
        presenter.apiBulkBookService(bookServiceModel);

    }

    // @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkForImagePermision() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_CODE_ASK_PERMISSIONS);
        } else {
            imagePicker.showImagePicker();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //  requestPermissions.onRequestPermissionsResult(getActivity(),requestCode,permissions,grantResults);
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    imagePicker.showImagePicker();

                } else if (permissions.length > 0) {
                    if (shouldShowRequestPermissionRationale(permissions[0])) {
                        showDialog(getString(R.string.storage_permission_msg),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which) {
                                            case DialogInterface.BUTTON_POSITIVE:
                                                checkForImagePermision();
                                                break;
                                        }
                                    }
                                }, Constants.DENY);
                    } else {
                        showDialog(getString(R.string.storage_permission_msg),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which) {
                                            case DialogInterface.BUTTON_POSITIVE:
                                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                Uri uri = Uri.fromParts(Constants.SETTING_URI_SCHEME,
                                                        getActivity().getPackageName(), null);
                                                intent.setData(uri);
                                                startActivityForResult(intent, Constants.REQUEST_CODE_SETTINGS);
                                                break;
                                        }
                                    }
                                }, Constants.NEVER_ASK);
                    }
                }
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onImageSelectedFromPicker(File imageFile) {
        AmazoneS3 amazoneS3 = new AmazoneS3(getActivity());
        url = amazoneS3.setFileToUpload(imageFile);
        Log.e("final url", url);
        Glide.with(getActivity())
                .load(imageFile)
                .into(binding.ivUploadImage);

        binding.tvUploadImage.setVisibility(View.GONE);
        binding.ivUploadImage.setVisibility(View.GONE);

    }

    private void showDialog(String message, DialogInterface.OnClickListener okListener, String from) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity())
                .setMessage(message).setCancelable(false);


        if (from.equals(Constants.DENY)) {
            alertDialog.setTitle(R.string.title_permission_dialog)
                    .setPositiveButton(R.string.button_ok_permission_dialog, okListener)
                    .create()
                    .show();
        } else if (from.equals(Constants.NEVER_ASK)) {
            alertDialog.setTitle(R.string.title_permission_dialog)
                    .setPositiveButton(R.string.button_setting_permission_dialog, okListener)
                    .create()
                    .show();
        }

    }

}
