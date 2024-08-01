package com.maktoday.utils.facebook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Base64;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.gson.Gson;
import com.maktoday.utils.Log;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class FacebookLogin {
    private static final String TAG = "FacebookLogin";

    private Context mContext;
    private FacebookLoginListener facebookLoginListener;
    private CallbackManager mCallbackManager;
    private Object mUiRef;

    public FacebookLogin(Context context, Object uiRef) {
        mContext = context;
        mUiRef = uiRef;
       // printHashKey();
        FacebookSdk.sdkInitialize(context.getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();
        try {
            LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    Log.d(TAG, "onSuccess: " + new Gson().toJson(loginResult));
                    facebookLoginListener.onFbLoginSuccess();
                }

                @Override
                public void onCancel() {
                    Log.d(TAG, "onCancel: ");
                    facebookLoginListener.onFbLoginCancel();
                }

                @Override
                public void onError(FacebookException exception) {
                    Log.d(TAG, "onError: " + exception);
                    if (exception instanceof FacebookAuthorizationException) {
                        if (AccessToken.getCurrentAccessToken() != null) {
                            LoginManager.getInstance().logOut();
                        }
                    }
                    facebookLoginListener.onFbLoginError();
                }
            });
        }catch (Exception e){
            Log.e(TAG, "FacebookLogin: exception  "+ e.getMessage().toString());
        }
    }

    public void performLogin() {
        if(mUiRef instanceof Activity){
            LoginManager.getInstance().logInWithReadPermissions((Activity)mUiRef,
                    Arrays.asList("email", "public_profile"));
        }else if(mUiRef instanceof Fragment){
            LoginManager.getInstance().logInWithReadPermissions((Fragment)mUiRef,
                    Arrays.asList("email","public_profile"));
        }

    }

    public void getUserProfile() {
        Log.d(TAG, "getUserProfile: facebook");
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(@Nullable JSONObject jsonObject, @Nullable GraphResponse graphResponse) {
                        Log.d(TAG, "onCompleted: "+new Gson().toJson(jsonObject));
                        try {
                            facebookLoginListener.onGetprofileSuccess(jsonObject, graphResponse);
                            LoginManager.getInstance().logOut();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields","id,first_name,last_name,email,gender,picture.height(300).width(300)");
        request.setParameters(parameters);
        try {//     Exception handle request.executeAsync();
            request.executeAsync();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        try {//        mCallbackManager.onActivityResult(requestCode, resultCode, data);
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }catch ( Exception e){
            e.printStackTrace();
        }
    }

    public void setFacebookLoginListener(FacebookLoginListener facebookLoginListener){
        this.facebookLoginListener = facebookLoginListener;
    }

    private void printHashKey() {
        try {
            String packageName = mContext.getApplicationContext().getPackageName();
            PackageInfo info = mContext.getPackageManager().getPackageInfo(
                    packageName,
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e(TAG,"KeyHash:"+ Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG,"FacebookLogin"+"Package Name not found");
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG,"FacebookLogin"+"No such Algorithm");
        }
    }

}
