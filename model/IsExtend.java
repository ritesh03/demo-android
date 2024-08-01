package com.maktoday.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by cbl81 on 27/11/17.
 */

public class IsExtend {

    @SerializedName("transactionId")
    @Expose
    public String transactionId;
    @SerializedName("declineReason")
    @Expose
    public String declineReason;
    @SerializedName("extendAmount")
    @Expose
    public Float extendAmount;
    @SerializedName("agencyAction")
    @Expose
    public String agencyAction="";
    @SerializedName("requested")
    @Expose
    public Boolean requested;
    @SerializedName("extendDuration")
    @Expose
    public Integer extendDuration;
    @SerializedName("reason")
    @Expose
    public String reason;
}
