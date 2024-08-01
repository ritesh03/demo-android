package com.maktoday.views.paymentinfo;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.maktoday.R;
import com.maktoday.databinding.FragmentPaymentinfoBinding;
import com.maktoday.model.FullAddress;
import com.maktoday.model.SearchMaidBulkModel;
import com.maktoday.model.SearchMaidModel;
import com.maktoday.utils.BaseFragment;
import com.maktoday.utils.Constants;
import com.maktoday.utils.DataVariable;
import com.maktoday.utils.GeneralFunction;
import com.maktoday.utils.Log;
import com.maktoday.utils.Prefs;
import com.maktoday.views.PickerShowActivity;
import com.maktoday.views.cardlist.CardListFragment;
import com.maktoday.views.confirmbook.ConfirmBookFragment;
import com.maktoday.views.main.Main2Activity;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import static com.maktoday.views.home.HomeFragment.booking_type;
import static com.maktoday.views.home.HomeFragment.selectedDatesList;

/**
 * Created by cbl81 on 26/10/17.
 */

public class PaymentInfoFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = "PaymentInfoFragment";
    private FragmentPaymentinfoBinding binding;
    private SearchMaidModel searchMaidModel;
    private SearchMaidBulkModel searchMaidBulkModel;
    private String currency = "";
    private FullAddress address;
    double totalper=0.0;
    double per=0.0;

    public static PaymentInfoFragment newInstance(SearchMaidModel searchMaidModel, String transactionId, FullAddress address) {

        Bundle args = new Bundle();
        PaymentInfoFragment fragment = new PaymentInfoFragment();
        args.putParcelable(Constants.SEARCH_MAID_DATA, searchMaidModel);
        args.putParcelable(Constants.ADDRESS, address);
        args.putString("ID", transactionId);
        fragment.setArguments(args);
        return fragment;
    }

    public static PaymentInfoFragment newInstance(SearchMaidBulkModel searchMaidModel, String transactionId, FullAddress address) {

        Bundle args = new Bundle();
        PaymentInfoFragment fragment = new PaymentInfoFragment();
        args.putParcelable(Constants.SEARCH_MAID_DATA, searchMaidModel);
        args.putParcelable(Constants.ADDRESS, address);
        args.putString("ID", transactionId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataVariable.hideSoftKeyboard(getActivity());
        if (booking_type == 3) {
            searchMaidBulkModel = getArguments().getParcelable(Constants.SEARCH_MAID_DATA);
        } else {
            searchMaidModel = getArguments().getParcelable(Constants.SEARCH_MAID_DATA);
        }
        address = getArguments().getParcelable(Constants.ADDRESS);

    }

    @Override
    public void onResume() {
        super.onResume();
        CardListFragment.referenceId = "";
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPaymentinfoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
       Log.d(TAG, "onViewCreated: StartActivity");
        init();
        if (booking_type == 3) {
            setBulkData();
        } else {
           setData();
        }
        setListeners();
    }


    private void init() {
        showDialog();
        binding.tvShowAll.setVisibility(View.GONE);

        String country = Prefs.with(getActivity()).getString(Constants.COUNTRY_NAME, "");

     /*   if (country.contains("United Arab Emirates")) {
            currency = "AED";
        } else {
            currency = "BHD";
        }*/

        final AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }

    private void showDialog() {
        final Dialog openDialog = new Dialog(getContext(), R.style.full_screen_dialog);
        openDialog.setContentView(R.layout.dialog_booking);

        TextView ok = openDialog.findViewById(R.id.tvOk);
        TextView bookingNo = openDialog.findViewById(R.id.booking_no);
        bookingNo.setText(getArguments().getString("ID"));
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFirebaseAnalytics.logEvent("thnscr_click_ok", null);
                // TODO Auto-generated method stub
                openDialog.dismiss();
                DataVariable.hideSoftKeyboard(getActivity());
            }
        });
        openDialog.show();
    }

    private void setData() {
       /* if (searchMaidModel.profilePicURL != null && searchMaidModel.profilePicURL.getOriginal() != null) {
            Glide.with(getActivity())
                    .load(searchMaidModel.profilePicURL.getThumbnail())
                    .circleCrop()
                    .placeholder(R.drawable.id_card)
                    .into(binding.ivCardImage);

        }*/
        if (searchMaidModel.profilePicURL != null && searchMaidModel.profilePicURL.getOriginal() != null) {
            Glide.with(getActivity())
                    .load(searchMaidModel.profilePicURL.getThumbnail())
                    .circleCrop()
                    .placeholder(R.drawable.id_card)
                    .into(binding.ivCardImage);

        }else {
            Glide.with(getActivity())
                    .load(R.drawable.id_card)
                    .placeholder(R.drawable.id_card)
                    .into(binding.ivCardImage);
        }
        String language = Prefs.with(getActivity()).getString(Constants.LANGUAGE_CODE, "");

        if (language.equalsIgnoreCase("ar")) {
            binding.tvAgencyNameValue.setGravity(Gravity.START);

        } else {
            binding.tvAgencyNameValue.setGravity(Gravity.END);
        }

        setStringStyle(String.format("%s", searchMaidModel.maidName), binding.tvMaidNameValue);
        setStringStyle(String.format("%s", searchMaidModel.makId), binding.tvMaidIdValue);
        setStringStyle(String.format("%s", searchMaidModel.agencyName), binding.tvAgencyNameValue);
        binding.tvMaidIdValue.setVisibility(View.VISIBLE);
        binding.tvMaidId.setVisibility(View.VISIBLE);
        setStringStyle(String.format("%s", searchMaidModel.selectedTime), binding.tvBookingTimeValue);

        setStringStyle(String.format("%d %s", searchMaidModel.duration, getString(R.string.hours)), binding.tvBookingDurationValue);

        if (address != null) {
            if (searchMaidModel.currency.equalsIgnoreCase("BHD")){
                if (address.buildingNumber.isEmpty()){
                    binding.tvBuildingnumber.setVisibility(View.GONE);
                    binding.tvBuilding.setVisibility(View.GONE);
                }else{
                    binding.tvBuildingnumber.setVisibility(View.VISIBLE);
                    binding.tvBuilding.setVisibility(View.VISIBLE);
                }
                setStringStyle(String.format("%s", address.villaName), binding.tvVillaNoValue);
                setStringStyle(String.format("%s", address.buildingNumber), binding.tvBuildingnumber);
                setStringStyle(String.format("%s", address.buildingName), binding.tvBlockNumberValue);
                setStringStyle(String.format("%s", address.streetName), binding.tvRoadNoValue);
            }else {
                if (address.buildingName.isEmpty()){
                    binding.tvVillaNo.setVisibility(View.GONE);
                    binding.tvVillaNoValue.setVisibility(View.GONE);
                }else {
                    binding.tvVillaNo.setVisibility(View.VISIBLE);
                    binding.tvVillaNoValue.setVisibility(View.VISIBLE);
                }
                binding.tvVillaNo.setText(getString(R.string.label_flat_2));
                binding.tvBlockNumber.setText(getString(R.string.label_building_number_uk));
                binding.tvBuilding.setText(getString(R.string.label_street_2));
                binding.tvRoadNo.setText(getString(R.string.label_postalcode_2));
                setStringStyle(String.format("%s", address.buildingName), binding.tvVillaNoValue);
                setStringStyle(String.format("%s", address.streetName), binding.tvBuildingnumber);
                setStringStyle(String.format("%s", address.buildingNumber), binding.tvBlockNumberValue);
                setStringStyle(String.format("%s", address.postalCode), binding.tvRoadNoValue);
            }


            setStringStyle(String.format("%s", address.city), binding.tvCityValue);
            if (address.moreDetailedaddress != null && !address.moreDetailedaddress.equals("")) {
                binding.tvAdditionalDetails.setVisibility(View.GONE);
                binding.tvAdditionalDetailsValue.setVisibility(View.GONE);
                setStringStyle(String.format("%s", address.moreDetailedaddress), binding.tvAdditionalDetailsValue);
            } else {
                binding.tvAdditionalDetails.setVisibility(View.GONE);
                binding.tvAdditionalDetailsValue.setVisibility(View.GONE);

            }

        }
        double total;
        if (booking_type == 2) {
            binding.tvShowAll.setVisibility(View.VISIBLE);

            Timestamp ts = new Timestamp(searchMaidModel.workDate);
            Date date = new Date(ts.getTime());
            System.out.println(date);
            Log.e("start date", date + "");

            Timestamp tss = new Timestamp(searchMaidModel.endDate);
            Date datee = new Date(tss.getTime());
            System.out.println(datee);
            Log.e("end date", datee + "");
            Log.e("dates", getDatesBetweenUsingJava7(date, datee) + "");

            Collections.reverse(selectedDatesList);
            String temp_dates = "";
            for (int i = 0; i < selectedDatesList.size(); i++) {
                //   temp_dates = GeneralFunction.getFormatFromDate(selectedDatesList.get(i), "EEEE, MMMM dd") + ","+temp_dates;
                temp_dates = GeneralFunction.getFormatFromDate(selectedDatesList.get(i), "dd MMM") + ", " + temp_dates;
            }

            Log.e("temp_dates", "" + temp_dates);

            setStringStyle(String.format("%s", temp_dates.substring(0, temp_dates.length() - 2)), binding.tvBookingDateValue);

            Log.e("dayss", "" + selectedDatesList.size());

            total =searchMaidModel.maidPrice* searchMaidModel.duration * selectedDatesList.size();

            binding.tvTotalDayss.setVisibility(View.VISIBLE);
            binding.tvTotalDayssValue.setVisibility(View.VISIBLE);
            binding.tvTotalDayssValue.setText(selectedDatesList.size() + "");

          //  String language = Prefs.with(getActivity()).getString(Constants.LANGUAGE_CODE, "");
            if (language.equalsIgnoreCase("ar")) {

                binding.tvBookingDateValue.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
            } else {

                binding.tvBookingDateValue.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
            }

        } else {
            binding.tvShowAll.setVisibility(View.GONE);
            binding.tvTotalDayss.setVisibility(View.GONE);
            binding.tvTotalDayssValue.setVisibility(View.GONE);
            setStringStyle(String.format("%s", searchMaidModel.selectedDate), binding.tvBookingDateValue);
            total = searchMaidModel.maidPrice * searchMaidModel.duration;
            //total =ConfirmBookFragment.maidprice * searchMaidModel.duration;
        }


        if (ConfirmBookFragment.per==0.0 && ConfirmBookFragment.Promo_discount.isEmpty()){
            binding.tvVatAmountValue.setVisibility(View.GONE);
            binding.tvVat.setVisibility(View.GONE);
            binding.tvDiscountAmount.setVisibility(View.GONE);
            binding.tvDiscountAmountValue.setVisibility(View.GONE);
            binding.tvFinalPrice.setVisibility(View.GONE);
            binding.tvfinalpriceValue.setVisibility(View.GONE);
            //binding.tvDiscountprice.setVisibility(View.GONE);
            // binding.tvDiscountpriceValue.setVisibility(View.GONE);
            binding.tvTotalAmount.setText(getString(R.string.total_amount_caps));
        }else if (ConfirmBookFragment.per!=0.0 && !ConfirmBookFragment.Promo_discount.isEmpty() ) {
            binding.tvVatAmountValue.setVisibility(View.GONE);
            binding.tvVat.setVisibility(View.GONE);
            binding.tvDiscountAmount.setVisibility(View.GONE);
            binding.tvDiscountAmountValue.setVisibility(View.GONE);
            binding.tvFinalPrice.setVisibility(View.GONE);
            binding.tvfinalpriceValue.setVisibility(View.GONE);
            //binding.tvDiscountprice.setVisibility(View.VISIBLE);
            //binding.tvDiscountpriceValue.setVisibility(View.VISIBLE);
            binding.tvTotalAmount.setText(getString(R.string.total_amount_caps)+" (excl. VAT)");
            binding.tvVat.setText(getString(R.string.vat)+" ("+"@"+ConfirmBookFragment.vat_value+"%"+")");
            binding.tvFinalPrice.setText(getString(R.string.final_price_caps)+" (incl. VAT)");
        } else if (!ConfirmBookFragment.Promo_discount.isEmpty()){
            binding.tvDiscountAmount.setVisibility(View.GONE);
            binding.tvDiscountAmountValue.setVisibility(View.GONE);
            binding.tvFinalPrice.setVisibility(View.GONE);
            binding.tvfinalpriceValue.setVisibility(View.GONE);
            //binding.tvDiscountprice.setVisibility(View.VISIBLE);
            //binding.tvDiscountpriceValue.setVisibility(View.VISIBLE);
            binding.tvVatAmountValue.setVisibility(View.GONE);
            binding.tvVat.setVisibility(View.GONE);
        }else if (ConfirmBookFragment.per!=0.0){
            binding.tvVatAmountValue.setVisibility(View.GONE);
            binding.tvVat.setVisibility(View.GONE);
            binding.tvDiscountAmount.setVisibility(View.GONE);
            binding.tvDiscountAmountValue.setVisibility(View.GONE);
             binding.tvFinalPrice.setVisibility(View.GONE);
           binding.tvfinalpriceValue.setVisibility(View.GONE);
            //binding.tvDiscountprice.setVisibility(View.VISIBLE);
            //binding.tvDiscountpriceValue.setVisibility(View.VISIBLE);
            binding.tvTotalAmount.setText(getString(R.string.total_amount_caps)+" (excl. VAT)");
            binding.tvVat.setText(getString(R.string.vat)+" ("+"@"+ConfirmBookFragment.vat_value+"%"+")");
            binding.tvFinalPrice.setText(getString(R.string.final_price_caps)+" (incl. VAT)");

        }
        /* binding.tvVat.setText(getString(R.string.vat)+" ( at "+(ConfirmBookFragment.vat_value)+"%)");
         binding.tvDiscountAmount.setText(getString(R.string.label_discount)+" ( at 10%)");*/
       /* per = (total / 100.0) * Float.parseFloat(ConfirmBookFragment.vat_value);
        totalper=total+per;*/

        if (searchMaidModel.currency.equalsIgnoreCase("BHD")) {
            binding.tvVatAmountValue.setText(String.format(Locale.ENGLISH, "%s %.3f ", searchMaidModel.currency,ConfirmBookFragment.per));
            binding.tvTotalAmountValue.setText(String.format(Locale.ENGLISH, "%s %.3f", searchMaidModel.currency, total));
            setStringStyle(String.format(Locale.ENGLISH, "%s %.3f ", searchMaidModel.currency,searchMaidModel.maidPrice), binding.tvRateValue);
            if (ConfirmBookFragment.Promo_discount.isEmpty() || ConfirmBookFragment.Promo_discount == null) {
               /*
                binding.tvGrandtotalvalue.setText(String.format(Locale.ENGLISH, "%s %.3f ", searchMaidModel.currency,totalper));*/
                binding.tvfinalpriceValue.setText(String.format(Locale.ENGLISH, "%s %.3f ", searchMaidModel.currency, ConfirmBookFragment.totalper));

            }else {
                binding.tvfinalpriceValue.setText(String.format(Locale.ENGLISH, " %s %.3f", searchMaidModel.currency,ConfirmBookFragment.totalper));
                binding.tvDiscountAmountValue.setText(String.format(Locale.ENGLISH, "%s %s%.3f", searchMaidModel.currency, "-", Double.parseDouble(ConfirmBookFragment.Promo_discount)));
            }

        }else {

            binding.tvVatAmountValue.setText(String.format(Locale.ENGLISH, "%s %.2f ", searchMaidModel.currency,ConfirmBookFragment.per));
            binding.tvTotalAmountValue.setText(String.format(Locale.ENGLISH, "%s %.2f", searchMaidModel.currency, total));
            setStringStyle(String.format(Locale.ENGLISH, "%s %.2f", searchMaidModel.currency, searchMaidModel.maidPrice), binding.tvRateValue);
            if (ConfirmBookFragment.Promo_discount.isEmpty()|| ConfirmBookFragment.Promo_discount == null) {
             /*   binding.tvGrandtotalvalue.setText(String.format(Locale.ENGLISH, "%s %.2f ", searchMaidModel.currency,totalper));*/
                binding.tvfinalpriceValue.setText(String.format(Locale.ENGLISH, "%s %.2f ", searchMaidModel.currency, ConfirmBookFragment.totalper));
            }else {
                binding.tvfinalpriceValue.setText(String.format(Locale.ENGLISH, "%s %.2f", searchMaidModel.currency,ConfirmBookFragment.totalper));
                binding.tvDiscountAmountValue.setText(String.format(Locale.ENGLISH, "%s %s%.2f", searchMaidModel.currency, "-", Double.parseDouble(ConfirmBookFragment.Promo_discount)));

            }
        }
     binding.tvFinalPrice.setVisibility(View.GONE);
        binding.tvfinalpriceValue.setVisibility(View.GONE);
        binding.tvVatAmountValue.setVisibility(View.GONE);
        binding.tvVat.setVisibility(View.GONE);
        binding.tvTotalAmountValue.setVisibility(View.GONE);
        binding.tvTotalAmount.setVisibility(View.GONE);

    }

    public static List<Date> getDatesBetweenUsingJava7(
            Date startDate, Date endDate) {
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

    private void setBulkData() {
        Float total = 0.0F;
        binding.tvShowAll.setVisibility(View.GONE);
        try {
            if (searchMaidBulkModel.profilePicURL != null && searchMaidBulkModel.profilePicURL.getOriginal() != null) {
                /*Glide.with(getActivity())
                        .load(searchMaidBulkModel.profilePicURL.getOriginal())
                        .placeholder(R.drawable.id_card)
                        .into(binding.ivCardImage);*/
                Glide.with(getActivity())
                        .load(searchMaidBulkModel.profilePicURL.getThumbnail())
                        .circleCrop()
                        .placeholder(R.drawable.id_card)
                        .into(binding.ivCardImage);
            } else {
                Glide.with(getActivity())
                        .load(R.drawable.id_card)
                        .placeholder(R.drawable.id_card)
                        .into(binding.ivCardImage);
            }
            setStringStyle(String.format("%s", searchMaidBulkModel.makId), binding.tvMaidIdValue);
            setStringStyle(String.format("%s", getString(R.string.no_maids)), binding.tvTotalDayss);
            setStringStyle(String.format("%s", searchMaidBulkModel.maidCount), binding.tvTotalDayssValue);
            setStringStyle(String.format("%s", searchMaidBulkModel.agencyName), binding.tvAgencyNameValue);
            setStringStyle(String.format("%s", searchMaidBulkModel.selectedTime), binding.tvBookingTimeValue);
            setStringStyle(String.format("%s", searchMaidBulkModel.selectedDate), binding.tvBookingDateValue);
            setStringStyle(String.format("%d %s", searchMaidBulkModel.duration, getString(R.string.hours)), binding.tvBookingDurationValue);
            Log.e("Duration",""+searchMaidBulkModel.duration+"   "+searchMaidBulkModel.maidCount);
            total = searchMaidBulkModel.maidPrice*searchMaidBulkModel.duration* searchMaidBulkModel.maidCount;
        }catch (Exception e){

        }

        binding.tvMaidIdValue.setVisibility(View.GONE);
        binding.tvMaidId.setVisibility(View.GONE);
        binding.tvMaidNameValue.setVisibility(View.GONE);
        binding.tvMaidName.setVisibility(View.GONE);
        binding.tvMaidIdValue.setVisibility(View.GONE);
        binding.tvTotalDayss.setVisibility(View.VISIBLE);
        binding.tvTotalDayssValue.setVisibility(View.VISIBLE);



      //  setStringStyle(String.format(Locale.ENGLISH, "%s %.3f ", currency, searchMaidBulkModel.maidPrice), binding.tvRateValue);


        if (address != null) {
            if (searchMaidBulkModel.currency.equalsIgnoreCase("BHD")){
                setStringStyle(String.format("%s", address.villaName), binding.tvVillaNoValue);
                setStringStyle(String.format("%s", address.buildingNumber), binding.tvBuildingnumber);
                setStringStyle(String.format("%s", address.buildingName), binding.tvBlockNumberValue);
                setStringStyle(String.format("%s", address.streetName), binding.tvRoadNoValue);
            }else {
                if (address.buildingName.isEmpty()){
                    binding.tvVillaNo.setVisibility(View.GONE);
                    binding.tvVillaNoValue.setVisibility(View.GONE);
                }else {
                    binding.tvVillaNo.setVisibility(View.VISIBLE);
                    binding.tvVillaNoValue.setVisibility(View.VISIBLE);
                }
                binding.tvVillaNo.setText(getString(R.string.label_flat_2));
                binding.tvBlockNumber.setText(getString(R.string.label_building_number_uk));
                binding.tvBuilding.setText(getString(R.string.label_street_2));
                binding.tvRoadNo.setText(getString(R.string.label_postalcode_2));
                setStringStyle(String.format("%s", address.buildingName), binding.tvVillaNoValue);
                setStringStyle(String.format("%s", address.streetName), binding.tvBuildingnumber);
                setStringStyle(String.format("%s", address.buildingNumber), binding.tvBlockNumberValue);
                setStringStyle(String.format("%s", address.postalCode), binding.tvRoadNoValue);

            }

            setStringStyle(String.format("%s", address.city), binding.tvCityValue);
            if (address.moreDetailedaddress != null && !address.moreDetailedaddress.equals("")) {
                binding.tvAdditionalDetails.setVisibility(View.VISIBLE);
                binding.tvAdditionalDetailsValue.setVisibility(View.VISIBLE);
                setStringStyle(String.format("%s", address.moreDetailedaddress), binding.tvAdditionalDetailsValue);
            } else {
                binding.tvAdditionalDetails.setVisibility(View.GONE);
                binding.tvAdditionalDetailsValue.setVisibility(View.GONE);

            }

        }

        if (ConfirmBookFragment.per==0.0 && ConfirmBookFragment.Promo_discount.isEmpty()){
            binding.tvVatAmountValue.setVisibility(View.GONE);
            binding.tvVat.setVisibility(View.GONE);
            binding.tvDiscountAmount.setVisibility(View.GONE);
            binding.tvDiscountAmountValue.setVisibility(View.GONE);
            binding.tvFinalPrice.setVisibility(View.GONE);
            binding.tvfinalpriceValue.setVisibility(View.GONE);
            //binding.tvDiscountprice.setVisibility(View.GONE);
            // binding.tvDiscountpriceValue.setVisibility(View.GONE);
            binding.tvTotalAmount.setText(getString(R.string.total_amount));
        }else if (ConfirmBookFragment.per!=0.0 && !ConfirmBookFragment.Promo_discount.isEmpty() )  {
            binding.tvVatAmountValue.setVisibility(View.GONE);
            binding.tvVat.setVisibility(View.GONE);
            binding.tvDiscountAmount.setVisibility(View.GONE);
            binding.tvDiscountAmountValue.setVisibility(View.GONE);
             binding.tvFinalPrice.setVisibility(View.GONE);
           binding.tvfinalpriceValue.setVisibility(View.GONE);
            //binding.tvDiscountprice.setVisibility(View.VISIBLE);
            //binding.tvDiscountpriceValue.setVisibility(View.VISIBLE);
            binding.tvTotalAmount.setText(getString(R.string.total_amount)+" (excl. VAT)");
            binding.tvVat.setText(getString(R.string.vat)+" ("+"@"+ConfirmBookFragment.vat+"%"+")");
            binding.tvFinalPrice.setText(getString(R.string.final_price_inc_vat)+" (incl. VAT)");
        }else if (!ConfirmBookFragment.Promo_discount.isEmpty()) {
            binding.tvDiscountAmount.setVisibility(View.GONE);
            binding.tvDiscountAmountValue.setVisibility(View.GONE);
             binding.tvFinalPrice.setVisibility(View.GONE);
           binding.tvfinalpriceValue.setVisibility(View.GONE);
            //binding.tvDiscountprice.setVisibility(View.VISIBLE);
            //binding.tvDiscountpriceValue.setVisibility(View.VISIBLE);
            binding.tvVatAmountValue.setVisibility(View.GONE);
            binding.tvVat.setVisibility(View.GONE);
        }else if (ConfirmBookFragment.per!=0.0){
            binding.tvVatAmountValue.setVisibility(View.GONE);
            binding.tvVat.setVisibility(View.GONE);
            binding.tvDiscountAmount.setVisibility(View.GONE);
            binding.tvDiscountAmountValue.setVisibility(View.GONE);
             binding.tvFinalPrice.setVisibility(View.GONE);
           binding.tvfinalpriceValue.setVisibility(View.GONE);
            //binding.tvDiscountprice.setVisibility(View.VISIBLE);
            //binding.tvDiscountpriceValue.setVisibility(View.VISIBLE);
            binding.tvTotalAmount.setText(getString(R.string.total_amount)+" (excl. VAT)");
            binding.tvVat.setText(getString(R.string.vat)+" ("+"@"+ConfirmBookFragment.vat+"%"+")");
            binding.tvFinalPrice.setText(getString(R.string.final_price_inc_vat)+" (incl. VAT)");
        }
       /* per = (total / 100.0) * Float.parseFloat(ConfirmBookFragment.vat_value);
        totalper=total+per;*/

        Log.e("discount price",""+ConfirmBookFragment.Discounted_price+" "+ConfirmBookFragment.Promo_discount);
        if (searchMaidBulkModel.currency.equalsIgnoreCase("BHD")) {
            binding.tvVatAmountValue.setText(String.format(Locale.ENGLISH, "%s %.3f ", searchMaidBulkModel.currency,ConfirmBookFragment.per));
            binding.tvTotalAmountValue.setText(String.format(Locale.ENGLISH, "%s %.3f ", searchMaidBulkModel.currency, total));
            setStringStyle(String.format(Locale.ENGLISH, "%s %.3f ", searchMaidBulkModel.currency,searchMaidBulkModel.maidPrice), binding.tvRateValue);
            if (ConfirmBookFragment.Promo_discount.isEmpty() || ConfirmBookFragment.Promo_discount == null) {
                binding.tvfinalpriceValue.setText(String.format(Locale.ENGLISH, "%s %.3f ", searchMaidBulkModel.currency, ConfirmBookFragment.totalper));
               // binding.tvGrandtotalvalue.setText(String.format(Locale.ENGLISH, "%s %.3f ", searchMaidBulkModel.currency,totalper));

            }else {
                binding.tvfinalpriceValue.setText(String.format(Locale.ENGLISH, "%s %.3f", searchMaidBulkModel.currency,ConfirmBookFragment.totalper));
                binding.tvDiscountAmountValue.setText(String.format(Locale.ENGLISH, "%s %s%.3f", searchMaidBulkModel.currency, "-", Double.parseDouble(ConfirmBookFragment.Promo_discount)));
            }

        }else {
             try {
                  binding.tvVatAmountValue.setText(String.format(Locale.ENGLISH, "%s %.2f ", searchMaidBulkModel.currency,ConfirmBookFragment.per));
                 binding.tvTotalAmountValue.setText(String.format(Locale.ENGLISH, "%s %.2f", searchMaidBulkModel.currency, total));
                 setStringStyle(String.format(Locale.ENGLISH, "%s %.2f ", searchMaidBulkModel.currency, searchMaidBulkModel.maidPrice), binding.tvRateValue);
                 if (ConfirmBookFragment.Promo_discount.isEmpty()|| ConfirmBookFragment.Promo_discount == null) {
                     //   binding.tvGrandtotalvalue.setText(String.format(Locale.ENGLISH, "%s %.2f ", searchMaidBulkModel.currency,totalper));
                      binding.tvfinalpriceValue.setText(String.format(Locale.ENGLISH, "%s %.2f ", searchMaidBulkModel.currency, ConfirmBookFragment.totalper));
                 }else {
                     binding.tvfinalpriceValue.setText(String.format(Locale.ENGLISH, "%s %.2f", searchMaidBulkModel.currency,ConfirmBookFragment.totalper));
                     binding.tvDiscountAmountValue.setText(String.format(Locale.ENGLISH, "%s %s%.2f", searchMaidBulkModel.currency, "-", Double.parseDouble(ConfirmBookFragment.Promo_discount)));

                 }
             }catch (Exception e){

             }

        }

    }


    public void setStringStyle(String temp, TextView tvText) {

        tvText.setText(temp);
    }

    private void setListeners() {
        binding.tvMakePayment.setOnClickListener(this);
        binding.ivCardImage.setOnClickListener(this);
        binding.tvShowAll.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvMakePayment:
                ConfirmBookFragment.road_temp = "";
                ConfirmBookFragment.city_temp = "";
                ConfirmBookFragment.block_temp = "";
                ConfirmBookFragment.apartment_temp = "";
                ConfirmBookFragment.full_address_temp = "";
                ConfirmBookFragment.latLng = null;
                ConfirmBookFragment.totalper=0.0F;
                ConfirmBookFragment.per=0.0F;
                ConfirmBookFragment.total=0.0F;
                getActivity().finishAffinity();
                startActivity(new Intent(getActivity(), Main2Activity.class));
                break;

            case R.id.tvShowAll:

                Intent intent = new Intent(getActivity(), PickerShowActivity.class);
                intent.putExtra("type", "view");
                startActivity(intent);

                break;
            case R.id.ivCardImage:
              /*  Intent intentt = new Intent(getActivity(), ImageViewActivity.class);
                if (booking_type == 3) {
                    intentt.putExtra("imageUrl", searchMaidBulkModel.documentPicURL.getOriginal());
                } else {
                    intentt.putExtra("imageUrl", searchMaidModel.documentPicURL.getOriginal());
                }
                startActivity(intentt);*/
                break;

        }
    }
}
