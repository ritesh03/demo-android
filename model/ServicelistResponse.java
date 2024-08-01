package com.maktoday.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ServicelistResponse {

    @SerializedName("data")
    private List<ServicesProvide> data;
    @SerializedName("message")
    private String message;
    @SerializedName("statusCode")
    private String statusCode;

    public List<ServicesProvide> getData ()
    {
        return data;
    }

    public void setData (List<ServicesProvide>  data)
    {
        this.data = data;
    }

    public String getMessage ()
    {
        return message;
    }

    public void setMessage (String message)
    {
        this.message = message;
    }

    public String getStatusCode ()
    {
        return statusCode;
    }

    public void setStatusCode (String statusCode)
    {
        this.statusCode = statusCode;
    }



    public class Data
    {
        @SerializedName("isDeleted")
        private String isDeleted;
        @SerializedName("name")
        private String name;
        @SerializedName("isBlocked")
        private String isBlocked;
        @SerializedName("_id")
        private String _id;

        @SerializedName("description")
        private String description;

        @SerializedName("image")
        ProfilePicURL image;

        public String getIsDeleted ()
        {
            return isDeleted;
        }

        public void setIsDeleted (String isDeleted)
        {
            this.isDeleted = isDeleted;
        }

        public String getName ()
        {
            return name;
        }

        public void setName (String name)
        {
            this.name = name;
        }

        public String getIsBlocked ()
        {
            return isBlocked;
        }

        public void setIsBlocked (String isBlocked)
        {
            this.isBlocked = isBlocked;
        }

        public String get_id ()
        {
            return _id;
        }

        public void set_id (String _id)
        {
            this._id = _id;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

}
