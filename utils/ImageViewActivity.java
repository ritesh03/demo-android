package com.maktoday.utils;

import androidx.databinding.DataBindingUtil;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.maktoday.R;
import com.maktoday.databinding.ActivityImageViewBinding;
import com.stripe.android.paymentsheet.PaymentSheetResult;

public class ImageViewActivity extends AppCompatActivity {

    private ActivityImageViewBinding activityImageViewBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityImageViewBinding= DataBindingUtil.setContentView(this, R.layout.activity_image_view);
        init();
        Log.e("immm", getIntent().getStringExtra("imageUrl"));
        activityImageViewBinding.layoutProgress.setVisibility(View.VISIBLE);
        Glide.with(this)
                .load(getIntent().getStringExtra("imageUrl"))
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)

                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        activityImageViewBinding.layoutProgress.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        activityImageViewBinding.layoutProgress.setVisibility(View.GONE);

                        return false;
                    }
                })
                /*.listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<Drawable> target, boolean isFirstResource) {
                        activityImageViewBinding.layoutProgress.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        activityImageViewBinding.layoutProgress.setVisibility(View.GONE);
                        return false;
                    }
                })*/
                .into(activityImageViewBinding.touchImageView);

    }
    private void init() {
        setSupportActionBar(activityImageViewBinding.tbImageView);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_white);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}