package com.maktoday.views.agency;


import android.app.Activity;
import android.content.Intent;

import androidx.databinding.DataBindingUtil;

import android.location.Location;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.SearchView;

import com.maktoday.utils.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;

import com.maktoday.R;
import com.maktoday.adapter.AgencyAdapter;
import com.maktoday.databinding.ActivityAgencyBinding;
import com.maktoday.interfaces.AgencySelection;
import com.maktoday.model.PojoAgencyList;
import com.maktoday.utils.BaseActivity;
import com.maktoday.utils.Constants;
import com.maktoday.utils.DialogPopup;
import com.maktoday.utils.EndlessRecyclerOnScrollListener;
import com.maktoday.utils.GeneralFunction;
import com.maktoday.utils.Prefs;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by cbl81 on 28/10/17.
 */

public class AgencyActivity extends BaseActivity implements AgencySelection, AgencyContract.View,
        SearchView.OnQueryTextListener, View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private static final String TAG = "AgencyActivity";
    private ActivityAgencyBinding binding;
    private AgencyContract.Presenter presenter;
    private int pageNo = 1;
    private final int LIMIT = 10;
    private AgencyAdapter agencyAdapter;
    private ArrayList<PojoAgencyList.Data> agencyList = new ArrayList<>();
    private ArrayList<String> selectedAgencyList = new ArrayList<>();
    private String searchData = "";
    private Boolean isSearch = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_agency);
        Log.d(TAG, "onCreate: StartActivity");
        init();
        setData();
        setListeners();
    }

    private void init() {
        setSupportActionBar(binding.toolbar.toolbar);
        presenter = new AgencyPresenter();
        presenter.attachView(this);

        getDataList();
        if (getIntent().hasExtra("selectedAgency")) {
            selectedAgencyList = getIntent().getStringArrayListExtra("selectedAgency");
        }
    }

    private void setData() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_white);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        binding.toolbar.title.setText(R.string.agency_list);
        agencyAdapter = new AgencyAdapter(this, agencyList);
        binding.rvAgency.setAdapter(agencyAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.rvAgency.setLayoutManager(layoutManager);

        EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener = new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                pageNo = currentPage;
                getDataList();
            }
        };

        endlessRecyclerOnScrollListener.setVisibleThreshold(LIMIT);
        binding.rvAgency.addOnScrollListener(endlessRecyclerOnScrollListener);
        binding.cbSelectAll.setChecked(true);
        binding.searchView.setIconifiedByDefault(false);

        binding.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageNo = 1;
                binding.swipeRefresh.setRefreshing(true);
                getDataList();
            }
        });

    }

    private void setListeners() {
        binding.tvDone.setOnClickListener(this);
        binding.tvCancel.setOnClickListener(this);
        binding.cbSelectAll.setOnCheckedChangeListener(this);
        binding.searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_agency_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                binding.cbSelectAll.setOnCheckedChangeListener(null);
                binding.cbSelectAll.setChecked(true);
                binding.cbSelectAll.setOnCheckedChangeListener(this);
                for (int i = 0; i < agencyList.size(); i++) {
                    agencyList.get(i).setSelected(true);
                }
                agencyAdapter.notifyDataSetChanged();

                return true;
            case R.id.action_search:
                binding.rlsearchBar.setVisibility(View.VISIBLE);
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (agencyList.size() == 0 && searchData.isEmpty()) {
            menu.findItem(R.id.action_done).setVisible(false);
            menu.findItem(R.id.action_search).setVisible(false);
        } else {
            menu.findItem(R.id.action_done).setVisible(true);
            menu.findItem(R.id.action_search).setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        pageNo = 1;
        searchData = query;
        getDataList();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        pageNo = 1;
        searchData = newText;
        getDataList();
        return true;
    }


    public void getDataList() {
        if (GeneralFunction.isNetworkConnected(this, binding.parent)) {
            apiAgencyList();
        } else {
            binding.viewFlipper.setDisplayedChild(1);
            binding.noDataView.setData("Cannot connect");
            binding.noDataView.setImage(ContextCompat.getDrawable(this, R.drawable.no_internet));
            binding.swipeRefresh.setRefreshing(false);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvDone:
                if (agencyAdapter.selectedAgencyList.size() == 0) {
                    GeneralFunction.showSnackBar(AgencyActivity.this, binding.parent.getChildAt(1), getString(R.string.validation_select_agency));
                    return;
                }
                Intent intent = new Intent();
                if (agencyAdapter.selectedAgencyList.size() != 0) {
                    if (agencyAdapter.selectedAgencyList.size() == agencyList.size()) {
                        intent.putExtra("All", true);
                    } else {
                        intent.putExtra("All", false);
                        intent.putStringArrayListExtra("selectedList", agencyAdapter.selectedAgencyList);
                    }
                }
                setResult(Activity.RESULT_OK, intent);
                finish();
                break;
            case R.id.tvCancel:

                pageNo = 1;
                binding.rlsearchBar.setVisibility(View.GONE);
                if (!searchData.trim().isEmpty()) {
                    searchData = "";
                    getDataList();
                }
                break;
        }
    }

    @Override
    public void setLoading(boolean isLoading) {
        if (isLoading)
            binding.swipeRefresh.setRefreshing(isLoading);
        else
            binding.swipeRefresh.setRefreshing(isLoading);
    }

    @Override
    public void sessionExpired() {
        GeneralFunction.isUserBlocked(AgencyActivity.this);
    }

    @Override
    public void signupSuccess(PojoAgencyList data) {
        if (pageNo == 1) {
            agencyList.clear();
        }
        agencyList.addAll(data.getData());
        agencyAdapter.notifyDataSetChanged();
        if (agencyAdapter.getItemCount() == 0) {
            binding.viewFlipper.setDisplayedChild(1);
            binding.noDataView.setData(getString(R.string.no_agency_found));
            binding.noDataView.setImage(ContextCompat.getDrawable(this, R.drawable.ic_no_data));
        } else {
            binding.viewFlipper.setDisplayedChild(2);
        }

        if (agencyAdapter.getItemCount() != 0 && selectedAgencyList.size() != 0) {
            for (int i = 0; i < agencyList.size(); i++) {
                agencyList.get(i).setSelected(false
                );
            }
            for (int i = 0; i < agencyList.size(); i++) {
                for (String data2 : selectedAgencyList) {
                    if (agencyList.get(i).get_id().equals(data2)) {
                        agencyList.get(i).setSelected(true);
                    }
                }
            }
        }
        isSearch = false;
        binding.swipeRefresh.setRefreshing(false);
        invalidateOptionsMenu();
    }

    @Override
    public void signupError(String errorMessage) {
        new DialogPopup().alertPopup(AgencyActivity.this, getResources().getString(R.string.dialog_alert), errorMessage, "").show(getSupportFragmentManager(),"IOS_Dialog");
        binding.swipeRefresh.setRefreshing(false);
    }

    @Override
    public void signupFailure(String failureMessage) {
      Log.e(TAG, "signupFailure: "+failureMessage);
        new DialogPopup().alertPopup(AgencyActivity.this, getResources().getString(R.string.dialog_alert), getString(R.string.check_connection), "").show(getSupportFragmentManager(),"IOS_Dialog");
        binding.swipeRefresh.setRefreshing(false);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    public void apiAgencyList() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("uniquieAppKey", Constants.UNIQUE_APP_KEY);
        hashMap.put("pageNo", String.valueOf(pageNo));
        hashMap.put("limit", String.valueOf(LIMIT));

        if (searchData != null && !searchData.isEmpty()) {
            hashMap.put("searchAgencyName", searchData);
            isSearch = true;
        }
        Location location = Prefs.with(this).getObject(Constants.LOCATION, Location.class);
        hashMap.put("long", String.valueOf(location.getLongitude()));
        hashMap.put("lat", String.valueOf(location.getLatitude()));
        hashMap.put("country", Prefs.with(this).getString(Constants.COUNTRY_NAME, ""));
        presenter.apiAgencyList(hashMap, isSearch);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        for (int i = 0; i < agencyList.size(); i++) {
            agencyList.get(i).setSelected(b);
        }
        agencyAdapter.notifyDataSetChanged();
    }

    @Override
    public void setSelectAll(boolean b) {
        binding.cbSelectAll.setOnCheckedChangeListener(null);
        binding.cbSelectAll.setChecked(b);
        binding.cbSelectAll.setOnCheckedChangeListener(this);
    }
}
