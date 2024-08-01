package com.maktoday.views.map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.maktoday.utils.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.maktoday.R;
import com.maktoday.databinding.FragmentMapBinding;
import com.maktoday.model.SearchMaidModel;
import com.maktoday.utils.Constants;
import com.maktoday.utils.GeneralFunction;
import com.maktoday.utils.Prefs;
import com.maktoday.views.confirmbook.ConfirmBookFragment;

/**
 * Created by cbl81 on 1/11/17.
 */

public class MapFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener {

    private static final int LOCATION_DIALOG_FRAGMENT = 2;
    private FragmentMapBinding binding;
    private MapView mapView;
    private GoogleMap mGoogleMap;
    private final int PLACE_PICKER_REQUEST = 1;
    private LatLng latLng;
    private String address = "";
    private Marker marker;
    private static final String TAG = "MapFragment";

    private SearchMaidModel searchMaidModel;

    public static MapFragment newInstance(SearchMaidModel searchMaidModel) {

        Bundle args = new Bundle();
        MapFragment fragment = new MapFragment();
        args.putParcelable(Constants.SEARCH_MAID_DATA, searchMaidModel);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchMaidModel = getArguments().getParcelable(Constants.SEARCH_MAID_DATA);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        binding = FragmentMapBinding.inflate(inflater, container, false);

        Log.d(TAG, "onCreateView: StartActivity");
        mapView = binding.mapView;
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(MapFragment.this); //this is important

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        init();
        setData();
        setListeners();
    }

    /**
     * intialise data
     */
    private void init() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(binding.toolbarMap);
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getSupportActionBar().setDisplayShowHomeEnabled(true);
            activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
            activity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_white);
        }

    }

    /**
     * set data on views
     */
    private void setData() {
        /*
        LocationFragment locationFragment=new LocationFragment();
        locationFragment.setTargetFragment(this, LOCATION_DIALOG_FRAGMENT);
        locationFragment.show(getActivity().getSupportFragmentManager(),"Location");*/

        if (latLng == null) {
            showLocationDialog();
        }
        binding.title.setVisibility(View.GONE);
        binding.tvLocationName.setVisibility(View.VISIBLE);

        if (marker == null) {
            binding.tvContinue.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorBlack40));
            binding.tvContinue.setOnClickListener(null);
        } else {
            binding.tvContinue.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.skyBlue));
            binding.tvContinue.setOnClickListener(this);
        }
    }

    /**
     * set click listener
     */
    private void setListeners() {
        binding.tvLocationName.setOnClickListener(this);
        binding.tvContinue.setOnClickListener(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);

        if (latLng != null) {
            // create marker
            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(address);
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_green));
            marker = mGoogleMap.addMarker(markerOptions);
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem search = menu.findItem(R.id.action_simpleSearch);
        search.setIcon(ContextCompat.getDrawable(getContext(), R.drawable.ic_search_white));
        menu.getItem(0).setVisible(false);
        menu.getItem(1).setVisible(true);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            case R.id.action_simpleSearch:
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    Intent intent1 = builder.build(getActivity());
                    intent1.putExtra("primary_color", ContextCompat.getColor(getActivity(), R.color.appBlueColor));
                    intent1.putExtra("primary_color_dark", ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));

                    startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case PLACE_PICKER_REQUEST:
                    Place place = PlacePicker.getPlace(getActivity(), data);
                    address = place.getAddress().toString();
                    binding.tvLocationName.setText(address);
                    latLng = place.getLatLng();


                    Prefs.with(getContext()).save(Constants.MAP_ADDRESS, address);
                    Prefs.with(getContext()).save(Constants.MAP_LATLNG, latLng);
                    if (marker != null) {
                        marker.remove();
                    }

                    // create marker
                    MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(address);
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_filled));
                    marker = mGoogleMap.addMarker(markerOptions);
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
                    if (marker == null) {
                        binding.tvContinue.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorBlack40));
                        binding.tvContinue.setOnClickListener(null);
                    } else {
                        binding.tvContinue.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.skyBlue));
                        binding.tvContinue.setOnClickListener(this);
                    }

                    break;


            }
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvLocationName:

                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }

                break;

            case R.id.tvContinue:

                if (address != null && !address.isEmpty()) {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                            .replace(R.id.frameLayout, ConfirmBookFragment.newInstance(searchMaidModel)).addToBackStack("ConfirmBookFragment").commit();
                } else {
                    GeneralFunction.showSnackBar(getContext(), binding.parent, getString(R.string.select_location_again));
                }
                break;
        }
    }

    public void showLocationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_location_popup, null);
        ImageView iv = view.findViewById(R.id.ivClose);
        TextView tvEnterLocality = view.findViewById(R.id.tvEnterLocality);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCancelable(false);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        tvEnterLocality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    Intent intent1 = builder.build(getActivity());
                    intent1.putExtra("primary_color", ContextCompat.getColor(getActivity(), R.color.appBlueColor));
                    intent1.putExtra("primary_color_dark", ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));

                    startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        });

    }
}
