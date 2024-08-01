package com.maktoday.views.home;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import com.maktoday.utils.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.maktoday.R;
import com.maktoday.adapter.NoMaidsAdapter;
import com.maktoday.adapter.TimeDurationAdapter;
import com.maktoday.databinding.FragmentHomeBinding;
import com.maktoday.interfaces.NoMaidSelection;
import com.maktoday.interfaces.TimeSelection;
import com.maktoday.model.ApiResponse;
import com.maktoday.model.BookServiceModel;
import com.maktoday.model.FullAddress;
import com.maktoday.model.MaidData;
import com.maktoday.model.PojoLogin;
import com.maktoday.model.PojoMyBooking;
import com.maktoday.model.PojoService;
import com.maktoday.model.PojoServiceNew;
import com.maktoday.model.SearchMaidBulkModel;
import com.maktoday.model.SearchMaidModel;
import com.maktoday.model.ServicelistResponse;
import com.maktoday.model.TimeSlot;
import com.maktoday.utils.Constants;
import com.maktoday.utils.DialogPopup;
import com.maktoday.utils.GeneralFunction;
import com.maktoday.utils.LocationFragment;
import com.maktoday.utils.Prefs;
import com.maktoday.utils.dialog.IOSAlertDialog;
import com.maktoday.views.PickerActivity;
import com.maktoday.views.authenticate.AuthenticateActivity;
import com.maktoday.views.confirmbook.ConfirmBookFragment;
import com.maktoday.views.maidbook.MaidBookActivity;
import com.maktoday.views.maidprofile.MaidProfileFragment;
import com.maktoday.views.main.Main2Activity;
import com.maktoday.views.slotavailable.SlotAvailbleActivity;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static com.maktoday.views.main.Main2Activity.countTextView;
import static com.maktoday.views.upcomingbooking.UpComingBookingFragment.rescheduleUpdate;

import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * Created by cbl81 on 27/10/17.
 */

public class HomeFragment extends LocationFragment implements HomeContract.View, View.OnClickListener, DatePickerDialog.OnDateSetListener,
        TimeSelection, NoMaidSelection, DatePickerFragment.DateDialogListener {

    //TAG
    private static final String TAG = "HomeFragment";
    private static final int REQ_CODE = 0;
    private static final int BOOK_AGAIN_CODE = 10;
    private static final String DIALOG_DATE = "MainActivity.DateDialog";
    public static ArrayList<Date> selectedDatesList = new ArrayList<>();
    public static int noticount=0;
    public static FragmentHomeBinding binding;
    public static int booking_type = 1;
    public ArrayList<String> selectedAgencyList = new ArrayList<>();
    NumberPicker hourPicker;
    String times[] = {"8:00 AM", "9:00 AM", "10:00 AM", "11:00 AM", "12:00 PM", "1:00 PM", "2:00 PM", "3:00 PM", "4:00 PM", "5:00 PM", "6:00 PM", "7:00 PM", "8:00 PM"};
    private int duration;
    private int mHour, mMinute;
    private int maid_count;
    private HomeContract.Presenter presenter;
    private Calendar calendarDate;
    private Calendar calendartoDate;
    private Calendar calendarTime;
    private String locationName;
    private boolean isBookAgain = false; // this fragment also used in Booking when we use "Book Again" functionality.
    private String reschuleStatus = ""; // this fragment also used in Booking when we use "Book Again" functionality.
    private String SERVICE_ID = ""; // this fragment also used in Booking when we use "Book Again" functionality.
    // So,To differentiate we used isBookAgain variable
    private SearchMaidModel searchMaidModel;
    private PojoMyBooking.Datum bookingDataModel;
    private MaidData maidData;

    private boolean isAddressSet;
    public  boolean isService=false;
 //  public static String servicesID;

    private BroadcastReceiver deleteAddressReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            PojoLogin pojoLogin = Prefs.with(getActivity()).getObject(Constants.DATA, PojoLogin.class);
            boolean removeSelectedAddress = true;
            for (FullAddress fullAddress : pojoLogin.multipleAddress) {
                final String address = getAddressValue(fullAddress);
                if (fullAddress.lat == searchMaidModel.lat &&
                        fullAddress.lng == searchMaidModel.lng &&
                        address.equals(searchMaidModel.locationName)) {
                    removeSelectedAddress = false;
                    break;
                }
            }

            if (removeSelectedAddress) {
                Log.d("HomeFragment", "Selected address deleted");
                searchMaidModel.lat = 0.0;
                searchMaidModel.lng = 0.0;
                binding.tvAddressValue.setText(R.string.address);
                binding.tvLocation.setText(R.string.select_address);
            } else {
                Log.d("HomeFragment", "Non-selected address deleted");
            }
        }
    };
    private TimeZone tempTimeZone;

    public static HomeFragment newInstance(SearchMaidModel searchMaidModel) {
        Bundle args = new Bundle();
        args.putParcelable(Constants.SEARCH_MAID_DATA, searchMaidModel);
        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e(TAG, " ----------------- onViewCreated");
      Log.d(TAG, "onViewCreated:StartActivity ");

        init();
        setData();
        setListeners();
        booking_type = 1;
        final IntentFilter filter = new IntentFilter(Constants.ACTION_ADDRESS_DELETED);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(deleteAddressReceiver, filter);
        if (isBookAgain) {
            binding.bulkTxtview.setEnabled(false);
            binding.bulkTxtview.setTextColor(Color.parseColor("#90000000"));
            binding.bulkTxtview.setBackgroundResource(R.drawable.rect_grey_round);
            if (reschuleStatus.equalsIgnoreCase("yes")) {
                binding.cardView.setVisibility(View.GONE);
                binding.halfLayout.setVisibility(View.GONE);
                binding.tvSearchMaid.setText(getResources().getString(R.string.reschedule));
                binding.tvSelecteddate.setText("");
                binding.tvSelectedtime.setText("");
                binding.tvSubTitle.setText(getResources().getString(R.string.reschedule_title_txt));
            }
        } else {
            binding.bulkTxtview.setEnabled(true);
            binding.bulkTxtview.setTextColor(Color.parseColor("#000000"));
            binding.bulkTxtview.setBackgroundResource(0);
        }
    }

  private void   showInitDate(boolean show, String formattedDateH,SimpleDateFormat sdf ){

        if(show){
             if (Integer.parseInt(formattedDateH) >20 || Integer.parseInt(formattedDateH)< 7) {
            binding.tvSelectedtime.setText("--:--");
            binding.tvSelecteddate.setText("--,-- -");
             }else {
              binding.tvSelectedtime.setText(sdf.format(calendarTime.getTime()));
             binding.tvSelecteddate.setText(GeneralFunction.getFormatFromDate(calendarDate.getTime(), "EEEE, MMMM dd"));
             }
        }else{
            binding.tvSelectedtime.setText("--:--");
            binding.tvSelecteddate.setText("--,-- -");
        }

    }

    /**
     * used to intialise the view or reference
     */
    private void init() {
        presenter = new HomePresenter();
        presenter.attachView(this);
        if (getArguments() != null) {
            isBookAgain = getArguments().getBoolean(Constants.BOOK_AGAIN);
            reschuleStatus = getArguments().getString(Constants.reschuleStatus);

            SERVICE_ID = getArguments().getString(Constants.SERVICE_ID);

            if (reschuleStatus.equalsIgnoreCase("yes")) {
                if (getArguments().getString(Constants.BOOKING_TYPE).equalsIgnoreCase("2")) {
                    booking_type = 3;
                } else {
                    booking_type = 1;
                }
                bookingDataModel = new Gson().fromJson(getArguments().getString(Constants.BOOKING_DATA), PojoMyBooking.Datum.class);
            }else {
                bookingDataModel = new Gson().fromJson(getArguments().getString(Constants.BOOKING_DATA), PojoMyBooking.Datum.class);
            }
            searchMaidModel = getArguments().getParcelable(Constants.SEARCH_MAID_DATA);
            Log.d(TAG, "init: maid data:--  "+ new Gson().toJson(getArguments().getString(Constants.MAID_DATA)));
            maidData= new Gson().fromJson(getArguments().getString(Constants.MAID_DATA),MaidData.class);
            binding.toolbar.setVisibility(View.GONE);
        }
    }

    /**
     * used to set data,adapter on views
     */
    private void setData() {
        calendarDate = Calendar.getInstance();
        calendartoDate = Calendar.getInstance();
        calendarTime = Calendar.getInstance();
        if (searchMaidModel == null) {
            searchMaidModel = new SearchMaidModel();
        }

        // set adapter to show booking hours
        NoMaidsAdapter noMaidsAdapter = new NoMaidsAdapter(getActivity(), this, false);
        TimeDurationAdapter timeDurationAdapter = new TimeDurationAdapter(getActivity(), this, false);
        if (isBookAgain) {
            timeDurationAdapter.selectedPosition = 0;
            // noMaidsAdapter.selectedPosition = searchMaidModel.duration;
        }
        binding.rvNoMaids.setAdapter(noMaidsAdapter);
        binding.rvNoMaids.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvTime.setAdapter(timeDurationAdapter);
        binding.rvTime.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        //  show by default date and time
        if (isBookAgain) {
            long currentMillis = System.currentTimeMillis();
            searchMaidModel.workDate = currentMillis;
            searchMaidModel.endDate = currentMillis;
            searchMaidModel.startTime = currentMillis;
            /*if (searchMaidModel.workDate > 0) {
                calendarDate.setTimeInMillis(searchMaidModel.workDate);
                calendarDate.add(Calendar.DATE, 7);
            }*/
        }

        calendarTime.set(Calendar.MINUTE, 0);
        calendarTime.set(Calendar.SECOND, 0);
        calendarTime.set(Calendar.MILLISECOND, 0);
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
        //  if (isBookAgain) {
        //  Toast.makeText(getActivity(), "ttt", Toast.LENGTH_SHORT).show();
        searchMaidModel.startTime = calendarTime.getTimeInMillis() + 3600000 + 3600000 + 3600000;
        if (searchMaidModel.startTime > 0) {
            Log.d(TAG, "setData: searchMaidModel.startTime:-- "+ searchMaidModel.startTime);
            calendarTime.setTimeInMillis(searchMaidModel.startTime);
        }
        //  }

       // Date c = Calendar.getInstance().getTime();
        Date c = calendarTime.getTime();
        Log.e(TAG, "setData: c value"+ c.toString());
        SimpleDateFormat dfH = new SimpleDateFormat("HH");
        String formattedDateH = dfH.format(c);
        Log.e("checkk", formattedDateH);
           showInitDate(false,formattedDateH,sdf);
        // show and hide views according to the Booking Condition either is opened from booking screen or not
        if (isBookAgain) {
            binding.vDivider3.setVisibility(View.VISIBLE);
            binding.tvAddress.setVisibility(View.GONE);
            binding.tvAddressValue.setVisibility(View.GONE);
            binding.vDivider2.setVisibility(View.GONE);
            binding.bulkLay.setEnabled(false);
            binding.tvSelectAgency.setVisibility(View.GONE);
            binding.tvArrow.setVisibility(View.GONE);
            binding.tvSearchMaid.setText(getString(R.string.next));
            binding.tvEdit.setVisibility(View.GONE);
        } else {
            binding.bulkLay.setEnabled(true);
            binding.vDivider3.setVisibility(View.GONE);
            binding.tvAddress.setVisibility(View.GONE);
            binding.tvAddressValue.setVisibility(View.GONE);
            binding.vDivider2.setVisibility(View.VISIBLE);
            binding.tvSelectAgency.setVisibility(View.GONE);
            binding.tvArrow.setVisibility(View.GONE);
            binding.tvSearchMaid.setText(getString(R.string.next));
            binding.tvEdit.setVisibility(View.GONE);
        }
        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
    }

    /*
     * used to set listeners on views
     */
    private void setListeners() {
        if (isBookAgain) {
            binding.tvLocation.setOnClickListener(null);
            binding.tvLocation.setVisibility(View.GONE);
        } else {
            binding.tvLocation.setOnClickListener(this);
        }
        binding.tvSearchMaid.setOnClickListener(this);
        binding.tvSelectAgency.setOnClickListener(this);
        binding.tvArrow.setOnClickListener(this);
        binding.tvSelecteddate.setOnClickListener(this);
        binding.tvtoSelecteddate.setOnClickListener(this);
        binding.tvSelectedtime.setOnClickListener(this);
        binding.tvSearchMaid.setOnClickListener(this);
        binding.tvEdit.setOnClickListener(this);
        binding.singleTxtview.setOnClickListener(this);
        binding.multipleTxtview.setOnClickListener(this);
        binding.bulkTxtview.setOnClickListener(this);
        binding.oneLay.setOnClickListener(this);
        binding.multiLay.setOnClickListener(this);
        binding.bulkLay.setOnClickListener(this);
        binding.rvTime.setOnClickListener(this);
        binding.tvServiceNext.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        Intent intent;
        ConfirmBookFragment confirmBookFragment;
        switch (view.getId()) {
            case R.id.tvSelecteddate:
                android.util.Log.e(TAG, "onClick: tvSelecteddate" );
                mFirebaseAnalytics.logEvent("bkmscr_tap_day", null);
               DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),R.style.DateDialogStyle,this, calendarDate.get(Calendar.YEAR),
                        calendarDate.get(Calendar.MONTH), calendarDate.get(Calendar.DAY_OF_MONTH));
                DatePicker datePicker = datePickerDialog.getDatePicker();
                datePicker.setMinDate((System.currentTimeMillis() + 172800000) - 1000);
                Calendar nextYear = Calendar.getInstance();
                nextYear.add(Calendar.MONTH, 3);
                datePicker.setMaxDate(nextYear.getTimeInMillis());
                datePickerDialog.show();

                break;

            case R.id.tvtoSelecteddate:
                mFirebaseAnalytics.logEvent("bkmscr_tap_day", null);
