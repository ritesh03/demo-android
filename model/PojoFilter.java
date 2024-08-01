package com.maktoday.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by cbl81 on 21/11/17.
 */

public class PojoFilter {

    @SerializedName("statusCode")
    public int statusCode;
    @SerializedName("message")
    public String message;
    @SerializedName("data")
    public List<String> data;
}
