package com.maktoday.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by cbl81 on 15/11/17.
 */

public class PojoAgencyList {

    @SerializedName("statusCode")
    public int statusCode;
    @SerializedName("message")
    public String message;
    @SerializedName("data")
    public List<Data> data;

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
        public String _id;
        @SerializedName("rating")
        public Float rating;
        @SerializedName("profilePicURL")
        public ProfilePicURL profilePicURL;
        @SerializedName("agencyName")
        public String agencyName;
        @SerializedName("maidCount")
        public int maidCount;
        public Boolean isSelected=true;

        public Boolean getSelected() {
            return isSelected;
        }

        public void setSelected(Boolean selected) {
            isSelected = selected;
        }

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public Float getRating() {
            return rating;
        }

        public void setRating(Float rating) {
            this.rating = rating;
        }

        public ProfilePicURL getProfilePicURL() {
            return profilePicURL;
        }

        public void setProfilePicURL(ProfilePicURL profilePicURL) {
            this.profilePicURL = profilePicURL;
        }

        public String getAgencyName() {
            return agencyName;
        }

        public void setAgencyName(String agencyName) {
            this.agencyName = agencyName;
        }

        public int getMaidCount() {
            return maidCount;
        }

        public void setMaidCount(int maidCount) {
            this.maidCount = maidCount;
        }
    }
}
