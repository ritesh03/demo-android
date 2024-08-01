package com.maktoday.adapter;

import android.app.Activity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.joooonho.SelectableRoundedImageView;
import com.maktoday.R;
import com.maktoday.interfaces.AgencySelection;
import com.maktoday.model.PojoAgencyList;

import java.util.ArrayList;

/**
 * Created by cbl81 on 28/10/17.
 */

public class AgencyAdapter extends RecyclerView.Adapter<AgencyAdapter.MyViewHolder> {

    private Context context;
    private Activity activity;
    private ArrayList<PojoAgencyList.Data> agencyList=new ArrayList<>();
    public ArrayList<String> selectedAgencyList=new ArrayList<>();
    private int count=0;


    public AgencyAdapter(Activity context, ArrayList<PojoAgencyList.Data> agencyList) {

        this.context=context;
        this.agencyList=agencyList;
        count=agencyList.size();
    }

    @Override
    public AgencyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater=activity.getLayoutInflater();
        View view=inflater.inflate(R.layout.item_rv_agencylist,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AgencyAdapter.MyViewHolder holder, int position) {


        try {//----------- Exception profile pic
            if (agencyList.get(position).getProfilePicURL() != null
                    && agencyList.get(position).getProfilePicURL().getThumbnail() != null) {
                Glide.with(context)
                        .load(agencyList.get(position).getProfilePicURL().getThumbnail())
                        .circleCrop()
                        .placeholder(R.drawable.ic_agency_placeholder)
                        .into(holder.ivAgency);
            }
        }catch (Exception e){
            e.printStackTrace();
            try {//----------- if firbase return exception
                FirebaseCrashlytics.getInstance().recordException(e);
            } catch (Exception fe) {
                fe.printStackTrace();
            }
        }


    try {//============= Exception hanle get Agency name
        String name = agencyList.get(position).getAgencyName();
        holder.tvAgencyName.setText(String.format("%s%s", name.substring(0, 1).toUpperCase(), name.substring(1)));
    }catch ( Exception e){
        e.printStackTrace();
        try {//----------- if firbase return exception
            FirebaseCrashlytics.getInstance().recordException(e);
        } catch (Exception fe) {
            fe.printStackTrace();
        }
    }

    try {//------- Exception handle maid count
        if (agencyList.get(position).getMaidCount() == 1)
            holder.tvNoMaid.setText(String.format("%s%s", String.valueOf(agencyList.get(position).getMaidCount()), context.getString(R.string.single_maid)));
        else
            holder.tvNoMaid.setText(String.format("%s %s", String.valueOf(agencyList.get(position).getMaidCount()), context.getString(R.string.multiple_maids)));
    }catch (Exception e){
        e.printStackTrace();
        try {//----------- if firbase return exception
            FirebaseCrashlytics.getInstance().recordException(e);
        } catch (Exception fe) {
            fe.printStackTrace();
        }
    }
       try {//------------------- Exception handle get Rating
           holder.tvAgencyRating.setText(String.format("%.1f", agencyList.get(position).getRating()));
       }catch (Exception e){
           e.printStackTrace();
           try {//----------- if firbase return exception
               FirebaseCrashlytics.getInstance().recordException(e);
           } catch (Exception fe) {
               fe.printStackTrace();
           }
       }
       try {//-------- Exception handle getSelected
           holder.cbSelected.setChecked(agencyList.get(position).getSelected());
       }catch (Exception e){
           e.printStackTrace();
           try {//----------- if firbase return exception
               FirebaseCrashlytics.getInstance().recordException(e);
           } catch (Exception fe) {
               fe.printStackTrace();
           }
       }
    }

    @Override
    public int getItemCount() {
        return agencyList.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        private SelectableRoundedImageView ivAgency;
        private TextView tvNoMaid,tvAgencyRating,tvAgencyName;
        private CheckBox cbSelected;
        public MyViewHolder(View itemView) {
            super(itemView);
            ivAgency=itemView.findViewById(R.id.ivAgency);
            tvNoMaid=itemView.findViewById(R.id.tvNoMaid);
            tvAgencyRating=itemView.findViewById(R.id.tvAgencyRating);
            tvAgencyName=itemView.findViewById(R.id.tvAgencyName);
            cbSelected=itemView.findViewById(R.id.cbSelected);
            cbSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    try {//------ Exception hadle click on checkbox
                        if (b) {
                            count++;
                            selectedAgencyList.add(agencyList.get(getAdapterPosition()).get_id());
                        } else {
                            count--;
                            selectedAgencyList.remove(agencyList.get(getAdapterPosition()).get_id());
                        }
                        if (count == agencyList.size()) {
                            ((AgencySelection) context).setSelectAll(true);
                        } else {
                            ((AgencySelection) context).setSelectAll(false);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        try {//----------- if firbase return exception
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