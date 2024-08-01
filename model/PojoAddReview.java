package com.maktoday.model;

/**
 * Created by cbl1005 on 8/1/18.
 */

public class PojoAddReview {
    private String uniquieAppKey;
    private String serviceId;
    private MaidRating maidRating;

    public PojoAddReview(String uniquieAppKey, String serviceId, MaidRating maidRating, String feedBack) {
        this.uniquieAppKey = uniquieAppKey;
        this.serviceId = serviceId;
        this.maidRating = maidRating;
        this.feedBack = feedBack;
    }

    private String feedBack;

}
