package com.maktoday.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by cbl81 on 23/11/17.
 */

public class PojoService {

    @SerializedName("statusCode")
    public int statusCode;
    @SerializedName("message")
    public String message;
    @SerializedName("data")
    public Data data;

    public static class Data {
        @SerializedName("serviceId")
        public List<String> serviceId;

        @SerializedName("referenceId")
        public String referenceId;

        @SerializedName("vat")
        public String vat;
        @SerializedName("agencyId")
        public String agencyId;


    }
}
