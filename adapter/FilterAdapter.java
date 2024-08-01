package com.maktoday.adapter;

import android.app.Activity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.maktoday.R;
import com.maktoday.interfaces.ApplyFilter;
import com.maktoday.model.PojoFilterLanguage;

import java.util.ArrayList;

/**
 * Created by cbl81 on 21/11/17.
 */

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.ViewHolder> implements Filterable {

    private Activity context;
    private ArrayList<PojoFilterLanguage.Data> filterList = new ArrayList<>();
    private String filterType = "";
    private final static String NATIONALITY = "Nationality";
    private final static String LANGUAGE = "Language";
    private final static String GENDER = "Gender";
    private final static String RELIGION = "Religion";
    private final static String AGENCY = "agency";
    private final static String MATERIAL = "Material";
    private ApplyFilter applyFilter;
    private ArrayList<PojoFilterLanguage.Data> orig = null;

    public FilterAdapter(Activity context, ArrayList<PojoFilterLanguage.Data> filterList, ApplyFilter applyFilter) {
        this.context = context;
        this.filterList = filterList;
        this.applyFilter = applyFilter;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_filter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            if (filterList.get(position).languageName != null) {
                try {
                    holder.tvName.setText(filterList.get(position).languageName.substring(0, 1).toUpperCase() + filterList.get(position).languageName.substring(1));
                }catch (Exception e){
                    e.printStackTrace();
                    try {//----------- IF FIREBASE RETURN EXCEPTION
                        FirebaseCrashlytics.getInstance().recordException(e);
                    } catch (Exception fe) {
                        fe.printStackTrace();
                    }
                }
                try {
                    holder.cbSelected.setChecked(filterList.get(position).isSelected);
                }catch (Exception e){
                  e.printStackTrace();
                    try {//----------- IF FIREBASE RETURN EXCEPTION
                        FirebaseCrashlytics.getInstance().recordException(e);
                    } catch (Exception fe) {
                        fe.printStackTrace();
                    }
                }
            } else {
                holder.tvName.setText("");
                try {
                    holder.cbSelected.setChecked(filterList.get(position).isSelected);
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
        return filterList.size();
    }

    public void setFilterType(String type) {
        filterType = type;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private CheckBox cbSelected;
        private TextView tvName;

        public ViewHolder(View itemView) {
            super(itemView);
            cbSelected = itemView.findViewById(R.id.cbSelected);
            tvName = itemView.findViewById(R.id.tvName);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            boolean status = false;
            int pos;
            switch (filterType) {
                case NATIONALITY:
                    try {
                        status = !filterList.get(getAdapterPosition()).isSelected;
                        pos = getAdapterPosition();
                        filterList.get(pos).isSelected = status;
                        notifyDataSetChanged();
                        applyFilter.updateNationalityList(pos, status);
                    }catch (Exception e){
                        e.printStackTrace();
                        try {//----------- IF FIREBASE RETURN EXCEPTION
                            FirebaseCrashlytics.getInstance().recordException(e);
                        } catch (Exception fe) {
                            fe.printStackTrace();
                        }
                    }
                    break;
                case LANGUAGE:
                    try {
                        status = !filterList.get(getAdapterPosition()).isSelected;
                        pos = getAdapterPosition();
                        filterList.get(pos).isSelected = !filterList.get(pos).isSelected;
                        notifyDataSetChanged();
                        applyFilter.updateLanguageList(pos, status);
                    }catch (Exception e){
                        e.printStackTrace();
                        try {//----------- IF FIREBASE RETURN EXCEPTION
                            FirebaseCrashlytics.getInstance().recordException(e);
                        } catch (Exception fe) {
                            fe.printStackTrace();
                        }
                    }
                    break;
                case GENDER:
                    try {
                        pos = getAdapterPosition();
                        status = !filterList.get(pos).isSelected;
                        filterList.get(pos).isSelected = !filterList.get(pos).isSelected;
                        notifyDataSetChanged();
                        applyFilter.updateGenderList(pos, status);
                    }catch ( Exception e){
                        e.printStackTrace();
                        try {//----------- IF FIREBASE RETURN EXCEPTION
                            FirebaseCrashlytics.getInstance().recordException(e);
                        } catch (Exception fe) {
                            fe.printStackTrace();
                        }
                    }
                    break;
                case MATERIAL:
                    try {
                        pos = getAdapterPosition();
                        status = !filterList.get(pos).isSelected;
                        filterList.get(pos).isSelected = !filterList.get(pos).isSelected;
                        notifyDataSetChanged();
                        applyFilter.updateMaterialList(pos, status);
                    }
                    catch (Exception e){
                        try {//----------- IF FIREBASE RETURN EXCEPTION
                            FirebaseCrashlytics.getInstance().recordException(e);
                        } catch (Exception fe) {
                            fe.printStackTrace();
                        }

                    }
                    break;
                case RELIGION:
                    try {
                        status = !filterList.get(getAdapterPosition()).isSelected;
                        pos = getAdapterPosition();
                        filterList.get(pos).isSelected = !filterList.get(pos).isSelected;
                        notifyDataSetChanged();
                        applyFilter.updateReligionList(pos, status);
                    }catch (Exception e){
                        e.printStackTrace();
                        try {//----------- IF FIREBASE RETURN EXCEPTION
                            FirebaseCrashlytics.getInstance().recordException(e);
                        } catch (Exception fe) {
                            fe.printStackTrace();
                        }
                    }
                    break;
                case AGENCY:
                    try {
                        status = !filterList.get(getAdapterPosition()).isSelected;
                        pos = getAdapterPosition();
                        filterList.get(pos).isSelected = !filterList.get(pos).isSelected;
                        notifyDataSetChanged();
                        applyFilter.updateAgencyList(pos, status);
                    }catch (Exception e){
                        e.printStackTrace();
                        try {//----------- IF FIREBASE RETURN EXCEPTION
                            FirebaseCrashlytics.getInstance().recordException(e);
                        } catch (Exception fe) {
                            fe.printStackTrace();
                        }
                    }
                    break;
            }
        }
    }

    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                final ArrayList<PojoFilterLanguage.Data> results = new ArrayList<PojoFilterLanguage.Data>();
                if (orig == null)
                    orig = filterList;
                if (constraint != null) {
                    if (orig != null && orig.size() > 0) {
                        for (final PojoFilterLanguage.Data g : orig) {
                            if (g.languageName
                                    .contains(constraint.toString()))
                                results.add(g);
                        }
                    }
                    oReturn.values = results;
                }
                return oReturn;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {
                filterList = (ArrayList<PojoFilterLanguage.Data>) results.values;
                filterType = NATIONALITY;
                notifyDataSetChanged();
            }
        };
    }


}
