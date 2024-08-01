package com.maktoday.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cbl1005 on 24/1/18.
 */

public class PojoChatData {
    @SerializedName("totalMsgCount")
    @Expose
    public Integer totalMsgCount;
    @SerializedName("latestMessage")
    @Expose
    public List<LatestMessage> latestMessage = new ArrayList<>();
}