//             selectedDatesList.clear();
//              binding.tvtoSelecteddate.setText("");
               /* DatePickerFragment dialog = new DatePickerFragment();
                dialog.show(getActivity().getSupportFragmentManager(), DIALOG_DATE);
*/
                binding.tvSelectedtime.setText("");
                Intent intent1 = new Intent(getActivity(), PickerActivity.class);
                startActivity(intent1);

                /*  calendartoDate = Calendar.getInstance();
                DatePickerDialog datePickerDialogg = new DatePickerDialog(getActivity(), R.style.DateDialogStyle, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendartoDate.set(year, month, dayOfMonth);
                        calendartoDate.set(Calendar.MINUTE, 0);
                        calendartoDate.set(Calendar.SECOND, 0);
                        calendartoDate.set(Calendar.MILLISECOND, 0);
                        binding.tvtoSelecteddate.setText(GeneralFunction.getFormatFromDate(calendartoDate.getTime(), "EEEE, MMMM dd"));

                        calendarTime.set(Calendar.YEAR, calendartoDate.get(Calendar.YEAR));
                        calendarTime.set(Calendar.MONTH, calendartoDate.get(Calendar.MONTH));
                        calendarTime.set(Calendar.DAY_OF_MONTH, calendartoDate.get(Calendar.DAY_OF_MONTH));

                        if (isBookAgain) {
                            calendarTime.set(Calendar.MINUTE, 0);
                            calendarTime.set(Calendar.SECOND, 0);
                            calendarTime.set(Calendar.MILLISECOND, 0);

                            // searchMaidModel.workDate = calendartoDate.getTimeInMillis();
                            searchMaidModel.endDate = calendartoDate.getTimeInMillis();
                            searchMaidModel.startTime = calendarTime.getTimeInMillis();
                        }
                    }
                }, calendartoDate.get(Calendar.YEAR),
                        calendartoDate.get(Calendar.MONTH), calendartoDate.get(Calendar.DAY_OF_MONTH));
                DatePicker datePickerr = datePickerDialogg.getDatePicker();
                Calendar tempCalenderr = Calendar.getInstance();
                datePickerr.setMinDate(tempCalenderr.getTimeInMillis());

                tempCalenderr.add(Calendar.MONTH, 1);
                tempCalenderr.set(Calendar.DAY_OF_MONTH, tempCalenderr.getActualMaximum(Calendar.DAY_OF_MONTH));
              //  datePickerr.setMaxDate(tempCalenderr.getTimeInMillis());
                datePickerDialogg.show();*/
                break;

            case R.id.tvSelectedtime:
                mFirebaseAnalytics.logEvent("bkmscr_tap_time", null);
               /* AlertDialog.Builder builder;
                AlertDialog alert;
                builder = new AlertDialog.Builder(getActivity());


                builder.setTitle("Select Time");
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface d, int arg1) {
                        d.cancel();
                    }

                    ;
                });
               Log.e("difff",printDifference());
                if(Integer.parseInt(printDifference())>17)
                {
                    times = new String[]{"8:00 AM", "9:00 AM", "10:00 AM", "11:00 AM", "12:00 PM", "1:00 PM", "2:00 PM", "3:00 PM", "4:00 PM", "5:00 PM", "6:00 PM", "7:00 PM", "8:00 PM"};
                }
                else
                {
                    times = new String[]{ "10:00 AM", "11:00 AM", "12:00 PM", "1:00 PM", "2:00 PM", "3:00 PM", "4:00 PM", "5:00 PM", "6:00 PM", "7:00 PM", "8:00 PM"};
                }
                builder.setItems(times, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        // Do something with the selection
                     //   binding.tvSelectedtime.setText(times[item] + "");
                        calendarTime.set(Calendar.YEAR, calendarDate.get(Calendar.YEAR));
                        calendarTime.set(Calendar.MONTH, calendarDate.get(Calendar.MONTH));
                        calendarTime.set(Calendar.DAY_OF_MONTH, calendarDate.get(Calendar.DAY_OF_MONTH));
                        calendarTime.set(Calendar.HOUR, Integer.parseInt(times[item].split(":")[0]));
                        if (times[item].contains("AM")) {
                            calendarTime.set(Calendar.AM_PM, Calendar.AM);
                        } else {
                            calendarTime.set(Calendar.AM_PM, Calendar.PM);
                        }
                        showTime();
                    }
                });
                alert = builder.create();
                alert.show();*/
                if (booking_type == 2) {
                    if (binding.tvtoSelecteddate.getText().toString().equalsIgnoreCase("")) {
                        GeneralFunction.showSnackBar(getActivity(), binding.parent, getResources().getString(R.string.date_validationn));
                    } else {
                        openMonthDialog().show();
                    }
                } else {
                    if (binding.tvSelecteddate.getText().toString().equalsIgnoreCase("--,-- -")) {
                        GeneralFunction.showSnackBar(getActivity(), binding.parent, getResources().getString(R.string.date_validationn));
                    } else {
                        openMonthDialog().show();
                    }
                }
                break;
            case R.id.bulk_lay:
                mFirebaseAnalytics.logEvent("bkmscr_tap_bulk", null);
                binding.bulkTxtview.performClick();
                break;
            case R.id.multi_lay:
                mFirebaseAnalytics.logEvent("bkmscr_tap_mult", null);
                Log.e(TAG, " ------- multi lay click");
                binding.multipleTxtview.performClick();
                break;
            case R.id.one_lay:
                mFirebaseAnalytics.logEvent("bkmscr_tap_oneday", null);
                binding.singleTxtview.performClick();
                break;
            case R.id.single_txtview:
                binding.tvServiceDate.setText(R.string.datee);
                binding.tvtoServiceDate.setVisibility(View.GONE);
                binding.tvtoSelecteddate.setVisibility(View.GONE);
                binding.vDividerr.setVisibility(View.GONE);
                binding.vDivider22.setVisibility(View.GONE);
                binding.rvNoMaids.setVisibility(View.GONE);
                binding.tvSelectNoMaids.setVisibility(View.GONE);
                binding.tvServiceDate.setVisibility(View.VISIBLE);
                binding.tvSelecteddate.setVisibility(View.VISIBLE);
                //   binding.vDividerr.setVisibility(View.VISIBLE);
                booking_type = 1;
                selectedDatesList.clear();
                binding.multipleTxtview.setTextColor(Color.BLACK);
                if (isBookAgain) {
                }
                else {
                    binding.bulkTxtview.setBackgroundResource(0);
                    binding.bulkTxtview.setTextColor(Color.BLACK);
                }
                binding.singleTxtview.setTextColor(Color.WHITE);
                binding.singleTxtview.setBackgroundResource(R.drawable.green_rounded);
                binding.multipleTxtview.setBackgroundResource(0);

                setData();
                break;
            case R.id.multiple_txtview:
                Log.e(TAG, " ------- multi txt click");
                Log.e(TAG, " ------- reschedule status = " + reschuleStatus);
                Log.e(TAG, " ------- isBookAgain = " + isBookAgain);

                if (isBookAgain) {
                } else {
                    binding.bulkTxtview.setBackgroundResource(0);
                    binding.bulkTxtview.setTextColor(Color.BLACK);
                }

                selectedDatesList.clear();
                binding.tvtoSelecteddate.setText("");
                binding.tvSelectedtime.setText("");
                binding.tvtoServiceDate.setVisibility(View.VISIBLE);
                binding.tvtoSelecteddate.setVisibility(View.VISIBLE);
                binding.vDividerr.setVisibility(View.VISIBLE);
                binding.vDivider22.setVisibility(View.GONE);
                binding.rvNoMaids.setVisibility(View.GONE);
                binding.tvSelectNoMaids.setVisibility(View.GONE);
                binding.tvServiceDate.setVisibility(View.GONE);
                binding.vDividerr.setVisibility(View.GONE);
                binding.tvSelecteddate.setVisibility(View.GONE);

                booking_type = 2;
                binding.multipleTxtview.setTextColor(Color.WHITE);

                binding.singleTxtview.setTextColor(Color.BLACK);
                binding.multipleTxtview.setBackgroundResource(R.drawable.green_rounded);
                binding.singleTxtview.setBackgroundResource(0);

                setData();
                binding.tvSelectedtime.setText("");
                break;

            case R.id.bulk_txtview:
                Log.e(TAG, " ------- Bulk book click");
                Log.e(TAG, " ------- reschedule status = " + reschuleStatus);
                Log.e(TAG, " ------- isBookAgain = " + isBookAgain);
                selectedDatesList.clear();
                binding.tvServiceDate.setText(R.string.datee);
                binding.tvtoServiceDate.setVisibility(View.GONE);
                binding.tvtoSelecteddate.setVisibility(View.GONE);
                binding.vDividerr.setVisibility(View.GONE);
                binding.vDivider22.setVisibility(View.VISIBLE);
                binding.rvNoMaids.setVisibility(View.VISIBLE);
                binding.tvSelectNoMaids.setVisibility(View.VISIBLE);

                binding.tvServiceDate.setVisibility(View.VISIBLE);
                binding.tvSelecteddate.setVisibility(View.VISIBLE);
                //  binding.vDividerr.setVisibility(View.VISIBLE);
                booking_type = 3;

                binding.multipleTxtview.setTextColor(Color.BLACK);
                binding.bulkTxtview.setTextColor(Color.WHITE);
                binding.singleTxtview.setTextColor(Color.BLACK);

                binding.bulkTxtview.setBackgroundResource(R.drawable.green_rounded);
                binding.multipleTxtview.setBackgroundResource(0);
                binding.singleTxtview.setBackgroundResource(0);
                setData();

                break;

            case R.id.tvArrow:
            case R.id.tvSelectAgency:
             /*   intent = new Intent(getActivity(), AgencyActivity.class);
                intent.putStringArrayListExtra("selectedAgency",selectedAgencyList);
                startActivityForResult(intent,REQ_CODE);*/
                break;

            case R.id.tvSearchMaid:
                if(GeneralFunction.isNetworkConnected(requireActivity(),binding.cardView)) {
                    mFirebaseAnalytics.logEvent("bkmscr_click_next", null);
                    Log.d(TAG, "onClick:");

                    /////////////////// Reschedule Bulk booking start =================
                    if (reschuleStatus.equalsIgnoreCase("yes")) {
                        if (getArguments().getString(Constants.BOOKING_TYPE).equalsIgnoreCase("2")) {
                            if (!checkValidation())
                                return;
                            HashMap<String, String> hashMap = new HashMap<>();

                            hashMap.put("workDate", String.valueOf(searchMaidModel.workDate));
                            hashMap.put("limit", "200");
                            hashMap.put("startTime", String.valueOf(searchMaidModel.startTime));
                            hashMap.put("uniquieAppKey", bookingDataModel.uniquieAppKey);
                            hashMap.put("hour", String.valueOf(GeneralFunction.getTimeInTimeZone(new Date(searchMaidModel.startTime), TimeZone.getTimeZone(bookingDataModel.timeZone)).getTime()));
                            hashMap.put("endDate", String.valueOf(searchMaidModel.endDate));
                            hashMap.put("serviceId", SERVICE_ID);
                            hashMap.put("timeZone", bookingDataModel.timeZone);
                            hashMap.put("pageNo", "1");

                            presenter.apiSearchBulkMaidAgain(bookingDataModel, hashMap, Prefs.with(getActivity()).getString(Constants.ACCESS_TOKEN, ""), reschuleStatus, SERVICE_ID);

                        } else {

                            if (!checkValidation())
                                return;

                            FullAddress fullAddress = Prefs.with(getContext()).getObject(Constants.MAP_FULL_ADDRESS, FullAddress.class);
                            searchMaidModel.lat = Double.parseDouble(getArguments().getString("lat"));
                            searchMaidModel.lng = Double.parseDouble(getArguments().getString("lng"));
                            final String googleApiKey = getString(R.string.google_api_key_mak);
                            //get Time zone From Lat Long
                            presenter.getTimeZoneFromLatLong(searchMaidModel.lat, searchMaidModel.lng, googleApiKey);
                        }
                    } 
                    else {
                        if (booking_type == 3) {
                            if (!checkValidation())
                                return;
                            confirmBookFragment = ConfirmBookFragment.newInstance(null, true, true, "");
                            confirmBookFragment.setTargetFragment(this, BOOK_AGAIN_CODE);
                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                                    .add(android.R.id.content, confirmBookFragment, "ConfirmBookFragment")
                                    .addToBackStack("ConfirmBookFragment").commit();
                        }
                        else {
                            if (booking_type == 2) {
                                if (!checkValidation())
                                    return;
                                confirmBookFragment = ConfirmBookFragment.newInstance(null, true, true);
                                confirmBookFragment.setTargetFragment(this, BOOK_AGAIN_CODE);
                                getActivity().getSupportFragmentManager().beginTransaction()
                                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                                        .add(android.R.id.content, confirmBookFragment, "ConfirmBookFragment")
                                        .addToBackStack("ConfirmBookFragment").commit();
                            } 
                            else {
                                if (!checkValidation())
                                    return;
                                confirmBookFragment = ConfirmBookFragment.newInstance(null, true, true);
                                confirmBookFragment.setTargetFragment(this, BOOK_AGAIN_CODE);
                                getActivity().getSupportFragmentManager().beginTransaction()
                                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                                        .add(android.R.id.content, confirmBookFragment, "ConfirmBookFragment")
                                        .addToBackStack("ConfirmBookFragment").commit();
                            }
                        }
                    }
              /*  if (isBookAgain) {
                    final String googleApiKey = getString(R.string.google_api_key_mak);
                    presenter.getTimeZoneFromLatLong(searchMaidModel.lat, searchMaidModel.lng, googleApiKey);
                } else {
                    calendarTime.set(Calendar.SECOND, 0);
                    calendarTime.set(Calendar.MILLISECOND, 0);
                    SearchMaidModel searchMaidModel1 = new SearchMaidModel();
                    searchMaidModel1.uniquieAppKey = Constants.UNIQUE_APP_KEY;
                    searchMaidModel1.workDate = calendarDate.getTimeInMillis();
                    searchMaidModel1.startTime = calendarTime.getTimeInMillis();
                    searchMaidModel1.duration = duration;
                    searchMaidModel1.lng = searchMaidModel.lng;
                    searchMaidModel1.lat = searchMaidModel.lat;
                    searchMaidModel1.locationName = binding.tvLocation.getText().toString();
                    searchMaidModel1.selectedDate = binding.tvSelecteddate.getText().toString();
                    searchMaidModel1.selectedTime = binding.tvSelectedtime.getText().toString();
                    searchMaidModel1.selectedAgency = selectedAgencyList;
                    intent = new Intent(getActivity(), MaidBookActivity.class);
                    intent.putExtra(Constants.SEARCH_MAID_DATA, searchMaidModel1);
                    Log.d("Booking details", searchMaidModel1.toString());
                    startActivity(intent);
                }*/
                }else {
                    setLoading(false);
                }
                break;
            case R.id.tvLocation:
                confirmBookFragment = ConfirmBookFragment.newInstance(null, true, true);
                confirmBookFragment.setTargetFragment(this, BOOK_AGAIN_CODE);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                        .add(android.R.id.content, confirmBookFragment, "ConfirmBookFragment")
                        .addToBackStack("ConfirmBookFragment").commit();
                break;
            case R.id.tvEdit:
                confirmBookFragment = ConfirmBookFragment.newInstance(null, true, false);
                confirmBookFragment.setTargetFragment(this, BOOK_AGAIN_CODE);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                        .add(android.R.id.content, confirmBookFragment, "ConfirmBookFragment")
                        .addToBackStack("ConfirmBookFragment").commit();
                break;
            case R.id.rvTime:
                mFirebaseAnalytics.logEvent("bkmscr_tap_dur", null);
                break;
            case  R.id.tvServiceNext:
                break;

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(GeneralFunction.isNetworkConnected(requireActivity(),binding.cardView)) {
            presenter.getNotiCount(Prefs.with(getActivity()).getString(Constants.ACCESS_TOKEN, ""));
        }
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
        if (selectedDatesList.size() > 0) {
            Log.e("on finish", selectedDatesList + "");
            Collections.sort(selectedDatesList);
            Collections.reverse(selectedDatesList);
            String temp_dates = "";
            for (int i = 0; i < selectedDatesList.size(); i++) {
                //   temp_dates = GeneralFunction.getFormatFromDate(selectedDatesList.get(i), "EEEE, MMMM dd") + ","+temp_dates;
                temp_dates = GeneralFunction.getFormatFromDate(selectedDatesList.get(i), "dd MMMM") + ", " + temp_dates;
            }

            Log.e("temp_dates", "" + temp_dates.substring(0, temp_dates.length() - 2));

            binding.tvtoSelecteddate.setText(temp_dates.substring(0, temp_dates.length() - 2) + "");
        } else {
            binding.tvtoSelecteddate.setText("");
        }
    }


    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        calendarDate.set(year, month, day);
        calendarDate.set(Calendar.MINUTE, 0);
        calendarDate.set(Calendar.SECOND, 0);
        calendarDate.set(Calendar.MILLISECOND, 0);
        binding.tvSelectedtime.setText("");
        binding.tvSelecteddate.setText(GeneralFunction.getFormatFromDate(calendarDate.getTime(), "EEEE, MMMM dd"));

        calendarTime.set(Calendar.YEAR, calendarDate.get(Calendar.YEAR));
        calendarTime.set(Calendar.MONTH, calendarDate.get(Calendar.MONTH));
        calendarTime.set(Calendar.DAY_OF_MONTH, calendarDate.get(Calendar.DAY_OF_MONTH));

        if (isBookAgain) {
            calendarTime.set(Calendar.MINUTE, 0);
            calendarTime.set(Calendar.SECOND, 0);
            calendarTime.set(Calendar.MILLISECOND, 0);

            searchMaidModel.workDate = calendarDate.getTimeInMillis();
            searchMaidModel.startTime = calendarTime.getTimeInMillis();
        }
    }

    // used to convert particular format
    private void showTime() {
        calendarTime.set(Calendar.MINUTE, 0);
        calendarTime.set(Calendar.SECOND, 0);
        calendarTime.set(Calendar.MILLISECOND, 0);
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
        if (isBookAgain) {
            searchMaidModel.startTime = calendarTime.getTimeInMillis();
            if (searchMaidModel.startTime > 0) {
                calendarTime.setTimeInMillis(searchMaidModel.startTime);
            }
        }
        binding.tvSelectedtime.setText(sdf.format(calendarTime.getTime()));
    }


    private Dialog openMonthDialog() {
        printDifference();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();

        View dialog = inflater.inflate(R.layout.layout_time_picker, null);
        hourPicker = dialog.findViewById(R.id.hourPicker);
        final NumberPicker secPicker = dialog.findViewById(R.id.secPicker);
        final ImageView cross_picker = dialog.findViewById(R.id.cross_picker);
        final TextView select_txtview = dialog.findViewById(R.id.select_txtview);

        final String[] hourArray = new String[]{"1:00", "2:00", "3:00", "4:00", "5:00", "6:00",
                "7:00", "8:00", "9:00", "10:00", "11:00", "12:00"};

        hourPicker.setMinValue(1);
        hourPicker.setMaxValue(12);

        final int hour = calendarTime.get(Calendar.HOUR_OF_DAY);
        if (hour > 12) {
            hourPicker.setValue(hour - 12);
        } else {
            hourPicker.setValue(hour);
        }
        hourPicker.setDisplayedValues(hourArray);
        hourPicker.setWrapSelectorWheel(false);

        final String[] secArray = new String[]{"AM", "PM"};
        secPicker.setMinValue(0);
        secPicker.setMaxValue(1);
        secPicker.setDisplayedValues(secArray);

        int am_pm = calendarTime.get(Calendar.AM_PM);
        if (am_pm == Calendar.AM) {
            secPicker.setValue(0);
        } else {
            secPicker.setValue(1);
        }

        secPicker.setWrapSelectorWheel(false);
        builder.setView(dialog);
        AlertDialog dialogg = builder.create();

        cross_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogg.dismiss();
            }
        });

        select_txtview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int dd = hourPicker.getValue() == 12 ? 0 : hourPicker.getValue();
                // Noon and midnight are represented by 0, not by 12
                calendarTime.set(Calendar.YEAR, calendarDate.get(Calendar.YEAR));
                calendarTime.set(Calendar.MONTH, calendarDate.get(Calendar.MONTH));
                calendarTime.set(Calendar.DAY_OF_MONTH, calendarDate.get(Calendar.DAY_OF_MONTH));
                calendarTime.set(Calendar.HOUR, hourPicker.getValue() == 12 ? 0 : hourPicker.getValue());

                if (secPicker.getValue() == 0) {
                    calendarTime.set(Calendar.AM_PM, Calendar.AM);
                } else {
                    calendarTime.set(Calendar.AM_PM, Calendar.PM);
                }
                showTime();
                dialogg.dismiss();
            }
        });

        SimpleDateFormat curFormater = new SimpleDateFormat("EEEE, MMMM dd", Locale.ENGLISH);
        Date dateObj = null;
      //  String newDateStr = "";
      //  SimpleDateFormat postFormater = new SimpleDateFormat("EEEE, MMMM dd", Locale.ENGLISH);
        try {
            if (booking_type == 2) {
                if (selectedDatesList.size() > 0) {
                    Collections.sort(selectedDatesList);
                    String mytime = GeneralFunction.getFormatFromDate(selectedDatesList.get(0), "EEEE, MMMM dd") + " " + binding.tvSelectedtime.getText().toString();
                    dateObj = curFormater.parse(mytime);
                }
            } else {
                dateObj = curFormater.parse(binding.tvSelecteddate.getText().toString());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat postFormater = new SimpleDateFormat("EEEE, MMMM dd", Locale.ENGLISH);
        String newDateStr = postFormater.format(dateObj);
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
        SimpleDateFormat df = new SimpleDateFormat("EEEE, MMMM dd", Locale.ENGLISH);
        String formattedDate = df.format(c);
        SimpleDateFormat dfH = new SimpleDateFormat("HH", Locale.ENGLISH);
        String formattedDateH = dfH.format(c);
        Log.e("d1", "==" + formattedDate + "");
        int morningStartTime = 8;
        if (getCountOfDays(formattedDate, newDateStr) == 1) {
            morningStartTime = 9;
        } else if (getCountOfDays(formattedDate, newDateStr) == 2) {
            if (Integer.parseInt(formattedDateH) == 16) {
                morningStartTime = 8;
                hourPicker.setValue(9);
            } else if (Integer.parseInt(formattedDateH) > 16) {
                morningStartTime = 9;
                hourPicker.setValue(10);
            } else {
                morningStartTime = 7;
                hourPicker.setValue(8);
            }
            // hourPicker.setValue(10);
            secPicker.setValue(0);
        } else {
            morningStartTime = 7;
            secPicker.setValue(0);
            hourPicker.setValue(8);
        }

        if (secArray[secPicker.getValue()].equalsIgnoreCase("PM") && hourPicker.getValue() > 8 && hourPicker.getValue() < 12) {
            //select_txtview.setBackgroundColor(getResources().getColor(R.color.app_grey));
            select_txtview.setEnabled(false);
            select_txtview.setAlpha(0.3f);

        } else if (secArray[secPicker.getValue()].equalsIgnoreCase("AM") && hourPicker.getValue() <= morningStartTime) {
            //select_txtview.setBackgroundColor(getResources().getColor(R.color.app_grey));
            select_txtview.setEnabled(false);
            select_txtview.setAlpha(0.3f);
        } else {
            if (secArray[secPicker.getValue()].equalsIgnoreCase("AM") && hourPicker.getValue() == 12) {
                //select_txtview.setBackgroundColor(getResources().getColor(R.color.app_grey));
                select_txtview.setEnabled(false);
                select_txtview.setAlpha(0.3f);
            } else {
                //select_txtview.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                select_txtview.setEnabled(true);
                select_txtview.setAlpha(1.0f);
            }
        }

        hourPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                Log.e("picker value", picker.getValue() + "," + secArray[secPicker.getValue()]);
                if (hourPicker.getValue() == 12 && secArray[secPicker.getValue()].equalsIgnoreCase("AM")) {
                    secPicker.setValue(1);
                }

                if (hourPicker.getValue() == 9 && secArray[secPicker.getValue()].equalsIgnoreCase("PM")) {
                  //  select_txtview.setBackgroundColor(getResources().getColor(R.color.app_grey));
                    select_txtview.setEnabled(false);
                    select_txtview.setAlpha(0.3f);
                }

                if (getCountOfDays(formattedDate, newDateStr) == 2) {
                    if (Integer.parseInt(formattedDateH) == 16) {
                        if (hourArray[newVal - 1].equalsIgnoreCase("8:00") && secArray[secPicker.getValue()].equalsIgnoreCase("AM")) {
                           // select_txtview.setBackgroundColor(getResources().getColor(R.color.app_grey));
                            select_txtview.setEnabled(false);
                            select_txtview.setAlpha(0.3f);
                        } else {
                           // select_txtview.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                            select_txtview.setEnabled(true);
                            select_txtview.setAlpha(1.0f);
                        }

                    } else if (Integer.parseInt(formattedDateH) == 17) {
                        if (hourArray[newVal - 1].equalsIgnoreCase("8:00") && secArray[secPicker.getValue()].equalsIgnoreCase("AM")) {
                           // select_txtview.setBackgroundColor(getResources().getColor(R.color.app_grey));
                            select_txtview.setEnabled(false);
                            select_txtview.setAlpha(0.3f);

                        } else if (hourArray[newVal - 1].equalsIgnoreCase("9:00") && secArray[secPicker.getValue()].equalsIgnoreCase("AM")) {
                           // select_txtview.setBackgroundColor(getResources().getColor(R.color.app_grey));
                            select_txtview.setEnabled(false);
                            select_txtview.setAlpha(0.3f);

                        } else {
                           // select_txtview.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                            select_txtview.setEnabled(true);
                            select_txtview.setAlpha(1.0f);
                        }

                    } else if (Integer.parseInt(formattedDateH) >= 18) {


                        if (secArray[secPicker.getValue()].equalsIgnoreCase("AM") && hourPicker.getValue() < 10) {
                            //select_txtview.setBackgroundColor(getResources().getColor(R.color.app_grey));
                            select_txtview.setEnabled(false);
                            select_txtview.setAlpha(0.3f);
                        } else {

                            //select_txtview.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                            select_txtview.setEnabled(true);
                            select_txtview.setAlpha(1.0f);
                        }
                    } else {

                        //select_txtview.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        select_txtview.setEnabled(true);
                        select_txtview.setAlpha(1.0f);
                    }

                    if (secArray[secPicker.getValue()].equalsIgnoreCase("PM") && hourPicker.getValue() > 8 && hourPicker.getValue() < 12) {
                        //select_txtview.setBackgroundColor(getResources().getColor(R.color.app_grey));
                        select_txtview.setEnabled(false);
                        select_txtview.setAlpha(0.3f);
                    }
                    if (secArray[secPicker.getValue()].equalsIgnoreCase("AM") && hourPicker.getValue() < 8) {
                       // select_txtview.setBackgroundColor(getResources().getColor(R.color.app_grey));
                        select_txtview.setEnabled(false);
                        select_txtview.setAlpha(0.3f);
                    }
                    if (secArray[secPicker.getValue()].equalsIgnoreCase("AM") && hourPicker.getValue() == 12) {
                        //select_txtview.setBackgroundColor(getResources().getColor(R.color.app_grey));
                        select_txtview.setEnabled(false);
                        select_txtview.setAlpha(0.3f);
                    }

                } else if (getCountOfDays(formattedDate, newDateStr) == 1) {

                    if ((Integer.parseInt(formattedDateH) + 2) <= 12 && (Integer.parseInt(formattedDateH) + 2) > 9) {
                        Log.e("if1==", "enter");

                        if (secArray[secPicker.getValue()].equalsIgnoreCase("AM") && hourPicker.getValue() <= 9) {
                           // select_txtview.setBackgroundColor(getResources().getColor(R.color.app_grey));
                            select_txtview.setEnabled(false);
                            select_txtview.setAlpha(0.3f);
                        }

                        if (secArray[secPicker.getValue()].equalsIgnoreCase("PM") && hourPicker.getValue() > 8
                                && hourPicker.getValue() < 12) {
                          //  select_txtview.setBackgroundColor(getResources().getColor(R.color.app_grey));
                            select_txtview.setEnabled(false);
                            select_txtview.setAlpha(0.3f);
                        }
                        if (secArray[secPicker.getValue()].equalsIgnoreCase("AM") &&
                                hourPicker.getValue() <= (Integer.parseInt(formattedDateH) + 2)) {
                            //select_txtview.setBackgroundColor(getResources().getColor(R.color.app_grey));
                            select_txtview.setEnabled(false);
                            select_txtview.setAlpha(0.3f);
                        } else {


                            if ((Integer.parseInt(formattedDateH) + 2) == 12) {
                                if (secArray[secPicker.getValue()].equalsIgnoreCase("PM") &&
                                        hourPicker.getValue() == 12) {
                                   // select_txtview.setBackgroundColor(getResources().getColor(R.color.app_grey));
                                    select_txtview.setEnabled(false);
                                    select_txtview.setAlpha(0.3f);
                                } else {
                                    //select_txtview.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                    select_txtview.setEnabled(true);
                                    select_txtview.setAlpha(1.0f);
                                }
                            } else {
                               // select_txtview.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                select_txtview.setEnabled(true);
                                select_txtview.setAlpha(1.0f);
                            }

                        }
                    } else if ((Integer.parseInt(formattedDateH) + 2) > 12 && (Integer.parseInt(formattedDateH) + 2) < 24) {

                        if (hourPicker.getValue() <= ((Integer.parseInt(formattedDateH) + 2) - 12)) {

                            //select_txtview.setBackgroundColor(getResources().getColor(R.color.app_grey));
                            select_txtview.setEnabled(false);
                            select_txtview.setAlpha(0.3f);
                        } else {
                           // select_txtview.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                            select_txtview.setEnabled(true);
                            select_txtview.setAlpha(1.0f);
                        }

                        if (secArray[secPicker.getValue()].equalsIgnoreCase("AM") && hourPicker.getValue() <= 12) {
                            //select_txtview.setBackgroundColor(getResources().getColor(R.color.app_grey));
                            select_txtview.setEnabled(false);
                            select_txtview.setAlpha(0.3f);
                        }

                        if (secArray[secPicker.getValue()].equalsIgnoreCase("PM") && hourPicker.getValue() > 8) {
                            //select_txtview.setBackgroundColor(getResources().getColor(R.color.app_grey));
                            select_txtview.setEnabled(false);
                            select_txtview.setAlpha(0.3f);
                        }
                    } else {

                        if (secArray[secPicker.getValue()].equalsIgnoreCase("PM") && hourPicker.getValue() > 8
                                && hourPicker.getValue() < 12) {
                           // select_txtview.setBackgroundColor(getResources().getColor(R.color.app_grey));
                            select_txtview.setEnabled(false);
                            select_txtview.setAlpha(0.3f);
                        } else if (secArray[secPicker.getValue()].equalsIgnoreCase("AM") && hourPicker.getValue() <= 9) {
                            //select_txtview.setBackgroundColor(getResources().getColor(R.color.app_grey));
                            select_txtview.setEnabled(false);
                            select_txtview.setAlpha(0.3f);
                        } else if (secArray[secPicker.getValue()].equalsIgnoreCase("AM") && hourPicker.getValue() == 12) {
                            //select_txtview.setBackgroundColor(getResources().getColor(R.color.app_grey));
                            select_txtview.setEnabled(false);
                            select_txtview.setAlpha(0.3f);
                        } else {
                            //select_txtview.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                            select_txtview.setEnabled(true);
                            select_txtview.setAlpha(1.0f);
                        }

                    }

                    if (secArray[secPicker.getValue()].equalsIgnoreCase("PM") && hourPicker.getValue() > 8 && hourPicker.getValue() < 12) {
                       // select_txtview.setBackgroundColor(getResources().getColor(R.color.app_grey));
                        select_txtview.setEnabled(false);
                        select_txtview.setAlpha(0.3f);
                    }
                    if (secArray[secPicker.getValue()].equalsIgnoreCase("AM") && hourPicker.getValue() < 8) {
                        //select_txtview.setBackgroundColor(getResources().getColor(R.color.app_grey));
                        select_txtview.setEnabled(false);
                        select_txtview.setAlpha(0.3f);
                    }
                    if (secArray[secPicker.getValue()].equalsIgnoreCase("AM") && hourPicker.getValue() == 12) {
                        //select_txtview.setBackgroundColor(getResources().getColor(R.color.app_grey));
                        select_txtview.setEnabled(false);
                        select_txtview.setAlpha(0.3f);
                    }
                    if (secArray[secPicker.getValue()].equalsIgnoreCase("PM") && Integer.parseInt(formattedDateH) > 15) {
                        if (hourPicker.getValue() == 12) {
                           // select_txtview.setBackgroundColor(getResources().getColor(R.color.app_grey));
                            select_txtview.setEnabled(false);
                            select_txtview.setAlpha(0.3f);
                        }
                    }

                } else {


                    if (secArray[secPicker.getValue()].equalsIgnoreCase("PM") && hourPicker.getValue() > 8 && hourPicker.getValue() < 12) {
                       // select_txtview.setBackgroundColor(getResources().getColor(R.color.app_grey));
                        select_txtview.setEnabled(false);
                        select_txtview.setAlpha(0.3f);
                    } else if (secArray[secPicker.getValue()].equalsIgnoreCase("AM") && hourPicker.getValue() < 8) {
                        //select_txtview.setBackgroundColor(getResources().getColor(R.color.app_grey));
                        select_txtview.setEnabled(false);
                        select_txtview.setAlpha(0.3f);
                    } else {
                        if (secArray[secPicker.getValue()].equalsIgnoreCase("AM") && hourPicker.getValue() == 12) {
                           // select_txtview.setBackgroundColor(getResources().getColor(R.color.app_grey));
                            select_txtview.setEnabled(false);
                            select_txtview.setAlpha(0.3f);
                        } else {
                           // select_txtview.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                            select_txtview.setEnabled(true);
                            select_txtview.setAlpha(1.0f);
                        }
                    }
                }

            }
        });

        secPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                Log.e("dddddd", hourArray[hourPicker.getValue() - 1] + "," + secArray[secPicker.getValue()]);
                if (hourPicker.getValue() == 12 && secArray[secPicker.getValue()].equalsIgnoreCase("AM")) {
                    secPicker.setValue(1);
                }

                if (getCountOfDays(formattedDate, newDateStr) == 2) {
                    if (Integer.parseInt(formattedDateH) == 16) {
                        if (hourArray[hourPicker.getValue() - 1].equalsIgnoreCase("8:00") && secArray[secPicker.getValue()].equalsIgnoreCase("AM")) {
                           // select_txtview.setBackgroundColor(getResources().getColor(R.color.app_grey));
                            select_txtview.setEnabled(false);
                            select_txtview.setAlpha(0.3f);
                        } else {
                            //select_txtview.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                            select_txtview.setEnabled(true);
                            select_txtview.setAlpha(1.0f);
                        }

                    } else if (Integer.parseInt(formattedDateH) == 17) {
                        if (hourArray[hourPicker.getValue() - 1].equalsIgnoreCase("8:00") && secArray[secPicker.getValue()].equalsIgnoreCase("AM")) {
                           // select_txtview.setBackgroundColor(getResources().getColor(R.color.app_grey));
                            select_txtview.setEnabled(false);
                            select_txtview.setAlpha(0.3f);

                        } else if (hourArray[hourPicker.getValue() - 1].equalsIgnoreCase("9:00") && secArray[secPicker.getValue()].equalsIgnoreCase("AM")) {
                           // select_txtview.setBackgroundColor(getResources().getColor(R.color.app_grey));
                            select_txtview.setEnabled(false);
                            select_txtview.setAlpha(0.3f);

                        } else {
                           // select_txtview.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                            select_txtview.setEnabled(true);
                            select_txtview.setAlpha(1.0f);
                        }

                    } else if (Integer.parseInt(formattedDateH) >= 18) {

                        if (secArray[secPicker.getValue()].equalsIgnoreCase("AM") && hourPicker.getValue() < 10) {
                           //select_txtview.setBackgroundColor(getResources().getColor(R.color.app_grey));
                            select_txtview.setEnabled(false);
                            select_txtview.setAlpha(0.3f);
                        } else {

                            //select_txtview.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                            select_txtview.setEnabled(true);
                            select_txtview.setAlpha(1.0f);
                        }
                    } else {

                        //select_txtview.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        select_txtview.setEnabled(true);
                        select_txtview.setAlpha(1.0f);
                    }

                    if (secArray[secPicker.getValue()].equalsIgnoreCase("PM") && hourPicker.getValue() > 8 && hourPicker.getValue() < 12) {
                       // select_txtview.setBackgroundColor(getResources().getColor(R.color.app_grey));
                        select_txtview.setEnabled(false);
                        select_txtview.setAlpha(0.3f);
                    }
                    if (secArray[secPicker.getValue()].equalsIgnoreCase("AM") && hourPicker.getValue() < 8) {
                        //select_txtview.setBackgroundColor(getResources().getColor(R.color.app_grey));
                        select_txtview.setEnabled(false);
                        select_txtview.setAlpha(0.3f);
                    }
                    if (secArray[secPicker.getValue()].equalsIgnoreCase("AM") && hourPicker.getValue() == 12) {
                        //select_txtview.setBackgroundColor(getResources().getColor(R.color.app_grey));
                        select_txtview.setEnabled(false);
                        select_txtview.setAlpha(0.3f);
                    }

                } else if (getCountOfDays(formattedDate, newDateStr) == 1) {
                    if ((Integer.parseInt(formattedDateH) + 2) <= 12 && (Integer.parseInt(formattedDateH) + 2) > 9) {
                        Log.e("if1==", "enter");
                        if (secArray[secPicker.getValue()].equalsIgnoreCase("AM")
                                && hourPicker.getValue() <= 9) {
                            //select_txtview.setBackgroundColor(getResources().getColor(R.color.app_grey));
                            select_txtview.setEnabled(false);
                            select_txtview.setAlpha(0.3f);
                        }

                        if (secArray[secPicker.getValue()].equalsIgnoreCase("PM")
                                && hourPicker.getValue() > 8 && hourPicker.getValue() < 12) {
                            //select_txtview.setBackgroundColor(getResources().getColor(R.color.app_grey));
                            select_txtview.setEnabled(false);
                            select_txtview.setAlpha(0.3f);
                        }
                        if (secArray[secPicker.getValue()].equalsIgnoreCase("AM") && hourPicker.getValue() <= (Integer.parseInt(formattedDateH) + 2)) {
                            //select_txtview.setBackgroundColor(getResources().getColor(R.color.app_grey));
                            select_txtview.setEnabled(false);
                            select_txtview.setAlpha(0.3f);
                        } else {
                            if ((Integer.parseInt(formattedDateH) + 2) == 12) {
                                if (secArray[secPicker.getValue()].equalsIgnoreCase("PM") &&
                                        hourPicker.getValue() == 12) {
                                    //select_txtview.setBackgroundColor(getResources().getColor(R.color.app_grey));
                                    select_txtview.setEnabled(false);
                                    select_txtview.setAlpha(0.3f);
                                } else {
                                    //select_txtview.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                    select_txtview.setEnabled(true);
                                    select_txtview.setAlpha(1.0f);
                                }
                            } else {
                                //select_txtview.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                select_txtview.setEnabled(true);
                                select_txtview.setAlpha(1.0f);
                            }

                        }

                    } else if ((Integer.parseInt(formattedDateH) + 2) > 12 && (Integer.parseInt(formattedDateH) + 2) < 24) {
                        Log.e("if2==", "enter");

                        if (hourPicker.getValue() <= ((Integer.parseInt(formattedDateH) + 2) - 12)) {
                            //select_txtview.setBackgroundColor(getResources().getColor(R.color.app_grey));
                            select_txtview.setEnabled(false);
                            select_txtview.setAlpha(0.3f);
                        } else {
                            //select_txtview.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                            select_txtview.setEnabled(true);
                            select_txtview.setAlpha(1.0f);
                        }

                        if (secArray[secPicker.getValue()].equalsIgnoreCase("AM") && hourPicker.getValue() <= 12) {
                            //select_txtview.setBackgroundColor(getResources().getColor(R.color.app_grey));
                            select_txtview.setEnabled(false);
                            select_txtview.setAlpha(0.3f);
                        }

                        if (secArray[secPicker.getValue()].equalsIgnoreCase("PM") && hourPicker.getValue() > 8) {
                            //select_txtview.setBackgroundColor(getResources().getColor(R.color.app_grey));
                            select_txtview.setEnabled(false);
                            select_txtview.setAlpha(0.3f);
                        }
                    } else {

                        if (secArray[secPicker.getValue()].equalsIgnoreCase("PM") && hourPicker.getValue() > 8 && hourPicker.getValue() < 12) {
                            //select_txtview.setBackgroundColor(getResources().getColor(R.color.app_grey));
                            select_txtview.setEnabled(false);
                            select_txtview.setAlpha(0.3f);
                        } else if (secArray[secPicker.getValue()].equalsIgnoreCase("AM") && hourPicker.getValue() <= 9) {
                            //select_txtview.setBackgroundColor(getResources().getColor(R.color.app_grey));
                            select_txtview.setEnabled(false);
                            select_txtview.setAlpha(0.3f);
                        } else if (secArray[secPicker.getValue()].equalsIgnoreCase("AM") && hourPicker.getValue() == 12) {
                            //select_txtview.setBackgroundColor(getResources().getColor(R.color.app_grey));
                            select_txtview.setEnabled(false);
                            select_txtview.setAlpha(0.3f);
                        } else {
                            //select_txtview.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                            select_txtview.setEnabled(true);
                            select_txtview.setAlpha(1.0f);
                        }

                    }

                    if (secArray[secPicker.getValue()].equalsIgnoreCase("PM") && hourPicker.getValue() > 8 && hourPicker.getValue() < 12) {
                        //select_txtview.setBackgroundColor(getResources().getColor(R.color.app_grey));
                        select_txtview.setEnabled(false);
                        select_txtview.setAlpha(0.3f);
                    }
                    if (secArray[secPicker.getValue()].equalsIgnoreCase("AM") && hourPicker.getValue() < 8) {
                        //select_txtview.setBackgroundColor(getResources().getColor(R.color.app_grey));
                        select_txtview.setEnabled(false);
                        select_txtview.setAlpha(0.3f);
                    }
                    if (secArray[secPicker.getValue()].equalsIgnoreCase("AM") && hourPicker.getValue() == 12) {
                        //select_txtview.setBackgroundColor(getResources().getColor(R.color.app_grey));
                        select_txtview.setEnabled(false);
                        select_txtview.setAlpha(0.3f);
                    }
                    if (secArray[secPicker.getValue()].equalsIgnoreCase("PM") && Integer.parseInt(formattedDateH) > 15) {
                        if (hourPicker.getValue() == 12) {
                            //select_txtview.setBackgroundColor(getResources().getColor(R.color.app_grey));
                            select_txtview.setEnabled(false);
                            select_txtview.setAlpha(0.3f);
                        }
                    }

                } else {

                    if (secArray[secPicker.getValue()].equalsIgnoreCase("PM") && hourPicker.getValue() > 8 && hourPicker.getValue() < 12) {
                        //select_txtview.setBackgroundColor(getResources().getColor(R.color.app_grey));
                        select_txtview.setEnabled(false);
                        select_txtview.setAlpha(0.3f);
                    } else if (secArray[secPicker.getValue()].equalsIgnoreCase("AM") && hourPicker.getValue() < 8) {
                        //select_txtview.setBackgroundColor(getResources().getColor(R.color.app_grey));
                        select_txtview.setEnabled(false);
                        select_txtview.setAlpha(0.3f);
                    } else {
                        if (secArray[secPicker.getValue()].equalsIgnoreCase("AM") && hourPicker.getValue() == 12) {
                           // select_txtview.setBackgroundColor(getResources().getColor(R.color.app_grey));
                            select_txtview.setEnabled(false);
                            select_txtview.setAlpha(0.3f);
                        } else {
                           // select_txtview.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                            select_txtview.setEnabled(true);
                            select_txtview.setAlpha(1.0f);
                        }
                    }
                }
            }
        });
        return dialogg;
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

    }

    @Override
    public void signupError(String errorMessage) {
        new DialogPopup().alertPopup(getActivity(), getResources().getString(R.string.dialog_alert), errorMessage, "").show(requireActivity().getSupportFragmentManager(), "ios_dialog");;
    }

    @Override
    public void signupFailure(String failureMessage) {
      Log.e(TAG, "signupFailure: "+failureMessage);
        new DialogPopup().alertPopup(getActivity(), getResources().getString(R.string.dialog_alert), getString(R.string.check_connection), "").show(requireActivity().getSupportFragmentManager(), "ios_dialog");;
    }

    @Override
    public void bookServiceSuccess(PojoService body) {
        searchMaidModel.agencyId=body.data.agencyId;
        TimeSlot timeSlot = getArguments().getParcelable(Constants.MAID_AVAILABLE_TIMESLOT);
        ConfirmBookFragment confirmBookFragment = ConfirmBookFragment.newInstance(
                searchMaidModel,
                false,
                false,
                body.data.serviceId.get(0),
                timeSlot, body.data.referenceId,
                body.data.vat);

        confirmBookFragment.setTargetFragment(this, BOOK_AGAIN_CODE);
        getActivity().getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                .add(android.R.id.content, confirmBookFragment, "ConfirmBookFragment")
                .addToBackStack("ConfirmBookFragment").commit();

        /*Intent intent1 = new Intent(getActivity(), ExtendPaymentActivity.class);
        intent1.putExtra(Constants.SERVICE_ID, body.data.serviceId);
        intent1.putExtra(Constants.SEARCH_MAID_DATA, searchMaidModel);
        intent1.putExtra(Constants.BOOK_AGAIN, true);
        if (getArguments().containsKey(Constants.MAID_AVAILABLE_TIMESLOT))
            intent1.putExtra(Constants.MAID_AVAILABLE_TIMESLOT, getArguments().getParcelable(Constants.MAID_AVAILABLE_TIMESLOT));
        startActivityForResult(intent1, REQ_CODE);*/
    }

    @Override
    public void ServiceListSuccess(ServicelistResponse body) {

    }

    @Override
    public void bookServiceSuccessNew(PojoServiceNew body) {

        Log.e("book service success", "" + new Gson().toJson(body));
        TimeSlot timeSlot = getArguments().getParcelable(Constants.MAID_AVAILABLE_TIMESLOT);
        maidData.setVat(body.data.vat);
        maidData.setNew_vat(body.data.new_vat);
        Log.d(TAG, "bookServiceSuccessNew: maid Data:-- "+ new Gson().toJson(maidData));
        try{
            if(body.data.enable3ds == null) {
                body.data.enable3ds = false;
            }
                android.util.Log.e(TAG, "bookServiceSuccessNew: pojoService.data.enable3ds : "+ (body.data.enable3ds instanceof Boolean) + "  "+ body.data.enable3ds );
                if (body.data.enable3ds instanceof Boolean) {
                    Prefs.with(requireContext()).save(Constants.ENABLE_3DS,body.data.enable3ds );
                }

        }catch (Exception e){
            e.printStackTrace();
        }

        MaidProfileFragment maidProfileFragment = MaidProfileFragment.newInstance(bookingDataModel, maidData, searchMaidModel, reschuleStatus,body.data.serviceId);
        getActivity().getSupportFragmentManager().beginTransaction()
//                 .addSharedElement(ivMaid, adapterPosition + "")
                //.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(android.R.id.content, maidProfileFragment, "MaidProfileFragment")
                .addToBackStack("MaidProfileFragment").commit();

        /*    searchMaidModel.agencyId=body.data.agencyId;
        ConfirmBookFragment confirmBookFragment = ConfirmBookFragment.newInstance(
                searchMaidModel,
                false,
                false,
                body.data.serviceId,
                timeSlot, body.data.referenceId,body.data.vat);
        confirmBookFragment.setTargetFragment(this, BOOK_AGAIN_CODE);
        getActivity().getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                .add(android.R.id.content, confirmBookFragment, "ConfirmBookFragment")
                .addToBackStack("ConfirmBookFragment").commit();*/

    }

    @Override
    public void displayTimeSlots(String errorMessage, TimeSlot timeSlot) {
        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getActivity(), SlotAvailbleActivity.class);
        intent.putExtra("data", timeSlot);
        startActivity(intent);
    }

    @Override
    public void reschduleError(String errorMessage) {
        if (errorMessage.equalsIgnoreCase("error")) {
            alertReschduleSuccess(getActivity(), getResources().getString(R.string.sorry_no_maids), getString(R.string.proceed_other_maids), "error");
        } else if (errorMessage.equalsIgnoreCase("done")) {
            alertReschduleSuccess(getActivity(), "", getResources().getString(R.string.reschdule_success), "success");
        }
    }

    public Dialog alertReschdule(final Activity activity, String title, String message, final String customMessage) {
        Dialog dialog = null;
        try {
//
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

            ok.setText(getResources().getString(R.string.yes));
            cancel.setText(getResources().getString(R.string.no));

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
                    if (getArguments().getString(Constants.BOOKING_TYPE).equalsIgnoreCase("2")) {
                        booking_type = 3;
                        calendarTime.set(Calendar.SECOND, 0);
                        calendarTime.set(Calendar.MILLISECOND, 0);
                        SearchMaidBulkModel bulkModel = new SearchMaidBulkModel();
                        bulkModel.uniquieAppKey = Constants.UNIQUE_APP_KEY;
                        bulkModel.workDate = calendarDate.getTimeInMillis();

                        if (booking_type == 1) {
                            bulkModel.endDate = calendarDate.getTimeInMillis();
                        } else if (booking_type == 2) {
                            bulkModel.endDate = calendartoDate.getTimeInMillis();
                        }
                        bulkModel.hour = GeneralFunction.getTimeInTimeZone(new Date(searchMaidModel.startTime), TimeZone.getTimeZone(bookingDataModel.timeZone)).getTime();

                        bulkModel.startTime = calendarTime.getTimeInMillis();
                        bulkModel.duration = bookingDataModel.duration;
                        bulkModel.maidCount = Integer.parseInt(bookingDataModel.maidCount);
                        bulkModel.lng = searchMaidModel.lng;
                        bulkModel.lat = searchMaidModel.lat;
                        bulkModel.locationName = bookingDataModel.locationName;
                        bulkModel.selectedDate = binding.tvSelecteddate.getText().toString();
                        bulkModel.selectedTime = binding.tvSelectedtime.getText().toString();
                        bulkModel.selectedAgency = selectedAgencyList;
                        Intent intent = new Intent(getActivity(), MaidBookActivity.class);
                        intent.putExtra(Constants.SEARCH_MAID_DATA, bulkModel);
                        intent.putExtra(Constants.BOOKING_DATA, new Gson().toJson(bookingDataModel) + "");
                        intent.putExtra(Constants.BOOKING_TYPE, String.valueOf(booking_type));
                        intent.putExtra(Constants.reschuleStatus, reschuleStatus + "");
                        intent.putExtra(Constants.SERVICE_ID, bookingDataModel._id + "");
                        Log.d("Booking details bulk", bulkModel.toString());
                        startActivity(intent);

                    }
                    else {
                        Log.e("Booking details", searchMaidModel.toString());
                        //  supportFragmentManager.popBackStack();
                        //  activity.finish();
                        SearchMaidModel searchMaidModel1 = new SearchMaidModel();
                        searchMaidModel1.uniquieAppKey = Constants.UNIQUE_APP_KEY;
                        searchMaidModel1.workDate = calendarDate.getTimeInMillis();

                        if (booking_type == 1) {

                            searchMaidModel1.startTime = calendarTime.getTimeInMillis();
                            searchMaidModel1.endDate = calendarDate.getTimeInMillis();
                        } else if (booking_type == 2) {
                            String mytime = GeneralFunction.getFormatFromDate(selectedDatesList.get(0), "dd MMMM") + " " + binding.tvSelectedtime.getText().toString();
                            Log.e("mytime", mytime);

                            SimpleDateFormat dateFormat = new SimpleDateFormat(
                                    "dd MMMM hh:mm a", Locale.ENGLISH);

                            Date myDate = null;
                            try {
                                myDate = dateFormat.parse(mytime);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            Log.e("f date", myDate.toString());
                            searchMaidModel1.startTime = myDate.getTime();
                            searchMaidModel1.endDate = calendartoDate.getTimeInMillis();
                        }

                        if (reschuleStatus.equalsIgnoreCase("yes")) {
                            searchMaidModel1.duration = searchMaidModel.duration;
                        } else {
                            searchMaidModel1.duration = duration;
                        }

                        if (reschuleStatus.equalsIgnoreCase("yes")) {
                            searchMaidModel1.lat = Double.parseDouble(getArguments().getString("lat"));
                            searchMaidModel1.lng = Double.parseDouble(getArguments().getString("lng"));
                        } else {
                            searchMaidModel1.lng = searchMaidModel.lng;
                            searchMaidModel1.lat = searchMaidModel.lat;
                        }
                        searchMaidModel1.locationName = binding.tvLocation.getText().toString();
                        searchMaidModel1.selectedDate = binding.tvSelecteddate.getText().toString();
                        searchMaidModel1.selectedToDate = binding.tvtoSelecteddate.getText().toString();
                        searchMaidModel1.hour = GeneralFunction.getTimeInTimeZone(new Date(searchMaidModel.startTime), tempTimeZone).getTime();

                        searchMaidModel1.selectedTime = binding.tvSelectedtime.getText().toString();
                        searchMaidModel1.selectedAgency = selectedAgencyList;
                        Intent intent = new Intent(getActivity(), MaidBookActivity.class);
                        intent.putExtra(Constants.SEARCH_MAID_DATA, searchMaidModel1);
                        intent.putExtra(Constants.BOOKING_TYPE, booking_type + "");
                        intent.putExtra(Constants.BOOKING_DATA, new Gson().toJson(bookingDataModel) + "");
                        intent.putExtra(Constants.reschuleStatus, reschuleStatus + "");
                        intent.putExtra(Constants.SERVICE_ID, bookingDataModel._id + "");
                        Log.e("Booking a", searchMaidModel1.toString());

                        startActivity(intent);
                    }
                    finalDialog.dismiss();
                }
            });
            dialog.show();


        } catch (Exception e) {
            e.printStackTrace();
        }

        return dialog;
    }



    public void alertReschduleSuccess(final Activity activity, String title, String message, final String customMessage) {
        Dialog dialog1 = null;


        if(customMessage.equals("success")) {


            IOSAlertDialog dialog =  IOSAlertDialog.newInstance(
                    requireContext(),
                    null,
                    title + "\n" + message,
                    getString(R.string.ok), // positive button text
                    null,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            rescheduleUpdate.rescheduleUpdate();
                            getActivity().getSupportFragmentManager().popBackStackImmediate();
                            activity.finish();
                            dialog.cancel();
                        }
                    },
                    null,
                    ContextCompat.getColor(requireContext(), R.color.app_color),
                    ContextCompat.getColor(requireContext(), R.color.app_color),
                    false

            );


            dialog.show(requireActivity().getSupportFragmentManager(), "ios_dialog");;


