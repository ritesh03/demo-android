package com.maktoday.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import java.util.Locale;

 public class LocaleHelper {

    public static Context onAttach(Context context, String defaultLanguage) {
        return setLocale(context,defaultLanguage);
    }

    private static Context setLocale(Context context, String language) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
           return updateResources(context, language);
        } else
            return  updateResourcesLegacy(context, language);
    }


     @TargetApi(Build.VERSION_CODES.N)
     private static Context updateResources(Context context,String language) {
         Locale locale =new  Locale(language);
         Locale.setDefault(locale);
         Configuration configuration = context.getResources().getConfiguration();
         configuration.setLocale(locale);
         configuration.setLayoutDirection(locale);
         return context.createConfigurationContext(configuration);
     }

     private static Context updateResourcesLegacy(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;
        configuration.setLayoutDirection(locale);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
         return context;
    }

}