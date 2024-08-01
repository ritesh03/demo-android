package com.maktoday.views.ongoingbooking;

import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.maktoday.R;
import com.maktoday.adapter.BookingAdapter;
import com.maktoday.databinding.FragmentOngoingBinding;
import com.maktoday.interfaces.CancelBookingInterface;
import com.maktoday.interfaces.ExtensionUpdate;
import com.maktoday.interfaces.OpenMaid;
import com.maktoday.model.ApiResponse;
import com.maktoday.model.MaidData;
import com.maktoday.model.PojoMyBooking;
import com.maktoday.utils.Constants;
import com.maktoday.utils.DialogPopup;
import com.maktoday.utils.EndlessRecyclerOnScrollListener;
import com.maktoday.utils.GeneralFunction;
import com.maktoday.utils.Log;
import com.maktoday.views.extendservice.ExtendServiceDialog;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by cbl81 on 28/10/17.
 */

public class OnGoingBookingFragment extends Fragment implements OnGoingContract.View,
        CancelBookingInterface, DialogInterface.OnDismissListener, OpenMaid, ExtensionUpdate {

    private static final String TAG = "OnGoingBookingFragment";
    private final int LIMIT = 10;
    private FragmentOngoingBinding binding;
    private OnGoingContract.Presenter presenter;
    private List<PojoMyBooking.Datum> bookingList = new ArrayList<>();
    private BookingAdapter bookingAdapter;
    private int page = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentOngoingBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: StartActivity");
        init();
        setData();
        getDataList();
    }

    private void init() {
        presenter = new OnGoingPresenter();
        presenter.attachView(this);
        page = 1;
    }

    private void setData() {
        binding.viewFlipper.setDisplayedChild(2);
        bookingAdapter = new BookingAdapter(getActivity(), "OnGoing", bookingList, this, this);
        binding.rvOnGoing.setAdapter(bookingAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        binding.rvOnGoing.setLayoutManager(linearLayoutManager);
        EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener = new EndlessRecyclerOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                page = currentPage;
                getDataList();
            }
        };
        endlessRecyclerOnScrollListener.setVisibleThreshold(LIMIT);
        binding.rvOnGoing.addOnScrollListener(endlessRecyclerOnScrollListener);

        binding.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                binding.swipeRefresh.setRefreshing(true);
                getDataList();
            }
        });
    }

    private void getDataList() {
        if (GeneralFunction.isNetworkConnected(getActivity(), getView())) {
            binding.swipeRefresh.setRefreshing(true);
            if (presenter != null) {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("uniquieAppKey", Constants.UNIQUE_APP_KEY);
                hashMap.put("onBasisOfDate", "ON_GOING");
                hashMap.put("timeZone", TimeZone.getDefault().getID());
                hashMap.put("pageNo", String.valueOf(page));
                hashMap.put("limit", String.valueOf(LIMIT));
                Log.e("param",hashMap.toString());
                presenter.apiOnGoing(hashMap);
            }
        } else {
            binding.noDataView.setData(getString(R.string.cant_connect));
            binding.noDataView.setImage(ContextCompat.getDrawable(getActivity(), R.drawable.no_internet));
            binding.viewFlipper.setDisplayedChild(1);
        }
    }

    @Override
    public void setLoading(boolean isLoading) {
        binding.swipeRefresh.setRefreshing(isLoading);
    }

    @Override
    public void sessionExpired() {
        GeneralFunction.isUserBlocked(getActivity());
        binding.swipeRefresh.setRefreshing(false);
    }

    @Override
    public void onGoingSuccess(ApiResponse<PojoMyBooking> data) {
    Log.d(TAG, "onGoingSuccess: "+ new Gson().toJson(data));
        if (page == 1) {
            bookingList.clear();
        }
        bookingList.addAll(data.getData().data);
        bookingAdapter.notifyDataSetChanged();
        binding.swipeRefresh.setRefreshing(false);
        if (bookingAdapter.getItemCount() == 0) {
            binding.noDataView.setData(getString(R.string.no_booking_found));
            //binding.noDataView.setImage(ContextCompat.getDrawable(getActivity(), R.drawable.ic_no_data));
            binding.viewFlipper.setDisplayedChild(1);
        } else {
            binding.viewFlipper.setDisplayedChild(2);
        }
        binding.swipeRefresh.setRefreshing(false);
    }

    @Override
    public void error(String errorMessage) {
        new DialogPopup().alertPopup(getActivity(), getResources().getString(R.string.dialog_alert), errorMessage, "").show(requireActivity().getSupportFragmentManager(), "ios_dialog");
        binding.swipeRefresh.setRefreshing(false);
    }

    @Override
    public void failure(String failureMessage) {
       Log.e(TAG, "failure: "+ failureMessage);
        new DialogPopup().alertPopup(getActivity(), getResources().getString(R.string.dialog_alert),  getString(R.string.check_connection), "").show(requireActivity().getSupportFragmentManager(), "ios_dialog");
        binding.swipeRefresh.setRefreshing(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.detachView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void showCancelDialog(String bookingId, String timeZone) {

    }

    @Override
    public void showExtendDialog(String payment_mode, String referenceId, String serviceMakId, Float actualPrice, String currency, String vat) {
        FragmentManager fragmentManager = getChildFragmentManager();
        fragmentManager.beginTransaction().addToBackStack(null);
        ExtendServiceDialog.newInstance(this,payment_mode,referenceId, serviceMakId,actualPrice, currency,vat).show(fragmentManager, "ExtendServiceDialog");
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        page = 1;
        getDataList();
    }

    @Override
    public void openMaidProfile(MaidData maidData) {

    }

    @Override
    public void extensionUpdate() {
        page = 1;
        binding.swipeRefresh.setRefreshing(true);
        getDataList();
    }
}
