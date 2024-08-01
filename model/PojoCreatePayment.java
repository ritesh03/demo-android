package com.maktoday.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by cbl81 on 24/11/17.
 */

public class PojoCreatePayment {

    @SerializedName("statusCode")
    public int statusCode;
    @SerializedName("message")
    public String message;
    @SerializedName("data")
    public Data data = new Data();

    public static class Data {
        @SerializedName("trxId")
        public String transactionId = "";

        public List<Datum> data = null;
    }

    public class Datum {

        @SerializedName("address")
        @Expose
        private FullAddress address;

        public FullAddress getAddress() {
            return address;
        }

        public void setAddress(FullAddress address) {
            this.address = address;
        }

    }
}
