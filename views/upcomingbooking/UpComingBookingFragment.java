package com.maktoday.views.upcomingbooking;

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
import com.maktoday.databinding.FragmentUpcomingBinding;
import com.maktoday.interfaces.CancelBookingInterface;
import com.maktoday.interfaces.ExtensionUpdate;
import com.maktoday.interfaces.OpenMaid;
import com.maktoday.interfaces.RescheduleUpdate;
import com.maktoday.model.ApiResponse;
import com.maktoday.model.MaidData;
import com.maktoday.model.PojoMyBooking;
import com.maktoday.utils.Constants;
import com.maktoday.utils.EndlessRecyclerOnScrollListener;
import com.maktoday.utils.GeneralFunction;
import com.maktoday.utils.Log;
import com.maktoday.views.cancelbooking.CancelBookingDialog;
import com.maktoday.views.extendservice.ExtendServiceDialog;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by cbl81 on 28/10/17.
 */

public class UpComingBookingFragment extends Fragment implements UpComingContract.View, CancelBookingInterface, DialogInterface, OpenMaid, ExtensionUpdate, RescheduleUpdate {

    private static final String TAG = "UpComingBookingFragment";
    private final int LIMIT = 10;
    private FragmentUpcomingBinding binding;
    private UpComingContract.Presenter presenter;
    private BookingAdapter bookingAdapter;
    private List<PojoMyBooking.Datum> bookingList = new ArrayList<>();
    private int page = 1;
    public static RescheduleUpdate rescheduleUpdate;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentUpcomingBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
      Log.d(TAG, "onViewCreated: StartActivity");
        init();
        setData();
        getDataList();
        rescheduleUpdate=this;
    }

    private void init() {
        presenter = new UpComingPresenter();
        presenter.attachView(this);
        page = 1;
    }

    @Override
    public void onResume() {
        super.onResume();
        //getDataList();
    }

    private void setData() {
        binding.viewFlipper.setDisplayedChild(2);
        bookingAdapter = new BookingAdapter(getActivity(), "UpComing", bookingList, this, this);
        binding.rvUpComing.setAdapter(bookingAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        binding.rvUpComing.setLayoutManager(linearLayoutManager);

        EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener = new EndlessRecyclerOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                page = currentPage;
                getDataList();
            }
        };
        endlessRecyclerOnScrollListener.setVisibleThreshold(LIMIT);

        binding.rvUpComing.addOnScrollListener(endlessRecyclerOnScrollListener);

        binding.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                binding.swipeRefresh.setRefreshing(true);
                page = 1;
                getDataList();
            }
        });
    }

    public void getDataList() {
        if (GeneralFunction.isNetworkConnected(getActivity(), getView())) {
            binding.swipeRefresh.setRefreshing(true);
            if (presenter != null) {

                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("uniquieAppKey", Constants.UNIQUE_APP_KEY);
                hashMap.put("onBasisOfDate", "UPCOMING");
                hashMap.put("timeZone", TimeZone.getDefault().getID());
                hashMap.put("pageNo", String.valueOf(page));
                hashMap.put("limit", String.valueOf(LIMIT));
                presenter.apiUpComing(hashMap);
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
    public void upComingSuccess(ApiResponse<PojoMyBooking> data) {
        android.util.Log.d(TAG, "upComingSuccess: list :---   " + new Gson().toJson(data));

        if (page == 1) {
            bookingList.clear();
        }
        bookingList.addAll(data.getData().data);
        Log.e("sizee",data.getData().data.size()+"");
        bookingAdapter.notifyDataSetChanged();
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
    public void error(String errorMsg) {
        if (binding.parent.getChildAt(1) != null)
            GeneralFunction.showSnackBar(getActivity(), binding.parent.getChildAt(1), errorMsg);
        binding.swipeRefresh.setRefreshing(false);
    }

    @Override
    public void failure(String failureMessage) {
      Log.e(TAG, "failure: "+ failureMessage);
        if (binding.parent.getChildAt(1) != null)
            GeneralFunction.showSnackBar(getActivity(), binding.parent.getChildAt(1), getString(R.string.check_connection));
        binding.swipeRefresh.setRefreshing(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.detachView();
    }

    @Override
    public void showCancelDialog(String bookingId, String timeZone) {
        FragmentManager fragmentManager = getChildFragmentManager();
        CancelBookingDialog dialog = CancelBookingDialog.newInstance(
                String.valueOf(bookingId),
                String.valueOf(timeZone));
        dialog.show(fragmentManager, "CancelBookingDialog");

    }

    @Override
    public void showExtendDialog(String payment_mode, String referenceId, String serviceMakId, Float actualPrice, String currency, String vat) {
        FragmentManager fragmentManager = getChildFragmentManager();
        fragmentManager.beginTransaction().addToBackStack(null);
        ExtendServiceDialog.newInstance(this,payment_mode,referenceId,serviceMakId, actualPrice, currency, vat).show(fragmentManager, "ExtendServiceDialog");
    }

    @Override
    public void cancel() {
        page = 1;
    }
    @Override
    public void dismiss() {
        page = 1;
        getDataList();
    }

    @Override
    public void openMaidProfile(MaidData maidData) {
    }

    @Override
    public void extensionUpdate() {
    }

    @Override
    public void rescheduleUpdate() {
        binding.swipeRefresh.setRefreshing(true);
        page = 1;
        getDataList();
    }
}
