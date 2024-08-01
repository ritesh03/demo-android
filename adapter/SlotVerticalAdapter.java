package com.maktoday.adapter;

import android.app.Activity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.maktoday.R;
import com.maktoday.model.TimeSlot;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by cbl81 on 24/12/17.
 */

public class SlotVerticalAdapter extends RecyclerView.Adapter<SlotVerticalAdapter.SlotViewHolder> {

    public Activity context;
    public TimeSlot timeSlot;

    public SlotVerticalAdapter(Activity context, TimeSlot timeSlot) {
        this.context = context;
        this.timeSlot=timeSlot;
    }

    @Override
    public SlotVerticalAdapter.SlotViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = context.getLayoutInflater();
        View view = inflater.inflate(R.layout.item_rv_slot, parent, false);
        return new SlotVerticalAdapter.SlotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SlotVerticalAdapter.SlotViewHolder holder, int position) {
        holder.rvSlot.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false));

        Set<String> slotList=new HashSet<>();
        switch (position)
        {
            case 0:
                slotList=timeSlot._0;
                break;
            case 1:
                slotList=timeSlot._1;
                break;
            case 2:
                slotList=timeSlot._2;
                break;
            case 3:
                slotList=timeSlot._3;
                break;
            case 4:
                slotList=timeSlot._4;
                break;
            case 5:
                slotList=timeSlot._5;
                break;
            case 6:
                slotList=timeSlot._6;
                break;
        }
        holder.rvSlot.setAdapter(new SlotHorizontalAdapter(context,slotList));
    }

    @Override
    public int getItemCount() {
        return 7;
    }

    public class SlotViewHolder extends RecyclerView.ViewHolder {
        private RecyclerView rvSlot;
        public SlotViewHolder(View itemView) {
            super(itemView);
            rvSlot=itemView.findViewById(R.id.rvSlot);
        }
    }
}