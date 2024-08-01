package com.maktoday.model;

public class GoogleTimeZoneResponse {
    public String timeZoneName;
    public int rawOffset;
    public String timeZoneId;
    public int dstOffset;
    public String status;

    @Override
    public String toString() {
        return
                "GoogleTimeZoneResponse{" +
                        "timeZoneName = '" + timeZoneName + '\'' +
                        ",rawOffset = '" + rawOffset + '\'' +
                        ",timeZoneId = '" + timeZoneId + '\'' +
                        ",dstOffset = '" + dstOffset + '\'' +
                        ",status = '" + status + '\'' +
                        "}";
    }
}
