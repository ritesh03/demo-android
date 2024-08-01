package com.maktoday.views.setting;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
//import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.maktoday.model.DeleteAccountResponse;
import com.maktoday.utils.DialogPopup;
import com.maktoday.utils.GeneralFunction;
import com.maktoday.utils.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.maktoday.R;
import com.maktoday.databinding.FragmentSettingBinding;
import com.maktoday.model.PojoLogin;
import com.maktoday.utils.Constants;
import com.maktoday.utils.Prefs;
import com.maktoday.utils.dialog.IOSAlertDialog;
import com.maktoday.views.authenticate.AuthenticateActivity;
import com.maktoday.views.changePassword.ChangePasswordActivity;
import com.maktoday.views.changelanguage.ChangeLanguageActivity;
import com.maktoday.views.home.HomeFragment;
import com.maktoday.views.main.Main2Activity;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import java.util.HashMap;

/**
 * Created by cbl81 on 27/10/17.
 */

public class SettingFragment extends Fragment implements View.OnClickListener,SettingContract.View{

    private static final String TAG = "SettingFragment";
    private FragmentSettingBinding binding;
   // private Dialog  deleteDialog;
  //  private AlertDialog dialog;
    private SettingPresenter presenter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding= FragmentSettingBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: StartActivity");
        intialization();
        setData();
        setListeners();
        if (HomeFragment.noticount>0){
            Main2Activity.redCircle.setVisibility(View.VISIBLE);
            Main2Activity.countTextView.setText(String.valueOf(HomeFragment.noticount));
        }

        if(Prefs.get().getString(Constants.LOGIN_COUNTRY,"").equalsIgnoreCase("BH"))
        {
            binding.view2.setVisibility(View.VISIBLE);
            binding.tvChangeLanguage.setVisibility(View.VISIBLE);
        } else {
            binding.view2.setVisibility(View.GONE);
            binding.tvChangeLanguage.setVisibility(View.GONE);
        }

        if(Prefs.with(getContext()).getObject(Constants.DATA,PojoLogin.class).faceBookLogin){
            binding.tvChangePassword.setVisibility(View.GONE);
            binding.view3.setVisibility(View.GONE);
            binding.tvChangeLanguage.setVisibility(View.VISIBLE);
            binding.view2.setVisibility(View.VISIBLE);

        }else {
            binding.tvChangePassword.setVisibility(View.VISIBLE);
            binding.view3.setVisibility(View.VISIBLE);
        }
    }

    private void intialization() {
        presenter = new SettingPresenter();
        presenter.attachView(this);
        PojoLogin loginData= Prefs.with(getContext()).getObject(Constants.DATA,PojoLogin.class);
        if (loginData == null)
            return;
        //if (loginData.isProfileComplete() && !loginData.faceBookLogin) {
            binding.tvChangePassword.setVisibility(View.VISIBLE);
            binding.view3.setVisibility(View.VISIBLE);
    //    }
    }

    private void setData() {

    }

    private void setListeners() {
        binding.tvChangePassword.setOnClickListener(this);
        binding.tvChangeLanguage.setOnClickListener(this);
        binding.tvDeleteAccount.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId())
        {
            case R.id.tvChangePassword:

                getActivity().startActivity(new Intent(getActivity(), ChangePasswordActivity.class));
                break;

                case R.id.tvChangeLanguage:

                getActivity().startActivity(new Intent(getActivity(), ChangeLanguageActivity.class));
                //getActivity().getSupportFragmentManager().beginTransaction().add(android.R.id.content,new ChangeLanguageFragment()).addToBackStack(null).commit();
                break;
            case R.id.tvDeleteAccount:

                 deleteAccountPopUP();
                break;
        }
    }


    public void deleteAccountPopUP() {


        IOSAlertDialog iosAlertDialog =  IOSAlertDialog.newInstance(
          requireContext(),
          getString(R.string.delete_account),
         getString(R.string.deleteaccount_sure),
                getString(R.string.cancel1),
                getString(R.string.delete_account),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int idd) {
                        dialog.cancel();
                    }
                },
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int idd) {
                        if (GeneralFunction.isNetworkConnected(getActivity(), getView())) {
                            HashMap<String ,String> map = new HashMap<>();
                            //{"uniquieAppKey":"892d9a5e3c21f60eb95ad196da1b0220","action":"DELETE"}
                            map.put("uniquieAppKey",Constants.UNIQUE_APP_KEY);
                            map.put("action","DELETE");
                            presenter.apiDeleteAccount(map);
                        }
                    }
                },
                ContextCompat.getColor(getActivity(), R.color.app_color),
                ContextCompat.getColor(getActivity(), R.color.coral),
                false,
                true
        );
        iosAlertDialog.show(requireActivity().getSupportFragmentManager(), "ios_dialog");
  /*      dialog = new AlertDialog.Builder(getActivity())
                .setMessage(getResources().getString(R.string.deleteaccount_sure))
                .setCancelable(false)
                .setNegativeButton(R.string.cancel1, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int idd) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton(R.string.delete_account,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int idd) {
                                if (GeneralFunction.isNetworkConnected(getActivity(), getView())) {
                                    HashMap<String ,String> map = new HashMap<>();
                                    //{"uniquieAppKey":"892d9a5e3c21f60eb95ad196da1b0220","action":"DELETE"}
                                    map.put("uniquieAppKey",Constants.UNIQUE_APP_KEY);
                                    map.put("action","DELETE");
                                    presenter.apiDeleteAccount(map);
                                }
                            }
                        }).show();

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getActivity(), R.color.coral));
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setAllCaps(false);
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setAllCaps(false);
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getActivity(), R.color.appColor));
*/
    }
    @Override
    public void setLoading(boolean isLoading) {

    }

    @Override
    public void successDeleteAccount(DeleteAccountResponse response) {


        String language = Prefs.with(requireActivity()).getString(Constants.LANGUAGE_CODE, "en");
        String device_token = Prefs.with(requireActivity()).getString(Constants.DEVICE_TOKEN, "");
        Prefs.with(requireActivity()).removeAll();
        Prefs.with(requireActivity()).save(Constants.LANGUAGE_CODE, language);
        //  ShortcutBadger.applyCount(this, 0);
        //  Prefs.with(this).save(Constants.DEVICE_TOKEN, device_token);
        requireActivity().finishAffinity();
        if (AuthenticateActivity.mGoogleSignInClient != null) {
            AuthenticateActivity.mGoogleSignInClient.signOut()
                    .addOnCompleteListener(requireActivity(), new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    });
        }
        Prefs.with(requireActivity()).save(Constants.LANGUAGE_Click_Status, "yes");
        startActivity(new Intent(requireActivity(), AuthenticateActivity.class));

    }

    @Override
    public void error(String failureMessage) {

        new DialogPopup().alertPopup(requireActivity(), getResources().getString(R.string.dialog_alert), failureMessage, "").show(requireActivity().getSupportFragmentManager(), "ios_dialog");
    }

    @Override
    public void sessionExpired() {
        GeneralFunction.isUserBlocked(requireActivity());
    }

    @Override
    public void failure(String failureMessage) {
        Log.e(TAG, "failure: "+failureMessage);
        new DialogPopup().alertPopup(requireActivity(), getResources().getString(R.string.dialog_alert), getString(R.string.check_connection), "").show(requireActivity().getSupportFragmentManager(), "ios_dialog");
    }
}
