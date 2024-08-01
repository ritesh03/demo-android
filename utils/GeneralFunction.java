package com.maktoday.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.maktoday.R;
import com.maktoday.utils.dialog.IOSAlertDialog;
import com.maktoday.views.authenticate.AuthenticateActivity;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * Created by cbl81 on 8/11/17.
 */

public class GeneralFunction {

    private static Dialog dialog;

    public static void showSnackBar(Context context, View parentView, String msg) {
        final Snackbar snackbar = Snackbar.make(parentView, msg, Snackbar.LENGTH_LONG);
        snackbar.setAction(context.getString(R.string.OK), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        }).show();

        //set color of action button text
        snackbar.setActionTextColor(ContextCompat.getColor(context, R.color.skyBlue));

        //set color of snackbar text
        TextView view = snackbar.getView().findViewById(R.id.snackbar_text);
        view.setTextColor(ContextCompat.getColor(context, android.R.color.white));
    }

    public static String getFormatFromDate(Date date, String format) {

        Locale locale = Locale.ENGLISH;

        SimpleDateFormat sdf = new SimpleDateFormat(format, locale);
        try {
            return sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static void showProgress(Context activity) {

        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.dismiss();
                return;
            }
        }
        try {
            dialog = new Dialog(activity);
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.layout_progress);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void hideKeyboardFromActivity(Activity activity) {
        try {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            //Find the currently focused view, so we can grab the correct window token from it.
            View view = activity.getCurrentFocus();
            //If no view currently has focus, create a new one, just so we can grab a window token from it
            if (view == null) {
                view = new View(activity);
            }
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void hideKeyboardFromFragment(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void dismissProgress() {
        try {
            if (dialog != null) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void isUserBlocked(final Activity activity) {

        try {
            IOSAlertDialog dialog =  IOSAlertDialog.newInstance(
                    activity,
                    null,
                    activity.getString(R.string.token_expired),
                    activity.getString(R.string.ok),
                    null,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int idd) {
                            String language = Prefs.with(activity).getString(Constants.LANGUAGE_CODE, "en");
                            Prefs.with(activity).removeAll();
                            Prefs.with(activity).save(Constants.LANGUAGE_CODE, language);
                            Prefs.with(activity).save(Constants.LANGUAGE_Click_Status, "yes");
                            ShortcutBadger.applyCount(activity, 0);

                            Intent intent = new Intent(activity, AuthenticateActivity.class);
                            activity.finishAffinity();
                            activity.startActivity(intent);
                        }
                    },
                    null,
                    ContextCompat.getColor(activity, R.color.app_color),
                    0,
                    false
            );

            dialog.show(((AppCompatActivity) activity).getSupportFragmentManager(), "ios_dialog");


  /*        AlertDialog  dialog = new AlertDialog.Builder(activity)
                    .setMessage(activity.getString(R.string.token_expired))
                    .setCancelable(false)
                    .setPositiveButton(activity.getString(R.string.ok),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int idd) {
                                    String language = Prefs.with(activity).getString(Constants.LANGUAGE_CODE, "en");
                    Prefs.with(activity).removeAll();
                    Prefs.with(activity).save(Constants.LANGUAGE_CODE, language);
                    Prefs.with(activity).save(Constants.LANGUAGE_Click_Status, "yes");
                    ShortcutBadger.applyCount(activity, 0);

                    Intent intent = new Intent(activity, AuthenticateActivity.class);
                    activity.finishAffinity();
                    activity.startActivity(intent);
                                }
                            }).show();

            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(activity, R.color.appColor));
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setAllCaps(false);
*/

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static boolean isNetworkConnected(Activity context, View view) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm.getActiveNetworkInfo() != null) {
                return true;
            } else {
                if (view != null) {
                    showSnackBar(context, view, context.getString(R.string.check_connection));
                }
                return false;
            }
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }

    }

    @Nullable
    public static File createImageFile(@NonNull final String directory) throws IOException {
        File imageFile = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File storageDir = new File(directory);
            if (!storageDir.mkdirs()) {
                if (!storageDir.exists()) {
                    return null;
                }
            }
            String imageFileName = "IMG_" + System.currentTimeMillis() + "_";

            imageFile = File.createTempFile(imageFileName, ".jpg", storageDir);
        }
        return imageFile;
    }

    public static Date getTimeInTimeZone(Date date, TimeZone timeZone) {
        String newDate = getFormatFromDate(date, "yyyy/MM/dd HH:mm:ss");

        SimpleDateFormat inputParser = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
        inputParser.setTimeZone(timeZone);
        try {
            return inputParser.parse(newDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date(0);
        }
    }
}