package com.maktoday.views.ratingdialog;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.maktoday.R;
import com.maktoday.databinding.DialogRatingBinding;
import com.maktoday.model.ApiResponse;
import com.maktoday.model.MaidRating;
import com.maktoday.model.PojoAddReview;
import com.maktoday.utils.Constants;
import com.maktoday.utils.GeneralFunction;
import com.maktoday.utils.Log;
import com.stripe.android.paymentsheet.PaymentSheetResult;

/**
 * Created by cbl81 on 4/12/17.
 */

public class RatingDialogFragment extends DialogFragment implements RatingContract.View, View.OnClickListener {
    private DialogRatingBinding binding;
    private RatingContract.Presenter presenter;
    private static final String TAG = "RatingDialogFragment";

    public static RatingDialogFragment newInstance(String requestId, String name, String image) {

        Bundle args = new Bundle();
        args.putString("id", requestId);
        args.putString("name", name);
        args.putString("image", image);

        RatingDialogFragment fragment = new RatingDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.full_screen);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogRatingBinding.inflate(inflater, container, false);
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
        presenter = new RatingPresenter();
        presenter.attachView(this);
    }

    private void setData() {
        Log.e("rating screen image", getArguments().getString("image", ""));
        Log.e("rating screen name", getArguments().getString("name"));
        if (!getArguments().getString("image", "").isEmpty()) {
            Glide.with(getActivity())
                    .load(getArguments().getString("image"))
                    .circleCrop()
                    .into(binding.ivImage);
        }
        binding.tvName.setText(getArguments().getString("name"));
    }

    private void setListeners() {
        binding.tvConfirm.setOnClickListener(this);
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
    public void ratingSuccess(ApiResponse data) {
        GeneralFunction.showSnackBar(getActivity(), getActivity().findViewById(android.R.id.content), getString(R.string.review_submitted));
        dismiss();
    }

    @Override
    public void ratingError(String errorMessage) {
        binding.tvError.setText(String.format("%s %s", getString(R.string.error), errorMessage));
        binding.tvError.setVisibility(View.VISIBLE);
    }

    @Override
    public void ratingFailure(String failureMessage) {
     Log.e(TAG, "ratingFailure: "+failureMessage);
        binding.tvError.setText(String.format("%s %s", getString(R.string.error), getString(R.string.check_connection)));
        binding.tvError.setVisibility(View.VISIBLE);
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvConfirm:
                if (GeneralFunction.isNetworkConnected(getActivity(), getActivity().findViewById(android.R.id.content))) {
                    MaidRating maidRating = new MaidRating();

                    if (binding.stars.rbCleaning.getRating() != 0.0) {
                        maidRating.cleaning = binding.stars.rbCleaning.getRating();
                    }
                    if (binding.stars.rbIroning.getRating() != 0.0) {
                        maidRating.ironing = binding.stars.rbIroning.getRating();
                    }
                    if (binding.stars.rbWashing.getRating() != 0.0) {
                        maidRating.cooking = binding.stars.rbWashing.getRating();
                    }
                    if (binding.stars.rbBabyCare.getRating() != 0.0) {
                        maidRating.childCare = binding.stars.rbBabyCare.getRating();
                    }

                    float totalRating = (maidRating.cleaning + maidRating.ironing + maidRating.cooking + maidRating.childCare) / 4;

                    if (totalRating == 0) {
                        Toast.makeText(getActivity(), R.string.please_enter_the_rating, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String reason = binding.etReason.getText().toString().trim();
                    if (reason.isEmpty()) {
                        reason = null;
                    }

                    PojoAddReview addReview = new PojoAddReview(Constants.UNIQUE_APP_KEY, getArguments().getString("id"),
                            maidRating, reason);


                    presenter.apiRating(addReview);
                } else {
                    binding.tvError.setText(R.string.check_connection);
                    binding.tvError.setVisibility(View.VISIBLE);
                }
                break;
        }
    }
}
