package com.maktoday.views.setting;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.maktoday.utils.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.maktoday.R;
import com.maktoday.adapter.TutorialAdapter;
import com.maktoday.databinding.FragmentIntroBinding;
import com.maktoday.views.home.HomeFragment;
import com.maktoday.views.main.Main2Activity;

import java.util.ArrayList;
import java.util.List;

import cz.intik.overflowindicator.SimpleSnapHelper;

/**
 * Created by cbl81 on 27/10/17.
 */

public class IntroFragment extends Fragment  {

    private static final String TAG = "IntroFragment";
    private List<Integer> imageList ;
    private FragmentIntroBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentIntroBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: StartActivity");
        if (HomeFragment.noticount>0){
            Main2Activity.redCircle.setVisibility(View.VISIBLE);
            Main2Activity.countTextView.setText(String.valueOf(HomeFragment.noticount));
        }
        imageList = new ArrayList<>();
        imageList.clear();
        addImagesToList();
        setAdapter();
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
        binding.rlTutorial.setVisibility(View.VISIBLE);
        binding.rvTutorial.setAdapter(new TutorialAdapter(getActivity(), imageList));
        binding.rvTutorial.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.viewPagerIndicator.attachToRecyclerView(binding.rvTutorial);

        SimpleSnapHelper snapHelper = new SimpleSnapHelper(binding.viewPagerIndicator);
        snapHelper.attachToRecyclerView(binding.rvTutorial);
    }

}
