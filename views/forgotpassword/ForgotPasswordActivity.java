package com.maktoday.views.forgotpassword;

import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.maktoday.utils.Log;
import android.util.Patterns;
import android.view.View;

import com.maktoday.R;
import com.maktoday.databinding.ActivityForgotpasswordBinding;
import com.maktoday.model.ApiResponse;
import com.maktoday.utils.BaseActivity;
import com.maktoday.utils.DataVariable;
import com.maktoday.utils.DialogPopup;
import com.maktoday.utils.GeneralFunction;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import static com.maktoday.R.string.email_valid_validation;

/**
 * Created by cbl81 on 25/10/17.
 */

public class ForgotPasswordActivity extends BaseActivity implements View.OnClickListener, ForgotPasswordContract.View {

    private static final String TAG = "ForgotPasswordActivity";
    private ActivityForgotpasswordBinding binding;
    private ForgotPasswordContract.Presenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_forgotpassword);
        Log.d(TAG, "onCreate: StartActivity");
        init();
        setData();
        setListener();
    }

    private void init() {
        presenter = new ForgotPasswordPresenter();
        presenter.attachView(this);
    }

    private void setData() {
    }

    private void setListener() {
        binding.back.setOnClickListener(this);
        binding.tvSend.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                DataVariable.hideSoftKeyboard(ForgotPasswordActivity.this);
                finish();
                break;
            case R.id.tvSend:
                DataVariable.hideSoftKeyboard(this);
                String email = binding.etEmail.getText().toString().trim();


                if (email.isEmpty()) {
                    GeneralFunction.showSnackBar(ForgotPasswordActivity.this, binding.parent, getString(R.string.email_empty_validation));
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    GeneralFunction.showSnackBar(ForgotPasswordActivity.this, binding.parent, getString(email_valid_validation));
                    binding.etEmail.requestFocus();

                } else {
                    if (GeneralFunction.isNetworkConnected(this, binding.parent))
                        presenter.apiForgotPassword(email);
                }

                break;
        }
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
    public void forgotPasswordSuccess(ApiResponse data) {
        binding.etEmail.setText("");
        new DialogPopup().alertPopup(ForgotPasswordActivity.this, getResources().getString(R.string.dialog_alert), getString(R.string.reset_password_sent), "").show(getSupportFragmentManager(), "ios_dialog");;

    }

    @Override
    public void forgotPasswordError(String errorMessage) {
        new DialogPopup().alertPopup(ForgotPasswordActivity.this, getResources().getString(R.string.dialog_alert), errorMessage, "").show(getSupportFragmentManager(), "ios_dialog");;
    }

    @Override
    public void forgotPasswordFailure(String failureMessage) {
      Log.e(TAG, "forgotPasswordFailure: "+failureMessage);
        new DialogPopup().alertPopup(ForgotPasswordActivity.this, getResources().getString(R.string.dialog_alert), getString(R.string.check_connection), "").show(getSupportFragmentManager(), "ios_dialog");;
    }
}
