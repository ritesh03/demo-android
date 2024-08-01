package com.maktoday.views.chatmap;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.maktoday.utils.Constants;
import com.maktoday.utils.Log;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.maktoday.R;
import com.maktoday.databinding.ActivityMapBinding;

import com.maktoday.utils.dialog.IOSAlertDialog;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Created by cbl1005 on 31/1/18.
 */



public class MapActivity extends AppCompatActivity implements OnMapReadyCallback,View.OnClickListener {

    private static final String TAG = "MapActivity";
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private GoogleMap googleMap;
    private Marker marker;
    private PlacesClient placesClient;
    private LatLng latLng;
    private ActivityMapBinding binding;
    LocationManager locationManager;
    boolean GpsStatus;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback mLocationCallback;


    private final ActivityResultLauncher<Intent> resultLauncherAddressSearch = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Intent data = result.getData();
                if (result.getResultCode() == Activity.RESULT_OK) {
                    if (data != null) {
                        Place place = Autocomplete.getPlaceFromIntent(data);
                        latLng = place.getLatLng();
                        binding.tvsearchLocation.setText(place.getAddress());
                        Log.i("Location", "LatLng: " + latLng);
                        marker.setPosition(latLng);
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f));
                    }
                } else {
                    if (data != null) {
                        Status status = Autocomplete.getStatusFromIntent(data);
                        Log.i("Location", status.getStatusMessage());
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(MapActivity.this, R.layout.activity_map);

        setContentView(binding.getRoot());
        init();
        setData();
        setListeners();


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(MapActivity.this);
        locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,1000).build();
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data

                    Log.d("Latitude11", String.valueOf(location.getLatitude()));
                    Log.d("Latitude11", String.valueOf(location.getLongitude()));
                }
            }
        };

        binding.mapView.onCreate(savedInstanceState);
        Places.initialize(this, getString(R.string.google_api_key_mak));
        placesClient = Places.createClient(this);

        binding.mapView.getMapAsync(this);

        binding.tvsearchLocation.setOnClickListener(view -> {
            List<Place.Field> placeFields = Arrays.asList(
                    Place.Field.LAT_LNG,
                    Place.Field.ADDRESS,
                    Place.Field.ADDRESS_COMPONENTS
            );
            Intent autocompleteIntent = new Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.OVERLAY, placeFields)
                    .build(this);
            resultLauncherAddressSearch.launch(autocompleteIntent);
        });

        submitCurrentLocation();
    }


        private void init() {
        setSupportActionBar(binding.toolbarMap);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_white);
        }
    }
        private void setData() {
        binding.title.setText(R.string.share_location);
    }


        private void setListeners() {
        binding.tvShare.setOnClickListener(this);
        binding.fabCurrentLocation.setOnClickListener(this);
    }

    @Override
    public void onPause() {
        binding.mapView.onPause();
        stopLocationUpdates();
        super.onPause();
    }
    private void stopLocationUpdates() {

        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvShare:
                Intent intent = new Intent();
                intent.putExtra(Constants.LATTITUDE, String.valueOf(latLng.latitude));
                intent.putExtra(Constants.LONGITUDE, String.valueOf(latLng.longitude));
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.fabCurrentLocation:
                submitCurrentLocation();
                break;
        }

    }


    void submitCurrentLocation(){
        if (checkLocationPermission()) {
            // Log.e("1", "1");
            if (GPSStatus()) {
                //Log.e("2", "2");
                //  commonFunction.showProgressDialog(MapActivity.this);

                getCurrentLocation();
            } else {
                //Log.e("3", "3");
                showDialogOK(getString(R.string.gps_required),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        Intent intent1 = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                        startActivity(intent1);
                                        break;
                                    case DialogInterface.BUTTON_NEGATIVE:

                                        break;
                                }
                            }
                        });

            }
        } else {

        }
    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {


        IOSAlertDialog dialog =  IOSAlertDialog.newInstance(
                MapActivity.this,
                getString(R.string.title_permission_dialog),
                message,
                getString(R.string.button_ok_permission_dialog),
                getString(R.string.cancel1),
                okListener,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                },
                ContextCompat.getColor(MapActivity.this,R.color.app_color),
                ContextCompat.getColor(MapActivity.this,R.color.app_color),
                true
        );
        dialog.show(getSupportFragmentManager(), "ios_dialog");;



    }

    public boolean GPSStatus() {
        locationManager = (LocationManager) MapActivity.this.getSystemService(Context.LOCATION_SERVICE);
        GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                ActivityCompat.requestPermissions(MapActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);

            } else {

                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }



    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        LatLng initialLocation = new LatLng(37.7749, -122.4194);
        latLng = initialLocation;
      MarkerOptions  markerOptions = new MarkerOptions().position(initialLocation).title("");
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_filled));
        marker = googleMap.addMarker(markerOptions);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, 12f));

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
            map.getUiSettings().setMyLocationButtonEnabled(false);

        }


        googleMap.setOnMarkerClickListener(clickedMarker -> {
           return true;
        });

        googleMap.setOnMapClickListener(latLng -> {
            // Update marker position when the map is clicked and marker move is enabled
           marker.setPosition(latLng);
           this.latLng = latLng;
            setLocation(latLng,false);
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.mapView.onResume();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
       binding.mapView.onLowMemory();
    }
        @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        binding.mapView.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e(TAG, "reeeeee " + requestCode + "");
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                            Intent intent1 = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent1);
                    }

                } else {
                   showDialog(getString(R.string.permissions_required_location),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case DialogInterface.BUTTON_POSITIVE:
                                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            Uri uri = Uri.fromParts(Constants.SETTING_URI_SCHEME,
                                                    getApplicationContext().getPackageName(), null);
                                            intent.setData(uri);
                                            startActivityForResult(intent, Constants.REQUEST_CODE_SETTINGS);
                                            break;
                                    }
                                }
                            }, Constants.NEVER_ASK);//take to settings
                }
                break;
        }
    }

    private void showDialog(String message, DialogInterface.OnClickListener okListener, String from) {
        if (from.equals(Constants.DENY)) {
            IOSAlertDialog dialog =  IOSAlertDialog.newInstance(
                    MapActivity.this,
                    getString(R.string.title_permission_dialog),
                    message,
                    getString(R.string.button_ok_permission_dialog),
                    getString(R.string.cancel1),
                    okListener,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    },
                    ContextCompat.getColor(MapActivity.this,R.color.app_color),
                    ContextCompat.getColor(MapActivity.this,R.color.coral),
                    true
            );
            dialog.show(getSupportFragmentManager(), "ios_dialog");;
        }else{
            IOSAlertDialog dialog =  IOSAlertDialog.newInstance(
                    MapActivity.this,
                    getString(R.string.title_permission_dialog),
                    message,
                    getString(R.string.button_setting_permission_dialog),
                    getString(R.string.cancel1),
                    okListener,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    },
                    ContextCompat.getColor(MapActivity.this,R.color.app_color),
                    ContextCompat.getColor(MapActivity.this,R.color.coral),
                    true
            );
            dialog.show(getSupportFragmentManager(), "ios_dialog");;
        }
        

    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
          return;
        }

        mFusedLocationClient.getLastLocation().addOnSuccessListener(MapActivity.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                // Got last known location. In some rare situations this can be null.
                LatLng latLng1 = new LatLng(location.getLatitude(),location.getLongitude());
                MapActivity.this.latLng = latLng1;
                setLocation(latLng1,true);
            }
        });
        }


        private  void setLocation(LatLng latLng, Boolean moveCamera){

            if (latLng != null) {
                // Logic to handle location object
                Log.d(TAG, "Latitude" + String.valueOf(latLng.latitude));
                Log.d(TAG, "Latitude" + String.valueOf(latLng.longitude));

                Geocoder geocoder = new Geocoder(MapActivity.this, Locale.getDefault());
                latLng = new LatLng(latLng.latitude, latLng.longitude);

                List<Address> addressList = null;
                try {
                    addressList = geocoder.getFromLocation(
                            latLng.latitude, latLng.longitude, 1);
                } catch (IOException | NullPointerException ioe) {
                    ioe.printStackTrace();
                }

                if (addressList != null && addressList.size() > 0) {
                    Address address = addressList.get(0);
                    Log.e(TAG, "Address data " + addressList.toString());

                    if (address.getThoroughfare() != null) {
                        binding.tvsearchLocation.setText(address.getThoroughfare() + "," + address.getAdminArea() + "," + address.getPostalCode() + "," + address.getCountryName());

                    } else {
                        binding.tvsearchLocation.setText(address.getAddressLine(0));
                    }

                    Log.i("Location", "LatLng: " + latLng);

                    android.util.Log.d(TAG, "setLocation: move camra "+ moveCamera);
                    if(moveCamera) {
                        marker.setPosition(latLng);
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f));
                    }
                } else {
                    //   Toast.makeText(MapActivity.this, "Location not found", Toast.LENGTH_SHORT).show();
                    getLocatoinUpdate();
                }
            }
        }



    private void getLocatoinUpdate() {

        if (ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
          return;
        }
        mFusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, null);

    }





}


