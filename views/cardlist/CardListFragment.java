package com.maktoday.views.cardlist;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.github.kittinunf.fuel.Fuel;
import com.github.kittinunf.fuel.core.FuelError;
import com.github.kittinunf.fuel.core.Handler;
import com.github.kittinunf.fuel.core.Headers;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.maktoday.Config.Config;
import com.maktoday.R;
import com.maktoday.adapter.CardListAdapter;
import com.maktoday.databinding.FragmentCardListBinding;
import com.maktoday.interfaces.CardListUpdate;
import com.maktoday.interfaces.UpdateProgressStatus;
import com.maktoday.model.FullAddress;
import com.maktoday.model.PaytabTransactionVerificationResponse;
import com.maktoday.model.PojoCardList;
import com.maktoday.model.PojoCreatePayment;
import com.maktoday.model.PojoLogin;
import com.maktoday.model.SearchMaidBulkModel;
import com.maktoday.model.SearchMaidModel;
import com.maktoday.model.TimeSlot;

import com.maktoday.stripe.StripePaymentActivity;
import com.maktoday.utils.BaseFragment;
import com.maktoday.utils.Constants;
import com.maktoday.utils.DialogPopup;
import com.maktoday.utils.GeneralFunction;
import com.maktoday.utils.Log;
import com.maktoday.utils.Prefs;
import com.maktoday.views.AllService.ServiceFragment;
import com.maktoday.views.WebViewActivity;
import com.maktoday.views.bookagain.BookAgainActivity;
import com.maktoday.views.confirmbook.ConfirmBookFragment;
import com.maktoday.views.maidbook.MaidBookActivity;
import com.maktoday.views.paymentinfo.PaymentInfoFragment;
import com.maktoday.views.slotavailable.SlotAvailbleActivity;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.Stripe;
import com.stripe.android.googlepaylauncher.GooglePayLauncher;
import com.stripe.android.model.PaymentIntent;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;
import com.stripe.android.view.CardMultilineWidget;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.maktoday.utils.Constants.USER_COUNTRY;
import static com.maktoday.views.home.HomeFragment.booking_type;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by cbl81 on 23/11/17.
 */

public class CardListFragment extends BaseFragment implements View.OnClickListener, CardListContract.View, CardListUpdate {
    private static final String TAG = "CardListFragment";
    private static final int REQ_CODE = 2;
    private static String amount = "";
    private static final int PAYMENT_REQ_CODE = 1;
    private FragmentCardListBinding binding;
    private CardListContract.Presenter presenter;
    private CardListAdapter cardListAdapter;
    private List<PojoCardList.Data> cardList = new ArrayList<>();
    private PojoCardList.Data selectedCard;
    private String cardId = "";
    public EditText etCvv;
    private String cardType = "";
    public static SearchMaidModel searchMaidModel;
    public static SearchMaidBulkModel searchMaidBulkModel;
    private UpdateProgressStatus updateProgressStatus;
    private String transactionId = "";
    private Boolean isExistingCustomerCard = false;
    private Boolean isCreditCard = false;
    private boolean isExtend, bookAgain;     //to differentiate two flow one by direct payment and other one bt extend service;
    private String pt_token = "";
    public static String customerEmail = "";
    public static String customerPassword = "";
    public static String serviceId = "";
    public static String referenceId = "";
    public static String paymentMode = "";
    public static String type;
    public static String currency = "";
    public static String bookingType_page = " ";
    private GooglePayLauncher googlePayLauncher;
    private Stripe stripe;
    private CardMultilineWidget mCardMultilineWidget;


    /// Stripe variables
    PaymentSheet paymentSheet;
    String paymentIntentClientSecret;
    PaymentSheet.CustomerConfiguration customerConfig;
    ///

    public static CardListFragment newInstance(String payment_mode, String referenceId, String serviceId, SearchMaidModel searchMaidModel, TimeSlot timeSlot, String type) {
        return newInstance(payment_mode, referenceId, serviceId, searchMaidModel, false, false, timeSlot, type);
    }


    public static CardListFragment newInstance(String payment_mode, String referenceId, String serviceId, SearchMaidBulkModel searchMaidModel, TimeSlot timeSlot, String type) {
        return newInstance(payment_mode, referenceId, serviceId, searchMaidModel, false, false, timeSlot, type);
    }

