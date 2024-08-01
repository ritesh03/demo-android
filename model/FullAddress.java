package com.maktoday.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

public class FullAddress implements Parcelable {

    @SerializedName("_id")
    public String id;
    public String uniquieAppKey;
    public String streetName="";
    public String buildingName="";
    public String villaName="";

    @SerializedName("lat")
    public Double lat=0.0;
    @SerializedName("long")
    public Double lng=0.0 ;

    public String city="";
    public String country="";
    public String moreDetailedaddress;
    public LatLng mapLatLng ;
    public String address;

    public String buildingNumber="";
    public String postalCode="";


    public FullAddress() {
    }

    protected FullAddress(Parcel in) {
            id = in.readString();
            uniquieAppKey = in.readString();
            streetName = in.readString();
            buildingName = in.readString();
            villaName = in.readString();
            lat = in.readDouble();
            lng = in.readDouble();
            city = in.readString();
            country = in.readString();
            moreDetailedaddress = in.readString();
            mapLatLng = (LatLng) in.readValue(LatLng.class.getClassLoader());
            address = in.readString();
            buildingNumber=in.readString();
            postalCode=in.readString();
        }

        @Override
        public int describeContents () {
            return 0;
        }

        @Override
        public void writeToParcel (Parcel dest, int flags){
            dest.writeString(id);
            dest.writeString(uniquieAppKey);
            dest.writeString(streetName);
            dest.writeString(buildingName);
            dest.writeString(villaName);
            dest.writeDouble(lat);
            dest.writeDouble(lng);
            dest.writeString(city);
            dest.writeString(country);
            dest.writeString(moreDetailedaddress);
            dest.writeValue(mapLatLng);
            dest.writeString(address);
            dest.writeString(buildingNumber);
            dest.writeString(postalCode);
        }

        @SuppressWarnings("unused")
        public static final Parcelable.Creator<FullAddress> CREATOR = new Parcelable.Creator<FullAddress>() {
            @Override
            public FullAddress createFromParcel(Parcel in) {
                return new FullAddress(in);
            }

            @Override
            public FullAddress[] newArray(int size) {
                return new FullAddress[size];
            }
        };

}