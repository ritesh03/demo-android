package com.maktoday.views.confirmbook;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.maktoday.utils.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
//import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.location.places.Place;
//import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.gson.Gson;
import com.maktoday.Config.Config;
import com.maktoday.R;
import com.maktoday.adapter.AddressListAdapter;
import com.maktoday.databinding.FragmentConformbookBinding;
import com.maktoday.interfaces.SelectAddress;
import com.maktoday.model.ApiResponse;
import com.maktoday.model.FullAddress;
import com.maktoday.model.PojoLogin;
import com.maktoday.model.PojoMyBooking;
import com.maktoday.model.PojoSearchMaid;
import com.maktoday.model.PromoResponse;
import com.maktoday.model.SearchMaidBulkModel;
import com.maktoday.model.SearchMaidModel;
import com.maktoday.model.TimeSlot;
import com.maktoday.utils.BaseFragment;
import com.maktoday.utils.Constants;
import com.maktoday.utils.DataVariable;
import com.maktoday.utils.DialogPopup;
import com.maktoday.utils.GeneralFunction;
import com.maktoday.utils.Prefs;
import com.maktoday.utils.dialog.IOSAlertDialog;
import com.maktoday.views.AllService.ServiceFragment;
import com.maktoday.views.PickerShowActivity;
import com.maktoday.views.extendpayment.ExtendPaymentActivity;
import com.maktoday.views.maidsearch.MaidContract;
import com.maktoday.views.maidsearch.MaidPresenter;
import com.maktoday.views.main.Main2Activity;
import com.maktoday.views.payment.PaymentStateFragment;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.TimeZone;

import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getApplicationContext;
import static com.maktoday.utils.Constants.USER_COUNTRY;
import static com.maktoday.views.home.HomeFragment.booking_type;
import static com.maktoday.views.home.HomeFragment.selectedDatesList;

/**
 * Created by cbl81 on 2/11/17.
 */

public class ConfirmBookFragment extends BaseFragment implements View.OnClickListener, SelectAddress, ConfirmBookContract.View, MaidContract.View {
    private static final int PLACE_PICKER_REQUEST = 123;
    private static final String TAG = "ConfirmBookFragment";
    public static double Discounted_price = 0.0;
    public static double totalper = 0.0;
    public static double maidprice = 0.0;
    public static double per = 0.0;
    public static double total = 0.0;
    public static double exclVatAmount = 0.0;

    public Double totalValue = 0.0;
    public Double percentage = 0.0;
    public static String Promo_id = "";
    public static String Promo_desc = "";
    public static String Promo_discount = "";
    public static String amount_temp = "";
    public static String temp_duration = "";
    public static String temp_currency = "";
    public static String agency_id = "";
    public static Float vat = 0.0F;
    public static String Promo_name = "";
    public static String vat_value = "";
    private FragmentConformbookBinding binding;
    private int pageNo = 1;
    private PojoSearchMaid searchMaidData1;
    private TimeZone bookingTimeZone;
    private Dialog errorPopUpDialog;
    private final int LIMIT = 20;
    private SearchMaidModel searchMaidModel;
    private SearchMaidBulkModel searchMaidBulkModel;
    public static LatLng latLng;
    public static LatLng latLng_temp;
    public static String road_temp = "";
    public static String city_temp = "";
    public static String block_temp = "";
    public static String apartment_temp = "";
    public static String full_address_temp = "";
    public static String buildingNumber = "";
    public static String postalCode = "";
    private MaidContract.Presenter presenterr;
    private String country;
    private String country_code;
    private String currency = "";
    private String reschuleStatus = "";
    private String serviceId = "";
    private PojoMyBooking.Datum bookingDataModel;
    PlacesClient placesClient;
    private String ref_id = "";
    private AddressListAdapter addressListAdapter;
    private FullAddress selectedFullAddress;
    private ConfirmBookContract.Presenter presenter;
    private boolean sameAddress = false;
    private ArrayList<FullAddress> addressList;
    private boolean bookAgain;  // this flag denotes flow coming from book again in past booking or book again in favourites
    private boolean isHome;     // this flag denoted flow coming from search bar in Home screen
    private FusedLocationProviderClient mFusedLocationClient;

    private double totalpayment = 0.0;

    public static ConfirmBookFragment newInstance(SearchMaidModel searchMaidModel, boolean bookAgain, boolean isHome) {
        Bundle args = new Bundle();
        ConfirmBookFragment fragment = new ConfirmBookFragment();
        args.putParcelable(Constants.SEARCH_MAID_DATA, searchMaidModel);
        args.putBoolean(Constants.BOOK_AGAIN, bookAgain);
        args.putBoolean(Constants.ISHOME, isHome);
        fragment.setArguments(args);
        return fragment;
    }

    public static ConfirmBookFragment newInstance(SearchMaidBulkModel searchMaidModel, boolean bookAgain, boolean isHome, String type) {
        Bundle args = new Bundle();
        ConfirmBookFragment fragment = new ConfirmBookFragment();
        args.putParcelable(Constants.SEARCH_MAID_DATA, searchMaidModel);
        args.putBoolean(Constants.BOOK_AGAIN, bookAgain);
        args.putBoolean(Constants.ISHOME, isHome);
        fragment.setArguments(args);
        return fragment;
    }

    public static ConfirmBookFragment newInstance(SearchMaidModel searchMaidModel, boolean bookAgain, boolean isHome, String serviceId, TimeSlot timeSlot, String ref_id, String vat) {
        Bundle args = new Bundle();
        ConfirmBookFragment fragment = new ConfirmBookFragment();
        args.putParcelable(Constants.SEARCH_MAID_DATA, searchMaidModel);
        args.putBoolean(Constants.BOOK_AGAIN, bookAgain);
        args.putBoolean(Constants.ISHOME, isHome);
        args.putString("ref_id", ref_id);
        args.putString(Constants.SERVICE_ID, serviceId);
        args.putString(Constants.VAT, vat);
        if (timeSlot != null) {
            args.putParcelable(Constants.MAID_AVAILABLE_TIMESLOT, timeSlot);
        }
        fragment.setArguments(args);
        return fragment;
    }

    public static ConfirmBookFragment newInstance(SearchMaidModel searchMaidModel, boolean bookAgain) {
        return newInstance(searchMaidModel, false, false);
    }

    public static ConfirmBookFragment newInstance(SearchMaidBulkModel searchMaidModel, boolean bookAgain) {
        return newInstance(searchMaidModel, false, false, "");
    }

    public static ConfirmBookFragment newInstance(SearchMaidModel searchMaidModel) {
        return newInstance(searchMaidModel, false);
    }

    public static ConfirmBookFragment newInstance(PojoMyBooking.Datum datum, SearchMaidBulkModel searchMaidModel, String rescheduleStatus, String ServiceId) {
        if (rescheduleStatus.equalsIgnoreCase("yes")) {
            android.util.Log.d(TAG, "newInstance: if (rescheduleStatus.equalsIgnoreCase(\"yes\"))");
            Log.d(TAG, "newInstance: if searchMaidModel :-- " + new Gson().toJson(searchMaidModel));
            Log.d(TAG, "newInstance: if PojoMyBooking.Datum :-- " + new Gson().toJson(datum));
            Log.d(TAG, "newInstance: if rescheduleStatus :-- " + rescheduleStatus);
            Log.d(TAG, "newInstance: if ServiceId :-- " + ServiceId);
            Bundle args = new Bundle();
            ConfirmBookFragment fragment = new ConfirmBookFragment();
            args.putParcelable(Constants.SEARCH_MAID_DATA, searchMaidModel);
            args.putBoolean(Constants.BOOK_AGAIN, false);
            args.putBoolean(Constants.ISHOME, false);
            args.putString(Constants.BOOKING_DATA, new Gson().toJson(datum));
            args.putString(Constants.reschuleStatus, rescheduleStatus);
            args.putString(Constants.SERVICE_ID, ServiceId);
            fragment.setArguments(args);
            return fragment;
        } else {
            Log.d(TAG, "newInstance:  } else { " + new Gson().toJson(searchMaidModel));
            return newInstance(searchMaidModel, false);
        }
    }

    public static Fragment newInstance(SearchMaidModel searchMaidModel, String vat) {
        Bundle args = new Bundle();
        ConfirmBookFragment fragment = new ConfirmBookFragment();
        args.putParcelable(Constants.SEARCH_MAID_DATA, searchMaidModel);
        args.putString(Constants.VAT, vat);
        fragment.setArguments(args);
        return fragment;
    }

    public static Fragment newInstance(SearchMaidModel searchMaidModel, String vat, String serviceId) {
        Bundle args = new Bundle();
        ConfirmBookFragment fragment = new ConfirmBookFragment();
        args.putParcelable(Constants.SEARCH_MAID_DATA, searchMaidModel);
        args.putString(Constants.VAT, vat);
        args.putString(Constants.SERVICE_ID, serviceId);
        fragment.setArguments(args);
        return fragment;
    }

    private void apiSearchMaidData(TimeZone bookingTimeZone) {
        FullAddress fullAddress = Prefs.with(getContext()).getObject(Constants.MAP_FULL_ADDRESS, FullAddress.class);

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("uniquieAppKey", searchMaidBulkModel.uniquieAppKey);
        hashMap.put("workDate", String.valueOf(searchMaidBulkModel.workDate));
        hashMap.put("endDate", String.valueOf(searchMaidBulkModel.workDate));
        hashMap.put("startTime", String.valueOf(searchMaidBulkModel.startTime));
        hashMap.put("duration", String.valueOf(searchMaidBulkModel.duration));
        hashMap.put("maidCount", String.valueOf(searchMaidBulkModel.maidCount));
        hashMap.put("long", String.valueOf(searchMaidBulkModel.lng));
        hashMap.put("lat", String.valueOf(searchMaidBulkModel.lat));
        if (!ServiceFragment.servicesID.isEmpty()) {
            hashMap.put("services", ServiceFragment.servicesID);
        }
        if (reschuleStatus.equalsIgnoreCase("yes")) {
            hashMap.put("country", bookingDataModel.address.country);
        } else {
            hashMap.put("country", fullAddress.country);
        }
        hashMap.put("pageNo", String.valueOf(pageNo));
        hashMap.put("limit", String.valueOf(LIMIT));

        hashMap.put("deviceTimeZone", TimeZone.getDefault().getID());
        hashMap.put("timeZone", bookingTimeZone.getID());
        hashMap.put("hour", String.valueOf(GeneralFunction.getTimeInTimeZone(new Date(searchMaidBulkModel.startTime), bookingTimeZone).getTime()));

        if (GeneralFunction.isNetworkConnected(getActivity(), getActivity().findViewById(android.R.id.content))) {
            binding.tvProceedPayment.setEnabled(false);
            binding.amoutDetails.llTotal.setVisibility(View.GONE);
            presenter.apiSearchBulkMaid(hashMap);
        }
    }