    //// Book again payment flow
    public static CardListFragment newInstance(String payment_mode, String referenceIdd, String serviceId, SearchMaidModel searchMaidModel, TimeSlot timeSlot, String type, String BookingType) {

        referenceId = referenceIdd;
        paymentMode = payment_mode;
        bookingType_page = BookingType;
        Bundle args = new Bundle();
        args.putString(Constants.SERVICE_ID, serviceId);
        args.putString("referenceId", referenceIdd);
        args.putString(Constants.TYPE, type);
        args.putParcelable(Constants.SEARCH_MAID_DATA, searchMaidModel);
        args.putBoolean(Constants.ISEXTEND, false);
        args.putBoolean(Constants.BOOK_AGAIN, false);
        if (timeSlot != null)
            args.putParcelable(Constants.MAID_AVAILABLE_TIMESLOT, timeSlot);
        CardListFragment fragment = new CardListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static CardListFragment newInstance(String payment_mode, String referenceIdd, String serviceId, SearchMaidBulkModel searchMaidModel, TimeSlot timeSlot, String type, String BookingType) {
        referenceId = referenceIdd;
        paymentMode = payment_mode;
        bookingType_page = BookingType;
        Bundle args = new Bundle();
        args.putString(Constants.SERVICE_ID, serviceId);
        args.putString("referenceId", referenceIdd);
        args.putString(Constants.TYPE, type);
        args.putParcelable(Constants.SEARCH_MAID_DATA, searchMaidModel);
        args.putBoolean(Constants.ISEXTEND, false);
        args.putBoolean(Constants.BOOK_AGAIN, false);
        if (timeSlot != null)
            args.putParcelable(Constants.MAID_AVAILABLE_TIMESLOT, timeSlot);
        CardListFragment fragment = new CardListFragment();
        fragment.setArguments(args);
        return fragment;
    }


    public static CardListFragment newInstance(String payment_mode, String referenceIdd, String serviceId, SearchMaidModel searchMaidModel, boolean isExtend, boolean bookAgain, TimeSlot timeSlot, String type) {
        referenceId = referenceIdd;
        paymentMode = payment_mode;
        Bundle args = new Bundle();
        args.putString(Constants.SERVICE_ID, serviceId);
        args.putString("referenceId", referenceIdd);
        args.putString(Constants.TYPE, type);
        args.putParcelable(Constants.SEARCH_MAID_DATA, searchMaidModel);
        args.putBoolean(Constants.ISEXTEND, isExtend);
        args.putBoolean(Constants.BOOK_AGAIN, bookAgain);
        if (timeSlot != null)
            args.putParcelable(Constants.MAID_AVAILABLE_TIMESLOT, timeSlot);
        CardListFragment fragment = new CardListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static CardListFragment newInstance(String payment_mode, String referenceIdd, String serviceId, SearchMaidBulkModel searchMaidModel, boolean isExtend, boolean bookAgain, TimeSlot timeSlot, String type) {
        referenceId = referenceIdd;
        paymentMode = payment_mode;
        Bundle args = new Bundle();
        Log.e("rrr", "" + referenceIdd + "," + referenceId);
        args.putString(Constants.SERVICE_ID, serviceId);
        args.putString("referenceId", referenceIdd);
        args.putString(Constants.TYPE, type);
        args.putParcelable(Constants.SEARCH_MAID_DATA, searchMaidModel);
        args.putBoolean(Constants.ISEXTEND, isExtend);
        args.putBoolean(Constants.BOOK_AGAIN, bookAgain);
        if (timeSlot != null)
            args.putParcelable(Constants.MAID_AVAILABLE_TIMESLOT, timeSlot);
        CardListFragment fragment = new CardListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (booking_type == 3) {
            searchMaidBulkModel = getArguments().getParcelable(Constants.SEARCH_MAID_DATA);
        } else {
            searchMaidModel = getArguments().getParcelable(Constants.SEARCH_MAID_DATA);
        }
        bookAgain = getArguments().getBoolean(Constants.BOOK_AGAIN);
        type = getArguments().getString(Constants.TYPE, "");
        stripe = new Stripe(getActivity(), Config.PUBLISH_KEY);

    }

    String language;

    @Override
    public void onResume() {
        super.onResume();
        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            binding.radioCard.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
            binding.radioCash.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);

        } else {
            binding.radioCard.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
            binding.radioCash.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
        }
        language = Prefs.with(getActivity()).getString(Constants.LANGUAGE_CODE, "");
        if (language.equalsIgnoreCase("ar")) {
            binding.radioGroup.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            binding.radioCard.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
            binding.radioCash.setGravity(Gravity.DISPLAY_CLIP_HORIZONTAL | Gravity.CENTER_VERTICAL);

        } else {
            binding.radioCard.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            binding.radioCash.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }

       /* if (booking_type == 3) {
            binding.radioCash.setVisibility(View.GONE);
            binding.cashIcon.setVisibility(View.GONE);
        } else if (booking_type == 2) {
            binding.radioCash.setVisibility(View.GONE);
            binding.cashIcon.setVisibility(View.GONE);
        } else {
            if (Prefs.get().getString(USER_COUNTRY,"").equalsIgnoreCase("Bahrain")){
                binding.radioCash.setVisibility(View.VISIBLE);
                binding.cashIcon.setVisibility(View.VISIBLE);
            }else {
                binding.radioCash.setVisibility(View.GONE);
                binding.cashIcon.setVisibility(View.GONE);
            }
        }*/

        Log.e("Config.TRANSACTION_ID", " " + Config.TRANSACTION_ID);
        if (Config.TRANSACTION_ID.equalsIgnoreCase("")) {
            serviceId = getArguments().getString(Constants.SERVICE_ID);
        } else {
            setLoading(true);
            HashMap<String, String> hashMap = new HashMap<>();

            hashMap.put("uniquieAppKey", Constants.UNIQUE_APP_KEY);
            hashMap.put("serviceId", serviceId);
            hashMap.put("transactionId", Config.TRANSACTION_ID);
            hashMap.put("tapId", "");

            /*Config.TRANSACTION_ID = "";
            Config.TAP_ID = "";*/
            hashMap.put("cardType", "2");
            hashMap.put("referenceId", referenceId);

            hashMap.put("promoId", ConfirmBookFragment.Promo_id);
            hashMap.put("transactionStatus", "true");
               /* if (bookAgain) {
                    hashMap.put("isExtension", String.valueOf(false));
                } else*/
            if (isExtend) {
                hashMap.put("isExtension", String.valueOf(true));
            } else {
                hashMap.put("isExtension", String.valueOf(false));
            }
            ////////Bulk booking ===========================================
            if (booking_type == 3) {

                if (GeneralFunction.isNetworkConnected(getActivity(), binding.parent)) {
                    presenter.apiCardPaymentPaytabsBulk(hashMap);
                }

            } else {
                ////////single and multiple booking ===========================================
                if (GeneralFunction.isNetworkConnected(getActivity(), binding.parent)) {
                    presenter.apiCardPaymentPaytabs(hashMap);
                }
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCardListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        paymentSheet = new PaymentSheet(this, this::onPaymentSheetResult);
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: StartActivity");
        Log.d(TAG, "Type: " + type);
        if (booking_type == 3) {
            currency = searchMaidBulkModel.currency;
        } else {
            currency = searchMaidModel.currency;
        }

        init();
        setData();
        callChangeLanguageApi();
        setListeners();


        //binding.radioCard.setChecked(true);
        binding.radioCard.setEnabled(true);
        binding.radioCash.setEnabled(true);

    }



    void clickMakePaymentForZeroAmount(){
       try {
           if (isExtend) {
           } else {
               amount = String.format(Locale.ENGLISH, "%.3f", ConfirmBookFragment.totalper);
           }
//----------------------------End Amount ------------///////////////////////////////////////////////////////////////////////////

//------------------------If amount is 0
           if (Double.parseDouble(amount) <= 0) {
               binding.tvMakePayment.callOnClick();
           }
       }catch (Exception e){
           e.printStackTrace();
       }
    }

    private void callChangeLanguageApi() {
        String language = Prefs.with(getActivity()).getString(Constants.LANGUAGE_CODE, "en");
        if (GeneralFunction.isNetworkConnected(getActivity(), binding.tvMakePayment)) {
            if (language.equals("en")) {
                presenter.apiChangeLanguage("EN");
            } else {
                presenter.apiChangeLanguage("AR");
            }
        }
    }



    /**
     * intialization
     */
    private void init() {

        binding.parent.setVisibility(View.GONE);
        presenter = new CardListPresenter();
        presenter.attachView(this);
        isExtend = getArguments().getBoolean(Constants.ISEXTEND);
//        googlePayLauncher = new GooglePayLauncher(this,new GooglePayLauncher.Config(
//                GooglePayEnvironment.Test,
//
//
//        ))

        cardListAdapter = new CardListAdapter(getActivity(), cardList, this, isExtend, currency);
        binding.rvCardList.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvCardList.setAdapter(cardListAdapter);
        binding.rvCardList.setNestedScrollingEnabled(false);
        if (!isExtend) {
            updateProgressStatus = (UpdateProgressStatus) getActivity().getSupportFragmentManager().findFragmentByTag("PaymentStateFragment");
        }
    }

    /**
     * set data on views
     */
    private void setData() {


        //Prefs.get().getString(USER_COUNTRY,"").equalsIgnoreCase("BH")
        if (currency.equalsIgnoreCase("BHD")) {
            binding.parent.setVisibility(View.VISIBLE);
            binding.tvMakePayment.setVisibility(View.VISIBLE);
            binding.tvGpay.setVisibility(View.GONE);
            PojoCardList.Data data = new PojoCardList.Data();
            if (booking_type == 3) {
                data = new PojoCardList.Data();
                data.set_id("1");
                data.setCardBrand("Credit/debit");
                data.setCardFingerPrint("credit/debit");
                data.setCardType(getString(R.string.credit_debit));
                data.setCardNumber("1");
                cardList.add(0, data);
            } else if (booking_type == 2) {
                data = new PojoCardList.Data();
                data.set_id("1");
                data.setCardBrand("Credit/debit");
                data.setCardFingerPrint("credit/debit");
                data.setCardType(getString(R.string.credit_debit));
                data.setCardNumber("1");
                cardList.add(0, data);
            } else {
                data.set_id("0");
                data.setCardBrand("cash");
                data.setCardFingerPrint("cash");
                data.setCardNumber("0");
                data.setCardType(getString(R.string.cash_carlist));
                cardList.add(0, data);
                data = new PojoCardList.Data();
                data.set_id("1");
                data.setCardBrand("Credit/debit");
                data.setCardFingerPrint("credit/debit");
                data.setCardType(getString(R.string.credit_debit));
                data.setCardNumber("1");
                cardList.add(1, data);
            }
        } else {
            binding.parent.setVisibility(View.VISIBLE);
            binding.tvMakePayment.setVisibility(View.VISIBLE);
            binding.tvGpay.setVisibility(View.VISIBLE);
            //for now hide gpay text
            binding.tvGpay.setVisibility(View.GONE);
            presenter.apiCardList();
        }

        if (!isExtend) {
            if (updateProgressStatus != null)
                if (bookingType_page.isEmpty()) {
                    updateProgressStatus.changeProgressStatus(((MaidBookActivity) getActivity()).step);
                } else {
                    updateProgressStatus.changeProgressStatus(((BookAgainActivity) getActivity()).step);
                }
        }
    }

    /**
     * setListeners
     */
    private void setListeners() {
        binding.tvAddCard.setOnClickListener(this);
        binding.tvMakePayment.setOnClickListener(this);
        binding.tvGpay.setOnClickListener(this);
        clickMakePaymentForZeroAmount();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.tvGpay:

                break;


            case R.id.tvAddCard:
                if (booking_type == 3) {
                    // amount = String.format(Locale.ENGLISH, "%.3f", searchMaidBulkModel.maidPrice * searchMaidBulkModel.duration);
                    try {
                        amount = String.format(Locale.ENGLISH, "%.3f", ConfirmBookFragment.totalper);
                    }catch (Exception e){
                        e.printStackTrace();
                        try {//----------- IF FIREBASE RETURN EXCEPTION
                            FirebaseCrashlytics.getInstance().recordException(e);
                        } catch (Exception fe) {
                            fe.printStackTrace();
                        }
                    }
                } else {
                    // amount = String.format(Locale.ENGLISH, "%.3f", searchMaidModel.maidPrice * searchMaidModel.duration);
                    try {
                        amount = String.format(Locale.ENGLISH, "%.3f", ConfirmBookFragment.totalper);
                    }catch (Exception e){
                        e.printStackTrace();
                        try {//----------- IF FIREBASE RETURN EXCEPTION
                            FirebaseCrashlytics.getInstance().recordException(e);
                        } catch (Exception fe) {
                            fe.printStackTrace();
                        }
                    }
                }
                if (Prefs.get().getString(USER_COUNTRY, "").equalsIgnoreCase("United Kingdom")) {
                    String fullName = "";
                    String mobile ="";
                    String email = "";
                    String service ="";
                    String currency_code = "";
                    PojoLogin pojoLogin = Prefs.with(getContext()).getObject(Constants.DATA, PojoLogin.class);
                   Intent intent = new Intent(getActivity(), StripePaymentActivity.class);

                    fullName = pojoLogin.fullName;
                    mobile =pojoLogin.getCountryCode() + pojoLogin.getPhoneNo();
                    email =  pojoLogin.email;

                    intent.putExtra("name", pojoLogin.fullName);
                    intent.putExtra("mobile", pojoLogin.getCountryCode() + pojoLogin.getPhoneNo());
                    intent.putExtra("address", pojoLogin.email);


                    intent.putExtra("amount", amount);


                    if (booking_type == 3) {
                        service = searchMaidBulkModel.maidName;
                        currency_code = searchMaidBulkModel.currency;
                       intent.putExtra("service", searchMaidBulkModel.maidName);
                        intent.putExtra("currency_code", searchMaidBulkModel.currency);

                    } else {
                        service =searchMaidModel.maidName;
                        currency_code =  searchMaidModel.currency;
                       intent.putExtra("service", searchMaidModel.maidName);
                        intent.putExtra("currency_code", searchMaidModel.currency);
                    }

                    try {
                        android.util.Log.e(TAG, "onClick: Enable3ds value : " + Prefs.with(requireContext()).getBoolean(Constants.ENABLE_3DS, false));
                        if(Prefs.with(requireContext()).getBoolean(Constants.ENABLE_3DS,false)){
                            payUsingStripe(fullName,mobile,email,service,currency_code,serviceId);
                        }else{
                            startActivity(intent);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    try {
                        android.util.Log.e(TAG, "onClick: Enable3ds value : str " + Prefs.with(requireContext()).getString(Constants.ENABLE_3DS, ""));
                        if(Objects.equals(Prefs.with(requireContext()).getString(Constants.ENABLE_3DS, ""), "true")){
                            android.util.Log.e(TAG, "onClick: true string" );
                            payUsingStripe(fullName,mobile,email,service,currency_code,serviceId);
                        }else{
                            startActivity(intent);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                } else {
                    PojoLogin pojoLogin = Prefs.with(getContext()).getObject(Constants.DATA, PojoLogin.class);

                    Intent intent = new Intent(getActivity(), WebViewActivity.class);
                    intent.putExtra("name", pojoLogin.fullName);
                    intent.putExtra("mobile", pojoLogin.getCountryCode() + pojoLogin.getPhoneNo());
                    intent.putExtra("address", pojoLogin.email);

                    if (ConfirmBookFragment.Promo_id != null && !ConfirmBookFragment.Promo_id.equalsIgnoreCase("")) {
                        intent.putExtra("amount", String.format(Locale.ENGLISH, "%.3f", ConfirmBookFragment.Discounted_price));
                    } else {
                        intent.putExtra("amount", amount);
                    }

                    if (booking_type == 3) {
                        intent.putExtra("service", searchMaidBulkModel.maidName);
                        intent.putExtra("currency_code", searchMaidBulkModel.currency);
                    } else {
                        intent.putExtra("service", searchMaidModel.maidName);
                        Log.d(TAG, "onClick: currency_code" + searchMaidModel.currency);
                        intent.putExtra("currency_code", searchMaidModel.currency);
                    }
                    startActivity(intent);
                }
                break;

            case R.id.tvMakePayment:
                //Card option is selected check
                // binding.radioCard.isChecked()
//------------------------------Amount -----//////////////////////////////////////////////////////////////////
                double totalper = 0.0;
                double per = 0.0;
                //   double totalValue=0.0;
                if (isExtend) {
                    if (booking_type == 3) {
                        amount = String.format(Locale.ENGLISH, "%.3f", searchMaidBulkModel.maidPrice * searchMaidBulkModel.duration * searchMaidBulkModel.maidCount);
                    } else {
                        amount = String.format(Locale.ENGLISH, "%.3f", searchMaidModel.maidPrice * searchMaidModel.duration);
                    }
                    per = (Float.parseFloat(amount) / 100.0) * Float.parseFloat(searchMaidModel.vat == null ? "0" : searchMaidModel.vat);
                    totalper = Float.parseFloat(amount) + per;
                    ConfirmBookFragment.totalper = totalper;
                } else {
                    amount = String.format(Locale.ENGLISH, "%.3f", ConfirmBookFragment.totalper);
                }
//----------------------------End Amount ------------///////////////////////////////////////////////////////////////////////////

 //------------------------If amount is 0
                if (Double.parseDouble(amount) <= 0) {

                    Log.e("ammount", "" + ConfirmBookFragment.totalper);
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("uniquieAppKey", Constants.UNIQUE_APP_KEY);
                    if(language == null){
                        language =   Prefs.with(getActivity()).getString(Constants.LANGUAGE_CODE, "en");
                    }

                    if (language.equalsIgnoreCase("EN")) {
                        hashMap.put("amount", String.format("%.2f", ConfirmBookFragment.totalper));
                    }
                    else {
                        hashMap.put("amount", String.valueOf(ConfirmBookFragment.totalper));
                    }
                    hashMap.put("saveCards", "true");
                    if (isExtend) {
                        hashMap.put("isExtension", String.valueOf(true));
                    }
                    else {
                        hashMap.put("isExtension", String.valueOf(false));
                    }
                    if(Objects.equals(serviceId, "")){
                        serviceId = getArguments().getString(Constants.SERVICE_ID);
                    }
                    hashMap.put("serviceId", serviceId);
                    hashMap.put("cardToken", "free");
                    android.util.Log.e("creatpayment param==", new Gson().toJson(hashMap));
                    presenter.apiCreatePayment(hashMap);

                }

   //---------------if amount is greater than 0
                else {

                    Log.d(TAG, "onClick: amount  " + amount);
                    if (cardType.equalsIgnoreCase("")) {
                        Toast.makeText(getActivity(), getString(R.string.select_payment_option), Toast.LENGTH_LONG).show();
                    } else if (cardType.equalsIgnoreCase(getString(R.string.credit_debit))) {
                        //payment card booking check
                        mFirebaseAnalytics.logEvent("paymscr_click_card", null);
                        // Bulk booking
                /*    if (booking_type == 3) {
                        amount = String.format(Locale.ENGLISH, "%.3f", searchMaidBulkModel.maidPrice * searchMaidBulkModel.duration * searchMaidBulkModel.maidCount);
                    }//Multiple booking
                    else if (booking_type == 2) {
                        amount = String.format(Locale.ENGLISH, "%.3f", searchMaidModel.maidPrice * searchMaidModel.duration * selectedDatesList.size());
                    } // one day booking
                    else {
                        amount = String.format(Locale.ENGLISH, "%.3f", searchMaidModel.maidPrice * searchMaidModel.duration);
                    }*/


                        //User country chk and open payment method
                        if (currency.equalsIgnoreCase("BHD")) {
                            PojoLogin pojoLogin = Prefs.with(getContext()).getObject(Constants.DATA, PojoLogin.class);
                            Intent intent = new Intent(getActivity(), WebViewActivity.class);
                            intent.putExtra("name", pojoLogin.fullName);
                            intent.putExtra("mobile", pojoLogin.getCountryCode() + pojoLogin.getPhoneNo());
                            intent.putExtra("address", pojoLogin.email);

                            intent.putExtra("amount", amount);
                            if (booking_type == 3) {
                                intent.putExtra("service", searchMaidBulkModel.maidName);
                                intent.putExtra("currency_code", searchMaidBulkModel.currency);
                            } else {
                                intent.putExtra("service", searchMaidModel.maidName);
                                intent.putExtra("currency_code", searchMaidModel.currency);
                            }
                            startActivity(intent);


                        } else {
                            String fullName = "";
                            String mobile ="";
                            String email = "";
                            String service ="";
                            String currency_code = "";
                            PojoLogin pojoLogin = Prefs.with(getContext()).getObject(Constants.DATA, PojoLogin.class);
                            Intent intent = new Intent(getActivity(), StripePaymentActivity.class);
                          fullName = pojoLogin.fullName;
                            mobile = pojoLogin.getCountryCode() + pojoLogin.getPhoneNo();
                            email = pojoLogin.email;
                            intent.putExtra("name", pojoLogin.fullName);
                            intent.putExtra("mobile", pojoLogin.getCountryCode() + pojoLogin.getPhoneNo());
                            intent.putExtra("address", pojoLogin.email);
                            intent.putExtra("serviceId", serviceId);

                            if (booking_type == 3) {
                                service = searchMaidBulkModel.maidName;
                                currency_code = searchMaidBulkModel.currency;
                                intent.putExtra("service", searchMaidBulkModel.maidName);
                                intent.putExtra("currency_code", searchMaidBulkModel.currency);
                            } else {
                                 service = searchMaidModel.maidName;
                                  currency_code = searchMaidModel.currency;
                                intent.putExtra("service", searchMaidModel.maidName);
                                intent.putExtra("currency_code", searchMaidModel.currency);
                            }

                            try {
                                android.util.Log.e(TAG, "onClick: Enable3ds value : " + Prefs.with(requireContext()).getBoolean(Constants.ENABLE_3DS, false));
                                if(Prefs.with(requireContext()).getBoolean(Constants.ENABLE_3DS,false)){
                                    payUsingStripe(fullName,mobile,email,service,currency_code,serviceId);
                                }else{
                                    startActivity(intent);
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            try {
                                android.util.Log.e(TAG, "onClick: Enable3ds value : str " + Prefs.with(requireContext()).getString(Constants.ENABLE_3DS, ""));
                                if(Objects.equals(Prefs.with(requireContext()).getString(Constants.ENABLE_3DS, ""), "true")){
                                    android.util.Log.e(TAG, "onClick: true string" );
                                    payUsingStripe(fullName,mobile,email,service,currency_code,serviceId);
                                }else{
                                    startActivity(intent);
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }

                        }
                    } else if (!cardType.equalsIgnoreCase(getString(R.string.credit_debit))
                            &&
                            !cardType.equalsIgnoreCase(getString(R.string.cash_carlist)))
                    {
                        if (isExtend) {
                            if (booking_type == 3) {
                                amount = String.format(Locale.ENGLISH, "%.3f", searchMaidBulkModel.maidPrice * searchMaidBulkModel.duration * searchMaidBulkModel.maidCount);
                            } else {
                                amount = String.format(Locale.ENGLISH, "%.3f", searchMaidModel.maidPrice * searchMaidModel.duration);
                            }
//                        android.util.Log.d(TAG, "onClick: amount:-- "+ amount);
//                        android.util.Log.d(TAG, "onClick: vat:-- card"+ searchMaidModel.vat);  /*vat contains new avt*/
                            per = (Float.parseFloat(amount) / 100.0) * Float.parseFloat(searchMaidModel.vat == null ? "0" : searchMaidModel.vat); /*vat contains new avt*/

                            totalper = Float.parseFloat(amount) + per;
                            ConfirmBookFragment.totalper = totalper;
                        }
                        Log.e("ammount", "" + ConfirmBookFragment.totalper);
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("uniquieAppKey", Constants.UNIQUE_APP_KEY);
                        if (language.equalsIgnoreCase("EN")) {
                            hashMap.put("amount", String.format("%.2f", ConfirmBookFragment.totalper));
                        }
                        else {
                            hashMap.put("amount", String.valueOf(ConfirmBookFragment.totalper));
                        }
                        hashMap.put("saveCards", "true");
                        if (isExtend) {
                            hashMap.put("isExtension", String.valueOf(true));
                        }
                        else {
                            hashMap.put("isExtension", String.valueOf(false));
                        }
                        hashMap.put("serviceId", serviceId);
                        hashMap.put("cardToken", selectedCard.getCardToken());
                        android.util.Log.e("creatpayment param==", new Gson().toJson(hashMap));
                        presenter.apiCreatePayment(hashMap);
                    }
                    else if (cardType.equalsIgnoreCase(getString(R.string.cash_carlist))) {
                        //Cash option is selected  check
                        mFirebaseAnalytics.logEvent("paymscr_click_cash", null);
                        Config.TRANSACTION_ID = "cash";
                        /////////////////transaction id check
                        if (Config.TRANSACTION_ID.equalsIgnoreCase("")) {
                            serviceId = getArguments().getString(Constants.SERVICE_ID);

                        } else {
                            // Bulk cash booking  type check
                            if (booking_type == 3) {
                                HashMap<String, String> hashMap = new HashMap<>();

                                hashMap.put("uniquieAppKey", Constants.UNIQUE_APP_KEY);
                                hashMap.put("serviceId", serviceId);
                                hashMap.put("paymentMode", "1");
                                hashMap.put("transactionId", Config.TRANSACTION_ID);
                                hashMap.put("tapId", Config.TAP_ID);
                                hashMap.put("cardType", "2");
                                hashMap.put("referenceId", referenceId);

                                hashMap.put("promoId", ConfirmBookFragment.Promo_id);
                                hashMap.put("transactionStatus", "true");
                                //Book again param  isExtension check
                                if (isExtend) {
                                    hashMap.put("isExtension", String.valueOf(true));
                                } else {
                                    hashMap.put("isExtension", String.valueOf(false));
                                }
                                //Bulk cash booking api check
                                if (GeneralFunction.isNetworkConnected(getActivity(), binding.parent)) {
                                    presenter.apiCardPaymentPaytabsBulk(hashMap);
                                }
                            } else {
                                // One Day booking cash check
                                HashMap<String, String> hashMap = new HashMap<>();

                                hashMap.put("uniquieAppKey", Constants.UNIQUE_APP_KEY);
                                hashMap.put("serviceId", serviceId);
                                hashMap.put("transactionId", Config.TRANSACTION_ID);
                                hashMap.put("tapId", Config.TAP_ID);
                                hashMap.put("cardType", "2");
                                hashMap.put("paymentMode", "1");
                                hashMap.put("referenceId", referenceId);
                                hashMap.put("promoId", ConfirmBookFragment.Promo_id);

                                hashMap.put("transactionStatus", "true");
                                if (isExtend) {
                                    hashMap.put("isExtension", String.valueOf(true));
                                } else {
                                    hashMap.put("isExtension", String.valueOf(false));
                                }
                                Log.e("isExtension", "" + isExtend);
                                /////////////////One Day booking cash booking api check
                                if (GeneralFunction.isNetworkConnected(getActivity(), binding.parent)) {
                                    presenter.apiCardPaymentPaytabs(hashMap);
                                }
                            }
                        }
                    }
                }

                break;


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
    public void payTabTransactionSuccess(PaytabTransactionVerificationResponse body) {
        if (body.getResponseCode().equals("100")) {
            if (type.equals("Guest")) {
                callApiCardPaymetPayTabs(isCreditCard);
            } else {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("uniquieAppKey", Constants.UNIQUE_APP_KEY);
                hashMap.put("cardNumber", body.getCardLastFourDigits());
                hashMap.put("customerEmail", customerEmail);
                hashMap.put("customerPassword", customerPassword);
                hashMap.put("cardToken", pt_token);
                presenter.apiAddPayment(hashMap);
            }
        } else {
            Toast.makeText(getActivity(), body.getResult(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void cardListSuccess(PojoCardList data) {
        cardList.clear();
     /*   if (data.getData().size() == 0) {
            binding.parent.setVisibility(View.VISIBLE);
            binding.tvMakePayment.setVisibility(View.VISIBLE);

        } else {
            binding.parent.setVisibility(View.VISIBLE);
            cardList.addAll(data.getData());

        }*/
        binding.parent.setVisibility(View.VISIBLE);
        if (currency.equalsIgnoreCase("GBP")) {
            PojoCardList.Data data1 = new PojoCardList.Data();
            data1.set_id("1");
            data1.setCardBrand("Credit/debit");
            data1.setCardFingerPrint("credit/debit");
            data1.setCardType(getString(R.string.credit_debit));
            data1.setCardNumber("1");
            cardList.add(0, data1);
            cardList.addAll(data.getData());
        } else {
            PojoCardList.Data data2 = new PojoCardList.Data();
            if (booking_type == 1) {
                data2.set_id("0");
                data2.setCardBrand("cash");
                data2.setCardFingerPrint("cash");
                data2.setCardNumber("0");
                data2.setCardType(getString(R.string.cash_carlist));
                cardList.add(0, data2);

                data2 = new PojoCardList.Data();
                data2.set_id("1");
                data2.setCardBrand("Credit/debit");
                data2.setCardFingerPrint("credit/debit");
                data2.setCardType(getString(R.string.credit_debit));
                data2.setCardNumber("1");
                cardList.add(1, data2);
                cardList.addAll(data.getData());
            } else {
                data2 = new PojoCardList.Data();
                data2.set_id("1");
                data2.setCardBrand("Credit/debit");
                data2.setCardFingerPrint("credit/debit");
                data2.setCardType(getString(R.string.credit_debit));
                data2.setCardNumber("1");
                cardList.add(0, data2);
                cardList.addAll(data.getData());
            }

        }

        // binding.radioCash.setVisibility(View.GONE);
        // binding.cashIcon.setVisibility(View.GONE);
        cardListAdapter.notifyDataSetChanged();
    }

    @Override
    public void createPaymentSuccess(PojoCreatePayment.Data data1) {
        Config.TRANSACTION_ID = data1.transactionId;
        android.util.Log.e("transaction_id", Config.TRANSACTION_ID);
        HashMap<String, String> hashMap = new HashMap<>();

        hashMap.put("uniquieAppKey", Constants.UNIQUE_APP_KEY);
        hashMap.put("serviceId", serviceId);
        hashMap.put("transactionId", Config.TRANSACTION_ID);
        hashMap.put("tapId", "");
            /*Config.TRANSACTION_ID = "";
            Config.TAP_ID = "";*/
        hashMap.put("cardType", "2");
        hashMap.put("referenceId", referenceId);

        hashMap.put("promoId", ConfirmBookFragment.Promo_id);
        hashMap.put("transactionStatus", "true");

               /* if (bookAgain) {
                    hashMap.put("isExtension", String.valueOf(false));
                } else*/
        if (isExtend) {
            hashMap.put("isExtension", String.valueOf(true));
        } else {
            hashMap.put("isExtension", String.valueOf(false));
        }
        ////////Bulk booking ===========================================
        if (booking_type == 3) {

            if (GeneralFunction.isNetworkConnected(getActivity(), binding.parent)) {
                presenter.apiCardPaymentPaytabsBulk(hashMap);
            }

        } else {
            ////////single and multiple booking ===========================================
            if (GeneralFunction.isNetworkConnected(getActivity(), binding.parent)) {
                presenter.apiCardPaymentPaytabs(hashMap);
            }
        }
    }

    @Override
    public void successPaymentPaytabs(PojoCreatePayment.Data data1) {
        Log.e("success", new Gson().toJson(data1));
        Config.TRANSACTION_ID = "";

        if (bookAgain) {
            if (booking_type == 3) {
                getFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                        .add(R.id.parent, PaymentInfoFragment.newInstance(searchMaidBulkModel, data1.transactionId,
                                data1.data.get(0).getAddress()), "PaymentInfoFragment").addToBackStack("PaymentInfoFragment").commitAllowingStateLoss();

            } else {
                getFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                        .add(R.id.parent, PaymentInfoFragment.newInstance(searchMaidModel, data1.transactionId,
                                data1.data.get(0).getAddress()), "PaymentInfoFragment").addToBackStack("PaymentInfoFragment").commitAllowingStateLoss();
            }
        } else if (isExtend) {
            Toast.makeText(getActivity(), getString(R.string.service_extended), Toast.LENGTH_SHORT).show();
            getActivity().onBackPressed();
        } else {

            PojoLogin data = Prefs.with(getActivity()).getObject(Constants.DATA, PojoLogin.class);
            if (data.isGuestFlag() && !data.isFirstBookingDone()) {
                data.setFirstBookingDone(true);
            }
            ServiceFragment.servicesID = "";
            Prefs.with(getActivity()).save(Constants.DATA, data);
            UpdateProgressStatus updateProgressStatus = (UpdateProgressStatus) getActivity().getSupportFragmentManager()
                    .findFragmentByTag("PaymentStateFragment");
            updateProgressStatus.changeProgressStatus(3);
            if (booking_type == 3) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                        .replace(R.id.childframeLayout, PaymentInfoFragment.newInstance(searchMaidBulkModel, data1.transactionId,
                                data1.data.get(0).getAddress()), "PaymentInfoFragment").addToBackStack("PaymentInfoFragment").commit();


            } else {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                        .replace(R.id.childframeLayout, PaymentInfoFragment.newInstance(searchMaidModel, data1.transactionId,
                                data1.data.get(0).getAddress()), "PaymentInfoFragment").addToBackStack("PaymentInfoFragment").commit();

            }
        }
    }

    @Override
    public void deleteCardSuccess() {
        presenter.apiCardList();
        GeneralFunction.showSnackBar(getActivity(), binding.parent, getString(R.string.card_deleted_successfully));
    }

    @Override
    public void error(String failureMessage) {
        if (failureMessage.equals("The maid you have selected is busy, please select another maid")) {
            Toast.makeText(getContext(), failureMessage, Toast.LENGTH_SHORT).show();
            if (getArguments().containsKey(Constants.MAID_AVAILABLE_TIMESLOT)) {
                Intent intent = new Intent(getContext(), SlotAvailbleActivity.class);
                intent.putExtra("data", String.valueOf(getArguments().getParcelable(Constants.MAID_AVAILABLE_TIMESLOT)));
                getContext().startActivity(intent);
            }
        } else {
            new DialogPopup().alertPopup(getActivity(), getResources().getString(R.string.dialog_alert), failureMessage, "").show(requireActivity().getSupportFragmentManager(), "ios_dialog");

        }


    }

    @Override
    public void failure(String failureMessage) {
   Log.e(TAG, "failure: "+failureMessage);
        new DialogPopup().alertPopup(getActivity(), getResources().getString(R.string.dialog_alert), getString(R.string.check_connection), "").show(requireActivity().getSupportFragmentManager(), "ios_dialog");
    }

    @Override
    public void addCardAddedSuccess(String cardToken) {
        callApiCardPaymetPayTabs(isCreditCard);
    }

    @Override
    public void successLanguageChange(String language) {
        if (language.equals("EN")) {
            Prefs.with(getActivity()).save(Constants.LANGUAGE_CODE, "en");
        } else {
            Prefs.with(getActivity()).save(Constants.LANGUAGE_CODE, "ar");
        }
    }

    @Override
    public void selectedCard(PojoCardList.Data data, EditText etCVV, String cardType) {
        this.selectedCard = data;
        this.etCvv = etCVV;
        this.cardType = cardType;
        // Log.e("cardlist data==",""+selectedCard);
        // hit api create charge payment
       /* if(cardType.equalsIgnoreCase("Credit/debit")){

        }else if (cardType.equalsIgnoreCase("cash")){

        }*/
    }

    @Override
    public void deleteCard(String id) {
        cardId = id;
        HashMap<String, String> map = new HashMap<>();
        map.put("uniquieAppKey", Constants.UNIQUE_APP_KEY);
        map.put("cardId", cardId);
        if (GeneralFunction.isNetworkConnected(getActivity(), binding.parent)) {
            presenter.apiCardDelete(map);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE && resultCode == RESULT_OK) {
            if (isExtend) {
                if (data.getBooleanExtra("back", false)) {
                    if (cardList.size() == 0) {
                        getActivity().onBackPressed();
                    }
                } else {
                    getActivity().onBackPressed();
                }

            } else {
                ((MaidBookActivity) getActivity()).step = 3;
                updateProgressStatus.changeProgressStatus(3);
                if (booking_type == 3) {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                            .replace(R.id.childframeLayout, PaymentInfoFragment.newInstance(searchMaidBulkModel, data.getStringExtra("ID"), (FullAddress) data.getParcelableExtra("Address")), "PaymentInfoFragment").commit();


                } else {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                            .replace(R.id.childframeLayout, PaymentInfoFragment.newInstance(searchMaidModel, data.getStringExtra("ID"), (FullAddress) data.getParcelableExtra("Address")), "PaymentInfoFragment").commit();

                }
            }
        } else if (requestCode == PAYMENT_REQ_CODE) {
            SharedPreferences shared_prefs = getActivity().getSharedPreferences("MAK_Shared", Activity.MODE_PRIVATE);
            String pt_response_code = shared_prefs.getString("pt_response_code", "");
            String pt_transaction_id = shared_prefs.getString("pt_transaction_id", "");
            transactionId = pt_transaction_id;
            pt_token = shared_prefs.getString("pt_token", "");
            customerEmail = shared_prefs.getString("pt_token_customer_email", "");
            customerPassword = shared_prefs.getString("pt_token_customer_password", "");


            if (pt_response_code.equals("100")) {
                if (GeneralFunction.isNetworkConnected(getActivity(), binding.parent)) {

                    if (isExistingCustomerCard) {
                        callApiCardPaymetPayTabs(isCreditCard);

                    } else if (!isCreditCard) {
                        callApiCardPaymetPayTabs(isCreditCard);

                    } else {
                        HashMap<String, String> hashMapVerification = new HashMap<>();
                        if (isCreditCard) {
                            hashMapVerification.put("merchant_email", getString(R.string.paytab_live_credit_email));
                            hashMapVerification.put("secret_key", getString(R.string.paytab_live_credit_secret_key));
                        } else {
                            hashMapVerification.put("merchant_email", getString(R.string.paytab_live_debit_email));
                            hashMapVerification.put("secret_key", getString(R.string.paytab_live_debit_secret_key));
                        }
                        hashMapVerification.put("transaction_id", pt_transaction_id);

                        if (GeneralFunction.isNetworkConnected(getActivity(), binding.parent)) {
                            presenter.apiPaytabTransactionVerification(hashMapVerification);
                        }

                    }


                }
            } else if (pt_response_code.equals("")) {
                if (cardList.size() == 0) {
                    if (bookAgain) {
                        getActivity().finish();
                    } else {
                        getActivity().getSupportFragmentManager().popBackStack();

                    }
                }
            }
        } else if (requestCode == REQ_CODE && resultCode == RESULT_CANCELED) {
            if (isExtend) {
                if (data.getBooleanExtra("back", false)) {
                    if (cardList.size() == 0) {
                        getActivity().onBackPressed();
                    }
                } else {
                    getActivity().onBackPressed();
                }

            }
        }
    }

    private void callApiCardPaymetPayTabs(Boolean isCreditCard) {
        HashMap<String, String> hashMap = new HashMap<>();

        hashMap.put("uniquieAppKey", Constants.UNIQUE_APP_KEY);
        hashMap.put("serviceId", getArguments().getString(Constants.SERVICE_ID));
        hashMap.put("transactionId", transactionId);
        hashMap.put("promoId", ConfirmBookFragment.Promo_id);
        if (isCreditCard)
            hashMap.put("cardType", "2");
        else
            hashMap.put("cardType", "1");


        hashMap.put("transactionStatus", "true");
        if (isExtend) {
            hashMap.put("isExtension", String.valueOf(true));
        } else {
            hashMap.put("isExtension", String.valueOf(false));
        }
        if (GeneralFunction.isNetworkConnected(getActivity(), binding.parent)) {
            presenter.apiCardPaymentPaytabs(hashMap);
        }

    }//callApiCardPaymetPayTabs



    /** Pay using stipe*/


    void payUsingStripe(String fullName,String mobile,String address,String service,String currency_code, String serviceId){
     setLoading(true);

        HashMap<String,String> request = new HashMap<>();
        request.put("name",fullName);
        request.put("mobile",mobile);
        request.put("email",address);
        request.put("service",service);
        request.put("amount",String.format("%.2f", ConfirmBookFragment.totalper));
        request.put("currency_code",currency_code);
        request.put("serviceId",serviceId);



        android.util.Log.e(TAG, "onCreate: "+ new Gson().toJson(request));

        Fuel.INSTANCE.post(Config.getBaseURL() +"user/payment-sheet", null ) .header(Headers.CONTENT_TYPE, "application/json") // Set the content type if required
                .body(new Gson().toJson(request), Charset.defaultCharset())
                .responseString(new Handler<String>() {
                    @Override
                    public void success(String s) {

                        try {
                            final JSONObject result = new JSONObject(s);
                            android.util.Log.e(TAG, "success: response : "+ result);
                            JSONObject data = result.getJSONObject("data");

                            customerConfig = new PaymentSheet.CustomerConfiguration(
                                    data.getString("customer"),
                                    data.getString("ephemeralKey")
                            );
                            paymentIntentClientSecret = data.getString("paymentIntent");

                            PaymentConfiguration.init(requireContext().getApplicationContext(), data.getString("publishableKey"));
                            presentPaymentSheet();
                            setLoading(false);
                        }
                        catch (JSONException e) {
                            setLoading(false);
                            Log.e(TAG, "Fail Fuel APi   ",e );
                            /* handle error */
                        }
                    }

                    @Override
                    public void failure(@NonNull FuelError fuelError) {
                        android.util.Log.e(TAG, "failure: "+ fuelError.getMessage() );
                        setLoading(false);
                        /* handle error */ }
                });
    }

    void onPaymentSheetResult(final PaymentSheetResult paymentSheetResult) {
        // implemented in the next steps

        if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {

            Log.d(TAG, "Canceled");
        } else if (paymentSheetResult instanceof PaymentSheetResult.Failed) {
            android.util.Log.e(TAG, "onPaymentSheetResult: fail payment "+((PaymentSheetResult.Failed) paymentSheetResult).getError()  );

            Log.e(TAG, "Got error: ", ((PaymentSheetResult.Failed) paymentSheetResult).getError());
            Toast.makeText(requireContext(),"Failed",Toast.LENGTH_SHORT).show();
        } else if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
           setLoading(true);
             // Display for example, an order confirmation screen
            Log.d(TAG, "Completed   " + paymentSheetResult);
            stripe.retrievePaymentIntent(paymentIntentClientSecret, new ApiResultCallback<PaymentIntent>() {
                @Override
                public void onSuccess(@NonNull PaymentIntent paymentIntent) {
                   Config.TRANSACTION_ID=  paymentIntent.getId();
                   onResume();

                }

                @Override
                public void onError(@NonNull Exception e) {
                 Toast.makeText(requireContext(),e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                }
            });


        }
    }

    private void presentPaymentSheet() {
        final PaymentSheet.Configuration configuration = new PaymentSheet.Configuration.Builder("Example, Inc.")
                .customer(customerConfig)
                // Set `allowsDelayedPaymentMethods` to true if your business can handle payment methods
                // that complete payment after a delay, like SEPA Debit and Sofort.
                .allowsDelayedPaymentMethods(true)
                .build();
        paymentSheet.presentWithPaymentIntent(
                paymentIntentClientSecret,
                configuration
        );

    }


}//CardListFragment
