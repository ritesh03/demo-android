package com.maktoday.views.payment;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;

import com.maktoday.utils.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.maktoday.R;
import com.maktoday.databinding.FragmentPaymentStateBinding;
import com.maktoday.interfaces.UpdateProgressStatus;
import com.maktoday.model.PojoLogin;
import com.maktoday.model.SearchMaidBulkModel;
import com.maktoday.model.SearchMaidModel;
import com.maktoday.utils.Constants;
import com.maktoday.utils.Prefs;
import com.maktoday.views.bookagain.BookAgainActivity;
import com.maktoday.views.login.LoginFragment;
import com.maktoday.views.maidbook.MaidBookActivity;
import com.maktoday.views.signupstep2.SignUp2Fragment;

import static com.maktoday.views.home.HomeFragment.booking_type;

/**
 * Created by cbl81 on 26/10/17.
 */

public class PaymentStateFragment extends Fragment implements UpdateProgressStatus {

    private static final String TAG = "PaymentStateFragment";
    private FragmentPaymentStateBinding binding;
    private SearchMaidModel searchMaidModel;
    private SearchMaidBulkModel searchMaidBulkModel;

    private int currentPosition = 0;

    public static PaymentStateFragment newInstance(SearchMaidModel searchMaidModel) {

        Bundle args = new Bundle();
        PaymentStateFragment fragment = new PaymentStateFragment();
        args.putParcelable(Constants.SEARCH_MAID_DATA, searchMaidModel);
        fragment.setArguments(args);
        return fragment;
    }

    public static PaymentStateFragment newInstance(SearchMaidBulkModel searchMaidModel) {

        Bundle args = new Bundle();
        PaymentStateFragment fragment = new PaymentStateFragment();
        args.putParcelable(Constants.SEARCH_MAID_DATA, searchMaidModel);
        fragment.setArguments(args);
        return fragment;
    }

    public static Fragment newInstance(SearchMaidModel searchMaidModel, String serviceId) {
        Bundle args = new Bundle();
        PaymentStateFragment fragment = new PaymentStateFragment();
        args.putParcelable(Constants.SEARCH_MAID_DATA, searchMaidModel);
        args.putString(Constants.SERVICE_ID,serviceId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (booking_type == 3) {
            searchMaidBulkModel = getArguments().getParcelable(Constants.SEARCH_MAID_DATA);
        } else {
            searchMaidModel = getArguments().getParcelable(Constants.SEARCH_MAID_DATA);

        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPaymentStateBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: StartActivity");
        setHasOptionsMenu(true);
        init();
        if (booking_type == 3) {
            setDataBulk();
        } else {
            setData();
        }

        setListeners();
    }

    private void init() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        binding.stateProgressBar.setCurrentPosition(currentPosition % 5);

        activity.setSupportActionBar(binding.toolbar);
        //binding.title.setText(R.string.payment);
        binding.title.setText("");

        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
            activity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_white);
        }
    }

    private void setData() {
        String token = Prefs.with(getActivity()).getString(Constants.ACCESS_TOKEN, "");
        PojoLogin data = Prefs.with(getActivity()).getObject(Constants.DATA, PojoLogin.class);
        if (data.isGuestFlag()) {
            getChildFragmentManager().beginTransaction().
                    add(R.id.childframeLayout, LoginFragment.newInstance("Payment",
                            false, searchMaidModel), "LoginFragment").commit();
            binding.stateProgressBar.setCurrentPosition(currentPosition % 5);
        } else {
            final String serviceId = getArguments().getString(Constants.SERVICE_ID);
            if (serviceId!=null){
                ((BookAgainActivity) getActivity()).step = 1;
                currentPosition = 1;
                binding.stateProgressBar.setCurrentPosition(currentPosition % 5);
                getChildFragmentManager().beginTransaction().
                        add(R.id.childframeLayout, SignUp2Fragment.newInstance("Payment", searchMaidModel,serviceId), "SignUp2Fragment").commit();
            }else {
                ((MaidBookActivity) getActivity()).step = 1;
                currentPosition = 1;
                binding.stateProgressBar.setCurrentPosition(currentPosition % 5);
                getChildFragmentManager().beginTransaction().
                        add(R.id.childframeLayout, SignUp2Fragment.newInstance("Payment", searchMaidModel), "SignUp2Fragment").commit();
            }
        }
    }

    private void setDataBulk() {
        String token = Prefs.with(getActivity()).getString(Constants.ACCESS_TOKEN, "");
        PojoLogin data = Prefs.with(getActivity()).getObject(Constants.DATA, PojoLogin.class);
        if (data.isGuestFlag()) {
            getChildFragmentManager().beginTransaction().
                    add(R.id.childframeLayout, LoginFragment.newInstance("Payment",
                            false, searchMaidBulkModel), "LoginFragment").commit();
            binding.stateProgressBar.setCurrentPosition(currentPosition % 5);
        } else {
            ((MaidBookActivity) getActivity()).step = 1;

            currentPosition = 1;
            binding.stateProgressBar.setCurrentPosition(currentPosition % 5);
            getChildFragmentManager().beginTransaction().
                    add(R.id.childframeLayout, SignUp2Fragment.newInstance("Payment", searchMaidBulkModel), "SignUp2Fragment").commit();
        }
    }

    private void setListeners() {

    }

    @Override
    public void changeProgressStatus(int position) {
        if (binding == null || binding.stateProgressBar == null)
            return;
        binding.stateProgressBar.setCurrentPosition(position % 5);
    }

    @Override
    public int getProgressBarPosition() {
        return binding.stateProgressBar.getCurrentPosition();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            getActivity().onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
