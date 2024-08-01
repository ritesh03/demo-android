package com.maktoday.utils;

import com.maktoday.model.MaidData;

public class MaidUtils {
    private MaidUtils() {
    }

    public static double getMaidRating(MaidData data) {
        double rating = 0.0;
        int count = 0;

        if (data.getAvgIroning() != 0.0) {
            count++;
        }
        if (data.getAvgCooking() != 0.0) {
            count++;
        }
        if (data.getAvgCleaning() != 0.0) {
            count++;
        }
        if (data.getAvgChildCare() != 0.0) {
            count++;
        }
        rating = (data.getAvgCooking()
                + data.getAvgIroning() + data.getAvgCleaning()
                + data.getAvgChildCare()) / Double.parseDouble(String.valueOf(count));

        return rating;
    }
}