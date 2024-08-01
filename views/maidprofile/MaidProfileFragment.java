package com.maktoday.views.maidprofile;


import static com.maktoday.views.home.HomeFragment.booking_type;
import static com.maktoday.views.home.HomeFragment.selectedDatesList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.maktoday.R;
import com.maktoday.adapter.ReviewAdapter;
import com.maktoday.databinding.FragmentMaidprofileBinding;
import com.maktoday.model.BookServiceModel;
import com.maktoday.model.MaidData;
import com.maktoday.model.PojoMaidProfile;
import com.maktoday.model.PojoMyBooking;
import com.maktoday.model.PojoReview;
import com.maktoday.model.SearchMaidBulkModel;
import com.maktoday.model.SearchMaidModel;
import com.maktoday.model.ServicesProvide;
import com.maktoday.utils.BaseFragment;
import com.maktoday.utils.Constants;
import com.maktoday.utils.DialogPopup;
import com.maktoday.utils.GeneralFunction;
import com.maktoday.utils.Log;
import com.maktoday.utils.Prefs;
import com.maktoday.views.confirmbook.ConfirmBookFragment;
import com.maktoday.views.main.Main2Activity;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by cbl81 on 26/10/17.
 */

public class MaidProfileFragment extends BaseFragment implements MaidProfileContract.View, View.OnClickListener {

    private static final String TAG = "MaidProfileFragment";
    private FragmentMaidprofileBinding binding;
    private MaidProfileContract.Presenter presenter;
    private SearchMaidModel searchMaidModel;
    private PojoMyBooking.Datum bookingDataModel;
    private PojoMaidProfile pojoMaidProfile;
    private String maidID = "";
    private String reschuleStatus = "";
    private String serviceId = "";
    private String currency = "";
    private ReviewAdapter reviewAdapter;
    private MaidData maidData;
    private List<PojoReview> reviewList = new ArrayList<>();
    private boolean isFavourite = false;

    public static MaidProfileFragment newInstance(PojoMyBooking.Datum bookingDataModel, MaidData maidData, SearchMaidModel searchMaidModel, boolean isFavourite, String reschuleStatus, String serviceID) {
        Bundle args = new Bundle();
        MaidProfileFragment fragment = new MaidProfileFragment();
        args.putString(Constants.MAID_ID, maidData.get_id());
        Log.d(TAG, "newInstance: maid data 1 :--  "+ new Gson().toJson(maidData));
        args.putParcelable(Constants.MAID_DATA, maidData);
        args.putString(Constants.reschuleStatus, reschuleStatus);
        args.putString(Constants.SERVICE_ID, serviceID);
        args.putString(Constants.BOOKING_DATA, new Gson().toJson(bookingDataModel));
        args.putParcelable(Constants.SEARCH_MAID_DATA, searchMaidModel);
        args.putBoolean(Constants.ISFAVOURITE, maidData.isFavourite());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
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
    }

    public static MaidProfileFragment newInstance(PojoMyBooking.Datum bookingDataModel, MaidData maidData, SearchMaidModel searchMaidModel, String reschuleStatus, String serviceID) {
        Bundle args = new Bundle();
        MaidProfileFragment fragment = new MaidProfileFragment();
        args.putString(Constants.MAID_ID, maidData.get_id());
        Log.d(TAG, "newInstance: maid data 2:--  "+ new Gson().toJson(maidData));
        args.putParcelable(Constants.MAID_DATA, maidData);
        args.putString(Constants.reschuleStatus, reschuleStatus);
        args.putString(Constants.SERVICE_ID, serviceID);
        args.putString(Constants.BOOKING_DATA, new Gson().toJson(bookingDataModel));
        args.putParcelable(Constants.SEARCH_MAID_DATA, searchMaidModel);
        fragment.setArguments(args);
        return fragment;
    }

