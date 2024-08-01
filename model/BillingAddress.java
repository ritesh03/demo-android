package com.maktoday.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by cbl81 on 15/11/17.
 */

public class BillingAddress {

    @SerializedName("billingName")
    public String billingName;
    @SerializedName("streetName")
    public String streetName;
    @SerializedName("buildingName")
    public String buildingName;
    @SerializedName("villaName")
    public String villaName;
    @SerializedName("city")
    public String city;
    @SerializedName("country")
    public String country;
    @SerializedName("countryENCode")
    public String countryENCode;
    @SerializedName("countryCode")
    public String countryCode;
    @SerializedName("phoneNo")
    public String phoneNo;

    public String moreDetailedaddress;
}
