package com.maktoday.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cbl81 on 16/11/17.
 */

public class MaidData implements Parcelable {
    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MaidData> CREATOR = new Parcelable.Creator<MaidData>() {
        @Override
        public MaidData createFromParcel(Parcel in) {
            return new MaidData(in);
        }

        @Override
        public MaidData[] newArray(int size) {
            return new MaidData[size];
        }
    };
    @SerializedName("timeSlot")
    @Expose
    public TimeSlot timeSlot;
    public String religion;
    public ProfilePicURL agencyImage;
    @SerializedName("_id")
    private String _id;

    public String getMaidCount() {
        return maidCount;
    }

    public void setMaidCount(String maidCount) {
        this.maidCount = maidCount;
    }

    @SerializedName("gender")
    private String gender;
    @SerializedName("maidCount")
    private String maidCount;
    @SerializedName("email")
    private String email;
    @SerializedName("country")
    private String country;
    @SerializedName("countryCode")
    private String countryCode;
    @SerializedName("phoneNo")
    private String phoneNo;
    @SerializedName("makId")
    private String makId;
    @SerializedName("agencyId")
    private String agencyId;

    @SerializedName("agencyType")
    private String agencyType;

    @SerializedName("uniquieAppKey")
    private String uniquieAppKey;
    @SerializedName("isLogin")
    private boolean isLogin;
    @SerializedName("isDeleted")
    private boolean isDeleted;
    @SerializedName("isBlocked")
    private boolean isBlocked;
    @SerializedName("registrationDate")
    private String registrationDate;
    @SerializedName("lastTimeLogin")
    private String lastTimeLogin;
    @SerializedName("timeSlots")
    private List<String> timeSlots;
    @SerializedName("profilePicURL")
    private ProfilePicURL profilePicURL;
    @SerializedName("password")
    private String password;
    @SerializedName("locationName")
    private String locationName;
    @SerializedName("rating")
    private Float rating = 0.0f;
    @SerializedName("languages")
    private List<Languages> languages;
    @SerializedName("price")
    private Double price;
    @SerializedName("makPrice")
    private Float makPrice;
    private boolean isFavourite = false;
    private String currency;
    private float avgCleaning;
    private float avgIroning;
    private float avgCooking;
    private float avgChildCare;
    @SerializedName("actualPrice")
    private Float actualPrice;
    @SerializedName("experience")
    private String experience;
    @SerializedName("nationality")
    private String nationality;
    @SerializedName("dob")
    private String dob;
    @SerializedName("description")
    private String description;
    @SerializedName("lastName")
    private String lastName;
    @SerializedName("firstName")
    private String firstName;
    @SerializedName("step")
    private int step;
    @SerializedName("__v")
    private int __v;
    @SerializedName("feedBack")
    private String feedBack;
    @SerializedName("currentLocation")
    private List<Double> currentLocation;
    @SerializedName("documentPicURL")
    private DocumentPicURL documentPicURL;
    @SerializedName("distance")
    private String distance;
    private String agencyName;
    @SerializedName("vat")
    private String vat;
    @SerializedName("new_vat")
    private String new_vat;
    @SerializedName("services")
    public List<ServicesProvide> services;

    public MaidData() {

    }


    protected MaidData(Parcel in) {
        _id = in.readString();
        gender = in.readString();
        maidCount = in.readString();
        email = in.readString();
        country = in.readString();
        countryCode = in.readString();
        phoneNo = in.readString();
        makId = in.readString();
        agencyId = in.readString();
        agencyType = in.readString();
        uniquieAppKey = in.readString();
        isLogin = in.readByte() != 0x00;
        isDeleted = in.readByte() != 0x00;
        isBlocked = in.readByte() != 0x00;
        registrationDate = in.readString();
        lastTimeLogin = in.readString();
        if (in.readByte() == 0x01) {
            timeSlots = new ArrayList<String>();
            in.readList(timeSlots, String.class.getClassLoader());
        } else {
            timeSlots = null;
        }
        profilePicURL = (ProfilePicURL) in.readValue(ProfilePicURL.class.getClassLoader());
        password = in.readString();
        locationName = in.readString();
        rating = in.readByte() == 0x00 ? null : in.readFloat();
        if (in.readByte() == 0x01) {
            languages = new ArrayList<Languages>();
            in.readList(languages, Languages.class.getClassLoader());
        } else {
            languages = null;
        }
        price = in.readDouble();
        makPrice = in.readFloat();
        avgCleaning = in.readFloat();
        avgIroning = in.readFloat();
        avgCooking = in.readFloat();
        avgChildCare = in.readFloat();
        actualPrice = in.readFloat();
        experience = in.readString();
        nationality = in.readString();
        dob = in.readString();
        description = in.readString();
        lastName = in.readString();
        firstName = in.readString();
        step = in.readInt();
        __v = in.readInt();
        feedBack = in.readString();
        religion = in.readString();
        if (in.readByte() == 0x01) {
            currentLocation = new ArrayList<Double>();
            in.readList(currentLocation, Double.class.getClassLoader());
        } else {
            currentLocation = null;
        }
        documentPicURL = (DocumentPicURL) in.readValue(DocumentPicURL.class.getClassLoader());
        distance = in.readString();
        agencyImage = (ProfilePicURL) in.readValue(ProfilePicURL.class.getClassLoader());
        agencyName = in.readString();
        vat=in.readString();
        new_vat = in.readString();
    }

