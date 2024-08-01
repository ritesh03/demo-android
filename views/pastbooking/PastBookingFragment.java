package com.maktoday.views.pastbooking;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.maktoday.utils.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.maktoday.R;
import com.maktoday.adapter.BookingAdapter;
import com.maktoday.databinding.FragmentPastBinding;
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
import com.maktoday.views.extendservice.ExtendServiceDialog;
import com.maktoday.views.maidprofile.MaidProfileFragment;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by cbl81 on 28/10/17.
 */

public class PastBookingFragment extends Fragment implements PastBookingContract.View, CancelBookingInterface, OpenMaid, ExtensionUpdate {

    private static final String TAG = "PastBookingFragment";
    private final int LIMIT = 10;
    private FragmentPastBinding binding;
    private PastBookingContract.Presenter presenter;
    private List<PojoMyBooking.Datum> bookingList = new ArrayList<>();
    private BookingAdapter bookingAdapter;
    private int page = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPastBinding.inflate(inflater, container, false);
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
        presenter = new PastBookingPresenter();
        presenter.attachView(this);
    }

    private void setData() {
        bookingAdapter = new BookingAdapter(getActivity(), "Past", bookingList, PastBookingFragment.this, PastBookingFragment.this);
        binding.rvPast.setAdapter(bookingAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        binding.rvPast.setLayoutManager(layoutManager);
        EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener = new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                page = currentPage;
                getDataList();
            }
        };
        endlessRecyclerOnScrollListener.setVisibleThreshold(LIMIT);
        binding.rvPast.addOnScrollListener(endlessRecyclerOnScrollListener);

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
                hashMap.put("onBasisOfDate", "COMPLETED");
                hashMap.put("timeZone", TimeZone.getDefault().getID());
                hashMap.put("pageNo", String.valueOf(page));
                hashMap.put("limit", String.valueOf(LIMIT));

                presenter.apiPastBooking(hashMap);
            }
        } else {
            binding.noDataView.setData(getString(R.string.cant_connect));
            binding.noDataView.setImage(ContextCompat.getDrawable(getContext(), R.drawable.no_internet));
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
    public void pastBookingSuccess(ApiResponse<PojoMyBooking> data) {
        try {
            if (page == 1) {
                bookingList.clear();
            }
            bookingList.addAll(data.getData().data);
            bookingAdapter.notifyDataSetChanged();

            if (bookingAdapter.getItemCount() == 0) {
                binding.noDataView.setData(getString(R.string.no_booking_found));
//            binding.noDataView.setImage(ContextCompat.getDrawable(getContext(), R.drawable.ic_no_data));
                binding.viewFlipper.setDisplayedChild(1);
            } else {
                binding.viewFlipper.setDisplayedChild(2);
            }
            binding.swipeRefresh.setRefreshing(false);
        }catch (Exception e){
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(e);
        }

    }

    @Override
    public void error(String errorMessage) {
        new DialogPopup().alertPopup(getActivity(), getResources().getString(R.string.dialog_alert), errorMessage, "").show(requireActivity().getSupportFragmentManager(), "ios_dialog");
        binding.swipeRefresh.setRefreshing(false);
    }

    @Override
    public void failure(String failureMessage) {
      Log.e(TAG, "failure: "+failureMessage);
        new DialogPopup().alertPopup(getActivity(), getResources().getString(R.string.dialog_alert), getString(R.string.check_connection), "").show(requireActivity().getSupportFragmentManager(), "ios_dialog");
        binding.swipeRefresh.setRefreshing(false);
    }


    @Override
    public void showCancelDialog(String bookingId, String timeZone) {

    }

    @Override
    public void showExtendDialog(String payment_mode, String referenceId, String serviceMakId, Float actualPrice, String currency, String vat) {
        FragmentManager fragmentManager = getChildFragmentManager();
        fragmentManager.beginTransaction().addToBackStack(null);
        ExtendServiceDialog.newInstance(this, payment_mode, referenceId, serviceMakId, actualPrice, currency, vat).show(fragmentManager, "ExtendServiceDialog");

    }

    @Override
    public void openMaidProfile(MaidData maidData) {

        MaidProfileFragment maidProfileFragment = MaidProfileFragment.newInstance(null, maidData, null, true, "", "");
        getActivity().getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                .add(android.R.id.content, maidProfileFragment, "MaidProfileFragment").addToBackStack("MaidProfileFragment").commit();

    }

    @Override
    public void extensionUpdate() {

    }
}
