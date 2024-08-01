package com.maktoday.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by cbl81 on 27/11/17.
 */

public class MaidID {

    @SerializedName("_id")
    @Expose
    public String _id;
    @SerializedName("gender")
    @Expose
    public String gender;
    @SerializedName("email")
    @Expose
    public String email;
    @SerializedName("country")
    @Expose
    public String country;
    @SerializedName("countryCode")
    @Expose
    public String countryCode;
    @SerializedName("phoneNo")
    @Expose
    public String phoneNo;
    @SerializedName("makId")
    @Expose
    public String makId;
    @SerializedName("agencyId")
    @Expose
    public String agencyId;
    @SerializedName("uniquieAppKey")
    @Expose
    public String uniquieAppKey;
    @SerializedName("isLogin")
    @Expose
    public Boolean isLogin;
    @SerializedName("isDeleted")
    @Expose
    public Boolean isDeleted;
    @SerializedName("isBlocked")
    @Expose
    public Boolean isBlocked;
    @SerializedName("registrationDate")
    @Expose
    public String registrationDate;
    @SerializedName("lastTimeLogin")
    @Expose
    public Object lastTimeLogin;
    @SerializedName("timeSlots")
    @Expose
    public List<Object> timeSlots = null;
    @SerializedName("profilePicURL")
    @Expose
    public ProfilePicURL profilePicURL;
    @SerializedName("password")
    @Expose
    public String password;
    @SerializedName("locationName")
    @Expose
    public String locationName;

    public String currency;
    @SerializedName("rating")
    @Expose
    public float rating;
    @SerializedName("languages")
    @Expose
    public List<String> languages = null;
    @SerializedName("price")
    @Expose
    public Integer price;

    @SerializedName("actualPrice")
    @Expose
    public Float actualPrice;


    @SerializedName("experience")
    @Expose
    public Integer experience;
    @SerializedName("nationality")
    @Expose
    public String nationality;

    @SerializedName("description")
    @Expose
    public String description;
    @SerializedName("lastName")
    @Expose
    public String lastName;
    @SerializedName("firstName")
    @Expose
    public String firstName;
    @SerializedName("step")
    @Expose
    public Integer step;
    @SerializedName("__v")
    @Expose
    public Integer __v;
    @SerializedName("feedBack")
    @Expose
    public String feedBack;
    @SerializedName("currentLocation")
    @Expose
    public List<Float> currentLocation = null;
    @SerializedName("documentPicURL")
    @Expose
    public DocumentPicURL documentPicURL;

}
