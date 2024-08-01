package com.maktoday.adapter;

import android.app.Activity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.maktoday.R;
import com.maktoday.model.MaidRating;
import com.maktoday.model.PojoReview;
import com.maktoday.utils.GeneralFunction;
import com.maktoday.utils.Log;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by cbl81 on 26/10/17.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.MyViewHolder> {
    private static final String TAG = "ReviewAdapter";
    private Activity activity;
    private List<PojoReview> reviewList=new ArrayList<>();

    public ReviewAdapter(Activity activity, List<PojoReview> reviewList) {
        this.activity = activity;
        this.reviewList=reviewList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater=activity.getLayoutInflater();
        View view=inflater.inflate(R.layout.item_rv_reviews,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        try {
            Log.e("ddd", new Gson().toJson(reviewList.get(position)));

            MaidRating maidRating = reviewList.get(position).maidRating;
            try {
                Double rating = 0.0;
                int count = 0;

                if (maidRating.cooking != 0.0) {
                    count++;
                }
                if (maidRating.ironing != 0.0) {
                    count++;
                }
                if (maidRating.cleaning != 0.0) {
                    count++;
                }
                if (maidRating.childCare != 0.0) {
                    count++;
                }

                rating = (maidRating.cooking
                        + maidRating.ironing + maidRating.cleaning
                        + maidRating.childCare) / Double.parseDouble(String.valueOf(count));
                if (rating.isNaN()) {
                    rating = 0.0;
                }

                if(rating != 0) {
                    holder.tvRating.setVisibility(View.VISIBLE);
                    holder.tvRating.setText(String.format(Locale.ENGLISH, "%.1f", rating));
                }else{
                    holder.tvRating.setVisibility(View.GONE);
                }
            }catch (Exception e){
                e.printStackTrace();
                try {//----------- IF FIREBASE RETURN EXCEPTION
                    FirebaseCrashlytics.getInstance().recordException(e);
                } catch (Exception fe) {
                    fe.printStackTrace();
                }
            }

            try {
                Log.e(TAG, "onBindViewHolder: tvReview :-- "+ reviewList.get(position).description);
                if (reviewList.get(position).description != null && !reviewList.get(position).description.isEmpty()) {
                    try {
                        holder.tvReviews.setVisibility(View.VISIBLE);
                        holder.tvReviews.setText(reviewList.get(position).description);
                    }catch (Exception e){
                        e.printStackTrace();
                        try {//----------- IF FIREBASE RETURN EXCEPTION
                            FirebaseCrashlytics.getInstance().recordException(e);
                        } catch (Exception fe) {
                            fe.printStackTrace();
                        }
                    }
                } else {
                    holder.tvReviews.setText("");
                }
            }catch (Exception e){
                e.printStackTrace();
                try {//----------- IF FIREBASE RETURN EXCEPTION
                    FirebaseCrashlytics.getInstance().recordException(e);
                } catch (Exception fe) {
                    fe.printStackTrace();
                }
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);

            if (reviewList.get(position).userId != null) {
                try {
                    if (reviewList.get(position).userId.fullName == null) {
                        holder.tvDate.setText(GeneralFunction.getFormatFromDate(dateFormat.parse(reviewList.get(position).timeStamp), "dd MMM yyyy"));
                    } else {
                        holder.tvDate.setText(/*reviewList.get(position).userId.fullName + " " +*/ GeneralFunction.getFormatFromDate(dateFormat.parse(reviewList.get(position).timeStamp), "dd MMM yyyy"));
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    try {//----------- IF FIREBASE RETURN EXCEPTION
                        FirebaseCrashlytics.getInstance().recordException(e);
                    } catch (Exception fe) {
                        fe.printStackTrace();
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            try {//----------- IF FIREBASE RETURN EXCEPTION
                FirebaseCrashlytics.getInstance().recordException(e);
            } catch (Exception fe) {
                fe.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView tvRating,tvReviews,tvDate;
        public MyViewHolder(View itemView) {
            super(itemView);
            tvRating=itemView.findViewById(R.id.tvRating);
            tvReviews=itemView.findViewById(R.id.tvReviews);
            tvDate=itemView.findViewById(R.id.tvDate);
        }
    }
}
