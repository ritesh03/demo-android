package com.maktoday.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class ServicesProvide implements Parcelable {

    public static final Parcelable.Creator<ServicesProvide> CREATOR = new Parcelable.Creator<ServicesProvide>() {
        @Override
        public ServicesProvide createFromParcel(Parcel in) {
            return new ServicesProvide(in);
        }

        @Override
        public ServicesProvide[] newArray(int size) {
            return new ServicesProvide[size];
        }
    };
    @SerializedName("_id")
    private String _id;
    @SerializedName("name")
    private String name;


    @SerializedName("description")
    private String description;

    @SerializedName("image")
    private ProfilePicURL image;


    public ServicesProvide(Parcel in) {
        _id = in.readString();
        name = in.readString();
        description = in.readString();
        image = (ProfilePicURL) in.readValue(ProfilePicURL.class.getClassLoader());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String get_id() {
        return _id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ProfilePicURL getImage() {
        return image;
    }

    public void setImage(ProfilePicURL image) {
        this.image = image;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
      dest.writeString(_id);
      dest.writeString(name);
      dest.writeString(description);
        dest.writeValue(image);
    }

    @Override
    public String toString() {
        return "ServicesProvide{" +
                "_id='" + _id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", image=" + image +
                '}';
    }
}
