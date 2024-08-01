package com.maktoday.views.AllService;

import static com.maktoday.views.main.Main2Activity.countTextView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.maktoday.R;
import com.maktoday.adapter.ServicelistAdapter;
import com.maktoday.databinding.FragmentServiceBinding;
import com.maktoday.model.MaidData;
import com.maktoday.model.PojoMyBooking;
import com.maktoday.model.SearchMaidModel;
import com.maktoday.model.ServicelistResponse;
import com.maktoday.model.ServicesProvide;
import com.maktoday.utils.Constants;
import com.maktoday.utils.DialogPopup;
import com.maktoday.utils.GeneralFunction;
import com.maktoday.utils.Log;
import com.maktoday.utils.Prefs;
import com.maktoday.views.home.HomeActivity;
import com.maktoday.views.home.HomeFragment;
import com.maktoday.views.main.Main2Activity;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import java.util.ArrayList;
import java.util.List;

public class ServiceFragment extends Fragment implements ServiceContract.View,ServicelistAdapter.ServiceOnclick{
    private static final String TAG = "ServiceFragment";
    public FragmentServiceBinding binding;
    ServiceContract.Presenter presenter;
    ServicelistAdapter servicelistAdapter;
    List<ServicesProvide> servicelist;
    ServicelistAdapter.ServiceOnclick serviceOnclick;
    public static int noticount=0;
    public static String servicesID;
    private boolean isBookAgain = false; // this fragment also used in Booking when we use "Book Again" functionality.
    private String reschuleStatus = ""; // this fragment also used in Booking when we use "Book Again" functionality.
    private final String SERVICE_ID = "";
    private SearchMaidModel searchMaidModel;
    private PojoMyBooking.Datum bookingDataModel;
    private MaidData maidData;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding=FragmentServiceBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        android.util.Log.d(TAG, "onViewCreated: StartActivity");
        init();
        setData();
        servicesID="";
        binding.tvServiceNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (GeneralFunction.isNetworkConnected(requireActivity(), binding.rlparent)) {
                    if (servicesID.isEmpty()) {
                        GeneralFunction.showSnackBar(getActivity(), binding.rlparent, getResources().getString(R.string.select_service));
                    } else {
                        if (isBookAgain) {
                            Bundle bundle = new Bundle();
                            android.util.Log.d(TAG, "onClick: isbook again");
                            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
                            HomeFragment homeFragment = new HomeFragment();
                            bundle.putBoolean(Constants.BOOK_AGAIN, isBookAgain);
                            bundle.putString(Constants.reschuleStatus, getArguments().getString(Constants.reschuleStatus));
                            bundle.putParcelable(Constants.SEARCH_MAID_DATA, searchMaidModel);
                            bundle.putString(Constants.BOOKING_DATA, getArguments().getString(Constants.BOOKING_DATA));
                            bundle.putParcelable(Constants.MAID_AVAILABLE_TIMESLOT, getArguments().getParcelable(Constants.MAID_AVAILABLE_TIMESLOT));
                            bundle.putString(Constants.SERVICE_ID, getArguments().getString(Constants.SERVICE_ID));
                            bundle.putString(Constants.BOOKING_TYPE, getArguments().getString(Constants.BOOKINGT_YPE));
                            android.util.Log.d(TAG, "onClick: maidData:--  "+ new Gson().toJson(getArguments().getString(Constants.MAID_DATA)));
                            bundle.putString(Constants.MAID_DATA, getArguments().getString(Constants.MAID_DATA));
                           // android.util.Log.d(TAG, "maid data inside service fragment if book again "+ new Gson().toJson(getArguments().getString(Constants.MAID_DATA)));
                            bundle.putString("payment_mode", getArguments().getString("payment_mode"));
                            bundle.putString("lat", getArguments().getString("lat"));
                            bundle.putString("lng", getArguments().getString("lng"));
                            homeFragment.setArguments(bundle);
                            // fragmentTransaction.add(android.R.id.content, homeFragment).addToBackStack("HomeFragment").commit();
                            fragmentTransaction.add(R.id.flBookAgain, homeFragment).addToBackStack("HomeFragment").commit();
                        } else {
                            android.util.Log.d(TAG, "onClick: else or not is book again");
                       /* Fragment homeFagment= new HomeFragment();
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                                .add(android.R.id.content, homeFagment, "HomeFragment")
                                .addToBackStack("HomeFragment").commit();*/
                            startActivity(new Intent(getActivity(), HomeActivity.class));
                        }

                    }
                }
            }
        });

    }

    private void init() {

        presenter=new ServicePresenter();
        presenter.attachView(this);
        //book again data
        if (getArguments() != null) {
            isBookAgain = getArguments().getBoolean(Constants.BOOK_AGAIN);
            reschuleStatus = getArguments().getString(Constants.reschuleStatus);
            bookingDataModel = new Gson().fromJson(getArguments().getString(Constants.BOOKING_DATA), PojoMyBooking.Datum.class);
            searchMaidModel = getArguments().getParcelable(Constants.SEARCH_MAID_DATA);
            maidData= new Gson().fromJson(getArguments().getString(Constants.MAID_DATA),MaidData.class);
            Log.d(TAG, "init: maid data:--  "+ new Gson().toJson(maidData));
        }
        Log.e(TAG,"bookagain====="+isBookAgain);
        // hit api getAllNormalService
        setLoading(true);
        binding.swipeToRefresh.setRefreshing(true);
        if(GeneralFunction.isNetworkConnected(requireActivity(),binding.rlparent)) {
            presenter.apiServicelist();
        }
        binding.swipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                binding.swipeToRefresh.setRefreshing(true);
                if(GeneralFunction.isNetworkConnected(requireActivity(),binding.rlparent)) {
                    presenter.apiServicelist();
                }else {
                    binding.swipeToRefresh.setRefreshing(false);
                }
            }
        });
    }
    private void setData() {
        servicelist=new ArrayList<>();
        serviceOnclick=this;
        binding.rvServicelist.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.rvServicelist.setHasFixedSize(false);
        servicelistAdapter = new ServicelistAdapter(getActivity(),servicelist,serviceOnclick);
        binding.rvServicelist.setAdapter(servicelistAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(GeneralFunction.isNetworkConnected(requireActivity(),binding.rlparent)) {
            presenter.getNotiCount(Prefs.with(getActivity()).getString(Constants.ACCESS_TOKEN, ""));
        }
    }

    @Override
    public void setLoading(boolean isLoading) {
        binding.swipeToRefresh.setRefreshing(isLoading);
    }

    @Override
    public void sessionExpired() {
       binding.swipeToRefresh.setRefreshing(false);
        try {
            GeneralFunction.isUserBlocked(getActivity());
        }catch (Exception e){
            Log.d(TAG, "sessionExpired: "+e.getMessage());
        }
    }

    @Override
    public void ServiceListSuccess(ServicelistResponse body) {
       binding.swipeToRefresh.setRefreshing(false);
        servicelist.clear();

        Bundle bundle = getArguments();
        if(bundle != null && bundle.containsKey(Constants.isFavorite) && bundle.getString(Constants.isFavorite).equals("yes")){
            SearchMaidModel searchMaidModel1 =  bundle.getParcelable(Constants.SEARCH_MAID_DATA);
            if(searchMaidModel1.services.get(0).get_id().equals("6155aa5954f19a0763558690")){
                servicelist.addAll(body.getData());
            }else {
                servicelist.addAll(searchMaidModel1.services);
            }

        }else {
            servicelist.addAll(body.getData());
        }
        servicelistAdapter.adddata(servicelist);
        servicelistAdapter.notifyDataSetChanged();
    }

    @Override
    public void apiFailure(String failureMessage) {
        binding.swipeToRefresh.setRefreshing(false);
        Activity activity = getActivity();
        if(activity != null) {
            new DialogPopup().alertPopup(activity, activity.getString(R.string.dialog_alert), activity.getString(R.string.check_connection), "").show(requireActivity().getSupportFragmentManager(),"IOS_Dialog");
        }
    }

    @Override
    public void notiResopnse(Integer noti_count) {
        try {
            if (noti_count > 0) {
                Main2Activity.redCircle.setVisibility(View.VISIBLE);
                countTextView.setText(noti_count.toString());
                // HomeFragment.noticount=noti_count;
                // ShortcutBadger.applyCount(getActivity(),noti_count);

       /*     if (noti_count > 10) {
                countTextView.setText(noti_count.toString() + "+");
            } else {
                countTextView.setText(noti_count.toString());
            }*/
            } else {
                Main2Activity.redCircle.setVisibility(View.GONE);
                countTextView.setText("");
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
    public void itemonclick(String id, String name) {
        Log.e("ServiceFragment",id+" "+name);
        servicesID=id;
    }
}
