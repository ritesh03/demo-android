package com.maktoday.views.signup;

import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import com.maktoday.utils.Log;

import androidx.annotation.Nullable;

import com.maktoday.R;
import com.maktoday.databinding.ActivitySignupBinding;
import com.maktoday.utils.BaseActivity;
import com.maktoday.views.signupstep1.SignUp1Fragment;

/**
 * Created by cbl81 on 25/10/17.
 */

public class SignUpActivity extends BaseActivity {
    private ActivitySignupBinding binding;

    private static final String TAG = "SignUpActivity";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signup);
        Log.d(TAG, "onCreate: StartActivity");
        init();
        setData();
    }

    private void init() {
    }

    private void setData() {

        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                .add(R.id.fl, SignUp1Fragment.newInstance("SignUp1"), "SignUp1").commit();

    }
}
