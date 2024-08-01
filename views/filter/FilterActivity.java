package com.maktoday.views.filter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.maktoday.R;
import com.maktoday.adapter.FilterAdapter;
import com.maktoday.databinding.LayoutFilterBinding;
import com.maktoday.interfaces.ApplyFilter;
import com.maktoday.model.PojoFilterLanguage;
import com.maktoday.utils.BaseActivity;
import com.maktoday.utils.Constants;
import com.maktoday.utils.DialogPopup;
import com.maktoday.utils.GeneralFunction;
import com.maktoday.utils.Log;
import com.maktoday.utils.Prefs;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.maktoday.utils.Constants.COUNTRY_NAME;
import static com.maktoday.utils.Constants.USER_COUNTRY;

/**
 * Created by cbl81 on 21/11/17.
 */

public class FilterActivity extends BaseActivity implements FilterContract.View,
        View.OnClickListener, ApplyFilter, SearchView.OnQueryTextListener, android.widget.SearchView.OnQueryTextListener {

    private static final String TAG = "FilterActivity";
    private final static String NATIONALITY = "Nationality";
    private final static String LANGUAGE = "Language";
    private final static String GENDER = "Gender";
    private final static String MATERIAL = "Material";
    private final static String RELIGION = "Religion";
    private final static String AGENCY = "agency";
    public List<PojoFilterLanguage.Data> nationalityList = new ArrayList<>();
    private boolean filterStatus = false;
    private FilterContract.Presenter presenter;
    private LayoutFilterBinding binding;
    private FilterAdapter filterAdapter;
    private ArrayList<PojoFilterLanguage.Data> filterList = new ArrayList<>();
    private List<PojoFilterLanguage.Data> religionList = new ArrayList<>();
    private List<PojoFilterLanguage.Data> languageDataList = new ArrayList<>();
    private List<PojoFilterLanguage.Data> genderList = new ArrayList<>();
    private List<PojoFilterLanguage.Data> materialList = new ArrayList<>();
    private List<PojoFilterLanguage.Data> agencyList = new ArrayList<>();
    private List<PojoFilterLanguage.Data> defaultNationalityList, defaultLanguageList, defaultGenderList, defaultMaterialList, defaultReligionList, defaultAgencyList;
    private TextView selectedView;
    private String sortBy = "";
    private boolean isNationalityHit, isLanguageHit, isGenderHit, isMaterialHit, isReligion, isAgency;
    private int lastSelectedPosition = 0;
    String currency="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(FilterActivity.this, R.layout.layout_filter);
        //currency=getIntent().getStringExtra("currency");
        Log.d(TAG, "onCreate: StartActivity");
        init();
        setData();
        setListeners();
        selectLastSelectedItem();
       // Prefs.get().getString(USER_COUNTRY, "").equalsIgnoreCase("GB")
        if (Prefs.get().getString(USER_COUNTRY, "").equalsIgnoreCase("BH")) {
            binding.tvNationality.setVisibility(View.VISIBLE);
            binding.tvReligion.setVisibility(View.VISIBLE);
        } else {
            binding.tvNationality.setVisibility(View.GONE);
            binding.tvReligion.setVisibility(View.GONE);

        }
    }

    private void init() {
        if (Prefs.get().getString(USER_COUNTRY, "").equalsIgnoreCase("BH")) {
            lastSelectedPosition = getIntent().getIntExtra(Constants.LAST_SELECTED_POSITION, 0);
        } else {
            // lastSelectedPosition = 1;
            lastSelectedPosition = getIntent().getIntExtra(Constants.LAST_SELECTED_POSITION, 0);
            if(lastSelectedPosition==0){
                lastSelectedPosition = 1;
            }else {
                lastSelectedPosition = getIntent().getIntExtra(Constants.LAST_SELECTED_POSITION, 0);
            }
        }

        presenter = new FilterPresenter();
        presenter.attachView(this);
        filterAdapter = new FilterAdapter(FilterActivity.this, filterList, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(FilterActivity.this);
        binding.rvFilter.setLayoutManager(layoutManager);

        binding.rvFilter.setAdapter(filterAdapter);

        setGenderListData();

        setMaterialListData();
        setupSearchView();
    }

    private void setGenderListData() {
        PojoFilterLanguage.Data tempMale = new PojoFilterLanguage.Data();
        tempMale.languageName = "Male";
        genderList.add(tempMale);

        PojoFilterLanguage.Data tempFemale = new PojoFilterLanguage.Data();
        tempFemale.languageName = "Female";
        genderList.add(tempFemale);
        isGenderHit = true;
    }

    private void setMaterialListData() {
        PojoFilterLanguage.Data tempMale = new PojoFilterLanguage.Data();
        tempMale.languageName = "Cleaner with material";
        materialList.add(tempMale);

        PojoFilterLanguage.Data tempFemale = new PojoFilterLanguage.Data();
        tempFemale.languageName = "Cleaner without material";
        materialList.add(tempFemale);
        isMaterialHit = true;
    }

    private void setData() {


        if (Prefs.get().getString(USER_COUNTRY, "").equalsIgnoreCase("BH")) {
            binding.tvNationality.setTextColor(ContextCompat.getColor(FilterActivity.this, R.color.colorBlackBF));
            selectedView = binding.tvNationality;
        } else {
            binding.tvLanguage.setTextColor(ContextCompat.getColor(FilterActivity.this, R.color.colorBlackBF));
            selectedView = binding.tvLanguage;
        }

        //by default selected filter values
        if (getIntent().hasExtra("nationalityList") && getIntent().getParcelableArrayListExtra("nationalityList") != null) {
            defaultNationalityList = getIntent().getParcelableArrayListExtra("nationalityList");
            checkAndUpdateFilterStatus(defaultNationalityList);
        }

        if (getIntent().hasExtra("languageList") && getIntent().getParcelableArrayListExtra("languageList") != null) {
            defaultLanguageList = getIntent().getParcelableArrayListExtra("languageList");
            checkAndUpdateFilterStatus(defaultLanguageList);
        }
        if (getIntent().hasExtra("genderList") && getIntent().getParcelableArrayListExtra("genderList") != null) {
            defaultGenderList = getIntent().getParcelableArrayListExtra("genderList");
            checkAndUpdateFilterStatus(defaultGenderList);
        }
        if (getIntent().hasExtra("materialList") && getIntent().getParcelableArrayListExtra("materialList") != null) {
            defaultMaterialList = getIntent().getParcelableArrayListExtra("materialList");
            checkAndUpdateFilterStatus(defaultMaterialList);
        }

        if (getIntent().hasExtra("religionList") && getIntent().getParcelableArrayListExtra("religionList") != null) {
            defaultReligionList = getIntent().getParcelableArrayListExtra("religionList");
            checkAndUpdateFilterStatus(defaultReligionList);
        }

        if (getIntent().hasExtra("agencylist") && getIntent().getParcelableArrayListExtra("agencylist") != null) {
            defaultAgencyList = getIntent().getParcelableArrayListExtra("agencylist");
            checkAndUpdateFilterStatus(defaultAgencyList);
        }

        if (filterStatus) {
            binding.tvReset.setVisibility(View.VISIBLE);
        } else {
            binding.tvReset.setVisibility(View.GONE);
        }
    }

    private void checkAndUpdateFilterStatus(List<PojoFilterLanguage.Data> filters) {
        // If filter status is already true then skip further checking for filter selected
        if (filterStatus)
            return;

        // If any of the filter is selected, then set the filter status to true so that reset button will be visible
        for (PojoFilterLanguage.Data data : filters) {
            if (data.isSelected) {
                filterStatus = true;
                break;
            }
        }
    }

    private void selectLastSelectedItem() {
        if (getIntent().hasExtra(Constants.LAST_SELECTED_POSITION)) {
            switch (lastSelectedPosition) {
                case 0:
                    binding.tvNationality.performClick();
                    break;
                case 1:
                    binding.tvLanguage.performClick();
                    break;
                case 2:
                    binding.tvReligion.performClick();
                    break;
                case 3:
                    binding.tvGender.performClick();
                    break;
                case 4:
                    binding.tvAgency.performClick();
                    break;
                case 5:
                    binding.tvMaterial.performClick();
                    break;
            }
        }
    }

    private void setListeners() {
        binding.tvNationality.setOnClickListener(this);
        binding.tvLanguage.setOnClickListener(this);
        binding.tvGender.setOnClickListener(this);
        binding.tvMaterial.setOnClickListener(this);
        binding.tvAgency.setOnClickListener(this);
        binding.tvReligion.setOnClickListener(this);
        binding.llCancel.setOnClickListener(this);
        binding.llApply.setOnClickListener(this);
        binding.tvReset.setOnClickListener(this);
    }

    private void setupSearchView() {
        binding.searchView1.setIconifiedByDefault(false);
        binding.searchView1.setOnQueryTextListener(this);
        binding.searchView1.setSubmitButtonEnabled(true);
        binding.searchView1.setQueryHint("Search Here");
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        filterAdapter.getFilter().filter(newText);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }


    private void apiNationalityData() {
        HashMap<String, String> map = new HashMap<>();
        map.put("uniquieAppKey", Constants.UNIQUE_APP_KEY);
        presenter.apiNationality(map);
        isNationalityHit = true;
    }

    private void apiLanguageData() {
        HashMap<String, String> map = new HashMap<>();
        map.put("uniquieAppKey", Constants.UNIQUE_APP_KEY);
        presenter.apiLanguage(map);
        isLanguageHit = true;

    }

    private void apiAgencyData() {
        HashMap<String, String> map = new HashMap<>();
       // map.put("uniquieAppKey", Constants.UNIQUE_APP_KEY);
        map.put("countryName",Prefs.get().getString(COUNTRY_NAME,""));
        Log.e("apiAgency param",new Gson().toJson(map));
        presenter.apiAgency(map);
        isAgency = true;

    }

    private void apiReligion() {
        HashMap<String, String> map = new HashMap<>();
        map.put("uniquieAppKey", Constants.UNIQUE_APP_KEY);
        presenter.apiReligion(map);
        isReligion = true;
    }

    @Override
    public void setLoading(boolean isLoading) {
        if (isLoading)
            GeneralFunction.showProgress(FilterActivity.this);
        else
            GeneralFunction.dismissProgress();
    }

    @Override
    public void sessionExpired() {
        GeneralFunction.isUserBlocked(FilterActivity.this);
    }

    @Override
    public void nationalitySuccess(List<PojoFilterLanguage.Data> data) {
        nationalityList = data;
        if (isNationalityHit) {
            for (PojoFilterLanguage.Data temp1 : nationalityList) {
                for (PojoFilterLanguage.Data temp2 : defaultNationalityList) {
                    try {
                        if (temp1.languageName.equals(temp2.languageName)) {
                            temp1.isSelected = temp2.isSelected;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        filterList.clear();
        filterList.addAll(nationalityList);
        filterAdapter.setFilterType(NATIONALITY);
        filterAdapter.notifyDataSetChanged();
        isNationalityHit = false;
    }

    @Override
    public void agencySuccess(List<PojoFilterLanguage.Data> data) {
        agencyList = data;
        if (isAgency) {
            for (PojoFilterLanguage.Data temp1 : agencyList) {
                if (defaultAgencyList != null && defaultAgencyList.size() > 0) {
                    for (PojoFilterLanguage.Data temp2 : defaultAgencyList) {
                        try {
                            if (temp1.languageName.equals(temp2.languageName)) {
                                temp1.isSelected = temp2.isSelected;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        filterList.clear();
        filterList.addAll(agencyList);
        filterAdapter.setFilterType(AGENCY);
        filterAdapter.notifyDataSetChanged();
        isAgency = false;
    }

    @Override
    public void languageSuccess(List<PojoFilterLanguage.Data> data) {
        languageDataList = data;
        if (isLanguageHit) {
            for (PojoFilterLanguage.Data temp1 : languageDataList) {
                for (PojoFilterLanguage.Data temp2 : defaultLanguageList) {
                    if (temp1.languageName != null && temp2.languageName != null && temp1.languageName.equals(temp2.languageName)) {
                        temp1.isSelected = temp2.isSelected;
                    }
                }
            }
        }
        filterList.clear();
        filterList.addAll(languageDataList);
        filterAdapter.setFilterType(LANGUAGE);
        filterAdapter.notifyDataSetChanged();
        isLanguageHit = false;
    }

    @Override
    public void religionSuccess(List<PojoFilterLanguage.Data> data) {
        religionList = data;
        if (isReligion) {
            for (PojoFilterLanguage.Data temp1 : religionList) {
                for (PojoFilterLanguage.Data temp2 : defaultReligionList) {
                    if (temp1.languageName != null && temp2.languageName != null && temp1.languageName.equals(temp2.languageName)) {
                        temp1.isSelected = temp2.isSelected;
                    }
                }
            }
        }
        filterList.clear();
        filterList.addAll(religionList);
        filterAdapter.setFilterType(RELIGION);
        filterAdapter.notifyDataSetChanged();
        isReligion = false;
    }

    public void genderSuccess() {
        filterList.clear();

        if (isGenderHit) {
            for (PojoFilterLanguage.Data temp1 : genderList) {
                for (PojoFilterLanguage.Data temp2 : defaultGenderList) {
                    if (temp1.languageName.equals(temp2.languageName)) {
                        temp1.isSelected = temp2.isSelected;
                    }
                }
            }
        }
        filterList.addAll(genderList);
        filterAdapter.setFilterType(GENDER);
        filterAdapter.notifyDataSetChanged();
        isGenderHit = false;

    }

    public void materialSuccess() {
        filterList.clear();

        if (isMaterialHit) {
            for (PojoFilterLanguage.Data temp1 : materialList) {
                for (PojoFilterLanguage.Data temp2 : defaultMaterialList) {
                    if (temp1.languageName.equals(temp2.languageName)) {
                        temp1.isSelected = temp2.isSelected;
                    }
                }
            }
        }
        filterList.addAll(materialList);
        filterAdapter.setFilterType(MATERIAL);
        filterAdapter.notifyDataSetChanged();
        isMaterialHit = false;

    }

    @Override
    public void error(String errorMsg) {
        new DialogPopup().alertPopup(FilterActivity.this, getResources().getString(R.string.dialog_alert), errorMsg, "").show(getSupportFragmentManager(), "ios_dialog");;
    }

    @Override
    public void failure(String failureMessage) {
        new DialogPopup().alertPopup(FilterActivity.this, getResources().getString(R.string.dialog_alert), getString(R.string.check_connection), "").show(getSupportFragmentManager(), "ios_dialog");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.tvNationality:
                //pageNo=1;
                selectedView.setTextColor(ContextCompat.getColor(FilterActivity.this, R.color.colorBlack40));
                binding.tvNationality.setTextColor(ContextCompat.getColor(FilterActivity.this, R.color.colorBlackBF));
                selectedView = (TextView) view;

                if (nationalityList.size() == 0) {
                    if (GeneralFunction.isNetworkConnected(FilterActivity.this, binding.parent)) {
                        apiNationalityData();
                    }
                } else {
                    nationalitySuccess(nationalityList);
                }
                break;
            case R.id.tvLanguage:
                // pageNo=1;
                selectedView.setTextColor(ContextCompat.getColor(FilterActivity.this, R.color.colorBlack40));
                binding.tvLanguage.setTextColor(ContextCompat.getColor(FilterActivity.this, R.color.colorBlackBF));
                selectedView = (TextView) view;

                if (languageDataList.size() == 0) {
                    if (GeneralFunction.isNetworkConnected(FilterActivity.this, binding.parent)) {
                        apiLanguageData();
                    }
                } else {
                    languageSuccess(languageDataList);
                }

                break;
            case R.id.tvGender:
                // pageNo=1;
                selectedView.setTextColor(ContextCompat.getColor(FilterActivity.this, R.color.colorBlack40));
                binding.tvGender.setTextColor(ContextCompat.getColor(FilterActivity.this, R.color.colorBlackBF));
                selectedView = (TextView) view;
                genderSuccess();
                break;
            case R.id.tvMaterial:
                // pageNo=1;
                selectedView.setTextColor(ContextCompat.getColor(FilterActivity.this, R.color.colorBlack40));
                binding.tvMaterial.setTextColor(ContextCompat.getColor(FilterActivity.this, R.color.colorBlackBF));
                selectedView = (TextView) view;
                materialSuccess();
                break;

            case R.id.tvReligion:
                selectedView.setTextColor(ContextCompat.getColor(FilterActivity.this, R.color.colorBlack40));
                binding.tvReligion.setTextColor(ContextCompat.getColor(FilterActivity.this, R.color.colorBlackBF));
                selectedView = (TextView) view;
                if (religionList.size() == 0) {
                    if (GeneralFunction.isNetworkConnected(FilterActivity.this, binding.parent)) {
                        apiReligion();
                    }
                } else {
                    religionSuccess(religionList);
                }

                break;

            case R.id.tvAgency:

                selectedView.setTextColor(ContextCompat.getColor(FilterActivity.this, R.color.colorBlack40));
                binding.tvAgency.setTextColor(ContextCompat.getColor(FilterActivity.this, R.color.colorBlackBF));
                selectedView = (TextView) view;
                if (agencyList.size() == 0) {
                    if (GeneralFunction.isNetworkConnected(FilterActivity.this, binding.parent)) {
                        apiAgencyData();
                    }
                } else {
                    religionSuccess(agencyList);
                }

                break;

            case R.id.llCancel:
                setResult(Activity.RESULT_CANCELED, intent);
                finish();
                break;
            case R.id.llApply:
                intent.putParcelableArrayListExtra("nationalityList", (ArrayList<PojoFilterLanguage.Data>) nationalityList);
                intent.putParcelableArrayListExtra("agencyList", (ArrayList<PojoFilterLanguage.Data>) agencyList);
                intent.putParcelableArrayListExtra("languageList", (ArrayList<PojoFilterLanguage.Data>) languageDataList);
                intent.putParcelableArrayListExtra("genderList", (ArrayList<PojoFilterLanguage.Data>) genderList);
                intent.putParcelableArrayListExtra("religionList", (ArrayList<PojoFilterLanguage.Data>) religionList);
                intent.putParcelableArrayListExtra("materialList", (ArrayList<PojoFilterLanguage.Data>) materialList);
                intent.putExtra(Constants.LAST_SELECTED_POSITION, lastSelectedPosition);
                setResult(Activity.RESULT_OK, intent);
                finish();
                break;
            case R.id.tvReset:
                filterStatus = false;
                nationalityList.clear();
                languageDataList.clear();
                genderList.clear();
                religionList.clear();
                materialList.clear();
                defaultGenderList.clear();
                defaultLanguageList.clear();
                defaultNationalityList.clear();
                defaultReligionList.clear();
                defaultMaterialList.clear();
                intent.putParcelableArrayListExtra("nationalityList", (ArrayList<PojoFilterLanguage.Data>) nationalityList);
                intent.putParcelableArrayListExtra("languageList", (ArrayList<PojoFilterLanguage.Data>) languageDataList);
                intent.putParcelableArrayListExtra("genderList", (ArrayList<PojoFilterLanguage.Data>) genderList);
                intent.putParcelableArrayListExtra("religionList", (ArrayList<PojoFilterLanguage.Data>) religionList);
                intent.putParcelableArrayListExtra("materialList", (ArrayList<PojoFilterLanguage.Data>) materialList);
                intent.putExtra(Constants.LAST_SELECTED_POSITION, lastSelectedPosition);
                setResult(Activity.RESULT_OK, intent);
                setGenderListData();
                setMaterialListData();
                //selectLastSelectedItem();
                filterAdapter.notifyDataSetChanged();
                binding.tvReset.setVisibility(View.GONE);
                finish();
                break;

        }
    }


    @Override
    public void updateNationalityList(int position, boolean status) {
        filterStatus = true;
        nationalityList.get(position).isSelected = status;
        binding.tvReset.setVisibility(View.VISIBLE);
        lastSelectedPosition = 0;


    }

    @Override
    public void updateLanguageList(int position, boolean status) {
        filterStatus = true;
        if (languageDataList.size() != 0)
            languageDataList.get(position).isSelected = status;
        binding.tvReset.setVisibility(View.VISIBLE);
        lastSelectedPosition = 1;


        // languageDataList.add(data);
    }

    @Override
    public void updateGenderList(int position, boolean status) {
        filterStatus = true;
        genderList.get(position).isSelected = status;
        binding.tvReset.setVisibility(View.VISIBLE);
        lastSelectedPosition = 3;


        //gender=data;
    }

    @Override
    public void updateMaterialList(int position, boolean status) {
        filterStatus = true;
        materialList.get(position).isSelected = status;
        binding.tvReset.setVisibility(View.VISIBLE);
        lastSelectedPosition = 2;
    }

    @Override
    public void updateReligionList(int position, boolean status) {
        filterStatus = true;
        religionList.get(position).isSelected = status;
        binding.tvReset.setVisibility(View.VISIBLE);
        lastSelectedPosition = 2;

    }

    @Override
    public void updateAgencyList(int position, boolean status) {
        filterStatus = true;
        agencyList.get(position).isSelected = status;
        binding.tvReset.setVisibility(View.VISIBLE);
        lastSelectedPosition = 4;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
