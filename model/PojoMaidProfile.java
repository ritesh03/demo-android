package com.maktoday.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by cbl81 on 17/11/17.
 */

public class PojoMaidProfile {

    @SerializedName("_id")
    public String _id;
    @SerializedName("gender")
    public String gender;
    @SerializedName("email")
    public String email;
    @SerializedName("country")
    public String country;
    @SerializedName("countryCode")
    public String countryCode;
    @SerializedName("phoneNo")
    public String phoneNo;
    @SerializedName("makId")
    public String makId;
    @SerializedName("locationName")
    public String locationName;
    @SerializedName("rating")
    public float rating;
    @SerializedName("languages")
    public List<Languages> languages;
    @SerializedName("price")
    public Double price;
    @SerializedName("servicesProvide")
    public List<ServicesProvide> servicesProvide;
    @SerializedName("isFavourite")
    public boolean isFavourite;

    public float avgDefault;
    public float avgCleaning;
    public float avgIroning;

    public float getAvgCooking() {
        return avgCooking;
    }

    public void setAvgCooking(float avgCooking) {
        this.avgCooking = avgCooking;
    }

    public float avgCooking;
    public float avgWashing;

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }

    public float avgChildCare;

    @SerializedName("actualPrice")
    public Float actualPrice;


    @SerializedName("makPrice")
    public Float makPrice;


    @SerializedName("experience")
    public String experience;
    @SerializedName("nationality")
    public String nationality;
    @SerializedName("dob")
    public String dob;
    @SerializedName("description")
    public String description;
    @SerializedName("lastName")
    public String lastName;
    @SerializedName("firstName")
    public String firstName;
    @SerializedName("feedBack")
    public String feedBack;
    @SerializedName("currentLocation")
    public List<String> currentLocation;
    @SerializedName("agencyId")
    public String agencyId;
    @SerializedName("agencyName")
    public String agencyName;
    @SerializedName("reviewData")
    public List<PojoReview> review;
    @SerializedName("reviewCount")
    public int reviewCount;

    @SerializedName("totalService")
    public String totalService;
    @SerializedName("vat")
    public String vat;

    @SerializedName("new_vat")
    public  String new_vat;

    public ProfilePicURL agencyImage;

    public ProfilePicURL profilePicURL;

    public DocumentPicURL documentPicURL;

    public float getAvgDefault() {
        return avgDefault;
    }

    public void setAvgDefault(float avgDefault) {
        this.avgDefault = avgDefault;
    }

    public float getAvgCleaning() {
        return avgCleaning;
    }

    public void setAvgCleaning(float avgCleaning) {
        this.avgCleaning = avgCleaning;
    }

    public float getAvgIroning() {
        return avgIroning;
    }

    public void setAvgIroning(float avgIroning) {
        this.avgIroning = avgIroning;
    }

    public float getAvgWashing() {
        return avgWashing;
    }

    public void setAvgWashing(float avgWashing) {
        this.avgWashing = avgWashing;
    }

    public float getAvgChildCare() {
        return avgChildCare;
    }

    public void setAvgChildCare(float avgChildCare) {
        this.avgChildCare = avgChildCare;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getMakId() {
        return makId;
    }

    public void setMakId(String makId) {
        this.makId = makId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public List<Languages> getLanguages() {
        return languages;
    }

    public void setLanguages(List<Languages> languages) {
        this.languages = languages;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFeedBack() {
        return feedBack;
    }

    public void setFeedBack(String feedBack) {
        this.feedBack = feedBack;
    }

    public List<String> getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(List<String> currentLocation) {
        this.currentLocation = currentLocation;
    }

    public String getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(String agencyId) {
        this.agencyId = agencyId;
    }

    public String getAgencyName() {
        return agencyName;
    }

    public void setAgencyName(String agencyName) {
        this.agencyName = agencyName;
    }

    public List<PojoReview> getReview() {
        return review;
    }

    public void setReview(List<PojoReview> review) {
        this.review = review;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public Float getActualPrice() {
        return actualPrice;
    }

    public Float getMakPrice() {
        return makPrice;
    }
    public String getTotalService() {
        return totalService;
    }

    public void setTotalService(String totalService) {
        this.totalService = totalService;
    }
    public String getVat() {
        return vat;
    }

    public void setVat(String vat) {
        this.vat = vat;
    }

    public String getNew_vat() {
        return new_vat;
    }

    public void setNew_vat(String new_vat) {
        this.new_vat = new_vat;
    }
}
