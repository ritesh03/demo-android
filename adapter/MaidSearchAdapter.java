package com.maktoday.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.joooonho.SelectableRoundedImageView;
import com.maktoday.R;
import com.maktoday.interfaces.StartFragment;
import com.maktoday.model.MaidData;
import com.maktoday.model.PojoSearchMaid;
import com.maktoday.utils.Constants;
import com.maktoday.utils.DialogPopup;
import com.maktoday.utils.Log;
import com.maktoday.utils.Prefs;
import com.maktoday.utils.dialog.IOSAlertDialog;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by cbl81 on 1/11/17.
 */

public class MaidSearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_ITEM = 1;
    private final MaidSearchCallback callback;
    public Integer secondHeader = null;
    private Activity activity;
    private Context context;
    private ArrayList<MaidData> maidDataList = new ArrayList<>();
    private StartFragment startFragment;
    private Boolean listType = false;
    private String currency = "";
    private boolean isFavourite = false;
    private boolean isSearch = false;

    private static final String TAG = "MaidSearchAdapter";
    PojoSearchMaid searchMaidData1;

    public MaidSearchAdapter(Activity context, ArrayList<MaidData> maidDataList, Integer secondHeader, StartFragment startFragment, Boolean listtype, MaidSearchCallback callback, PojoSearchMaid searchMaidData1) {
        this.maidDataList = maidDataList;
        this.activity = context;
        this.listType = listtype;
        this.context = context;
        this.secondHeader = secondHeader;
        this.startFragment = startFragment;
        this.searchMaidData1 = searchMaidData1;
        String country = Prefs.with(context).getString(Constants.COUNTRY_NAME, "");
        if (country.contains("United Arab Emirates")) {
            currency = "AED";
        } else {
            currency = "BHD";
        }
        this.callback = callback;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = null;

        if (viewType == TYPE_HEADER) {
            view = inflater.inflate(R.layout.item_header_searchmaid, parent, false);
            return new HeaderViewHolder(view);
        } else if (viewType == TYPE_ITEM) {
            view = inflater.inflate(R.layout.item_rv_agency, parent, false);
            return new MyViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyViewHolder) {

            // Log.e("ddd",""+new Gson().toJson(maidDataList.get(position)));
            Log.e("maidDataList", "" + maidDataList.size());

            MyViewHolder viewHolder = (MyViewHolder) holder;

            try {

                if (maidDataList.get(position) != null) {
                   /* if (maidDataList.get(position).getProfilePicURL() != null
                            && maidDataList.get(position).getProfilePicURL().getOriginal() != null
                            && !maidDataList.get(position).getProfilePicURL().getOriginal().isEmpty()) {

                        try {
                            Glide.with(context)
                                    .load(maidDataList.get(position).getProfilePicURL().getOriginal())
                                    .circleCrop()
                                    .placeholder(R.drawable.ic_user_pic)
                                    .into(viewHolder.ivMaid);

                        } catch (Exception e) {
                            e.printStackTrace();
                            try {//----------- IF FIREBASE RETURN EXCEPTION
                                FirebaseCrashlytics.getInstance().recordException(e);
                            } catch (Exception fe) {
                                fe.printStackTrace();
                            }
                        }
                    } else*/ if (maidDataList.get(position).agencyImage != null
                            && maidDataList.get(position).agencyImage.getThumbnail() != null
                            && !maidDataList.get(position).agencyImage.getThumbnail().isEmpty()
                    ) {
                        try {
                            Glide.with(context)
                                    .load(maidDataList.get(position).agencyImage.getThumbnail())
                                    .placeholder(R.drawable.ic_user_pic)
                                    .circleCrop()

                                    .into(viewHolder.ivMaid);
                        } catch (Exception e) {
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
                                    .into(viewHolder.ivMaid);
                        } catch (Exception e) {
                            e.printStackTrace();
                            try {//----------- IF FIREBASE RETURN EXCEPTION
                                FirebaseCrashlytics.getInstance().recordException(e);
                            } catch (Exception fe) {
                                fe.printStackTrace();
                            }
                        }
                    }
                    if (listType) {
                        viewHolder.ivClock.setVisibility(View.VISIBLE);
                    } else
                        viewHolder.ivClock.setVisibility(View.GONE);

                    String firstname = "";
                    try {
                        if (maidDataList.get(position).getFirstName() != null && !maidDataList.get(position).getFirstName().isEmpty()) {
                            firstname = maidDataList.get(position).getFirstName().substring(0, 1).toUpperCase() + maidDataList.get(position).getFirstName().substring(1);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        try {//----------- IF FIREBASE RETURN EXCEPTION
                            FirebaseCrashlytics.getInstance().recordException(e);
                        } catch (Exception fe) {
                            fe.printStackTrace();
                        }
                    }
                    String lastName = "";
                    try {
                        if (maidDataList.get(position).getLastName() != null && !maidDataList.get(position).getLastName().isEmpty()) {
                            lastName = maidDataList.get(position).getLastName().substring(0, 1).toUpperCase() + maidDataList.get(position).getLastName().substring(1);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        try {//----------- IF FIREBASE RETURN EXCEPTION
                            FirebaseCrashlytics.getInstance().recordException(e);
                        } catch (Exception fe) {
                            fe.printStackTrace();
                        }
                    }
                    try {
                        viewHolder.tvMaidName.setText(String.format("%s %s", firstname, lastName));
                    } catch (Exception e) {
                        e.printStackTrace();
                        try {//----------- IF FIREBASE RETURN EXCEPTION
                            FirebaseCrashlytics.getInstance().recordException(e);
                        } catch (Exception fe) {
                            fe.printStackTrace();
                        }
                    }

                    try {
                        String languagee = Prefs.with(activity).getString(Constants.LANGUAGE_CODE, "");
                        if (languagee.equalsIgnoreCase("ar")) {
                            try {
                                viewHolder.tvExperience.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                            } catch (Exception e) {
                                e.printStackTrace();
                                try {//----------- IF FIREBASE RETURN EXCEPTION
                                    FirebaseCrashlytics.getInstance().recordException(e);
                                } catch (Exception fe) {
                                    fe.printStackTrace();
                                }
                            }
                        } else {
                            try {
                                viewHolder.tvExperience.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                            } catch (Exception e) {
                                e.printStackTrace();
                                try {//----------- IF FIREBASE RETURN EXCEPTION
                                    FirebaseCrashlytics.getInstance().recordException(e);
                                } catch (Exception fe) {
                                    fe.printStackTrace();
                                }
                            }
                            //   viewHolder.tvExperience.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        try {//----------- IF FIREBASE RETURN EXCEPTION
                            FirebaseCrashlytics.getInstance().recordException(e);
                        } catch (Exception fe) {
                            fe.printStackTrace();
                        }
                    }

                    try {
                        if (maidDataList.get(position).getExperience() == null || maidDataList.get(position).getExperience().isEmpty()) {
                            viewHolder.ex_lay.setVisibility(View.GONE);
                        } else {
                            viewHolder.ex_lay.setVisibility(View.VISIBLE);
                            viewHolder.tvExperience.setText(String.format(Locale.ENGLISH, "%s", maidDataList.get(position).getExperience()));
                        }
                    } catch (Exception e) {
                        viewHolder.ex_lay.setVisibility(View.GONE);
                        e.printStackTrace();
                        try {//----------- IF FIREBASE RETURN EXCEPTION
                            FirebaseCrashlytics.getInstance().recordException(e);
                        } catch (Exception fe) {
                            fe.printStackTrace();
                        }
                    }

                    try {
                        Double rating = 0.0;
                        int count = 0;
                        MaidData data = maidDataList.get(position);
                        if (data.getAvgIroning() != 0.0) {
                            count++;
                        }
                        if (data.getAvgCooking() != 0.0) {
                            count++;
                        }
                        if (data.getAvgCleaning() != 0.0) {
                            count++;
                        }
                        if (data.getAvgChildCare() != 0.0) {
                            count++;
                        }
                        rating = (data.getAvgCooking()
                                + data.getAvgIroning() + data.getAvgCleaning()
                                + data.getAvgChildCare()) / Double.parseDouble(String.valueOf(count));
                        if (rating.isNaN()) {
                            rating = 0.0;
                            viewHolder.ratingBar.setVisibility(View.GONE);
                        } else {
                            viewHolder.ratingBar.setVisibility(View.VISIBLE);
                        }
                        viewHolder.ratingBar.setRating(Float.valueOf(String.format(Locale.US, "%.1f", rating)));
                    } catch (Exception e) {
                        e.printStackTrace();
                        try {//----------- IF FIREBASE RETURN EXCEPTION
                            FirebaseCrashlytics.getInstance().recordException(e);
                        } catch (Exception fe) {
                            fe.printStackTrace();
                        }
                    }

                    try {
                        Double per = (maidDataList.get(position).getActualPrice() / 100.0) * Float.parseFloat(maidDataList.get(position).getNew_vat() == null ? maidDataList.get(position).getVat() : maidDataList.get(position).getNew_vat()  /*getVat()*/);
                        Double totalper = maidDataList.get(position).getActualPrice() + per;
                        Log.e("vat persenatge value", "" + per);
                        if (maidDataList.get(position).getCurrency().equalsIgnoreCase("BHD") && maidDataList.get(position).getNationality() != null) {

                            try {//----------------------Exception handle tv Nationality-0-----
                                if (!((maidDataList).get(position).getNationality().isEmpty())) {
                                    viewHolder.tvNationality.setVisibility(View.VISIBLE);
                                    viewHolder.tvNationality.setText(String.format("%s %s", activity.getString(R.string.nationality_label), maidDataList.get(position).getNationality()));
                                } else {
                                    viewHolder.tvNationality.setVisibility(View.GONE);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                try {//----------- IF FIREBASE RETURN EXCEPTION
                                    FirebaseCrashlytics.getInstance().recordException(e);
                                } catch (Exception fe) {
                                    fe.printStackTrace();
                                }
                                viewHolder.tvNationality.setVisibility(View.GONE);
                            }


                            try {
                                viewHolder.tvCharges.setText(String.format(Locale.ENGLISH, "%s %.03f", maidDataList.get(position).getCurrency(), maidDataList.get(position).getActualPrice()));
                            } catch (Exception e) {
                                e.printStackTrace();
                                try {//----------- IF FIREBASE RETURN EXCEPTION
                                    FirebaseCrashlytics.getInstance().recordException(e);
                                } catch (Exception fe) {
                                    fe.printStackTrace();
                                }
                            }
                            try {
                                String language = "";
                                for (int i = 0; i < maidDataList.get(position).getLanguages().size(); i++) {
                                    language = maidDataList.get(position).getLanguages().get(i).getLanguageName() + ", " + language;
                                }
                                if (maidDataList.get(position).getLanguages().size() > 1) {
                                    viewHolder.tvReligion.setText(String.format("%s %s", activity.getString(R.string.languages_label), language.substring(0, language.length() - 2)));
                                } else {
                                    if (maidDataList.get(position).getLanguages().size() != 0 && maidDataList.get(position).getLanguages() != null) {
                                        viewHolder.tvReligion.setText(String.format("%s %s", activity.getString(R.string.languages_label), maidDataList.get(position).getLanguages().get(0).getLanguageName()));
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                viewHolder.tvReligion.setVisibility(View.GONE);
                                try {//----------- IF FIREBASE RETURN EXCEPTION
                                    FirebaseCrashlytics.getInstance().recordException(e);
                                } catch (Exception fe) {
                                    fe.printStackTrace();
                                }
                            }
                        } else {
                            viewHolder.tvNationality.setVisibility(View.GONE);
                            viewHolder.tvReligion.setVisibility(View.GONE);
                            try {
                                viewHolder.tvCharges.setText(String.format(Locale.ENGLISH, "%s %.02f", maidDataList.get(position).getCurrency(), maidDataList.get(position).getActualPrice()));
                            } catch (Exception e) {
                                e.printStackTrace();
                                try {//----------- IF FIREBASE RETURN EXCEPTION
                                    FirebaseCrashlytics.getInstance().recordException(e);
                                } catch (Exception fe) {
                                    fe.printStackTrace();
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        try {//----------- IF FIREBASE RETURN EXCEPTION
                            FirebaseCrashlytics.getInstance().recordException(e);
                        } catch (Exception fe) {
                            fe.printStackTrace();
                        }
                    }


                }

            } catch (Exception e) {
                e.printStackTrace();
                try {//----------- IF FIREBASE RETURN EXCEPTION
                    FirebaseCrashlytics.getInstance().recordException(e);
                } catch (Exception fe) {
                    fe.printStackTrace();
                }
            }
        } else {
            HeaderViewHolder viewHolder = (HeaderViewHolder) holder;
            if (position == 0 && !listType) {
                if (isSearch) {
                    viewHolder.tvHeader.setText(activity.getString(R.string.results));
                } else {
                    // viewHolder.tvHeader.setText(context.getString(R.string.suggested_maid));
                    if (searchMaidData1.getData().getSuggestedMaids().size() != 0) {
                        viewHolder.tvHeader.setText(activity.getString(R.string.suggested_maid));
                    } else {
                        viewHolder.tvHeader.setText(activity.getString(R.string.other_maid));
                    }
                }
            } else {
                viewHolder.tvHeader.setText(activity.getString(R.string.other_maid));
            }

           /* if(searchMaidData1.getData().getRequestedMaids().size()!=0){
                viewHolder.tvHeader.setText(context.getString(R.string.other_maid));
            }*/

        }
    }

    @Override
    public int getItemCount() {
        return maidDataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 || (secondHeader != null && position == secondHeader + 1)) {
            return TYPE_HEADER;
        } else {
            return TYPE_ITEM;
        }
    }

    public void setSecondHeader(Integer count) {
        secondHeader = count;
    }

    public void setType(Boolean otherList) {
        listType = otherList;
    }

    public void isSearchResult(Boolean isSearch) {
        this.isSearch = isSearch;
    }

    public void setModele(PojoSearchMaid searchMaidData) {
        searchMaidData1 = searchMaidData;
    }

    public interface MaidSearchCallback {
        void onMaidTimeSlotsClicked(MaidData maidData);
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        private SelectableRoundedImageView ivMaid;
        private TextView tvMaidName, tvExperience, tvCharges, tvNationality, tvReligion;
        private RatingBar ratingBar;
        private ImageView ivClock;
        private LinearLayout ex_lay;

        MyViewHolder(View itemView) {
            super(itemView);
            ivMaid = itemView.findViewById(R.id.ivMaid);
            tvMaidName = itemView.findViewById(R.id.tvMaidName);
            tvExperience = itemView.findViewById(R.id.tvExperience);
            ex_lay = itemView.findViewById(R.id.ex_lay);
            tvCharges = itemView.findViewById(R.id.tvCharges);
            tvNationality = itemView.findViewById(R.id.tvDistance);
            ivClock = itemView.findViewById(R.id.ivClock);
            ratingBar = itemView.findViewById(R.id.rbRating);
            tvReligion = itemView.findViewById(R.id.tvReligion);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ivMaid.setTransitionName(getAdapterPosition() + "");
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (!listType)
                            startFragment.startIntent(maidDataList.get(getAdapterPosition()), ivMaid, getAdapterPosition());
                        else {
                            final IOSAlertDialog dialog = new DialogPopup().alertPopup(activity, activity.getResources().getString(R.string.dialog_alert),
                                    activity.getResources().getString(R.string.alertHeading), "others");

                            dialog.show(((AppCompatActivity) activity).getSupportFragmentManager(), "ios_dialog");

// Set the custom view's click listener directly
                            View okButton = dialog.getView().findViewById(R.id.positiveButton);
                            okButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                    try {
                                        callback.onMaidTimeSlotsClicked(maidDataList.get(getAdapterPosition()));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        try {
                                            FirebaseCrashlytics.getInstance().recordException(e);
                                        } catch (Exception fe) {
                                            fe.printStackTrace();
                                        }
                                    }
                                }
                            });
                        /*    dialog.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                    try {
                                        callback.onMaidTimeSlotsClicked(maidDataList.get(getAdapterPosition()));
                                    }catch (Exception e){
                                        e.printStackTrace();
                                        try {//----------- IF FIREBASE RETURN EXCEPTION
                                            FirebaseCrashlytics.getInstance().recordException(e);
                                        } catch (Exception fe) {
                                            fe.printStackTrace();
                                        }
                                    }
                                }
                            });*/
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        try {//----------- IF FIREBASE RETURN EXCEPTION
                            FirebaseCrashlytics.getInstance().recordException(e);
                        } catch (Exception fe) {
                            fe.printStackTrace();
                        }
                    }


                }
            });

            ivClock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        callback.onMaidTimeSlotsClicked(maidDataList.get(getAdapterPosition()));
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

    private class HeaderViewHolder extends RecyclerView.ViewHolder {
        private TextView tvHeader;

        private HeaderViewHolder(View itemView) {
            super(itemView);
            tvHeader = itemView.findViewById(R.id.tvHeader);
        }
    }
}
