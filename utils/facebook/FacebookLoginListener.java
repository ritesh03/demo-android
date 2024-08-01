package com.maktoday.utils.facebook;

import com.facebook.GraphResponse;

import org.json.JSONObject;

/**
 * Created by cbl81 on 13/11/17.
 */

public interface FacebookLoginListener {

    void onFbLoginSuccess();

    void onFbLoginCancel();

    void onFbLoginError();

    void onGetprofileSuccess(JSONObject object, GraphResponse response);

}