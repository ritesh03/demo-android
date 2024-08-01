package com.maktoday.views.updateprofile;

import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import com.maktoday.utils.Log;
import androidx.annotation.Nullable;

import com.maktoday.R;
import com.maktoday.databinding.ActivityUpdateProfileBinding;
import com.maktoday.utils.BaseActivity;
import com.maktoday.views.signupstep2.SignUp2Fragment;

/**
 * Created by cbl81 on 28/10/17.
 */

public class UpdateProfileActivity extends BaseActivity {
    private ActivityUpdateProfileBinding binding;

    private static final String TAG = "UpdateProfileActivity";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_update_profile);
        Log.d(TAG, "onCreate: StartActivity");
        init();
        setData();
        setListensers();
    }

    private void init() {

    }

    private void setData() {
        getSupportFragmentManager().beginTransaction().add(R.id.frameLayout, SignUp2Fragment.newInstance("UpdateProfile"), "SignUp2").commit();
    }

    private void setListensers() {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}
