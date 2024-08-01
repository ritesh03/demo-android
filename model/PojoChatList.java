package com.maktoday.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by cbl1005 on 24/1/18.
 */

public class PojoChatList {

    @SerializedName("_id")
    @Expose
    public String id;

    /*  @SerializedName("senderId")
      @Expose
      public String senderId;
      @SerializedName("receiverId")
      @Expose
      public ReceiverId receiverId;*/

    @SerializedName("serviceId")
    @Expose
    public String serviceId;
    @SerializedName("senderType")
    @Expose
    public String senderType;
    @SerializedName("messageType")
    @Expose
    public String messageType;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("location")
    @Expose
    public Object location;
    @SerializedName("timeStamp")
    @Expose
    public String timeStamp;
    @SerializedName("unreadCount")
    @Expose
    public Integer unreadCount;
    @SerializedName("userData")
    @Expose
    public UserData userData;

    public PojoChatList() {

    }

    public class UserData {
        @SerializedName("_id")
        @Expose
        public String id;
        @SerializedName("firstName")
        @Expose
        public String firstName = "";

        @SerializedName("lastName")
        @Expose
        public String lastName = "";
    }
}
