package com.maktoday.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NotiCountResponse {
    @SerializedName("unreadCount")
    @Expose
    public Integer unreadCount;
    @SerializedName("unreadChat")
    @Expose
    public Integer unreadChat;
}
