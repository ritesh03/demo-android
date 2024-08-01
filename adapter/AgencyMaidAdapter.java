package com.maktoday.adapter;

import android.app.Activity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.maktoday.R;

/**
 * Created by cbl81 on 27/10/17.
 */

public class AgencyMaidAdapter extends RecyclerView.Adapter<AgencyMaidAdapter.MyViewHolder>{

    private Activity activity;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    public AgencyMaidAdapter(Activity activity) {
        this.activity = activity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater=activity.getLayoutInflater();
        View view=inflater.inflate(R.layout.item_rv_agency,parent,false);
        return new AgencyMaidAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 15;
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {
        public MyViewHolder(View itemView) {
            super(itemView);
        }
    }
}

