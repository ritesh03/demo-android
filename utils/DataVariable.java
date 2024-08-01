package com.maktoday.utils;

import android.app.Activity;
import android.view.inputmethod.InputMethodManager;

import com.google.gson.Gson;
import com.stripe.android.paymentsheet.PaymentSheetResult;

/**
 * static variables and methods used in the app.
 */
public class DataVariable {

    public static Gson gson = new Gson();

    public static void hideSoftKeyboard(Activity activity) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception exception) {
            //exception.printStackTrace();
            Log.e("exception", "===" + "hideSoftKeyboard");
        }
    }
}