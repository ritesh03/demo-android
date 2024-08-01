package com.maktoday.adapter;

import android.app.Activity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.maktoday.R;
import com.maktoday.interfaces.TimeSelection;

/**
 * Created by cbl81 on 30/10/17.
 */

public class TimeDurationAdapter extends RecyclerView.Adapter<TimeDurationAdapter.MyViewHolder> {

    public int selectedPosition;
    private Activity context;
    private boolean isSelected;
    private Boolean isExtend;
    private TimeSelection timeSelection;

    public TimeDurationAdapter(Activity context, TimeSelection timeSelection, Boolean isExtend) {
        this.context = context;
        this.timeSelection = timeSelection;
        this.isExtend = isExtend;
       // Toast.makeText(context, ""+isExtend, Toast.LENGTH_SHORT).show();
        if (isExtend)
            timeSelection.setSelectedDuration(selectedPosition + 1);
        else
            timeSelection.setSelectedDuration(selectedPosition + 3);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = context.getLayoutInflater();
        View view = inflater.inflate(R.layout.item_rv_timeduration, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if (isExtend)
            holder.tvDuration.setText(String.valueOf(position + 1));
        else
            holder.tvDuration.setText(String.valueOf(position + 3));

        if (selectedPosition == position) {
            holder.tvDuration.setTextColor(ContextCompat.getColor(context, android.R.color.white));
            holder.tvDuration.setBackground(ContextCompat.getDrawable(context, R.drawable.circle_skyblue));
        } else {
            holder.tvDuration.setTextColor(ContextCompat.getColor(context, R.color.colorBlack65));
            holder.tvDuration.setBackground(null);
        }
    }

    @Override
    public int getItemCount() {
        if (isExtend)
            return 12;
        else
            return 10;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvDuration;

        public MyViewHolder(final View itemView) {
            super(itemView);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            tvDuration.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake_error));
                    selectedPosition = getAdapterPosition();
                    if (isExtend)
                        timeSelection.setSelectedDuration(selectedPosition + 1);
                    else
                        timeSelection.setSelectedDuration(selectedPosition + 3);

                    notifyDataSetChanged();
                }
            });

        }
    }
}
