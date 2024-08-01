package com.maktoday.views.agencyprofle;

import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import com.maktoday.utils.Log;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;

import com.maktoday.R;
import com.maktoday.adapter.AgencyMaidAdapter;
import com.maktoday.databinding.ActivityAgencyprofileBinding;
import com.maktoday.utils.BaseActivity;

/**
 * Created by cbl81 on 26/10/17.
 */

public class AgencyProfileActivity extends BaseActivity {
    private static final String TAG = "AgencyProfileActivity";
    private ActivityAgencyprofileBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_agencyprofile);
        Log.d(TAG, "onCreate: StartActivity");
        setData();
    }

    private void setData() {
        binding.rvMaid.setAdapter(new AgencyMaidAdapter(this));
        binding.rvMaid.setLayoutManager(new GridLayoutManager(this, 2));
    }
}
