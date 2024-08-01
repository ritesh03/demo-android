package com.maktoday.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Set;

/**
 * Created by cbl1005 on 4/1/18.
 */

public class TimeSlot implements Parcelable {
    @SerializedName("0")
    @Expose
    public Set<String > _0 = null;
    @SerializedName("1")
    @Expose
    public Set<String> _1 = null;
    @SerializedName("2")
    @Expose
    public Set<String> _2 = null;
    @SerializedName("3")
    @Expose
    public Set<String> _3 = null;
    @SerializedName("4")
    @Expose
    public Set<String> _4 = null;
    @SerializedName("5")
    @Expose
    public Set<String> _5 = null;
    @SerializedName("6")
    @Expose
    public Set<String> _6 = null;
    public TimeSlot()
    {

    }
    public TimeSlot(Parcel in) {
        _0 = (Set) in.readValue(Set.class.getClassLoader());
        _1 = (Set) in.readValue(Set.class.getClassLoader());
        _2 = (Set) in.readValue(Set.class.getClassLoader());
        _3 = (Set) in.readValue(Set.class.getClassLoader());
        _4 = (Set) in.readValue(Set.class.getClassLoader());
        _5 = (Set) in.readValue(Set.class.getClassLoader());
        _6 = (Set) in.readValue(Set.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(_0);
        dest.writeValue(_1);
        dest.writeValue(_2);
        dest.writeValue(_3);
        dest.writeValue(_4);
        dest.writeValue(_5);
        dest.writeValue(_6);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<TimeSlot> CREATOR = new Parcelable.Creator<TimeSlot>() {
        @Override
        public TimeSlot createFromParcel(Parcel in) {
            return new TimeSlot(in);
        }

        @Override
        public TimeSlot[] newArray(int size) {
            return new TimeSlot[size];
        }
    };
}
