package com.maktoday.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StripeChargeApi {
    @SerializedName("statusCode")
    @Expose
    public Integer statusCode;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("data")
    @Expose
    public Data data;

    public class Data {

        @SerializedName("id")
        @Expose
        public String id;
        @SerializedName("object")
        @Expose
        public String object;
        @SerializedName("amount")
        @Expose
        public Integer amount;
        @SerializedName("amount_refunded")
        @Expose
        public Integer amountRefunded;
        @SerializedName("application")
        @Expose
        public Object application;
        @SerializedName("application_fee")
        @Expose
        public Object applicationFee;
        @SerializedName("application_fee_amount")
        @Expose
        public Object applicationFeeAmount;
        @SerializedName("balance_transaction")
        @Expose
        public String balanceTransaction;

        @SerializedName("captured")
        @Expose
        public Boolean captured;
        @SerializedName("created")
        @Expose
        public Integer created;
        @SerializedName("currency")
        @Expose
        public String currency;
        @SerializedName("customer")
        @Expose
        public Object customer;
        @SerializedName("description")
        @Expose
        public String description;
        @SerializedName("destination")
        @Expose
        public Object destination;
        @SerializedName("dispute")
        @Expose
        public Object dispute;
        @SerializedName("failure_code")
        @Expose
        public Object failureCode;
        @SerializedName("failure_message")
        @Expose
        public Object failureMessage;

        @SerializedName("invoice")
        @Expose
        public Object invoice;
        @SerializedName("livemode")
        @Expose
        public Boolean livemode;

        @SerializedName("on_behalf_of")
        @Expose
        public Object onBehalfOf;
        @SerializedName("order")
        @Expose
        public Object order;

        @SerializedName("paid")
        @Expose
        public Boolean paid;
        @SerializedName("payment_intent")
        @Expose
        public Object paymentIntent;
        @SerializedName("payment_method")
        @Expose
        public String paymentMethod;

        @SerializedName("receipt_email")
        @Expose
        public Object receiptEmail;
        @SerializedName("receipt_number")
        @Expose
        public Object receiptNumber;
        @SerializedName("receipt_url")
        @Expose
        public String receiptUrl;
        @SerializedName("refunded")
        @Expose
        public Boolean refunded;

        @SerializedName("review")
        @Expose
        public Object review;
        @SerializedName("shipping")
        @Expose
        public Object shipping;

        @SerializedName("source_transfer")
        @Expose
        public Object sourceTransfer;
        @SerializedName("statement_descriptor")
        @Expose
        public Object statementDescriptor;
        @SerializedName("status")
        @Expose
        public String status;
        @SerializedName("transfer_data")
        @Expose
        public Object transferData;
        @SerializedName("transfer_group")
        @Expose
        public Object transferGroup;

    }
}
