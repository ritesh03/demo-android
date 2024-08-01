package com.maktoday.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.LatLng;
import com.maktoday.R;
import com.maktoday.interfaces.OnLocationUpdatedListener;
import com.maktoday.utils.dialog.IOSAlertDialog;

public class LocationFragment extends BaseFragment implements GoogleApiClient.ConnectionCallbacks, LocationListener, OnLocationUpdatedListener {

    private static final int CODE_REQUEST_LOCATION = 1;
    private static final int REQUEST_CHECK_SETTINGS = 2;
    protected LatLng latLongCurrentLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest locationRequest;
    private OnLocationUpdatedListener onLocationUpdatedListener;
    private Context context;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setLocationUpdateListener(this);
        setGoogleApiClient();
    }

    public void setLocationUpdateListener(OnLocationUpdatedListener onUpdateListener) {
        onLocationUpdatedListener = onUpdateListener;
    }

    private void setGoogleApiClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //  checkForLocationPermissions(context);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        setCurrentLocation();
        //stopLocationUpdates();
    }

    private void checkForLocationPermissions(Context activity) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    CODE_REQUEST_LOCATION);
        } else {
            displayLocationSettingsRequest();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CODE_REQUEST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    // user rejected the permission
                    boolean showRationale = shouldShowRequestPermissionRationale(permissions[0]);
                    if (!showRationale) {
                        showDialog(getString(R.string.permissions_required_location),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which) {
                                            case DialogInterface.BUTTON_POSITIVE:
                                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                Uri uri = Uri.fromParts(Constants.SETTING_URI_SCHEME,
                                                        context.getPackageName(), null);
                                                intent.setData(uri);
                                                startActivityForResult(intent, Constants.REQUEST_CODE_SETTINGS);
                                                break;
                                        }
                                    }
                                }, Constants.NEVER_ASK);//take to settings
                        // user also CHECKED "never ask again"
                    } else if (Manifest.permission.ACCESS_FINE_LOCATION.equals(permissions[0])) {
                        showDialog(getString(R.string.permissions_required_location),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which) {
                                            case DialogInterface.BUTTON_POSITIVE:
                                                checkForLocationPermissions(context);
                                                break;
                                        }
                                    }
                                }, Constants.DENY);//ask permission again
                        // user did NOT check "never ask again"
                    }
                } else {
                    displayLocationSettingsRequest();
                }
            }
        }
    }

    private void displayLocationSettingsRequest() {
        // Create location request
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000 / 2);
        locationRequest.setFastestInterval(2000);

        // Create request to change gps settings
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        setCurrentLocation();
                        startLocationUpdates();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CHECK_SETTINGS) {
            setCurrentLocation();
            startLocationUpdates();
        } else if (resultCode != Activity.RESULT_OK && requestCode == REQUEST_CHECK_SETTINGS) {
            showDialogOK(getString(R.string.gps_required),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    displayLocationSettingsRequest();
                                    break;
                            }
                        }
                    });
        } else if (resultCode == Activity.RESULT_OK && requestCode == Constants.REQUEST_CODE_SETTINGS) {
            displayLocationSettingsRequest();
        } else {
            checkForLocationPermissions(context);
            onLocationUpdatedListener.onLocationUpdateFailure(new LatLng(0, 0));
        }
    }

 /*   private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context)
                .setMessage(message).setCancelable(false);
        alertDialog.setTitle(R.string.title_permission_dialog)
                .setPositiveButton(R.string.button_ok_permission_dialog, okListener)
                .create()
                .show();

    }*/

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        IOSAlertDialog dialog =  IOSAlertDialog.newInstance(
                context,
                getString(R.string.title_permission_dialog),
                message,
                getString(R.string.button_ok_permission_dialog),
                null,
                okListener,
                null,
                ContextCompat.getColor(context, R.color.app_color),
                0,
                false
        );
        dialog.show(requireActivity().getSupportFragmentManager(), "ios_dialog");;
    }


    private void showDialog(String message, DialogInterface.OnClickListener okListener, String from) {
        IOSAlertDialog dialog = null;
        if (from.equals(Constants.DENY)) {
            dialog =  IOSAlertDialog.newInstance(
                    context,
                    getString(R.string.title_permission_dialog),
                    message,
                    getString(R.string.button_ok_permission_dialog),
                    null,
                    okListener,
                    null,
                    ContextCompat.getColor(context, R.color.app_color),
                    0,
                    false
            );
        } else if (from.equals(Constants.NEVER_ASK)) {
            dialog =  IOSAlertDialog.newInstance(
                    context,
                    getString(R.string.title_permission_dialog),
                    message,
                    getString(R.string.button_setting_permission_dialog),
                    null,
                    okListener,
                    null,
                    ContextCompat.getColor(context, R.color.app_color),
                    0,
                    false
            );
        }
        if (dialog != null) {
            dialog.show(requireActivity().getSupportFragmentManager(), "ios_dialog");;
        }
    }

/*
    private void showDialog(String message, DialogInterface.OnClickListener okListener, String from) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context)
                .setMessage(message).setCancelable(false);

        if (from.equals(Constants.DENY)) {
            alertDialog.setTitle(R.string.title_permission_dialog)
                    .setPositiveButton(R.string.button_ok_permission_dialog, okListener)
                    .create()
                    .show();
        } else if (from.equals(Constants.NEVER_ASK)) {
            alertDialog.setTitle(R.string.title_permission_dialog)
                    .setPositiveButton(R.string.button_setting_permission_dialog, okListener)
                    .create()
                    .show();
        }

    }
*/

    private void setCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, R.string.permission_not_granted, Toast.LENGTH_SHORT).show();
        } else {
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                latLongCurrentLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                onLocationUpdatedListener.onLocationUpdated(latLongCurrentLocation);
            }
        }
    }

    protected void startLocationUpdates() {
        if (mGoogleApiClient.isConnected() && locationRequest != null) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
        }
    }

    protected void stopLocationUpdates() {
        if (mGoogleApiClient.isConnected())
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onLocationUpdated(LatLng latLongCurrentLocation) {
    }

    @Override
    public void onLocationUpdateFailure(LatLng latLongCurrentLocation) {
    }
}