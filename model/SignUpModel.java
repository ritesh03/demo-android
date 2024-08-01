package com.maktoday.model;

/**
 * Created by cbl81 on 13/11/17.
 */

public class SignUpModel {
    public String uniquieAppKey;
    public String nationalId;
    public String streetName;
    public String buildingName;
    public String villaName;
    public String moreDetailedaddress;

    public String city;
    public String country;
    public String countryENCode;
    public String deviceToken;
    public String deviceType;
    public String address;
    public  Boolean optForEmail;

    public ProfilePicURL profilePicURL;
    public DocumentPicURL documentPicURL;

    public SignUpModel(String uniquieAppKey, String nationalId,Boolean optForEmail, String streetName, String buildingName, String villaName, String moreDetailedaddress, String city, String country, String countryENCode, String countryCode, String phoneNo, String timeZone, String deviceToken, String deviceType) {
        this.uniquieAppKey = uniquieAppKey;
        this.nationalId = nationalId;
        this.streetName = streetName;
        this.buildingName = buildingName;
        this.villaName = villaName;
        if (moreDetailedaddress != null && !moreDetailedaddress.isEmpty()) {
            this.moreDetailedaddress = moreDetailedaddress;
        }
        this.optForEmail = optForEmail;
        this.city = city;
        this.country = country;
        this.countryENCode = countryENCode;
        this.countryCode = countryCode;
        this.phoneNo = phoneNo;
        this.timeZone = timeZone;
        this.deviceToken = deviceToken;
        this.deviceType = deviceType;

    }

    public String countryCode;
    public String phoneNo;
    public Boolean isGuestFlag;
    public String fullName;
    public String firstName;
    public String lastName;
    public String timeZone;
    public String email;

    public void setGuestFlag(boolean isGuestFlag) {
        this.isGuestFlag = isGuestFlag;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setProfilePicURL(ProfilePicURL profilePicURL) {
        this.profilePicURL = profilePicURL;
    }

    public void setDocumentPicURL(DocumentPicURL documentPicURL) {
        this.documentPicURL = documentPicURL;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
