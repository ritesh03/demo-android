package com.maktoday.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by cbl81 on 16/11/17.
 */

public class Languages implements Parcelable {

    @SerializedName("_id")
    private String _id;
    @SerializedName("uniquieAppKey")
    private String uniquieAppKey;
    @SerializedName("isDeleted")
    private boolean isDeleted;
    @SerializedName("registrationDate")
    private String registrationDate;
    @SerializedName("languageCode")
    private String languageCode;
    @SerializedName("languageName")
    private String languageName;
    @SerializedName("__v")
    private int __v;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getUniquieAppKey() {
        return uniquieAppKey;
    }

    public void setUniquieAppKey(String uniquieAppKey) {
        this.uniquieAppKey = uniquieAppKey;
    }

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }

    public int get__v() {
        return __v;
    }

    public void set__v(int __v) {
        this.__v = __v;
    }

    protected Languages(Parcel in) {
        _id = in.readString();
        uniquieAppKey = in.readString();
        isDeleted = in.readByte() != 0x00;
        registrationDate = in.readString();
        languageCode = in.readString();
        languageName = in.readString();
        __v = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_id);
        dest.writeString(uniquieAppKey);
        dest.writeByte((byte) (isDeleted ? 0x01 : 0x00));
        dest.writeString(registrationDate);
        dest.writeString(languageCode);
        dest.writeString(languageName);
        dest.writeInt(__v);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Languages> CREATOR = new Parcelable.Creator<Languages>() {
        @Override
        public Languages createFromParcel(Parcel in) {
            return new Languages(in);
        }

        @Override
        public Languages[] newArray(int size) {
            return new Languages[size];
        }
    };
}
