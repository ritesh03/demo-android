package com.maktoday.views.slotavailable;

import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.maktoday.utils.Log;
import android.view.MenuItem;

import com.maktoday.R;
import com.maktoday.adapter.SlotVerticalAdapter;
import com.maktoday.adapter.WeekAdapter;
import com.maktoday.databinding.ActivitySlotBinding;
import com.maktoday.model.TimeSlot;
import com.maktoday.utils.BaseActivity;

public class SlotAvailbleActivity extends BaseActivity {

    private static final String TAG = "SlotAvailableActivity";
    private ActivitySlotBinding binding;
    private TimeSlot timeSlot;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_slot);
        Log.d(TAG, "onCreate: StartActivity");
        init();
        setData();
    }

    private void init() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_white);
        }
    }

    private void setData() {
        timeSlot = new TimeSlot();
        if (getIntent() != null) {
            timeSlot = getIntent().getParcelableExtra("data");
            binding.title.setText(R.string.availability);
            binding.rv.setAdapter(new SlotVerticalAdapter(SlotAvailbleActivity.this, timeSlot));
            binding.rv.setLayoutManager(new LinearLayoutManager(SlotAvailbleActivity.this));
        }

        binding.rvWeekName.setAdapter(new WeekAdapter(SlotAvailbleActivity.this));
        binding.rvWeekName.setLayoutManager(new LinearLayoutManager(SlotAvailbleActivity.this));

    }//setData

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

}
