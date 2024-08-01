package com.maktoday.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by cbl1005 on 24/1/18.
 */

public class LatestMessage {

    @SerializedName("_id")
    @Expose
    public String id;
    @SerializedName("senderType")
    @Expose
    public String senderType;
    @SerializedName("senderId")
    @Expose
    public String senderId;
    @SerializedName("receiverId")
    @Expose
    public String receiverId;
    @SerializedName("serviceId")
    @Expose
    public String serviceId;
    @SerializedName("conversationId")
    @Expose
    public String conversationId;
    @SerializedName("isExpired")
    @Expose
    public Boolean isExpired;

    @SerializedName("timeStamp")
    @Expose
    public String timeStamp;
    @SerializedName("message")
    @Expose
    public String message;

    @SerializedName("location")
    @Expose
    public List<Double> location;

    @SerializedName("messageType")
    @Expose
    public String messageType;
}
