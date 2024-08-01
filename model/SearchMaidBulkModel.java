package com.maktoday.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by cbl81 on 16/11/17.
 */

public class SearchMaidBulkModel implements Parcelable {
    public static final Creator<SearchMaidBulkModel> CREATOR = new Creator<SearchMaidBulkModel>() {
        @Override
        public SearchMaidBulkModel createFromParcel(Parcel in) {
            return new SearchMaidBulkModel(in);
        }

        @Override
        public SearchMaidBulkModel[] newArray(int size) {
            return new SearchMaidBulkModel[size];
        }
    };
    public  String authorization;
    public String uniquieAppKey;
    public ArrayList<String> nationality;
    public ArrayList<String> languages;
    public String gender="";
    public String searchMaidName="";
    public String maidId;
    public String agencyId;
    public long workDate;
    public long endDate;
    public long startTime;
    public long hour;
    public int duration;
    public int maidCount;
    public double lng=0.0;
    public double lat=0.0;
    public String locationName="";
    public String sort="";
    public double pageNo;
    public double limit;
    public String maidName="";
    public Float maidPrice=new Float(0.000);
    public String mapLocation="";
    public String agencyName="";
    public String selectedDate="";
    public String selectedTime="";
    public String currency="";
    public ArrayList<String> selectedAgency;
    public ProfilePicURL profilePicURL;
    public DocumentPicURL documentPicURL;
    public String additionAddress="";
    public String contactNo="";
    public String makId;
    public String servicesID;
    public SearchMaidBulkModel()
    {

    }

    protected SearchMaidBulkModel(Parcel in) {
        authorization = in.readString();
        uniquieAppKey = in.readString();
        nationality = in.createStringArrayList();
        languages = in.createStringArrayList();
        gender = in.readString();
        searchMaidName = in.readString();
        maidId = in.readString();
        agencyId = in.readString();
        workDate = in.readLong();
        endDate = in.readLong();
        startTime = in.readLong();
        hour = in.readLong();
        duration = in.readInt();
        maidCount = in.readInt();
        lng = in.readDouble();
        lat = in.readDouble();
        locationName = in.readString();
        sort = in.readString();
        pageNo = in.readDouble();
        limit = in.readDouble();
        maidName = in.readString();
        if (in.readByte() == 0) {
            maidPrice = null;
        } else {
            maidPrice = in.readFloat();
        }
        mapLocation = in.readString();
        agencyName = in.readString();
        selectedDate = in.readString();
        selectedTime = in.readString();
        currency = in.readString();
        selectedAgency = in.createStringArrayList();
        profilePicURL = in.readParcelable(ProfilePicURL.class.getClassLoader());
        documentPicURL = in.readParcelable(DocumentPicURL.class.getClassLoader());
        additionAddress = in.readString();
        contactNo = in.readString();
        makId = in.readString();
        servicesID=in.readString();
    }

    @Override
    public String toString() {
        return "SearchMaidModel{" +
                "authorization='" + authorization + '\'' +
                ", uniquieAppKey='" + uniquieAppKey + '\'' +
                ", nationality=" + nationality +
                ", languages=" + languages +
                ", gender='" + gender + '\'' +
                ", searchMaidName='" + searchMaidName + '\'' +
                ", maidId='" + maidId + '\'' +
                ", agencyId='" + agencyId + '\'' +
                ", workDate=" + workDate +
                ", endDate=" + endDate +
                ", startTime=" + startTime +
                ", hour=" + hour +
                ", duration=" + duration +
                ", maidCount=" + maidCount +
                ", lng=" + lng +
                ", lat=" + lat +
                ", locationName='" + locationName + '\'' +
                ", sort='" + sort + '\'' +
                ", pageNo=" + pageNo +
                ", limit=" + limit +
                ", maidName='" + maidName + '\'' +
                ", maidPrice=" + maidPrice +
                ", mapLocation='" + mapLocation + '\'' +
                ", agencyName='" + agencyName + '\'' +
                ", selectedDate='" + selectedDate + '\'' +
                ", selectedTime='" + selectedTime + '\'' +
                ", currency='" + currency + '\'' +
                ", selectedAgency=" + selectedAgency +
                ", profilePicURL=" + profilePicURL +
                ", documentPicURL=" + documentPicURL +
                ", additionAddress='" + additionAddress + '\'' +
                ", contactNo='" + contactNo + '\'' +
                ", makId='" + makId + '\'' +
                ", servicesID='" + servicesID + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(authorization);
        parcel.writeString(uniquieAppKey);
        parcel.writeStringList(nationality);
        parcel.writeStringList(languages);
        parcel.writeString(gender);
        parcel.writeString(searchMaidName);
        parcel.writeString(maidId);
        parcel.writeString(agencyId);
        parcel.writeLong(workDate);
        parcel.writeLong(endDate);
        parcel.writeLong(startTime);
        parcel.writeLong(hour);
        parcel.writeInt(duration);
        parcel.writeInt(maidCount);
        parcel.writeDouble(lng);
        parcel.writeDouble(lat);
        parcel.writeString(locationName);
        parcel.writeString(sort);
        parcel.writeDouble(pageNo);
        parcel.writeDouble(limit);
        parcel.writeString(maidName);
        if (maidPrice == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeFloat(maidPrice);
        }
        parcel.writeString(mapLocation);
        parcel.writeString(agencyName);
        parcel.writeString(selectedDate);
        parcel.writeString(selectedTime);
        parcel.writeString(currency);
        parcel.writeStringList(selectedAgency);
        parcel.writeParcelable(profilePicURL, i);
        parcel.writeParcelable(documentPicURL, i);
        parcel.writeString(additionAddress);
        parcel.writeString(contactNo);
        parcel.writeString(makId);
        parcel.writeString(servicesID);
    }
}

