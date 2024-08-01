package com.maktoday.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.joooonho.SelectableRoundedImageView;
import com.maktoday.R;
import com.maktoday.interfaces.CancelBookingInterface;
import com.maktoday.interfaces.OpenMaid;
import com.maktoday.model.AgencyId;
import com.maktoday.model.FullAddress;
import com.maktoday.model.MaidData;
import com.maktoday.model.PojoMyBooking;
import com.maktoday.model.SearchMaidModel;
import com.maktoday.utils.Constants;
import com.maktoday.utils.Log;
import com.maktoday.utils.Prefs;
import com.maktoday.utils.dialog.IOSAlertDialog;
import com.maktoday.views.bookagain.BookAgainActivity;
import com.maktoday.views.chat.ChatActivity;
import com.maktoday.views.home.HomeFragment;
import com.maktoday.views.main.Main2Activity;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by cbl81 on 28/10/17.
 */

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.MyViewHolder> {

    private static final String TAG = "BookingAdapter";
    private Activity context;
    private String pageType;
    private List<PojoMyBooking.Datum> bookingList = new ArrayList<>();
    private Calendar calendar;
    private Calendar calendarTime;
    private SimpleDateFormat simpleDateFormat;
    private SimpleDateFormat simpleDateFormatTime;
    private CancelBookingInterface cancelBooking;
    private OpenMaid openMaid;
    private SelectableRoundedImageView ivMaid;

    /*maid profile open in single tap */  private long mLastClickTime = 0;

    public BookingAdapter(Activity context, String pageType, List<PojoMyBooking.Datum> bookingList, CancelBookingInterface bookingInterface, OpenMaid openMaid) {
        this.context = context;
        this.pageType = pageType;
        this.bookingList = bookingList;
        calendar = Calendar.getInstance(Locale.ENGLISH);
        calendarTime = Calendar.getInstance(Locale.ENGLISH);
        simpleDateFormat = new SimpleDateFormat("EEE, MMM dd", Locale.ENGLISH);
        simpleDateFormatTime = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
        cancelBooking = bookingInterface;
        this.openMaid = openMaid;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = context.getLayoutInflater();
        View view = inflater.inflate(R.layout.item_rv_booking, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.tvReschdule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // int temp = printDifference(holder.tvDateValue.getText().toString());
                try {//------- Exception handle date dialog Reschdule
                    if (printDifference(holder.tvDateValue.getText().toString()) < 24) {

                        IOSAlertDialog dialog =  IOSAlertDialog.newInstance(
                                context,
                                null,
                                context.getString(R.string.rescheduleTxt),
                                context.getString(R.string.ok),
                                null,
                                null,
                                null,
                                ContextCompat.getColor(context, R.color.appColor),
                                ContextCompat.getColor(context, R.color.appColor) ,
                                false
                        );


                        dialog.show(((AppCompatActivity) context).getSupportFragmentManager(), "ios_dialog");

                        /*AlertDialog dialog = new AlertDialog.Builder(context)
                                .setCancelable(false)
                                .setMessage(context.getString(R.string.rescheduleTxt))
                                .setPositiveButton(R.string.ok,
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        })
                                .show();
                        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.appColor));
                        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setAllCaps(false);*/
                    } else {

                        IOSAlertDialog dialog =   IOSAlertDialog.newInstance(
                             context,
                             null,
                                context.getString(R.string.rescheduleTxt),
                                context.getString(R.string.yes),
                                context.getString( R.string.cancel),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                        FullAddress mapAddress = bookingList.get(position).address;
                                        Log.e(TAG, " -------- setting address for booking item" + mapAddress);
                                        Prefs.with(context).save(Constants.MAP_FULL_ADDRESS, mapAddress);
                                        MaidData maidID = bookingList.get(position).maidId;
                                        PojoMyBooking.Datum data = bookingList.get(position);
                                        AgencyId agencyId = bookingList.get(position).agencyId;
                                        SearchMaidModel searchMaidModel = new SearchMaidModel();
                                        String firstName = maidID.getFirstName();
                                        String lastName = maidID.getLastName();
                                        if (lastName != null && !lastName.isEmpty()) {
                                            searchMaidModel.maidName = String.format("%s %s", context.getString(R.string.label_maidNAme), firstName.substring(0, 1).toUpperCase() + firstName.substring(1));
                                        }
                                        searchMaidModel.maidId = maidID.get_id();
                                        searchMaidModel.maidPrice = maidID.getActualPrice();
                                        searchMaidModel.agencyName = agencyId.agencyName;
                                        searchMaidModel.duration = data.duration;
                                        searchMaidModel.workDate = data.workDate;
                                        searchMaidModel.startTime = data.startTime;
                                        searchMaidModel.agencyType = data.agencyId.agencyType;
                                        // searchMaidModel.hour=data.hour;
                                        searchMaidModel.currency = bookingList.get(position).maidId.getCurrency();
                                                /*if (maidID.getProfilePicURL() != null && maidID.getProfilePicURL().getOriginal() != null && !maidID.getProfilePicURL().getOriginal().isEmpty()) {
                                                    searchMaidModel.profilePicURL = maidID.getProfilePicURL();
                                                } else*/ if (agencyId.profilePicURL != null && agencyId.profilePicURL.getOriginal() != null
                                                && !agencyId.profilePicURL.getOriginal().isEmpty()) {
                                            searchMaidModel.profilePicURL = agencyId.profilePicURL;
                                        }

                                        FragmentTransaction fragmentTransaction = ((Main2Activity) context).getSupportFragmentManager().beginTransaction();
                                        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                                                android.R.anim.fade_out);
                                        Log.e("location", data.bookingLocation.toString());
                                        HomeFragment homeFragment = new HomeFragment();
                                        Bundle bundle = new Bundle();

                                        bundle.putBoolean(Constants.BOOK_AGAIN, true);
                                        bundle.putString(Constants.reschuleStatus, "yes");

                                        bundle.putParcelable(Constants.SEARCH_MAID_DATA, searchMaidModel);

                                        bundle.putString(Constants.SERVICE_ID, bookingList.get(position)._id);
                                        bundle.putString("lat", data.bookingLocation.get(1).toString());
                                        bundle.putString(Constants.BOOKINGT_YPE, bookingList.get(position).bookingType);
                                        bundle.putString("lng", data.bookingLocation.get(0).toString());
                                        bundle.putString("payment_mode", bookingList.get(position).paymentMode);

                                        homeFragment.setArguments(bundle);

                                        Intent intent1 = new Intent(context, BookAgainActivity.class);
                                        intent1.putExtra(Constants.SEARCH_MAID_DATA, searchMaidModel);
                                        intent1.putExtra(Constants.BOOKING_DATA, new Gson().toJson(bookingList.get(position)));
                                        intent1.putExtra(Constants.SERVICE_ID, bookingList.get(position)._id);
                                        intent1.putExtra(Constants.reschuleStatus, "yes");
                                        intent1.putExtra(Constants.isFavorite, "");
                                        intent1.putExtra(Constants.BOOKINGT_YPE, bookingList.get(position).bookingType);
                                        intent1.putExtra("lat", data.bookingLocation.get(1).toString());
                                        intent1.putExtra("lng", data.bookingLocation.get(0).toString());
                                        intent1.putExtra("payment_mode", bookingList.get(position).paymentMode);
                                        context.startActivity(intent1);
                                    }
                                },
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                },
                                ContextCompat.getColor(context, R.color.app_color),
                                ContextCompat.getColor(context, R.color.app_color),
                                false
                        );
                        dialog.show(((AppCompatActivity)context).getSupportFragmentManager(),"IOSDialog");

                   /*

                        AlertDialog dialog = new AlertDialog.Builder(context)
                                .setCancelable(false)
                                .setMessage(context.getString(R.string.rescheduleTxt))
                                .setPositiveButton(R.string.yes,
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                                FullAddress mapAddress = bookingList.get(position).address;
                                                Log.e(TAG, " -------- setting address for booking item" + mapAddress);
                                                Prefs.with(context).save(Constants.MAP_FULL_ADDRESS, mapAddress);
                                                MaidData maidID = bookingList.get(position).maidId;
                                                PojoMyBooking.Datum data = bookingList.get(position);
                                                AgencyId agencyId = bookingList.get(position).agencyId;
                                                SearchMaidModel searchMaidModel = new SearchMaidModel();
                                                String firstName = maidID.getFirstName();
                                                String lastName = maidID.getLastName();
                                                if (lastName != null && !lastName.isEmpty()) {
                                                    searchMaidModel.maidName = String.format("%s %s", context.getString(R.string.label_maidNAme), firstName.substring(0, 1).toUpperCase() + firstName.substring(1));
                                                }
                                                searchMaidModel.maidId = maidID.get_id();
                                                searchMaidModel.maidPrice = maidID.getActualPrice();
                                                searchMaidModel.agencyName = agencyId.agencyName;
                                                searchMaidModel.duration = data.duration;
                                                searchMaidModel.workDate = data.workDate;
                                                searchMaidModel.startTime = data.startTime;
                                                searchMaidModel.agencyType = data.agencyId.agencyType;
                                                // searchMaidModel.hour=data.hour;
                                                searchMaidModel.currency = bookingList.get(position).maidId.getCurrency();
                                                *//*if (maidID.getProfilePicURL() != null && maidID.getProfilePicURL().getOriginal() != null && !maidID.getProfilePicURL().getOriginal().isEmpty()) {
                                                    searchMaidModel.profilePicURL = maidID.getProfilePicURL();
                                                } else*//* if (agencyId.profilePicURL != null && agencyId.profilePicURL.getOriginal() != null
                                                        && !agencyId.profilePicURL.getOriginal().isEmpty()) {
                                                    searchMaidModel.profilePicURL = agencyId.profilePicURL;
                                                }

                                                FragmentTransaction fragmentTransaction = ((Main2Activity) context).getSupportFragmentManager().beginTransaction();
                                                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                                                        android.R.anim.fade_out);
                                                Log.e("location", data.bookingLocation.toString());
                                                HomeFragment homeFragment = new HomeFragment();
                                                Bundle bundle = new Bundle();

                                                bundle.putBoolean(Constants.BOOK_AGAIN, true);
                                                bundle.putString(Constants.reschuleStatus, "yes");

                                                bundle.putParcelable(Constants.SEARCH_MAID_DATA, searchMaidModel);

                                                bundle.putString(Constants.SERVICE_ID, bookingList.get(position)._id);
                                                bundle.putString("lat", data.bookingLocation.get(1).toString());
                                                bundle.putString(Constants.BOOKINGT_YPE, bookingList.get(position).bookingType);
                                                bundle.putString("lng", data.bookingLocation.get(0).toString());
                                                bundle.putString("payment_mode", bookingList.get(position).paymentMode);

                                                homeFragment.setArguments(bundle);

                                                Intent intent1 = new Intent(context, BookAgainActivity.class);
                                                intent1.putExtra(Constants.SEARCH_MAID_DATA, searchMaidModel);
                                                intent1.putExtra(Constants.BOOKING_DATA, new Gson().toJson(bookingList.get(position)));
                                                intent1.putExtra(Constants.SERVICE_ID, bookingList.get(position)._id);
                                                intent1.putExtra(Constants.reschuleStatus, "yes");
                                                intent1.putExtra(Constants.isFavorite, "");
                                                intent1.putExtra(Constants.BOOKINGT_YPE, bookingList.get(position).bookingType);
                                                intent1.putExtra("lat", data.bookingLocation.get(1).toString());
                                                intent1.putExtra("lng", data.bookingLocation.get(0).toString());
                                                intent1.putExtra("payment_mode", bookingList.get(position).paymentMode);
                                                context.startActivity(intent1);
                                            }
                                        })
                                .setNegativeButton(
                                        R.string.cancel,
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        }).show();

                        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.appColor));
                        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.appColor));
                        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setAllCaps(false);
                        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setAllCaps(false);*/
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    try {//----------- IF FIREBASE RETURN EXCEPTION
                        FirebaseCrashlytics.getInstance().recordException(e);
                    } catch (Exception fe) {
                        fe.printStackTrace();
                    }
                }
            }
        });
        try {//------Handle log exception booking data

            Log.e("booking data", new Gson().toJson(bookingList.get(position)));

        } catch (Exception e) {
            e.printStackTrace();
            try {//----------- IF FIREBASE RETURN EXCEPTION
                FirebaseCrashlytics.getInstance().recordException(e);
            } catch (Exception fe) {
                fe.printStackTrace();
            }
        }

        try {//Exception handle---------------First name Last name
            String firstName = bookingList.get(position).maidId.getFirstName();
            String lastName = bookingList.get(position).maidId.getLastName();
            if (lastName != null && !lastName.isEmpty()) {
                holder.tvMaidName.setText(String.format("%s", firstName.substring(0, 1).toUpperCase() + firstName.substring(1)) + " " +
                        lastName.substring(0, 1).toUpperCase() + lastName.substring(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {//----------- IF FIREBASE RETURN EXCEPTION
                FirebaseCrashlytics.getInstance().recordException(e);
            } catch (Exception fe) {
                fe.printStackTrace();
            }
        }


        try {//----------------Profile pic url exception ckeck
            /*if (bookingList.get(position).maidId.getProfilePicURL() != null
                    && bookingList.get(position).maidId.getProfilePicURL().getThumbnail() != null
                    && !bookingList.get(position).maidId.getProfilePicURL().getThumbnail().isEmpty()) {
                Glide.with(context)
                        .load(bookingList.get(position).maidId.getProfilePicURL().getThumbnail())
                        .circleCrop()
                        .placeholder(R.drawable.ic_user_pic)
                        .into(holder.ivMaid);
            } else */if (bookingList.get(position).agencyId.profilePicURL != null
                    && bookingList.get(position).agencyId.profilePicURL.getThumbnail() != null
                    && !bookingList.get(position).agencyId.profilePicURL.getThumbnail().isEmpty()
            ) {
                Glide.with(context)
                        .load(bookingList.get(position).agencyId.profilePicURL.getThumbnail())
                        .circleCrop()
                        .placeholder(R.drawable.ic_user_pic)
                        .into(holder.ivMaid);
            } else {
                Glide.with(context)
                        .load(R.drawable.ic_user_pic)
                        .circleCrop()
                        .placeholder(R.drawable.ic_user_pic)
                        .into(holder.ivMaid);
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {//----------- IF FIREBASE RETURN EXCEPTION
                FirebaseCrashlytics.getInstance().recordException(e);
            } catch (Exception fe) {
                fe.printStackTrace();
            }
            Log.e(TAG, "onBindViewHolder: profilepic exception  " + e.getMessage());
        }

        try {//-------------Exception handle service name
            holder.tvServiceValue.setText(bookingList.get(position).services.name);
        } catch (Exception e) {
            e.printStackTrace();
            try {//----------- IF FIREBASE RETURN EXCEPTION
                FirebaseCrashlytics.getInstance().recordException(e);
            } catch (Exception fe) {
                fe.printStackTrace();
            }
            Log.e(TAG, "onBindViewHolder: " + e.getLocalizedMessage());
        }
// ------------------------------- Exception handle for profile pic for booking type 2
        try {//------------------- Exception handle for multiple booling Profile pic
            if (bookingList.get(position).bookingType.equalsIgnoreCase("2")) {
                if (bookingList.get(position).agencyId.profilePicURL != null
                        && bookingList.get(position).agencyId.profilePicURL.getOriginal() != null
                        && !bookingList.get(position).agencyId.profilePicURL.getOriginal().isEmpty()
                ) {
                    Glide.with(context)
                            .load(bookingList.get(position).agencyId.profilePicURL.getOriginal())
                            .circleCrop()
                            .placeholder(R.drawable.ic_user_pic)
                            .into(holder.ivMaid);
                } else {
                    Glide.with(context)
                            .load(R.drawable.ic_user_pic)
                            .circleCrop()
                            .placeholder(R.drawable.ic_user_pic)
                            .into(holder.ivMaid);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {//----------- IF FIREBASE RETURN EXCEPTION
                FirebaseCrashlytics.getInstance().recordException(e);
            } catch (Exception fe) {
                fe.printStackTrace();
            }
            Log.e(TAG, "onBindViewHolder: exception  profile pic for booking type 2 " + e.getMessage());
        }

        try {//---------Exception hadle agency name LOG
            Log.e(TAG, bookingList.get(position).agencyId.agencyName + "," + bookingList.get(position).agencyId._id);
        } catch (Exception e) {
            e.printStackTrace();
            try {//----------- IF FIREBASE RETURN EXCEPTION
                FirebaseCrashlytics.getInstance().recordException(e);
            } catch (Exception fe) {
                fe.printStackTrace();
            }
        }
        try {//-------- Exception handle agenucy name
            holder.tvAgencyName.setText(String.format("%s", bookingList.get(position).agencyId.agencyName));
        } catch (Exception e) {
            e.printStackTrace();
            try {//----------- IF FIREBASE RETURN EXCEPTION
                FirebaseCrashlytics.getInstance().recordException(e);
            } catch (Exception fe) {
                fe.printStackTrace();
            }
            Log.e(TAG, "onBindViewHolder: tvAgencyName " + e.getMessage());
        }


        try {//-------------- Exception handle booking id
            holder.tvBookIdValue.setText(String.format("%s", String.valueOf(bookingList.get(position).bookingId)));
        } catch (Exception e) {
            e.printStackTrace();
            try {//----------- IF FIREBASE RETURN EXCEPTION
                FirebaseCrashlytics.getInstance().recordException(e);
            } catch (Exception fe) {
                fe.printStackTrace();
            }
            Log.e(TAG, "onBindViewHolder: booking Id  " + e.getMessage());
        }

        try {//------------------Work Date Excetion
            calendar.setTimeInMillis(bookingList.get(position).workDate);
        } catch (Exception e) {
            e.printStackTrace();
            try {//----------- IF FIREBASE RETURN EXCEPTION
                FirebaseCrashlytics.getInstance().recordException(e);
            } catch (Exception fe) {
                fe.printStackTrace();
            }
            Log.e(TAG, "onBindViewHolder: work date exception  " + e.getMessage());
        }


        try {//-------------------------Start Time exception
            calendarTime.setTimeInMillis(bookingList.get(position).startTime);
        } catch (Exception e) {
            e.printStackTrace();
            try {//----------- IF FIREBASE RETURN EXCEPTION
                FirebaseCrashlytics.getInstance().recordException(e);
            } catch (Exception fe) {
                fe.printStackTrace();
            }
            Log.e(TAG, "onBindViewHolder: Start Time Exception " + e.getMessage());
        }
        // holder.tvDateValue.setText(String.format("%s · %s", simpleDateFormat.format(calendar.getTime()), simpleDateFormatTime.format(bookingList.get(position).startTime)));
//---------------------------------------------Handle date Exception
        try {
            holder.tvDateValue.setText(String.format("%s · %s", simpleDateFormat.format(bookingList.get(position).startTime), simpleDateFormatTime.format(bookingList.get(position).startTime)));

        } catch (Exception e) {
            e.printStackTrace();
            try {//----------- IF FIREBASE RETURN EXCEPTION
                FirebaseCrashlytics.getInstance().recordException(e);
            } catch (Exception fe) {
                fe.printStackTrace();
            }
            Log.e(TAG, "onBindViewHolder: Date Exception " + e.getMessage());
        }

//----------------------------------duration Exception handle ----------------------------------
        try {
            holder.tvDurationValue.setText(bookingList.get(position).duration + " hours");
        } catch (Exception e) {
            e.printStackTrace();
            try {//----------- IF FIREBASE RETURN EXCEPTION
                FirebaseCrashlytics.getInstance().recordException(e);
            } catch (Exception fe) {
                fe.printStackTrace();
            }
            Log.e(TAG, "onBindViewHolder: Duration Exception  " + e.getMessage());
        }

//------------------Handle Exception address --------------
        try {
            FullAddress fullAddress = bookingList.get(position).address;
            String other = "";
            String address = "";
            if (fullAddress.moreDetailedaddress != null && !fullAddress.moreDetailedaddress.isEmpty()) {
                other = context.getString(R.string.label_additionaldetails) + " " + fullAddress.moreDetailedaddress;
            } else {
                other = "";
            }
            if (bookingList.get(position).address.country.equalsIgnoreCase("Bahrain")) {
                if (fullAddress.buildingNumber.isEmpty()) {
                    address = context.getString(R.string.label_villa_2) + " " + fullAddress.villaName + "\n"
                            + context.getString(R.string.label_block_2) + " " + fullAddress.buildingName + "\n"
                            + context.getString(R.string.label_road_2) + " " + fullAddress.streetName + "\n"
                            + context.getString(R.string.label_city) + " " + fullAddress.city + ", " + fullAddress.country;
                } else {
                    address = context.getString(R.string.label_villa_2) + " " + fullAddress.villaName + "\n"
                            + context.getString(R.string.label_block_2) + " " + fullAddress.buildingName + "\n"
                            + context.getString(R.string.label_building_2) + " " + fullAddress.buildingNumber + "\n"
                            + context.getString(R.string.label_road_2) + " " + fullAddress.streetName + "\n"
                            + context.getString(R.string.label_city) + " " + fullAddress.city + ", " + fullAddress.country;
                }
            } else {
                if (fullAddress.buildingName.isEmpty()) {
                    address = context.getString(R.string.label_building_uk) + " " + fullAddress.buildingNumber + "\n"
                            + context.getString(R.string.label_street) + " " + fullAddress.streetName + "\n"
                            + context.getString(R.string.label_postalcode) + " " + fullAddress.postalCode + "\n"
                            + context.getString(R.string.label_city) + " " + fullAddress.city + ", " + fullAddress.country;
                } else {
                    address = context.getString(R.string.label_flat) + " " + fullAddress.buildingName + "\n"
                            + context.getString(R.string.label_building_uk) + " " + fullAddress.buildingNumber + "\n"
                            + context.getString(R.string.label_street) + " " + fullAddress.streetName + "\n"
                            + context.getString(R.string.label_postalcode) + " " + fullAddress.postalCode + "\n"
                            + context.getString(R.string.label_city) + " " + fullAddress.city + ", " + fullAddress.country;
                }
            }
            holder.tvAddressValue.setText(address);
        }
        catch (Exception e) {
            e.printStackTrace();
            try {//----------- IF FIREBASE RETURN EXCEPTION
                FirebaseCrashlytics.getInstance().recordException(e);
            } catch (Exception fe) {
                fe.printStackTrace();
            }
            Log.e(TAG, "onBindViewHolder: Address Exception  " + e.getMessage());
        }

        if (pageType.equals("UpComing")) {
            //------------------------Handle Upcomming visibilty Exception----------------
            try {
                if (bookingList.get(position).isExtend.transactionId != null && !bookingList.get(position).isExtend.transactionId.isEmpty()) {
                    holder.tvExtendSmall.setVisibility(View.GONE);
                    holder.tvStatus.setText(getExtensionStatus(bookingList.get(position).isExtend.agencyAction));
                    holder.tvStatus.setVisibility(View.GONE);
                } else {
                    holder.tvExtendSmall.setVisibility(View.GONE);
                    holder.tvStatus.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                try {//----------- IF FIREBASE RETURN EXCEPTION
                    FirebaseCrashlytics.getInstance().recordException(e);
                } catch (Exception fe) {
                    fe.printStackTrace();
                }
                Log.e(TAG, "onBindViewHolder: Exception Upcomming visibilty " + e.getMessage());
            }
        } else if (pageType.equals("OnGoing")) {
 //------------------------------------Handle ONGoing Exception-----------------
            try {
                if (bookingList.get(position).isExtend.transactionId != null && !bookingList.get(position).isExtend.transactionId.isEmpty()) {
                    holder.tvExtend.setText(getExtensionStatus(bookingList.get(position).isExtend.agencyAction));
                    holder.tvExtend.setEnabled(false);
                    holder.tvExtend.setClickable(false);
                    holder.tvExtend.setAlpha(0.5f);
                } else {
                    holder.tvExtend.setText(context.getString(R.string.extend));
                    holder.tvExtend.setEnabled(true);
                    holder.tvExtend.setAlpha(1f);
                    holder.tvExtend.setClickable(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
                try {//----------- IF FIREBASE RETURN EXCEPTION
                    FirebaseCrashlytics.getInstance().recordException(e);
                } catch (Exception fe) {
                    fe.printStackTrace();
                }
                Log.e(TAG, "onBindViewHolder: OnGoing exception " + e.getMessage());
            }
        }

    PojoMyBooking.Datum booking = bookingList.get(position);
        //holder.tvVatAmountValue.setText(booking.vat+"%");
        //  Log.e("ddddddddddd", booking.promoDiscount);
        double totalper;
        double per = 0.0f;
        double total = 0.0f;
        double totalExcVat = 0.0f;
//        double vatValue = 0.0f;
        double totalValue = 0.0;
        double percentage = 0.0;
        double maidprice = 0.0;

        if (pageType.equalsIgnoreCase("Past")) {
            holder.tvChat.setVisibility(View.GONE);
        } else if (pageType.equalsIgnoreCase("OnGoing")) {
            holder.tvChat.setVisibility(View.GONE);
        }


      /*  if (bookingList.get(position).address.country.equalsIgnoreCase("Bahrain")) {
            maidprice=totalper;
        }else {
            try {
                maidprice=Float.parseFloat(String.format("%.2f",totalper));
            }catch (Exception e){

            }
        }*/

 //-------------------------Amount Work Start--------------------------------------------------------------
//------------------------------ Exception actual price
        try {
            if (bookingList.get(position).currency.equalsIgnoreCase("BHD")) {
                holder.tvRatePerHourValue.setText(String.format(Locale.ENGLISH, "%s %.3f", booking.currency, bookingList.get(position).actualPrice));
                maidprice = Double.parseDouble(String.format(Locale.ENGLISH,"%.3f",(bookingList.get(position).actualPrice)));
            } else {
                holder.tvRatePerHourValue.setText(String.format(Locale.ENGLISH, "%s %.2f", booking.currency, bookingList.get(position).actualPrice));
                maidprice = Double.parseDouble(String.format(Locale.ENGLISH,"%.2f",(bookingList.get(position).actualPrice)));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "onBindViewHolder:Exception actual price " + e.getMessage());
            try {//----------- IF FIREBASE RETURN EXCEPTION
                FirebaseCrashlytics.getInstance().recordException(e);
            } catch (Exception fe) {
                fe.printStackTrace();
            }
        }

        //   holder.tvVatAmount.setText(context.getString(R.string.vat_value)+" (at "+bookingList.get(position).vat+"%) :");

        try {//---------- Exception handle booking type
            if (bookingList.get(position).bookingType.equalsIgnoreCase("2")) {
                holder.tvChat.setVisibility(View.GONE);
                holder.tvMaidName.setVisibility(View.INVISIBLE);
                holder.tvMaidNameLabel.setVisibility(View.VISIBLE);
                holder.tvMaidName.setText("");
                holder.tvMaidNameLabel.setText(R.string.bulk_booking);
                holder.tvPaymentValue.setText(context.getResources().getString(R.string.cardd));
                holder.tvExtend.setEnabled(false);
                holder.tvExtend.setVisibility(View.GONE);
                holder.tvExtendSmall.setEnabled(false);
                holder.tvNoMaids.setVisibility(View.VISIBLE);
                holder.tvNoMaidsValuee.setVisibility(View.VISIBLE);
                try {//------------- Exception handle maid count
                    holder.tvNoMaidsValuee.setText(bookingList.get(position).maidCount);
                } catch (Exception e) {
                    e.printStackTrace();
                    try {//----------- IF FIREBASE RETURN EXCEPTION
                        FirebaseCrashlytics.getInstance().recordException(e);
                    } catch (Exception fe) {
                        fe.printStackTrace();
                    }
                }
                holder.tvExtendSmall.setBackgroundResource(R.drawable.rect_grey_chat);
                if (pageType.equalsIgnoreCase("OnGoing")) {
                    holder.tvExtend.setBackgroundResource(R.drawable.rect_grey_chat);
                }

                try {
                    totalValue = (Integer.parseInt(bookingList.get(position).maidCount) * bookingList.get(position).duration * maidprice);
                } catch (Exception e) {
                    e.printStackTrace();
                    try {//----------- IF FIREBASE RETURN EXCEPTION
                        FirebaseCrashlytics.getInstance().recordException(e);
                    } catch (Exception fe) {
                        fe.printStackTrace();
                    }
                }
                // binding.tvTotalPriceValue.setText(String.format(Locale.US, "%s %.3f", maidData.getCurrency(), total));
            } else {

                try {//------------------- Exception handle payment mode
                    if (bookingList.get(position).paymentMode.equalsIgnoreCase("1")) {
                        holder.tvPaymentValue.setText(context.getResources().getString(R.string.cash));
                    } else {
                        holder.tvPaymentValue.setText(context.getResources().getString(R.string.cardd));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    try {//----------- IF FIREBASE RETURN EXCEPTION
                        FirebaseCrashlytics.getInstance().recordException(e);
                    } catch (Exception fe) {
                        fe.printStackTrace();
                    }
                }

                try {//----------- Exception handle price
                    totalValue = Double.parseDouble(String.format(Locale.ENGLISH,"%.3f",(bookingList.get(position).duration * maidprice)));
                } catch (Exception e) {
                    e.printStackTrace();
                    try {//----------- IF FIREBASE RETURN EXCEPTION
                        FirebaseCrashlytics.getInstance().recordException(e);
                    } catch (Exception fe) {
                        fe.printStackTrace();
                    }
                }

                // binding.tvTotalPriceValue.setText(String.format(Locale.US, "%s %.3f", maidData.getCurrency(), total));

                try {//---------------- Excepion handle currency amount
                    holder.tvTotalAmountValue.setText(String.format("%s %.3f", bookingList.get(position).currency, bookingList.get(position).amount));
                } catch (Exception e) {
                    e.printStackTrace();
                    try {//----------- IF FIREBASE RETURN EXCEPTION
                        FirebaseCrashlytics.getInstance().recordException(e);
                    } catch (Exception fe) {
                        fe.printStackTrace();
                    }
                }

                holder.tvMaidNameLabel.setText(R.string.label_maidNAme);


                try {//------------------- Exception handle log agency type
                    Log.d(TAG, "onBindViewHolder: ");
                    Log.d(TAG, "onBindViewHolder: getAgencyType:-- " + bookingList.get(position).agencyId.agencyType);
                } catch (Exception e) {
                    e.printStackTrace();
                    try {//----------- IF FIREBASE RETURN EXCEPTION
                        FirebaseCrashlytics.getInstance().recordException(e);
                    } catch (Exception fe) {
                        fe.printStackTrace();
                    }
                }
          /*  if (bookingList.get(position).agencyId._id.equalsIgnoreCase("5ad5a5c4cce102577f043c19")) {
                holder.tvChat.setVisibility(View.VISIBLE);
            } else {
                holder.tvChat.setVisibility(View.GONE);
            }*/

                if (pageType.equalsIgnoreCase("UpComing")) {
                    try {//------------------- Exception ahndle agency type.
                        if (bookingList.get(position).agencyId.agencyType.equalsIgnoreCase("NORMAL") || bookingList.get(position).agencyId.agencyType.equalsIgnoreCase("MAK_REGISTERED")) {
                            holder.tvChat.setVisibility(View.VISIBLE);
                        } else {
                            holder.tvChat.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        try {//----------- IF FIREBASE RETURN EXCEPTION
                            FirebaseCrashlytics.getInstance().recordException(e);
                        } catch (Exception fe) {
                            fe.printStackTrace();
                        }
                    }
                }

                holder.tvMaidName.setVisibility(View.VISIBLE);
                holder.tvMaidNameLabel.setVisibility(View.VISIBLE);
                holder.tvExtend.setEnabled(true);
                holder.tvExtendSmall.setEnabled(true);
                holder.tvNoMaids.setVisibility(View.GONE);
                holder.tvNoMaidsValuee.setVisibility(View.GONE);
                if (pageType.equalsIgnoreCase("OnGoing")) {
                    holder.tvExtend.setBackgroundResource(R.drawable.button_bg);
                }
                holder.tvExtendSmall.setBackgroundResource(R.drawable.button_bg);
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {//----------- IF FIREBASE RETURN EXCEPTION
                FirebaseCrashlytics.getInstance().recordException(e);
            } catch (Exception fe) {
                fe.printStackTrace();
            }
        }


        try {//---------------------- Exception handle for vat and promro code
            if ((bookingList.get(position).new_vat == null ? bookingList.get(position).vat : bookingList.get(position).new_vat) /*vat*/.equalsIgnoreCase("0") && booking.promoDiscount.equalsIgnoreCase("0")) {
                holder.tvVatAmountValue.setVisibility(View.GONE);
                holder.tvVatAmount.setVisibility(View.GONE);
                holder.tvFinalPrice.setVisibility(View.GONE);
                holder.tvFinalpriceValue.setVisibility(View.GONE);
                holder.tvDiscountAmountValue.setVisibility(View.GONE);
                holder.tvActualAmountValue.setVisibility(View.GONE);
                holder.tvActualAmount.setVisibility(View.GONE);
                holder.tvDiscountAmount.setVisibility(View.GONE);
                holder.tvDiscountprice.setVisibility(View.GONE);
                holder.tvDiscountpriceValue.setVisibility(View.GONE);
                holder.tvFinalAmount.setVisibility(View.GONE);
                holder.tvFinalAmountValue.setVisibility(View.GONE);
                holder.tvTotalAmount.setText(context.getString(R.string.total_amount) + " :");
            } else if (!booking.promoDiscount.equalsIgnoreCase("0") && !(bookingList.get(position).new_vat == null ? bookingList.get(position).vat : bookingList.get(position).new_vat)/*vat*/.equalsIgnoreCase("0")) {
                holder.tvVatAmountValue.setVisibility(View.VISIBLE);
                holder.tvVatAmount.setVisibility(View.VISIBLE);
                holder.tvFinalPrice.setVisibility(View.VISIBLE);
                holder.tvFinalpriceValue.setVisibility(View.VISIBLE);
                holder.tvDiscountAmountValue.setVisibility(View.VISIBLE);
                holder.tvDiscountAmount.setVisibility(View.VISIBLE);
                holder.tvFinalAmountValue.setVisibility(View.GONE);
                holder.tvDiscountprice.setVisibility(View.GONE);
                holder.tvDiscountpriceValue.setVisibility(View.GONE);
                holder.tvActualAmount.setVisibility(View.VISIBLE);
                holder.tvActualAmountValue.setVisibility(View.VISIBLE);
                holder.tvActualAmount.setText(context.getString(R.string.actual_amount) + " (incl. VAT) :");
                holder.tvTotalAmount.setText(context.getString(R.string.total_amount) + " (excl. VAT) :");
                try {//------- Exception handle for vat value
                    holder.tvVatAmount.setText(context.getString(R.string.vat_value) + " (" + "@" + (bookingList.get(position).new_vat == null ? bookingList.get(position).vat : bookingList.get(position).new_vat)/*vat*/ + "%" + ") :");
                } catch (Exception e) {
                    e.printStackTrace();
                    try {//----------- IF FIREBASE RETURN EXCEPTION
                        FirebaseCrashlytics.getInstance().recordException(e);
                    } catch (Exception fe) {
                        fe.printStackTrace();
                    }
                }
                holder.tvFinalPrice.setText(context.getString(R.string.final_amount_with_vat) + " (incl. VAT) :");
            } else if (!booking.promoDiscount.equalsIgnoreCase("0")) {
                holder.tvVatAmountValue.setVisibility(View.GONE);
                holder.tvVatAmount.setVisibility(View.GONE);
                holder.tvActualAmountValue.setVisibility(View.GONE);
                holder.tvActualAmount.setVisibility(View.GONE);
                holder.tvFinalPrice.setVisibility(View.VISIBLE);
                holder.tvFinalpriceValue.setVisibility(View.VISIBLE);
                holder.tvDiscountAmountValue.setVisibility(View.VISIBLE);
                holder.tvDiscountAmount.setVisibility(View.VISIBLE);
                holder.tvDiscountprice.setVisibility(View.GONE);
                holder.tvDiscountpriceValue.setVisibility(View.GONE);
                holder.tvFinalAmount.setVisibility(View.GONE);
                holder.tvFinalAmountValue.setVisibility(View.GONE);
                holder.tvTotalAmount.setText(context.getString(R.string.total_amount) + " :");
                holder.tvFinalPrice.setText(context.getString(R.string.final_amount_with_vat) + " :");
            } else if (!(bookingList.get(position).new_vat == null ? bookingList.get(position).vat : bookingList.get(position).new_vat)/*vat*/.equalsIgnoreCase("0")) {
                holder.tvVatAmountValue.setVisibility(View.VISIBLE);
                holder.tvVatAmount.setVisibility(View.VISIBLE);
                holder.tvFinalPrice.setVisibility(View.VISIBLE);
                holder.tvFinalpriceValue.setVisibility(View.VISIBLE);
                holder.tvDiscountAmountValue.setVisibility(View.GONE);
                holder.tvActualAmountValue.setVisibility(View.GONE);
                holder.tvActualAmount.setVisibility(View.GONE);
                holder.tvDiscountAmount.setVisibility(View.GONE);
                holder.tvDiscountprice.setVisibility(View.GONE);
                holder.tvDiscountpriceValue.setVisibility(View.GONE);
                holder.tvFinalAmount.setVisibility(View.GONE);
                holder.tvFinalAmountValue.setVisibility(View.GONE);
                holder.tvTotalAmount.setText(context.getString(R.string.total_amount) + " (excl. VAT) :");
                try {//------------------------- Exception handle for vat value
                    holder.tvVatAmount.setText(context.getString(R.string.vat_value) + " (" + "@" + (bookingList.get(position).new_vat == null ? bookingList.get(position).vat : bookingList.get(position).new_vat)/*vat*/ + "%" + ") :");
                } catch (Exception e) {
                    e.printStackTrace();
                    try {//----------- IF FIREBASE RETURN EXCEPTION
                        FirebaseCrashlytics.getInstance().recordException(e);
                    } catch (Exception fe) {
                        fe.printStackTrace();
                    }
                }
                holder.tvFinalPrice.setText(context.getString(R.string.final_amount_with_vat) + " (incl. VAT) :");
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {//----------- IF FIREBASE RETURN EXCEPTION
                FirebaseCrashlytics.getInstance().recordException(e);
            } catch (Exception fe) {
                fe.printStackTrace();
            }
        }

        try {//---------- Exception handle promo discount
            Log.d(TAG, "onBindViewHolder: promo discount  " + booking.promoDiscount);
        } catch (Exception e) {
            e.printStackTrace();
            try {//----------- IF FIREBASE RETURN EXCEPTION
                FirebaseCrashlytics.getInstance().recordException(e);
            } catch (Exception fe) {
                fe.printStackTrace();
            }
        }


        try {//-------------------------- Exception handle booking.promodiscount
            if (!booking.promoDiscount.equalsIgnoreCase("0")) {
              /* double total=0.0F;
                if (bookingList.get(position).bookingType.equalsIgnoreCase("2")) {
                    total = (Integer.parseInt(bookingList.get(position).maidCount) * bookingList.get(position).duration *bookingList.get(position).actualPrice);

                }else {
                    total = (bookingList.get(position).duration * bookingList.get(position).actualPrice);

                }*/


                try {//-----------handle booking.amount exception
                    Log.e("Discount price==== ", booking.amount.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    try {//----------- IF FIREBASE RETURN EXCEPTION
                        FirebaseCrashlytics.getInstance().recordException(e);
                    } catch (Exception fe) {
                        fe.printStackTrace();
                    }
                }
                // rounded Discount price
                Double Discount_price = 0.0;
                Double exclVatAmount = 0.0;

                try {//------------------- Exception handle booking.amount
                    if (booking.currency.equalsIgnoreCase("BHD")) {
                        Discount_price = Double.parseDouble(String.valueOf(booking.amount));
                    } else {
                        Discount_price = Double.parseDouble(String.valueOf(booking.amount));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    try {//----------- IF FIREBASE RETURN EXCEPTION
                        FirebaseCrashlytics.getInstance().recordException(e);
                    } catch (Exception fe) {
                        fe.printStackTrace();
                    }
                }

//------------------------------------------------new calculation
                try {//--------------------------- Exception handle booking.vat
                    if (booking.currency.equalsIgnoreCase("BHD")) {
                        if (booking.new_vat == null) {  /* old calculation on old vat */
                            per = (Discount_price / 100.0) * Float.parseFloat((booking.vat));
                        } else {
//                        Discount_price = Discount_price / ((Float.parseFloat((booking.new_vat)) + 100) / 100);
                            exclVatAmount = Double.parseDouble(String.format (Locale.ENGLISH,"%.3f",(Discount_price / ((Double.parseDouble((booking.new_vat)) + 100) / 100))));
                            //per = (Discount_price * (Double.parseDouble(booking.new_vat))) / 100;
                            per = Double.parseDouble(String.format(Locale.ENGLISH,"%.3f",(Discount_price - exclVatAmount)));
                        }
                    }else{
                        if (booking.new_vat == null) {  /* old calculation on old vat */
                            per = (Discount_price / 100.0) * Float.parseFloat((booking.vat));
                        } else {
//                        Discount_price = Discount_price / ((Float.parseFloat((booking.new_vat)) + 100) / 100);
                            exclVatAmount = Double.parseDouble(String.format(Locale.ENGLISH,"%.2f", (Discount_price / ((Double.parseDouble((booking.new_vat)) + 100) / 100))));
//                            per = (Discount_price * (Double.parseDouble(booking.new_vat))) / 100;
                            per = Double.parseDouble(String.format(Locale.ENGLISH,"%.2f",(Discount_price-exclVatAmount)));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    try {//----------- IF FIREBASE RETURN EXCEPTION
                        FirebaseCrashlytics.getInstance().recordException(e);
                    } catch (Exception fe) {
                        fe.printStackTrace();
                    }
                }

                totalper = per + exclVatAmount;

                try {//----------- Exception handle log
                    Log.d(TAG, "onBindViewHolder: (!booking.promoDiscount.equalsIgnoreCase(\"0\")) Discount_price value" + Discount_price);
                    Log.d(TAG, "onBindViewHolder: (!booking.promoDiscount.equalsIgnoreCase(\"0\")) excl vat" + exclVatAmount);
                    Log.d(TAG, "onBindViewHolder:(!booking.promoDiscount.equalsIgnoreCase(\"0\")) vat value" + per);
                    Log.d(TAG, "onBindViewHolder: (!booking.promoDiscount.equalsIgnoreCase(\"0\")) totalper value" + totalper);
                } catch (Exception e) {
                    try {//----------- IF FIREBASE RETURN EXCEPTION
                        FirebaseCrashlytics.getInstance().recordException(e);
                    } catch (Exception fe) {
                        fe.printStackTrace();
                    }
                }
//---------------------------------------------------end new calculation

                // Double finalvalue=totalper-Float.parseFloat(booking.promoDiscount);

                try {//---------Exception handle currency
                    if (booking.currency.equalsIgnoreCase("BHD")) {


                        if (per == 0) {
                            try {//--------- Exception handle promo discount
                                holder.tvTotalAmountValue.setText(String.format(Locale.ENGLISH, "%s %.3f", bookingList.get(position).currency, exclVatAmount - Double.parseDouble(booking.promoDiscount)));
                            } catch (Exception e) {
                                e.printStackTrace();
                                try {//----------- IF FIREBASE RETURN EXCEPTION
                                    FirebaseCrashlytics.getInstance().recordException(e);
                                } catch (Exception fe) {
                                    fe.printStackTrace();
                                }
                            }
                        } else {
                            try {//-------------- Exception handle currency
                                holder.tvTotalAmountValue.setText(String.format(Locale.ENGLISH, "%s %.3f", bookingList.get(position).currency, exclVatAmount));
                            } catch (Exception e) {
                                e.printStackTrace();
                                try {//----------- IF FIREBASE RETURN EXCEPTION
                                    FirebaseCrashlytics.getInstance().recordException(e);
                                } catch (Exception fe) {
                                    fe.printStackTrace();
                                }
                            }
                        }


                        try {//-----------------  Exception handle for vat
                            if (!(booking.new_vat == null ? booking.vat : booking.new_vat).equalsIgnoreCase("0")) {
                                holder.tvActualAmountValue.setText((String.format(Locale.ENGLISH, "%s %.3f", booking.currency, (booking.amount + Double.parseDouble(booking.promoDiscount)))));
                            }
                        } catch (Exception e) {
                            try {//----------- IF FIREBASE RETURN EXCEPTION
                                FirebaseCrashlytics.getInstance().recordException(e);
                            } catch (Exception fe) {
                                fe.printStackTrace();
                            }
                        }
                        //---- [holder.tvDiscountAmountValue]
                        try {//----------- Exception-handle for  currency and promo discount
                            holder.tvDiscountAmountValue.setText(String.format(Locale.ENGLISH, "%s %s%.3f", booking.currency, " -", Double.parseDouble(booking.promoDiscount)));
                        } catch (Exception e) {
                            e.printStackTrace();
                            try {//----------- IF FIREBASE RETURN EXCEPTION
                                FirebaseCrashlytics.getInstance().recordException(e);
                            } catch (Exception fe) {
                                fe.printStackTrace();
                            }
                        }
                        // ------- [holder.tvFinalAmountValue]
                        try {//======== Exception handle currency----totel per----
                            holder.tvFinalAmountValue.setText(String.format(Locale.ENGLISH, "%s %.3f", booking.currency, totalper));
                        } catch (Exception e) {
                            e.printStackTrace();
                            try {//----------- IF FIREBASE RETURN EXCEPTION
                                FirebaseCrashlytics.getInstance().recordException(e);
                            } catch (Exception fe) {
                                fe.printStackTrace();
                            }
                        }

                        //----[holder.tvFinalpriceValue]-----
                        try {//-------------- Exception handle currency and amount
                            holder.tvFinalpriceValue.setText(String.format(Locale.ENGLISH, "%s %.3f", booking.currency, booking.amount));
                        } catch (Exception e) {
                            e.printStackTrace();
                            try {//----------- IF FIREBASE RETURN EXCEPTION
                                FirebaseCrashlytics.getInstance().recordException(e);
                            } catch (Exception fe) {
                                fe.printStackTrace();
                            }
                        }
                        //---[holder.tvVatAmountValue]
                        try {//------------ Exception handle currency and per
                            holder.tvVatAmountValue.setText(String.format(Locale.ENGLISH, "%s %.3f", booking.currency, per));
                        } catch (Exception e) {
                            e.printStackTrace();
                            try {//----------- IF FIREBASE RETURN EXCEPTION
                                FirebaseCrashlytics.getInstance().recordException(e);
                            } catch (Exception fe) {
                                fe.printStackTrace();
                            }
                        }

                        //----- [holder.tvDiscountPriceValue]
                        try {//--------------- Exception handle currency amount
                            holder.tvDiscountpriceValue.setText(String.format(Locale.ENGLISH, " %s %.3f ", booking.currency, booking.amount));
                        } catch (Exception e) {
                            try {//----------- IF FIREBASE RETURN EXCEPTION
                                FirebaseCrashlytics.getInstance().recordException(e);
                            } catch (Exception fe) {
                                fe.printStackTrace();
                            }
                        }
                    } else { ///  set amount for uk case ------
                        if (per == 0) {
                            try {//----- Exception handle promo discount
//                                holder.tvTotalAmountValue.setText(String.format(Locale.ENGLISH, "%s %.2f", bookingList.get(position).currency, Discount_price - Double.parseDouble(booking.promoDiscount)));
                                holder.tvTotalAmountValue.setText(String.format(Locale.ENGLISH, "%s %.2f", bookingList.get(position).currency, exclVatAmount - Double.parseDouble(booking.promoDiscount)));
                            } catch (Exception e) {
                                e.printStackTrace();
                                try {//----------- IF FIREBASE RETURN EXCEPTION
                                    FirebaseCrashlytics.getInstance().recordException(e);
                                } catch (Exception fe) {
                                    fe.printStackTrace();
                                }
                            }
                        } else {
                            try {//-------- Exception handle currency and discount
//                                holder.tvTotalAmountValue.setText(String.format(Locale.ENGLISH, "%s %.2f", bookingList.get(position).currency, Discount_price));
                                holder.tvTotalAmountValue.setText(String.format(Locale.ENGLISH, "%s %.2f", bookingList.get(position).currency, exclVatAmount));
                            } catch (Exception e) {
                                e.printStackTrace();
                                try {//----------- IF FIREBASE RETURN EXCEPTION
                                    FirebaseCrashlytics.getInstance().recordException(e);
                                } catch (Exception fe) {
                                    fe.printStackTrace();
                                }
                            }
                        }


                        try {//---------- Exception currency amount promo discount-------
                            if (!(booking.new_vat == null ? booking.vat : booking.new_vat).equalsIgnoreCase("0")) {
                                holder.tvActualAmountValue.setText((String.format(Locale.ENGLISH, "%s %.2f", booking.currency, (booking.amount + Double.parseDouble(booking.promoDiscount)))));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            try {//----------- IF FIREBASE RETURN EXCEPTION
                                FirebaseCrashlytics.getInstance().recordException(e);
                            } catch (Exception fe) {
                                fe.printStackTrace();
                            }
                        }
                        //------h[older.tvDiscountAmountValue]
                        try {
                            holder.tvDiscountAmountValue.setText(String.format(Locale.ENGLISH, "%s %s%.2f", booking.currency, " -", Double.parseDouble(booking.promoDiscount)));
                        } catch (Exception e) {
                            e.printStackTrace();
                            try {//----------- IF FIREBASE RETURN EXCEPTION
                                FirebaseCrashlytics.getInstance().recordException(e);
                            } catch (Exception fe) {
                                fe.printStackTrace();
                            }
                        }
                        //------[holder.tvFinalAmountValue]
                        try {
                            holder.tvFinalAmountValue.setText(String.format(Locale.ENGLISH, "%s %.2f", booking.currency, totalper));
                        } catch (Exception e) {
                            e.printStackTrace();
                            try {//----------- IF FIREBASE RETURN EXCEPTION
                                FirebaseCrashlytics.getInstance().recordException(e);
                            } catch (Exception fe) {
                                fe.printStackTrace();
                            }
                        }
                        //-------[holder.tvFinalpriceValue]
                        try {
                            holder.tvFinalpriceValue.setText(String.format(Locale.ENGLISH, "%s %.2f", booking.currency, booking.amount));
                        } catch (Exception e) {
                            e.printStackTrace();
                            try {//----------- IF FIREBASE RETURN EXCEPTION
                                FirebaseCrashlytics.getInstance().recordException(e);
                            } catch (Exception fe) {
                                fe.printStackTrace();
                            }
                        }
                        //----[holder.tvVatAmountValue]
                        try {
                            holder.tvVatAmountValue.setText(String.format(Locale.ENGLISH, "%s %.2f", booking.currency, per));
                        } catch (Exception e) {
                            e.printStackTrace();
                            try {//----------- IF FIREBASE RETURN EXCEPTION
                                FirebaseCrashlytics.getInstance().recordException(e);
                            } catch (Exception fe) {
                                fe.printStackTrace();
                            }
                        }
                        //------[holder.tvDiscountpriceValue]
                        try {
                            holder.tvDiscountpriceValue.setText(String.format(Locale.ENGLISH, " %s %.2f ", booking.currency, booking.amount));
                        } catch (Exception e) {
                            e.printStackTrace();
                            try {//----------- IF FIREBASE RETURN EXCEPTION
                                FirebaseCrashlytics.getInstance().recordException(e);
                            } catch (Exception fe) {
                                fe.printStackTrace();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    try {//----------- IF FIREBASE RETURN EXCEPTION
                        FirebaseCrashlytics.getInstance().recordException(e);
                    } catch (Exception fe) {
                        fe.printStackTrace();
                    }
                }
            } else {
           /* holder.tvDiscountAmountValue.setVisibility(View.GONE);
            holder.tvFinalAmountValue.setVisibility(View.GONE);
            holder.tvDiscountAmount.setVisibility(View.GONE);
            holder.tvFinalAmount.setVisibility(View.GONE);
            holder.tvFinalPrice.setVisibility(View.GONE);
            holder.tvFinalAmountValue.setVisibility(View.GONE);*/
//        }
                // total value rounded
                try {//------ Exception handle currency
                    if (bookingList.get(position).currency.equalsIgnoreCase("BHD")) {
                        total = Double.parseDouble(String.format(Locale.ENGLISH,"%.3f",totalValue));
                    } else {
                        total = Double.parseDouble(String.format(Locale.ENGLISH,"%.2f",totalValue));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    try {//----------- IF FIREBASE RETURN EXCEPTION
                        FirebaseCrashlytics.getInstance().recordException(e);
                    } catch (Exception fe) {
                        fe.printStackTrace();
                    }
                }

                //-----------------Check new vat == null then use  vat
                try {//-------- Exception handle new vat
                    if (booking.new_vat == null) {
                        try {
                            per = (total / 100.0) * Float.parseFloat(booking.vat);
                        } catch (Exception e) {
                            e.printStackTrace();
                            try {//----------- IF FIREBASE RETURN EXCEPTION
                                FirebaseCrashlytics.getInstance().recordException(e);
                            } catch (Exception fe) {
                                fe.printStackTrace();
                            }
                        }
                    } else {
                        //------------------------------------------------new calculation
                        //------[total]
                        try {//-----------
//                            total = total / ((Float.parseFloat((booking.new_vat == null ? booking.vat : booking.new_vat)) + 100) / 100);
                            if (bookingList.get(position).currency.equalsIgnoreCase("BHD")) {
                                totalExcVat = Double.parseDouble(String.format(Locale.ENGLISH,"%.3f",(total / ((Double.parseDouble((booking.new_vat == null ? booking.vat : booking.new_vat)) + 100) / 100))));
                            }else{
                                totalExcVat = Double.parseDouble(String.format(Locale.ENGLISH,"%.2f",(total / ((Double.parseDouble((booking.new_vat == null ? booking.vat : booking.new_vat)) + 100) / 100))));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            try {//----------- IF FIREBASE RETURN EXCEPTION
                                FirebaseCrashlytics.getInstance().recordException(e);
                            } catch (Exception fe) {
                                fe.printStackTrace();
                            }
                        }
                        //------[per]
                        try { //-----------
                            //per = (total * (Double.parseDouble(booking.new_vat == null ? booking.vat : booking.new_vat))) / 100;
                            if (bookingList.get(position).currency.equalsIgnoreCase("BHD")) {
                                per = Double.parseDouble(String.format(Locale.ENGLISH,"%.3f",(total - totalExcVat)));
                            }else{
                                per = Double.parseDouble(String.format(Locale.ENGLISH,"%.2f",(total - totalExcVat)));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            try {//----------- IF FIREBASE RETURN EXCEPTION
                                FirebaseCrashlytics.getInstance().recordException(e);
                            } catch (Exception fe) {
                                fe.printStackTrace();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    try {//----------- IF FIREBASE RETURN EXCEPTION
                        FirebaseCrashlytics.getInstance().recordException(e);
                    } catch (Exception fe) {
                        fe.printStackTrace();
                    }
                }

                totalper = per + totalExcVat;
                Log.e(TAG, "onBindViewHolder: total value" + total);
                Log.e(TAG, "onBindViewHolder: totalExcVat value" + totalExcVat);
                Log.e(TAG, "onBindViewHolder: vat value" + per);
                Log.e(TAG, "onBindViewHolder: totalper value" + totalper);


//---------------------------------------------------end new calculation
                try {//----------handle exception currency---------
                    if (bookingList.get(position).currency.equalsIgnoreCase("BHD")) {
                        //------[holder.tvTotalAmountValue]
                        try {//------------- Exception handle currency AND TOTAL
//                            holder.tvTotalAmountValue.setText(String.format(Locale.ENGLISH, "%s %.3f", bookingList.get(position).currency, total));
                            holder.tvTotalAmountValue.setText(String.format(Locale.ENGLISH, "%s %.3f", bookingList.get(position).currency, totalExcVat));
                        } catch (Exception e) {
                            e.printStackTrace();
                            try {//----------- IF FIREBASE RETURN EXCEPTION
                                FirebaseCrashlytics.getInstance().recordException(e);
                            } catch (Exception fe) {
                                fe.printStackTrace();
                            }
                        }
//------[booking.promoDiscount]
                        if (booking.promoDiscount.equalsIgnoreCase("0") || booking.promoDiscount == null) {
//----[holder.tvFinalAmountValue]
                            try {
                                holder.tvFinalAmountValue.setText(String.format(Locale.ENGLISH, "%s %.3f", booking.currency, totalper));
                            } catch (Exception e) {
                                e.printStackTrace();
                                try {//----------- IF FIREBASE RETURN EXCEPTION
                                    FirebaseCrashlytics.getInstance().recordException(e);
                                } catch (Exception fe) {
                                    fe.printStackTrace();
                                }
                            }
                            //-----[holder.tvFinalpriceValue]
                            try {
                                holder.tvFinalpriceValue.setText(String.format(Locale.ENGLISH, "%s %.3f", booking.currency, totalper));
                            } catch (Exception e) {
                                e.printStackTrace();
                                try {//----------- IF FIREBASE RETURN EXCEPTION
                                    FirebaseCrashlytics.getInstance().recordException(e);
                                } catch (Exception fe) {
                                    fe.printStackTrace();
                                }
                            }
//-----[holder.tvVatAmountValue]
                            try {
                                holder.tvVatAmountValue.setText(String.format(Locale.ENGLISH, "%s %.3f", booking.currency, per));
                            } catch (Exception e) {
                                e.printStackTrace();
                                try {//----------- IF FIREBASE RETURN EXCEPTION
                                    FirebaseCrashlytics.getInstance().recordException(e);
                                } catch (Exception fe) {
                                    fe.printStackTrace();
                                }
                            }
                        }
                    } else {
                        //---------[holder.tvTotalAmountValue]
                        try {
//                            holder.tvTotalAmountValue.setText(String.format(Locale.ENGLISH, "%s %.2f", bookingList.get(position).currency, total));
                            holder.tvTotalAmountValue.setText(String.format(Locale.ENGLISH, "%s %.2f", bookingList.get(position).currency, totalExcVat));
                        } catch (Exception e) {
                            e.printStackTrace();
                            try {//----------- IF FIREBASE RETURN EXCEPTION
                                FirebaseCrashlytics.getInstance().recordException(e);
                            } catch (Exception fe) {
                                fe.printStackTrace();
                            }
                        }
//---------[booking.promoDiscount]
                        if (booking.promoDiscount.equalsIgnoreCase("0") || booking.promoDiscount == null) {
                            //-------[holder.tvFinalAmountValue]
                            try {
                                holder.tvFinalAmountValue.setText(String.format(Locale.ENGLISH, "%s %.2f", booking.currency, totalper));
                            } catch (Exception e) {
                                e.printStackTrace();
                                try {//----------- IF FIREBASE RETURN EXCEPTION
                                    FirebaseCrashlytics.getInstance().recordException(e);
                                } catch (Exception fe) {
                                    fe.printStackTrace();
                                }
                            }
//-------------[holder.tvFinalpriceValue]
                            try {
                                holder.tvFinalpriceValue.setText(String.format(Locale.ENGLISH, "%s %.2f", booking.currency, totalper));
                            } catch (Exception e) {
                                e.printStackTrace();
                                try {//----------- IF FIREBASE RETURN EXCEPTION
                                    FirebaseCrashlytics.getInstance().recordException(e);
                                } catch (Exception fe) {
                                    fe.printStackTrace();
                                }
                            }
//------------ [holder.tvVatAmountValue]
                            try {
                                holder.tvVatAmountValue.setText(String.format(Locale.ENGLISH, "%s %.2f", booking.currency, per));
                            }catch (Exception e){
                                e.printStackTrace();
                                try {//----------- IF FIREBASE RETURN EXCEPTION
                                    FirebaseCrashlytics.getInstance().recordException(e);
                                } catch (Exception fe) {
                                    fe.printStackTrace();
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    try {//----------- IF FIREBASE RETURN EXCEPTION
                        FirebaseCrashlytics.getInstance().recordException(e);
                    } catch (Exception fe) {
                        fe.printStackTrace();
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            try {//----------- IF FIREBASE RETURN EXCEPTION
                FirebaseCrashlytics.getInstance().recordException(e);
            } catch (Exception fe) {
                fe.printStackTrace();
            }
        }

    }

    private String getExtensionStatus(String agencyAction) {
        switch (agencyAction) {
            case Constants.EXTENSION_ACCEPT:
                return context.getString(R.string.extension_accepted);

            case Constants.EXTENSION_DECLINE:
                return context.getString(R.string.extension_declined);

            case Constants.EXTENSION_PENDING:
                return context.getString(R.string.extension_pending);

            default:
                return agencyAction;
        }
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvReschdule, tvPaymentValue, tvBookIdValue, tvNoMaids, tvNoMaidsValuee, tvExtend, tvStatus, tvChat, tvExtendSmall, tvMaidName, tvAgencyName, tvDateValue,
                tvDurationValue, tvAddressValue, tvCancel, tvFinalAmountValue, tvFinalAmount, tvDiscountAmount, tvDiscountAmountValue, tvRatePerHourValue, tvTotalAmountValue,
                tvMaidNameLabel, tvVatAmountValue, tvVatAmount, tvTotalAmount, tvActualAmountValue, tvActualAmount;
        TextView tvDiscountprice, tvDiscountpriceValue, tvFinalPrice, tvFinalpriceValue, tvServices, tvServiceValue;
        LinearLayout llExtend;
        ImageView ivMaid;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvReschdule = itemView.findViewById(R.id.tvReschdule);
            llExtend = itemView.findViewById(R.id.llExtend);
            tvMaidNameLabel = itemView.findViewById(R.id.tvMaidNameLabel);
            tvFinalAmount = itemView.findViewById(R.id.tvFinalAmount);
            tvExtend = itemView.findViewById(R.id.tvExtend);
            tvNoMaidsValuee = itemView.findViewById(R.id.tvNoMaidsValuee);
            tvNoMaids = itemView.findViewById(R.id.tvNoMaids);
            tvBookIdValue = itemView.findViewById(R.id.tvBookIdValue);
            tvPaymentValue = itemView.findViewById(R.id.tvPaymentValue);
            tvExtendSmall = itemView.findViewById(R.id.tvExtendSmall);
            ivMaid = itemView.findViewById(R.id.ivMaid);
            tvMaidName = itemView.findViewById(R.id.tvMaidName);
            tvAgencyName = itemView.findViewById(R.id.tvAgencyName);
            tvDateValue = itemView.findViewById(R.id.tvDateValue);
            tvDurationValue = itemView.findViewById(R.id.tvDurationValue);
            tvAddressValue = itemView.findViewById(R.id.tvAddressValue);
            tvCancel = itemView.findViewById(R.id.tvCancel);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvChat = itemView.findViewById(R.id.tvChat);
            tvRatePerHourValue = itemView.findViewById(R.id.tvRatePerHourValue);
            tvFinalAmountValue = itemView.findViewById(R.id.tvFinalAmountValue);
            tvDiscountAmountValue = itemView.findViewById(R.id.tvDiscountAmountValue);
            tvDiscountAmount = itemView.findViewById(R.id.tvDiscountAmount);
            tvTotalAmountValue = itemView.findViewById(R.id.tvTotalAmountValue);
            tvVatAmountValue = itemView.findViewById(R.id.tvVatAmountValue);
            tvVatAmount = itemView.findViewById(R.id.tvVatAmount);

            tvDiscountprice = itemView.findViewById(R.id.tvDiscountprice);
            tvDiscountpriceValue = itemView.findViewById(R.id.tvDiscountpriceValue);
            tvFinalPrice = itemView.findViewById(R.id.tvFinalPrice);
            tvFinalpriceValue = itemView.findViewById(R.id.tvFinalpriceValue);
            tvTotalAmount = itemView.findViewById(R.id.tvTotalAmount);
            tvServices = itemView.findViewById(R.id.tvServices);
            tvServiceValue = itemView.findViewById(R.id.tvServiceValue);
            tvActualAmountValue = itemView.findViewById(R.id.tvActualAmountValue);
            tvActualAmount = itemView.findViewById(R.id.tvActualAmount);

            switch (pageType) {
                case "OnGoing":
                    llExtend.setVisibility(View.GONE);
                    tvExtend.setVisibility(View.VISIBLE);
                    tvExtend.setText(context.getString(R.string.extend));
                    tvExtend.setOnClickListener(this);
                    tvChat.setOnClickListener(this);
                    tvChat.setVisibility(View.GONE);
                    tvReschdule.setVisibility(View.GONE);
                    break;
                case "UpComing":
                    llExtend.setVisibility(View.VISIBLE);
                    tvCancel.setVisibility(View.VISIBLE);
                    tvExtend.setVisibility(View.GONE);
                    tvExtend.setText(context.getString(R.string.extend));
                    tvExtendSmall.setOnClickListener(this);
                    tvCancel.setOnClickListener(this);
                    tvChat.setOnClickListener(this);
                    tvAddressValue.setOnClickListener(this);
                    tvChat.setVisibility(View.VISIBLE);
                    tvReschdule.setVisibility(View.VISIBLE);
                    break;
                case "Past":
                    llExtend.setVisibility(View.GONE);
                    tvExtend.setVisibility(View.VISIBLE);
                    tvExtend.setText(context.getString(R.string.book_again));
                    tvExtend.setOnClickListener(this);
                    tvChat.setVisibility(View.GONE);
                    tvReschdule.setVisibility(View.GONE);
                    itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //handle
                            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                                return;
                            }
                            mLastClickTime = SystemClock.elapsedRealtime();
                            Log.d(TAG, "onClick: maid data:--  " + new Gson().toJson(bookingList.get(getAdapterPosition()).maidId));

                            openMaid.openMaidProfile(bookingList.get(getAdapterPosition()).maidId);

                        }
                    });
                    break;
            }
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.tvExtendSmall:
                    //  Log.d(TAG, "onClick: tgyug");
                    //   Log.d(TAG, "onClick: vat :---  dhuih "+bookingList.get(getAdapterPosition()).maidId.getNew_vat());
                    cancelBooking.showExtendDialog(bookingList.get(getAdapterPosition()).paymentMode, bookingList.get(getAdapterPosition()).referenceId, bookingList.get(getAdapterPosition())._id,
                            bookingList.get(getAdapterPosition()).maidId.getActualPrice(),
                            bookingList.get(getAdapterPosition()).maidId.getCurrency(), bookingList.get(getAdapterPosition()).maidId.getNew_vat()/*getVat()*/);
                    //  Log.e("country",bookingList.get(getAdapterPosition()).address.country);

                    break;
                case R.id.tvCancel:


                    IOSAlertDialog dialog =  IOSAlertDialog.newInstance(
                      context,
                            null,
                            (bookingList.get(getAdapterPosition()).bookingType.equalsIgnoreCase("2")) ?
                                    context.getString(R.string.rescheduleTxt) :
                                    context.getString(R.string.cancel_text_cash),
                            context.getString(R.string.yes),
                            context.getString(R.string.no),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    cancelBooking.showCancelDialog(bookingList.get(getAdapterPosition())._id,
                                            bookingList.get(getAdapterPosition()).timeZone);
                                    dialog.cancel();
                                }
                            },
                                        new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                },
                            ContextCompat.getColor(context, R.color.app_color),
                            ContextCompat.getColor(context, R.color.app_color),
                            true


                    );

                    dialog.show(((AppCompatActivity) context).getSupportFragmentManager(), "ios_dialog");


           /*         AlertDialog dialog = new AlertDialog.Builder(context)
                            .setCancelable(true)
                            .setMessage((bookingList.get(getAdapterPosition()).bookingType.equalsIgnoreCase("2")) ?
                                    context.getString(R.string.rescheduleTxt) :
                                    context.getString(R.string.cancel_text_cash))
                            .setPositiveButton(
                                    R.string.yes,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            cancelBooking.showCancelDialog(bookingList.get(getAdapterPosition())._id,
                                                    bookingList.get(getAdapterPosition()).timeZone);
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
                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.appColor));
                    dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.appColor));
                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).setAllCaps(false);
                    dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setAllCaps(false);*/

                    break;
                case R.id.tvChat:
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra(Constants.USER_ID, bookingList.get(getAdapterPosition()).maidId.get_id());
                    intent.putExtra(Constants.SERVICE_ID, bookingList.get(getAdapterPosition())._id);
                    intent.putExtra(Constants.NAME, bookingList.get(getAdapterPosition()).maidId.getFirstName() + " " + bookingList.get(getAdapterPosition()).maidId.getLastName());
                    context.startActivity(intent);
                    break;
                case R.id.tvExtend:
                    if (pageType.equals("OnGoing")) {
                        Log.d(TAG, "onClick: new vat:--  " + bookingList.get(getAdapterPosition()).maidId.getNew_vat());
                        Log.d(TAG, "onClick:  vat:--  " + bookingList.get(getAdapterPosition()).maidId.getVat());
                        cancelBooking.showExtendDialog(bookingList.get(getAdapterPosition()).paymentMode, bookingList.get(getAdapterPosition()).referenceId, bookingList.get(getAdapterPosition())._id,
                                bookingList.get(getAdapterPosition()).maidId.getActualPrice(),
                                bookingList.get(getAdapterPosition()).maidId.getCurrency(), bookingList.get(getAdapterPosition()).maidId.getNew_vat()/*getVat()*/);
                        Log.e("country", bookingList.get(getAdapterPosition()).address.country);
                        Prefs.get().save(Constants.USER_COUNTRY, bookingList.get(getAdapterPosition()).address.country);

                    } else {

                        MaidData maidID = bookingList.get(getAdapterPosition()).maidId;
                        PojoMyBooking.Datum data = bookingList.get(getAdapterPosition());
                        AgencyId agencyId = bookingList.get(getAdapterPosition()).agencyId;
                        SearchMaidModel searchMaidModel = new SearchMaidModel();
                        String firstName = maidID.getFirstName();
                        String lastName = maidID.getLastName();
                        if (lastName != null && !lastName.isEmpty()) {
                            searchMaidModel.maidName = String.format("%s %s", context.getString(R.string.label_maidNAme), firstName.substring(0, 1).toUpperCase() + firstName.substring(1));
                        }

                        searchMaidModel.makId = maidID.getMakId();
                        searchMaidModel.maidId = maidID.get_id();
                        searchMaidModel.maidPrice = maidID.getActualPrice();
                        searchMaidModel.agencyName = agencyId.agencyName;
                        searchMaidModel.duration = data.duration;
                        searchMaidModel.workDate = data.workDate;
                        searchMaidModel.startTime = data.startTime;
                        searchMaidModel.locationName = data.locationName;
                        searchMaidModel.agencyType = data.agencyId.agencyType;
                        searchMaidModel.agencyId = agencyId._id;
                        searchMaidModel.servicesID = bookingList.get(getAdapterPosition()).services._id;
                        searchMaidModel.currency = bookingList.get(getAdapterPosition()).maidId.getCurrency();
                        /*if (maidID.getProfilePicURL() != null && maidID.getProfilePicURL().getOriginal() != null && !maidID.getProfilePicURL().getOriginal().isEmpty()) {
                            searchMaidModel.profilePicURL = maidID.getProfilePicURL();
                        } else*/ if (agencyId.profilePicURL != null && agencyId.profilePicURL.getOriginal() != null
                                && !agencyId.profilePicURL.getOriginal().isEmpty()) {
                            searchMaidModel.profilePicURL = agencyId.profilePicURL;
                        }
                        Intent intent1 = new Intent(context, BookAgainActivity.class);
                        intent1.putExtra(Constants.SEARCH_MAID_DATA, searchMaidModel);
                        intent1.putExtra(Constants.SERVICE_ID, bookingList.get(getAdapterPosition())._id);
                        intent1.putExtra(Constants.BOOKING_TYPE, bookingList.get(getAdapterPosition()).bookingType);
                        intent1.putExtra(Constants.BOOKING_DATA, new Gson().toJson(bookingList.get(getAdapterPosition())));
                        intent1.putExtra(Constants.MAID_DATA, new Gson().toJson(maidID));
                        intent1.putExtra(Constants.reschuleStatus, "");
                        intent1.putExtra(Constants.isFavorite, "");
                        intent1.putExtra("payment_mode", bookingList.get(getAdapterPosition()).paymentMode);
                        context.startActivity(intent1);
                    }
                    break;
            }
        }
    }

    public int printDifference(String endDate) {
        SimpleDateFormat simpleDateFormatt = new SimpleDateFormat("EEE, MMM dd · hh:mm a", Locale.ENGLISH);

        String currentDateandTime = simpleDateFormatt.format(new Date());

        Date date2 = null;
        Date date1 = null;
        try {
            date1 = simpleDateFormatt.parse(currentDateandTime);
            date2 = simpleDateFormatt.parse(endDate);
        } catch (ParseException e) {
            e.printStackTrace();
            try {//----------- IF FIREBASE RETURN EXCEPTION
                FirebaseCrashlytics.getInstance().recordException(e);
            } catch (Exception fe) {
                fe.printStackTrace();
            }
        }

        long difference = date2.getTime() - date1.getTime();
        return (int) TimeUnit.MILLISECONDS.toHours(difference);
    }
}
