package com.maktoday.views.maidsearch;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.appcompat.widget.SearchView;

import android.transition.Fade;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.joooonho.SelectableRoundedImageView;
import com.maktoday.R;
import com.maktoday.adapter.MaidSearchAdapter;
import com.maktoday.databinding.FragmentSearchMaidBinding;
import com.maktoday.interfaces.StartFragment;
import com.maktoday.model.FullAddress;
import com.maktoday.model.MaidData;
import com.maktoday.model.PojoFilterLanguage;
import com.maktoday.model.PojoMyBooking;
import com.maktoday.model.PojoSearchMaid;
import com.maktoday.model.SearchMaidModel;
import com.maktoday.model.TimeSlot;
import com.maktoday.utils.BaseFragment;
import com.maktoday.utils.Constants;
import com.maktoday.utils.DataVariable;
import com.maktoday.utils.DetailsTransition;
import com.maktoday.utils.DialogPopup;
import com.maktoday.utils.EndlessRecyclerOnScrollListener;
import com.maktoday.utils.GeneralFunction;
import com.maktoday.utils.Log;
import com.maktoday.utils.Prefs;
import com.maktoday.utils.dialog.IOSAlertDialog;
import com.maktoday.views.AllService.ServiceFragment;
import com.maktoday.views.confirmbook.ConfirmBookFragment;
import com.maktoday.views.filter.FilterActivity;
import com.maktoday.views.home.HomeFragment;
import com.maktoday.views.maidprofile.MaidProfileFragment;
import com.maktoday.views.slotavailable.SlotAvailbleActivity;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static com.maktoday.views.home.HomeFragment.booking_type;

/**
 * Created by cbl81 on 31/10/17.
 */

