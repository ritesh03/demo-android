package com.maktoday.views.changePassword;

import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import androidx.annotation.Nullable;

import android.text.Editable;
import android.text.TextWatcher;
import com.maktoday.utils.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.maktoday.R;
import com.maktoday.databinding.ActivityChangepasswordBinding;
import com.maktoday.model.ApiResponse;
import com.maktoday.utils.BaseActivity;
import com.maktoday.utils.Constants;
import com.maktoday.utils.DialogPopup;
import com.maktoday.utils.GeneralFunction;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import java.util.HashMap;

/**
 * Created by cbl81 on 30/11/17.
 */

public class ChangePasswordActivity extends BaseActivity implements ChangePasswordContract.View {

    private static final String TAG = "ChangePasswordActivity";
    private ActivityChangepasswordBinding binding;
    private ChangePasswordContract.Presenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: StartActivity");
        init();
        setListeners();
    }

    private void init() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_changepassword);
        presenter = new ChangePasswordPresenter();
        presenter.attachView(this);
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_white);
        }
    }

    private void setListeners() {
        binding.tvUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(GeneralFunction.isNetworkConnected(ChangePasswordActivity.this,binding.etCnfPassword)) {
                    if (!checkValidation())
                        return;

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("uniquieAppKey", Constants.UNIQUE_APP_KEY);
                    hashMap.put("oldPassword", binding.etOldPassword.getText().toString());
                    hashMap.put("newPassword", binding.etNewPassword.getText().toString());
                    presenter.apiChangePassword(hashMap);
                }
            }
        });

        binding.tilOldPassword.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                binding.tilOldPassword.setErrorEnabled(false);
                binding.tilOldPassword.setError(null);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.tilNewPassword.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                binding.tilNewPassword.setErrorEnabled(false);
                binding.tilNewPassword.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.tilCnfPassword.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                binding.tilCnfPassword.setErrorEnabled(false);
                binding.tilCnfPassword.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
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
    public void changePasswordSuccess(ApiResponse data) {

        Toast.makeText(this, getString(R.string.password_changed_succesfully), Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void changePasswordError(String errorMessage) {
        new DialogPopup().alertPopup(ChangePasswordActivity.this, getResources().getString(R.string.dialog_alert), errorMessage, "").show(getSupportFragmentManager(), "ios_dialog");;
    }

    @Override
    public void changePasswordFailure(String failureMessage) {
      Log.e(TAG, "changePasswordFailure: "+failureMessage);
        new DialogPopup().alertPopup(ChangePasswordActivity.this, getResources().getString(R.string.dialog_alert), getString(R.string.check_connection), "").show(getSupportFragmentManager(), "ios_dialog");;
    }

    private boolean checkValidation() {
        String oldPassword = binding.etOldPassword.getText().toString();
        String newPassword = binding.etNewPassword.getText().toString();
        String confirmPassword = binding.etCnfPassword.getText().toString();

        if (oldPassword.isEmpty()) {
            binding.tilOldPassword.setError(getString(R.string.oldPassword_empty_validation));
            binding.etOldPassword.requestFocus();
            return false;
        } else if (oldPassword.trim().isEmpty()) {
            binding.tilOldPassword.setError(getString(R.string.old_password_should_not_be_blank));
            binding.etOldPassword.requestFocus();
            return false;
        } else if (oldPassword.length() < 6 || oldPassword.length() > 15) {
            binding.tilOldPassword.setError(getString(R.string.oldpass_length_err));
            binding.etOldPassword.requestFocus();
            return false;
        } else if (newPassword.isEmpty()) {
            binding.tilNewPassword.setError(getString(R.string.newPassword_empty_validation));
            binding.etNewPassword.requestFocus();
            return false;
        } else if (newPassword.trim().isEmpty()) {
            binding.tilNewPassword.setError(getString(R.string.new_password_should_not_be_blank));
            binding.etNewPassword.requestFocus();
            return false;
        } else if (newPassword.length() < 6 || oldPassword.length() > 15) {
            binding.tilNewPassword.setError(getString(R.string.new_pass_err));
            binding.etNewPassword.requestFocus();
            return false;
        } else if (confirmPassword.isEmpty()) {
            binding.tilCnfPassword.setError(getString(R.string.confirmPassword_empty_password));
            binding.etCnfPassword.requestFocus();
            return false;
        } else if (confirmPassword.trim().isEmpty()) {
            binding.tilCnfPassword.setError(getString(R.string.confirm_password_should_not_be_blank));
            binding.etCnfPassword.requestFocus();
            return false;
        } else if (confirmPassword.length() < 6 || oldPassword.length() > 15) {
            binding.tilCnfPassword.setError(getString(R.string.confirmpass_length_err));
            binding.etCnfPassword.requestFocus();
            return false;
        } else if (!binding.etCnfPassword.getText().toString()
                .equals(binding.etNewPassword.getText().toString())) {
            binding.tilCnfPassword.setError(getString(R.string.pass_match_err));
            binding.tilCnfPassword.requestFocus();
            return false;
        }
        return true;
    }
}