//public class MapActivity extends LocationBaseActivity implements OnMapReadyCallback, View.OnClickListener {
//    private static final String TAG = "MapActivity";
//    private GoogleMap mGoogleMap;
//    private ActivityMapBinding binding;
//    private LatLng latLng;
//    private String address = "";
//    private Marker marker;
//    private MarkerOptions markerOptions;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        binding = DataBindingUtil.setContentView(MapActivity.this, R.layout.activity_map);
//        Log.d(TAG, "onCreate: StartActivity");
//        init();
//        binding.mapView.onCreate(savedInstanceState);
//        binding.mapView.getMapAsync(this);
//        setData();
//        setListeners();
//    }
//
//    private void init() {
//        setSupportActionBar(binding.toolbarMap);
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setDisplayShowHomeEnabled(true);
//            getSupportActionBar().setDisplayShowTitleEnabled(false);
//            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_white);
//        }
//    }
//
//    private void setData() {
//        binding.title.setText(R.string.share_location);
//    }
//
//    private void setListeners() {
//        binding.tvShare.setOnClickListener(this);
//    }
//
//    @Override
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.tvShare:
//                Intent intent = new Intent();
//                intent.putExtra(Constants.LATTITUDE, String.valueOf(latLng.latitude));
//                intent.putExtra(Constants.LONGITUDE, String.valueOf(latLng.longitude));
//                setResult(RESULT_OK, intent);
//                finish();
//                break;
//        }
//
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        binding.mapView.onResume();
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        binding.mapView.onPause();
//    }
//
//    @Override
//    public void onLocationUpdated(Location location) {
//        stopLocationUpdates();
//        latLng = new LatLng(location.getLatitude(), location.getLongitude());
//
//        // create marker
//        if (markerOptions != null) {
//            marker.remove();
//        }
//        markerOptions = new MarkerOptions().position(latLng).title(address);
//        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_filled));
//        marker = mGoogleMap.addMarker(markerOptions);
//        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
//    }
//
//    @Override
//    public void onLocationUpdateFailure() {
//
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        binding.mapView.onDestroy();
//    }
//
//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        binding.mapView.onSaveInstanceState(outState);
//    }
//
//
//    @Override
//    public void onLowMemory() {
//        super.onLowMemory();
//        binding.mapView.onLowMemory();
//    }
//
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mGoogleMap = googleMap;
//        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        if (item.getItemId() == android.R.id.home) {
//            finish();
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//}







