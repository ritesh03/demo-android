package com.maktoday.adapter;

import android.app.Activity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.maktoday.R;

import java.util.ArrayList;
import java.util.List;

public class TutorialAdapter extends RecyclerView.Adapter<TutorialAdapter.TutorialHolder> {

    public Activity context;
    private List<Integer> imageList=new ArrayList<>();

    public TutorialAdapter(Activity context, List<Integer> imageList) {
        this.context = context;
        this.imageList=imageList;
    }

    @Override
    public TutorialHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = context.getLayoutInflater();
        View view = inflater.inflate(R.layout.item_tutorial, parent, false);
        return new TutorialHolder(view);

    }

    @Override
    public void onBindViewHolder(TutorialHolder holder, int position) {
       // holder.ivTutorial.setImageResource(imageList.get(position));
        Glide.with(context)
                .load(imageList.get(position))
                .placeholder(R.drawable.signup_bg)
                .into(holder.ivTutorial);
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    class TutorialHolder extends RecyclerView.ViewHolder {
        private ImageView ivTutorial;
        TutorialHolder(View itemView)
        {
            super(itemView);
            ivTutorial=itemView.findViewById(R.id.ivTutorial);
        }
    }
}

