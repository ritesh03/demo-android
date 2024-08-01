package com.maktoday.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cbl81 on 16/11/17.
 */

public class SearchMaidModel implements Parcelable {
    public static final Creator<SearchMaidModel> CREATOR = new Creator<SearchMaidModel>() {
        @Override
        public SearchMaidModel createFromParcel(Parcel in) {
            return new SearchMaidModel(in);
        }

        @Override
        public SearchMaidModel[] newArray(int size) {
            return new SearchMaidModel[size];
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
    public String agencyType;
    public long workDate;
    public long endDate;
    public long startTime;
    public long hour;
    public int duration;
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
    public String selectedToDate="";
    public String selectedTime="";
    public String currency="";
    public String servicesID;
    public ArrayList<String> selectedAgency;
    public ProfilePicURL profilePicURL;
    public DocumentPicURL documentPicURL;
    public List<ServicesProvide> services = new ArrayList<ServicesProvide>();
    public String additionAddress="";
    public String contactNo="";
    public String vat="";
    public String new_vat="";
    public String makId;


    public SearchMaidModel()
    {

    }

    protected SearchMaidModel(Parcel in) {
        authorization = in.readString();
        uniquieAppKey = in.readString();
        nationality = in.createStringArrayList();
        languages = in.createStringArrayList();
        gender = in.readString();
        searchMaidName = in.readString();
        maidId = in.readString();
        agencyId = in.readString();
        agencyType = in.readString();
        workDate = in.readLong();
        endDate = in.readLong();
        startTime = in.readLong();
        hour = in.readLong();
        duration = in.readInt();
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
        selectedToDate = in.readString();
        selectedTime = in.readString();
        currency = in.readString();
        selectedAgency = in.createStringArrayList();
        profilePicURL = in.readParcelable(ProfilePicURL.class.getClassLoader());
        documentPicURL = in.readParcelable(DocumentPicURL.class.getClassLoader());
        services = in.readArrayList(ServicesProvide.class.getClassLoader());
        additionAddress = in.readString();
        contactNo = in.readString();
        makId = in.readString();
        vat=in.readString();
        new_vat = in.readString();
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
                ",agencyType='"+ agencyType + '\''+
                ", workDate=" + workDate +
                ", endDate=" + endDate +
                ", startTime=" + startTime +
                ", hour=" + hour +
                ", duration=" + duration +
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
                ", selectedToDate='" + selectedToDate + '\'' +
                ", selectedTime='" + selectedTime + '\'' +
                ", currency='" + currency + '\'' +
                ", selectedAgency=" + selectedAgency +
                ", profilePicURL=" + profilePicURL +
                ", documentPicURL=" + documentPicURL +
                ", services=" + services+
                ", additionAddress='" + additionAddress + '\'' +
                ", contactNo='" + contactNo + '\'' +
                ", makId='" + makId + '\'' +
                ",vat='"+vat+'\''+
                ",new_vat='"+new_vat+'\''+
                ",servicesID='"+servicesID+'\''+
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
        parcel.writeString(agencyType);
        parcel.writeLong(workDate);
        parcel.writeLong(endDate);
        parcel.writeLong(startTime);
        parcel.writeLong(hour);
        parcel.writeInt(duration);
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
        parcel.writeString(selectedToDate);
        parcel.writeString(selectedTime);
        parcel.writeString(currency);
        parcel.writeStringList(selectedAgency);
        parcel.writeParcelable(profilePicURL, i);
        parcel.writeParcelable(documentPicURL, i);
        parcel.writeList(services);
        parcel.writeString(additionAddress);
        parcel.writeString(contactNo);
        parcel.writeString(makId);
        parcel.writeString(vat);
        parcel.writeString(new_vat);
        parcel.writeString(servicesID);
    }
}

