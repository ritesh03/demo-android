package com.maktoday.adapter;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.maktoday.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cbl81 on 27/10/17.
 */

public class BookingFragmentPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragmentList=new ArrayList<>();
    private List<String> titleList=new ArrayList<>();

    public  BookingFragmentPagerAdapter(Context context,FragmentManager fm, List<Fragment> fragmentList){
        super(fm);
        this.fragmentList=fragmentList;
        titleList.add(context.getString(R.string.ongoing));
        titleList.add(context.getString(R.string.upcoming));
        titleList.add(context.getString(R.string.past));
    }

    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                return fragmentList.get(position);

            case 1:
                return fragmentList.get(position);

            case 2:
                return fragmentList.get(position);

            default:
                return null;
        }

    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titleList.get(position);
    }

    @Override
    public int getCount() {
        return 3;
    }
}
