package com.maktoday.views.addNewCard;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import com.maktoday.utils.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;

import com.maktoday.R;
import com.maktoday.databinding.FragmentAddcardBinding;
import com.maktoday.model.PojoCreatePayment;
import com.maktoday.model.PojoLogin;
import com.maktoday.model.SearchMaidModel;
import com.maktoday.utils.BaseActivity;
import com.maktoday.utils.Constants;
import com.maktoday.utils.DataVariable;
import com.maktoday.utils.DialogPopup;
import com.maktoday.utils.GeneralFunction;
import com.maktoday.utils.Prefs;
import com.maktoday.views.paymentinfo.PaymentInfoFragment;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import static com.maktoday.R.id.tvExpiryMonthValue;

/**
 * Created by cbl81 on 29/11/17.
 */

public class AddCardActivity extends BaseActivity implements AddCardContract.View, View.OnClickListener {

    private static final String TAG = "AddCardActivity";
    private FragmentAddcardBinding binding;
    private AddCardContract.Presenter presenter;
    private SearchMaidModel searchMaidModel;
    private boolean isExtend, bookAgain;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.fragment_addcard);
        Log.d(TAG, "onCreate: StartActivity");
        init();
        setData();
        setListeners();
    }

    private void init() {
        presenter = new AddCardPresenter();
        presenter.attachView(this);
        setSupportActionBar(binding.addToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_white);
        }

        isExtend = getIntent().getBooleanExtra(Constants.ISEXTEND, false);

    }

    private void setData() {
        String country = Prefs.with(this).getString(Constants.COUNTRY_NAME, "");
        String currency = "";
        if (country.contains("United Arab Emirates")) {
            currency = "AED";
        } else {
            currency = "BHD";
        }

        searchMaidModel = getIntent().getParcelableExtra(Constants.SEARCH_MAID_DATA);
        bookAgain = getIntent().getBooleanExtra(Constants.BOOK_AGAIN, false);
        Float total = searchMaidModel.maidPrice * searchMaidModel.duration;
        binding.tvTotalPrice.setText(String.format(Locale.getDefault(), "%s  %.3f ", currency, total));
    }

    private void setListeners() {
        binding.tvExpiryMonthValue.setOnClickListener(this);
        binding.tvExpiryYearValue.setOnClickListener(this);
        binding.tvMakePayment.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case tvExpiryMonthValue:
                openMonthDialog().show();
                break;
            case R.id.tvExpiryYearValue:
                openYearDialog().show();
                break;

        }
    }

    private Dialog openMonthDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        Calendar cal = Calendar.getInstance();
        View dialog = inflater.inflate(R.layout.item_month_picker, null);
        final NumberPicker monthPicker = dialog.findViewById(R.id.monthPicker);
        final int month = cal.get(Calendar.MONTH);
        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);
        monthPicker.setValue(month + 1);

        builder.setView(dialog)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        int month = monthPicker.getValue();
                        binding.tvExpiryMonthValue.setText(String.valueOf(month));
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        return builder.create();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            DataVariable.hideSoftKeyboard(AddCardActivity.this);
            Intent intent = new Intent();
            intent.putExtra("back", true);
            setResult(RESULT_CANCELED, intent);
            finish();
        }
        return true;
    }

    private Dialog openYearDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        Calendar cal = Calendar.getInstance();
        View dialog = inflater.inflate(R.layout.item_year_picker, null);
        final NumberPicker yearPicker = dialog.findViewById(R.id.yearPicker);
        final int year = cal.get(Calendar.YEAR);
        yearPicker.setMinValue(year);
        yearPicker.setMaxValue(year + 50);
        yearPicker.setValue(year);

        builder.setView(dialog)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        int year = yearPicker.getValue();
                        binding.tvExpiryYearValue.setText(String.valueOf(year));
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        return builder.create();
    }

    private void createPayment(String tokenID) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("uniquieAppKey", Constants.UNIQUE_APP_KEY);
        hashMap.put("serviceId", getIntent().getStringExtra(Constants.SERVICE_ID));
        hashMap.put("cardToken", tokenID);

        if (!binding.cbSaveCard.isChecked()) {
            hashMap.put("saveCards", "false");
        } else {
            hashMap.put("saveCards", "true");
        }

        if (bookAgain) {
            hashMap.put("isExtension", String.valueOf(false));
        } else if (isExtend) {
            hashMap.put("isExtension", String.valueOf(true));
        } else {
            hashMap.put("isExtension", String.valueOf(false));
        }

        presenter.apiCreatePayment(hashMap);
    }

    @Override
    public void setLoading(boolean isLoading) {
        if (isLoading)
            GeneralFunction.showProgress(this);
        else
            GeneralFunction.dismissProgress();
    }

    @Override
    public void sessionExpired() {
        GeneralFunction.isUserBlocked(this);
    }

    @Override
    public void createPaymentSuccess(PojoCreatePayment.Data data1) {
        Intent intent = new Intent();

        if (bookAgain) {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                    .add(android.R.id.content, PaymentInfoFragment.newInstance(searchMaidModel, data1.transactionId,
                            data1.data.get(0).getAddress()), "PaymentInfoFragment").addToBackStack("PaymentInfoFragment").commit();
            return;
        } else if (isExtend) {
            Toast.makeText(AddCardActivity.this, getString(R.string.service_extended), Toast.LENGTH_SHORT).show();
        } else {
            GeneralFunction.showSnackBar(this, binding.parent, getString(R.string.order_placed_successfully));
            intent.putExtra("ID", data1.transactionId);
            intent.putExtra("Address", data1.data.get(0).getAddress());
        }

        PojoLogin data = Prefs.with(AddCardActivity.this).getObject(Constants.DATA, PojoLogin.class);
        if (data.isGuestFlag() && !data.isFirstBookingDone()) {
            data.setFirstBookingDone(true);
        }

        Prefs.with(AddCardActivity.this).save(Constants.DATA, data);


        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void addCardSuccess(String cardToken) {
        createPayment(cardToken);
    }

    @Override
    public void paymentError(String failureMessage) {
        new DialogPopup().alertPopup(this, getResources().getString(R.string.dialog_alert), failureMessage, "").show(getSupportFragmentManager(),"IOS_Dialog");
    }

    @Override
    public void paymentFailure(String failureMessage) {
       Log.e(TAG, "paymentFailure: "+failureMessage);
        new DialogPopup().alertPopup(this, getResources().getString(R.string.dialog_alert), getString(R.string.check_connection), "").show((getSupportFragmentManager()),"IOS_Dialog");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

}
