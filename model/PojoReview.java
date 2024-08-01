package com.maktoday.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by cbl81 on 5/12/17.
 */

public class PojoReview {

    @SerializedName("_id")
    public String _id;
    @SerializedName("uniquieAppKey")
    public String uniquieAppKey;
    @SerializedName("userId")
    public UserId userId;
    @SerializedName("maidId")
    public String maidId;
    @SerializedName("agencyId")
    public String agencyId;
    @SerializedName("serviceId")
    public String serviceId;
    @SerializedName("timeStamp")
    public String timeStamp;
    @SerializedName("isBlocked")
    public boolean isBlocked;
    @SerializedName("isDeleted")
    public boolean isDeleted;
    @SerializedName("feedBack")
    public String description;
    @SerializedName("maidRating")
    public MaidRating maidRating;
    @SerializedName("__v")
    public int __v;

    public static class UserId {
        @SerializedName("_id")
        public String _id;
        @SerializedName("email")
        public String email;
        @SerializedName("fullName")
        public String fullName;
        @SerializedName("phoneNo")
        public String phoneNo;
    }
}
