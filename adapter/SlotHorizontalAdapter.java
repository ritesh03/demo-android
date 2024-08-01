package com.maktoday.adapter;

import android.app.Activity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import com.maktoday.R;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by cbl81 on 24/12/17.
 */

public class SlotHorizontalAdapter extends RecyclerView.Adapter<SlotHorizontalAdapter.SlotViewHolder> {

    public Activity context;
    public Set<String> slotList=new HashSet<>();

    public SlotHorizontalAdapter(Activity context, Set<String> slotList) {
        this.context = context;
        this.slotList=slotList;
    }

    @Override
    public SlotViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = context.getLayoutInflater();
        View view = inflater.inflate(R.layout.item_slot_view, parent, false);
        return new SlotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SlotViewHolder holder, int position) {
        if (position + 9 < 12) {
            holder.tvTime.setText((position + 8) + ":00 AM");
        } else if (position + 9 == 12) {
            holder.tvTime.setText((position + 8) + ":00 PM");
        } else {
            holder.tvTime.setText((position - 3) + ":00 PM");
        }
        if (slotList.contains(String.valueOf(position + 8))) {
            holder.tvTime.setTextColor(ContextCompat.getColor(context, R.color.colorGreen));
            holder.tvTime.setChecked(true);
        }
        else {
            holder.tvTime.setTextColor(ContextCompat.getColor(context, R.color.black40));
            holder.tvTime.setChecked(false);
        }
    }

    @Override
    public int getItemCount() {
        return 12;
    }

    public class SlotViewHolder extends RecyclerView.ViewHolder {
        private CheckedTextView tvTime;
        public SlotViewHolder(View itemView)
        {
            super(itemView);
            tvTime=itemView.findViewById(R.id.tvTime);

        }
    }
}
