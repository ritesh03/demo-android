package com.maktoday.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by cbl81 on 21/11/17.
 */

public class PojoFilterLanguage {

    @SerializedName("statusCode")
    public int statusCode;
    @SerializedName("message")
    public String message;
    @SerializedName("data")
    public List<Data> data;

    public static class Data implements Parcelable{
        @SerializedName("_id")
        public String _id;

        @SerializedName("languageName")
        public String languageName="";

        public boolean isSelected;

        public Data(Parcel in) {
            _id = in.readString();
            languageName = in.readString();
            isSelected = in.readByte() != 0;
        }

        public static final Creator<Data> CREATOR = new Creator<Data>() {
            @Override
            public Data createFromParcel(Parcel in) {
                return new Data(in);
            }

            @Override
            public Data[] newArray(int size) {
                return new Data[size];
            }
        };

        public Data() {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(_id);
            parcel.writeString(languageName);
            parcel.writeByte((byte) (isSelected ? 1 : 0));
        }
    }
}
