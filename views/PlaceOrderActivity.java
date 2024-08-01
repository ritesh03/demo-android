package com.maktoday.views;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Message;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import com.maktoday.utils.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.maktoday.Config.Config;
import com.maktoday.R;
import com.maktoday.utils.BaseActivity;
import com.maktoday.utils.GeneralFunction;
import com.stripe.android.paymentsheet.PaymentSheetResult;

public class PlaceOrderActivity extends BaseActivity {
    WebView webView;
    private WebView wv1;
    private static final String TAG = "PlaceOrderActivity";
    String paymentUrl = "";
    TextView title;
    Toolbar headerrr;
    RelativeLayout back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);
        Log.d(TAG, "onCreate: StartActivity");
        webView = findViewById(R.id.webView);
        headerrr = findViewById(R.id.headerrr);
        back = findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        String amount = getIntent().getStringExtra("amount");

        if (getIntent().getStringExtra("type").equalsIgnoreCase("debit")) {
            paymentUrl = "http://52.36.127.111/BenefitIntegration/init.php?amount=" + amount;
            loadData();
        } else {
            paymentUrl = "http://52.36.127.111:8002/secureId";
            loadData();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public class myWebClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            //  super.onPageStarted(view, url, favicon);
            GeneralFunction.showProgress(PlaceOrderActivity.this);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onFormResubmission(WebView view, Message dontResend, Message resend) {
            super.onFormResubmission(view, dontResend, resend);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            Log.e("finish url", url);
            if (getIntent().getStringExtra("type").equalsIgnoreCase("debit")) {
                if (url.contains("http://52.36.127.111/BenefitTest/Success.php")) {

                    String[] urlSplit = url.split("paymentid");

                    Log.e("payment_id", urlSplit[1].split("&")[0].substring(1));
                    Config.TRANSACTION_ID = urlSplit[1].split("&")[0].substring(1);

                    finish();

                } else if (url.contains("http://52.36.127.111/BenefitTest/Error.php?error")) {
                    Toast.makeText(PlaceOrderActivity.this, "Payment Failed", Toast.LENGTH_SHORT).show();
                    loadData();
                } else if (url.contains("http://52.36.127.111/BenefitTest/Failed.php")) {
                    Toast.makeText(PlaceOrderActivity.this, "Payment Failed", Toast.LENGTH_SHORT).show();
                    loadData();
                }

                GeneralFunction.dismissProgress();
            } else {
                if (url.equalsIgnoreCase("http://52.36.127.111:8002/error")) {
                    Toast.makeText(PlaceOrderActivity.this, "Payment Failed", Toast.LENGTH_SHORT).show();
                    loadData();

                } else if (url.contains("http://52.36.127.111:8002/success")) {
                    // finish();
                    Config.TRANSACTION_ID = "15632435654";

                    finish();
                }

                GeneralFunction.dismissProgress();

            }
        }

    }

    private void loadData() {
        webView.setWebViewClient(new myWebClient());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        webView.clearHistory();
        webView.clearCache(true);
        webView.getSettings().setJavaScriptEnabled(true);

        webView.loadUrl(paymentUrl);
    }

}
