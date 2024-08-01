package com.maktoday.views.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import com.maktoday.utils.Log;

import com.maktoday.R;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Log.d(TAG, "onCreate: StartActivityHome");
        Fragment homeFagment = new HomeFragment();
         getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                .add(android.R.id.content, homeFagment, "HomeFragment")
                .commit();
    }
}