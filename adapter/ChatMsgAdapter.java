package com.maktoday.adapter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.maktoday.utils.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.maktoday.R;
import com.maktoday.model.LatestMessage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by cbl1005 on 24/1/18.
 */

public class ChatMsgAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private static final String TAG = "ChatMsgAdapter";
    private static final int USER_MSGID=0;
    private static final int MAID_MSGID=1;
    private static final int USER_IMAGEID=2;
    private static final int MAID_IMAGEID=3;
    private Activity context;
    private List<LatestMessage> msgList = new ArrayList<>();
    private Calendar calendarDate;
    private SimpleDateFormat sdfTime;
    String[]monthName={"Jan","Feb","Mar", "Apr", "May", "Jun", "Jul",
            "Aug", "Sep", "Oct", "Nov",
            "Dec"};

    public ChatMsgAdapter(Activity context,List<LatestMessage> msgList)
    {
        this.context=context;
        this.msgList=msgList;
        calendarDate=Calendar.getInstance();
        sdfTime = new SimpleDateFormat("hh:mm a",Locale.getDefault());
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater=context.getLayoutInflater();
        View view=null;
        if (viewType==MAID_MSGID) {
            view=inflater.inflate(R.layout.rv_item_text_left,parent,false);
            return new ChatViewHolder(view);
        }
        else if(viewType==MAID_IMAGEID) {
            view=inflater.inflate(R.layout.rv_item_image_left,parent,false);
            return new ChatImageViewHolder(view);
        }
        else if(viewType==USER_MSGID) {
            view=inflater.inflate(R.layout.rv_item_text_right,parent,false);
            return new ChatViewHolder(view);
        }
        else {
            view=inflater.inflate(R.layout.rv_item_image_right,parent,false);
            return new ChatImageViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        try {
            calendarDate.setTimeInMillis(Long.parseLong(msgList.get(position).timeStamp));
        }catch (Exception e){
            e.printStackTrace();
            try {//----------- IF FIREBASE RETURN EXCEPTION
                FirebaseCrashlytics.getInstance().recordException(e);
            } catch (Exception fe) {
                fe.printStackTrace();
            }
        }
        if(holder instanceof ChatViewHolder){
            try {
                if (msgList.get(position).message != null && !msgList.get(position).message.isEmpty()) {
                    try {
                        ((ChatViewHolder) holder).tvMsg.setText(msgList.get(position).message);
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
                    ((ChatViewHolder) holder).tvTime.setText(String.format(Locale.getDefault(), "%s %s, %s", calendarDate.get(Calendar.DATE), monthName[calendarDate.get(Calendar.MONTH)], sdfTime.format(calendarDate.getTime())));
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
        else {
            if (((ChatImageViewHolder)holder).mapView != null) {
                // Initialise the MapView
                ((ChatImageViewHolder)holder).mapView.onResume();
                // Set the map ready callback to receive the GoogleMap object
                ((ChatImageViewHolder)holder).mapView.getMapAsync(googleMap -> {
                    try {
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(msgList.get(position).location.get(1), msgList.get(position).location.get(0)), 13f));
                    }catch (Exception e){
                        e.printStackTrace();
                        try {//----------- IF FIREBASE RETURN EXCEPTION
                            FirebaseCrashlytics.getInstance().recordException(e);
                        } catch (Exception fe) {
                            fe.printStackTrace();
                        }
                    }
                    try {
                        googleMap.addMarker(new MarkerOptions().position(new LatLng(msgList.get(position).location.get(1), msgList.get(position).location.get(0))));
                    }catch (Exception e){
                        e.printStackTrace();
                        try {//----------- IF FIREBASE RETURN EXCEPTION
                            FirebaseCrashlytics.getInstance().recordException(e);
                        } catch (Exception fe) {
                            fe.printStackTrace();
                        }
                    }

                    googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    googleMap.getUiSettings().setAllGesturesEnabled(false);
                });
            }

            ((ChatImageViewHolder)holder). mapViewClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        String uri = "google.navigation:q=" + msgList.get(position).location.get(1) + "," + msgList.get(position).location.get(0) + "&mode=l";
                        Log.d(TAG, "onClick: " + uri);
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        context.startActivity(intent);
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

            try {
                ((ChatImageViewHolder) holder).tvTime.setText(String.format(Locale.getDefault(), "%s %s, %s", calendarDate.get(Calendar.DATE), monthName[calendarDate.get(Calendar.MONTH)], sdfTime.format(calendarDate.getTime())));
            }catch (Exception e){
                e.printStackTrace();
                try {//----------- IF FIREBASE RETURN EXCEPTION
                    FirebaseCrashlytics.getInstance().recordException(e);
                } catch (Exception fe) {
                    fe.printStackTrace();
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return msgList.size();
    }

    @Override
    public int getItemViewType(int position) {

        try {
            if (msgList.get(position).senderType.equals("USER") && msgList.get(position).messageType.equals("LOCATION")) {
                return USER_IMAGEID;
            } else if (msgList.get(position).senderType.equals("USER") && msgList.get(position).messageType.equals("MESSAGE")) {
                return USER_MSGID;
            } else if (msgList.get(position).senderType.equals("MAID") && msgList.get(position).messageType.equals("LOCATION")) {
                return MAID_IMAGEID;
            } else {
                return MAID_MSGID;
            }
        }catch (Exception e){
            e.printStackTrace();
            try {//----------- IF FIREBASE RETURN EXCEPTION
                FirebaseCrashlytics.getInstance().recordException(e);
            } catch (Exception fe) {
                fe.printStackTrace();
            }
            return  MAID_MSGID;//------------If Exception open this------
        }

    }

    public class ChatViewHolder extends RecyclerView.ViewHolder{
        private TextView tvTime,tvMsg;
        public ChatViewHolder(View itemView) {
            super(itemView);
            tvMsg=itemView.findViewById(R.id.tvMsg);
            tvTime=itemView.findViewById(R.id.tvTime);
        }
    }
    public class ChatImageViewHolder extends RecyclerView.ViewHolder{
      //  private ImageView ivMap;
        private TextView tvTime;
        private View mapViewClick;
        private MapView mapView;
        public ChatImageViewHolder(View itemView) {
            super(itemView);
            tvTime=itemView.findViewById(R.id.tvTime);
            // ivMap=itemView.findViewById(R.id.ivMap);
            mapView = itemView.findViewById(R.id.mapView);
            tvTime=itemView.findViewById(R.id.tvTime);
            mapViewClick = itemView.findViewById(R.id.mapViewClick);


            if(mapView!= null) {
                mapView.onCreate(null);
            }
        }
    }
}
