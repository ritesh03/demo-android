package com.maktoday.adapter;

import android.app.Activity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.maktoday.R;

import java.text.DateFormatSymbols;

/**
 * Created by cbl81 on 26/12/17.
 */

public class WeekAdapter extends RecyclerView.Adapter<WeekAdapter.WeekHolder> {

    public Activity context;

    public WeekAdapter(Activity context) {
        this.context = context;
    }

    @Override
    public WeekAdapter.WeekHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = context.getLayoutInflater();
        View view = inflater.inflate(R.layout.item_rv_week, parent, false);
        return new WeekAdapter.WeekHolder(view);

    }

    @Override
    public void onBindViewHolder(WeekAdapter.WeekHolder holder, int position) {
        String weekday = new DateFormatSymbols().getShortWeekdays()[position+1];
        holder.tvWeek.setText(weekday);
    }

    @Override
    public int getItemCount() {
        return 7;
    }

    public class WeekHolder extends RecyclerView.ViewHolder {
        private TextView tvWeek;

        public WeekHolder(View itemView) {
            super(itemView);
            tvWeek = itemView.findViewById(R.id.tvWeek);
        }
    }
}