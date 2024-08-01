package com.maktoday.utils;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.google.firebase.analytics.FirebaseAnalytics;

public abstract class BaseFragment extends Fragment {

    public FirebaseAnalytics mFirebaseAnalytics;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(requireContext());
    }
}