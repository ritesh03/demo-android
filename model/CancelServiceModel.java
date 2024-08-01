package com.maktoday.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by cbl81 on 3/12/17.
 */

public class CancelServiceModel {

    @SerializedName("uniquieAppKey")
    public String uniquieAppKey;
    @SerializedName("serviceId")
    public String serviceId;
    @SerializedName("deleteReason")
    public String deleteReason;
    @SerializedName("timeZone")
    public String timeZone;
}
