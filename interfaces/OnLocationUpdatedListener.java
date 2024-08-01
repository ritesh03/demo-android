package com.maktoday.interfaces;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by cbl81 on 7/12/17.
 */

public interface OnLocationUpdatedListener {
    void onLocationUpdated(LatLng latLongCurrentLocation);
    void onLocationUpdateFailure(LatLng latLongCurrentLocation);
}