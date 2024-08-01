package com.maktoday.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ProfilePicURL implements Parcelable {
    private String original;
    private String thumbnail;

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public ProfilePicURL() {

    }

    protected ProfilePicURL(Parcel in) {
        original = in.readString();
        thumbnail = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(original);
        dest.writeString(thumbnail);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ProfilePicURL> CREATOR = new Parcelable.Creator<ProfilePicURL>() {
        @Override
        public ProfilePicURL createFromParcel(Parcel in) {
            return new ProfilePicURL(in);
        }

        @Override
        public ProfilePicURL[] newArray(int size) {
            return new ProfilePicURL[size];
        }
    };
}
