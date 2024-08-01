package com.maktoday.model;

import android.os.Parcel;
import android.os.Parcelable;

public class DocumentPicURL implements Parcelable {
    private String thumbnail;
    private String original;

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    public DocumentPicURL() {
        }
    public DocumentPicURL(Parcel in) {
        thumbnail = in.readString();
        original = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(thumbnail);
        dest.writeString(original);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<DocumentPicURL> CREATOR = new Parcelable.Creator<DocumentPicURL>() {
        @Override
        public DocumentPicURL createFromParcel(Parcel in) {
            return new DocumentPicURL(in);
        }

        @Override
        public DocumentPicURL[] newArray(int size) {
            return new DocumentPicURL[size];
        }
    };
}
