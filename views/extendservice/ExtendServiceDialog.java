package com.maktoday.views.extendservice;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.maktoday.R;
import com.maktoday.adapter.TimeDurationAdapter;
import com.maktoday.databinding.DialogExtendServiceBinding;
import com.maktoday.interfaces.ExtensionUpdate;
import com.maktoday.interfaces.TimeSelection;
import com.maktoday.model.ApiResponse;
import com.maktoday.utils.Constants;
import com.maktoday.utils.GeneralFunction;
import com.maktoday.utils.Log;
import com.maktoday.views.extendpayment.ExtendPaymentActivity;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import java.util.HashMap;

/**
 * Created by cbl81 on 2/12/17.
 */

public class ExtendServiceDialog extends DialogFragment implements View.OnClickListener, TimeSelection, ExtendServiceContract.View {
    private static final String TAG = "ExtendServiceDialog";
    private static int REQ_CODE = 100;
    private DialogExtendServiceBinding binding;
    private int duration;
    private ExtendServiceContract.Presenter presenter;
    public static ExtensionUpdate extensionUpdatee;

    public static ExtendServiceDialog newInstance(ExtensionUpdate extensionUpdate, String payment_mode, String referenceId, String serviceId, Float actualPrice, String currency, String vat) {
        extensionUpdatee = extensionUpdate;
        Bundle args = new Bundle();
        args.putString(Constants.SERVICE_ID, serviceId);
        args.putString("referenceId", referenceId);
        args.putString("payment_mode", payment_mode);
        args.putFloat(Constants.PRICE, actualPrice);
        args.putString(Constants.CURRENCY, currency);
        args.putString(Constants.VAT,vat);
        ExtendServiceDialog fragment = new ExtendServiceDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.full_screen_dialog);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogExtendServiceBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: StartActivity");
        init();
        setData();
        setListeners();
    }

    private void init() {
        presenter = new ExtendServicePresenter();
        presenter.attachView(this);
    }

    private void setData() {
        binding.rvTime.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvTime.setAdapter(new TimeDurationAdapter(getActivity(), this, true));
    }


    @Override
    public void onDismiss(final DialogInterface dialog) {
        super.onDismiss(dialog);
        Fragment parentFragment = getParentFragment();
        if (parentFragment instanceof DialogInterface) {
            ((DialogInterface) parentFragment).dismiss();
        }
    }

    private void setListeners() {
        binding.ivCross.setOnClickListener(this);
        binding.tvConfirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvConfirm:
                if (GeneralFunction.isNetworkConnected(getActivity(), getView())) {
                    callExtendServiceApi();
                } else {
                    binding.tvError.setText(R.string.check_connection);
                    binding.tvError.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.ivCross:
                getDialog().onBackPressed();
                break;
        }
    }

    private void callExtendServiceApi() {
        if (binding.etReason.getText().toString().trim().isEmpty()) {
            binding.tvError.setText(R.string.enter_reason_validation);
            binding.tvError.setVisibility(View.VISIBLE);
            binding.etReason.requestFocus();
            return;
        }

        HashMap<String, String> map = new HashMap<>();
        map.put("uniquieAppKey", Constants.UNIQUE_APP_KEY);
        map.put("serviceId", getArguments().getString(Constants.SERVICE_ID));
        map.put("duration", String.valueOf(duration));
        map.put("reason", binding.etReason.getText().toString().trim());

        Log.e("extend params",new Gson().toJson(map));
        presenter.apiExtendService(map);
    }

    @Override
    public void setSelectedDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public void setLoading(boolean isLoading) {
        if (isLoading) {
            binding.tvConfirm.setVisibility(View.GONE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.tvConfirm.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void sessionExpired() {
        GeneralFunction.isNetworkConnected(getActivity(), getView());
    }

    @Override
    public void extendServiceSuccess(ApiResponse data) {
        Log.e(" --------- extendServiceSuccess res", new Gson().toJson(data));

        Intent intent = new Intent(getActivity(), ExtendPaymentActivity.class);
        intent.putExtra(Constants.SERVICE_ID, getArguments().getString(Constants.SERVICE_ID));
        intent.putExtra("referenceId", getArguments().getString("referenceId"));
        intent.putExtra("payment_mode", getArguments().getString("payment_mode"));
        intent.putExtra("isExtension","true");
        intent.putExtra(Constants.PRICE, getArguments().getFloat(Constants.PRICE));
        intent.putExtra(Constants.DURATION, duration);
        intent.putExtra(Constants.CURRENCY, getArguments().getString(Constants.CURRENCY, ""));
        intent.putExtra(Constants.VAT,getArguments().getString(Constants.VAT,""));
        android.util.Log.d(TAG, "extendServiceSuccess: vatvalue:--  "+getArguments().getString(Constants.VAT,""));

        intent.putExtra(Constants.BOOK_AGAIN, false);
        startActivityForResult(intent, REQ_CODE);
    }

    @Override
    public void extendServiceError(String errorMessage) {
        Log.e("121", errorMessage);
        binding.tvError.setText(String.format("%s %s", getString(R.string.error), errorMessage));
        binding.tvError.setVisibility(View.VISIBLE);
    }

    @Override
    public void extendServiceFailure(String failureMessage) {
        Log.e("1233", failureMessage);
        binding.tvError.setText(String.format("%s %s", getString(R.string.error), failureMessage));
        binding.tvError.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.detachView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE) {
            android.util.Log.d(TAG, "onActivityResult: true");

            extensionUpdatee.extensionUpdate();
            dismiss();
        }
    }
}