/*
            AlertDialog dialog = new AlertDialog.Builder(requireContext())
                    .setCancelable(false)
                    .setMessage(title+"\n"+message)
                    .setPositiveButton(
                    R.string.ok,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            rescheduleUpdate.rescheduleUpdate();
                            getActivity().getSupportFragmentManager().popBackStackImmediate();
                            activity.finish();
                            dialog.cancel();
                        }
                    })
                    .show();

            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.app_color));
            dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.app_color));
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setAllCaps(false);
            dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setAllCaps(false);*/


        }else{


            IOSAlertDialog dialog =  IOSAlertDialog.newInstance(
                    getContext(),
                    null,
                    (title + "\n" + message),
                    getString(R.string.yes),
                    getString(R.string.no),
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                           /// postive click

                            if (getArguments().getString(Constants.BOOKING_TYPE).equalsIgnoreCase("2")) {
                                booking_type = 3;
                                calendarTime.set(Calendar.SECOND, 0);
                                calendarTime.set(Calendar.MILLISECOND, 0);
                                SearchMaidBulkModel bulkModel = new SearchMaidBulkModel();
                                bulkModel.uniquieAppKey = Constants.UNIQUE_APP_KEY;
                                bulkModel.workDate = calendarDate.getTimeInMillis();

                                if (booking_type == 1) {
                                    bulkModel.endDate = calendarDate.getTimeInMillis();
                                } else if (booking_type == 2) {
                                    bulkModel.endDate = calendartoDate.getTimeInMillis();
                                }
                                bulkModel.hour = GeneralFunction.getTimeInTimeZone(new Date(searchMaidModel.startTime), TimeZone.getTimeZone(bookingDataModel.timeZone)).getTime();

                                bulkModel.startTime = calendarTime.getTimeInMillis();
                                bulkModel.duration = bookingDataModel.duration;
                                bulkModel.maidCount = Integer.parseInt(bookingDataModel.maidCount);
                                bulkModel.lng = searchMaidModel.lng;
                                bulkModel.lat = searchMaidModel.lat;
                                bulkModel.locationName = bookingDataModel.locationName;
                                bulkModel.selectedDate = binding.tvSelecteddate.getText().toString();
                                bulkModel.selectedTime = binding.tvSelectedtime.getText().toString();
                                bulkModel.selectedAgency = selectedAgencyList;
                                Intent intent = new Intent(getActivity(), MaidBookActivity.class);
                                intent.putExtra(Constants.SEARCH_MAID_DATA, bulkModel);
                                intent.putExtra(Constants.BOOKING_DATA, new Gson().toJson(bookingDataModel) + "");
                                intent.putExtra(Constants.BOOKING_TYPE, String.valueOf(booking_type));
                                intent.putExtra(Constants.reschuleStatus, reschuleStatus + "");
                                intent.putExtra(Constants.SERVICE_ID, bookingDataModel._id + "");
                                Log.d("Booking details bulk", bulkModel.toString());
                                startActivity(intent);

                            }
                            else {
                                Log.e("Booking details", searchMaidModel.toString());
                                //  supportFragmentManager.popBackStack();
                                //  activity.finish();
                                SearchMaidModel searchMaidModel1 = new SearchMaidModel();
                                searchMaidModel1.uniquieAppKey = Constants.UNIQUE_APP_KEY;
                                searchMaidModel1.workDate = calendarDate.getTimeInMillis();

                                if (booking_type == 1) {

                                    searchMaidModel1.startTime = calendarTime.getTimeInMillis();
                                    searchMaidModel1.endDate = calendarDate.getTimeInMillis();
                                } else if (booking_type == 2) {
                                    String mytime = GeneralFunction.getFormatFromDate(selectedDatesList.get(0), "dd MMMM") + " " + binding.tvSelectedtime.getText().toString();
                                    Log.e("mytime", mytime);

                                    SimpleDateFormat dateFormat = new SimpleDateFormat(
                                            "dd MMMM hh:mm a", Locale.ENGLISH);

                                    Date myDate = null;
                                    try {
                                        myDate = dateFormat.parse(mytime);

                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    Log.e("f date", myDate.toString());
                                    searchMaidModel1.startTime = myDate.getTime();
                                    searchMaidModel1.endDate = calendartoDate.getTimeInMillis();
                                }

                                if (reschuleStatus.equalsIgnoreCase("yes")) {
                                    searchMaidModel1.duration = searchMaidModel.duration;
                                } else {
                                    searchMaidModel1.duration = duration;
                                }

                                if (reschuleStatus.equalsIgnoreCase("yes")) {
                                    searchMaidModel1.lat = Double.parseDouble(getArguments().getString("lat"));
                                    searchMaidModel1.lng = Double.parseDouble(getArguments().getString("lng"));
                                } else {
                                    searchMaidModel1.lng = searchMaidModel.lng;
                                    searchMaidModel1.lat = searchMaidModel.lat;
                                }
                                searchMaidModel1.locationName = binding.tvLocation.getText().toString();
                                searchMaidModel1.selectedDate = binding.tvSelecteddate.getText().toString();
                                searchMaidModel1.selectedToDate = binding.tvtoSelecteddate.getText().toString();
                                searchMaidModel1.hour = GeneralFunction.getTimeInTimeZone(new Date(searchMaidModel.startTime), tempTimeZone).getTime();

                                searchMaidModel1.selectedTime = binding.tvSelectedtime.getText().toString();
                                searchMaidModel1.selectedAgency = selectedAgencyList;
                                Intent intent = new Intent(getActivity(), MaidBookActivity.class);
                                intent.putExtra(Constants.SEARCH_MAID_DATA, searchMaidModel1);
                                intent.putExtra(Constants.BOOKING_TYPE, booking_type + "");
                                intent.putExtra(Constants.BOOKING_DATA, new Gson().toJson(bookingDataModel) + "");
                                intent.putExtra(Constants.reschuleStatus, reschuleStatus + "");
                                intent.putExtra(Constants.SERVICE_ID, bookingDataModel._id + "");
                                Log.e("Booking a", searchMaidModel1.toString());

                                startActivity(intent);
                            }
                            dialog.cancel();
                        }
                    },
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //  negative click
                            dialog.cancel();
                        }
                    },
                    ContextCompat.getColor(requireContext(), R.color.app_color),
                    ContextCompat.getColor(requireContext(), R.color.app_color),
                    false


            );
            dialog.show(requireActivity().getSupportFragmentManager(), "ios_dialog");;

         /*   AlertDialog dialog = new AlertDialog.Builder(requireContext())
                    .setCancelable(false)
                    .setMessage(title+"\n"+message)
                    .setPositiveButton(
                            R.string.yes,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    if (getArguments().getString(Constants.BOOKING_TYPE).equalsIgnoreCase("2")) {
                                        booking_type = 3;
                                        calendarTime.set(Calendar.SECOND, 0);
                                        calendarTime.set(Calendar.MILLISECOND, 0);
                                        SearchMaidBulkModel bulkModel = new SearchMaidBulkModel();
                                        bulkModel.uniquieAppKey = Constants.UNIQUE_APP_KEY;
                                        bulkModel.workDate = calendarDate.getTimeInMillis();

                                        if (booking_type == 1) {
                                            bulkModel.endDate = calendarDate.getTimeInMillis();
                                        } else if (booking_type == 2) {
                                            bulkModel.endDate = calendartoDate.getTimeInMillis();
                                        }
                                        bulkModel.hour = GeneralFunction.getTimeInTimeZone(new Date(searchMaidModel.startTime), TimeZone.getTimeZone(bookingDataModel.timeZone)).getTime();

                                        bulkModel.startTime = calendarTime.getTimeInMillis();
                                        bulkModel.duration = bookingDataModel.duration;
                                        bulkModel.maidCount = Integer.parseInt(bookingDataModel.maidCount);
                                        bulkModel.lng = searchMaidModel.lng;
                                        bulkModel.lat = searchMaidModel.lat;
                                        bulkModel.locationName = bookingDataModel.locationName;
                                        bulkModel.selectedDate = binding.tvSelecteddate.getText().toString();
                                        bulkModel.selectedTime = binding.tvSelectedtime.getText().toString();
                                        bulkModel.selectedAgency = selectedAgencyList;
                                        Intent intent = new Intent(getActivity(), MaidBookActivity.class);
                                        intent.putExtra(Constants.SEARCH_MAID_DATA, bulkModel);
                                        intent.putExtra(Constants.BOOKING_DATA, new Gson().toJson(bookingDataModel) + "");
                                        intent.putExtra(Constants.BOOKING_TYPE, String.valueOf(booking_type));
                                        intent.putExtra(Constants.reschuleStatus, reschuleStatus + "");
                                        intent.putExtra(Constants.SERVICE_ID, bookingDataModel._id + "");
                                        Log.d("Booking details bulk", bulkModel.toString());
                                        startActivity(intent);

                                    }
                                    else {
                                        Log.e("Booking details", searchMaidModel.toString());
                                        //  supportFragmentManager.popBackStack();
                                        //  activity.finish();
                                        SearchMaidModel searchMaidModel1 = new SearchMaidModel();
                                        searchMaidModel1.uniquieAppKey = Constants.UNIQUE_APP_KEY;
                                        searchMaidModel1.workDate = calendarDate.getTimeInMillis();

                                        if (booking_type == 1) {

                                            searchMaidModel1.startTime = calendarTime.getTimeInMillis();
                                            searchMaidModel1.endDate = calendarDate.getTimeInMillis();
                                        } else if (booking_type == 2) {
                                            String mytime = GeneralFunction.getFormatFromDate(selectedDatesList.get(0), "dd MMMM") + " " + binding.tvSelectedtime.getText().toString();
                                            Log.e("mytime", mytime);

                                            SimpleDateFormat dateFormat = new SimpleDateFormat(
                                                    "dd MMMM hh:mm a", Locale.ENGLISH);

                                            Date myDate = null;
                                            try {
                                                myDate = dateFormat.parse(mytime);

                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                            Log.e("f date", myDate.toString());
                                            searchMaidModel1.startTime = myDate.getTime();
                                            searchMaidModel1.endDate = calendartoDate.getTimeInMillis();
                                        }

                                        if (reschuleStatus.equalsIgnoreCase("yes")) {
                                            searchMaidModel1.duration = searchMaidModel.duration;
                                        } else {
                                            searchMaidModel1.duration = duration;
                                        }

                                        if (reschuleStatus.equalsIgnoreCase("yes")) {
                                            searchMaidModel1.lat = Double.parseDouble(getArguments().getString("lat"));
                                            searchMaidModel1.lng = Double.parseDouble(getArguments().getString("lng"));
                                        } else {
                                            searchMaidModel1.lng = searchMaidModel.lng;
                                            searchMaidModel1.lat = searchMaidModel.lat;
                                        }
                                        searchMaidModel1.locationName = binding.tvLocation.getText().toString();
                                        searchMaidModel1.selectedDate = binding.tvSelecteddate.getText().toString();
                                        searchMaidModel1.selectedToDate = binding.tvtoSelecteddate.getText().toString();
                                        searchMaidModel1.hour = GeneralFunction.getTimeInTimeZone(new Date(searchMaidModel.startTime), tempTimeZone).getTime();

                                        searchMaidModel1.selectedTime = binding.tvSelectedtime.getText().toString();
                                        searchMaidModel1.selectedAgency = selectedAgencyList;
                                        Intent intent = new Intent(getActivity(), MaidBookActivity.class);
                                        intent.putExtra(Constants.SEARCH_MAID_DATA, searchMaidModel1);
                                        intent.putExtra(Constants.BOOKING_TYPE, booking_type + "");
                                        intent.putExtra(Constants.BOOKING_DATA, new Gson().toJson(bookingDataModel) + "");
                                        intent.putExtra(Constants.reschuleStatus, reschuleStatus + "");
                                        intent.putExtra(Constants.SERVICE_ID, bookingDataModel._id + "");
                                        Log.e("Booking a", searchMaidModel1.toString());

                                        startActivity(intent);
                                    }
                                    dialog.cancel();
                                }
                            })
                    .setNegativeButton(
                            R.string.no,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            }).show();

            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.app_color));
            dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.appColor));
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setAllCaps(false);
            dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setAllCaps(false);*/

        }

    }

    @Override
    public void notiResopnse(Integer noti_count) {

        try {
            if (noti_count > 0) {
                Main2Activity.redCircle.setVisibility(View.VISIBLE);
                noticount = noti_count;
                countTextView.setText(noti_count.toString());
                ShortcutBadger.applyCount(getActivity(), noti_count);

        /*    if (noti_count > 10) {
                countTextView.setText(noti_count.toString() + "+");
            } else {
                countTextView.setText(noti_count.toString());
            }*/
            } else {
                Main2Activity.redCircle.setVisibility(View.GONE);
                countTextView.setText("");
            }
        }catch (Exception e){
            Log.e(TAG, "notiResopnse: excep  "+e.getMessage());
        }

    }

    @Override
    public void logoutSuccess() {
        String language = Prefs.with(getActivity()).getString(Constants.LANGUAGE_CODE, "en");
        Prefs.with(getActivity()).removeAll();
        Prefs.with(getActivity()).save(Constants.LANGUAGE_CODE, language);
        getActivity().finishAffinity();
        startActivity(new Intent(getActivity(), AuthenticateActivity.class));
    }

    @Override
    public void bookingTimeZoneReceived(@Nullable TimeZone timeZone) {
        if (timeZone == null) {
            Toast.makeText(getActivity(), R.string.unable_to_locate_time_zone, Toast.LENGTH_SHORT).show();
        } else {

            tempTimeZone = timeZone;

            callBookApi(timeZone, SERVICE_ID);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.detachView();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(deleteAddressReceiver);
    }

    public void setByDefaultLocation(Location location) {
        if (getActivity() != null) {
            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
            if (Geocoder.isPresent()) {
                List<Address> addressList = null;
                try {
                    addressList = geocoder.getFromLocation(
                            location.getLatitude(), location.getLongitude(), 1);
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
                if (addressList != null && addressList.size() > 0) {
                    locationName = addressList.get(0).getLocality();
                    String country = addressList.get(0).getCountryName();
                    Prefs.with(getActivity()).save(Constants.COUNTRY_NAME, country);
                    Prefs.with(getActivity()).save(Constants.LOCATION, location);
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case BOOK_AGAIN_CODE:

                    FullAddress fullAddress = Prefs.with(getContext()).getObject(Constants.MAP_FULL_ADDRESS, FullAddress.class);
                    String other = "";
                    if (fullAddress.moreDetailedaddress != null && !fullAddress.moreDetailedaddress.isEmpty()) {
                        other = getActivity().getString(R.string.label_additionaldetails) + " " + fullAddress.moreDetailedaddress + "\n";
                    } else {
                        other = "";
                    }
                    if (reschuleStatus.equalsIgnoreCase("yes")) {

                        if (!checkValidation())
                            return;
                        searchMaidModel.lat = Double.parseDouble(getArguments().getString("lat"));
                        searchMaidModel.lng = Double.parseDouble(getArguments().getString("lng"));
                    } else {
                        searchMaidModel.lat = fullAddress.lat;
                        searchMaidModel.lng = fullAddress.lng;
                    }
                    if (data != null && data.hasExtra(Constants.SIGN_UP) && data.getBooleanExtra(Constants.SIGN_UP, false)) {

                        if (fullAddress.moreDetailedaddress != null && !fullAddress.moreDetailedaddress.isEmpty()) {
                            other = fullAddress.moreDetailedaddress + " , ";
                        } else {
                            other = "";
                        }
                        String address = fullAddress.streetName + " , " + fullAddress.buildingName + " , " + fullAddress.villaName + " , "
                                + other + fullAddress.city/*+" , "+fullAddress.country*/;
                        binding.tvLocation.setBackgroundResource(R.drawable.border_gray_rect);
                        binding.tvLocation.setText(address);
                     //Book Again check

                        if (isBookAgain) {
                            if (reschuleStatus.equalsIgnoreCase("yes")) {
                                //  FullAddress fullAddress = Prefs.with(getContext()).getObject(Constants.MAP_FULL_ADDRESS, FullAddress.class);
                                searchMaidModel.lat = Double.parseDouble(getArguments().getString("lat"));
                                searchMaidModel.lng = Double.parseDouble(getArguments().getString("lng"));
                            }

                            final String googleApiKey = getString(R.string.google_api_key_mak);
                            presenter.getTimeZoneFromLatLong(searchMaidModel.lat, searchMaidModel.lng, googleApiKey);
                        } else {

                            if (booking_type == 3) {

                                calendarTime.set(Calendar.SECOND, 0);
                                calendarTime.set(Calendar.MILLISECOND, 0);
                                SearchMaidBulkModel bulkModel = new SearchMaidBulkModel();
                                bulkModel.uniquieAppKey = Constants.UNIQUE_APP_KEY;
                                bulkModel.workDate = calendarDate.getTimeInMillis();

                                if (booking_type == 1) {
                                    bulkModel.endDate = calendarDate.getTimeInMillis();
                                } else if (booking_type == 2) {
                                    bulkModel.endDate = calendartoDate.getTimeInMillis();
                                }

                                bulkModel.startTime = calendarTime.getTimeInMillis();
                                bulkModel.duration = duration;
                                bulkModel.maidCount = maid_count;
                                bulkModel.lng = searchMaidModel.lng;
                                bulkModel.lat = searchMaidModel.lat;
                                bulkModel.locationName = binding.tvLocation.getText().toString();
                                bulkModel.selectedDate = binding.tvSelecteddate.getText().toString();
                                bulkModel.selectedTime = binding.tvSelectedtime.getText().toString();
                                bulkModel.selectedAgency = selectedAgencyList;
                                Intent intent = new Intent(getActivity(), MaidBookActivity.class);
                                intent.putExtra(Constants.SEARCH_MAID_DATA, bulkModel);
                                intent.putExtra(Constants.BOOKING_TYPE, String.valueOf(booking_type));
                                intent.putExtra(Constants.reschuleStatus, reschuleStatus + "");
                                intent.putExtra(Constants.SERVICE_ID, SERVICE_ID + "");
                                Log.d("Booking details bulk", bulkModel.toString());
                                startActivity(intent);

                            } else {
                                //  calendarTime.set(Calendar.SECOND, 0);
                                //  calendarTime.set(Calendar.MILLISECOND, 0);
                                SearchMaidModel searchMaidModel1 = new SearchMaidModel();
                                searchMaidModel1.uniquieAppKey = Constants.UNIQUE_APP_KEY;
                                searchMaidModel1.workDate = calendarDate.getTimeInMillis();

                                if (booking_type == 1) {

                                    searchMaidModel1.startTime = calendarTime.getTimeInMillis();
                                    searchMaidModel1.endDate = calendarDate.getTimeInMillis();
                                } else if (booking_type == 2) {
                                    String mytime = GeneralFunction.getFormatFromDate(selectedDatesList.get(0), "dd MMMM") + " " + binding.tvSelectedtime.getText().toString();
                                    Log.e("mytime", mytime);

                                    SimpleDateFormat dateFormat = new SimpleDateFormat(
                                            "dd MMMM hh:mm a", Locale.ENGLISH);

                                    Date myDate = null;
                                    try {
                                        myDate = dateFormat.parse(mytime);

                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    Log.e("f date", myDate.toString());
                                    searchMaidModel1.startTime = myDate.getTime();
                                    searchMaidModel1.endDate = calendartoDate.getTimeInMillis();
                                }


                                searchMaidModel1.duration = duration;
                                if (reschuleStatus.equalsIgnoreCase("yes")) {

                                    if (!checkValidation())
                                        return;
                                    searchMaidModel1.lat = Double.parseDouble(getArguments().getString("lat"));
                                    searchMaidModel1.lng = Double.parseDouble(getArguments().getString("lng"));


                                } else {
                                    searchMaidModel1.lng = searchMaidModel.lng;
                                    searchMaidModel1.lat = searchMaidModel.lat;
                                }

                                searchMaidModel1.locationName = binding.tvLocation.getText().toString();
                                searchMaidModel1.selectedDate = binding.tvSelecteddate.getText().toString();
                                searchMaidModel1.selectedToDate = binding.tvtoSelecteddate.getText().toString();
                                searchMaidModel1.selectedTime = binding.tvSelectedtime.getText().toString();
                                searchMaidModel1.selectedAgency = selectedAgencyList;
                                Intent intent = new Intent(getActivity(), MaidBookActivity.class);
                                intent.putExtra(Constants.SEARCH_MAID_DATA, searchMaidModel1);
                                intent.putExtra(Constants.BOOKING_TYPE, booking_type + "");
                                intent.putExtra(Constants.reschuleStatus, reschuleStatus + "");
                                intent.putExtra(Constants.SERVICE_ID, SERVICE_ID + "");
                                Log.e("Booking details", searchMaidModel1.toString());
                                startActivity(intent);


                            }

                        }

                    } else {
                        Log.e("6666", "666666");
                        String address = getAddressValue(fullAddress);
                        binding.tvAddressValue.setText(address);
                        searchMaidModel.locationName = address;
                    }

                    break;

                   /*case REQ_CODE:
                    if (data.getBooleanExtra("All", false)) {
                        selectedAgencyList.clear();
                        binding.tvArrow.setText(getString(R.string.all));
                    } else {
                        selectedAgencyList = data.getStringArrayListExtra("selectedList");
                        binding.tvArrow.setText(selectedAgencyList.size() + " " + "Selected");
                    }

                    break;*/

            }
        }

    }

    private String getAddressValue(FullAddress address) {
        String other;
        if (address.moreDetailedaddress != null && !address.moreDetailedaddress.isEmpty()) {
            other = getActivity().getString(R.string.label_additionaldetails) + " " + address.moreDetailedaddress + "\n";
        } else {
            other = "";
        }

        return "\n" + getActivity().getString(R.string.label_villa) + " " + address.streetName + "\n"
                + getActivity().getString(R.string.label_block) + " " + address.buildingName + "\n"
                + getActivity().getString(R.string.label_road) + " " + address.villaName + "\n"
                + other + address.city + "," + address.country;
    }


    public boolean checkTimeValidation(int j) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd:hh:mm a", Locale.ENGLISH);
        SimpleDateFormat sdff = new SimpleDateFormat("yyyy:MM:dd", Locale.ENGLISH);
        String currentDateandTime = sdff.format(new Date()) + ":" + binding.tvSelectedtime.getText().toString();
        Log.e("fdfdf", binding.tvSelectedtime.getText().toString());
        String currentDateandTime2 = sdff.format(new Date()) + ":" + "11:00 PM";
        Log.e("duration", j + "");
        Date date = null;
        Date date2 = null;
        try {
            date = sdf.parse(currentDateandTime);
            date2 = sdf.parse(currentDateandTime2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR, j);

        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(date2);
        // calendar2.add(Calendar.HOUR, j);

        System.out.println("Time here " + calendar.getTime());
        Log.e(TAG,"111"+ calendar.getTime() + "");
        Log.e(TAG,"222"+calendar2.getTime() + "");

        if (calendar.getTime().after(calendar2.getTime())) {
            return false;
        } else {
            return true;
        }
    }


    public String printDifference() {
        //milliseconds
        //  Toast.makeText(getActivity(), "enter", Toast.LENGTH_SHORT).show();

        Calendar tempCalendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd:hh:mm a", Locale.ENGLISH);
        SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);

        SimpleDateFormat sdff = new SimpleDateFormat("yyyy:MM:dd", Locale.ENGLISH);
        String currentDateandTime = sdff.format(new Date()) + ":" + binding.tvSelectedtime.getText().toString();
        String currentDateandTime2 = sdff.format(new Date()) + ":" + "9:00 AM";
        Date currentDate = null, selectedDate = null, selectedtoDate = null;
        try {
            currentDate = sdf.parse(sdf.format(tempCalendar.getTime()));
            selectedDate = sdff.parse(sdff.format(tempCalendar.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        //calendar.setTime(selectedDate);
        calendar.add(Calendar.HOUR, 0);
        Calendar tempCalendarr = Calendar.getInstance();
        Date selectedDateTime = null;
        try {
            selectedDateTime = sdf.parse(sdf.format(calendar.getTime()));

        } catch (ParseException e) {
            e.printStackTrace();
        }

        long different = selectedDateTime.getTime() - currentDate.getTime();

        System.out.println("startDate : " + selectedDateTime);
        System.out.println("endDate : " + currentDate);
        System.out.println("different : " + different);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        System.out.printf(
                "%d days, %d hours, %d minutes, %d seconds%n",
                elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds);


        return String.valueOf(elapsedHours);
    }


//    public boolean checkValidation() {
//
//        Calendar tempCalendar = Calendar.getInstance();
//        calendarDate.set(Calendar.HOUR, 0);
//        calendarDate.set(Calendar.HOUR_OF_DAY, 0);
//        calendarDate.set(Calendar.MINUTE, 0);
//        calendarDate.set(Calendar.SECOND, 0);
//        calendarDate.set(Calendar.MILLISECOND, 0);
//        calendarTime.set(calendarDate.get(Calendar.YEAR),
//                calendarDate.get(Calendar.MONTH), calendarDate.get(Calendar.DAY_OF_MONTH));
//        calendarTime.set(Calendar.SECOND, 0);
//        calendarTime.set(Calendar.MILLISECOND, 0);
//        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd yyyy", Locale.ENGLISH);
//        Date tempDate = null, selectedDate = null, selectedtoDate = null;
//        try {
//            tempDate = sdf.parse(sdf.format(tempCalendar.getTime()));
//            selectedDate = sdf.parse(sdf.format(calendarDate.getTime()));
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        if (binding.tvSelecteddate.getText().toString().equalsIgnoreCase("")||binding.tvSelecteddate.getText().toString().equalsIgnoreCase("--,-- -")) {
//            GeneralFunction.showSnackBar(getActivity(), binding.parent, getResources().getString(R.string.date_validationn));
//            return false;
//        } else if (binding.tvSelectedtime.getText().toString().equalsIgnoreCase("")||binding.tvSelectedtime.getText().toString().equalsIgnoreCase("--:--")) {
//            if (booking_type == 2) {
//                if (binding.tvtoSelecteddate.getText().toString().equalsIgnoreCase("")) {
//                    GeneralFunction.showSnackBar(getActivity(), binding.parent, getResources().getString(R.string.date_validationn));
//                    return false;
//                } else if (binding.tvSelectedtime.getText().toString().equalsIgnoreCase("")) {
//                    GeneralFunction.showSnackBar(getActivity(), binding.parent, getResources().getString(R.string.please_select_the_start_time_from_9_am_to_9_pm));
//                    return false;
//                }
//            } else {
//                GeneralFunction.showSnackBar(getActivity(), binding.parent, getResources().getString(R.string.please_select_the_start_time_from_9_am_to_9_pm));
//                return false;
//            }
//        } else {
//            if (!checkTimeValidation(duration)) {
//                GeneralFunction.showSnackBar(getActivity(), binding.parent, getResources().getString(R.string.time_end_check));
//                return false;
//            }
//        }
//        /*if (searchMaidModel.lat == 0.0 && searchMaidModel.lng == 0.0) {
//
//            binding.tvLocation.setBackgroundResource(R.drawable.red_bg);
//            GeneralFunction.showSnackBar(getActivity(), binding.parent, getString(R.string.err_select_location));
//            return false;
//        } else */
//
//
//        if (booking_type == 2) {
//
//            if (binding.tvtoSelecteddate.getText().toString().equalsIgnoreCase("")) {
//                GeneralFunction.showSnackBar(getActivity(), binding.parent, getResources().getString(R.string.date_validationn));
//                return false;
//            }
//            Log.e("1111", "" + tempDate);
//            Log.e("2222", "" + selectedDatesList.get(0));
//            if (tempDate.compareTo(selectedDatesList.get(selectedDatesList.size() - 1)) == 0) {
//                tempCalendar.add(Calendar.HOUR, 2);
//                if (calendarTime.getTime().compareTo(tempCalendar.getTime()) < 0) {
//                    GeneralFunction.showSnackBar(getActivity(), binding.parent, getString(R.string.err_future_date));
//                    return false;
//                }
//            }
//
//
//        } else {
//            if (tempDate.compareTo(selectedDate) == 0) {
//                tempCalendar.add(Calendar.HOUR, 2);
//                if (calendarTime.getTime().compareTo(tempCalendar.getTime()) < 0) {
//                    GeneralFunction.showSnackBar(getActivity(), binding.parent, getString(R.string.err_future_date));
//                    return false;
//                }
//            }
//        }
//        if (calendarTime.getTime().getHours() > 21 || calendarTime.getTime().getHours() < 8) {
//            Toast.makeText(getActivity(), R.string.please_select_the_start_time_from_9_am_to_9_pm, Toast.LENGTH_SHORT).show();
//            return false;
//        }
//
//        if (booking_type == 2) {
//
//            try {
//                //  tempDate = sdf.parse(sdf.format(tempCalendar.getTime()));
//                selectedtoDate = sdf.parse(sdf.format(calendartoDate.getTime()));
//                if (selectedDate.after(selectedtoDate)) {
//                    GeneralFunction.showSnackBar(getActivity(), binding.parent, getString(R.string.date_validation));
//                    return false;
//                }
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//
//
//        }
//
//
//        if (isBookAgain && isAddressSet) {
//            if (binding.tvAddressValue.getText().toString().trim().isEmpty() ||
//                    binding.tvAddressValue.getText().toString().trim().equals(getString(R.string.address))) {
//                GeneralFunction.showSnackBar(getActivity(), binding.parent, getString(R.string.err_address));
//                return false;
//            }
//        }
//        PojoLogin data = Prefs.with(getActivity()).getObject(Constants.DATA, PojoLogin.class);
//        Log.e("log data", "" + new Gson().toJson(data));
//    /*    if (data != null && !data.isVerified && !data.isGuestFlag) {
//            new AlertDialog.Builder(getActivity())
//                    .setMessage(R.string.please_verify_your_email_id)
//                    .setCancelable(false)
//                    .setPositiveButton(R.string.ok,
//                            new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int idd) {
//                                    if (GeneralFunction.isNetworkConnected(getActivity(), binding.tvSearchMaid)) {
//                                        presenter.apilogout();
//                                    }
//                                }
//                            }).show();
//            return false;
//        }*/
//        return true;
//    }

  public boolean checkValidation(){
      Calendar tempCalendar = Calendar.getInstance();
      calendarDate.set(Calendar.HOUR, 0);
      calendarDate.set(Calendar.HOUR_OF_DAY, 0);
      calendarDate.set(Calendar.MINUTE, 0);
      calendarDate.set(Calendar.SECOND, 0);
      calendarDate.set(Calendar.MILLISECOND, 0);
      calendarTime.set(calendarDate.get(Calendar.YEAR),
              calendarDate.get(Calendar.MONTH), calendarDate.get(Calendar.DAY_OF_MONTH));
      calendarTime.set(Calendar.SECOND, 0);
      calendarTime.set(Calendar.MILLISECOND, 0);
      SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd yyyy", Locale.ENGLISH);
      Date tempDate = null, selectedDate = null, selectedtoDate = null;
      try {
          tempDate = sdf.parse(sdf.format(tempCalendar.getTime()));
          selectedDate = sdf.parse(sdf.format(calendarDate.getTime()));
      } catch (ParseException e) {
          e.printStackTrace();
      }
      switch(booking_type){
          case 1:
              //Merge case 1 and 3
          case 3:
              if (binding.tvSelecteddate.getText().toString().equalsIgnoreCase("")||binding.tvSelecteddate.getText().toString().equalsIgnoreCase("--,-- -")) {
                  GeneralFunction.showSnackBar(getActivity(), binding.parent, getResources().getString(R.string.date_validationn));
                  return false;
              }
              else if(binding.tvSelectedtime.getText().toString().equalsIgnoreCase("")|| binding.tvSelectedtime.getText().toString().equals("--:--")){
                  GeneralFunction.showSnackBar(getActivity(), binding.parent, getResources().getString(R.string.please_select_the_start_time_from_9_am_to_9_pm));
                  return false;
              }
              else if(!checkTimeValidation(duration)){
                  GeneralFunction.showSnackBar(getActivity(), binding.parent, getResources().getString(R.string.time_end_check));
                  return false;
              }
              if (tempDate.compareTo(selectedDate) == 0) {
                  tempCalendar.add(Calendar.HOUR, 2);
                  if (calendarTime.getTime().compareTo(tempCalendar.getTime()) < 0) {
                      GeneralFunction.showSnackBar(getActivity(), binding.parent, getString(R.string.err_future_date));
                      return false;
                  }
              }
              break;
          case 2:
              if (binding.tvtoSelecteddate.getText().toString().equalsIgnoreCase("")) {
                  GeneralFunction.showSnackBar(getActivity(), binding.parent, getResources().getString(R.string.date_validationn));
                  return false;
              }
              else if (binding.tvSelectedtime.getText().toString().equalsIgnoreCase("")) {
                  GeneralFunction.showSnackBar(getActivity(), binding.parent, getResources().getString(R.string.please_select_the_start_time_from_9_am_to_9_pm));
                  return false;
              }
              else if(!checkTimeValidation(duration)){
                  GeneralFunction.showSnackBar(getActivity(), binding.parent, getResources().getString(R.string.time_end_check));
                  return false;
              }
              try {
                  //  tempDate = sdf.parse(sdf.format(tempCalendar.getTime()));
                  selectedtoDate = sdf.parse(sdf.format(calendartoDate.getTime()));
                  if (selectedDate.after(selectedtoDate)) {
                      GeneralFunction.showSnackBar(getActivity(), binding.parent, getString(R.string.date_validation));
                      return false;
                  }
              } catch (ParseException e) {
                  e.printStackTrace();
              }
              if (tempDate.compareTo(selectedDatesList.get(selectedDatesList.size() - 1)) == 0) {
                  tempCalendar.add(Calendar.HOUR, 2);
                  if (calendarTime.getTime().compareTo(tempCalendar.getTime()) < 0) {
                      GeneralFunction.showSnackBar(getActivity(), binding.parent, getString(R.string.err_future_date));
                      return false;
                  }
              }
      }//End switch

      if (isBookAgain && isAddressSet) {
          if (binding.tvAddressValue.getText().toString().trim().isEmpty() ||
                  binding.tvAddressValue.getText().toString().trim().equals(getString(R.string.address))) {
              GeneralFunction.showSnackBar(getActivity(), binding.parent, getString(R.string.err_address));
              return false;
          }
      }
      return true;
  }

    public int getCountOfDays(String createdDateString, String expireDateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM dd", Locale.ENGLISH);
        Date createdConvertedDate = null, expireCovertedDate = null, todayWithZeroTime = null;
        try {
            createdConvertedDate = dateFormat.parse(createdDateString);
            expireCovertedDate = dateFormat.parse(expireDateString);
            Date today = new Date();
            todayWithZeroTime = dateFormat.parse(dateFormat.format(today));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int cYear, cMonth , cDay ;
        if (createdConvertedDate.after(todayWithZeroTime)) {
            Calendar cCal = Calendar.getInstance();
            cCal.setTime(createdConvertedDate);
            cYear = cCal.get(Calendar.YEAR);
            cMonth = cCal.get(Calendar.MONTH);
            cDay = cCal.get(Calendar.DAY_OF_MONTH);
        } else {
            Calendar cCal = Calendar.getInstance();
            cCal.setTime(todayWithZeroTime);
            cYear = cCal.get(Calendar.YEAR);
            cMonth = cCal.get(Calendar.MONTH);
            cDay = cCal.get(Calendar.DAY_OF_MONTH);
        }
    /*Calendar todayCal = Calendar.getInstance();
    int todayYear = todayCal.get(Calendar.YEAR);
    int today = todayCal.get(Calendar.MONTH);
    int todayDay = todayCal.get(Calendar.DAY_OF_MONTH);
    */
        Calendar eCal = Calendar.getInstance();
        eCal.setTime(expireCovertedDate);
        int eYear = eCal.get(Calendar.YEAR);
        int eMonth = eCal.get(Calendar.MONTH);
        int eDay = eCal.get(Calendar.DAY_OF_MONTH);
        Calendar date1 = Calendar.getInstance();
        Calendar date2 = Calendar.getInstance();
        date1.clear();
        date1.set(cYear, cMonth, cDay);
        date2.clear();
        date2.set(eYear, eMonth, eDay);
        date2.add(Calendar.DATE, 1);
        long diff = date2.getTimeInMillis() - date1.getTimeInMillis();
        float dayCount = (float) diff / (24 * 60 * 60 * 1000);
        return ((int) dayCount);
    }

    private void callBookApi(TimeZone bookingTimeZone, String SERVICE_ID) {
        BookServiceModel.ServiceData serviceData = new BookServiceModel.ServiceData();
        serviceData.startTime = String.valueOf(calendarTime.getTimeInMillis());
        serviceData.workDate = String.valueOf(calendarDate.getTimeInMillis());
        serviceData.endDate = String.valueOf(calendartoDate.getTimeInMillis());
        // serviceData.workDate = "Wednesday, October 24";
        if (reschuleStatus.equalsIgnoreCase("yes")) {
            serviceData.duration = bookingDataModel.duration;
            //   searchMaidModel.duration = duration;
        } else {
            serviceData.duration = duration;
            searchMaidModel.duration = duration;
        }
        BookServiceModel bookServiceModel = new BookServiceModel();
        bookServiceModel.uniquieAppKey = Constants.UNIQUE_APP_KEY;
        if (reschuleStatus.equalsIgnoreCase("yes")) {
            if (!checkValidation())
                return;
            searchMaidModel.lat = Double.parseDouble(getArguments().getString("lat"));
            searchMaidModel.lng = Double.parseDouble(getArguments().getString("lng"));
        } else {
            searchMaidModel.lng = searchMaidModel.lng;
            searchMaidModel.lat = searchMaidModel.lat;
        }
//        bookServiceModel.bookingId = ;
        bookServiceModel.lng = searchMaidModel.lng;
        bookServiceModel.lat = searchMaidModel.lat;
        bookServiceModel.locationName = binding.tvLocation.getText().toString();
        if (searchMaidModel.maidId != null) {
            bookServiceModel.maidId = searchMaidModel.maidId;
        }
        // Send start time in the time zone of the booking location
        // bookServiceModel.hour = String.valueOf(GeneralFunction.getTimeInTimeZone(searchMaidModel.startTime, timeZone));
        //     bookServiceModel.hour = String.valueOf(GeneralFunction.getTimeInTimeZone(new Date(searchMaidModel.startTime), bookingTimeZone).getTime());
        bookServiceModel.timeZone = bookingTimeZone.getID();
        bookServiceModel.deviceTimeZone = TimeZone.getDefault().getID();
        //  bookServiceModel.serviceData.add(serviceData);
        if (searchMaidModel.additionAddress != null && !searchMaidModel.additionAddress.isEmpty()) {
            bookServiceModel.moreDetailedaddress = searchMaidModel.additionAddress;
        }
        if (Prefs.with(getActivity()).getString(Constants.COUNTRY_NAME, "").contains("United Arab Emirates")) {
            bookServiceModel.currency = "AED";
        } else {
            bookServiceModel.currency = "BHD";
        }
        searchMaidModel.selectedTime = binding.tvSelectedtime.getText().toString();
        if (booking_type == 2) {
            String mytime = GeneralFunction.getFormatFromDate(HomeFragment.selectedDatesList.get(HomeFragment.selectedDatesList.size() - 1), "dd MMMM yy") + " " + binding.tvSelectedtime.getText().toString();
            Log.e("mytimeff", mytime);

            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    "dd MMMM yy hh:mm a", Locale.ENGLISH);

            Date myDate = null;
            try {
                myDate = dateFormat.parse(mytime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //   Log.e("f dateff",myDate.toString());
            Collections.reverse(selectedDatesList);
            for (int i = 0; i < selectedDatesList.size(); i++) {
                //   Timestamp tsss = new Timestamp(getDatesBetweenUsingJava7(date, datee).get(i).getTime());
                serviceData = new BookServiceModel.ServiceData();
                serviceData.workDate = String.valueOf(selectedDatesList.get(i).getTime());
                if (reschuleStatus.equalsIgnoreCase("yes")) {
                    serviceData.duration = bookingDataModel.duration;
                    //   searchMaidModel.duration = duration;
                } else {
                    serviceData.duration = duration;

                }

                String mytimee = GeneralFunction.getFormatFromDate(HomeFragment.selectedDatesList.get(i), "dd MMMM yy") + " " + binding.tvSelectedtime.getText().toString();

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
            bookServiceModel.hour = String.valueOf(GeneralFunction.getTimeInTimeZone(myDate, bookingTimeZone).getTime());

        } else if (booking_type == 1) {

            searchMaidModel.workDate = calendarDate.getTimeInMillis();

            if (booking_type == 1) {
                searchMaidModel.endDate = calendarDate.getTimeInMillis();
            } else if (booking_type == 2) {
                searchMaidModel.endDate = calendartoDate.getTimeInMillis();
            }

            searchMaidModel.startTime = calendarTime.getTimeInMillis();
            //   cdsearchMaidModel.duration = duration;
            searchMaidModel.selectedDate = binding.tvSelecteddate.getText().toString();
            searchMaidModel.selectedToDate = binding.tvtoSelecteddate.getText().toString();
            bookServiceModel.hour = String.valueOf(GeneralFunction.getTimeInTimeZone(new Date(searchMaidModel.startTime), bookingTimeZone).getTime());
            serviceData = new BookServiceModel.ServiceData();
            serviceData.startTime = String.valueOf(calendarTime.getTimeInMillis());
            serviceData.workDate = String.valueOf(calendarDate.getTimeInMillis());

            if (reschuleStatus.equalsIgnoreCase("yes")) {
                serviceData.duration = bookingDataModel.duration;
                //   searchMaidModel.duration = duration;
            } else {
                serviceData.duration = duration;

            }
            serviceData.hour = String.valueOf(GeneralFunction.getTimeInTimeZone(new Date(searchMaidModel.startTime), bookingTimeZone).getTime());
            bookServiceModel.serviceData.add(serviceData);
        }

        FullAddress fullAddress = Prefs.with(getActivity()).getObject(Constants.MAP_FULL_ADDRESS, FullAddress.class);
        Log.e(TAG, " ------ setting _id = null");
        fullAddress.id = null;
      /*  if (reschuleStatus.equalsIgnoreCase("yes")) {

            Log.e(TAG, " ------ setting _id = null");
            fullAddress.id = null;
        } else {
//            fullAddress.mapLatLng = null;
//            fullAddress.address = null;
            fullAddress.id = null;
//            fullAddress.lng = null;
//            fullAddress.lat = null;
//            if (fullAddress.moreDetailedaddress != null && fullAddress.moreDetailedaddress.isEmpty()) {
//                fullAddress.moreDetailedaddress = null;
//            }
        }*/
        bookServiceModel.address = fullAddress;
     /*   if (!servicesID.isEmpty()){
            bookServiceModel.services=servicesID;
        }*/
        if (bookServiceModel.hour!=null){
            searchMaidModel.hour = Long.parseLong(bookServiceModel.hour);
        }

        Log.e("Booking details", new Gson().toJson(bookServiceModel));

        if (reschuleStatus.equalsIgnoreCase("yes")) {

            HashMap<String, String> hashMap = new HashMap<>();

            hashMap.put("serviceId", String.valueOf(bookingDataModel._id));
            hashMap.put("bookingId", String.valueOf(bookingDataModel.bookingId));
            hashMap.put("uniquieAppKey", bookingDataModel.uniquieAppKey);
            hashMap.put("workDate", String.valueOf(searchMaidModel.workDate));
            hashMap.put("startTime", String.valueOf(searchMaidModel.startTime));
            hashMap.put("hour", String.valueOf(searchMaidModel.hour));
            hashMap.put("duration", String.valueOf(bookingDataModel.duration));
            hashMap.put("timeZone", String.valueOf(tempTimeZone.getID()));
            hashMap.put("maidId", String.valueOf(bookingDataModel.maidId.get_id()));
            //hit api if reschuleStatus
            presenter.apiCheckMaidAvailable(bookingDataModel, bookServiceModel, hashMap, Prefs.with(getActivity()).getString(Constants.ACCESS_TOKEN, ""), reschuleStatus, SERVICE_ID);

        } else {
            // hit api booking again and favorite
            presenter.apiBookService(bookingDataModel, bookServiceModel, Prefs.with(getActivity()).getString(Constants.ACCESS_TOKEN, ""), reschuleStatus, SERVICE_ID);
        }
    }


    @Override
    public void setSelectedDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public void onLocationUpdated(LatLng latLongCurrentLocation) {
        super.onLocationUpdated(latLongCurrentLocation);
        Location location = new Location("");
        location.setLongitude(latLongCurrentLocation.longitude);
        location.setLatitude(latLongCurrentLocation.latitude);
        setByDefaultLocation(location);
        Prefs.with(getActivity()).save(Constants.LOCATION, location);
        stopLocationUpdates();
    }

    @Override
    public void onLocationUpdateFailure(LatLng latLongCurrentLocation) {
        super.onLocationUpdateFailure(latLongCurrentLocation);
    }


    @Override
    public void setSelectedMaids(int duration) {
        this.maid_count = duration;
    }

    @Override
    public void onFinishDialog(ArrayList<Date> selectedDatesList) {
        Log.e("on finish", selectedDatesList + "");
        Collections.sort(selectedDatesList);
        Collections.reverse(selectedDatesList);
        String temp_dates = "";
        for (int i = 0; i < selectedDatesList.size(); i++) {
            //   temp_dates = GeneralFunction.getFormatFromDate(selectedDatesList.get(i), "EEEE, MMMM dd") + ","+temp_dates;
            temp_dates = GeneralFunction.getFormatFromDate(selectedDatesList.get(i), "dd MMMM") + "," + temp_dates;
        }

        Log.e("temp_dates", "" + temp_dates);
        Log.e("temp_dates", "" + temp_dates.substring(0, temp_dates.length() - 2));

      /*  binding.tvtoSelecteddate.setText(GeneralFunction.getFormatFromDate(calendartoDate.getTime(), "EEEE, MMMM dd"));
        binding.tvtoSelecteddate.setText();*/
        binding.tvtoSelecteddate.setText(temp_dates.substring(0, temp_dates.length() - 2) + "");


    }



}
