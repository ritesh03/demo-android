package com.maktoday.views.cancelbooking;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.maktoday.utils.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.maktoday.R;
import com.maktoday.databinding.DialogCancelServiceBinding;
import com.maktoday.model.ApiResponse;
import com.maktoday.model.CancelServiceModel;
import com.maktoday.utils.Constants;
import com.maktoday.utils.GeneralFunction;
import com.stripe.android.paymentsheet.PaymentSheetResult;

/**
 * Created by cbl81 on 3/12/17.
 */

public class CancelBookingDialog extends DialogFragment implements View.OnClickListener, CancelBookingContract.View {

    private static final String TAG = "CancelBookingDialog";
    private DialogCancelServiceBinding binding;
    private CancelBookingContract.Presenter presenter;

    public static CancelBookingDialog newInstance(String id, String timeZone) {
        Bundle args = new Bundle();
        args.putString("ServiceId", id);
        args.putString("timeZone", timeZone);
        CancelBookingDialog fragment = new CancelBookingDialog();
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
        binding = DialogCancelServiceBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: StartActivity");
        init(view);
        setListener();
    }

    private void init(View view) {
        presenter = new CancelBookingPresenter();
        presenter.attachView(this);
    }

    @Override
    public void onDismiss(final DialogInterface dialog) {
        super.onDismiss(dialog);
        Fragment parentFragment = getParentFragment();
        if (parentFragment instanceof DialogInterface) {
            ((DialogInterface) parentFragment).dismiss();
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }

    private void setListener() {
        binding.ivCross.setOnClickListener(this);
        binding.tvConfirm.setOnClickListener(this);

        binding.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int checkedId) {
                switch (checkedId) {

                    case R.id.rbOther:
                        binding.etReason.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tvConfirm) {
            if (GeneralFunction.isNetworkConnected(getActivity(), getView())) {
                callCancelBookingApi();
            } else {
                binding.tvError.setText(R.string.check_connection);
                binding.tvError.setVisibility(View.VISIBLE);
            }

        } else if (view.getId() == R.id.ivCross) {
            dismiss();
        }
    }

    private void callCancelBookingApi() {
        String reason = "";
        int selectedId = binding.radioGroup.getCheckedRadioButtonId();

        if (selectedId == -1) {
            binding.tvError.setText(R.string.select_reason_validation);
            binding.tvError.setVisibility(View.VISIBLE);
            return;
        }
        switch (selectedId) {
            case R.id.rbTimeDate:
                reason = binding.rbTimeDate.getText().toString();
                break;
            case R.id.rbAlternative:
                reason = binding.rbAlternative.getText().toString();
                break;
            case R.id.rbOther:
                if (binding.etReason.getText().toString().trim().isEmpty()) {
                    binding.tvError.setText(R.string.mention_reason_validation);
                    binding.tvError.setVisibility(View.VISIBLE);
                    binding.etReason.requestFocus();
                    return;
                }

                reason = binding.etReason.getText().toString().trim();
                break;
        }

        CancelServiceModel cancelServiceModel = new CancelServiceModel();
        cancelServiceModel.uniquieAppKey = Constants.UNIQUE_APP_KEY;
        cancelServiceModel.serviceId = getArguments().getString("ServiceId");
        cancelServiceModel.deleteReason = reason;
        cancelServiceModel.timeZone = getArguments().getString("timeZone");
      //  binding.progressBar.setVisibility(View.VISIBLE);
        presenter.apiCancelBooking(cancelServiceModel);
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
        GeneralFunction.isUserBlocked(getActivity());
    }

    @Override
    public void cancelBookingSuccess(ApiResponse data) {
        binding.tvConfirm.setVisibility(View.VISIBLE);
        binding.progressBar.setVisibility(View.GONE);
        dismiss();
        GeneralFunction.showSnackBar(getActivity(), getActivity().findViewById(android.R.id.content), getString(R.string.service_cancelled));

    }

    @Override
    public void cancelBookingError(String errorMessage) {
        binding.tvError.setText(String.format("%s %s", getString(R.string.error), errorMessage));
        binding.tvError.setVisibility(View.VISIBLE);
        binding.tvConfirm.setVisibility(View.VISIBLE);
        binding.progressBar.setVisibility(View.GONE);
    }

    @Override
    public void cancelBookingFailure(String failureMessage) {
        Log.e(TAG, "cancelBookingFailure: "+failureMessage);
        binding.tvError.setText(String.format("%s %s", getString(R.string.error), getString(R.string.check_connection)));
        binding.tvError.setVisibility(View.VISIBLE);
        binding.tvConfirm.setVisibility(View.VISIBLE);
        binding.progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

}
