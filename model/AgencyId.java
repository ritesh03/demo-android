package com.maktoday.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by cbl1005 on 17/1/18.
 */

public class AgencyId {
    public String _id;
    public String agencyName;
    public ProfilePicURL profilePicURL;
    @SerializedName("agencyType")
    public String agencyType;
}
