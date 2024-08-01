package com.maktoday.views.issue;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import com.maktoday.utils.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.maktoday.R;
import com.maktoday.databinding.FragmentIssueBinding;
import com.maktoday.model.ApiResponse;
import com.maktoday.utils.Constants;
import com.maktoday.utils.DataVariable;
import com.maktoday.utils.DialogPopup;
import com.maktoday.utils.GeneralFunction;
import com.maktoday.utils.Prefs;
import com.maktoday.views.home.HomeFragment;
import com.maktoday.views.main.Main2Activity;
import com.stripe.android.paymentsheet.PaymentSheetResult;

/**
 * Created by cbl81 on 27/10/17.
 */

public class IssueFragment extends Fragment implements IssueContract.View {
    private static final String TAG = "IssueFragment";
    private FragmentIssueBinding binding;
    private IssueContract.Presenter presenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentIssueBinding.inflate(inflater, container, false);
        //set variables in Binding
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: StartActivity");
        init();
        setListeners();
        if (HomeFragment.noticount>0){
            Main2Activity.redCircle.setVisibility(View.VISIBLE);
            Main2Activity.countTextView.setText(String.valueOf(HomeFragment.noticount));
        }
    }

    private void init() {
        presenter = new IssuePresenter();
        presenter.attachView(this);

        String first = getString(R.string.please_contact_our_customer);
        String second ="";
        String phone1 = "";
        String phone2 = "";
        String three ="";
        String emailContact = "";
        if (Prefs.with(getActivity()).getString(Constants.LOGIN_COUNTRY,"").equalsIgnoreCase("BH")){
            second = getString(R.string.who_are_avilable);
            phone1 = getString(R.string.contact_us_phone);
            phone2 = getString(R.string.contact_us_phone_two);
            three = getString(R.string.we_can_also_be);
            emailContact = getString(R.string.contact_us_email_bh);
          //  binding.tvTextPara.setText(Html.fromHtml(first +" "+phone1 +" "+second +" "+ phone2 + three+ " "+emailContact ));
            SpannableStringBuilder stringBuilder = new SpannableStringBuilder(first);
            stringBuilder.append(" ").append(phone1);
            String finalPhone1 = phone1;
            stringBuilder.setSpan(new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + finalPhone1));
                    startActivity(intent);
                }
            },stringBuilder.length() - phone1.length(),stringBuilder.length(),0);
            stringBuilder.append(" ").append(second).append(" ").append(phone2);
            String finalPhone2 = phone2;
            stringBuilder.setSpan(new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + finalPhone2));
                    startActivity(intent);
                }
            },stringBuilder.length()-phone2.length(),stringBuilder.length(),0);
            stringBuilder.append("\n").append(three).append(" ").append(emailContact);
            String [] finalEmailContact1 = {emailContact};
            stringBuilder.setSpan(new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    composeEmail(finalEmailContact1,"");
                }
            },stringBuilder.length()-emailContact.length(),stringBuilder.length(),0);
            binding.tvTextPara.setMovementMethod(LinkMovementMethod.getInstance());
            binding.tvTextPara.setText(stringBuilder, TextView.BufferType.SPANNABLE);
        }

        else {
            second = getString(R.string.who_are_avilable_uk);
            phone1 = getString(R.string.contact_us_phone_uk);
            three = getString(R.string.we_can_also_be_uk);
            emailContact = getString(R.string.contact_us_email_uk);
            //#0095bc
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(first);
            spannableStringBuilder.append(" ").append(phone1);
            spannableStringBuilder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.blue_click)),spannableStringBuilder.length() - phone1.length(),spannableStringBuilder.length(),0);
            String finalPhone = phone1;
            spannableStringBuilder.setSpan(new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + finalPhone));
                    startActivity(intent);
                }
            },spannableStringBuilder.length() - phone1.length(),spannableStringBuilder.length(),0);
            spannableStringBuilder.append(" ").append(second).append("\n").append(three);
            spannableStringBuilder.append(" ").append(emailContact);
            spannableStringBuilder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.blue_click)),spannableStringBuilder.length()-emailContact.length(),spannableStringBuilder.length(),0);
            String [] finalEmailContact = {emailContact};
            spannableStringBuilder.setSpan(new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    composeEmail(finalEmailContact,"");
                }
            },spannableStringBuilder.length()-emailContact.length(),spannableStringBuilder.length(),0);
            binding.tvTextPara.setMovementMethod(LinkMovementMethod.getInstance());
            binding.tvTextPara.setText(spannableStringBuilder, TextView.BufferType.SPANNABLE);

        }//else
    }//init

    public void composeEmail(String[] addresses, String subject) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }//composeEmail


    private void setListeners() {
        binding.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataVariable.hideSoftKeyboard(getActivity());
            }
        });
        binding.tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String issueString = binding.etData.getText().toString().trim();
                if (!issueString.isEmpty()) {
                    if (GeneralFunction.isNetworkConnected(getActivity(), binding.parent)) {
                        presenter.apiIssue(issueString);
                    }
                } else {
                    GeneralFunction.showSnackBar(getActivity(), binding.parent, getString(R.string.enter_issue));
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.detachView();
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
    }

    @Override
    public void issueSuccess(ApiResponse data) {
        binding.etData.setText("");
        //GeneralFunction.showSnackBar(getActivity(), getView(), getString(R.string.issue_submitted));
        Toast.makeText(getActivity(), getString(R.string.issue_submitted), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void issueError(String errorMessage) {
        new DialogPopup().alertPopup(getActivity(), getResources().getString(R.string.dialog_alert), errorMessage, "").show(requireActivity().getSupportFragmentManager(), "ios_dialog");;
    }

    @Override
    public void issueFailure(String failureMessage) {
        Log.e(TAG, "issueFailure: "+failureMessage);
        new DialogPopup().alertPopup(getActivity(), getResources().getString(R.string.dialog_alert), failureMessage, "").show(requireActivity().getSupportFragmentManager(), "ios_dialog");
    }
}