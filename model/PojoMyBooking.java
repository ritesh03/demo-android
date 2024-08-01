package com.maktoday.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by cbl81 on 27/11/17.
 */

public class PojoMyBooking {
    @SerializedName("count")
    @Expose
    public Integer count;
    @SerializedName("data")
    @Expose
    public List<Datum> data = null;
    @SerializedName("_id")
    public String _id;
    @SerializedName("agencyName")
    public String agencyName;

    public class Datum {

        @SerializedName("_id")
        @Expose
        public String _id;
        @SerializedName("referenceId")
        @Expose
        public String referenceId;
        @SerializedName("serviceMakId")
        @Expose
        public String serviceMakId;
        @SerializedName("maidId")
        @Expose
        public MaidData maidId;
        @SerializedName("userId")
        @Expose
        public String userId;
        @SerializedName("promoDiscount")
        @Expose
        public String promoDiscount;
        @SerializedName("amountWithoutDiscount")
        @Expose
        public String amountWithoutDiscount;
        @SerializedName("promoName")
        @Expose
        public String promoName;
        @SerializedName("paymentMode")
        @Expose
        public String paymentMode;
        @SerializedName("agencyId")
        @Expose
        public AgencyId agencyId;



        @SerializedName("workDate")
        @Expose
        public long workDate;

        @SerializedName("hour")
        @Expose
        public long hour;

        @SerializedName("startTime")
        @Expose
        public long startTime;

        @SerializedName("endTime")
        @Expose
        public long endTime;

        @SerializedName("bookingTime")
        @Expose
        public String bookingTime;

        @SerializedName("amount")
        @Expose
        public Float amount;

        @SerializedName("actualPrice")
        @Expose
        public Float actualPrice;

        @SerializedName("duration")
        @Expose
        public Integer duration;

        @SerializedName("uniquieAppKey")
        @Expose
        public String uniquieAppKey;
        @SerializedName("bookingLocation")
        @Expose
        public List<Float> bookingLocation = null;
        @SerializedName("timeZone")
        @Expose
        public String timeZone;
        @SerializedName("currency")
        @Expose
        public String currency;
        @SerializedName("transactionId")
        @Expose
        public String transactionId;
        @SerializedName("maidCount")
        @Expose
        public String maidCount;
        @SerializedName("isExtend")
        @Expose
        public IsExtend isExtend;
        @SerializedName("deleteReason")
        @Expose
        public String deleteReason;
        @SerializedName("deleteRequestByUser")
        @Expose
        public Boolean deleteRequestByUser;
        @SerializedName("deleteAction")
        @Expose
        public String deleteAction;
        @SerializedName("declineReason")
        @Expose
        public String declineReason;
        @SerializedName("isCompleted")
        @Expose
        public Boolean isCompleted;
        @SerializedName("agencyAction")
        @Expose
        public String agencyAction;
        @SerializedName("billingAddress")
        @Expose
        public BillingAddress billingAddress;
        @SerializedName("moreDetailedaddress")
        @Expose
        public String moreDetailedaddress;
        @SerializedName("address")
        @Expose
        public FullAddress address;
        @SerializedName("locationName")
        @Expose
        public String locationName;

        @SerializedName("bookingType")
        @Expose
        public String bookingType;
        @SerializedName("__v")
        @Expose
        public Integer __v;
        @SerializedName("bookingId")
        @Expose
        public Integer bookingId;
        @SerializedName("vat")
        @Expose
        public String vat;
        @SerializedName("new_vat")
        @Expose
        public String new_vat;
        @SerializedName("services")
        @Expose
        public Services services;
    }
   public class Services{
        @SerializedName("_id")
        @Expose
        public String _id;
        @SerializedName("name")
        @Expose
        public String name;
    }
}
