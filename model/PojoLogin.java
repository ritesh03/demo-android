package com.maktoday.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by cbl81 on 13/11/17.
 */

public class PojoLogin {
    private FullAddress usersAddress;
    private String nationalId;
    private String countryENCode;
    private String countryCode;

    @SerializedName("phoneNo")
    private String phoneNo;

    @SerializedName("_id")
    public String _id;
    @SerializedName("facebookId")
    public String facebookId;
    @SerializedName("uniquieAppKey")
    public String uniquieAppKey;
    @SerializedName("email")
    public String email;
    @SerializedName("deviceToken")
    public String deviceToken;
    @SerializedName("deviceType")
    public String deviceType;
    @SerializedName("actualSignupDone")
    public boolean actualSignupDone;
    @SerializedName("firstBookingDone")
    public boolean firstBookingDone;
    @SerializedName("isGuestFlag")
    public boolean isGuestFlag;
    @SerializedName("faceBookLogin")
    public boolean faceBookLogin;
    @SerializedName("profileComplete")
    public boolean profileComplete;
    @SerializedName("isDeactivated")
    public boolean isDeactivated;
    @SerializedName("isDeleted")
    public boolean isDeleted;
    @SerializedName("isBlocked")
    public boolean isBlocked;
    @SerializedName("lastTimeLogin")
    public String lastTimeLogin;
    @SerializedName("firstTimeLogin")
    public boolean firstTimeLogin;
    @SerializedName("isLogin")
    public boolean isLogin;
    @SerializedName("emailVerified")
    public boolean emailVerified;
    @SerializedName("isVerified")
    public boolean isVerified;
    @SerializedName("registrationDate")
    public String registrationDate;
    @SerializedName("locationName")
    public String locationName="";
    @SerializedName("documentPicURL")
    public DocumentPicURL documentPicURL;
    @SerializedName("profilePicURL")
    public ProfilePicURL profilePicURL;
    @SerializedName("billingAddress")
    public BillingAddress billingAddress;
    @SerializedName("fullName")
    public String fullName="";
    @SerializedName("accessToken")
    public String accessToken;
    @SerializedName("firstName")
    public String firstName;
    @SerializedName("lastName")
    public String lastName;


    public ArrayList<FullAddress> multipleAddress;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public String getUniquieAppKey() {
        return uniquieAppKey;
    }

    public void setUniquieAppKey(String uniquieAppKey) {
        this.uniquieAppKey = uniquieAppKey;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public boolean isActualSignupDone() {
        return actualSignupDone;
    }

    public void setActualSignupDone(boolean actualSignupDone) {
        this.actualSignupDone = actualSignupDone;
    }

    public boolean isFirstBookingDone() {
        return firstBookingDone;
    }

    public void setFirstBookingDone(boolean firstBookingDone) {
        this.firstBookingDone = firstBookingDone;
    }

    public boolean isGuestFlag() {
        return isGuestFlag;
    }

    public void setGuestFlag(boolean guestFlag) {
        isGuestFlag = guestFlag;
    }

    public boolean isFaceBookLogin() {
        return faceBookLogin;
    }

    public void setFaceBookLogin(boolean faceBookLogin) {
        this.faceBookLogin = faceBookLogin;
    }

    public boolean isProfileComplete() {
        return profileComplete;
    }

    public void setProfileComplete(boolean profileComplete) {
        this.profileComplete = profileComplete;
    }

    public boolean isDeactivated() {
        return isDeactivated;
    }

    public void setDeactivated(boolean deactivated) {
        isDeactivated = deactivated;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    public String getLastTimeLogin() {
        return lastTimeLogin;
    }

    public void setLastTimeLogin(String lastTimeLogin) {
        this.lastTimeLogin = lastTimeLogin;
    }

    public boolean isFirstTimeLogin() {
        return firstTimeLogin;
    }

    public void setFirstTimeLogin(boolean firstTimeLogin) {
        this.firstTimeLogin = firstTimeLogin;
    }

    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean login) {
        isLogin = login;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public DocumentPicURL getDocumentPicURL() {
        return documentPicURL;
    }

    public void setDocumentPicURL(DocumentPicURL documentPicURL) {
        this.documentPicURL = documentPicURL;
    }

    public ProfilePicURL getProfilePicURL() {
        return profilePicURL;
    }

    public void setProfilePicURL(ProfilePicURL profilePicURL) {
        this.profilePicURL = profilePicURL;
    }

    public BillingAddress getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(BillingAddress billingAddress) {
        this.billingAddress = billingAddress;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }


    public FullAddress getUsersAddress() {
        return usersAddress;
    }

    public void setUsersAddress(FullAddress usersAddress) {
        this.usersAddress = usersAddress;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public String getCountryENCode() {
        return countryENCode;
    }

    public void setCountryENCode(String countryENCode) {
        this.countryENCode = countryENCode;
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
}