public class MaidFragment extends BaseFragment implements MaidContract.View,
        SearchView.OnQueryTextListener, StartFragment, View.OnClickListener, MaidSearchAdapter.MaidSearchCallback {

    private final static String EXPERIENCE = "EXPERIENCE";
    private final static String RATING = "RATING";
    private final static String DISTANCE = "DISTANCE";
    private final static String PRICE = "PRICE";
    private final static int REQUEST_CODE = 0;
    private static final String TAG = "MaidFragment";
    private FragmentSearchMaidBinding binding;
    private MaidContract.Presenter presenter;
    private MaidSearchAdapter maidSearchAdapter;
    private SearchMaidModel searchMaidModel;
    private String rescheduleStatus;
    private String serviceId;
    private ArrayList<MaidData> maidDataList = new ArrayList<>();
    private int pageNo = 1;
    private final int LIMIT = 2;
    private Integer headerCount;
    private Boolean otherList = false;
    private ArrayList<String> sortBy = new ArrayList<>();
    private String selectedSortBy = "";
    private BottomSheetDialog mBottomSheetDialog;
    private TextView tvExperience, tvRating, tvPrice, selectedTextView,tvReset;
    public static SelectableRoundedImageView ivMaid;
    public static int adapterPosition;
    public static String temp_name = "";
    public static String temp_id = "";
    private EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener;
    public static MaidData maid_temp;
    private List<PojoFilterLanguage.Data> nationalityList = new ArrayList<>();
    private List<PojoFilterLanguage.Data> materialList = new ArrayList<>();
    private List<PojoFilterLanguage.Data> agencylist = new ArrayList<>();
    private List<PojoFilterLanguage.Data> languageDataList = new ArrayList<>();
    private List<PojoFilterLanguage.Data> genderList = new ArrayList<>();
    private List<PojoFilterLanguage.Data> religionList = new ArrayList<>();
    private List<String> selectedGenderList = new ArrayList<>();
    private String searchMaidName = "";
    private boolean isSearch = false;
    private String priceSort = "";
    private boolean checkOtherMaid = false;
    private Context context;
    private PojoSearchMaid searchMaidData1;
    private int lastSelectedPosition = 0;
    private IOSAlertDialog errorPopUpDialog;
    private TimeZone bookingTimeZone;
    public static PojoMyBooking.Datum bookingDataModel;

    public static MaidFragment newInstance(PojoMyBooking.Datum datum, SearchMaidModel searchMaidModel, String rescheduleStatus, String ServiceId) {

        bookingDataModel = datum;
        Bundle args = new Bundle();
        MaidFragment fragment = new MaidFragment();
        args.putParcelable(Constants.SEARCH_MAID_DATA, searchMaidModel);

        args.putString(Constants.reschuleStatus, rescheduleStatus);
        args.putString(Constants.SERVICE_ID, ServiceId);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchMaidBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchMaidModel = getArguments().getParcelable(Constants.SEARCH_MAID_DATA);
        serviceId = getArguments().getString(Constants.SERVICE_ID);
        rescheduleStatus = getArguments().getString(Constants.reschuleStatus);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
       Log.d(TAG, "onViewCreated: StartActivity");
        setHasOptionsMenu(true);
        init();
        setData();
        setListener();
    }

    private void init() {
        //intialize presenter view
        presenter = new MaidPresenter();
        //attach the presenter view
        presenter.attachView(this);
        sortBy.add(EXPERIENCE);
        sortBy.add(PRICE);
        sortBy.add(RATING);
        //for crate home button
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(binding.toolbar);
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
            activity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_white);
        }

        //get data
        searchMaidModel = getActivity().getIntent().getParcelableExtra(Constants.SEARCH_MAID_DATA);

        //intialize Bottom sheet
        mBottomSheetDialog = new BottomSheetDialog(getActivity());
        View sheetView = getActivity().getLayoutInflater().inflate(R.layout.layout_sort, null);
        mBottomSheetDialog.setContentView(sheetView);
        tvExperience = mBottomSheetDialog.findViewById(R.id.tvExperience);
        tvRating = mBottomSheetDialog.findViewById(R.id.tvRating);
        tvPrice = mBottomSheetDialog.findViewById(R.id.tvPrice);
        tvPrice = mBottomSheetDialog.findViewById(R.id.tvPrice);
        tvReset = mBottomSheetDialog.findViewById(R.id.tvReset);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ConfirmBookFragment.Promo_id != null) {
            ConfirmBookFragment.Discounted_price = 0.0;
            ConfirmBookFragment.Promo_id = "";
            ConfirmBookFragment.Promo_discount = "";
            ConfirmBookFragment.Promo_desc = "";
            ConfirmBookFragment.Promo_name = "";
            ConfirmBookFragment.amount_temp = "";
            ConfirmBookFragment.temp_duration = "";
            ConfirmBookFragment.temp_currency = "";
        }
    }

    private void setData() {
        binding.swipeRefresh.setRefreshing(false);
        if (maidDataList.size() == 0) {
            getSearchDataList();
        } else {
            searchMaidSuccess(searchMaidData1);
        }
       // Log.e("services",HomeFragment.servicesID);
        //or to support all versions use
        binding.title.setText(getActivity().getString(R.string.search_maid_title));
        binding.title.setTextSize(24);
        // recycler view adapter
        maidSearchAdapter = new MaidSearchAdapter(getActivity(), maidDataList, headerCount, this, otherList, this,searchMaidData1);
        binding.rvMaid.setAdapter(maidSearchAdapter);
        maidSearchAdapter.notifyDataSetChanged();
        // recycler view Layout Manager
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (maidSearchAdapter.getItemViewType(position)) {
                    case MaidSearchAdapter.TYPE_HEADER:
                        return 2;
                    case MaidSearchAdapter.TYPE_ITEM:
                        return 1;
                    default:
                        return -1;
                }
            }
        });
        binding.rvMaid.setLayoutManager(layoutManager);

        endlessRecyclerOnScrollListener = new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                pageNo = currentPage;
                getSearchDataList();

            }
        };
        endlessRecyclerOnScrollListener.setVisibleThreshold(LIMIT);
        binding.rvMaid.addOnScrollListener(endlessRecyclerOnScrollListener);

        binding.searchView.setIconifiedByDefault(false);
       // binding.swipeRefresh.setVisibility(View.GONE);
        binding.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                binding.swipeRefresh.setRefreshing(true);
                // pageNo = 1;
                getSearchDataList();
            }
        });
    }

    private void apiSearchMaidData(TimeZone bookingTimeZone) {
        FullAddress fullAddress = Prefs.with(getContext()).getObject(Constants.MAP_FULL_ADDRESS, FullAddress.class);
        HashMap<String, String> hashMap = new HashMap<>();
        try {// handle exception searchMaidModel.uniquieAppKey
            hashMap.put("uniquieAppKey", searchMaidModel.uniquieAppKey);
        }catch (Exception e){
            e.printStackTrace();
        }
        try {//  Handle exception !ServiceFragment.servicesID.isEmpty()
            if (!ServiceFragment.servicesID.isEmpty()) {
                hashMap.put("services", ServiceFragment.servicesID);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        if (booking_type == 2) {
            String temp_dates = "";
            String temp_hours = "";
            for (int i = 0; i < HomeFragment.selectedDatesList.size(); i++) {
                //   temp_dates = GeneralFunction.getFormatFromDate(selectedDatesList.get(i), "EEEE, MMMM dd") + ","+temp_dates;
                temp_dates = HomeFragment.selectedDatesList.get(i).getTime() + "," + temp_dates;
                //  hashMap.put("hour", String.valueOf(GeneralFunction.getTimeInTimeZone(new Date(searchMaidModel.startTime), bookingTimeZone).getTime()));
                String mytime = GeneralFunction.getFormatFromDate(HomeFragment.selectedDatesList.get(i), "dd MMMM yy") + " " + searchMaidModel.selectedTime;
                Log.e("mytimeff", mytime);

                SimpleDateFormat dateFormat = new SimpleDateFormat(
                        "dd MMMM yy hh:mm a", Locale.ENGLISH);

                Date myDate = null;
                try {
                    myDate = dateFormat.parse(mytime);
                    temp_hours = String.valueOf(GeneralFunction.getTimeInTimeZone(myDate, bookingTimeZone).getTime()) + "," + temp_hours;
                    Log.e(TAG, "apiSearchMaidData: mydate"+ myDate);
                   Log.e(TAG, "apiSearchMaidData: myTime "+ mytime);
                   Log.e(TAG, "apiSearchMaidData: GernalFunction "+ (GeneralFunction.getTimeInTimeZone(myDate, bookingTimeZone).getTime()));
                   Log.e(TAG, "apiSearchMaidData: TempHours "+ temp_hours);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }

           Log.e(TAG, "apiSearchMaidData: "+ temp_hours);

            hashMap.put("workDate", temp_dates.substring(0, temp_dates.length() - 1));
            hashMap.put("hours", temp_hours.substring(0, temp_hours.length() - 1));
            hashMap.put("endDate", HomeFragment.selectedDatesList.get(HomeFragment.selectedDatesList.size() - 1).getTime() + "");

            String mytime = GeneralFunction.getFormatFromDate(HomeFragment.selectedDatesList.get(HomeFragment.selectedDatesList.size() - 1), "dd MMMM yy") + " " + searchMaidModel.selectedTime;
            Log.e(TAG,"mytimeff"+ mytime);

            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    "dd MMMM yy hh:mm a", Locale.ENGLISH);

            Date myDate = null;
            try {
                myDate = dateFormat.parse(mytime);


            } catch (ParseException e) {
                e.printStackTrace();
            }
            Log.e(TAG,"f dateff"+ myDate.toString());
            //  hashMap.put("hour", String.valueOf(GeneralFunction.getTimeInTimeZone( HomeFragment.selectedDatesList.get(HomeFragment.selectedDatesList.size()-1), bookingTimeZone).getTime()));
            hashMap.put("hour", String.valueOf(GeneralFunction.getTimeInTimeZone(myDate, bookingTimeZone).getTime()));

            hashMap.put("startTime", String.valueOf(GeneralFunction.getTimeInTimeZone(myDate, TimeZone.getDefault()).getTime()));

        }
        else {

            hashMap.put("hour", String.valueOf(GeneralFunction.getTimeInTimeZone(new Date(searchMaidModel.startTime), bookingTimeZone).getTime()));

            Log.d(TAG, "hour" +   String.valueOf(GeneralFunction.getTimeInTimeZone(new Date(searchMaidModel.startTime), bookingTimeZone).getTime()) );
            Log.d(TAG,"new Date(searchMaidModel.startTime" + new Date(searchMaidModel.startTime));
            Log.d(TAG,"searchMaidModel.startTime "+ searchMaidModel.startTime);
            Log.d(TAG, "bookingTimeZone" + bookingTimeZone);

            hashMap.put("hours", String.valueOf(GeneralFunction.getTimeInTimeZone(new Date(searchMaidModel.startTime), bookingTimeZone).getTime()));

            hashMap.put("workDate", String.valueOf(searchMaidModel.workDate));
            hashMap.put("endDate", String.valueOf(searchMaidModel.endDate));
            hashMap.put("startTime", String.valueOf(searchMaidModel.startTime));
        }

        String country = "";
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        if (Geocoder.isPresent()) {
            List<Address> addressList = null;
            try {
                addressList = geocoder.getFromLocation(
                        searchMaidModel.lat, searchMaidModel.lng, 1);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            if (addressList != null && addressList.size() > 0) {
                //   locationName = addressList.get(0).getLocality();
                country = addressList.get(0).getCountryName();
                //   Prefs.with(getActivity()).save(Constants.COUNTRY_NAME, country);
                //    Prefs.with(getActivity()).save(Constants.LOCATION, location);
            }
        }

        hashMap.put("duration", String.valueOf(searchMaidModel.duration));
        hashMap.put("long", String.valueOf(searchMaidModel.lng));
        hashMap.put("lat", String.valueOf(searchMaidModel.lat));
        hashMap.put("country", country);
       // Toast.makeText(requireContext(), "pageNo "+ pageNo, Toast.LENGTH_SHORT).show();
        hashMap.put("pageNo", String.valueOf(pageNo));
        hashMap.put("limit", String.valueOf(LIMIT));

        if (agencylist != null) {
            if (agencylist.size() != 0) {
                ArrayList<String> tempList = new ArrayList<>();
                for (PojoFilterLanguage.Data data : agencylist) {
                    if (data._id != null && data.isSelected) {
                        tempList.add(data._id);
                    }
                }

                Log.e(TAG,"agencylist"+ new Gson().toJson(tempList));

                if (tempList.size() != 0) {
                    hashMap.put("agencyId", new Gson().toJson(tempList));
                }
            }
        }

        hashMap.put("deviceTimeZone", TimeZone.getDefault().getID());
        hashMap.put("timeZone", bookingTimeZone.getID());
        //  hashMap.put("hour", String.valueOf(GeneralFunction.getTimeInTimeZone(new Date(searchMaidModel.startTime), bookingTimeZone).getTime()));

        if (!selectedSortBy.trim().isEmpty()) {
            hashMap.put("sort", selectedSortBy);
        }

        selectedGenderList.clear();
        if (genderList.size() != 0) {
            for (PojoFilterLanguage.Data data : genderList) {
                if (data.isSelected) {
                    selectedGenderList.add(data.languageName.toUpperCase());
                }
            }
        }

        if (selectedGenderList.size() != 0 && selectedGenderList.size() == 2) {
            hashMap.put("gender", "BOTH");
        } else if (selectedGenderList.size() != 0 && selectedGenderList.size() == 1) {
            hashMap.put("gender", selectedGenderList.get(0));
        }

        if (nationalityList.size() != 0) {
            ArrayList<String> tempList = new ArrayList<>();
            for (PojoFilterLanguage.Data data : nationalityList) {
                if (data.languageName != null && data.isSelected) {
                    tempList.add(data.languageName);
                }
            }
            if (tempList.size() != 0) {
                hashMap.put("nationality", new Gson().toJson(tempList));
            }
        }

        if (religionList.size() != 0) {
            ArrayList<String> tempList = new ArrayList<>();
            for (PojoFilterLanguage.Data data : religionList) {
                if (data.languageName != null && data.isSelected) {
                    tempList.add(data.languageName);
                }
            }
            if (tempList.size() != 0) {
                hashMap.put("religion", new Gson().toJson(tempList));
            }
        }

        if (languageDataList.size() != 0) {
            ArrayList<String> tempList = new ArrayList<>();
            for (PojoFilterLanguage.Data data : languageDataList) {
                if (data.languageName != null && data.isSelected) {
                    tempList.add(data._id);
                }
            }
            if (tempList.size() != 0) {
                hashMap.put("languages", new Gson().toJson(tempList));
            }
        }


        Log.e(TAG, "m list"+new Gson().toJson(materialList));
        if (materialList.size() != 0) {
            ArrayList<String> tempList = new ArrayList<>();
            for (PojoFilterLanguage.Data data : materialList) {
                if (data.languageName != null && data.isSelected) {

                    if (data.languageName.equalsIgnoreCase("Cleaner with material")) {
                        tempList.add("1");

                    } else if (data.languageName.equalsIgnoreCase("Cleaner without material")) {
                        tempList.add("2");
                    }
                }
            }
            if (tempList.size() != 0) {
                hashMap.put("material", new Gson().toJson(tempList));
            }
        }

        if (!searchMaidName.isEmpty()) {
            hashMap.put("searchMaidName", searchMaidName);
            isSearch = true;
        }

        if (GeneralFunction.isNetworkConnected(getActivity(), getActivity().findViewById(android.R.id.content))) {
            presenter.apiSearchMaid(hashMap, isSearch);
        } else {
            binding.swipeRefresh.setRefreshing(false);
        }
    }

    public void getSearchDataList() {
        if (GeneralFunction.isNetworkConnected(getActivity(), getView())) {
            try {
                if (bookingTimeZone == null) {
                    final String googleApiKey = getString(R.string.google_api_key_mak);
                    presenter.getTimeZoneFromLatLong(searchMaidModel.lat, searchMaidModel.lng, googleApiKey);
                } else {
                    apiSearchMaidData(bookingTimeZone);

                }
            }catch (Exception e){

            }

        } else {
            binding.viewFlipper.setDisplayedChild(1);
            binding.noDataView.setData(getString(R.string.cant_connect));
            binding.noDataView.setImage(ContextCompat.getDrawable(getActivity(), R.drawable.no_internet));
            binding.swipeRefresh.setRefreshing(false);
        }
    }

    private void setListener() {
        binding.llSort1.setOnClickListener(this);
        binding.llFilter.setOnClickListener(this);
        binding.tvCancel.setOnClickListener(this);
        //  binding.searchView.setOnQueryTextListener(this);
        mBottomSheetDialog.findViewById(R.id.llExperience).setOnClickListener(this);
        mBottomSheetDialog.findViewById(R.id.llRating).setOnClickListener(this);
        mBottomSheetDialog.findViewById(R.id.tvReset).setOnClickListener(this);
        mBottomSheetDialog.findViewById(R.id.llPrice).setOnClickListener(this);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem search = menu.findItem(R.id.action_simpleSearch);
        // set your desired icon here based on a flag if you like
        search.setIcon(ContextCompat.getDrawable(context, R.drawable.ic_search_white));

        menu.findItem(1);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_simpleSearch:
                binding.searchView.setOnQueryTextListener(this);
                binding.rlsearchBar.setVisibility(View.VISIBLE);
                return true;
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        MenuItem searchItem = menu.findItem(R.id.action_simpleSearch);
        if (searchItem != null
                && maidDataList.size() == 0 && !isSearch) {
            searchItem.setVisible(false);
        } else {
            if (searchItem != null)
                searchItem.setVisible(true);
        }

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        pageNo = 1;
        searchMaidName = query;
        if (searchMaidName.isEmpty()) {
            maidSearchAdapter.isSearchResult(false);
        } else {
            maidSearchAdapter.isSearchResult(true);
        }
        getSearchDataList();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        pageNo = 1;
        searchMaidName = newText;
        if (searchMaidName.isEmpty()) {
            maidSearchAdapter.isSearchResult(false);
        } else {
            maidSearchAdapter.isSearchResult(true);
        }
        getSearchDataList();
        return false;
    }

    @Override
    public void setRefreshing(boolean isRefreshing) {
        binding.swipeRefresh.setRefreshing(isRefreshing);
    }

    @Override
    public void setLoading(boolean isLoading) {
        if (isLoading)
            GeneralFunction.showProgress(getActivity());
        else
            GeneralFunction.dismissProgress();
    }

    @Override
    public void sessionExpired() {
        GeneralFunction.isUserBlocked(getActivity());
        binding.swipeRefresh.setRefreshing(false);
    }

    @Override
    public void searchMaidSuccess(PojoSearchMaid searchMaidData) {
        Log.d(TAG, "search res"+" " + new Gson().toJson(searchMaidData));
        Log.d(TAG, "searchMaidSuccess: page number" + pageNo);

        searchMaidData1 = searchMaidData;

        if (pageNo == 1 || isSearch==true) {
            Log.d(TAG, "searchMaidSuccess: if (pageNo == 1 || isSearch )");
            maidDataList.clear();
            Log.d(TAG, "searchMaidSuccess:  if (pageNo == 1 || isSearch==true)");
            endlessRecyclerOnScrollListener.reset();
            if (searchMaidData.getData().getSuggestedMaids().size() != 0) {
                Log.d(TAG, "searchMaidSuccess: if (searchMaidData.getData().getSuggestedMaids().size() != 0)");
                headerCount = searchMaidData.getData().getSuggestedMaids().size();
                maidDataList.add(new MaidData());
                otherList = false;
                maidDataList.addAll(searchMaidData.getData().getSuggestedMaids());
                maidSearchAdapter.setType(otherList);
                maidSearchAdapter.setModele(searchMaidData);
            }
            Log.e(TAG,"SuggestedMaids"+" "+maidDataList.size());
        }
        if (searchMaidData.getData().getRequestedMaids().size() != 0) {
          Log.d(TAG, "searchMaidSuccess: if (searchMaidData.getData().getRequestedMaids().size() != 0)");
            if (pageNo == 1) {
               Log.d(TAG, "searchMaidSuccess: if (pageNo == 1)");
                maidDataList.add(new MaidData());
            }
            otherList = false;
            maidDataList.addAll(searchMaidData.getData().getRequestedMaids());
            maidSearchAdapter.setSecondHeader(headerCount);
            maidSearchAdapter.setType(otherList);
            maidSearchAdapter.setModele(searchMaidData);
        }


        if (searchMaidData.getData().getSuggestedMaids().size() == 0 && searchMaidData.getData().getRequestedMaids().size() == 0) {
            Log.d(TAG, "searchMaidSuccess: ");
            // Toast.makeText(getActivity(), "enrter", Toast.LENGTH_SHORT).show();
            //maidDataList.clear();
            if (pageNo == 1) {
                if (searchMaidData.getData().getOtherMaids().size() == 0) {
                    binding.viewFlipper.setDisplayedChild(1);
                    mFirebaseAnalytics.logEvent("avmscr_empty", null);
                    binding.noDataView.setData(getString(R.string.no_maid_found));
                //    binding.noDataView.setImage(ContextCompat.getDrawable(getActivity(), R.drawable.ic_no_data));
                } else {
                    if (errorPopUpDialog != null) {
                        errorPopUpDialog.dismiss();
                    }
                    errorPopUpDialog = new DialogPopup().alertPopup(getActivity(), getActivity().getResources().getString(R.string.dialog_alert),
                            getActivity().getResources().getString(R.string.alertHeading), "others");
                    maidDataList.add(new MaidData());
                }
            }
            if(searchMaidData.getData().getOtherMaids().size()!=0){
                otherList = true;
                Log.e(TAG,"listchk "+otherList.toString());
                maidSearchAdapter.setType(otherList);
                maidDataList.addAll(searchMaidData.getData().getOtherMaids());

            }else {
                otherList = true;
                Log.e(TAG,"listchk"+otherList.toString());
                maidSearchAdapter.setType(otherList);
                maidDataList.addAll(searchMaidData.getData().getOtherMaids());
//                maidSearchAdapter.notifyDataSetChanged();

            }


        }
        Log.e(TAG,"maidlist=="+""+searchMaidData.getData().getSuggestedMaids().size());
        maidSearchAdapter.notifyDataSetChanged();
        if (maidSearchAdapter.getItemCount() == 0) {
            binding.viewFlipper.setDisplayedChild(1);
            mFirebaseAnalytics.logEvent("avmscr_empty", null);
            binding.noDataView.setData(getString(R.string.no_maid_found));
            //   binding.noDataView.setImage(ContextCompat.getDrawable(getActivity(), R.drawable.ic_no_data));
            binding.tvSort.setEnabled(false);   // Disable sort when no result is found
        }
        else {
            binding.viewFlipper.setDisplayedChild(2);
            binding.tvSort.setEnabled(true);    // Enable sort when result is found
        }
        isSearch = false;
        maidSearchAdapter.notifyDataSetChanged();
        binding.swipeRefresh.setRefreshing(false);
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void bookingTimeZoneReceived(@Nullable TimeZone timeZone) {
        if (timeZone == null) {
            Toast.makeText(getActivity(), R.string.unable_to_locate_time_zone, Toast.LENGTH_SHORT).show();
        } else {
            this.bookingTimeZone = timeZone;
            apiSearchMaidData(timeZone);
        }
    }

    @Override
    public void maidNotAvailable(String errorMessage) {
        if (errorPopUpDialog != null) {
            errorPopUpDialog.dismiss();
        }
        errorPopUpDialog = new DialogPopup().alertPopup(getActivity(), getResources().getString(R.string.dialog_alert), errorMessage, "MAID_NOT_AVAILABLE");
        errorPopUpDialog.show(requireActivity().getSupportFragmentManager(),"iosDialog");
        binding.swipeRefresh.setRefreshing(false);
    }

    @Override
    public void signupFailure(String failureMessage) {
        if (errorPopUpDialog != null) {
            errorPopUpDialog.dismiss();
        }
        Log.e(TAG, "signupFailure: "+failureMessage);
        errorPopUpDialog = new DialogPopup().alertPopup(getActivity(), getResources().getString(R.string.dialog_alert), getString(R.string.check_connection), "");
        errorPopUpDialog.show(requireActivity().getSupportFragmentManager(),"iosDialog");
        binding.swipeRefresh.setRefreshing(false);
    }

    @Override
    public void onMaidTimeSlotsClicked(MaidData maidData) {
        if (GeneralFunction.isNetworkConnected(getActivity(), getView())) {
            presenter.getTimeSlots(maidData.get_id(), bookingTimeZone.getID());
        }
    }

    @Override
    public void displayTimeSlots(TimeSlot timeSlot) {
        Intent intent = new Intent(context, SlotAvailbleActivity.class);
        intent.putExtra("data", timeSlot);
        context.startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        //detach the presenter view
        presenter.detachView();
        if (errorPopUpDialog != null && errorPopUpDialog.getDialog() != null && errorPopUpDialog.getDialog().isShowing()) {
            errorPopUpDialog.dismiss();
        }

    }

    @Override
    public void startIntent(MaidData data, SelectableRoundedImageView ivMaid, int adapterPosition) {
        if (adapterPosition == 0) {
            mFirebaseAnalytics.logEvent("avmscr_tap_1", null);
        } else if (adapterPosition == 1) {
            mFirebaseAnalytics.logEvent("avmscr_tap_2", null);
        } else if (adapterPosition == 2) {
            mFirebaseAnalytics.logEvent("avmscr_tap_3", null);
        }

        searchMaidModel.maidId = data.get_id();
        searchMaidModel.agencyName = data.getAgencyName();
        searchMaidModel.agencyId = data.getAgencyId();
        searchMaidModel.agencyType = data.getAgencyType();
        searchMaidModel.makId = data.getMakId();
        searchMaidModel.maidPrice = data.getActualPrice();
        searchMaidModel.currency = data.getCurrency();

        String firstName = data.getFirstName().substring(0, 1).toUpperCase() + data.getFirstName().substring(1);
        String lastName = "";
        if (data.getLastName() != null && !data.getLastName().isEmpty()) {
            lastName = data.getLastName().substring(0, 1).toUpperCase() + data.getLastName().substring(1);
        }
        searchMaidModel.maidName = String.format("%s %s", firstName, lastName);


        maid_temp = data;
        Log.d(TAG, "startIntent: Maid Data:--  "+ new Gson().toJson(data));
        MaidProfileFragment maidProfileFragment = MaidProfileFragment.newInstance(bookingDataModel, data, searchMaidModel, rescheduleStatus, serviceId);
        DataVariable.hideSoftKeyboard(getActivity());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            maidProfileFragment.setSharedElementEnterTransition(new DetailsTransition());
            maidProfileFragment.setEnterTransition(new Fade());
            setExitTransition(new Fade());
            maidProfileFragment.setSharedElementReturnTransition(new DetailsTransition());
        }
        this.ivMaid = ivMaid;
        this.adapterPosition = adapterPosition;
        getActivity().getSupportFragmentManager().beginTransaction()
                .addSharedElement(ivMaid, adapterPosition + "")
                //.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.frameLayout, maidProfileFragment, "MaidProfileFragment")
                .addToBackStack("MaidProfileFragment").commit();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.llSort1:
                mFirebaseAnalytics.logEvent("avmscr_tap_sort", null);
                openBottomDialog();
                break;

            case R.id.llFilter:
                mFirebaseAnalytics.logEvent("avmscr_tap_fl", null);
                Intent intent = new Intent(getActivity(), FilterActivity.class);
                intent.putParcelableArrayListExtra("nationalityList", (ArrayList<PojoFilterLanguage.Data>) nationalityList);
                intent.putParcelableArrayListExtra("languageList", (ArrayList<PojoFilterLanguage.Data>) languageDataList);
                intent.putParcelableArrayListExtra("genderList", (ArrayList<PojoFilterLanguage.Data>) genderList);
                intent.putParcelableArrayListExtra("religionList", (ArrayList<PojoFilterLanguage.Data>) religionList);
                intent.putParcelableArrayListExtra("materialList", (ArrayList<PojoFilterLanguage.Data>) materialList);
                intent.putParcelableArrayListExtra("agencylist", (ArrayList<PojoFilterLanguage.Data>) agencylist);
                intent.putExtra(Constants.LAST_SELECTED_POSITION, lastSelectedPosition);
                intent.putExtra("currency",searchMaidModel.currency);
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.llExperience:
                pageNo = 1;
                tvReset.setVisibility(View.VISIBLE);
                mBottomSheetDialog.dismiss();
                selectedSortBy = sortBy.get(0);
                getSearchDataList();
                setColor(EXPERIENCE);
                break;
            case R.id.llRating:
                pageNo = 1;
                tvReset.setVisibility(View.VISIBLE);
                mBottomSheetDialog.dismiss();
                selectedSortBy = sortBy.get(2);
                getSearchDataList();
                setColor(RATING);
                break;
            case R.id.llPrice:
                pageNo = 1;
                tvReset.setVisibility(View.VISIBLE);
                mBottomSheetDialog.dismiss();
                showPriceDialog().show(requireActivity().getSupportFragmentManager(),"IOS_Dialog");
                break;
            case R.id.tvReset:
                tvReset.setVisibility(View.GONE);
                mBottomSheetDialog.dismiss();
                selectedSortBy = "";
                getSearchDataList();
                clearAllSort();
                break;
            case R.id.tvCancel:
                binding.searchView.setQuery("", true);
                binding.searchView.setOnQueryTextListener(null);
                maidSearchAdapter.isSearchResult(false);
                binding.rlsearchBar.setVisibility(View.GONE);
                if (searchMaidName.trim().isEmpty()) {
                    searchMaidName = "";
                    getSearchDataList();
                }
                break;
        }
    }

    private IOSAlertDialog showPriceDialog() {
        String[] array = {getString(R.string.high_to_low), getString(R.string.low_to_high)};

        int byDefaultCheck = -1;

        switch (selectedSortBy) {
            case "PRICE_HIGH":
                byDefaultCheck = 0;
                break;
            case "PRICE_LOW":
                byDefaultCheck = 1;
                break;
        }

        int finalByDefaultCheck = byDefaultCheck;
        IOSAlertDialog iosAlertDialog =  IOSAlertDialog.newInstance(
                requireContext(),
                getString(R.string.select_price),
                array,
                byDefaultCheck,
                getString(R.string.ok),
                getString(R.string.cancel1),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Positive button click listener
                        getSearchDataList();
                        setColor(PRICE);
                        dialog.dismiss();
                    }
                },
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Negative button click listener
                    if(finalByDefaultCheck == -1) {
                        selectedSortBy = "";
                    }else if(finalByDefaultCheck == 0){
                        selectedSortBy = "PRICE_HIGH";
                    }else if(finalByDefaultCheck == 1){
                        selectedSortBy = "PRICE_LOW";
                    }
                        dialog.dismiss();
                    }
                },
                ContextCompat.getColor(requireContext(), R.color.app_color),
                ContextCompat.getColor(requireContext(), R.color.app_color),
                false,
                true
        );

        iosAlertDialog.setRadioItemClickListener(new IOSAlertDialog.RadioItemClickListener() {
            @Override
            public void onRadioItemClick(String selectedItem, int which) {
                // Handle the selected radio item
                android.util.Log.e(TAG, "onRadioItemClick:  " + which);
                if (which == 0) {
                    selectedSortBy = "PRICE_HIGH";
                } else if (which == 1) {
                    selectedSortBy = "PRICE_LOW";
                }
            }
        });

        return  iosAlertDialog;


 /*       AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
       builder.setTitle(R.string.select_price)
                .setSingleChoiceItems(array, byDefaultCheck, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (which == 0) {
                            selectedSortBy = "PRICE_HIGH";
                        } else if (which == 1) {
                            selectedSortBy = "PRICE_LOW";
                        }

                    }
                })
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        getSearchDataList();
                        setColor(PRICE);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(getString(R.string.cancel1), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        return builder.create();*/
    }

    private void clearAllSort() {
        tvExperience.setTextColor(ContextCompat.getColor(getContext(), R.color.colorBlack65));
        tvRating.setTextColor(ContextCompat.getColor(getContext(), R.color.colorBlack65));
        tvPrice.setTextColor(ContextCompat.getColor(getContext(), R.color.colorBlack65));
        String language = Prefs.with(getActivity()).getString(Constants.LANGUAGE_CODE, "");
        if (language.equalsIgnoreCase("ar")) {
            tvExperience.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_experience, 0);
            tvRating.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_rating_sort, 0);
            tvPrice.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_price_grey, 0);
        } else {
            tvExperience.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_experience, 0, 0, 0);
            tvRating.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_rating_sort, 0, 0, 0);
            tvPrice.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_price_grey, 0, 0, 0);
        }

    }

    private void setColor(String type) {
        clearAllSort();
        String language = Prefs.with(getActivity()).getString(Constants.LANGUAGE_CODE, "");
        switch (type) {
            case EXPERIENCE:
                tvExperience.setTextColor(ContextCompat.getColor(getContext(), R.color.app_color));
                if (language.equalsIgnoreCase("ar")) {
                    tvExperience.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_experience_selected, 0);
                }else {
                    tvExperience.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_experience_selected, 0, 0, 0);
                }
                break;
            case RATING:
                tvRating.setTextColor(ContextCompat.getColor(getContext(), R.color.app_color));
                if (language.equalsIgnoreCase("ar")) {
                    tvRating.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_ratings_selected, 0);
                }else {
                    tvRating.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_ratings_selected, 0, 0, 0);
                }
                break;
            case PRICE:
                tvPrice.setTextColor(ContextCompat.getColor(getContext(), R.color.app_color));
                if (language.equalsIgnoreCase("ar")) {
                    tvPrice.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_price, 0);
                }else {
                    tvPrice.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_price, 0, 0, 0);
                }

                break;
        }
    }

    private void openBottomDialog() {
        mBottomSheetDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            pageNo = 1;
            nationalityList = data.getParcelableArrayListExtra("nationalityList");
            agencylist = data.getParcelableArrayListExtra("agencyList");
            languageDataList = data.getParcelableArrayListExtra("languageList");
            genderList = data.getParcelableArrayListExtra("genderList");
            religionList = data.getParcelableArrayListExtra("religionList");
            materialList = data.getParcelableArrayListExtra("materialList");
            lastSelectedPosition = data.getIntExtra(Constants.LAST_SELECTED_POSITION, 0);
            getSearchDataList();
        } else if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_CANCELED) {

        }
    }
}
