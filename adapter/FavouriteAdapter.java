package com.maktoday.adapter;

import android.app.Activity;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.maktoday.R;
import com.maktoday.interfaces.OpenMaid;
import com.maktoday.interfaces.UpdateFavourite;
import com.maktoday.model.MaidData;
import com.maktoday.model.SearchMaidModel;
import com.maktoday.utils.Constants;
import com.maktoday.utils.Log;
import com.maktoday.utils.MaidUtils;
import com.maktoday.utils.Prefs;
import com.maktoday.views.bookagain.BookAgainActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by cbl1005 on 8/2/18.
 */

public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.FavouriteViewHolder> {
    private Activity context;
    private List<MaidData> listFavourite = new ArrayList<>();
    private String currency="";
    private UpdateFavourite updateFavourite;
    private OpenMaid openMaid;
    private static final String TAG = "FavouriteAdapter";

    public FavouriteAdapter(Activity context, List<MaidData> listFavourite, UpdateFavourite updateFavourite, OpenMaid openMaid) {
        this.context = context;
        this.listFavourite = listFavourite;
        this.openMaid=openMaid;
        String country= Prefs.with(context).getString(Constants.COUNTRY_NAME,"");
        this.updateFavourite=updateFavourite;
        if(country.contains("United Arab Emirates")){
            currency="AED";
        }
        else {
            currency="BHD";
        }
    }

    @Override
    public FavouriteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = context.getLayoutInflater();
        View view = inflater.inflate(R.layout.item_rv_favourite, parent, false);
        return new FavouriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FavouriteViewHolder holder, int position) {
        try {
            MaidData maidData = listFavourite.get(position);
            if (maidData != null) {
                try {
                   /* if (maidData.getProfilePicURL() != null
                            && maidData.getProfilePicURL().getThumbnail() != null
                            && !maidData.getProfilePicURL().getThumbnail().isEmpty()) {
                        try {
                            Glide.with(context)
                                    .load(maidData.getProfilePicURL().getThumbnail())
                                    .circleCrop()
                                    .placeholder(R.drawable.ic_user_pic)
                                    .into(holder.ivMaid);
                        }catch (Exception e){
                            e.printStackTrace();
                            try {//----------- IF FIREBASE RETURN EXCEPTION
                                FirebaseCrashlytics.getInstance().recordException(e);
                            } catch (Exception fe) {
                                fe.printStackTrace();
                            }
                        }
                    }
                    else*/ if (maidData.agencyImage != null
                            && maidData.agencyImage.getOriginal() != null
                            && !maidData.agencyImage.getOriginal().isEmpty()
                    ) {
                        try {
                            Glide.with(context)
                                    .load(maidData.agencyImage.getOriginal())
                                    .circleCrop()
                                    .placeholder(R.drawable.ic_user_pic)
                                    .into(holder.ivMaid);
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
                        try {
                            Glide.with(context)
                                    .load(R.drawable.ic_user_pic)
                                    .circleCrop()
                                    .placeholder(R.drawable.ic_user_pic)
                                    .into(holder.ivMaid);
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
                }
                String firstName = "";
                String lastName = "";
                try {
                    if (maidData.getFirstName() != null && !maidData.getFirstName().isEmpty()) {
                        firstName = maidData.getFirstName();
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
                    if (maidData.getLastName() != null && !maidData.getLastName().isEmpty()) {
                        lastName = maidData.getLastName();
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
                    if (!lastName.isEmpty()) {
                        holder.tvMaidName.setText(String.format("%s", firstName.substring(0, 1).toUpperCase() + firstName.substring(1)) + " " +
                                maidData.getLastName().substring(0, 1).toUpperCase() + maidData.getLastName().substring(1));

                    } else {
                        holder.tvMaidName.setText(String.format("%s", firstName.substring(0, 1).toUpperCase() + firstName.substring(1)));
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
                    holder.tvAgencyName.setText(String.format("%s", maidData.getAgencyName()));
                }catch (Exception e){
                    e.printStackTrace();
                    try {//----------- IF FIREBASE RETURN EXCEPTION
                        FirebaseCrashlytics.getInstance().recordException(e);
                    } catch (Exception fe) {
                        fe.printStackTrace();
                    }
                }
                try {
                    if (maidData.getCurrency().equalsIgnoreCase("BHD")) {
                        try {
                            holder.tvRate.setText(String.format(Locale.US, "%s %s %.3f", context.getString(R.string.price_per_hour), maidData.getCurrency(), maidData.getActualPrice()));
                        }catch (Exception e){
                            e.printStackTrace();
                            try {//----------- IF FIREBASE RETURN EXCEPTION
                                FirebaseCrashlytics.getInstance().recordException(e);
                            } catch (Exception fe) {
                                fe.printStackTrace();
                            }
                        }
                    } else {
                        try {
                            holder.tvRate.setText(String.format(Locale.US, "%s %s %.2f", context.getString(R.string.price_per_hour), maidData.getCurrency(), maidData.getActualPrice()));
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

                try {
                    double maidRating = MaidUtils.getMaidRating(maidData);
                    holder.ratingBar.setRating(Float.valueOf(String.format(Locale.US, "%.1f", maidRating)));
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
        return listFavourite.size();
    }

    public class FavouriteViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivMaid,ivFavourite;
        private TextView tvMaidName,tvAgencyName,tvRate,tvBook;
        private RatingBar ratingBar;

        public FavouriteViewHolder(View itemView) {
            super(itemView);
            ivMaid = itemView.findViewById(R.id.ivMaid);
            tvMaidName=itemView.findViewById(R.id.tvMaidName);
            tvAgencyName=itemView.findViewById(R.id.tvAgencyName);
            tvRate=itemView.findViewById(R.id.tvRate);
            ratingBar=itemView.findViewById(R.id.rbRating);
            ivFavourite=itemView.findViewById(R.id.ivFavourite);
            tvBook=itemView.findViewById(R.id.tvBook);
            ivFavourite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        updateFavourite.removeFavourite(listFavourite.get(getAdapterPosition()).get_id());
                    }catch ( Exception e){
                        e.printStackTrace();
                        try {//----------- IF FIREBASE RETURN EXCEPTION
                            FirebaseCrashlytics.getInstance().recordException(e);
                        } catch (Exception fe) {
                            fe.printStackTrace();
                        }
                    }
                }
            });

            tvBook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        SearchMaidModel searchMaidModel = new SearchMaidModel();
                        MaidData maidData = listFavourite.get(getAdapterPosition());
                        String firstName = maidData.getFirstName();
                        String lastName = maidData.getLastName();

                        if (lastName != null && !lastName.isEmpty()) {
                            searchMaidModel.maidName = String.format("%s %s", firstName.substring(0, 1).toUpperCase() + firstName.substring(1), lastName.substring(0, 1).toUpperCase() + lastName.substring(1));
                        }
                        searchMaidModel.maidId = maidData.get_id();
                        searchMaidModel.makId = maidData.getMakId();
                        searchMaidModel.maidPrice = maidData.getActualPrice();
                        searchMaidModel.agencyName = maidData.getAgencyName();
                        searchMaidModel.currency = maidData.getCurrency();
                        searchMaidModel.agencyType = maidData.getAgencyType();
                        searchMaidModel.services = maidData.getServices();
                        /*searchMaidModel.servicesID=maidData.services._id;*/

                       /* if (maidData.getProfilePicURL() != null && maidData.getProfilePicURL().getOriginal() != null && !maidData.getProfilePicURL().getOriginal().isEmpty()) {
                            searchMaidModel.profilePicURL = maidData.getProfilePicURL();
                        } else*/ if (maidData.agencyImage.getOriginal() != null && maidData.agencyImage.getOriginal() != null
                                && !maidData.agencyImage.getOriginal().isEmpty()) {
                            searchMaidModel.profilePicURL = maidData.agencyImage;
                        }

                        Intent intent = new Intent(context, BookAgainActivity.class);
                        intent.putExtra(Constants.SEARCH_MAID_DATA, searchMaidModel);
                        intent.putExtra(Constants.reschuleStatus, "" + "");
                        intent.putExtra(Constants.isFavorite, "yes");
                        Log.d(TAG, "onClick: maid data on click book  " + new Gson().toJson(maidData));
                        intent.putExtra(Constants.MAID_DATA, new Gson().toJson(maidData));
                        intent.putExtra(Constants.MAID_AVAILABLE_TIMESLOT, listFavourite.get(getAdapterPosition()).timeSlot);
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

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Log.d(TAG, "onClick: favourite click list:--  " + new Gson().toJson(listFavourite.get(getAdapterPosition()).isFavourite()));
                        openMaid.openMaidProfile(listFavourite.get(getAdapterPosition()));
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