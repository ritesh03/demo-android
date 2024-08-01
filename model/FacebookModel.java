package com.maktoday.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.security.Policy;

/**
 * Created by cbl81 on 15/11/17.
 */

public class FacebookModel implements Parcelable{

    public FacebookModel(String uniquieAppKey, String facebookId, String fullName, String email, ProfilePicURL profilePicURL, String deviceToken,
                         String deviceType,String loginType,String firstName,String lastName) {
        this.uniquieAppKey = uniquieAppKey;
        this.facebookId = facebookId;
        this.fullName = fullName;
        this.email = email;
        this.profilePicURL = profilePicURL;
        this.deviceToken = deviceToken;
        this.deviceType = deviceType;
        this.loginType=loginType;
        this.firstName=firstName;
        this.lastName=lastName;
    }

    public String uniquieAppKey;
    public String facebookId;
    public String fullName;
    public String email;
    public ProfilePicURL profilePicURL;
    public String deviceToken;
    public String deviceType;
    public String loginType;
    public String firstName;
    public String lastName;

    protected FacebookModel(Parcel in) {
        uniquieAppKey = in.readString();
        facebookId = in.readString();
        fullName = in.readString();
        email = in.readString();
        profilePicURL = in.readParcelable(ProfilePicURL.class.getClassLoader());
        deviceToken = in.readString();
        deviceType = in.readString();
        loginType = in.readString();
        firstName = in.readString();
        lastName = in.readString();
    }

    public static final Creator<FacebookModel> CREATOR = new Creator<FacebookModel>() {
        @Override
        public FacebookModel createFromParcel(Parcel in) {
            return new FacebookModel(in);
        }

        @Override
        public FacebookModel[] newArray(int size) {
            return new FacebookModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(uniquieAppKey);
        parcel.writeString(facebookId);
        parcel.writeString(fullName);
        parcel.writeString(email);
        parcel.writeParcelable(profilePicURL, i);
        parcel.writeString(deviceToken);
        parcel.writeString(deviceType);
        parcel.writeString(loginType);
        parcel.writeString(firstName);
        parcel.writeString(lastName);
    }
}
