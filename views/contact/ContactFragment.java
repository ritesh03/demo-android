package com.maktoday.views.contact;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hbb20.CountryCodePicker;
import com.maktoday.R;
import com.maktoday.model.ContactusResponse;
import com.maktoday.utils.Constants;
import com.maktoday.utils.DataVariable;
import com.maktoday.utils.GeneralFunction;
import com.maktoday.utils.Log;
import com.maktoday.utils.Prefs;
import com.maktoday.views.home.HomeFragment;
import com.maktoday.views.main.Main2Activity;
import com.maktoday.webservices.RestClient;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.maktoday.utils.Constants.UnAuthorized;

public class ContactFragment extends Fragment {
    private static final String TAG = "ContactFragment";
    private static String url = "https://mak.today/MAK/AgencyPanel/#/contactUs/";

    RelativeLayout parent_view;
    CountryCodePicker ccp;
    TextView tv_text_para, btn_submit;
    EditText name, email, phone, comment;
    String country_code;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_dummy, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
       Log.d(TAG, "onViewCreated: StartActivity");
        init(view);
        if (HomeFragment.noticount>0){
            Main2Activity.redCircle.setVisibility(View.VISIBLE);
            Main2Activity.countTextView.setText(String.valueOf(HomeFragment.noticount));
        }
    }

    private void init(View view) {
        tv_text_para = view.findViewById(R.id.tv_text_para);
        name = view.findViewById(R.id.name);
        ccp = view.findViewById(R.id.ccp);
        parent_view = view.findViewById(R.id.parent_view);
        email = view.findViewById(R.id.email);
        phone = view.findViewById(R.id.phone);
        comment = view.findViewById(R.id.comment);
        btn_submit = view.findViewById(R.id.btn_submit);


        String first = getString(R.string.please_contact_our_customer);
        String second ="";
        String phone1 = "";
        String phone2 = "";
        String emailContact = "";
        String three ="";
        if (Prefs.with(getActivity()).getString(Constants.LOGIN_COUNTRY,"").equalsIgnoreCase("BH")){
            second = getString(R.string.who_are_avilable);
            phone1 = getString(R.string.contact_us_phone);
            phone2 = getString(R.string.contact_us_phone_two);
            three = getString(R.string.we_can_also_be);
            tv_text_para.setText(Html.fromHtml(first +" "+phone1 +" "+second +" "+ phone2 +three+" "+emailContact));
        }else {
            second = getString(R.string.who_are_avilable_uk);
            phone1 = getString(R.string.contact_us_phone_uk);
            three = getString(R.string.we_can_also_be_uk);
            emailContact = getString(R.string.contact_us_email_uk);
            tv_text_para.setText(Html.fromHtml(first +" "+phone1 +" "+second +three));
            android.util.Log.d(TAG, "init: contactUsUk:-- "+first +" "+phone1 +" "+second +three+" "+emailContact );
        }



     /*   country_code=ccp.getDefaultCountryCodeWithPlus();
        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                country_code=ccp.getSelectedCountryCodeWithPlus();
            }
        });*/

//        Log.e("country code",country_code);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             if (Validation()){
                 GeneralFunction.showProgress(getActivity());
                 Log.e("country code",ccp.getSelectedCountryCodeWithPlus());
                 RestClient.getModalApiService().contactUs(Prefs.get().getString(Constants.ACCESS_TOKEN, ""), Constants.UNIQUE_APP_KEY, name.getText().toString(), "USER", "+" + ccp.getSelectedCountryCode() + "" + phone.getText().toString(), comment.getText().toString(),
                         email.getText().toString(),ccp.getSelectedCountryCodeWithPlus(),Prefs.get().getString(Constants.USER_IDs,""))
                         .enqueue(new Callback<ContactusResponse>() {
                             @Override
                             public void onResponse(Call<ContactusResponse> call, Response<ContactusResponse> response) {
                                 GeneralFunction.dismissProgress();

                                 Log.e("res", new Gson().toJson(response.body().statusCode));
                                 // Toast.makeText(getActivity(), new Gson().toJson(response.body()), Toast.LENGTH_SHORT).show();
                                 if (response.isSuccessful()) {
                                     //view.createPaymentSuccess(response.body().data);
                                     if (response.body().statusCode == 201) {
                                         name.setText("");
                                         email.setText("");
                                         comment.setText("");
                                         phone.setText("");
                                         Toast.makeText(getActivity(), getActivity().getString(R.string.submit_success), Toast.LENGTH_SHORT).show();
                                     } else {
                                         GeneralFunction.showSnackBar(getActivity(), parent_view, response.body().message);

                                     }
                                 } else {
                                     if (response.code() == UnAuthorized) {
                                         GeneralFunction.isUserBlocked(getActivity());
                                     } else {
                                         GeneralFunction.showSnackBar(getActivity(), parent_view, response.message());
                                         // view.error(new JSONObject(response.errorBody().string()).getString("message"));
                                     }
                                 }
                             }

                             @Override
                             public void onFailure(Call<ContactusResponse> call, Throwable t) {
                                 if (view != null) {
                                     GeneralFunction.dismissProgress();
                                     GeneralFunction.showSnackBar(getActivity(), parent_view, t.getMessage());
                                 }
                             }

                         });
             }
            }
        });

        parent_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataVariable.hideSoftKeyboard(getActivity());
            }
        });
    }

    private boolean Validation() {
        if (name.getText().toString().trim().isEmpty()){
            GeneralFunction.showSnackBar(getActivity(),name, getString(R.string.fullname_empty_validation));
            name.requestFocus();
            return false;
        }else if (email.getText().toString().trim().isEmpty()){
            GeneralFunction.showSnackBar(getActivity(),email, getString(R.string.email_empty_validation));
            email.requestFocus();
            return false;
        }else if (!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
            GeneralFunction.showSnackBar(getActivity(),email, getString(R.string.email_valid_validation));
            email.requestFocus();
            return false;
        }else if (phone.getText().toString().trim().isEmpty()){
            GeneralFunction.showSnackBar(getActivity(),phone, getString(R.string.please_enter_contact_number));
            phone.requestFocus();
            return false;
        }else if (comment.getText().toString().trim().isEmpty()){
            GeneralFunction.showSnackBar(getActivity(),comment,getString(R.string.please_enter_comments));
            comment.requestFocus();
            return false;
        }

        return true;
    }

    private boolean validation(EditText editText) {
        String data = editText.getText().toString().trim();
        if (data.isEmpty()) {
            editText.setError(getString(R.string.please_enter_contact_number));
            editText.requestFocus();
            return false;
        }
        return true;
    }

    private boolean validationEmail(EditText editText) {
        String email = editText.getText().toString().trim();
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        Matcher matcher = pattern.matcher(email);
        if (matcher.matches()) {
            return true;
        }

        editText.setError(getString(R.string.email_valid_validation));
        return matcher.matches();
    }

}
