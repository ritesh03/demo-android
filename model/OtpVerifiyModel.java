package com.maktoday.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OtpVerifiyModel {
    @SerializedName("statusCode")
    public int statusCode;
    @SerializedName("message")
    public String message;
    @SerializedName("data")
    public PojoLogin data;

   /* public class Data{

    }*/
}
