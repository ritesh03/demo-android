package com.maktoday.views;

import android.content.Intent;

import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import com.maktoday.utils.Log;
import android.view.View;

import com.maktoday.R;
import com.maktoday.databinding.ActivityMainBinding;
import com.maktoday.utils.BaseActivity;
import com.maktoday.views.agencyprofle.AgencyProfileActivity;
import com.maktoday.views.authenticate.AuthenticateActivity;
import com.maktoday.views.maidprofile.MaidProfileFragment;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        Log.d(TAG, "onCreate: StartActivity");
        setListeners();
    }

    private void setListeners() {
        binding.tvPayment.setOnClickListener(this);
        binding.tvLogout.setOnClickListener(this);
        binding.tvMaidProfile.setOnClickListener(this);
        binding.tvAgencyProfile.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvPayment:
                //   startActivity(new Intent(MainActivity.this, PaymentActivity.class));
                break;
            case R.id.tvLogout:
                finishAffinity();
                startActivity(new Intent(MainActivity.this, AuthenticateActivity.class));
                break;
            case R.id.tvMaidProfile:

                startActivity(new Intent(MainActivity.this, MaidProfileFragment.class));
                break;
            case R.id.tvAgencyProfile:

                startActivity(new Intent(MainActivity.this, AgencyProfileActivity.class));
                break;
        }
    }
}
