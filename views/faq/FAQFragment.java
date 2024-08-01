package com.maktoday.views.faq;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ViewFlipper;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.maktoday.R;
import com.maktoday.utils.Constants;
import com.maktoday.utils.Log;
import com.maktoday.utils.Prefs;
import com.maktoday.views.home.HomeFragment;
import com.maktoday.views.main.Main2Activity;

/**
 * Created by cbl81 on 1/12/17.
 */

public class FAQFragment extends Fragment {
    private static final String TAG = "FAQFragment";
    private static String url = "https://mak.today/MAK/AgencyPanel/#/faq";
    private WebView webView;
    private ViewFlipper viewFlipper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_faq, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: StartActivity");
        init(view);
        setData();
        setListenser();
        if (HomeFragment.noticount>0){
            Main2Activity.redCircle.setVisibility(View.VISIBLE);
            Main2Activity.countTextView.setText(String.valueOf(HomeFragment.noticount));
        }
    }

    private void init(View view) {
        webView = view.findViewById(R.id.webview);
        viewFlipper = view.findViewById(R.id.viewFlipper);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.setVerticalScrollBarEnabled(true);
        if (Prefs.with(getActivity()).getString(Constants.LANGUAGE_CODE, "en").equals("en")) {
            url = Constants.FAQ;
        } else {
            url = Constants.FAQ;
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
                Log.d("URL", url);
                viewFlipper.setDisplayedChild(1);
            }
        });
    }
}