    public static MaidProfileFragment newInstance(PojoMyBooking.Datum bookingDataModel, MaidData maidData, SearchMaidBulkModel searchMaidModel, String reschuleStatus, String serviceID) {
        Bundle args = new Bundle();
        MaidProfileFragment fragment = new MaidProfileFragment();
        args.putString(Constants.MAID_ID, maidData.get_id());
        Log.d(TAG, "newInstance: 2"+ new Gson().toJson(maidData));
        args.putParcelable(Constants.MAID_DATA, maidData);
        args.putString(Constants.reschuleStatus, reschuleStatus);
        args.putString(Constants.SERVICE_ID, serviceID);
        args.putString(Constants.BOOKING_DATA, new Gson().toJson(bookingDataModel));
        args.putParcelable(Constants.SEARCH_MAID_DATA, searchMaidModel);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchMaidModel = getArguments().getParcelable(Constants.SEARCH_MAID_DATA);
        reschuleStatus = getArguments().getString(Constants.reschuleStatus);
        if (reschuleStatus.equalsIgnoreCase("yes")) {
            bookingDataModel = new Gson().fromJson(getArguments().getString(Constants.BOOKING_DATA), PojoMyBooking.Datum.class);
        }
        serviceId = getArguments().getString(Constants.SERVICE_ID);
       maidData = getArguments().getParcelable(Constants.MAID_DATA);
        Log.d(TAG, "onCreate: maid data:--  "+new Gson().toJson(maidData) );

        if (getArguments().containsKey(Constants.ISFAVOURITE)) {
            maidData.setFavourite(getArguments().getBoolean(Constants.ISFAVOURITE, false));
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMaidprofileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: StartActivity");
        init();
        setData();
        setListeners();
        if (reschuleStatus.equalsIgnoreCase("yes")) {
            binding.tvBook.setText(getResources().getString(R.string.reschedule));
        }
    }


    private void init() {
        setHasOptionsMenu(true);
        presenter = new MaidProfilePresenter();
        presenter.attachView(this);

        String country = Prefs.with(getActivity()).getString(Constants.COUNTRY_NAME, "");
        if (country.contains("United Arab Emirates")) {
            currency = "AED";
        } else {
            currency = "BHD";
        }

    }

    public int getCountOfDays(String createdDateString, String expireDateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM dd", Locale.getDefault());

        Date createdConvertedDate = null, expireCovertedDate = null, todayWithZeroTime = null;
        try {
            createdConvertedDate = dateFormat.parse(createdDateString);
            expireCovertedDate = dateFormat.parse(expireDateString);

            Date today = new Date();

            todayWithZeroTime = dateFormat.parse(dateFormat.format(today));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int cYear = 0, cMonth = 0, cDay = 0;

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

    @SuppressLint("DefaultLocale")
    private void setData() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(binding.toolbar);
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getSupportActionBar().setDisplayShowHomeEnabled(true);
            activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
            activity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_white);
        }

        reviewAdapter = new ReviewAdapter(activity, reviewList);
        binding.rvReviews.setAdapter(reviewAdapter);
        binding.rvReviews.setLayoutManager(new LinearLayoutManager(activity));
        binding.tvBook.setOnClickListener(this);

        if (maidData != null) {
            isFavourite = maidData.isFavourite();
            String firstName = maidData.getFirstName().substring(0, 1).toUpperCase() + maidData.getFirstName().substring(1);
            String lastName = "";
            if (maidData.getLastName() != null && !maidData.getLastName().isEmpty()) {
                lastName = maidData.getLastName().substring(0, 1).toUpperCase() + maidData.getLastName().substring(1);
            }
            binding.tvMaidName.setText(String.format("%s %s", firstName, lastName));
            binding.tvMaidId.setText(maidData.getMakId());
            binding.tvAgencyName.setText(String.format(" %s%s", maidData.getAgencyName().substring(0, 1).toUpperCase(),
                    maidData.getAgencyName().substring(1)));
//            binding.tvAgencyName.setTextColor(getResources().getColor(R.color.colorPrimary));
            String languagee = Prefs.with(getActivity()).getString(Constants.LANGUAGE_CODE, "");
            if (languagee.equalsIgnoreCase("ar")) {
                binding.tvExperience.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);

            } else {
                binding.tvExperience.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                //   viewHolder.tvExperience.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
            binding.tvExperience.setText(String.format("%s", maidData.getExperience()));

           /* if (maidData.getProfilePicURL() != null && maidData.getProfilePicURL().getOriginal() != null
                    && !maidData.getProfilePicURL().getOriginal().isEmpty()) {

                Log.e("image url", maidData.getProfilePicURL().getOriginal());

                Glide.with(getActivity())
                        .load(maidData.getProfilePicURL().getOriginal())
                        .circleCrop()
                        .placeholder(R.drawable.ic_user_pic)
                        .into(binding.ivMaid);
            } else*/ if (maidData.agencyImage != null
                    && maidData.agencyImage.getOriginal() != null && !maidData.agencyImage.getOriginal().isEmpty()) {
                Log.e("image url", maidData.agencyImage.getOriginal());
                Glide.with(getActivity())
                        .load(maidData.agencyImage.getOriginal())
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
            try {
                String languageArray = "";
                for (int i = 0; i < maidData.getLanguages().size(); i++) {
                    String name = maidData.getLanguages().get(i).getLanguageName();
                    languageArray = languageArray + name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();

                    if (i != (maidData.getLanguages().size() - 1)) {
                        languageArray = languageArray + ", ";
                    }
                }
                binding.tvLanguageValue.setText(String.format("%s", languageArray));
                binding.tvLanguageValue.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            }catch (Exception e){
                e.printStackTrace();
            }

            if (maidData.getCurrency().equalsIgnoreCase("BHD")) {
                binding.tvNationality.setVisibility(View.VISIBLE);
                binding.tvNationalityValue.setVisibility(View.VISIBLE);
                binding.tvReligion.setVisibility(View.VISIBLE);
                binding.tvReligionValue.setVisibility(View.VISIBLE);
            }else {
                binding.tvNationality.setVisibility(View.GONE);
                binding.tvNationalityValue.setVisibility(View.GONE);
                binding.tvReligion.setVisibility(View.GONE);
                binding.tvReligionValue.setVisibility(View.GONE);
            }

            if (maidData.getNationality()==null){
                binding.tvNationality.setVisibility(View.GONE);
                binding.tvNationalityValue.setVisibility(View.GONE);
            }else {
                if (maidData.getNationality().equalsIgnoreCase("")){
                    binding.tvNationality.setVisibility(View.GONE);
                    binding.tvNationalityValue.setVisibility(View.GONE);
                }else {
                    binding.tvNationalityValue.setText(String.format("%s", String.valueOf(maidData.getNationality())));
                }
            }

         if (maidData.getNationality()==null){
             binding.tvReligion.setVisibility(View.GONE);
             binding.tvReligionValue.setVisibility(View.GONE);
         }else {
               if ( maidData.religion.equalsIgnoreCase("")){
                binding.tvReligion.setVisibility(View.GONE);
                binding.tvReligionValue.setVisibility(View.GONE);
            }else {
                binding.tvReligionValue.setText(String.format("%s", maidData.religion));
            }

         }


            if (searchMaidModel != null) {
                binding.rlBook.setVisibility(View.VISIBLE);
                binding.tvBook.setVisibility(View.VISIBLE);
                binding.tvHourValue.setText(String.format("%s %s", searchMaidModel.duration, getString(R.string.concat_hour)));

                Float total ;
                Float maidprice=0.0f;
                Float per = (maidData.getActualPrice() / 100.0f) * Float.parseFloat( maidData.getNew_vat() == null ? "0":maidData.getNew_vat());
                Float totalper=maidData.getActualPrice()+per;
              /*  if (Prefs.get().getString(USER_COUNTRY, "").equalsIgnoreCase("Bahrain")) {
                    maidprice=totalper;
                }else {
                    try {
                        maidprice=Float.parseFloat(String.format("%.2f",totalper));
                    }catch (Exception e){

                    }
                }*/

              //  Log.e("vat persenatge value",""+per+ " "+maidprice);
                if (maidData.getCurrency().equalsIgnoreCase("BHD")) {
                    maidprice= Float.parseFloat( String.format (Locale.ENGLISH,"%.3f",(maidData.getActualPrice())));
                }else {
                    maidprice= Float.parseFloat( String.format(Locale.ENGLISH,"%.2f", (maidData.getActualPrice())));
                }

                if (booking_type == 2) {
                    binding.tvTotalDaysValue.setText(selectedDatesList.size() + "");

                    binding.tvTotalDaysValue.setVisibility(View.VISIBLE);
                    binding.tvTotalDays.setVisibility(View.VISIBLE);
                 //   total = (searchMaidModel.duration * maidData.getActualPrice() * selectedDatesList.size());

                   if((maidData.getCurrency().equalsIgnoreCase("BHD"))) {
                       total = Float.parseFloat (String.format(Locale.ENGLISH,"%.3f",(searchMaidModel.duration * maidprice * selectedDatesList.size())));
                   }else{
                       total = Float.parseFloat (String.format(Locale.ENGLISH,"%.2f",(searchMaidModel.duration * maidprice * selectedDatesList.size())));
                   }

                    //

                } else {
                    binding.tvTotalDaysValue.setVisibility(View.GONE);
                    binding.tvTotalDays.setVisibility(View.GONE);
                    //   total = (searchMaidModel.duration * maidData.getActualPrice());
                    if((maidData.getCurrency().equalsIgnoreCase("BHD"))) {
                        total = Float.parseFloat(String.format(Locale.ENGLISH,"%.3f",(searchMaidModel.duration * maidprice)));
                    }else{
                        total = Float.parseFloat(String.format(Locale.ENGLISH,"%.2f",(searchMaidModel.duration * maidprice)));
                    }
                    //
                }
                Log.e("total",""+total);

                if (maidData.getCurrency().equalsIgnoreCase("BHD")) {
                    binding.tvTotalPriceValue.setText(String.format(Locale.US, "%s %.3f", maidData.getCurrency(), total));
                    binding.tvChargesValues.setText(String.format(Locale.US, "%s %.3f", maidData.getCurrency(), maidData.getActualPrice()));
                }else {
                    binding.tvTotalPriceValue.setText(String.format(Locale.US, "%s %.2f", maidData.getCurrency(), total));
                    binding.tvChargesValues.setText(String.format(Locale.US, "%s %.2f", maidData.getCurrency(), maidData.getActualPrice()));
                }

               // binding.tvVatvalue.setText(maidData.getVat()+"%");
                //visibilty gone view start

               /* double per = (total / 100.0f) * Float.parseFloat(maidData.getVat());
                double totalper=total+per;
                binding.tvVatvalue.setText(String.format(Locale.US, "%s %.3f", maidData.getCurrency(),per));
                binding.tvGrandtotalvalue.setText(String.format(Locale.US, "%s %.3f", maidData.getCurrency(), totalper));*/
                //end
            } else {
                binding.rlBook.setVisibility(View.GONE);
                binding.tvBook.setVisibility(View.GONE);
            }

          /*  if (maidData.getDescription() == null || maidData.getDescription().isEmpty()) {
                binding.tvDescription.setText(R.string.no_description);

            } else {
                binding.tvDescription.setText(maidData.getDescription());

            }*/

            binding.tvOverview.setVisibility(View.VISIBLE);
            binding.tvDescription.setVisibility(View.VISIBLE);
//-----------Ironing
            try {//-----------------Rating IRONING
                if (maidData.getAvgIroning() != 0) {
                    binding.rating.llIroning.setVisibility(View.VISIBLE);
                    binding.rating.tvIroning.setText(String.format(Locale.US, "%.1f", maidData.getAvgIroning()));
                } else {
                    binding.rating.llIroning.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                binding.rating.llIroning.setVisibility(View.GONE);
                e.printStackTrace();
                try {//IF FIREBASE RETURN CRASH
                    FirebaseCrashlytics.getInstance().recordException(e);
                } catch (Exception fe) {
                    fe.printStackTrace();
                }
            }

//-------------Cooking
            try {//------------------- Rating Cooking
                if (maidData.getAvgCooking() != 0) {
                    binding.rating.llCooking.setVisibility(View.VISIBLE);
                    binding.rating.tvCooking.setText(String.format(Locale.US, "%.1f", maidData.getAvgCooking()));
                } else {
                    binding.rating.llCooking.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                binding.rating.llCooking.setVisibility(View.GONE);
                e.printStackTrace();
                try {//IF FIREBASE RETURN CRASH
                    FirebaseCrashlytics.getInstance().recordException(e);
                } catch (Exception fe) {
                    fe.printStackTrace();
                }
            }
//--------------Cleaning
            try {
                if (maidData.getAvgCleaning() != 0) {
                    binding.rating.llCleaning.setVisibility(View.VISIBLE);
                    binding.rating.tvCleaning.setText(String.format(Locale.US, "%.1f", maidData.getAvgCleaning()));
                } else {
                    binding.rating.llCleaning.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                binding.rating.llCleaning.setVisibility(View.GONE);
                e.printStackTrace();
                try {//IF FIREBASE RETURN CRASH
                    FirebaseCrashlytics.getInstance().recordException(e);
                } catch (Exception fe) {
                    fe.printStackTrace();
                }
            }

//-------------Child Care
            try {
                if (maidData.getAvgChildCare() != 0) {
                    binding.rating.llChildCare.setVisibility(View.VISIBLE);
                    binding.rating.tvChildCare.setText(String.format(Locale.US, "%.1f", maidData.getAvgChildCare()));
                } else {
                    binding.rating.llChildCare.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                binding.rating.llChildCare.setVisibility(View.GONE);
                e.printStackTrace();
                try {//IF FIREBASE RETURN CRASH
                    FirebaseCrashlytics.getInstance().recordException(e);
                } catch (Exception fe) {
                    fe.printStackTrace();
                }
            }
            Double rating;
            int count = 0;

            if (maidData.getAvgCooking() != 0.0) {
                count++;
            }
            if (maidData.getAvgIroning() != 0.0) {
                count++;
            }
            if (maidData.getAvgCleaning() != 0.0) {
                count++;
            }
            if (maidData.getAvgChildCare() != 0.0) {
                count++;
            }

            rating = (maidData.getAvgCooking()
                    + maidData.getAvgIroning() + maidData.getAvgCleaning()
                    + maidData.getAvgChildCare()) / Double.parseDouble(String.valueOf(count));

            if (rating.isNaN()) {
                rating = 0.0;
            }
           Log.d(TAG, "setData: rating :-   "+ rating +
                   "\nmaidData.getAvgIroning():  " +maidData.getAvgIroning()+
                   "\nmaidData.getAvgCooking():  " +maidData.getAvgCooking()+
                   "\nmaidData.getAvgCleaning():  " +maidData.getAvgCleaning()+
                   "\nmaidData.getAvgChildCare():  " +maidData.getAvgChildCare()
                );
            if (rating != 0.0) {
                binding.rating.llView.setVisibility(View.VISIBLE);
                binding.rating.tvRating.setVisibility(View.VISIBLE);
                binding.rating.tvnoRating.setVisibility(View.GONE);
                binding.rating.tvAvgRating.setText(String.format(Locale.US, "%.1f", rating));
            } else {
                binding.rating.tvRating.setVisibility(View.GONE);
                binding.rating.tvnoRating.setVisibility(View.VISIBLE);
                binding.rating.llView.setVisibility(View.GONE);
            }

        }

        if (!maidID.equals(getArguments().getString(Constants.MAID_ID))) {
            maidID = getArguments().getString(Constants.MAID_ID);
            if (GeneralFunction.isNetworkConnected(getActivity(), binding.parent)) {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.tvReview.setVisibility(View.GONE);
                binding.rvReviews.setVisibility(View.GONE);
                presenter.apiSearchMaid(getArguments().getString(Constants.MAID_ID));
            }
        } else {
            if (pojoMaidProfile != null) {
                getMaidSuccess(pojoMaidProfile);
            }
            else {
                if (GeneralFunction.isNetworkConnected(getActivity(), binding.parent)) {
                    binding.progressBar.setVisibility(View.VISIBLE);
                    binding.tvReview.setVisibility(View.GONE);
                    binding.rvReviews.setVisibility(View.GONE);
                    presenter.apiSearchMaid(getArguments().getString(Constants.MAID_ID));
                }
            }
        }
    }

    private void setListeners() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvBook:
                mFirebaseAnalytics.logEvent("mdscr_click_book", null);

                if (reschuleStatus.equalsIgnoreCase("yes")) {

                    BookServiceModel bookServiceModel = new BookServiceModel();

                    bookServiceModel.bookingId = String.valueOf(bookingDataModel.bookingId);

                    bookServiceModel.uniquieAppKey = Constants.UNIQUE_APP_KEY;
                    bookServiceModel.lng = searchMaidModel.lng;
                    bookServiceModel.lat = searchMaidModel.lat;
                    bookServiceModel.serviceId = bookingDataModel._id;
                    bookServiceModel.hour = String.valueOf(searchMaidModel.hour);
                    bookServiceModel.deviceTimeZone = TimeZone.getDefault().getID();
                    bookServiceModel.timeZone = "Asia/Bahrain";

                    BookServiceModel.ServiceData serviceData = new BookServiceModel.ServiceData();
                    serviceData.startTime = String.valueOf(searchMaidModel.startTime);
                    serviceData.workDate = String.valueOf(searchMaidModel.workDate);
                    serviceData.duration = searchMaidModel.duration;

                    serviceData.hour = String.valueOf(searchMaidModel.hour);

                    bookServiceModel.serviceData.add(serviceData);
                    bookServiceModel.maidId = searchMaidModel.maidId;
                    bookServiceModel.locationName = searchMaidModel.locationName;
                     // Bulk Booking again
                    if (booking_type == 3) {
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("agencyId", String.valueOf(bookingDataModel.agencyId));
                        hashMap.put("hour", String.valueOf(bookingDataModel.hour));
                        hashMap.put("uniquieAppKey", bookingDataModel.uniquieAppKey);
                        hashMap.put("startTime", String.valueOf(bookingDataModel.startTime));
                        hashMap.put("serviceId", bookingDataModel._id);
                        hashMap.put("workDate", String.valueOf(bookingDataModel.workDate));
                        presenter.apiRescheduleBulk(hashMap, Prefs.with(getActivity()).getString(Constants.ACCESS_TOKEN, ""), reschuleStatus);
                    } else {
                       // hit api one day book service again
                        presenter.apiBookServiceAgain(bookServiceModel, Prefs.with(getActivity()).getString(Constants.ACCESS_TOKEN, ""), reschuleStatus);
                    }
                } else {
                    // Service id chk and change layout
                    if(!serviceId.isEmpty() && serviceId!=null){
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,
                                        R.anim.slide_in_left, R.anim.slide_out_right)
                                .replace(android.R.id.content, ConfirmBookFragment.newInstance(searchMaidModel,/*maidData.getVat()*/ maidData.getNew_vat() /*== null ? maidData.getVat():maidData.getNew_vat()*/,serviceId))
                                .addToBackStack("ConfirmBookFragment").commit();
                    }else {
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,
                                        R.anim.slide_in_left, R.anim.slide_out_right)
                                .replace(R.id.frameLayout, ConfirmBookFragment.newInstance(searchMaidModel,maidData.getNew_vat()/*getVat()*/))
                                .addToBackStack("ConfirmBookFragment").commit();
                    }
                }
                break;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            getActivity().getSupportFragmentManager().popBackStack(); // close this activity and return to preview activity (if there is any)
        } else if (item.getItemId() == R.id.action_favourite) {
            mFirebaseAnalytics.logEvent("mdscr_tap_heart", null);
            if (isFavourite) {
                presenter.apiRemoveFavouriteMaid(maidID);
            } else {
                presenter.apiAddFavouriteMaid(maidID);
            }
        }
        return true;
    }

    @Override
    public void setLoading(boolean isLoading) {
        if (isLoading)
            GeneralFunction.showProgress(getActivity());
        else
            GeneralFunction.dismissProgress();
    }


    void setNumberOfbookings(int booking){
       // binding.tvnobooking.setText(profileData.getTotalService());
      if(booking >= 10){
          binding.tvnobooking.setText("10+ bookings");
      }
      if(booking>= 50){
          binding.tvnobooking.setText("50+ bookings");
      }
        if(booking>=100){
            binding.tvnobooking.setText("100+ bookings");
        }

    }

    @Override
    public void sessionExpired() {
        GeneralFunction.isUserBlocked(getActivity());
    }

    @Override
    public void getMaidSuccess(@NonNull PojoMaidProfile profileData) {

        Log.d(TAG, "getMaidSuccess:  "+new Gson().toJson( profileData));
        this.pojoMaidProfile = profileData;

        if (searchMaidModel != null) {
            searchMaidModel.maidName = binding.tvMaidName.getText().toString();
            searchMaidModel.maidPrice = profileData.actualPrice;
            searchMaidModel.agencyName = binding.tvAgencyName.getText().toString();
            searchMaidModel.maidId = profileData.get_id();
            try {//------------------Experiance exception handle
                if(profileData.getExperience() == null || profileData.getExperience().isEmpty() ){
                    binding.tvExperiencelay.setVisibility(View.GONE);
                }else {
                    binding.tvExperiencelay.setVisibility(View.VISIBLE);
                    binding.tvExperience.setText(String.format("%s", profileData.getExperience()));
                }
            }catch (Exception e){
                binding.tvExperiencelay.setVisibility(View.GONE);
                e.printStackTrace();
            }
             try {//---------------------Exception handle number of booking------
                 Log.d(TAG, "getMaidSuccess: number of booking "+profileData.getTotalService() );
                 if (Integer.parseInt(profileData.getTotalService()) >= 10) {
                     binding.tvnobookihlay.setVisibility(View.VISIBLE);
                    setNumberOfbookings(Integer.parseInt(profileData.getTotalService()));
                 } else {
                     binding.tvnobookihlay.setVisibility(View.GONE);
                 }
             } catch (Exception e) {
                 binding.tvnobookihlay.setVisibility(View.GONE);
                 e.printStackTrace();
             }

            searchMaidModel.vat = profileData.getVat() == null ? "0" : profileData.getVat();
            searchMaidModel.new_vat = profileData.getNew_vat() == null ? "0" : profileData.getNew_vat();
            /*if (profileData.profilePicURL != null && profileData.profilePicURL.getOriginal() != null
                    && !profileData.profilePicURL.getOriginal().isEmpty()) {
                searchMaidModel.profilePicURL = profileData.profilePicURL;
            } else */if (profileData.agencyImage != null
                    && profileData.agencyImage.getOriginal() != null && !profileData.agencyImage.getOriginal().isEmpty()) {
                searchMaidModel.profilePicURL = profileData.agencyImage;
            }
            searchMaidModel.documentPicURL = profileData.documentPicURL;
            searchMaidModel.contactNo = profileData.countryCode + "-" + profileData.phoneNo;
            searchMaidModel.makId = profileData.makId;
        }
            String languagee = Prefs.with(getActivity()).getString(Constants.LANGUAGE_CODE, "");
            if (languagee.equalsIgnoreCase("ar")) {
                binding.tvExperience.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);

            } else {
                binding.tvExperience.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                //   viewHolder.tvExperience.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
            binding.tvAboutMeDesc.setVisibility(View.VISIBLE);
            binding.tvAboutMe.setVisibility(View.VISIBLE);
            if (profileData.getDescription() == null || profileData.getDescription().isEmpty()) {
                binding.tvAboutMeDesc.setText(R.string.no_description);
            } else {
                binding.tvAboutMeDesc.setText(profileData.getDescription());
            }
            String jobService = "";
            if(profileData.servicesProvide!=null && !profileData.servicesProvide.isEmpty()) {
                for (ServicesProvide service : profileData.servicesProvide) {
                    if (service != null) {
                        if (!jobService.isEmpty()) {
                            jobService =jobService + " , ";
                            jobService = jobService + service.getName();
                        } else {
                            jobService = jobService + service.getName();
                        }
                    }
                }

                binding.tvOverview.setVisibility(View.VISIBLE);
                binding.llBasicInfo.setVisibility(View.VISIBLE);
                binding.tvDescription.setVisibility(View.VISIBLE);
                binding.tvDescription.setText(jobService);
            }








        binding.tvReview.setText(R.string.no_review);
        if (profileData.review == null || profileData.review.size() == 0) {
            binding.tvReview.setText(R.string.no_review);
            reviewList.clear();
            binding.rvReviews.setVisibility(View.GONE);
        } else {
            binding.tvReview.setText(R.string.reviews);
            reviewList.clear();
            for (int i = 0; i < profileData.getReview().size(); i++) {
                Double ratingg;


                int count = 0;

                if (profileData.getAvgCooking() != 0.0) {
                    count++;
                }
                if (profileData.getAvgIroning() != 0.0) {
                    count++;
                }
                if (profileData.getAvgCleaning() != 0.0) {
                    count++;
                }
                if (profileData.getAvgChildCare() != 0.0) {
                    count++;
                }

                ratingg = (profileData.getReview().get(i).maidRating.cooking
                        + profileData.getReview().get(i).maidRating.ironing + profileData.getReview().get(i).maidRating.cleaning
                        + profileData.getReview().get(i).maidRating.childCare) / Double.parseDouble(String.valueOf(count));
                if (ratingg.isNaN()) {
                    ratingg = 0.0;
                }

                if (ratingg != 0.0) {
                    reviewList.add(profileData.getReview().get(i));
                } else {

                }

            }
            if (reviewList.size() == 0) {
                binding.tvReview.setVisibility(View.GONE);
            } else {
                binding.tvReview.setVisibility(View.GONE);
            }

            reviewAdapter.notifyDataSetChanged();
            binding.rvReviews.setVisibility(View.VISIBLE);
        }
//---------IRONING
        try {
            if (profileData.avgIroning != 0) {
                binding.rating.llIroning.setVisibility(View.VISIBLE);
                binding.rating.tvIroning.setText(String.format(Locale.US, "%.1f", profileData.avgIroning));
            } else {
                binding.rating.llIroning.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            binding.rating.llIroning.setVisibility(View.GONE);
            e.printStackTrace();
            try {//IF FIREBASE RETURN CRASH
                FirebaseCrashlytics.getInstance().recordException(e);
            } catch (Exception fe) {
                fe.printStackTrace();
            }
        }
//---------COOKING
        try {
            if (profileData.avgCooking != 0) {
                binding.rating.llCooking.setVisibility(View.VISIBLE);
                binding.rating.tvCooking.setText(String.format(Locale.US, "%.1f", profileData.avgCooking));
            }else{
                binding.rating.llCooking.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            binding.rating.llCooking.setVisibility(View.GONE);
            e.printStackTrace();
            try {//IF FIREBASE RETURN CRASH
                FirebaseCrashlytics.getInstance().recordException(e);
            } catch (Exception fe) {
                fe.printStackTrace();
            }
        }
///--------CLEANING
        try {
            if( profileData.avgCleaning != 0) {
                binding.rating.llCleaning.setVisibility(View.VISIBLE);
                binding.rating.tvCleaning.setText(String.format(Locale.US, "%.1f", profileData.avgCleaning));
            }else{
                binding.rating.llCleaning.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            binding.rating.llCleaning.setVisibility(View.GONE);
            e.printStackTrace();
            try {//IF FIREBASE RETURN CRASH
                FirebaseCrashlytics.getInstance().recordException(e);
            } catch (Exception fe) {
                fe.printStackTrace();
            }
        }

//--------CHILD CARE
        try {
            if(profileData.avgChildCare != 0) {
                binding.rating.llChildCare.setVisibility(View.VISIBLE);
                binding.rating.tvChildCare.setText(String.format(Locale.US, "%.1f", profileData.avgChildCare));
            }else{
                binding.rating.llChildCare.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            binding.rating.llChildCare.setVisibility(View.GONE);
            e.printStackTrace();
            try {//IF FIREBASE RETURN CRASH
                FirebaseCrashlytics.getInstance().recordException(e);
            } catch (Exception fe) {
                fe.printStackTrace();
            }
        }
        Double rating;
        int count = 0;

        if (profileData.avgCooking != 0.0) {
            count++;
        }
        if (profileData.avgIroning != 0.0) {
            count++;
        }
        if (profileData.avgCleaning != 0.0) {
            count++;
        }
        if (profileData.avgChildCare != 0.0) {
            count++;
        }

        rating = (profileData.avgCooking
                + profileData.avgIroning + profileData.avgCleaning
                + profileData.avgChildCare) / Double.parseDouble(String.valueOf(count));

        if (rating.isNaN()) {
            rating = 0.0;
        }

       // binding.rating.tvAvgRating.setText(String.format(Locale.US, "%.1f", rating));

        Log.d(TAG, "setData: rating :-   "+ rating +
                "\nprofileData.getAvgIroning():  " +profileData.getAvgIroning()+
                "\nprofileData.getAvgCooking():  " +profileData.getAvgCooking()+
                "\nprofileData.getAvgCleaning():  " +profileData.getAvgCleaning()+
                "\nprofileData.getAvgChildCare():  " +profileData.getAvgChildCare()
        );
        if (rating != 0.0) {
            binding.rating.llView.setVisibility(View.VISIBLE);
            binding.rating.tvRating.setVisibility(View.VISIBLE);

            binding.rating.tvnoRating.setVisibility(View.GONE);
            binding.rating.tvAvgRating.setText(String.format(Locale.US, "%.1f", rating));
        } else {
            binding.rating.tvRating.setVisibility(View.GONE);
            binding.rating.tvnoRating.setVisibility(View.VISIBLE);
            binding.rating.llView.setVisibility(View.GONE);
        }

        //------------------Add is favourate
        isFavourite = profileData.isFavourite();

        getActivity().invalidateOptionsMenu();

        binding.progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        if (searchMaidModel == null) {
            menu.findItem(R.id.action_menu).setVisible(false);
        }

        MenuItem searchItem = menu.findItem(R.id.action_favourite);
        if (isFavourite) {
            searchItem.setIcon(R.drawable.ic_heart_white_filled);
        } else {
            searchItem.setIcon(R.drawable.ic_heart_white_empty);
        }

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void successAddMaid() {
        isFavourite = !isFavourite;
        maidData.setFavourite(isFavourite);
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void successRemoveMaid() {
        isFavourite = !isFavourite;
        maidData.setFavourite(isFavourite);
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void getMaidError(String errMsg) {
        new DialogPopup().alertPopup(getActivity(), getResources().getString(R.string.dialog_alert), errMsg, "").show(requireActivity().getSupportFragmentManager(), "ios_dialog");;
    }

    @Override
    public void getMaidFailure(String failureMessage) {
        Log.e(TAG, "getMaidFailure: " + failureMessage);
        new DialogPopup().alertPopup(getActivity(), getResources().getString(R.string.dialog_alert), getString(R.string.check_connection), "").show(requireActivity().getSupportFragmentManager(), "ios_dialog");;
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


        AlertDialog.Builder builder1 = new AlertDialog.Builder(requireContext());

        builder1.setMessage(title + "\n" +message);

        builder1.setCancelable(false);

        builder1.setPositiveButton(
                R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        builder1.setNegativeButton(
                R.string.no,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });


        dialog = builder1.create();
        dialog.show();



        return dialog;
    }

    public Dialog alertReschduleSuccess(final Activity activity, String title, String message, final String customMessage) {
        Dialog dialog = null;



        AlertDialog.Builder builder1 = new AlertDialog.Builder(requireContext());

        builder1.setMessage(title + "\n" +message);

        builder1.setCancelable(false);

        builder1.setPositiveButton(
                R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        ConfirmBookFragment.road_temp = "";
                        ConfirmBookFragment.city_temp = "";
                        ConfirmBookFragment.block_temp = "";
                        ConfirmBookFragment.apartment_temp = "";
                        ConfirmBookFragment.full_address_temp = "";
                        ConfirmBookFragment.latLng = null;
                        getActivity().finishAffinity();
                        startActivity(new Intent(getActivity(), Main2Activity.class));
                       dialog.cancel();
                    }
                });

        builder1.setNegativeButton(
                R.string.no,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });


        dialog = builder1.create();
        dialog.show();


      /*  try {

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

            ok.setText(getResources().getString(R.string.ok));
            cancel.setText(getResources().getString(R.string.no));

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
                    getActivity().finishAffinity();
                    startActivity(new Intent(getActivity(), Main2Activity.class));
                    finalDialog.dismiss();
                }
            });
            dialog.show();


        } catch (Exception e) {
            e.printStackTrace();
        }*/

        return dialog;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.detachView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_favourite, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }




}
