package com.maktoday.views.booking;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.maktoday.utils.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.maktoday.R;
import com.maktoday.adapter.BookingFragmentPagerAdapter;
import com.maktoday.databinding.FragmentBookingBinding;
import com.maktoday.views.home.HomeFragment;
import com.maktoday.views.main.Main2Activity;
import com.maktoday.views.ongoingbooking.OnGoingBookingFragment;
import com.maktoday.views.pastbooking.PastBookingFragment;
import com.maktoday.views.upcomingbooking.UpComingBookingFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cbl81 on 27/10/17.
 */

public class BookingFragment extends Fragment {
    private static final String TAG = "BookingFragment";
    private FragmentBookingBinding binding;
    private List<Fragment> fragmentList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBookingBinding.inflate(inflater, container, false);
        return binding.parent;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: StartActivity");
        init();
        setData();
        try {
            if (HomeFragment.noticount > 0) {
                Main2Activity.redCircle.setVisibility(View.VISIBLE);
                Main2Activity.countTextView.setText(String.valueOf(HomeFragment.noticount));
            }
        }catch (Exception e){
            e.printStackTrace();
            try {//----------- IF FIREBASE RETURN EXCEPTION
                FirebaseCrashlytics.getInstance().recordException(e);
            } catch (Exception fe) {
                fe.printStackTrace();
            }
        }
    }

    private void init() {
        fragmentList.add(new OnGoingBookingFragment());
        fragmentList.add(new UpComingBookingFragment());
        fragmentList.add(new PastBookingFragment());
    }

    private void setData() {
        binding.viewPager.setAdapter(new BookingFragmentPagerAdapter(getContext(), getChildFragmentManager(), fragmentList));
        binding.viewPager.setCurrentItem(0);
        binding.viewPager.setOffscreenPageLimit(2);
        binding.tabLayout.setupWithViewPager(binding.viewPager);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

       /* Fragment fragment = new HomeFragment();
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.frameLayout, fragment, "home");
        fragmentTransaction.commitAllowingStateLoss();*/
    }
}
