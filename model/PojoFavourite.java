package com.maktoday.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by cbl1005 on 8/2/18.
 */

public class PojoFavourite {
    @SerializedName("statusCode")
    private int statusCode;

    @SerializedName("message")
    private String message;

    public List<MaidData> data;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<MaidData> getData() {
        return data;
    }

    public void setData(List<MaidData> data) {
        this.data = data;
    }

}
