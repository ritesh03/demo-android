package com.maktoday.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.maktoday.R;
import com.maktoday.utils.dialog.IOSAlertDialog;
import com.maktoday.views.authenticate.AuthenticateActivity;

public class DialogPopup {
    private static final String TAG = "DialogPopup";
    public Dialog alertPopupOld(final Activity activity, String title, String message, final String customMessage) {

        Dialog dialog = null;

        try {

            dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
            dialog.setContentView(R.layout.dialog_popup);
            WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
            lp.dimAmount = 0.6f;
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            TextView header = dialog.findViewById(R.id.header);
            TextView text = dialog.findViewById(R.id.text);
            Button ok = dialog.findViewById(R.id.ok);
            ok.setText(activity.getString(R.string.ok));
            Button cancel = dialog.findViewById(R.id.cancel);
            cancel.setText(activity.getString(R.string.cancel1));
            text.setText(message);

            if ("Location".equalsIgnoreCase(customMessage)) {
                cancel.setVisibility(View.VISIBLE);
            } else {
                cancel.setVisibility(View.GONE);
            }

            if (customMessage.equals("others")) {
                header.setVisibility(View.VISIBLE);

                header.setText(activity.getResources().getString(R.string.alert_message));
            } else
                header.setVisibility(View.GONE);
            final Dialog finalDialog = dialog;
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finalDialog.dismiss();
                }
            });

            ok.setOnClickListener(v -> {
                if (customMessage.equalsIgnoreCase("Location")) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    activity.startActivity(intent);
                } else if ("Logout".equalsIgnoreCase(customMessage)) {
                    String language = Prefs.with(activity).getString(Constants.LANGUAGE_CODE, "en");
                    String device_token = Prefs.with(activity).getString(Constants.DEVICE_TOKEN, "");
                    Prefs.with(activity).removeAll();
                    Prefs.with(activity).save(Constants.LANGUAGE_CODE, language);
                    //  Prefs.with(this).save(Constants.DEVICE_TOKEN, device_token);
                    activity.finishAffinity();
                    if (AuthenticateActivity.mGoogleSignInClient != null) {
                        AuthenticateActivity.mGoogleSignInClient.signOut()
                                .addOnCompleteListener(activity, new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                    }
                                });
                    }
                    Prefs.with(activity).save(Constants.LANGUAGE_Click_Status, "yes");
                    activity. startActivity(new Intent(activity, AuthenticateActivity.class));
                } else if ("PaymentError".equalsIgnoreCase(customMessage)) {
                    activity.finish();
                } else if ("MAID_NOT_AVAILABLE".equalsIgnoreCase(customMessage)) {
                    activity.finish();
                } else {

                }
                if (message.equalsIgnoreCase(activity.getString(R.string.continue_with_your_booking))){
                    //activity.finish();
                }else if (message.equalsIgnoreCase(activity.getString(R.string.at_least_two_hours_after_current))){
                    activity.finish(); 
                }

                finalDialog.dismiss();
            });
            dialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return dialog;
    }


    public IOSAlertDialog alertPopup(final Activity activity, String title, String message, final String customMessage) {


        IOSAlertDialog dialog =  IOSAlertDialog.newInstance(
               activity,

                /** title -------*/
                (customMessage.equals("others"))?
                        activity.getResources().getString(R.string.alert_message): null,
             /** message -------*/

               message,
            /** negative  button ------------ text  */
            ("Location".equalsIgnoreCase(customMessage))?
                 activity.getResources().getString(R.string.cancel1) :null,

             /**  positive button text ------------------*/
                activity.getResources().getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Positive button click logic
                        if (customMessage.equalsIgnoreCase("Location")) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            activity.startActivity(intent);
                        } else if ("Logout".equalsIgnoreCase(customMessage)) {
                            String language = Prefs.with(activity).getString(Constants.LANGUAGE_CODE, "en");
                            String device_token = Prefs.with(activity).getString(Constants.DEVICE_TOKEN, "");
                            Prefs.with(activity).removeAll();
                            Prefs.with(activity).save(Constants.LANGUAGE_CODE, language);
                            //  Prefs.with(this).save(Constants.DEVICE_TOKEN, device_token);
                            activity.finishAffinity();
                            if (AuthenticateActivity.mGoogleSignInClient != null) {
                                AuthenticateActivity.mGoogleSignInClient.signOut()
                                        .addOnCompleteListener(activity, new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                            }
                                        });
                            }
                            Prefs.with(activity).save(Constants.LANGUAGE_Click_Status, "yes");
                            activity. startActivity(new Intent(activity, AuthenticateActivity.class));
                        } else if ("PaymentError".equalsIgnoreCase(customMessage)) {
                            activity.finish();
                        } else if ("MAID_NOT_AVAILABLE".equalsIgnoreCase(customMessage)) {
                            activity.finish();
                        } else {

                        }
                        if (message.equalsIgnoreCase(activity.getString(R.string.continue_with_your_booking))){
                            //activity.finish();
                        }else if (message.equalsIgnoreCase(activity.getString(R.string.at_least_two_hours_after_current))){
                            activity.finish();
                        }

                        dialog.dismiss();

                    }
                },
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Negative button click logic
                         dialog.dismiss();

                    }
                },

                ContextCompat.getColor(activity, R.color.app_color),  //negative color
                ContextCompat.getColor(activity, R.color.app_color),  //poitive color
                false // cancelable
        );




       return  dialog;

    }


}
