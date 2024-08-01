package com.maktoday.views.changelanguage;

import android.content.Intent;
import android.os.Bundle;
import com.maktoday.utils.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.maktoday.R;
import com.maktoday.databinding.FragmentChangeLanguageBinding;
import com.maktoday.model.ApiResponse;
import com.maktoday.model.PojoLogin;
import com.maktoday.utils.BaseActivity;
import com.maktoday.utils.Constants;
import com.maktoday.utils.DialogPopup;
import com.maktoday.utils.GeneralFunction;
import com.maktoday.utils.Prefs;
import com.maktoday.views.authenticate.AuthenticateActivity;
import com.maktoday.views.main.Main2Activity;
import com.maktoday.webservices.RestClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.maktoday.utils.Constants.USER_COUNTRY;
import static com.maktoday.utils.Constants.UnAuthorized;

/**
 * Created by cbl1005 on 20/1/18.
 */

public class ChangeLanguageActivity extends BaseActivity implements View.OnClickListener {
    private FragmentChangeLanguageBinding binding;
    private String language;
    private static final String TAG = "ChangeLanguageActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.fragment_change_language);
        Log.d(TAG, "onCreate: StartActivity");
        init();
        setData();
        setListeners();


        if (getIntent().hasExtra("type")) {
            binding.tvTitle.setText(R.string.select_language);

        } else {
            binding.tvTitle.setText(R.string.change_language);
        }
    }

    private void init() {
        setSupportActionBar(binding.toolbar);
    }

    private void setData() {

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_white);
        }


        if(Prefs.with(ChangeLanguageActivity.this).getObject(Constants.DATA, PojoLogin.class).faceBookLogin){
            binding.tvArabic.setVisibility(View.GONE);
        }else {
            binding.tvArabic.setVisibility(View.VISIBLE);
        }

        language = Prefs.with(ChangeLanguageActivity.this).getString(Constants.LANGUAGE_CODE, "");
        if (language.equals("en")) {
            binding.tvEnglish.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_check, 0);
            binding.tvArabic.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
        } else if (language.equals("ar")) {
            binding.tvEnglish.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
            binding.tvArabic.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_check, 0);
        }

    }

    private void setListeners() {
        binding.tvEnglish.setOnClickListener(this);
        binding.tvArabic.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvEnglish:
                if (GeneralFunction.isNetworkConnected(this, binding.tvArabic)) {
                    if (!language.equals("en")) {

                        Prefs.with(ChangeLanguageActivity.this).save(Constants.LANGUAGE_Click_Status, "yes");
                        Prefs.with(ChangeLanguageActivity.this).save(Constants.LANGUAGE_CODE, "en");

                        binding.tvEnglish.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_check, 0);
                        binding.tvArabic.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);

                        finishAffinity();

                        if (getIntent().hasExtra("type")) {
                            startActivity(new Intent(ChangeLanguageActivity.this, AuthenticateActivity.class));
                        } else {
                            apiChangeLanguage("EN");
                        }
                    }
                }
                break;
            case R.id.tvArabic:
                if (GeneralFunction.isNetworkConnected(this, binding.tvArabic)) {
                    if (!language.equals("ar")) {

                        Prefs.with(ChangeLanguageActivity.this).save(Constants.LANGUAGE_Click_Status, "yes");
                        Prefs.with(ChangeLanguageActivity.this).save(Constants.LANGUAGE_CODE, "ar");

                        binding.tvEnglish.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_check, 0);
                        binding.tvArabic.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);

                        finishAffinity();

                        if (getIntent().hasExtra("type")) {
                            startActivity(new Intent(ChangeLanguageActivity.this, AuthenticateActivity.class));
                        } else {
                            apiChangeLanguage("AR");
                        }

                    }
                }
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {

            if (getIntent().hasExtra("type")) {
                Intent intent1 = new Intent(Intent.ACTION_MAIN);
                intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent1.addCategory(Intent.CATEGORY_HOME);
                startActivity(intent1);
                finish();
            } else {
                finish();
            }
        }
        return true;
    }

    public void apiChangeLanguage(final String language) {
        setLoading(true);
        RestClient.getModalApiService().apiChangeLanguage(Prefs.get().getString(Constants.ACCESS_TOKEN, ""),
                Constants.UNIQUE_APP_KEY, language)
                .enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        GeneralFunction.dismissProgress();
                        if (response.isSuccessful()) {
                            issueSuccess(language);
                        } else {
                            if (response.code() == UnAuthorized) {
                                sessionExpired();
                            } else {
                                try {
                                    issueError(new JSONObject(response.errorBody().string()).getString("message"));
                                } catch (JSONException | IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        GeneralFunction.dismissProgress();
                        issueFailure(t.getMessage());
                    }

                });
    }

    public void setLoading(boolean isLoading) {
        if (isLoading)
            GeneralFunction.showProgress(this);
        else
            GeneralFunction.dismissProgress();
    }

    public void sessionExpired() {
        GeneralFunction.isUserBlocked(this);
    }

    public void issueSuccess(String data) {
        Prefs.with(ChangeLanguageActivity.this).save(Constants.LANGUAGE_Click_Status, "yes");
        if (data.equals("EN")) {
            Prefs.with(ChangeLanguageActivity.this).save(Constants.LANGUAGE_CODE, "en");
            binding.tvEnglish.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_check, 0);
            binding.tvArabic.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
            finishAffinity();
            if (getIntent().hasExtra("type")) {
                startActivity(new Intent(ChangeLanguageActivity.this, AuthenticateActivity.class));
            } else {
                startActivity(new Intent(ChangeLanguageActivity.this, Main2Activity.class));
            }
        } else {
            Prefs.with(ChangeLanguageActivity.this).save(Constants.LANGUAGE_CODE, "ar");
            binding.tvEnglish.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
            binding.tvArabic.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_check, 0);
            finishAffinity();
            if (getIntent().hasExtra("type")) {
                startActivity(new Intent(ChangeLanguageActivity.this, AuthenticateActivity.class));
            } else {
                startActivity(new Intent(ChangeLanguageActivity.this, Main2Activity.class));
            }

        }

    }

    public void issueError(String errorMessage) {
        new DialogPopup().alertPopup(this, getResources().getString(R.string.dialog_alert), errorMessage, "").show(getSupportFragmentManager(), "ios_dialog");;
    }

    public void issueFailure(String failureMessage) {
        new DialogPopup().alertPopup(this, getResources().getString(R.string.dialog_alert), failureMessage, "").show(getSupportFragmentManager(), "ios_dialog");
    }

}
