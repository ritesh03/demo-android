package com.maktoday.views.privacyPolicy;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.maktoday.utils.Log;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.maktoday.R;
import com.maktoday.utils.BaseActivity;
import com.maktoday.utils.Constants;
import com.maktoday.utils.Prefs;

public class PrivacyPolicyActivity extends BaseActivity {

    private static String url = "https://mak.today/makPrivacyPolicy/";

    private static final String TAG = "PrivacyPolicyAcivity";
    private WebView webView;
    private ViewFlipper viewFlipper;
    private Toolbar toolbar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);
        Log.d(TAG, "onCreate: StartActivity");
        init();
        setData();
        setListenser();
    }

    private void init() {

        webView = findViewById(R.id.webview);
        viewFlipper = findViewById(R.id.viewFlipper);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.setVerticalScrollBarEnabled(true);
        webView.clearCache(false);
        toolbar = findViewById(R.id.toolbar);

        final TextView tvTitle = findViewById(R.id.title);
        tvTitle.setText(R.string.privacy_policy);

       /* if (Prefs.with(this).getString(Constants.LANGUAGE_CODE, "en").equals("en")) {
            url = "https://mak.today/makPrivacyPolicy/";
        } else {
            url = "https://mak.today/makPrivacyPolicy/";
        }*/
        if (Prefs.with(this).getString(Constants.LOGIN_COUNTRY,"").equalsIgnoreCase("BH")){
            url = Constants.PRIVACY_POLICY_BH;
        }else {
            url = Constants.PRIVACY;
        }

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_white);
        }

    }

    private void setData() {
        viewFlipper.setDisplayedChild(0);
        webView.loadUrl(url);


    }

    private void setListenser() {
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                viewFlipper.setDisplayedChild(1);
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
}
