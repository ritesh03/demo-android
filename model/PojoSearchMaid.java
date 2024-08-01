package com.maktoday.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by cbl81 on 16/11/17.
 */

public class PojoSearchMaid {

    @SerializedName("statusCode")
    private int statusCode;
    @SerializedName("message")
    private String message;
    @SerializedName("data")
    private Data data;

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

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }


    public static class Data {


        public int getAvailableMaidsCount() {
            return availableMaidsCount;
        }

        public void setAvailableMaidsCount(int availableMaidsCount) {
            this.availableMaidsCount = availableMaidsCount;
        }

        @SerializedName("availableMaidsCount")
        private int availableMaidsCount;

        @SerializedName("requestedMaids")
        private List<MaidData> requestedMaids;
        @SerializedName("suggestedMaids")
        private List<MaidData> suggestedMaids;
        @SerializedName("otherMaids")
        private List<MaidData> otherMaids;

        public List<MaidData> getOtherMaids() {
            return otherMaids;
        }

        public void setOtherMaids(List<MaidData> otherMaids) {
            this.otherMaids = otherMaids;
        }

        public List<MaidData> data;
        public List<MaidData> getRequestedMaids() {
            return requestedMaids;
        }

        public void setRequestedMaids(List<MaidData> requestedMaids) {
            this.requestedMaids = requestedMaids;
        }

        public List<MaidData> getSuggestedMaids() {
            return suggestedMaids;
        }

        public void setSuggestedMaids(List<MaidData> suggestedMaids) {
            this.suggestedMaids = suggestedMaids;
        }
    }

}
