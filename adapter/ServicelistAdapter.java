package com.maktoday.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.maktoday.R;
import com.maktoday.model.ServicesProvide;
import com.maktoday.utils.Log;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import java.util.List;

public class ServicelistAdapter extends RecyclerView.Adapter<ServicelistAdapter.ViewHolder> {
     Context mcontext;
     List<ServicesProvide> servicelist;
     int selectedPosition=-1;

    ServiceOnclick serviceOnclick;
    public ServicelistAdapter(FragmentActivity activity, List<ServicesProvide> servicelist, ServiceOnclick serviceOnclick) {
        this.mcontext=activity;
        this.servicelist=servicelist;
        this.serviceOnclick=serviceOnclick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.service_list_adapter,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
      //-------SetService Name------
        try {
            try {
                holder.tvServiceName.setText(servicelist.get(position).getName());
            }catch (Exception e){
                e.printStackTrace();
                try {//----------- IF FIREBASE RETURN EXCEPTION
                    FirebaseCrashlytics.getInstance().recordException(e);
                } catch (Exception fe) {
                    fe.printStackTrace();
                }
            }
   //------- Set Service Desc
            try{
                holder.tvServiceDesc.setText(servicelist.get(position).getDescription());
            }catch (Exception e){
                e.printStackTrace();
                try {//----------- IF FIREBASE RETURN EXCEPTION
                    FirebaseCrashlytics.getInstance().recordException(e);
                } catch (Exception fe) {
                    fe.printStackTrace();
                }
            }
    //-----Set ICON--------
          try{
              if (servicelist.get(position).getImage() != null && servicelist.get(position).getImage().getOriginal() != null
                      && !servicelist.get(position).getImage().getOriginal().isEmpty()) {
                  Log.e("image url", servicelist.get(position).getImage().getOriginal());
                  Glide.with(mcontext)
                          .load(servicelist.get(position).getImage().getOriginal())
                          //.load("https://www.pngplay.com/wp-content/uploads/6/Exam-Logo-Background-PNG-Image.png")
                          .into(holder.ivServiceImage);
              }  else if (servicelist.get(position).getImage() != null && servicelist.get(position).getImage().getThumbnail() != null
                      && !servicelist.get(position).getImage().getThumbnail().isEmpty()) {
                  Log.e("image url", servicelist.get(position).getImage().getThumbnail());
                  Glide.with(mcontext)
                          .load(servicelist.get(position).getImage().getOriginal())
                          .into(holder.ivServiceImage);
              }
          }catch (Exception e){
              e.printStackTrace();
              try {//----------- IF FIREBASE RETURN EXCEPTION
                  FirebaseCrashlytics.getInstance().recordException(e);
              } catch (Exception fe) {
                  fe.printStackTrace();
              }
          }


            holder.rlParent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        serviceOnclick.itemonclick(servicelist.get(position).get_id(), servicelist.get(position).getName());
                        selectedPosition = position;
                        notifyDataSetChanged();
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

            if (selectedPosition == position) {
                //  holder.cvService.setBackground(ContextCompat.getDrawable(mcontext,R.drawable.rect_border_sky_service));
//            holder.tvServiceName.setTextColor(Color.parseColor("#7490c7"));
                try {
                    holder.rlParent.setBackground(mcontext.getResources().getDrawable(R.drawable.bg_service_selected_border));

                }catch (Exception e){
                    e.printStackTrace();
                    try {//----------- IF FIREBASE RETURN EXCEPTION
                        FirebaseCrashlytics.getInstance().recordException(e);
                    } catch (Exception fe) {
                        fe.printStackTrace();
                    }
                }
                try {
                    holder.tvServiceName.setTextColor(mcontext.getResources().getColor(R.color.white));
                    holder.tvServiceName.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                    holder.tvServiceDesc.setTextColor(mcontext.getResources().getColor(R.color.white));
                    ColorStateList csl = AppCompatResources.getColorStateList(mcontext, R.color.white);

                    holder.ivServiceImage.setImageTintList(csl);


                }catch (Exception e){
                    try {//----------- IF FIREBASE RETURN EXCEPTION
                        FirebaseCrashlytics.getInstance().recordException(e);
                    } catch (Exception fe) {
                        fe.printStackTrace();
                    }
                }
            } else {
                try {

                    holder.rlParent.setBackgroundColor(mcontext.getResources().getColor(R.color.white));
                    holder.rlParent.setBackground(mcontext.getResources().getDrawable(R.drawable.bg_service_unselect));

                }catch (Exception e){
                    e.printStackTrace();
                    try {//----------- IF FIREBASE RETURN EXCEPTION
                        FirebaseCrashlytics.getInstance().recordException(e);
                    } catch (Exception fe) {
                        fe.printStackTrace();
                    }
                }
                // holder.cvService.setBackground(ContextCompat.getDrawable(mcontext,R.drawable.rect_service_white));
                try {

                    holder.tvServiceName.setTextColor(mcontext.getResources().getColor(R.color.app_grey));
                    holder.tvServiceName.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                    holder.tvServiceDesc.setTextColor(mcontext.getResources().getColor(R.color.app_grey));
                    ColorStateList csl = AppCompatResources.getColorStateList(mcontext, R.color.black);

                    holder.ivServiceImage.setImageTintList(csl);


                }catch (Exception e){
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
        return servicelist.size();
    }

    public void adddata(List<ServicesProvide> servicelist) {
        this.servicelist=servicelist;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvServiceName;
        RelativeLayout rlParent;

        TextView tvServiceDesc;
        ImageView ivServiceImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
               tvServiceName=itemView.findViewById(R.id.tvServiceName);
               rlParent=itemView.findViewById(R.id.rlParent);

               tvServiceDesc = itemView.findViewById(R.id.tvServiceDesc);
               ivServiceImage = itemView.findViewById(R.id.ivService);
        }
    }
    public interface ServiceOnclick{

        void itemonclick(String id,String name);
    }
}
