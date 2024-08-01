package com.maktoday.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by cbl81 on 23/11/17.
 */

public class PojoCardList {

    @SerializedName("statusCode")
    private int statusCode;
    @SerializedName("message")
    private String message;
    @SerializedName("data")
    private List<Data> data;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public static class Data {
        @SerializedName("_id")
        private String _id;
        @SerializedName("isDeleted")
        private boolean isDeleted;
        @SerializedName("Date")
        private String Date;
        @SerializedName("cardFingerPrint")
        private String cardFingerPrint;
        @SerializedName("cardToken")
        private String cardToken;
        @SerializedName("cardNumber")
        private String cardNumber;
        @SerializedName("cardHolderName")
        private String cardHolderName;
        @SerializedName("cardBrand")
        private String cardBrand;
        @SerializedName("cardType")
        private String cardType;
        @SerializedName("validTill")
        private String validTill;

        @SerializedName("exp_month")
        private String exp_month;


        public String getCustomerPassword() {
            return customerPassword;
        }

        public void setCustomerPassword(String customerPassword) {
            this.customerPassword = customerPassword;
        }

        @SerializedName("customerPassword")
        private String customerPassword;

        @SerializedName("customerEmail")
        private String customerEmail;

        public boolean isSelected;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public boolean getIsDeleted() {
            return isDeleted;
        }

        public void setIsDeleted(boolean isDeleted) {
            this.isDeleted = isDeleted;
        }

        public String getDate() {
            return Date;
        }

        public void setDate(String Date) {
            this.Date = Date;
        }

        public String getCardFingerPrint() {
            return cardFingerPrint;
        }

        public void setCardFingerPrint(String cardFingerPrint) {
            this.cardFingerPrint = cardFingerPrint;
        }

        public String getCardToken() {
            return cardToken;
        }

        public void setCardToken(String cardToken) {
            this.cardToken = cardToken;
        }

        public String getCardNumber() {
            return cardNumber;
        }

        public void setCardNumber(String cardNumber) {
            this.cardNumber = cardNumber;
        }

        public String getCardHolderName() {
            return cardHolderName;
        }

        public void setCardHolderName(String cardHolderName) {
            this.cardHolderName = cardHolderName;
        }

        public String getCustomerEmail() {
            return customerEmail;
        }

        public void setCustomerEmail(String customerEmail) {
            this.customerEmail = customerEmail;
        }
        public String getCardBrand() {
            return cardBrand;
        }

        public void setCardBrand(String cardBrand) {
            this.cardBrand = cardBrand;
        }
        public String getCardType() {
            return cardType;
        }

        public void setCardType(String cardType) {
            this.cardType = cardType;
        }
        public String getValidTill() {
            return validTill;
        }

        public void setValidTill(String validTill) {
            this.validTill = validTill;
        }
        public String getExp_month() {
            return exp_month;
        }

        public void setExp_month(String exp_month) {
            this.exp_month = exp_month;
        }
    }
}