    public float getAvgCooking() {
        return avgCooking;
    }

    public void setAvgCooking(float avgCooking) {
        this.avgCooking = avgCooking;
    }

    public float getAvgChildCare() {
        return avgChildCare;
    }

    public void setAvgChildCare(float avgChildCare) {
        this.avgChildCare = avgChildCare;
    }

    public float getAvgCleaning() {
        return avgCleaning;
    }

    public void setAvgCleaning(float avgCleaning) {
        this.avgCleaning = avgCleaning;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }

    public float getAvgIroning() {
        return avgIroning;
    }

    public void setAvgIroning(float avgIroning) {
        this.avgIroning = avgIroning;
    }

    public String getCurrency() {
        return currency;
    }
    public String getVat() {
        return vat;
    }


    public void setVat(String vat) {
        this.vat = vat;
    }
    public String getNew_vat(){
        return  new_vat;
    }
    public  void setNew_vat(String new_vat) {this.new_vat = new_vat; }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getAgencyName() {
        return agencyName;
    }

    public Float getMakPrice() {
        return makPrice;
    }

    public Float getActualPrice() {
        return actualPrice;
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

    public String getAgencyId() {
        return agencyId;
    }


    public void setAgencyId(String agencyId) {
        this.agencyId = agencyId;
    }

    public List<ServicesProvide> getServices() {
        return services;
    }

    public void setServices(List<ServicesProvide> services) {
        this.services = services;
    }


    public String getAgencyType(){
        return  agencyType;
    }
    public void setAgencyType(String agencyType){
        this.agencyType = agencyType;
    }

    public String getUniquieAppKey() {
        return uniquieAppKey;
    }

    public void setUniquieAppKey(String uniquieAppKey) {
        this.uniquieAppKey = uniquieAppKey;
    }

    public boolean getIsLogin() {
        return isLogin;
    }

    public void setIsLogin(boolean isLogin) {
        this.isLogin = isLogin;
    }

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public boolean getIsBlocked() {
        return isBlocked;
    }

    public void setIsBlocked(boolean isBlocked) {
        this.isBlocked = isBlocked;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getLastTimeLogin() {
        return lastTimeLogin;
    }

    public void setLastTimeLogin(String lastTimeLogin) {
        this.lastTimeLogin = lastTimeLogin;
    }

    public List<String> getTimeSlots() {
        return timeSlots;
    }

    public void setTimeSlots(List<String> timeSlots) {
        this.timeSlots = timeSlots;
    }

    public ProfilePicURL getProfilePicURL() {
        return profilePicURL;
    }

    public void setProfilePicURL(ProfilePicURL profilePicURL) {
        this.profilePicURL = profilePicURL;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
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

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public int get__v() {
        return __v;
    }

    public void set__v(int __v) {
        this.__v = __v;
    }

    public String getFeedBack() {
        return feedBack;
    }

    public void setFeedBack(String feedBack) {
        this.feedBack = feedBack;
    }

    public List<Double> getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(List<Double> currentLocation) {
        this.currentLocation = currentLocation;
    }

    public DocumentPicURL getDocumentPicURL() {
        return documentPicURL;
    }

    public void setDocumentPicURL(DocumentPicURL documentPicURL) {
        this.documentPicURL = documentPicURL;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_id);
        dest.writeString(gender);
        dest.writeString(maidCount);
        dest.writeString(email);
        dest.writeString(country);
        dest.writeString(countryCode);
        dest.writeString(phoneNo);
        dest.writeString(makId);
        dest.writeString(agencyId);
        dest.writeString(agencyType);
        dest.writeString(uniquieAppKey);
        dest.writeByte((byte) (isLogin ? 0x01 : 0x00));
        dest.writeByte((byte) (isDeleted ? 0x01 : 0x00));
        dest.writeByte((byte) (isBlocked ? 0x01 : 0x00));
        dest.writeString(registrationDate);
        dest.writeString(lastTimeLogin);
        if (timeSlots == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(timeSlots);
        }
        dest.writeValue(profilePicURL);
        dest.writeString(password);
        dest.writeString(locationName);
        if (rating == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeFloat(rating);
        }
        if (languages == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(languages);
        }
        dest.writeDouble(price);
        dest.writeFloat(makPrice);
        dest.writeFloat(avgCleaning);
        dest.writeFloat(avgIroning);
        dest.writeFloat(avgCooking);
        dest.writeFloat(avgChildCare);
        dest.writeFloat(actualPrice);
        dest.writeString(experience);
        dest.writeString(nationality);
        dest.writeString(dob);
        dest.writeString(description);
        dest.writeString(lastName);
        dest.writeString(firstName);
        dest.writeInt(step);
        dest.writeInt(__v);
        dest.writeString(feedBack);
        dest.writeString(religion);
        if (currentLocation == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(currentLocation);
        }
        dest.writeValue(documentPicURL);
        dest.writeString(distance);
        dest.writeValue(agencyImage);
        dest.writeString(agencyName);
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


