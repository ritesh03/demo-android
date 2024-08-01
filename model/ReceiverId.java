package com.maktoday.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by cbl1005 on 24/1/18.
 */

public class ReceiverId {
        @SerializedName("_id")
        @Expose
        public String id;
        @SerializedName("fullName")
        @Expose
        public String fullName;
}
