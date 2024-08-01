package com.maktoday.views.extendpayment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.Nullable;

import com.maktoday.R;
import com.maktoday.model.SearchMaidBulkModel;
import com.maktoday.model.SearchMaidModel;
import com.maktoday.utils.BaseActivity;
import com.maktoday.utils.Constants;
import com.maktoday.utils.DataVariable;
import com.maktoday.utils.Log;
import com.maktoday.views.cardlist.CardListFragment;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import static com.maktoday.views.home.HomeFragment.booking_type;

/**
 * Created by cbl1005 on 17/1/18.
 */
public class ExtendPaymentActivity extends BaseActivity {
    private static final String TAG = "ExtendPaymentActivity";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extend_payment);
        Log.d(TAG, "onCreate: StartActivity");
        init();
        setData();
    }

    private void init() {
        setSupportActionBar(findViewById(R.id.addToolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_white);
        }
    }

    private void setData() {
        if (booking_type == 3) {
            String serviceId = getIntent().getStringExtra(Constants.SERVICE_ID);
            String referenceId = getIntent().getStringExtra("referenceId");
            String isExtend=getIntent().getStringExtra("isExtension");
            String vat=getIntent().getStringExtra(Constants.VAT);
            SearchMaidBulkModel searchMaidModel = getIntent().getParcelableExtra(Constants.SEARCH_MAID_DATA);
            if (isExtend.equalsIgnoreCase("true")){
                getSupportFragmentManager().beginTransaction().add(R.id.fl, CardListFragment.newInstance("", referenceId, serviceId, searchMaidModel, true, getIntent().getBooleanExtra(Constants.BOOK_AGAIN, false), getIntent().getParcelableExtra(Constants.MAID_AVAILABLE_TIMESLOT), ""), "CardListFragment").commit();
            }else {
                getSupportFragmentManager().beginTransaction().add(R.id.fl, CardListFragment.newInstance("", referenceId, serviceId, searchMaidModel, false, getIntent().getBooleanExtra(Constants.BOOK_AGAIN, false), getIntent().getParcelableExtra(Constants.MAID_AVAILABLE_TIMESLOT), ""), "CardListFragment").commit();

            }

        } else {
            String serviceId = getIntent().getStringExtra(Constants.SERVICE_ID);
            String referenceId = getIntent().getStringExtra("referenceId");
            String payment_mode = getIntent().getStringExtra("payment_mode");
            String isExtend=getIntent().getStringExtra("isExtension");
            String vat=getIntent().getStringExtra(Constants.VAT);
            Log.e("isExtension",isExtend);
            Log.e("vat value",vat);
            SearchMaidModel searchMaidModel = getIntent().getParcelableExtra(Constants.SEARCH_MAID_DATA);

            if (searchMaidModel == null) {
                searchMaidModel = new SearchMaidModel();
                searchMaidModel.maidPrice = getIntent().getFloatExtra(Constants.PRICE, 0.0f);
                searchMaidModel.duration = getIntent().getIntExtra(Constants.DURATION, 0);
                searchMaidModel.currency = getIntent().getStringExtra(Constants.CURRENCY);
                searchMaidModel.vat=vat;
            }
            if (isExtend.equalsIgnoreCase("true")){
                getSupportFragmentManager().beginTransaction().add(R.id.fl, CardListFragment.newInstance(payment_mode, referenceId, serviceId, searchMaidModel, true, getIntent().getBooleanExtra(Constants.BOOK_AGAIN, true), getIntent().getParcelableExtra(Constants.MAID_AVAILABLE_TIMESLOT), ""), "CardListFragment").commit();

            }else {
                getSupportFragmentManager().beginTransaction().add(R.id.fl, CardListFragment.newInstance(payment_mode, referenceId, serviceId, searchMaidModel, false, getIntent().getBooleanExtra(Constants.BOOK_AGAIN, false), getIntent().getParcelableExtra(Constants.MAID_AVAILABLE_TIMESLOT), ""), "CardListFragment").commit();

            }
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().findFragmentByTag("PaymentInfoFragment") == null) {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                Intent intent = new Intent();
                setResult(2, intent);
            }
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            DataVariable.hideSoftKeyboard(ExtendPaymentActivity.this);
            finish();
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
