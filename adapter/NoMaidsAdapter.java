package com.maktoday.adapter;

import android.app.Activity;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.maktoday.R;
import com.maktoday.interfaces.NoMaidSelection;

/**
 * Created by cbl81 on 30/10/17.
 */

public class NoMaidsAdapter extends RecyclerView.Adapter<NoMaidsAdapter.MyViewHolder> {

    public int selectedPosition;
    private Activity context;

    private NoMaidSelection timeSelection;

    public NoMaidsAdapter(Activity context, NoMaidSelection timeSelection, Boolean isExtend) {
        this.context = context;
        this.timeSelection = timeSelection;
        timeSelection.setSelectedMaids(selectedPosition + 2);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = context.getLayoutInflater();
        View view = inflater.inflate(R.layout.item_rv_timeduration, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.tvDuration.setText(String.valueOf(position + 2));

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
        return 99;
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

                    timeSelection.setSelectedMaids(selectedPosition + 2);

                    notifyDataSetChanged();

                }
            });

        }
    }
}
