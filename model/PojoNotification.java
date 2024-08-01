package com.maktoday.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by cbl81 on 2/12/17.
 */

public class PojoNotification {

    @SerializedName("statusCode")
    public int statusCode;
    @SerializedName("message")
    public String message;
    @SerializedName("data")
    public List<Data> data;


    public static class Data {
        @SerializedName("_id")
        public String _id;
        @SerializedName("type")
        public String type;
        @SerializedName("serviceId")
        public String serviceId;
        @SerializedName("bookingId")
        public int bookingId;

        @SerializedName("reviewSubmitted")
        public Boolean reviewSubmitted;
      /*  @SerializedName("actionPerformed")
        public boolean actionPerformed;*/
      /*  @SerializedName("read")
        public List<String> read;
        @SerializedName("isDeleted")
        public boolean isDeleted;
        @SerializedName("message")*/
        public String userMessage;
        @SerializedName("timeStamp")
        public String timeStamp;
        @SerializedName("maidId")
        public MaidID maidId;
        @SerializedName("receiverId")
        public String receiverId;
        @SerializedName("senderId")
        public SenderId senderId;
        @SerializedName("__v")
        public int __v;
        @SerializedName("link")
        public String link;
    }


    public static class SenderId {
        @SerializedName("_id")
        public String _id;
        @SerializedName("agencyName")
        public String agencyName;
        @SerializedName("profilePicURL")
        public ProfilePicURL profilePicURL;
    }
}
