package com.maktoday.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cbl81 on 20/11/17.
 */

public class BookServiceBulkModel {

    @SerializedName("uniquieAppKey")
    public String uniquieAppKey;
    @SerializedName("long")
    public double lng;
    @SerializedName("lat")
    public double lat;
    @SerializedName("locationName")
    public String locationName="";
    @SerializedName("promoId")
    public String promoId="";
    @SerializedName("promoDiscount")
    public String promoDiscount="";
    @SerializedName("amount")
    public double amount=0.0;
    @SerializedName("maidId")
    public String maidId;
    @SerializedName("serviceData")
    public List<ServiceData> serviceData=new ArrayList<>();
    @SerializedName("services")
    public String services;

    public String timeZone;
    public String maidCount;
    public String hour;
    public String deviceTimeZone;

    public String currency;

    public String moreDetailedaddress;

    public FullAddress address;

    @Override
    public String toString() {
        return "BookServiceModel{" +
                "uniquieAppKey='" + uniquieAppKey + '\'' +
                ", lng=" + lng +
                ", lat=" + lat +
                ", locationName='" + locationName + '\'' +
                ", promoId='" + promoId + '\'' +
                ", promoDiscount='" + promoDiscount + '\'' +
                ", amount='" + amount + '\'' +
                ", maidId='" + maidId + '\'' +
                ", serviceData=" + serviceData.toString() +
                ", timeZone='" + timeZone + '\'' +
                ", maidCount='" + maidCount + '\'' +
                ", deviceTimeZone='" + deviceTimeZone + '\'' +
                ", currency='" + currency + '\'' +
                ", moreDetailedaddress='" + moreDetailedaddress + '\'' +
                ", address=" + address +
                '}';
    }

    public static class ServiceData {
        @SerializedName("workDate")
        public String workDate;
        @SerializedName("endDate")
        public String endDate;
        @SerializedName("startTime")
        public String startTime;
        @SerializedName("duration")
        public int duration;
    }
}
