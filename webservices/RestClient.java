package com.maktoday.webservices;

import android.os.Build;

import com.maktoday.Config.Config;
import com.maktoday.utils.AppGlobal;
import com.readystatesoftware.chuck.ChuckInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by cbl81 on 8/11/17.
 */

public class RestClient {

    private static ApiService apiServiceModel = null;
    private static ApiService apiServiceModelPaytab = null;

    /**
     * By using this method your response will be coming in  modal class object
     *
     * @return object of ApiService interface
     */
    public static ApiService getModalApiService() {

        if (apiServiceModel == null) {
            apiServiceModel = getApiService(Config.getBaseURL());
        }

        return apiServiceModel;

    }//getModalApiService

    public static ApiService getPayTabApiService() {

        if (apiServiceModelPaytab == null) {
            apiServiceModelPaytab = getApiService(Config.getPaytabUrl());
        }

        return apiServiceModelPaytab;

    }//getPayTabApiService

    private static ApiService getApiService(String url) {

        OkHttpClient okHttpClient = new OkHttpClient();

        OkHttpClient.Builder builder = okHttpClient.newBuilder();
        if( Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            if (Config.appMode.name().equals("TEST") || Config.appMode.name().equals("DEV")) {
                HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
                httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                builder.addInterceptor(httpLoggingInterceptor);
                builder.addInterceptor(new ChuckInterceptor(AppGlobal.mContext));
            }
        }

        builder.connectTimeout(60, TimeUnit.SECONDS);
        builder.readTimeout(60, TimeUnit.SECONDS);
        builder.writeTimeout(60, TimeUnit.SECONDS);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)

                .addConverterFactory(GsonConverterFactory.create())
                .client(builder.build())
                .build();

        return retrofit.create(ApiService.class);

    }//getApiService

}//RestClient
