package com.maktoday.views.TitleScreen;

import android.content.DialogInterface;
import android.content.Intent;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.maktoday.utils.Constants;
import com.maktoday.utils.Log;
import android.view.View;

import com.maktoday.R;
import com.maktoday.adapter.TutorialAdapter;
import com.maktoday.databinding.ActivityTitleScreenBinding;
import com.maktoday.utils.BaseActivity;
import com.maktoday.utils.Prefs;
import com.maktoday.utils.dialog.IOSAlertDialog;
import com.maktoday.views.authenticate.AuthenticateActivity;

import java.util.ArrayList;
import java.util.List;

import cz.intik.overflowindicator.SimpleSnapHelper;
import me.leolin.shortcutbadger.ShortcutBadger;

public class TitleScreenActivity extends BaseActivity {

    private static final String TAG = "TitleScreenActivity";
    private ActivityTitleScreenBinding binding;
    private List<Integer> imageList = new ArrayList<>();
    private Boolean isTutorialOpen = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_title_screen);
        Log.d(TAG, "onCreate: StartActivity");
        addImagesToList();
        setAdapter();

        findViewById(R.id.tvContinue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isTutorialOpen) {
                    findViewById(R.id.top_layy).setBackgroundColor(Color.parseColor("#000000"));


                    IOSAlertDialog iosAlertDialog = IOSAlertDialog.newInstance(
                            TitleScreenActivity.this,
                            null,
                            getString(R.string.thank_you_for_downloading_the_mak_user_app),
                            getString(R.string.ok),
                            null,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            },
                            null,
                            ContextCompat.getColor(TitleScreenActivity.this, R.color.app_color),
                            ContextCompat.getColor(TitleScreenActivity.this, R.color.app_color),
                            false
                    );

                    iosAlertDialog.show(getSupportFragmentManager(),"Ios_AlertDialog");
//                    AlertDialog  dialog = new AlertDialog.Builder(TitleScreenActivity.this)
//                           .setCancelable(false)
//                            .setMessage(R.string.thank_you_for_downloading_the_mak_user_app)
//                           .setPositiveButton(R.string.ok, null)
//                           .show();
//
//                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(TitleScreenActivity.this, R.color.app_color));
//                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).setAllCaps(false);



                    binding.rlTutorial.setVisibility(View.VISIBLE);
                    binding.rlParent.setBackgroundColor(getResources().getColor(R.color.black));
                    isTutorialOpen = true;
                } else {
                    if (getIntent().hasExtra("type")) {
                        finish();
                    } else {
                        startActivity(new Intent(TitleScreenActivity.this, AuthenticateActivity.class));
                        finish();
                    }
                }
            }
        });
    }
    private void addImagesToList() {
        imageList.add(R.drawable.ic_tutorial_1);
        imageList.add(R.drawable.ic_tutorial_2);
        imageList.add(R.drawable.ic_tutorial_3);
        imageList.add(R.drawable.ic_tutorial_4);
        imageList.add(R.drawable.ic_tutorial_5);
        imageList.add(R.drawable.ic_tutorial_6);
        imageList.add(R.drawable.ic_tutorial_7);
        imageList.add(R.drawable.ic_tutorial_9);
    }

    private void setAdapter() {
        binding.rvTutorial.setAdapter(new TutorialAdapter(TitleScreenActivity.this, imageList));
        binding.rvTutorial.setLayoutManager(new LinearLayoutManager(TitleScreenActivity.this, LinearLayoutManager.HORIZONTAL, false));
        binding.viewPagerIndicator.attachToRecyclerView(binding.rvTutorial);
        SimpleSnapHelper snapHelper = new SimpleSnapHelper(binding.viewPagerIndicator);
        snapHelper.attachToRecyclerView(binding.rvTutorial);
    }

}
