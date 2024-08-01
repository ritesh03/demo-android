package com.maktoday.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.maktoday.R;
import com.maktoday.model.PojoLogin;
import com.maktoday.model.PojoNotification;
import com.maktoday.utils.Constants;
import com.maktoday.utils.Prefs;
import com.maktoday.views.notification.NotificationActivity;
import com.maktoday.views.ratingdialog.RatingDialogFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {

    private Activity context;
    private List<PojoNotification.Data> notificationDataList = new ArrayList<>();

    public NotificationAdapter(Activity context, List<PojoNotification.Data> notificationDataList) {
        this.context = context;
        this.notificationDataList = notificationDataList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = context.getLayoutInflater();
        View view = inflater.inflate(R.layout.item_rv_notification, parent, false);
        return new NotificationAdapter.MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        try {
            if (notificationDataList.get(position).type.equals("ALL_USER_TYPE")) {
                try {
                    holder.tvBookingNo.setText(context.getString(R.string.message_from_admin));
                }catch (Exception e){
                    e.printStackTrace();
                    try {//----------- IF FIREBASE RETURN EXCEPTION
                        FirebaseCrashlytics.getInstance().recordException(e);
                    } catch (Exception fe) {
                        fe.printStackTrace();
                    }
                }
            } else if (notificationDataList.get(position).type.equals("PLAY_STORE")||notificationDataList.get(position).type.equals("Feedback")) {
                try {
                    holder.tvBookingNo.setText(context.getString(R.string.message_from_admin));
                }catch (Exception e){
                    e.printStackTrace();
                    try {//----------- IF FIREBASE RETURN EXCEPTION
                        FirebaseCrashlytics.getInstance().recordException(e);
                    } catch (Exception fe) {
                        fe.printStackTrace();
                    }
                }
            }else if(notificationDataList.get(position).type.equals("LINK")){

                try {
                    holder.tvBookingNo.setText(context.getString(R.string.message_from_admin));
                }catch (Exception e){
                    e.printStackTrace();
                    try {//----------- IF FIREBASE RETURN EXCEPTION
                        FirebaseCrashlytics.getInstance().recordException(e);
                    } catch (Exception fe) {
                        fe.printStackTrace();
                    }
                }
            }else {
                try {
                    holder.tvBookingNo.setText(context.getString(R.string.booking_number) + " " + notificationDataList.get(position).bookingId);
                }catch (Exception e){
                    e.printStackTrace();
                    try {//----------- IF FIREBASE RETURN EXCEPTION
                        FirebaseCrashlytics.getInstance().recordException(e);
                    } catch (Exception fe) {
                        fe.printStackTrace();
                    }
                }
            }
            try {
                holder.tvDescription.setText(notificationDataList.get(position).userMessage);
            }catch (Exception e){
                e.printStackTrace();
                try {//----------- IF FIREBASE RETURN EXCEPTION
                    FirebaseCrashlytics.getInstance().recordException(e);
                } catch (Exception fe) {
                    fe.printStackTrace();
                }
            }
            try {
                holder.tvTime.setText(getTimeInFormat(notificationDataList.get(position).timeStamp));
            }catch (Exception e){
                e.printStackTrace();
                try {//----------- IF FIREBASE RETURN EXCEPTION
                    FirebaseCrashlytics.getInstance().recordException(e);
                } catch (Exception fe) {
                    fe.printStackTrace();
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
        return notificationDataList.size();
    }

    private String getTimeInFormat(String createdAt) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        df.setTimeZone(TimeZone.getTimeZone("UTC"));

        SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        df1.setTimeZone(TimeZone.getDefault());

        Date date = null;
        String dateString = null;
        try {
            date = df.parse(createdAt);
            dateString = df1.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            try {//----------- IF FIREBASE RETURN EXCEPTION
                FirebaseCrashlytics.getInstance().recordException(e);
            } catch (Exception fe) {
                fe.printStackTrace();
            }
        }
        long epoch = 0;
        if (date != null) {
            Date date1 = null;
            try {
                date1 = df1.parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
                try {//----------- IF FIREBASE RETURN EXCEPTION
                    FirebaseCrashlytics.getInstance().recordException(e);
                } catch (Exception fe) {
                    fe.printStackTrace();
                }
            }
            epoch = date1.getTime();
        }
        return String.valueOf(DateUtils.getRelativeTimeSpanString
                (epoch, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS));
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvBookingNo, tvDescription, tvTime;
        private RelativeLayout parent;

        public MyViewHolder(View itemView) {
            super(itemView);
            parent = itemView.findViewById(R.id.parent);
            tvBookingNo = itemView.findViewById(R.id.tvOrderNumber);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvTime = itemView.findViewById(R.id.tvTime);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        PojoNotification.Data data = notificationDataList.get(getAdapterPosition());
                        if (data.type.equals("SERVICE_COMPLETE") && !data.reviewSubmitted) { //&& !data.read
                            String name = data.maidId.firstName.substring(0, 1) + data.maidId.firstName.substring(1) + " " + data.maidId.lastName.substring(0, 1) + data.maidId.lastName.substring(1);
                            String image = "";
                            if (data.maidId.profilePicURL != null && data.maidId.profilePicURL.getOriginal() != null) {
                                image = data.maidId.profilePicURL.getOriginal();
                            }
                            RatingDialogFragment.newInstance(data.serviceId, name, image).show(((NotificationActivity) context).getSupportFragmentManager(), "RatingDialog");
                        } else if (data.type.equals("PLAY_STORE")|| data.type.equalsIgnoreCase("Feedback")) {
                            String market_uri = "https://play.google.com/store/apps/details?id=com.maktoday&hl=en";
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(market_uri));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            context.startActivity(intent);
                        }else if(data.type.equals("LINK")){
                            String link = data.link;
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(link));
                            context.startActivity(i);
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
            });
        }
    }
}
