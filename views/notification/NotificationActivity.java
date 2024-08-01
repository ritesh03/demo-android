package com.maktoday.views.notification;

import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;


import android.view.MenuItem;
import com.maktoday.utils.Log;
import com.maktoday.R;
import com.maktoday.adapter.NotificationAdapter;
import com.maktoday.databinding.ActivityNotificationBinding;
import com.maktoday.model.PojoNotification;
import com.maktoday.utils.BaseActivity;
import com.maktoday.utils.DialogPopup;
import com.maktoday.utils.GeneralFunction;
import com.maktoday.views.home.HomeFragment;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends BaseActivity implements NotificationContract.View {
    private static final String TAG = "NotificationActivity";
    private ActivityNotificationBinding binding;
    private NotificationContract.Presenter presenter;
    private NotificationAdapter notificationAdapter;
    private List<PojoNotification.Data> notificationDataList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notification);
        Log.d(TAG, "onCreate: StartActivity");
        init();
        setData();
        setListeners();
    }

    private void init() {
        setSupportActionBar(binding.toolbar.toolbar);
    }

    private void setData() {
        presenter = new NotificationPresenter();
        presenter.attachView(this);

        binding.toolbar.title.setText(getResources().getString(R.string.notification));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_white);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        notificationAdapter = new NotificationAdapter(NotificationActivity.this, notificationDataList);
        binding.rvNotification.setAdapter(notificationAdapter);
        binding.rvNotification.setLayoutManager(new LinearLayoutManager(NotificationActivity.this));

        callNotificationApi();
    }

    private void setListeners() {

        binding.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                callNotificationApi();
            }
        });
    }

    private void callNotificationApi() {
        if (GeneralFunction.isNetworkConnected(this, binding.parent)) {
            binding.swipeRefresh.setRefreshing(true);
            presenter.apiNotification();
        } else {
            binding.noDataView.setData(getString(R.string.cant_connect));
            binding.noDataView.setImage(ContextCompat.getDrawable(this, R.drawable.no_internet));
            binding.viewFlipper.setDisplayedChild(1);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void setLoading(boolean isLoading) {
        binding.swipeRefresh.setRefreshing(isLoading);
    }

    @Override
    public void sessionExpired() {
        GeneralFunction.isUserBlocked(this);
    }

    @Override
    public void notificationSuccess(PojoNotification data) {

        if (data.data.size() != 0) {
            HomeFragment.noticount=0;
            notificationDataList.clear();
            notificationDataList.addAll(data.data);
            notificationAdapter.notifyDataSetChanged();
            binding.viewFlipper.setDisplayedChild(2);
        } else {
            binding.noDataView.setData(getString(R.string.no_notifications_found));
         //   binding.noDataView.setImage(ContextCompat.getDrawable(this, R.drawable.ic_no_data));
            binding.viewFlipper.setDisplayedChild(1);
        }
        binding.swipeRefresh.setRefreshing(false);

    }

    @Override
    public void notificationError(String errorMessage) {
        new DialogPopup().alertPopup(this, getResources().getString(R.string.dialog_alert), errorMessage, "").show(getSupportFragmentManager(), "ios_dialog");
        binding.swipeRefresh.setRefreshing(false);
    }

    @Override
    public void notificationFailure(String failureMessage) {
        Log.e(TAG, "notificationFailure: "+failureMessage);
        new DialogPopup().alertPopup(this, getResources().getString(R.string.dialog_alert), getString(R.string.check_connection), "").show(getSupportFragmentManager(), "ios_dialog");
        binding.swipeRefresh.setRefreshing(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }
}
