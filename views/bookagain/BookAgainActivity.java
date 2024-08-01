package com.maktoday.views.bookagain;

import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;

import com.maktoday.utils.Log;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.maktoday.Config.Config;
import com.maktoday.R;
import com.maktoday.databinding.ActivityBookAgainBinding;
import com.maktoday.model.MaidData;
import com.maktoday.model.SearchMaidModel;
import com.maktoday.utils.BaseActivity;
import com.maktoday.utils.Constants;
import com.maktoday.views.AllService.ServiceFragment;
import com.maktoday.views.home.HomeFragment;

/**
 * Created by cbl81 on 4/12/17.
 */

public class BookAgainActivity extends BaseActivity {
    private static final String TAG = "BookAgainActivity";
    public int step = 0;
    private ActivityBookAgainBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_book_again);
        Log.d(TAG, "onCreate: StartActivity");
        init();
        setData();
    }

    private void init() {
        AppCompatActivity activity = (AppCompatActivity) BookAgainActivity.this;
        activity.setSupportActionBar(binding.toolbar);
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getSupportActionBar().setDisplayShowHomeEnabled(true);
            activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
            activity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_white);
        }
        //getArg
    }

    private void setData() {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                android.R.anim.fade_out);
        SearchMaidModel searchMaidModel = getIntent().getParcelableExtra(Constants.SEARCH_MAID_DATA);
     //   MaidData maidData=getIntent().getParcelableExtra(Constants.MAID_DATA);

         if (getIntent().getStringExtra(Constants.isFavorite).equalsIgnoreCase("yes")){
            // HomeFragment homeFragment = new HomeFragment();
             ServiceFragment serviceFragment = new ServiceFragment();
             Bundle bundle = new Bundle();
             bundle.putBoolean(Constants.BOOK_AGAIN, true);
             bundle.putString(Constants.reschuleStatus, getIntent().getStringExtra(Constants.reschuleStatus));
             bundle.putString(Constants.isFavorite,"yes");
             bundle.putParcelable(Constants.SEARCH_MAID_DATA, searchMaidModel);
             bundle.putString(Constants.BOOKING_DATA, getIntent().getStringExtra(Constants.BOOKING_DATA));
             bundle.putParcelable(Constants.MAID_AVAILABLE_TIMESLOT, getIntent().getParcelableExtra(Constants.MAID_AVAILABLE_TIMESLOT));
             bundle.putString(Constants.SERVICE_ID, getIntent().getStringExtra(Constants.SERVICE_ID));
             bundle.putString(Constants.BOOKING_TYPE, getIntent().getStringExtra(Constants.BOOKINGT_YPE));

             bundle.putString(Constants.MAID_DATA,getIntent().getStringExtra(Constants.MAID_DATA));
             Log.d(TAG, "onClick: maid data on book again activity "+ new Gson().toJson(getIntent().getStringExtra(Constants.MAID_DATA)));

             bundle.putString("payment_mode", getIntent().getStringExtra("payment_mode"));
             bundle.putString("lat", getIntent().getStringExtra("lat"));
             bundle.putString("lng", getIntent().getStringExtra("lng"));
             //homeFragment.setArguments(bundle);
             serviceFragment.setArguments(bundle);
             Config.inBookAgainScreen = true;
            // fragmentTransaction.add(R.id.flBookAgain, homeFragment).commit();
             fragmentTransaction.add(R.id.flBookAgain, serviceFragment).commit();
         }else {
          //  ServiceFragment serviceFragment = new ServiceFragment();
              HomeFragment homeFragment = new HomeFragment();
             Bundle bundle = new Bundle();
             bundle.putBoolean(Constants.BOOK_AGAIN, true);
             bundle.putString(Constants.reschuleStatus, getIntent().getStringExtra(Constants.reschuleStatus));

             bundle.putParcelable(Constants.SEARCH_MAID_DATA, searchMaidModel);
             bundle.putString(Constants.BOOKING_DATA, getIntent().getStringExtra(Constants.BOOKING_DATA));
             bundle.putParcelable(Constants.MAID_AVAILABLE_TIMESLOT, getIntent().getParcelableExtra(Constants.MAID_AVAILABLE_TIMESLOT));
             bundle.putString(Constants.SERVICE_ID, getIntent().getStringExtra(Constants.SERVICE_ID));
             bundle.putString(Constants.BOOKING_TYPE, getIntent().getStringExtra(Constants.BOOKINGT_YPE));
             bundle.putString(Constants.MAID_DATA,getIntent().getStringExtra(Constants.MAID_DATA));
             Log.d(TAG, "setData: maid data:--   jcj"+ new Gson().toJson(getIntent().getStringExtra(Constants.MAID_DATA)));
             bundle.putString("payment_mode", getIntent().getStringExtra("payment_mode"));
             bundle.putString("lat", getIntent().getStringExtra("lat"));
             bundle.putString("lng", getIntent().getStringExtra("lng"));
              homeFragment.setArguments(bundle);
             //serviceFragment.setArguments(bundle);
             Config.inBookAgainScreen = true;
            // fragmentTransaction.add(R.id.flBookAgain, serviceFragment).commit();
             fragmentTransaction.add(R.id.flBookAgain, homeFragment).commit();
         }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

}
