package com.maktoday.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cbl81 on 20/11/17.
 */

public class BookServiceModel {

    @SerializedName("uniquieAppKey")
    public String uniquieAppKey;

    @SerializedName("long")
    public double lng;
    @SerializedName("lat")
    public double lat;
    @SerializedName("locationName")
    public String locationName=".";
    @SerializedName("promoId")
    public String promoId="";
    @SerializedName("promoDiscount")
    public String promoDiscount="";
    @SerializedName("amount")
    public double amount=0.0;

    @SerializedName("serviceId")
    public String serviceId;
    @SerializedName("maidId")
    public String maidId;

    @SerializedName("services")
    public String services;

    @SerializedName("serviceData")
    public List<ServiceData> serviceData=new ArrayList<>();

    public String bookingId;

    public String timeZone;
    public String hour;
    public String deviceTimeZone;

    public String currency;

    public String moreDetailedaddress;

    public FullAddress address;

    @Override
    public String toString() {
        return "BookServiceModel{" +
                "uniquieAppKey='" + uniquieAppKey + '\'' +
                ", bookingId=" + bookingId +
                ", lng=" + lng +
                ", lat=" + lat +
                ", locationName='" + locationName + '\'' +
                ", promoId='" + promoId + '\'' +
                ", amount='" + amount + '\'' +
                ", serviceId='" + serviceId + '\'' +
                ", maidId='" + maidId + '\'' +
                ", serviceData=" + serviceData.toString() +
                ", timeZone='" + timeZone + '\'' +
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
        @SerializedName("hour")
        public String hour;
    }
}
