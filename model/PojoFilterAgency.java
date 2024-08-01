package com.maktoday.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cbl81 on 21/11/17.
 */

public class PojoFilterAgency {

    @SerializedName("statusCode")
    @Expose
    public Integer statusCode;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("data")
    @Expose
    public List<Datum> data = new ArrayList<Datum>();

    public class Datum {

        @SerializedName("_id")
        @Expose
        public String id;
        @SerializedName("agencyName")
        @Expose
        public String agencyName;
        @SerializedName("countryName")
        @Expose
        public String countryName;

    }
}