    public void getSearchDataList() {
        if (GeneralFunction.isNetworkConnected(getActivity(), getView())) {
            if (bookingTimeZone == null) {
                final String googleApiKey = getString(R.string.google_api_key_mak);
                Log.e(TAG, "check1" + new Gson().toJson(searchMaidBulkModel));
                if (reschuleStatus.equalsIgnoreCase("yes")) {
                    searchMaidBulkModel.lat = bookingDataModel.bookingLocation.get(1);
                    searchMaidBulkModel.lng = bookingDataModel.bookingLocation.get(0);
                }
                presenterr.getTimeZoneFromLatLong(searchMaidBulkModel.lat, searchMaidBulkModel.lng, googleApiKey);
            } else {
                apiSearchMaidData(bookingTimeZone);
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ConfirmBookFragment.Promo_id != null) {
            ConfirmBookFragment.Discounted_price = 0.0;
            ConfirmBookFragment.Promo_id = "";
            ConfirmBookFragment.Promo_discount = "";
            ConfirmBookFragment.Promo_desc = "";
            ConfirmBookFragment.Promo_name = "";
            ConfirmBookFragment.amount_temp = "";
            ConfirmBookFragment.temp_duration = "";
            ConfirmBookFragment.temp_currency = "";

        }
        if (getArguments() != null) {
            isHome = getArguments().getBoolean(Constants.ISHOME, false);
            ref_id = getArguments().getString("ref_id", "");

            if (booking_type == 3) {
                searchMaidBulkModel = getArguments().getParcelable(Constants.SEARCH_MAID_DATA);
            } else {
                searchMaidModel = getArguments().getParcelable(Constants.SEARCH_MAID_DATA);

            }
            vat_value = getArguments().getString(Constants.VAT);
            bookAgain = getArguments().getBoolean(Constants.BOOK_AGAIN, false);

            if (getArguments().containsKey(Constants.reschuleStatus)) {
                reschuleStatus = getArguments().getString(Constants.reschuleStatus);
                serviceId = getArguments().getString(Constants.SERVICE_ID);
                bookingDataModel = new Gson().fromJson(getArguments().getString(Constants.BOOKING_DATA), PojoMyBooking.Datum.class);
            }

        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentConformbookBinding.inflate(inflater, container, false);
        return binding.parent;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: StartActivity");
       // binding.llTotal.setVisibility(View.VISIBLE);
        setHasOptionsMenu(true);
        init();

        // Initialize Places.
        Places.initialize(getApplicationContext(), getString(R.string.google_api_key_mak));
        placesClient = Places.createClient(getActivity());

        if (booking_type == 3) {
            Log.d(TAG, "onViewCreated:  if (booking_type == 3) {");
            // getSearchDataList();
            setBulkData(null);
            if (isHome || bookAgain) {
                Log.d(TAG, "onViewCreated: if (isHome || bookAgain) {");
            } else {
                Log.d(TAG, "onViewCreated:   if (isHome || bookAgain) {    else");
                getSearchDataList();
            }
        } else {
            Log.d(TAG, "onViewCreated: if (booking_type == 3) { else { ");
            setData();
            binding.etStreet.setText(" ");
            binding.etzipcode.setText(" ");
        }

        setListeners();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(1000);

        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data

                    Log.d("Latitude11", String.valueOf(location.getLatitude()));
                    Log.d("Latitude11", String.valueOf(location.getLongitude()));
                }
            }
        };
    }

    /**
     * used to intialise the views and variables
     */
    private void init() {
        presenter = new ConfirmBookPresenter();
        presenter.attachView(this);
        presenterr = new MaidPresenter();
        presenterr.attachView(this);
        binding.tvAddress.setText(R.string.enter_your_city_or_postcode);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(binding.toolbar);
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getSupportActionBar().setDisplayShowHomeEnabled(true);
            activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
            activity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_white);
        }

        String country = Prefs.with(getActivity()).getString(Constants.COUNTRY_NAME, "");
        if (country.contains("United Arab Emirates")) {
            currency = "AED";
        } else {
            currency = "BHD";
        }


    }

    @Override
    public void onResume() {
        super.onResume();
        if (Promo_id.equalsIgnoreCase("")) {

        } else {
            enablePromoUI();
        }
/*        Promo_id="";
        binding.promoEdittext.setText("");*/
    }

    @Override
    public void searchMaidSuccess(PojoSearchMaid searchMaidData) {
        Log.e(TAG, "\" --------- searchMaidSuccess res = \" " + new Gson().toJson(searchMaidData));
        searchMaidData1 = searchMaidData;
    }

    @Override
    public void bookingTimeZoneReceived(@Nullable TimeZone timeZone) {
        if (timeZone == null) {
            Toast.makeText(getActivity(), R.string.unable_to_locate_time_zone, Toast.LENGTH_SHORT).show();
        } else {
            this.bookingTimeZone = timeZone;
            apiSearchMaidData(timeZone);
        }
    }

    @Override
    public void signupFailure(String failureMessage) {

    }

    @Override
    public void displayTimeSlots(TimeSlot timeSlot) {

    }

    @SuppressLint("ClickableViewAccessibility")
    private void setData() {

        PojoLogin dataLogin = Prefs.with(getActivity()).getObject(Constants.DATA, PojoLogin.class);
        if (dataLogin == null) {
            dataLogin = new PojoLogin();
        }
        addressList = dataLogin.multipleAddress;

        if (addressList == null) {
            Log.d(TAG, "setData: if (addressList == null) {");
            addressList = new ArrayList<>();
        }
        if (addressList.size() != 0) {
            Log.d(TAG, "setData:  if(addressList.size()!=0){");
            Collections.reverse(addressList);
        }

        if (isHome || bookAgain) {
            Log.d(TAG, "setData: if (isHome || bookAgain) {");
            binding.tvTitle.setVisibility(View.VISIBLE);
            binding.tvCTitle.setVisibility(View.VISIBLE);
            //set title in toolbar
            binding.title.setText(getString(R.string.select_location));
            binding.topLogooo.setVisibility(View.VISIBLE);
            binding.title.setVisibility(View.GONE);

            //to change the address all field should be editable
            //  binding.etCity.setEnabled(false);
            binding.etRoadNumber.setEnabled(true);
            binding.etBlockNumber.setEnabled(true);
            binding.etAddressDescription.setEnabled(true);
            binding.etApartment.setEnabled(true);
            binding.rlInfo.setVisibility(View.GONE);
            binding.tvSameAddress.setVisibility(View.GONE);
            binding.tvTitle.setVisibility(View.VISIBLE);
            binding.tvAddress.setVisibility(View.VISIBLE);
            binding.tvCurrent.setVisibility(View.VISIBLE);
            binding.vv1.setVisibility(View.GONE);
            binding.vv2.setVisibility(View.GONE);
            binding.tvProceedPayment.setText(R.string.select_location);
            // show saved address in the list
            if (addressList == null || addressList.size() == 0) {
                Log.d(TAG, "setData:  if (addressList == null || addressList.size() == 0) {");
                binding.llSavedAddress.setVisibility(View.GONE);
                binding.tvSameAddress.setVisibility(View.GONE);
            } else {
                Log.d(TAG, "setData:  if (addressList == null || addressList.size() == 0) {  } else {");
                binding.llSavedAddress.setVisibility(View.VISIBLE);
            }
            binding.adLay.setVisibility(View.GONE);
            binding.amoutDetails.promoLay.setVisibility(View.GONE);
            binding.llLocation.setVisibility(View.VISIBLE);
            binding.tvProceedPayment.setVisibility(View.GONE);
            binding.etCity.setEnabled(true);
        } else {
            Log.d(TAG, "setData:  if (isHome || bookAgain) {  else {");
            // hide saved address in the list
            binding.llSavedAddress.setVisibility(View.GONE);
            //set title in toolbar
            binding.title.setText(R.string.confirm_your_details);
            binding.topLogooo.setVisibility(View.GONE);
            binding.tvTitle.setVisibility(View.GONE);
            binding.tvCTitle.setVisibility(View.GONE);
            binding.topLogooo.setVisibility(View.GONE);
            binding.title.setVisibility(View.VISIBLE);

            binding.etCity.setEnabled(false);
            binding.etRoadNumber.setEnabled(false);
            binding.etBlockNumber.setEnabled(false);
            binding.etAddressDescription.setEnabled(false);
            binding.etApartment.setEnabled(false);
            if (Promo_id.equalsIgnoreCase("")) {
                Log.d(TAG, "setData: if (Promo_id.equalsIgnoreCase(\"\")) {");
                if (booking_type == 2) {
                    // binding.promoInputLay.setVisibility(View.GONE);
                }

            } else {
                // enablePromoUI();

            }
            //maid details hide
            binding.rlInfo.setVisibility(View.VISIBLE);

            // binding.tvTitle.setVisibility(View.VISIBLE);
            // binding.tvTitle.setText(R.string.service_address);
            binding.tvAddress.setVisibility(View.GONE);
            binding.tvCurrent.setVisibility(View.GONE);
            binding.vv1.setVisibility(View.GONE);
            binding.vv2.setVisibility(View.GONE);
            binding.adLay.setVisibility(View.GONE);
            binding.llLocation.setVisibility(View.GONE);
            binding.amoutDetails.promoLay.setVisibility(View.VISIBLE);

            if (reschuleStatus.equalsIgnoreCase("yes")) {
                Log.d(TAG, "setData: if (reschuleStatus.equalsIgnoreCase(\"yes\")) {");
                binding.tvProceedPayment.setText("Reschedule");
            } else {
                Log.d(TAG, "setData:  } else {");
                binding.tvProceedPayment.setText(R.string.proceed_to_checkout);

            }           // Only shown in case of "Confirm details"
//            if(Prefs.get().getString(USER_COUNTRY,"").equalsIgnoreCase("GB")){
//
//            }
//            else {

//                if (searchMaidModel.agencyId!=null){
//                    Log.e("agency_id===",searchMaidModel.agencyId);
//                    Log.d(TAG, "setData: agency type:--"+ searchMaidModel.agencyType);
//                    if (searchMaidModel.agencyId.equalsIgnoreCase("5ad5a5c4cce102577f043c19"))
//                    {
//                        if (!Prefs.get().getString(Constants.ISBACK,"").equalsIgnoreCase("true")){
//                            new DialogPopup().alertPopupp(getActivity(), getResources().getString(R.string.dialog_alert), getString(R.string.as_a_platform_providing_the_marketplace), "").show();
//                        }else {
//                            Prefs.with(getApplicationContext()).save(Constants.ISBACK,"false");
//                        }
//                    }
//                }
            if (searchMaidModel.agencyType != null) {
                Log.d(TAG, "setData: agency type 1:--" + searchMaidModel.agencyType);
                if (searchMaidModel.agencyType.equalsIgnoreCase("NORMAL")
                        || searchMaidModel.agencyType.equalsIgnoreCase("MAK_REGISTERED")) {

                    Log.d(TAG, "setData: agency type:-- " + searchMaidModel.agencyType);
                    if (!Prefs.get().getString(Constants.ISBACK, "").equalsIgnoreCase("true")) {
                        //   new DialogPopup().alertPopupp(getActivity(), getResources().getString(R.string.dialog_alert), getString(R.string.as_a_platform_providing_the_marketplace), "").show();
                    } else {
                        Prefs.with(getApplicationContext()).save(Constants.ISBACK, "false");
                    }
                }
            }


            //}//
        }

        /*
         *
         * when user enter in tha app as guest login
         */

        String accessToken = Prefs.with(getActivity()).getString(Constants.ACCESS_TOKEN, "");
        if (accessToken == null || accessToken.isEmpty() || accessToken.equals("bearer")) {
            binding.cbSaveCard.setVisibility(View.GONE);
            binding.tvSameAddress.setVisibility(View.GONE);
        } else {
            if (isHome || bookAgain) {
                binding.cbSaveCard.setVisibility(View.VISIBLE);
                //  binding.tvSameAddress.setVisibility(View.VISIBLE);
            } else {
                binding.cbSaveCard.setVisibility(View.GONE);
                binding.tvSameAddress.setVisibility(View.GONE);
            }

        }

        //end
        addressListAdapter = new AddressListAdapter(getActivity(), addressList, this, binding.tvProceedPayment);
        binding.rvAddress.setAdapter(addressListAdapter);
        binding.rvAddress.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvAddress.setHasFixedSize(false);

        //show data of selected maid in relative layout
        if (!isHome && !bookAgain) {

            if (searchMaidModel != null) {
                // binding.tvAgencyName.setText(getString(R.string.label_agencyName) + " " + searchMaidModel.agencyName);
                //binding.tvMaidName.setText(getString(R.string.label_maidNAme) + " " + searchMaidModel.maidName);
                FullAddress fullAddress = Prefs.with(getActivity()).getObject(Constants.MAP_FULL_ADDRESS, FullAddress.class);
                setStringStyleeColon(searchMaidModel.maidName, binding.tvMaidName);
                setStringStyleeColon(searchMaidModel.agencyName, binding.tvAgencyName);
                if (Prefs.get().getString(USER_COUNTRY, "").equalsIgnoreCase("BH")) {
                    if (fullAddress.buildingNumber.isEmpty()) {
                        setStringStyleeColon(fullAddress.villaName + ", " + fullAddress.buildingName + ", " +
                                fullAddress.streetName + ", " + fullAddress.city + ", " + fullAddress.country, binding.tvDuration);
                    } else {
                        setStringStyleeColon(fullAddress.villaName + ", " + fullAddress.buildingName + ", " +
                                fullAddress.buildingNumber + ", " + fullAddress.streetName + ", " +
                                fullAddress.city + ", " + fullAddress.country, binding.tvDuration);
                    }

                } else {
                    if (fullAddress.buildingName.isEmpty()) {
                        setStringStyleeColon(fullAddress.buildingNumber + ", " + fullAddress.streetName + ", " + fullAddress.postalCode + "\n" +
                                fullAddress.city + ", " + fullAddress.country, binding.tvDuration);
                    } else {
                        setStringStyleeColon(fullAddress.buildingName + ", " +
                                fullAddress.buildingNumber + ", " + fullAddress.streetName + ", " + fullAddress.postalCode + "\n" +
                                fullAddress.city + ", " + fullAddress.country, binding.tvDuration);
                    }
                }


                setStringStyleeColon(String.format("%s %s",
                        searchMaidModel.duration, getString(R.string.concat_hour)), binding.tvAddresss);

                SimpleDateFormat format = new SimpleDateFormat("EEEE, MMMM dd,yyyy", Locale.ENGLISH);
                try {

                    if (booking_type == 2) {
                        Collections.sort(selectedDatesList);
                        Collections.reverse(selectedDatesList);
                        String temp_dates = "";
                        for (int i = 0; i < selectedDatesList.size(); i++) {
                            //   temp_dates = GeneralFunction.getFormatFromDate(selectedDatesList.get(i), "EEEE, MMMM dd") + ","+temp_dates;
                            temp_dates = GeneralFunction.getFormatFromDate(selectedDatesList.get(i), "dd MMM") + ", " + temp_dates;
                        }

                        if (selectedDatesList.size() <= 2) {
                            binding.tvShowAll.setVisibility(View.GONE);

                            setStringStyleeColon(temp_dates.substring(0, temp_dates.length() - 2), binding.tvDate);
                            setStringStyleeColon(searchMaidModel.selectedTime, binding.tvTime);

                        } else {
                            binding.tvShowAll.setVisibility(View.VISIBLE);

                            setStringStyleeColon(temp_dates.substring(0, temp_dates.length() - 2), binding.tvDate);
                            setStringStyleeColon(searchMaidModel.selectedTime, binding.tvTime);

                            binding.tvDate.setVisibility(View.VISIBLE);
                            binding.tvShowAll.setVisibility(View.VISIBLE);
                            binding.tvRatePerHour.setVisibility(View.GONE);
                        }

                    } else {
                        Calendar calendar = Calendar.getInstance();
                        String year = String.valueOf(calendar.get(Calendar.YEAR));
                        Log.e("test_date", searchMaidModel.selectedDate + ", " + year);

                        // binding.tvTime.setText(format.format(format.parse(searchMaidModel.selectedDate)) + " at " + searchMaidModel.selectedTime);
                        Date newDate = format.parse(searchMaidModel.selectedDate + ", " + year);

                        format = new SimpleDateFormat("E, MMM d", Locale.ENGLISH);
                        String date = format.format(newDate);
                        binding.tvTime.setText(date + " at " + searchMaidModel.selectedTime);

                        binding.tvDate.setVisibility(View.GONE);
                        binding.tvShowAll.setVisibility(View.GONE);
                        binding.tvRatePerHour.setVisibility(View.GONE);
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                temp_duration = String.valueOf(searchMaidModel.duration);
                temp_currency = String.valueOf(searchMaidModel.currency);
                agency_id = searchMaidModel.agencyId;

                vat = Float.parseFloat((getArguments().getString(Constants.VAT) == null ? "0" : getArguments().getString(Constants.VAT)));

                if (searchMaidModel.maidPrice != null) {
                    //NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "BH"));
                    // vat value disable  button
                    if (getArguments().getString(Constants.VAT).equalsIgnoreCase("0")) {
                        binding.amoutDetails.txtvat.setVisibility(View.GONE);
                        binding.amoutDetails.tvVat.setVisibility(View.GONE);
                        binding.amoutDetails.tvTotalAmount.setText(getString(R.string.total_amount));
                        binding.amoutDetails.tvFinalAmount.setText(getString(R.string.final_price_inc_vat));
                    } else {
                        binding.amoutDetails.txtvat.setVisibility(View.VISIBLE);
                        binding.amoutDetails.tvVat.setVisibility(View.VISIBLE);
                        binding.amoutDetails.tvTotalAmount.setText(getString(R.string.total_amount) + " (excl. VAT)");
                        binding.amoutDetails.tvVat.setText(getString(R.string.vat) + " (" + "@" + getArguments().getString(Constants.VAT) + "%" + ")");
                        binding.amoutDetails.tvFinalAmount.setText(getString(R.string.final_price_inc_vat) + " (incl. VAT)");
                    }

                    // rounded maid price
                    if (searchMaidModel.currency.equalsIgnoreCase("BHD")) {
                        maidprice = searchMaidModel.maidPrice;
                    } else {
                        maidprice = searchMaidModel.maidPrice;
                    }

                    if (booking_type == 2) {

                        totalValue = searchMaidModel.duration * maidprice * selectedDatesList.size();
                        totalpayment = totalValue;
                        // total = searchMaidModel.duration * searchMaidModel.maidPrice;
                        // binding.pricetotal.setText(formatter.format(total));

                    } else {
                        totalValue = searchMaidModel.duration * maidprice;
                        //binding.pricetotal.setText(formatter.format(total));
                    }
                    // total value rounded
                    if (searchMaidModel.currency.equalsIgnoreCase("BHD")) {
                        total = Double.parseDouble(String.format(Locale.ENGLISH,"%.3f",(totalValue)));
                    } else {
                        total = Double.parseDouble(String.format(Locale.ENGLISH,"%.2f",(totalValue)));
                    }

                    if (getArguments().getString(Constants.VAT) != null) {
                        percentage = (total / 100.0f) * Float.parseFloat(getArguments().getString(Constants.VAT));
                        // VAT value rounded
                        if (searchMaidModel.currency.equalsIgnoreCase("BHD")) {
                            per = percentage;
                        } else {
                            per = percentage;
                        }


                     //   totalper = total + per;

//-------------------------------------new calculation
                        Log.e(TAG, "setData:789 per:--  " + total);
                        amount_temp = String.valueOf(total);
                        totalpayment = total;
                        Log.d(TAG, "setData: vat:-- " + Double.parseDouble(getArguments().getString(Constants.VAT)));

                        if(searchMaidModel.currency.equalsIgnoreCase("BHD")) {
                            exclVatAmount = Double.parseDouble(String.format(Locale.ENGLISH,"%.3f",(total / (Double.parseDouble(getArguments().getString(Constants.VAT) + 100) / 100))));
                            per = Double.parseDouble(String.format(Locale.ENGLISH,"%.3f",(total-exclVatAmount)));
                        }else{
                            exclVatAmount = Double.parseDouble(String.format(Locale.ENGLISH,"%.2f",(total / ((Double.parseDouble(getArguments().getString(Constants.VAT)) + 100) / 100))));
                            per = Double.parseDouble(String.format(Locale.ENGLISH,"%.2f",(total-exclVatAmount)));
                        }
                        Log.e(TAG, "setData: total Sahil: "+total);
                        Log.e(TAG, "setData: per Sahil: "+per);
                        Log.e(TAG, "setData: exclVatAmout Sahil: "+exclVatAmount);
                        totalper = exclVatAmount + per;

//------------------------------------end new calculation
                    }
                    // show all calculation
                    if (searchMaidModel.currency.equalsIgnoreCase("BHD")) {
                        //  setStringStyleeColon(String.format(Locale.ENGLISH, "%s %.3f", searchMaidModel.currency,per), binding.txtvat);
                        setStringStyleeColon(String.format(Locale.ENGLISH, "%s %.3f", searchMaidModel.currency, per), binding.amoutDetails.txtvat);
                        setStringStyleeColon(String.format(Locale.ENGLISH, "%s %.3f", searchMaidModel.currency, exclVatAmount), binding.amoutDetails.pricetotal);
                        setStringStyleeColon(String.format(Locale.ENGLISH, "%s %.3f", searchMaidModel.currency, totalper), binding.amoutDetails.finalAmountTxtview);
                    } else {
                        //  setStringStyleeColon(String.format(Locale.ENGLISH, "%s %.2f", searchMaidModel.currency,per), binding.txtvat);
                        setStringStyleeColon(String.format(Locale.ENGLISH, "%s %.2f", searchMaidModel.currency, per), binding.amoutDetails.txtvat);
                        setStringStyleeColon(String.format(Locale.ENGLISH, "%s %.2f", searchMaidModel.currency, exclVatAmount), binding.amoutDetails.pricetotal);
                        setStringStyleeColon(String.format(Locale.ENGLISH, "%s %.2f", searchMaidModel.currency, totalper), binding.amoutDetails.finalAmountTxtview);
                    }


                    //setStringStyleeColon(String.format(Locale.ENGLISH, "%s %.3f", searchMaidModel.currency, searchMaidModel.maidPrice), binding.tvPrice);
                    setStringStylee(String.format(Locale.ENGLISH, "%s %.3f", searchMaidModel.currency, exclVatAmount), binding.amoutDetails.priceTxtview);

                }
                if (searchMaidModel.profilePicURL != null && searchMaidModel.profilePicURL.getOriginal() != null) {
                    Glide.with(getActivity())
                            .load(searchMaidModel.profilePicURL.getOriginal())
                            .circleCrop()
                            .placeholder(R.drawable.ic_user_pic)
                            .into(binding.ivMaid);
                }
                else {
                    Glide.with(getActivity())
                            .load(R.drawable.ic_user_pic)
                            .circleCrop()
                            .placeholder(R.drawable.ic_user_pic)
                            .into(binding.ivMaid);
                }

            }
            binding.rlInfo.setVisibility(View.GONE);
        }
        //set address if first time screen open
        setAddressData("");

        //helps to scroll
        binding.etAddressDescription.setMovementMethod(new ScrollingMovementMethod());

        binding.scrollView.setOnTouchListener(new View.OnTouchListener() {
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

    }//setData

    @SuppressLint("ClickableViewAccessibility")
    private void setBulkData(PojoSearchMaid searchMaidData) {
        if (searchMaidData != null) {
            searchMaidBulkModel.agencyId = searchMaidData.getData().getRequestedMaids().get(0).getAgencyId();
        }
        PojoLogin dataLogin = Prefs.with(getActivity()).getObject(Constants.DATA, PojoLogin.class);
        if (dataLogin == null) {
            dataLogin = new PojoLogin();
        }
        addressList = dataLogin.multipleAddress;
        if (addressList == null) {
            addressList = new ArrayList<>();
        }
        if (addressList.size() != 0) {
            Collections.reverse(addressList);
        }

        if (isHome || bookAgain) {
            //set title in toolbar
            binding.title.setText("Search Location");
            binding.topLogooo.setVisibility(View.VISIBLE);
            binding.title.setVisibility(View.GONE);

            //to change the address all field should be editable
            //  binding.etCity.setEnabled(false);
            binding.etRoadNumber.setEnabled(true);
            binding.etBlockNumber.setEnabled(true);
            binding.etAddressDescription.setEnabled(true);
            binding.etApartment.setEnabled(true);
            binding.rlInfo.setVisibility(View.GONE);
            binding.tvSameAddress.setVisibility(View.GONE);
            binding.tvTitle.setVisibility(View.VISIBLE);
            binding.tvAddress.setVisibility(View.VISIBLE);
            binding.llLocation.setVisibility(View.VISIBLE);
            binding.tvCurrent.setVisibility(View.VISIBLE);
            binding.vv1.setVisibility(View.GONE);
            binding.amoutDetails.promoLay.setVisibility(View.GONE);
            binding.vv2.setVisibility(View.GONE);
            binding.tvProceedPayment.setText(R.string.select_location);
            // show saved address in the list
            if (addressList == null || addressList.size() == 0) {
                binding.llSavedAddress.setVisibility(View.GONE);
                binding.tvSameAddress.setVisibility(View.GONE);
            } else {
                binding.llSavedAddress.setVisibility(View.VISIBLE);
            }
            binding.adLay.setVisibility(View.GONE);
            binding.tvProceedPayment.setVisibility(View.GONE);
        } else {
            binding.amoutDetails.promoLay.setVisibility(View.VISIBLE);
            binding.llLocation.setVisibility(View.GONE);
            binding.topLogooo.setVisibility(View.GONE);
            binding.tvTitle.setVisibility(View.GONE);
            binding.title.setVisibility(View.VISIBLE);

            // hide saved address in the list
            binding.llSavedAddress.setVisibility(View.GONE);
            //set title in toolbar
            binding.title.setText(R.string.confirm_your_details);
            //  binding.etCity.setEnabled(false);
            binding.etRoadNumber.setEnabled(false);
            binding.etBlockNumber.setEnabled(false);
            binding.etAddressDescription.setEnabled(false);
            binding.etApartment.setEnabled(false);

            //maid details hide
            binding.rlInfo.setVisibility(View.VISIBLE);
            // binding.tvTitle.setText(R.string.service_address);
            binding.tvAddress.setVisibility(View.GONE);
            binding.tvCurrent.setVisibility(View.GONE);
            binding.vv1.setVisibility(View.GONE);
            binding.vv2.setVisibility(View.GONE);

            binding.tvProceedPayment.setText(R.string.proceed_to_checkout);
        }

        /**
         * when user enter in tha app as guest login
         */

        String accessToken = Prefs.with(getActivity()).getString(Constants.ACCESS_TOKEN, "");
        if (accessToken == null || accessToken.isEmpty() || accessToken.equals("bearer")) {
            binding.cbSaveCard.setVisibility(View.GONE);
            binding.tvSameAddress.setVisibility(View.GONE);
        } else {
            if (isHome || bookAgain) {
                binding.cbSaveCard.setVisibility(View.VISIBLE);
                //  binding.tvSameAddress.setVisibility(View.VISIBLE);
            } else {
                binding.cbSaveCard.setVisibility(View.GONE);
                binding.tvSameAddress.setVisibility(View.GONE);
            }
        }
        //end

        addressListAdapter = new AddressListAdapter(getActivity(), addressList, this, binding.tvProceedPayment);
        binding.rvAddress.setAdapter(addressListAdapter);
        binding.rvAddress.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvAddress.setHasFixedSize(false);

        //show data of selected maid in relative layout
        if (!isHome && !bookAgain) {

            if (searchMaidData != null) {
                binding.tvAgencyName.setText(searchMaidData.getData().getRequestedMaids().get(0).getAgencyName());
                binding.maidLabel.setText(getString(R.string.no_of_maidss));
                binding.tvRatePerHour.setVisibility(View.GONE);
                //NumberFormat formater_perrate = NumberFormat.getCurrencyInstance(new Locale("en","BH"));
                //binding.tvRatePerHour.setText(formater_perrate.format(searchMaidData.getData().getRequestedMaids().get(0).getActualPrice())+" per hour");
                setStringStyleeColon(String.format(Locale.ENGLISH, "%s %.3f", searchMaidData.getData().getRequestedMaids().get(0).getCurrency(), searchMaidData.getData().getRequestedMaids().get(0).getActualPrice()) + " per hour", binding.tvRatePerHour);

                if (String.valueOf(searchMaidBulkModel.maidCount).equalsIgnoreCase("1")) {
                    binding.tvMaidName.setText(String.valueOf(searchMaidBulkModel.maidCount) + " Maid");
                } else {
                    binding.tvMaidName.setText(String.valueOf(searchMaidBulkModel.maidCount) + " Maids ");
                }
                //  SimpleDateFormat format = new SimpleDateFormat("EEE, MMM dd", Locale.ENGLISH);
                SimpleDateFormat format = new SimpleDateFormat("EEEE, MMMM dd,yyyy", Locale.ENGLISH);
                try {
                    Calendar calendar = Calendar.getInstance();
                    String year = String.valueOf(calendar.get(Calendar.YEAR));
                    Date newDate = format.parse(searchMaidBulkModel.selectedDate + ", " + year);
                    format = new SimpleDateFormat("E, MMM d", Locale.ENGLISH);
                    String date = format.format(newDate);

                    binding.tvTime.setText(date + " at " + searchMaidBulkModel.selectedTime);

                    //binding.tvTime.setText(format.format(format.parse(searchMaidBulkModel.selectedDate)) + " at " + searchMaidBulkModel.selectedTime);

                    binding.tvDate.setText(format.format(format.parse(searchMaidBulkModel.selectedDate)));
                    binding.tvShowAll.setVisibility(View.GONE);
                    binding.tvDate.setVisibility(View.GONE);
                  /*  binding.tvDate.setText(String.format("%s at %s", format.format(format.parse(searchMaidBulkModel.selectedDate)),
                            searchMaidBulkModel.selectedTime));*/
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                FullAddress fullAddress = Prefs.with(getActivity()).getObject(Constants.MAP_FULL_ADDRESS, FullAddress.class);

                if (reschuleStatus.equalsIgnoreCase("yes")) {
                    if (Prefs.get().getString(USER_COUNTRY, "").equalsIgnoreCase("BH")) {

                        if (fullAddress.buildingNumber.isEmpty()) {
                            setStringStyleeColon(fullAddress.villaName + ", " + fullAddress.buildingName + ", " +
                                    fullAddress.streetName + ", " + fullAddress.city + ", " + fullAddress.country, binding.tvDuration);
                        } else {
                            setStringStyleeColon(fullAddress.villaName + ", " + fullAddress.buildingName + ", " +
                                    fullAddress.buildingNumber + ", " + fullAddress.streetName + ", " +
                                    fullAddress.city + ", " + fullAddress.country, binding.tvDuration);
                        }

                    } else {
                        if (fullAddress.buildingName.isEmpty()) {
                            setStringStyleeColon(fullAddress.buildingNumber + ", " + fullAddress.streetName + ", " + fullAddress.postalCode + "\n" +
                                    fullAddress.city + ", " + fullAddress.country, binding.tvDuration);
                        } else {
                            setStringStyleeColon(fullAddress.buildingName + ", " +
                                    fullAddress.buildingNumber + ", " + fullAddress.streetName + ", " + fullAddress.postalCode + "\n" +
                                    fullAddress.city + ", " + fullAddress.country, binding.tvDuration);
                        }
                    }
                }
                else {
                    if (Prefs.get().getString(USER_COUNTRY, "").equalsIgnoreCase("BH")) {
                        if (fullAddress.buildingNumber.isEmpty()) {
                            setStringStyleeColon(fullAddress.villaName + ", " + fullAddress.buildingName + ", " +
                                    fullAddress.streetName + ", " + fullAddress.city + ", " + fullAddress.country, binding.tvDuration);
                        } else {
                            setStringStyleeColon(fullAddress.villaName + ", " + fullAddress.buildingName + ", " +
                                    fullAddress.buildingNumber + ", " + fullAddress.streetName + ", " +
                                    fullAddress.city + ", " + fullAddress.country, binding.tvDuration);
                        }
                    } else {
                        if (fullAddress.buildingName.isEmpty()) {
                            setStringStyleeColon(fullAddress.buildingNumber + ", " + fullAddress.streetName + ", " + fullAddress.postalCode + "\n" +
                                    fullAddress.city + ", " + fullAddress.country, binding.tvDuration);
                        } else {
                            setStringStyleeColon(fullAddress.buildingName + ", " +
                                    fullAddress.buildingNumber + ", " + fullAddress.streetName + ", " + fullAddress.postalCode + "\n" +
                                    fullAddress.city + ", " + fullAddress.country, binding.tvDuration);
                        }
                    }
                }

                temp_duration = String.valueOf(searchMaidBulkModel.duration);
                temp_currency = searchMaidData.getData().getRequestedMaids().get(0).getCurrency();
                agency_id = searchMaidData.getData().getRequestedMaids().get(0).getAgencyId();
                Log.e("currency===========", temp_currency);
                setStringStyleeColon(String.format("%s %s", searchMaidBulkModel.duration, getString(R.string.concat_hour)), binding.tvAddresss);
                if (searchMaidData.getData().getRequestedMaids().get(0).getActualPrice() != null) {

                  /*  if (Prefs.get().getString(USER_COUNTRY, "").equalsIgnoreCase("Bahrain")) {
                        maidprice=totalper;
                    }else {
                        try {
                            maidprice=Float.parseFloat(String.format("%.2f",totalper));
                        }catch (Exception e){

                        }
                    }*/
                    // rounded maid price
                    if (searchMaidData.getData().getRequestedMaids().get(0).getCurrency().equalsIgnoreCase("BHD")) {
                        maidprice = searchMaidData.getData().getRequestedMaids().get(0).getActualPrice();
                    } else {
                        maidprice = Float.parseFloat(String.format("%.2f", searchMaidData.getData().getRequestedMaids().get(0).getActualPrice()));
                    }
                    totalValue = searchMaidBulkModel.maidCount * searchMaidBulkModel.duration * maidprice;
                    // Float total = searchMaidBulkModel.duration * searchMaidModel.maidPrice*searchMaidBulkModel.maidCount;

                    // rounded Totalvalue
                    if (searchMaidData.getData().getRequestedMaids().get(0).getCurrency().equalsIgnoreCase("BHD")) {
                        total = totalValue;
                    } else {
                        total = totalValue;
                    }

                    amount_temp = String.valueOf(total);
                    totalpayment = total;

                    // vat value comment and not show
                    Log.d(TAG, "setBulkData: new vat value " + searchMaidData.getData().getRequestedMaids().get(0).getNew_vat());
                    if (searchMaidData.getData().getRequestedMaids().get(0).getNew_vat()/*getVat()*/.equalsIgnoreCase("0")) {
                        binding.amoutDetails.txtvat.setVisibility(View.GONE);
                        binding.amoutDetails.tvVat.setVisibility(View.GONE);
                        binding.amoutDetails.tvTotalAmount.setText(getString(R.string.total_amount));
                        binding.amoutDetails.tvFinalAmount.setText(getString(R.string.final_price_inc_vat));
                    } else {
                        binding.amoutDetails.txtvat.setVisibility(View.VISIBLE);
                        binding.amoutDetails.tvVat.setVisibility(View.VISIBLE);
                        binding.amoutDetails.tvTotalAmount.setText(getString(R.string.total_amount) + " (excl. VAT)");
                        binding.amoutDetails.tvVat.setText(getString(R.string.vat) + " (" + "@" + searchMaidData.getData().getRequestedMaids().get(0)./*getVat()*/getNew_vat() + "%" + ")");
                        binding.amoutDetails.tvFinalAmount.setText(getString(R.string.final_price_inc_vat) + " (incl. VAT)");
                    }

                    vat = Float.parseFloat(searchMaidData.getData().getRequestedMaids().get(0).getNew_vat()/*getVat()*/);

                    Log.d(TAG, "setData: vat:-- " + vat);
//                    totalValue = total / (((vat) + 100) / 100);
                    exclVatAmount = total / (((vat) + 100) / 100);

//                    per = (totalValue * (vat)) / 100;   //vat
                    per =   total- exclVatAmount;   //vat
                    totalper = exclVatAmount + per;  //final amount

                    Log.e(TAG, "setBulkData: ");
                    Log.e(TAG, "setBulkData: per:-- " + per);


                    if (searchMaidData.getData().getRequestedMaids().get(0).getCurrency().equalsIgnoreCase("BHD")) {
                        setStringStyleeColon(String.format(Locale.ENGLISH, "%s %.3f", searchMaidData.getData().getRequestedMaids().get(0).getCurrency(), totalper), binding.amoutDetails.finalAmountTxtview);
                        setStringStyleeColon(String.format(Locale.ENGLISH, "%s %.3f", searchMaidData.getData().getRequestedMaids().get(0).getCurrency(), exclVatAmount), binding.amoutDetails.pricetotal);
                        setStringStyleeColon(String.format(Locale.ENGLISH, "%s %.3f", searchMaidData.getData().getRequestedMaids().get(0).getCurrency(), per), binding.amoutDetails.txtvat);
                    } else {
                        setStringStyleeColon(String.format(Locale.ENGLISH, "%s %.2f", searchMaidData.getData().getRequestedMaids().get(0).getCurrency(), totalper), binding.amoutDetails.finalAmountTxtview);
                        setStringStyleeColon(String.format(Locale.ENGLISH, "%s %.2f", searchMaidData.getData().getRequestedMaids().get(0).getCurrency(), exclVatAmount), binding.amoutDetails.pricetotal);
                        setStringStyleeColon(String.format(Locale.ENGLISH, "%s %.2f", searchMaidData.getData().getRequestedMaids().get(0).getCurrency(), per), binding.amoutDetails.txtvat);
                    }

                    setStringStylee(String.format(Locale.ENGLISH, "%s %.3f", searchMaidData.getData().getRequestedMaids().get(0).getCurrency(), total), binding.amoutDetails.priceTxtview);
                    setStringStyleeColon(String.format(Locale.ENGLISH, "%s %.3f", searchMaidData.getData().getRequestedMaids().get(0).getCurrency(), searchMaidData.getData().getRequestedMaids().get(0).getActualPrice()), binding.tvPrice);

                }

                if (searchMaidData.getData().getRequestedMaids().get(0).agencyImage != null && searchMaidData.getData().getRequestedMaids().get(0).agencyImage.getOriginal() != null) {
                    Glide.with(getActivity())
                            .load(searchMaidData.getData().getRequestedMaids().get(0).agencyImage.getOriginal())
                            .circleCrop()
                            .placeholder(R.drawable.ic_user_pic)
                            .into(binding.ivMaid);
                } else {
                    Glide.with(getActivity())
                            .load(R.drawable.ic_user_pic)
                            .circleCrop()
                            .placeholder(R.drawable.ic_user_pic)
                            .into(binding.ivMaid);
                }

            } else {
                // removed  new DialogPopup().alertPopupp(getActivity(), getResources().getString(R.string.no_maid_found), getString(R.string.as_a_platform_providing_the_marketplace), "").show();
            }

            if (reschuleStatus.equalsIgnoreCase("yes")) {
                binding.tvProceedPayment.setText(getResources().getString(R.string.reschedule));
            }
            binding.rlInfo.setVisibility(View.GONE);
        } else {
        }

        //end show selected maid data

        setAddressData("");

        //helps to scroll
        binding.etAddressDescription.setMovementMethod(new ScrollingMovementMethod());
        binding.scrollView.setOnTouchListener(new View.OnTouchListener() {
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

    private void setAddressData(String placeAddress) {
        if (isHome || bookAgain) {
            Geocoder geocoder = new Geocoder(getActivity(), Locale.ENGLISH);

            List<Address> addressList = null;
            try {
                addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            } catch (IOException | NullPointerException ioe) {
                ioe.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                try {//----------- IF FIREBASE RETURN EXCEPTION
                    FirebaseCrashlytics.getInstance().recordException(e);
                } catch (Exception fe) {
                    fe.printStackTrace();
                }
            }

            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                Log.d(TAG, "setAddressData: latitude:- " + latLng.latitude + ", longitude:- " + latLng.longitude);
                Log.d(TAG, "Address data place address:- " + placeAddress);
                Log.d(TAG, "Address data" + addressList.toString());

                if (address.getLocality() == null || address.getLocality().equals("")) {
                    try {
                        String currentString = address.getAddressLine(0).split(",")[1];
                        StringTokenizer st = new StringTokenizer(currentString, " ");
                        String city = st.nextToken();
                        Log.e(TAG, "City data " + city + "===" + placeAddress);
                        binding.etCity.setText(placeAddress);
                    }catch (Exception e){
                        e.printStackTrace();
                        try{//--- Record Exception to firebase crahlitics
                           FirebaseCrashlytics.getInstance().recordException(new Throwable("Excepton on Search address : "+placeAddress+"  and Exception is : " + e));
                        }catch (Exception ex){
                            ex.printStackTrace();
                        }
                    }
                } else
                    binding.etCity.setText(address.getLocality());
                //   binding.etRoadNumber.setText(address.getAddressLine(0).split(",")[1]);
                binding.etBlockNumber.setText("");
                binding.etRoadNumber.setText("");
                binding.etApartment.setText("");
                binding.etFlatnumber.setText("");
                binding.etbuildingnumber.setText("");
                binding.etAddressDescription.setText(" ");

                // binding.tvAddress.setText(address.getAddressLine(0));

                country = address.getCountryName();
                country_code = address.getCountryCode();
             /*   if (address.getThoroughfare()==null || address.getThoroughfare().isEmpty()){
                    binding.tvAddress.setText(address.getSubLocality()+","+address.getLocality());
                } else {
                    binding.tvAddress.setText(address.getThoroughfare()+","+address.getLocality()+" "+address.getPostalCode()+","+address.getCountryName());
                }*/

                Prefs.with(getActivity()).save(Constants.COUNTRY_NAME, country);
                Prefs.with(getActivity()).save(USER_COUNTRY, address.getCountryCode());

                Log.e(TAG, "Address data " + address.getCountryCode());

                //visible field according to country
                setVisibleaddressField(address.getCountryCode());
                binding.etzipcode.setText(address.getPostalCode());
                binding.etStreet.setText(address.getThoroughfare());

                //binding.adLay.setVisibility(View.VISIBLE);
                binding.tvProceedPayment.setVisibility(View.VISIBLE);

            }

         /*   if (!block_temp.equalsIgnoreCase("")) {
                binding.etRoadNumber.setText(road_temp);
                binding.etCity.setText(city_temp);
                //   binding.tvAddress.setText(getString(R.string.search_service_location));
                binding.etBlockNumber.setText(block_temp);
                binding.etApartment.setText(apartment_temp);
                binding.etAddressDescription.setText(" ");
            }*/

        } else {
            binding.rlInfo.setVisibility(View.VISIBLE);

            if (reschuleStatus.equalsIgnoreCase("yes")) {
                binding.etBlockNumber.setText(bookingDataModel.address.buildingName);
                binding.etCity.setText(bookingDataModel.address.city);
                binding.etRoadNumber.setText(bookingDataModel.address.streetName);
                binding.etApartment.setText(bookingDataModel.address.villaName);
                if (bookingDataModel.address.moreDetailedaddress != null)
                    binding.etAddressDescription.setText(bookingDataModel.address.moreDetailedaddress);

            } else {
                FullAddress fullAddress = Prefs.with(getActivity()).getObject(Constants.MAP_FULL_ADDRESS, FullAddress.class);
                binding.etBlockNumber.setText(fullAddress.buildingName);
                binding.etCity.setText(fullAddress.city);
                binding.etRoadNumber.setText(fullAddress.streetName);
                binding.etApartment.setText(fullAddress.villaName);
                if (fullAddress.moreDetailedaddress != null)
                    binding.etAddressDescription.setText(fullAddress.moreDetailedaddress);
            }
        }
    }

    private void setVisibleaddressField(String country) {

        if (country.equalsIgnoreCase("BH")) {
            binding.tvZipcode.setVisibility(View.GONE);
            binding.etzipcode.setVisibility(View.GONE);
            binding.tvFlatnumber.setVisibility(View.GONE);
            binding.etFlatnumber.setVisibility(View.GONE);
            binding.tvBuildingnumber.setVisibility(View.VISIBLE);
            binding.etbuildingnumber.setVisibility(View.VISIBLE);
            binding.tvStreet.setVisibility(View.GONE);
            binding.etStreet.setVisibility(View.GONE);

            binding.tvRoadNumber.setVisibility(View.VISIBLE);
            binding.etRoadNumber.setVisibility(View.VISIBLE);
            binding.tvBlockNumber.setVisibility(View.VISIBLE);
            binding.etBlockNumber.setVisibility(View.VISIBLE);
            binding.tvApartment.setVisibility(View.VISIBLE);
            binding.etApartment.setVisibility(View.VISIBLE);

            binding.tvBuildingnumber.setText(getString(R.string.building_number));
            binding.etRoadNumber.setText("");
            binding.etbuildingnumber.setText("");
            binding.etBlockNumber.setText("");
            binding.etApartment.setText("");
            binding.etAddressDescription.setText("");


        } else {
            binding.tvZipcode.setVisibility(View.VISIBLE);
            binding.etzipcode.setVisibility(View.VISIBLE);
            binding.tvFlatnumber.setVisibility(View.VISIBLE);
            binding.etFlatnumber.setVisibility(View.VISIBLE);
            binding.tvBuildingnumber.setVisibility(View.VISIBLE);
            binding.etbuildingnumber.setVisibility(View.VISIBLE);
            binding.tvStreet.setVisibility(View.VISIBLE);
            binding.etStreet.setVisibility(View.VISIBLE);

            binding.tvRoadNumber.setVisibility(View.GONE);
            binding.etRoadNumber.setVisibility(View.GONE);
            binding.tvBlockNumber.setVisibility(View.GONE);
            binding.etBlockNumber.setVisibility(View.GONE);
            binding.tvApartment.setVisibility(View.GONE);
            binding.etApartment.setVisibility(View.GONE);
            binding.tvBuildingnumber.setText(getString(R.string.building_number_uk));
            binding.etStreet.setText("");
            binding.etbuildingnumber.setText("");
            binding.etFlatnumber.setText("");
            binding.etzipcode.setText("");
            binding.etAddressDescription.setText("");
        }
    }

    private void setListeners() {
        binding.llLocation.setOnClickListener(this);
        binding.tvSameAddress.setOnClickListener(this);
        binding.tvProceedPayment.setOnClickListener(this);
        binding.tvAddress.setOnClickListener(this);
        //    binding.etCity.setOnClickListener(this);
        binding.tvCurrent.setOnClickListener(this);
        binding.tvShowAll.setOnClickListener(this);
        //  binding.promoErrorCross.setOnClickListener(this);
        binding.amoutDetails.applyTitle.setOnClickListener(this);
        binding.amoutDetails.promoApplyBtn.setOnClickListener(this);
        binding.amoutDetails.promoCross.setOnClickListener(this);
        binding.amoutDetails.promoEdittext.setOnClickListener(this);
        binding.tvAddress.setOnClickListener(this);
        binding.etRoadNumber.setOnClickListener(this);
        binding.etBlockNumber.setOnClickListener(this);
        binding.etApartment.setOnClickListener(this);
        binding.etAddressDescription.setOnClickListener(this);
    }

    private LocationRequest locationRequest;
    private LocationCallback mLocationCallback;


    private void getLocatoinUpdate() {

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mFusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, null);
    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;


    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {

                ActivityCompat.requestPermissions(getActivity(),
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

    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
    }


    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Toast.makeText(getActivity(), "Turn on location", Toast.LENGTH_SHORT).show();
            return;
        }

        mFusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    // Logic to handle location object
                    Log.d(TAG, "Latitude" + String.valueOf(location.getLatitude()));
                    Log.d(TAG, "Latitude" + String.valueOf(location.getLongitude()));

                    Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                    latLng = new LatLng(location.getLatitude(), location.getLongitude());

                    List<Address> addressList = null;
                    try {
                        addressList = geocoder.getFromLocation(
                                latLng.latitude, latLng.longitude, 1);
                    } catch (IOException | NullPointerException ioe) {
                        ioe.printStackTrace();
                    }

                    if (addressList != null && addressList.size() > 0) {
                        Address address = addressList.get(0);
                        Log.e(TAG, "Address data " + addressList.toString());

                        if (address.getLocality() == null || address.getLocality().equals(""))
                            binding.etCity.setText(address.getSubAdminArea());

                        else
                            binding.etCity.setText(address.getLocality());
                        country = address.getCountryName();
                        country_code = address.getCountryCode();
                        Prefs.with(getActivity()).save(Constants.COUNTRY_NAME, country);
                        Prefs.with(getActivity()).save(USER_COUNTRY, address.getCountryCode());

                        //visible field according to country
                        setVisibleaddressField(address.getCountryCode());
                        binding.etRoadNumber.setText("");

                        if (address.getThoroughfare() != null) {
                            binding.tvAddress.setText(address.getThoroughfare() + "," + address.getAdminArea() + "," + address.getPostalCode() + "," + address.getCountryName());
                            binding.etStreet.setText(address.getThoroughfare());
                            binding.etzipcode.setText(address.getPostalCode());
                        } else {
                            binding.tvAddress.setText(address.getAddressLine(0));
                            binding.etStreet.setText(address.getFeatureName());
                            binding.etzipcode.setText(address.getPostalCode());
                        }

                        if (address.getFeatureName() != null) {
                            binding.etApartment.setText("");
                        }

                        if (address.getSubLocality() != null) {
                            binding.etBlockNumber.setText("");
                        }
                        binding.etAddressDescription.setText("");

                        if (address.getAddressLine(0) != null) {
                            //binding.etAddressDescription.setText(address.getAddressLine(0));
                        }
                    }
                    binding.adLay.setVisibility(View.VISIBLE);
                    binding.tvProceedPayment.setVisibility(View.VISIBLE);
                } else {
                    //   Toast.makeText(getActivity(), "Location not found", Toast.LENGTH_SHORT).show();
                    getLocatoinUpdate();
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Log.e(TAG, "reeeeee " + requestCode + "");
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        //Request location updates:
                        if (GPSStatus()) {
                            getCurrentLocation();
                        } else {
                            // getCurrentLocation();
                            Intent intent1 = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent1);
                        }
                    }

                } else {
                  //  Toast.makeText(getActivity(), "Permission denied", Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                    showDialog(getString(R.string.permissions_required_location),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case DialogInterface.BUTTON_POSITIVE:
                                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            Uri uri = Uri.fromParts(Constants.SETTING_URI_SCHEME,
                                                    requireContext().getPackageName(), null);
                                            intent.setData(uri);
                                            startActivityForResult(intent, Constants.REQUEST_CODE_SETTINGS);
                                            break;
                                    }
                                }
                            }, Constants.NEVER_ASK);//take to settings
                }
                break;
        }
    }


    private void showDialog(String message, DialogInterface.OnClickListener okListener, String from) {
        if (from.equals(Constants.DENY)) {
            IOSAlertDialog dialog =  IOSAlertDialog.newInstance(
                    requireContext(),
                    getString(R.string.title_permission_dialog),
                    message,
                    getString(R.string.button_ok_permission_dialog),
                    getString(R.string.cancel1),
                    okListener,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    },
                    ContextCompat.getColor(requireContext(),R.color.app_color),
                    ContextCompat.getColor(requireContext(),R.color.coral),
                    true
            );
            dialog.show(requireActivity().getSupportFragmentManager(), "ios_dialog");;
        }else{
            IOSAlertDialog dialog =  IOSAlertDialog.newInstance(
                    requireContext(),
                    getString(R.string.title_permission_dialog),
                    message,
                    getString(R.string.button_setting_permission_dialog),
                    getString(R.string.cancel1),
                    okListener,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    },
                    ContextCompat.getColor(requireContext(),R.color.app_color),
                    ContextCompat.getColor(requireContext(),R.color.coral),
                    true
            );
            dialog.show(requireActivity().getSupportFragmentManager(), "ios_dialog");;
        }

      /*  AlertDialog.Builder alertDialog = new AlertDialog.Builder(requireContext())
                .setMessage(message).setCancelable(true);

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
        }*/

    }


    LocationManager locationManager;
    boolean GpsStatus;

    public boolean GPSStatus() {
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getActivity().onBackPressed();
        }
        return true;
    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {


        IOSAlertDialog dialog =  IOSAlertDialog.newInstance(
                requireContext(),
                getString(R.string.title_permission_dialog),
                message,
                getString(R.string.button_ok_permission_dialog),
                getString(R.string.cancel1),
                okListener,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                },
                ContextCompat.getColor(requireContext(),R.color.app_color),
                ContextCompat.getColor(requireContext(),R.color.app_color),
                true
        );
        dialog.show(requireActivity().getSupportFragmentManager(), "ios_dialog");;

       /* AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity())
                .setMessage(message).setCancelable(true);
        alertDialog.setTitle(R.string.title_permission_dialog)
                .setPositiveButton(R.string.button_ok_permission_dialog, okListener)
                .create()
                .show();*/

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvAddress:
                mFirebaseAnalytics.logEvent("lctscr_tap_ssl", null);
                road_temp = "";
                city_temp = "";
                block_temp = "";
                apartment_temp = "";
                full_address_temp = "";
                buildingNumber = "";
                postalCode = "";

                android.util.Log.e(TAG, "onClick: search Address" );
                // List<com.google.android.libraries.places.api.model.Place.Field> placeFieldss = new ArrayList<>(Arrays.asList(com.google.android.libraries.places.api.model.Place.Field.ADDRESS));
                List<Place.Field> placeFieldss = Arrays.asList(Place.Field.LAT_LNG, Place.Field.ADDRESS, Place.Field.ADDRESS_COMPONENTS);
                Intent autocompleteIntentt =
                        new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, placeFieldss)
                                //.setLocationBias(bounds)
                                // .setTypeFilter(typeFilters.get(3))
                                .build(getActivity());
                startActivityForResult(autocompleteIntentt, 1001);

                break;

            case R.id.tvShowAll:
                Intent intent = new Intent(getActivity(), PickerShowActivity.class);
                intent.putExtra("type", "view");
                startActivity(intent);

                break;

            case R.id.apply_title:
                if (binding.amoutDetails.promoInputLay.getVisibility() == View.VISIBLE) {
                } else {
                    binding.amoutDetails.promoInputLay.setVisibility(View.VISIBLE);
                }

                break;

            case R.id.promoApplyBtn:
                mFirebaseAnalytics.logEvent("mdprscr_tap_prc", null);
                applyPromoApi();
                break;

            case R.id.promoCross:
                disablePromoUI();
                break;
            case R.id.tvCurrent:
                mFirebaseAnalytics.logEvent("lctscr_tap_usecl", null);
                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

                if (checkLocationPermission()) {
                    // Log.e("1", "1");
                    if (GPSStatus()) {
                        //Log.e("2", "2");
                        //  commonFunction.showProgressDialog(getActivity());

                        getCurrentLocation();
                    } else {
                        //Log.e("3", "3");
                        showDialogOK(getString(R.string.gps_required),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which) {
                                            case DialogInterface.BUTTON_POSITIVE:
                                                Intent intent1 = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                                startActivity(intent1);
                                                break;
                                            case DialogInterface.BUTTON_NEGATIVE:

                                                break;
                                        }
                                    }
                                });

                    }
                } else {

                }

                break;

            case R.id.tvProceedPayment:

                if (GeneralFunction.isNetworkConnected(requireActivity(), binding.scrollView)) {
                    // Reschedule status chk
                    Log.d(TAG, "onClick: reshedule:--  " + String.valueOf(reschuleStatus));
                    if (reschuleStatus.equalsIgnoreCase("yes")) {

                        mFirebaseAnalytics.logEvent("mdprscr_click_cht", null);

                        HashMap<String, String> hashMap = new HashMap<>();

                        hashMap.put("agencyId", String.valueOf(searchMaidBulkModel.agencyId));
                        hashMap.put("hour", String.valueOf(searchMaidBulkModel.hour));
                        hashMap.put("uniquieAppKey", bookingDataModel.uniquieAppKey);
                        hashMap.put("startTime", String.valueOf(searchMaidBulkModel.startTime));
                        hashMap.put("serviceId", serviceId);
                        hashMap.put("workDate", String.valueOf(searchMaidBulkModel.workDate));
                        presenter.apiRescheduleBulk(hashMap, Prefs.with(getActivity()).getString(Constants.ACCESS_TOKEN, ""), reschuleStatus);

                    }
                    else {
                        Log.d(TAG, "onClick: reschedule status false");
                        DataVariable.hideSoftKeyboard(getActivity());
                        // is home and bookAgain status chk
                        if (isHome || bookAgain) {
                            Log.d(TAG, "onClick: isHome || bookAgain");
                            mFirebaseAnalytics.logEvent("lctscr_tap_sad", null);
                            //either address selected in the list or mannual entered data
                            if (selectedFullAddress == null) {
                                Log.d(TAG, "onClick: selectedFullAddress == null");
                                if (!validData()) {
                                    return;
                                }
                            }

                            FullAddress mapAddress;
                            if (selectedFullAddress == null) {
                                mapAddress = new FullAddress();
                                if (latLng != null) {
                                   // mapAddress.mapLatLng = latLng;
                                    mapAddress.lat = latLng.latitude;
                                    mapAddress.lng = latLng.longitude;
                                }
                              //  mapAddress.address = binding.tvAddress.getText().toString();
                                mapAddress.city = binding.etCity.getText().toString().trim();
                                if (Prefs.get().getString(USER_COUNTRY, "").equalsIgnoreCase("BH")) {
                                    mapAddress.streetName = binding.etRoadNumber.getText().toString().trim();
                                    mapAddress.buildingNumber = binding.etbuildingnumber.getText().toString().trim();
                                    mapAddress.buildingName = binding.etBlockNumber.getText().toString().trim();
                                    mapAddress.villaName = binding.etApartment.getText().toString().trim();
                                } else {
                                    mapAddress.streetName = binding.etStreet.getText().toString().trim();
                                    mapAddress.buildingNumber = binding.etbuildingnumber.getText().toString().trim();
                                    mapAddress.buildingName = binding.etFlatnumber.getText().toString().trim();
                                    mapAddress.postalCode = binding.etzipcode.getText().toString().trim();
                                }

                                if (!binding.etAddressDescription.getText().toString().trim().isEmpty()) {
                                    mapAddress.moreDetailedaddress = binding.etAddressDescription.getText().toString().trim();
                                }
                                mapAddress.country = country;

                            }
                            else {
                                mapAddress = selectedFullAddress;
                            }
                            road_temp = mapAddress.streetName;
                            block_temp = mapAddress.buildingName;
                            city_temp = mapAddress.city;
                            apartment_temp = mapAddress.villaName;
                            full_address_temp = mapAddress.moreDetailedaddress;
                            buildingNumber = mapAddress.buildingNumber;
                            postalCode = mapAddress.postalCode;
                            Log.e("addressss", new Gson().toJson(mapAddress) + "");
                            Prefs.with(getActivity()).save(Constants.MAP_FULL_ADDRESS, mapAddress);

                            //further flow according to the save address checkbox
                            if (binding.cbSaveCard.isChecked()) {
                                mapAddress.uniquieAppKey = Constants.UNIQUE_APP_KEY;
                                mapAddress.mapLatLng = null;
                                mapAddress.address = null;
                                mapAddress.id = null;
                                Log.e("Fdddfdfd", new Gson().toJson(mapAddress) + "");
                                presenter.apiSaveAddress(mapAddress);

                                return;
                            } else {
                                Log.d(TAG, "onClick: else {  binding.cbSaveCard.isChecked()");
                                // return back to the previous screen
                                Fragment targetFragment = getTargetFragment(); // HomeFragment in our case
                                if (targetFragment != null) {
                                    Intent data = null;
                                    if (isHome) {
                                        Log.d(TAG, "onClick: if (isHome) {");
                                        data = new Intent();
                                        data.putExtra(Constants.SIGN_UP, true);
                                    }
                                    targetFragment.onActivityResult(getTargetRequestCode(), getActivity().RESULT_OK, data);
                                }
                                getActivity().onBackPressed();
                            }

                        }

                        //-----------------------------------else isHome || bookAgain------------------------------------------------
                        else {
                            // continue with payment flow
                            if (booking_type == 3) {

                                if (searchMaidBulkModel != null) {
                                    final String serviceId = getArguments().getString(Constants.SERVICE_ID);
                                    final String referenceId = getArguments().getString("referenceId");
                                    searchMaidBulkModel.maidId = searchMaidData1.getData().getRequestedMaids().get(0).get_id();
                                    searchMaidBulkModel.currency = searchMaidData1.getData().getRequestedMaids().get(0).getCurrency();
                                    searchMaidBulkModel.maidPrice = searchMaidData1.getData().getRequestedMaids().get(0).getActualPrice();
                                    searchMaidBulkModel.agencyName = searchMaidData1.getData().getRequestedMaids().get(0).getAgencyName();
                                    searchMaidBulkModel.makId = searchMaidData1.getData().getRequestedMaids().get(0).getMakId();
                                    searchMaidBulkModel.documentPicURL = searchMaidData1.getData().getRequestedMaids().get(0).getDocumentPicURL();
                                    searchMaidBulkModel.makId = searchMaidData1.getData().getRequestedMaids().get(0).getMakId();
                                    searchMaidBulkModel.profilePicURL = searchMaidData1.getData().getRequestedMaids().get(0).getProfilePicURL();

                                    if (serviceId != null) {
                                        Config.TRANSACTION_ID = "";
                                        Config.TAP_ID = "";
                                        Intent intent1 = new Intent(getActivity(), ExtendPaymentActivity.class);
                                        intent1.putExtra(Constants.SERVICE_ID, serviceId + "");
                                        intent1.putExtra("referenceId", ref_id);
                                        intent1.putExtra("isExtension", "false");
                                        intent1.putExtra(Constants.SEARCH_MAID_DATA, searchMaidBulkModel);
                                        intent1.putExtra(Constants.BOOK_AGAIN, true);
                                        if (getArguments().containsKey(Constants.MAID_AVAILABLE_TIMESLOT))
                                            intent1.putExtra(Constants.MAID_AVAILABLE_TIMESLOT, String.valueOf(getArguments().getParcelable(Constants.MAID_AVAILABLE_TIMESLOT)));
                                        startActivity(intent1);
                                    } else {

                                        getActivity().getSupportFragmentManager().beginTransaction()
                                                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                                                .replace(R.id.frameLayout, PaymentStateFragment.newInstance(searchMaidBulkModel), "PaymentStateFragment")
                                                .addToBackStack("PaymentStateFragment").commit();
                                    }
                                }

                            }
                            else {

                                if (searchMaidModel != null) {
                                    final String serviceId = getArguments().getString(Constants.SERVICE_ID);

                                    if (serviceId != null) {
                         /*           Config.TRANSACTION_ID = "";
                                    Config.TAP_ID = "";
                                    Intent intent1 = new Intent(getActivity(), ExtendPaymentActivity.class);
                                    intent1.putExtra(Constants.SERVICE_ID, serviceId + "");
                                    intent1.putExtra("referenceId", ref_id);
                                    intent1.putExtra("isExtension","false");
                                    intent1.putExtra(Constants.SEARCH_MAID_DATA, searchMaidModel);
                                    intent1.putExtra(Constants.BOOK_AGAIN, true);
                                    if (getArguments().containsKey(Constants.MAID_AVAILABLE_TIMESLOT))
                                        intent1.putExtra(Constants.MAID_AVAILABLE_TIMESLOT, String.valueOf(getArguments().getParcelable(Constants.MAID_AVAILABLE_TIMESLOT)));
                                    startActivity(intent1);*/
                                        getActivity().getSupportFragmentManager().beginTransaction()
                                                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                                                .replace(android.R.id.content, PaymentStateFragment.newInstance(searchMaidModel, serviceId), "PaymentStateFragment")
                                                .addToBackStack("PaymentStateFragment").commit();
                                    } else {
                                        getActivity().getSupportFragmentManager().beginTransaction()
                                                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                                                .replace(R.id.frameLayout, PaymentStateFragment.newInstance(searchMaidModel), "PaymentStateFragment")
                                                .addToBackStack("PaymentStateFragment").commit();
                                    }
                                }
                            }
                        }
                    }
                } else {
                    setLoading(false);
                }
                break;

            case R.id.tvSameAddress:

                sameAddress = !sameAddress;
                if (sameAddress) {
                    binding.tvSameAddress.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.rect_border_sky_white));
                    binding.tvSameAddress.setCompoundDrawablesWithIntrinsicBounds(getContext().getDrawable(R.drawable.ic_tick), null, null, null);
                    FullAddress fullAddress = Prefs.with(getActivity()).getObject(Constants.MAP_FULL_ADDRESS, FullAddress.class);

                    if (fullAddress != null) {
                        if (fullAddress.city != null) {
                            binding.etCity.setText(fullAddress.city);
                        }

                        if (fullAddress.streetName != null) {
                            binding.etRoadNumber.setText(fullAddress.streetName);
                        }


                        if (fullAddress.buildingName != null) {
                            binding.etBlockNumber.setText(fullAddress.buildingName);
                        }

                        if (fullAddress.villaName != null) {
                            binding.etApartment.setText(fullAddress.villaName);
                        }

                        if (fullAddress.moreDetailedaddress != null) {
                            binding.etAddressDescription.setText(fullAddress.moreDetailedaddress);
                        }
                    } else {
                        PojoLogin pojoLogin = Prefs.with(getActivity()).getObject(Constants.DATA, PojoLogin.class);
                        fullAddress = new FullAddress();
                        Location location = Prefs.with(getActivity()).getObject(Constants.LOCATION, Location.class);
                        latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        fullAddress.city = pojoLogin.billingAddress.city;
                        fullAddress.streetName = pojoLogin.billingAddress.streetName;
                        fullAddress.buildingName = pojoLogin.billingAddress.buildingName;
                        fullAddress.villaName = pojoLogin.billingAddress.villaName;
                        fullAddress.lat = latLng.latitude;
                        fullAddress.lng = latLng.longitude;
                        fullAddress.address = binding.tvAddress.getText().toString();
                        fullAddress.villaName = pojoLogin.billingAddress.villaName;
                        fullAddress.moreDetailedaddress = pojoLogin.billingAddress.moreDetailedaddress;
                        if (fullAddress.city != null) {
                            binding.etCity.setText(fullAddress.city);
                        }
                        if (fullAddress.streetName != null) {
                            binding.etRoadNumber.setText(fullAddress.streetName);
                        }
                        if (fullAddress.buildingName != null) {
                            binding.etBlockNumber.setText(fullAddress.buildingName);
                        }
                        if (fullAddress.villaName != null) {
                            binding.etApartment.setText(fullAddress.villaName);
                        }
                        if (fullAddress.moreDetailedaddress != null) {
                            binding.etAddressDescription.setText(fullAddress.moreDetailedaddress);
                        }
                    }
                } else {
                    binding.tvSameAddress.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.rect_white));
                    binding.tvSameAddress.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);

                    setData();
                }
                break;

            case R.id.promoEdittext:
                mFirebaseAnalytics.logEvent("mdprscr_complete_prc", null);
                break;

            case R.id.etRoadNumber:
                mFirebaseAnalytics.logEvent("lctscr_rn_complete", null);
                break;

            case R.id.etBlockNumber:
                mFirebaseAnalytics.logEvent("lctscr_bln_complete", null);
                break;

            case R.id.etApartment:
                mFirebaseAnalytics.logEvent("lctscr_aprt_complete", null);
                break;

            case R.id.etAddressDescription:
                mFirebaseAnalytics.logEvent("lctscr_lndm_complete", null);
                break;
        }
    }

    private void applyPromoApi() {
        if (binding.amoutDetails.promoEdittext.getText().toString().equalsIgnoreCase("")) {
            binding.amoutDetails.errorLay.setVisibility(View.VISIBLE);
            binding.amoutDetails.promoErrorTxtview.setText(getString(R.string.enter_promo_validation));
        } else {
            HashMap<String, String> map = new HashMap<>();
            map.put("uniquieAppKey", Constants.UNIQUE_APP_KEY);
            map.put("codeName", binding.amoutDetails.promoEdittext.getText().toString());
            map.put("duration", temp_duration);
            map.put("amount", amount_temp);


            if (booking_type == 1) {
                map.put("agencyId", searchMaidModel.agencyId);
                map.put("bookingType", "1");
            } else if (booking_type == 2) {
                map.put("agencyId", searchMaidModel.agencyId);
                map.put("bookingType", "2");
            } else if (booking_type == 3) {
                map.put("bookingType", "3");
                map.put("agencyId", searchMaidData1.getData().getRequestedMaids().get(0).getAgencyId());
            }

            Log.e("bookAgain status==", "" + bookAgain);

            Log.e("apply promo params:--  ", new Gson().toJson(map) + "");
            if (GeneralFunction.isNetworkConnected(getActivity(), binding.ivMaid)) {
                presenter.apiApplyPromo(map);
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PLACE_PICKER_REQUEST:
                    // Place place = PlacePicker.getPlace(getActivity(), data);
            /*        Place place = Autocomplete.getPlaceFromIntent(data);
                    // binding.tvAddress.setText(place.getAddress().toString());
                    Log.i("Location", "Place: " + place.getAddress() + ", " + place.getId());
                    latLng = place.getLatLng();*/

                    setAddressData("");
                    break;
                case 1001:
                    android.util.Log.e(TAG, "onActivityResult: case 1001" );
                    if (resultCode == RESULT_OK) {
                        com.google.android.libraries.places.api.model.Place place = Autocomplete.getPlaceFromIntent(data);
                        latLng = place.getLatLng();
                        binding.tvAddress.setText(place.getAddress().toString());
                        Log.d(TAG, "binding.tvAddress.setText(place.getAddress().toString());" + place.getAddress().toString());
                        binding.adLay.setVisibility(View.VISIBLE);


//updated by sahil
                        try {
                            setAddressData(place.getAddressComponents().asList().get(1).getName());
//                            Log.e(TAG,"locations (place.getAddressComponents()).asList().get((place.getAddressComponents().asList().size()-1)) :---   "+(place.getAddressComponents()).asList().get((place.getAddressComponents().asList().size()-1)).getName());
//
                            Log.d(TAG, "onActivityResult: address list name " + place.getAddressComponents().asList().get(2).getName());
//                         Log.d(TAG, "onActivityResult: address component"+place.getAddressComponents());
//                         Log.d(TAG,"address list name"+place.getAddressComponents().asList().get((0)).getName());
//
//                            Log.e(TAG,"place.getAddress():---   "+place.getAddress());
//                            Log.e(TAG, "LocatioPlace: " + place.getAddress() + ", " + place.getAddressComponents().asList().get(2));

                        } catch (Exception e) {
                            Log.d(TAG, "onActivityResult: address exception:-    " + e.getMessage().toString());
                            setAddressData((place.getAddressComponents()).asList().get(0).getName());
                            Log.d(TAG, "onActivityResult: Exception Address name list" + place.getAddressComponents().asList().get(0).getName());
                            //   Log.d(TAG, "onActivityResult: address component"+place.getAddressComponents());
                            //  Log.e(TAG, "Location Place: " + place.getAddress() + ", " + place.getAddressComponents().asList().get(0));
                            //  Log.d(TAG,"Exception address list name"+place.getAddressComponents().asList().get((0)));

                            //   Log.e(TAG,"Exectiption (place.getAddressComponents()).asList().get(0):---- "+(place.getAddressComponents()).asList().get(0));
                            //  Log.e(TAG,"Exception place.getAddress():--- "+place.getAddress() );
                        }
//updated by sahil
                        // Log.e("Location", "Place: " + place.getAddress() + ", " + place.getAddressComponents().asList().get(2).getName());

                        Log.i("Location", "LatLng: " + place.getLatLng());
                    } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                        // TODO: Handle the error.
                        Status status = Autocomplete.getStatusFromIntent(data);
                        Log.i("Location", status.getStatusMessage());
                    }

                    break;
            }
        }

    }

    public boolean validData() {
        String city = binding.etCity.getText().toString().trim();
        String area = binding.etRoadNumber.getText().toString().trim();
        String buildingName = binding.etBlockNumber.getText().toString().trim();
        String apartment = binding.etApartment.getText().toString().trim();

        String flat = binding.etFlatnumber.getText().toString().trim();
        String building = binding.etbuildingnumber.getText().toString().trim();
        String Street = binding.etStreet.getText().toString().trim();
        String postcode = binding.etzipcode.getText().toString().trim();

        if (country.equalsIgnoreCase("Bahrain")) {
            if (city.isEmpty()) {

                GeneralFunction.showSnackBar(getActivity(), binding.parent, getString(R.string.city_empty_validation));
                binding.etCity.requestFocus();
                return false;
            } else if (area.isEmpty()) {
                GeneralFunction.showSnackBar(getActivity(), binding.parent, getString(R.string.area_empty_validation));
                binding.etRoadNumber.requestFocus();
                return false;
            } else if (building.isEmpty()) {
                GeneralFunction.showSnackBar(getActivity(), binding.parent, getString(R.string.enter_building_number));
                binding.etbuildingnumber.requestFocus();
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

        } else {
            if (city.isEmpty()) {
                GeneralFunction.showSnackBar(getActivity(), binding.parent, getString(R.string.city_empty_validation));
                binding.etCity.requestFocus();
                return false;
            } /*else if (flat.isEmpty()) {
                GeneralFunction.showSnackBar(getActivity(), binding.parent, getString(R.string.please_enter_flat));
                binding.etFlatnumber.requestFocus();
                return false;
            }*/ else if (Street.isEmpty()) {
                GeneralFunction.showSnackBar(getActivity(), binding.parent, getString(R.string.enter_street_number));
                binding.etStreet.requestFocus();
                return false;
            } else if (building.isEmpty()) {
                GeneralFunction.showSnackBar(getActivity(), binding.parent, getString(R.string.enter_building_number));
                binding.etbuildingnumber.requestFocus();
                return false;
            } else if (postcode.isEmpty()) {
                GeneralFunction.showSnackBar(getActivity(), binding.parent, getString(R.string.enter_post_code));
                binding.etzipcode.requestFocus();
                return false;
            }

        }

        return true;
    }

    public void setStringStylee(@NonNull String temp, TextView tvText) {
        if (temp != null) {
            final SpannableStringBuilder str = new SpannableStringBuilder(temp);
            str.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.white)), temp.indexOf(":") + 1, temp.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvText.setText(str);
        }
    }

    public void setStringStyleeColon(@NonNull String temp, TextView tvText) {
        if (temp != null) {
            final SpannableStringBuilder str = new SpannableStringBuilder(temp);
            tvText.setText(str);

        }
    }

    @Override
    public void getAddress(FullAddress address) {
        selectedFullAddress = address;
        try {
            String code = getCountryCode(selectedFullAddress.country);
            Log.e("country code", code);
            Prefs.with(getActivity()).save(Constants.USER_COUNTRY, code);
            Prefs.with(getActivity()).save(Constants.COUNTRY_NAME, selectedFullAddress.country);
        } catch (Exception e) {

        }

    }

    private String getCountryCode(String country) {
        String[] isoCountryCodes = Locale.getISOCountries();
        for (String code : isoCountryCodes) {
            Locale locale = new Locale("", code);
            if (country.equalsIgnoreCase(locale.getDisplayCountry())) {
                return code;
            }
        }
        return "";
    }

    @Override
    public void deleteAddress(String id) {
        HashMap<String, String> map = new HashMap<>();
        map.put("uniquieAppKey", Constants.UNIQUE_APP_KEY);
        map.put("id", id);
        if (GeneralFunction.isNetworkConnected(getActivity(), binding.ivMaid)) {
            presenter.apiDeleteAddress(map);
        }
    }

    @Override
    public void setRefreshing(boolean isRefreshing) {
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
    public void searchMaidSuccessBulk(PojoSearchMaid searchMaidData) {
        binding.amoutDetails.llTotal.setVisibility(View.VISIBLE);
        Log.e("searchhhh res", "" + new Gson().toJson(searchMaidData));
        searchMaidData1 = searchMaidData;
        // this.searchMaidModel=searchMaidData;


        if (searchMaidBulkModel.maidCount > searchMaidData.getData().getAvailableMaidsCount()) {
            binding.tvProceedPayment.setEnabled(false);
            if (searchMaidData.getData().getAvailableMaidsCount() == 1) {
                alertPopupp(getActivity(), getString(R.string.maximum) + " " + searchMaidData.getData().getAvailableMaidsCount() + " " + getString(R.string.bulk_txt_1_maid) + "", getString(R.string.as_a_platform_providing_the_marketplace), "");
            } else {
                alertPopupp(getActivity(), getString(R.string.maximum) + " " + searchMaidData.getData().getAvailableMaidsCount() + " " + getString(R.string.bulk_txt) + "", getString(R.string.as_a_platform_providing_the_marketplace), "");
            }
        } else {
            binding.tvProceedPayment.setEnabled(true);
            setBulkData(searchMaidData);
        }
    }

    @Override
    public void applyPromoSuccess(PromoResponse promoResponse) {
        Log.d(TAG, "applyPromoSuccess: " + new Gson().toJson(promoResponse));
        Promo_id = promoResponse.data.id;
        Promo_name = promoResponse.data.codeName;
        Promo_desc = promoResponse.data.description;
        Promo_discount = promoResponse.data.discount;
        Discounted_price = Double.parseDouble(promoResponse.data.discountedAmount);
        Log.e("price", "" + Discounted_price);
        binding.amoutDetails.errorLay.setVisibility(View.GONE);
        enablePromoUI();
    }

    private void enablePromoUI() {
        binding.amoutDetails.promoInputLay.setVisibility(View.GONE);
        binding.amoutDetails.appliedLay.setVisibility(View.VISIBLE);
        binding.amoutDetails.amountPaidLay.setVisibility(View.VISIBLE);
        binding.amoutDetails.layExclapplypromo.setVisibility(View.VISIBLE);
        binding.amoutDetails.layDiscountAmount.setVisibility(View.VISIBLE);
        //binding.amountTxtview.setText(binding.priceTxtview.getText().toString());
        binding.amoutDetails.amountTxtview.setText(binding.amoutDetails.pricetotal.getText().toString());


        // Discount price rounded
        if (temp_currency.equalsIgnoreCase("BHD")) {
            totalValue = Discounted_price;
        } else {
            totalValue = Discounted_price;
        }

        if (booking_type == 3) {
            if (vat == 0) {
                binding.amoutDetails.tvexclVatAmount.setVisibility(View.GONE);
                binding.amoutDetails.txtexclVatAmount.setVisibility(View.GONE);
                binding.amoutDetails.tvTotalAmount.setText(getString(R.string.actual_amount));
            } else {
                binding.amoutDetails.tvexclVatAmount.setVisibility(View.VISIBLE);
                binding.amoutDetails.txtexclVatAmount.setVisibility(View.VISIBLE);
                binding.amoutDetails.tvTotalAmount.setText(getString(R.string.actual_amount));
                binding.amoutDetails.tvexclVatAmount.setText(getString(R.string.total_amount) + " (excl. VAT)");
            }
            Log.d(TAG, "enablePromoUI: totalpayment  " + totalpayment);
            if(temp_currency.equalsIgnoreCase("BHD")) {
                total = Double.parseDouble(String.format(Locale.ENGLISH,"%.3f",(totalpayment - Double.parseDouble(Promo_discount))));
                exclVatAmount = Double.parseDouble(String.format(Locale.ENGLISH,"%.3f",(total / (((vat) + 100) / 100))));//231
                per = Double.parseDouble(String.format(Locale.ENGLISH,"%.3f",(total - exclVatAmount))); //2.7
                totalper = Double.parseDouble(String.format(Locale.ENGLISH,"%.3f",(exclVatAmount + per)));
            }else{
                total = Double.parseDouble(String.format(Locale.ENGLISH,"%.2f",(totalpayment - Double.parseDouble(Promo_discount))));
                exclVatAmount = Double.parseDouble(String.format(Locale.ENGLISH,"%.2f",(total / (((vat) + 100) / 100))));//231
                per = Double.parseDouble(String.format(Locale.ENGLISH,"%.2f",(total - exclVatAmount)));
                totalper = Double.parseDouble(String.format(Locale.ENGLISH,"%.2f",(exclVatAmount + per)));
            }
            Log.e(TAG, "setData:789 total:--  " + total);
            Log.e(TAG, "setData:789 exclVat:--  " + exclVatAmount);
            Log.d(TAG, "setData: vat:-- " + vat);
//------------------------- end new calculation
        }
        else {

            if (Double.parseDouble(getArguments().getString(Constants.VAT)) == 0) {
                binding.amoutDetails.tvexclVatAmount.setVisibility(View.GONE);
                binding.amoutDetails.txtexclVatAmount.setVisibility(View.GONE);
                binding.amoutDetails.tvTotalAmount.setText(getString(R.string.actual_amount));
            } else {
                binding.amoutDetails.tvexclVatAmount.setVisibility(View.VISIBLE);
                binding.amoutDetails.txtexclVatAmount.setVisibility(View.VISIBLE);
                binding.amoutDetails.tvTotalAmount.setText(getString(R.string.actual_amount) );
                binding.amoutDetails.tvexclVatAmount.setText(getString(R.string.total_amount) + " (excl. VAT)");

            }

            if (getArguments().getString(Constants.VAT) != null) {
                // percentage = (totalValue / 100.0) * Float.parseFloat(getArguments().getString(Constants.VAT));
                //------------------new calculation
                if (temp_currency.equalsIgnoreCase("BHD")) {
                    total = Double.parseDouble(String.format(Locale.ENGLISH,"%.3f",(totalpayment - Double.parseDouble(Promo_discount))));
                    exclVatAmount = Double.parseDouble(String.format(Locale.ENGLISH,"%.3f",(total / ((Double.parseDouble(getArguments().getString(Constants.VAT)) + 100) / 100))));   //30
                    per = Double.parseDouble(String.format(Locale.ENGLISH,"%.3f",(total - exclVatAmount))); //2.7
                    totalper = Double.parseDouble(String.format(Locale.ENGLISH,"%.3f",(exclVatAmount + per)));
                }else{
                    total = Double.parseDouble(String.format(Locale.ENGLISH,"%.2f",(totalpayment - Double.parseDouble(Promo_discount))));
                    exclVatAmount = Double.parseDouble(String.format(Locale.ENGLISH,"%.2f",(total / ((Double.parseDouble(getArguments().getString(Constants.VAT)) + 100) / 100))));   //30
                    per = Double.parseDouble(String.format(Locale.ENGLISH,"%.2f",(total - exclVatAmount))); //2.7
                    totalper = Double.parseDouble(String.format(Locale.ENGLISH,"%.2f",(exclVatAmount + per)));
                }

                Log.e(TAG, "setData:789 per:--  " + total);
                Log.d(TAG, "setData: vat:-- " + Double.parseDouble(getArguments().getString(Constants.VAT)));


//------------------------- end new calculation


            }
        }


        Log.d(TAG, "enablePromoUI: totalValue " + totalpayment);
        binding.amoutDetails.amountTxtview.setPaintFlags(binding.amoutDetails.finalAmountTxtview.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        String myString = Promo_name.toUpperCase();
        //String upperString = myString.substring(0,1).toUpperCase() + myString.substring(1);
        binding.amoutDetails.appliedPromoTxt.setText(myString + " - " + getString(R.string.applied_successfully));
        binding.amoutDetails.approvedIcon.setVisibility(View.VISIBLE);

        binding.amoutDetails.promoDescription.setText(Promo_desc);
        // total=Discounted_price;
        if (temp_currency.equalsIgnoreCase("BHD")) {
            binding.amoutDetails.pricetotal.setText(temp_currency + " " + String.format(Locale.ENGLISH, "%.3f", totalpayment));
            binding.amoutDetails.txtexclVatAmount.setText(temp_currency + " " + String.format(Locale.ENGLISH, "%.3f", exclVatAmount));
//            binding.amoutDetails.txtexclVatAmount.setText(temp_currency + " " + String.format(Locale.ENGLISH, "%.3f", total));
            binding.amoutDetails.txtvat.setText(temp_currency + " " + String.format(Locale.ENGLISH, "%.3f", per));
            binding.amoutDetails.finalAmountTxtview.setText(temp_currency + " " + String.format(Locale.ENGLISH, "%.3f", totalper));
            binding.amoutDetails.discountTxtview.setText(temp_currency + " -" + String.format(Locale.ENGLISH, "%.3f", Double.parseDouble(Promo_discount)));
        } else {
            binding.amoutDetails.pricetotal.setText(temp_currency + " " + String.format(Locale.ENGLISH, "%.2f", totalpayment));
//            binding.amoutDetails.txtexclVatAmount.setText(temp_currency + " " + String.format(Locale.ENGLISH, "%.2f", total));
            binding.amoutDetails.txtexclVatAmount.setText(temp_currency + " " + String.format(Locale.ENGLISH, "%.2f", exclVatAmount));
            binding.amoutDetails.txtvat.setText(temp_currency + " " + String.format(Locale.ENGLISH, "%.2f", per));
            binding.amoutDetails.finalAmountTxtview.setText(temp_currency + " " + String.format(Locale.ENGLISH, "%.2f", totalper));
            binding.amoutDetails.discountTxtview.setText(temp_currency + " -" + String.format(Locale.ENGLISH, "%.2f", Double.parseDouble(Promo_discount)));
        }


//        binding.txtvat.setWidth(binding.finalAmountTxtview.getWidth());
//        binding.priceTxtview.setWidth(binding.finalAmountTxtview.getWidth());
//        binding.discountTxtview.setWidth(binding.finalAmountTxtview.getWidth());

    }

    private void disablePromoUI() {
        binding.amoutDetails.promoInputLay.setVisibility(View.VISIBLE);
        binding.amoutDetails.appliedLay.setVisibility(View.GONE);
        binding.amoutDetails.layExclapplypromo.setVisibility(View.GONE);
        binding.amoutDetails.amountPaidLay.setVisibility(View.VISIBLE);
        binding.amoutDetails.amountTxtview.setText("");
        binding.amoutDetails.layDiscountAmount.setVisibility(View.GONE);
        binding.amoutDetails.tvTotalAmount.setText(getString(R.string.total_amount) + " (excl. VAT)");
        // binding.finalAmountTxtview.setText("");
        binding.amoutDetails.amountTxtview.setPaintFlags(binding.amoutDetails.finalAmountTxtview.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        binding.amoutDetails.appliedPromoTxt.setText("");
        binding.amoutDetails.approvedIcon.setVisibility(View.GONE);
        binding.amoutDetails.promoDescription.setText("");
        binding.amoutDetails.promoEdittext.setText("");
        Discounted_price = Double.parseDouble(amount_temp);
        // Discounted_price=0.0;
        Promo_id = "";
        Promo_discount = "";
        Promo_desc = "";
        Promo_name = "";


        // Discount price rounded
        if (temp_currency.equalsIgnoreCase("BHD")) {
            totalValue = Discounted_price;
        } else {
            totalValue = Discounted_price;
        }

        if (booking_type == 3) {

            total = searchMaidBulkModel.maidCount * searchMaidBulkModel.duration * maidprice;
            Log.d(TAG, "setData: vat:-- " + vat);
            exclVatAmount = total / (((vat) + 100) / 100);

//            per = (total * (vat)) / 100;
            per = total -exclVatAmount;
            totalper = exclVatAmount + per;


            /*new calculation*/


        } else {
            if (getArguments().getString(Constants.VAT) != null) {
                /*new calculation*/

                if (booking_type == 2) {

                    totalValue = searchMaidModel.duration * maidprice * selectedDatesList.size();
                    totalpayment = totalValue;
                    // total = searchMaidModel.duration * searchMaidModel.maidPrice;
                    // binding.pricetotal.setText(formatter.format(total));

                } else {
                    totalValue = searchMaidModel.duration * maidprice;
                    //binding.pricetotal.setText(formatter.format(total));
                }
                total = totalValue;
                Log.d(TAG, "setData: vat:-- " + Double.parseDouble(getArguments().getString(Constants.VAT)));

                if (temp_currency.equalsIgnoreCase("BHD")) {
                    exclVatAmount = Double.parseDouble(String.format(Locale.ENGLISH, "%.3f", (total / ((Double.parseDouble(getArguments().getString(Constants.VAT)) + 100) / 100))));
//                    per = Double.parseDouble(String.format(Locale.ENGLISH, "%.3f", (total * (Double.parseDouble(getArguments().getString(Constants.VAT)))) / 100));
                    per = Double.parseDouble(String.format(Locale.ENGLISH, "%.3f", (total - exclVatAmount)));
                }else{
                    exclVatAmount = Double.parseDouble(String.format(Locale.ENGLISH, "%.2f", (total / ((Double.parseDouble(getArguments().getString(Constants.VAT)) + 100) / 100))));
                    per = Double.parseDouble(String.format(Locale.ENGLISH, "%.2f", (total - exclVatAmount)));
//                    per = Double.parseDouble(String.format(Locale.ENGLISH, "%.2f", (total * (Double.parseDouble(getArguments().getString(Constants.VAT)))) / 100));
                }
                totalper = exclVatAmount + per;


                /*new calculation*/

            }
        }


        //total=Discounted_price;
        if (temp_currency.equalsIgnoreCase("BHD")) {
            binding.amoutDetails.txtvat.setText(temp_currency + " " + String.format(Locale.ENGLISH, "%.3f", per));
//            setStringStyleeColon(String.format(Locale.ENGLISH, "%s %.3f", temp_currency, total), binding.amoutDetails.pricetotal);
            setStringStyleeColon(String.format(Locale.ENGLISH, "%s %.3f", temp_currency, exclVatAmount), binding.amoutDetails.pricetotal);
            binding.amoutDetails.finalAmountTxtview.setText(temp_currency + " " + String.format(Locale.ENGLISH, "%.3f", totalper));
        } else {
            binding.amoutDetails.txtvat.setText(temp_currency + " " + String.format(Locale.ENGLISH, "%.2f", per));
            setStringStyleeColon(String.format(Locale.ENGLISH, "%s %.2f", temp_currency, exclVatAmount), binding.amoutDetails.pricetotal);
//            setStringStyleeColon(String.format(Locale.ENGLISH, "%s %.2f", temp_currency, total), binding.amoutDetails.pricetotal);
            binding.amoutDetails.finalAmountTxtview.setText(temp_currency + " " + String.format(Locale.ENGLISH, "%.2f", totalper));
        }
    }


    @Override
    public void promoError(String failureMessage) {

        binding.amoutDetails.errorLay.setVisibility(View.VISIBLE);
        binding.amoutDetails.promoEdittext.setText("");
        binding.amoutDetails.promoErrorTxtview.setText(failureMessage);
    }

    public void alertPopupp(final Activity activity, String title, String message, final String type) {
        binding.tvProceedPayment.setEnabled(true);
        binding.amoutDetails.llTotal.setVisibility(View.GONE);

        if (type.equalsIgnoreCase("bulk")) {
           IOSAlertDialog dialog =  IOSAlertDialog.newInstance(
                 requireContext(),
                  null,
                   title,
                  getString(R.string.ok),
                  null,
                   new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int id) {
                           binding.amoutDetails.llTotal.setVisibility(View.VISIBLE);
                           mFirebaseAnalytics.logEvent("mdprscr_tap_ok", null);
                           if (type.equalsIgnoreCase("bulk")) {
                               Log.d(TAG, "onClick:  if (type.equalsIgnoreCase(\"bulk\")) { ");
                               getActivity().finish();
                           } else {
                               Log.d(TAG, "onClick:  } else { dialog");
                               Log.d(TAG, "onClick: searchMaidData1.getData().getAvailableMaidsCount():-- " + searchMaidData1.getData().getAvailableMaidsCount());
                               searchMaidBulkModel.maidCount = searchMaidData1.getData().getAvailableMaidsCount();
                               setBulkData(searchMaidData1);
                      /*  if (String.valueOf(searchMaidBulkModel.maidCount).equalsIgnoreCase("1")) {
                            binding.tvMaidName.setText(String.valueOf(searchMaidBulkModel.maidCount) + " Maid \n" + String.format(Locale.ENGLISH, "%s %.3f", searchMaidData1.getData().getRequestedMaids().get(0).getCurrency(), searchMaidData1.getData().getRequestedMaids().get(0).getActualPrice()) + " per hour");
                        } else {
                            binding.tvMaidName.setText(String.valueOf(searchMaidBulkModel.maidCount) + " Maids \n" + String.format(Locale.ENGLISH, "%s %.3f", searchMaidData1.getData().getRequestedMaids().get(0).getCurrency(), searchMaidData1.getData().getRequestedMaids().get(0).getActualPrice()) + " per hour");
                        }
                        Log.e("if", "before");
                        if (searchMaidData1.getData().getRequestedMaids().get(0).getActualPrice() != null) {
                            Float total = searchMaidBulkModel.maidCount * searchMaidBulkModel.duration * searchMaidData1.getData().getRequestedMaids().get(0).getActualPrice();
                            setStringStyleeColon(String.format(Locale.ENGLISH, "%s %.3f", searchMaidData1.getData().getRequestedMaids().get(0).getCurrency(), total), binding.tvPrice);
                            setStringStylee(String.format(Locale.ENGLISH, "%s %.3f", searchMaidData1.getData().getRequestedMaids().get(0).getCurrency(), total), binding.priceTxtview);
                            Log.e("if", "enter");
                        }*/

                           }
                           dialog.cancel();
                       }
                   },
                   null,
                   ContextCompat.getColor(requireContext(),R.color.app_color),
                   ContextCompat.getColor(requireContext(),R.color.app_color),
                   false
           );
           dialog.show(requireActivity().getSupportFragmentManager(), "ios_dialog");;
        }else{
            IOSAlertDialog dialog =  IOSAlertDialog.newInstance(
                    requireContext(),  /**  context ----*/
                    null, /** title  */
                    title, /**  message */
                    getString(R.string.ok),  /** positive button title*/
                    getString(R.string.no),/** negative button title*/
                    new DialogInterface.OnClickListener() {  /** positive button click*/
                        public void onClick(DialogInterface dialog, int id) {
                            binding.tvProceedPayment.setEnabled(true);
                            binding.amoutDetails.llTotal.setVisibility(View.VISIBLE);
                            mFirebaseAnalytics.logEvent("mdprscr_tap_ok", null);
                            if (type.equalsIgnoreCase("bulk")) {
                                Log.d(TAG, "onClick:  if (type.equalsIgnoreCase(\"bulk\")) { ");

                                getActivity().finish();

                            } else {
                                Log.d(TAG, "onClick:  } else { dialog");

                                Log.d(TAG, "onClick: searchMaidData1.getData().getAvailableMaidsCount():-- " + searchMaidData1.getData().getAvailableMaidsCount());
                                searchMaidBulkModel.maidCount = searchMaidData1.getData().getAvailableMaidsCount();
                                setBulkData(searchMaidData1);
                      /*  if (String.valueOf(searchMaidBulkModel.maidCount).equalsIgnoreCase("1")) {
                            binding.tvMaidName.setText(String.valueOf(searchMaidBulkModel.maidCount) + " Maid \n" + String.format(Locale.ENGLISH, "%s %.3f", searchMaidData1.getData().getRequestedMaids().get(0).getCurrency(), searchMaidData1.getData().getRequestedMaids().get(0).getActualPrice()) + " per hour");
                        } else {
                            binding.tvMaidName.setText(String.valueOf(searchMaidBulkModel.maidCount) + " Maids \n" + String.format(Locale.ENGLISH, "%s %.3f", searchMaidData1.getData().getRequestedMaids().get(0).getCurrency(), searchMaidData1.getData().getRequestedMaids().get(0).getActualPrice()) + " per hour");
                        }
                        Log.e("if", "before");
                        if (searchMaidData1.getData().getRequestedMaids().get(0).getActualPrice() != null) {
                            Float total = searchMaidBulkModel.maidCount * searchMaidBulkModel.duration * searchMaidData1.getData().getRequestedMaids().get(0).getActualPrice();
                            setStringStyleeColon(String.format(Locale.ENGLISH, "%s %.3f", searchMaidData1.getData().getRequestedMaids().get(0).getCurrency(), total), binding.tvPrice);
                            setStringStylee(String.format(Locale.ENGLISH, "%s %.3f", searchMaidData1.getData().getRequestedMaids().get(0).getCurrency(), total), binding.priceTxtview);
                            Log.e("if", "enter");
                        }*/

                            }
                            dialog.cancel();
                        }
                    },
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            binding.amoutDetails.llTotal.setVisibility(View.VISIBLE);
                            getActivity().finish();
                            dialog.cancel();
                        }
                    },
                    ContextCompat.getColor(requireContext(),R.color.app_color),
                    ContextCompat.getColor(requireContext(),R.color.app_color),
                    false
            );
            dialog.show(requireActivity().getSupportFragmentManager(), "ios_dialog");;
        }

       /*
        AlertDialog dialog = null;
        AlertDialog.Builder builder1 = new AlertDialog.Builder(requireContext());
        *//* builder1.setTitle(Html.fromHtml("<b>"+title+"</b>"));*//*
        builder1.setMessage(title);
        builder1.setCancelable(false);
        if (type.equalsIgnoreCase("bulk")) {
            builder1.setPositiveButton(
                    R.string.ok,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            binding.amoutDetails.llTotal.setVisibility(View.VISIBLE);
                            mFirebaseAnalytics.logEvent("mdprscr_tap_ok", null);
                            if (type.equalsIgnoreCase("bulk")) {
                                Log.d(TAG, "onClick:  if (type.equalsIgnoreCase(\"bulk\")) { ");
                                getActivity().finish();
                            } else {
                                Log.d(TAG, "onClick:  } else { dialog");
                                Log.d(TAG, "onClick: searchMaidData1.getData().getAvailableMaidsCount():-- " + searchMaidData1.getData().getAvailableMaidsCount());
                                searchMaidBulkModel.maidCount = searchMaidData1.getData().getAvailableMaidsCount();
                                setBulkData(searchMaidData1);
                      *//*  if (String.valueOf(searchMaidBulkModel.maidCount).equalsIgnoreCase("1")) {
                            binding.tvMaidName.setText(String.valueOf(searchMaidBulkModel.maidCount) + " Maid \n" + String.format(Locale.ENGLISH, "%s %.3f", searchMaidData1.getData().getRequestedMaids().get(0).getCurrency(), searchMaidData1.getData().getRequestedMaids().get(0).getActualPrice()) + " per hour");
                        } else {
                            binding.tvMaidName.setText(String.valueOf(searchMaidBulkModel.maidCount) + " Maids \n" + String.format(Locale.ENGLISH, "%s %.3f", searchMaidData1.getData().getRequestedMaids().get(0).getCurrency(), searchMaidData1.getData().getRequestedMaids().get(0).getActualPrice()) + " per hour");
                        }
                        Log.e("if", "before");
                        if (searchMaidData1.getData().getRequestedMaids().get(0).getActualPrice() != null) {
                            Float total = searchMaidBulkModel.maidCount * searchMaidBulkModel.duration * searchMaidData1.getData().getRequestedMaids().get(0).getActualPrice();
                            setStringStyleeColon(String.format(Locale.ENGLISH, "%s %.3f", searchMaidData1.getData().getRequestedMaids().get(0).getCurrency(), total), binding.tvPrice);
                            setStringStylee(String.format(Locale.ENGLISH, "%s %.3f", searchMaidData1.getData().getRequestedMaids().get(0).getCurrency(), total), binding.priceTxtview);
                            Log.e("if", "enter");
                        }*//*

                            }
                            dialog.cancel();
                        }
                    });
        } else {
            builder1.setPositiveButton(
                    R.string.yes,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            binding.tvProceedPayment.setEnabled(true);
                            binding.amoutDetails.llTotal.setVisibility(View.VISIBLE);
                            mFirebaseAnalytics.logEvent("mdprscr_tap_ok", null);
                            if (type.equalsIgnoreCase("bulk")) {
                                Log.d(TAG, "onClick:  if (type.equalsIgnoreCase(\"bulk\")) { ");

                                getActivity().finish();

                            } else {
                                Log.d(TAG, "onClick:  } else { dialog");

                                Log.d(TAG, "onClick: searchMaidData1.getData().getAvailableMaidsCount():-- " + searchMaidData1.getData().getAvailableMaidsCount());
                                searchMaidBulkModel.maidCount = searchMaidData1.getData().getAvailableMaidsCount();
                                setBulkData(searchMaidData1);
                      *//*  if (String.valueOf(searchMaidBulkModel.maidCount).equalsIgnoreCase("1")) {
                            binding.tvMaidName.setText(String.valueOf(searchMaidBulkModel.maidCount) + " Maid \n" + String.format(Locale.ENGLISH, "%s %.3f", searchMaidData1.getData().getRequestedMaids().get(0).getCurrency(), searchMaidData1.getData().getRequestedMaids().get(0).getActualPrice()) + " per hour");
                        } else {
                            binding.tvMaidName.setText(String.valueOf(searchMaidBulkModel.maidCount) + " Maids \n" + String.format(Locale.ENGLISH, "%s %.3f", searchMaidData1.getData().getRequestedMaids().get(0).getCurrency(), searchMaidData1.getData().getRequestedMaids().get(0).getActualPrice()) + " per hour");
                        }
                        Log.e("if", "before");
                        if (searchMaidData1.getData().getRequestedMaids().get(0).getActualPrice() != null) {
                            Float total = searchMaidBulkModel.maidCount * searchMaidBulkModel.duration * searchMaidData1.getData().getRequestedMaids().get(0).getActualPrice();
                            setStringStyleeColon(String.format(Locale.ENGLISH, "%s %.3f", searchMaidData1.getData().getRequestedMaids().get(0).getCurrency(), total), binding.tvPrice);
                            setStringStylee(String.format(Locale.ENGLISH, "%s %.3f", searchMaidData1.getData().getRequestedMaids().get(0).getCurrency(), total), binding.priceTxtview);
                            Log.e("if", "enter");
                        }*//*

                            }
                            dialog.cancel();
                        }
                    });

            builder1.setNegativeButton(
                    R.string.no,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            binding.amoutDetails.llTotal.setVisibility(View.VISIBLE);
                            getActivity().finish();
                            dialog.cancel();
                        }
                    });
        }


        dialog = builder1.create();
        dialog.show();

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(requireContext().getResources().getColor(R.color.appColor));
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(requireContext().getResources().getColor(R.color.appColor));
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setAllCaps(false);
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setAllCaps(false);*/
        // return dialog;
    }



    @Override
    public void saveAddressSuccess(ApiResponse<PojoLogin> data) {
        Log.e("saveadaress",new Gson().toJson(data));
        Prefs.with(getActivity()).save(Constants.DATA, data.getData());
        if (bookAgain) {
            Fragment targetFragment = getTargetFragment(); // fragment1 in our case
            if (targetFragment != null) {
                Intent intentData = null;
                if (isHome) {
                    intentData = new Intent();
                    intentData.putExtra(Constants.SIGN_UP, true);
                }
                targetFragment.onActivityResult(getTargetRequestCode(), getActivity().RESULT_OK, intentData);
            }
            getActivity().getSupportFragmentManager().popBackStack();
        } else {
            if (searchMaidModel != null) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                        .replace(R.id.frameLayout, PaymentStateFragment.newInstance(searchMaidModel), "PaymentStateFragment")
                        .addToBackStack("PaymentStateFragment").commit();
            }
        }

    }

    @Override
    public void deleteAddressSuccess(ApiResponse<PojoLogin> data) {
        Prefs.with(getActivity()).save(Constants.DATA, data.getData());
        PojoLogin pojoLogin = Prefs.with(getActivity()).getObject(Constants.DATA, PojoLogin.class);
        addressList.clear();
        addressList.addAll(pojoLogin.multipleAddress);
        addressListAdapter.notifyDataSetChanged();
        if (addressList == null || addressList.size() == 0) {
            binding.llSavedAddress.setVisibility(View.GONE);
            binding.tvSameAddress.setVisibility(View.GONE);
            if (latLng != null) {
                binding.tvProceedPayment.setVisibility(View.VISIBLE);
            } else {
                binding.tvProceedPayment.setVisibility(View.GONE);
            }

        } else {
            binding.llSavedAddress.setVisibility(View.VISIBLE);
        }
        Intent deleteAddressIntent = new Intent();
        deleteAddressIntent.setAction(Constants.ACTION_ADDRESS_DELETED);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(deleteAddressIntent);
    }

    @Override
    public void saveAddressError(String errorMessage) {
        new DialogPopup().alertPopup(getActivity(), getResources().getString(R.string.dialog_alert), errorMessage, "").show(requireActivity().getSupportFragmentManager(), "ios_dialog");;

    }

    @Override
    public void saveAddressFailure(String failureMessage) {
        Log.d(TAG, "saveAddressFailure: "+failureMessage);
        if(failureMessage.equals("No maid found")){
            alertPopupp(getActivity(),getString(R.string.no_maid_found),getString(R.string.no_maid_found),"bulk");
        }else {
            alertPopupp(getActivity(), failureMessage, failureMessage, "bulk");
        }
    }

    @Override
    public void maidNotAvailable(String errorMessage) {
        if (errorPopUpDialog != null) {
            errorPopUpDialog.dismiss();
        }
        Log.d(TAG, "maidNotAvailable: "+ errorMessage);
        if(errorMessage.equals("No maid found")){
            alertPopupp(getActivity(), getResources().getString(R.string.dialog_alert),getString(R.string.no_maid_found),"bulk");
        }else {

            alertPopupp(getActivity(), getResources().getString(R.string.dialog_alert), errorMessage, "bulk");
        }

    }

    @Override
    public void reschduleError(String failureMessage) {
        if (failureMessage.equalsIgnoreCase("error")) {
            alertReschdule(getActivity(), getResources().getString(R.string.sorry_no_maids), getString(R.string.proceed_other_maids), "").show();
        } else if (failureMessage.equalsIgnoreCase("done")) {
            alertReschduleSuccess(getActivity(), getResources().getString(R.string.reschdule_success), "", "");
        }
    }

    public Dialog alertReschdule(final Activity activity, String title, String message, final String customMessage) {
        Dialog dialog = null;
        try {

            dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
            dialog.setContentView(R.layout.dialog_popup);
            WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
            lp.dimAmount = 0.6f;
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            TextView header = dialog.findViewById(R.id.header);
            TextView text = dialog.findViewById(R.id.text);
            Button ok = dialog.findViewById(R.id.ok);
            Button cancel = dialog.findViewById(R.id.cancel);
            text.setText(message);
            header.setText(title);
            cancel.setVisibility(View.VISIBLE);

            ok.setText("Yes");
            cancel.setText("No");

            header.setVisibility(View.VISIBLE);
            final Dialog finalDialog = dialog;
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finalDialog.dismiss();
                }
            });


            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    finalDialog.dismiss();
                }
            });
            dialog.show();


        } catch (Exception e) {
            e.printStackTrace();
        }

        return dialog;
    }

    public Dialog alertReschduleSuccess(final Activity activity, String title, String message, final String customMessage) {
        Dialog dialog = null;
        try {

            dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
            dialog.setContentView(R.layout.dialog_popup);
            WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
            lp.dimAmount = 0.6f;
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            TextView header = dialog.findViewById(R.id.header);
            TextView text = dialog.findViewById(R.id.text);
            Button ok = dialog.findViewById(R.id.ok);
            Button cancel = dialog.findViewById(R.id.cancel);
            text.setText(message);
            header.setText(title);
            cancel.setVisibility(View.VISIBLE);
            ok.setText("OK");
            cancel.setText("No");
            text.setVisibility(View.GONE);
            header.setVisibility(View.VISIBLE);
            cancel.setVisibility(View.GONE);
            final Dialog finalDialog = dialog;
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finalDialog.dismiss();
                }
            });
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ConfirmBookFragment.road_temp = "";
                    ConfirmBookFragment.city_temp = "";
                    ConfirmBookFragment.block_temp = "";
                    ConfirmBookFragment.apartment_temp = "";
                    ConfirmBookFragment.full_address_temp = "";
                    ConfirmBookFragment.latLng = null;
                    ConfirmBookFragment.buildingNumber="";
                    ConfirmBookFragment.postalCode="";
                    getActivity().finishAffinity();
                    startActivity(new Intent(getActivity(), Main2Activity.class));
                    finalDialog.dismiss();
                }
            });
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dialog;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem itemNotification = menu.findItem(R.id.action_notification);
        MenuItem itemMenu = menu.findItem(R.id.action_menu);
        if (itemNotification != null)
            itemNotification.setVisible(false);

        if (itemNotification != null)
            itemMenu.setVisible(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.detachView();
        presenterr.detachView();
    }
}
