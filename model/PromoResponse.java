package com.maktoday.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PromoResponse {
    @SerializedName("statusCode")
    @Expose
    public Integer statusCode;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("data")
    @Expose
    public Data data;

    public class Data {

        @SerializedName("_id")
        @Expose
        public String id;

        @SerializedName("discountedAmount")
        @Expose
        public String discountedAmount;
        @SerializedName("description")
        @Expose
        public String description;
        @SerializedName("discount")
        @Expose
        public String discount;
        @SerializedName("codeName")
        @Expose
        public String codeName;

    }
}
