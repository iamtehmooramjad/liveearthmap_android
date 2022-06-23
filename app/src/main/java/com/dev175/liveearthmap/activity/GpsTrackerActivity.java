package com.dev175.liveearthmap.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.dev175.liveearthmap.R;
import com.dev175.liveearthmap.databinding.ActivityGpsTrackerBinding;
import com.dev175.liveearthmap.utils.Constants;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Objects;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class GpsTrackerActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    private static final String TAG = "GpsTrackerActivity";
    private static final int REQUEST_FINE_LOCATION = 82;

    //For Data binding
    private ActivityGpsTrackerBinding binding;

    //Location Request
    private LocationRequest mLocationRequest;

    private static final long UPDATE_INTERVAL = 10000;  /* 10 secs */
    private static final long FASTEST_INTERVAL = 5000; /* 5 sec */

    //Google Map
    private GoogleMap mMap;


    //Location Callback
    LocationCallback mLocationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGpsTrackerBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //Check GPS
        isGpsEnabled(GpsTrackerActivity.this);

        //Change Text of toolbar
        Objects.requireNonNull(getSupportActionBar()).setTitle(Constants.GPS_TRACKER);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.gpsTrackerMap);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        //Set Background of Cardview
        binding.bottomLayoutCv.setBackgroundResource(R.drawable.cardview_bg_round);

        //Click Listener
        onClickListener();
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                // do work here
                onLocationChanged(locationResult.getLastLocation());
            }
        };


    }

    ////////////////////////// Click Listener For Copy and Share //////////////////////////////////
    private void onClickListener() {

        //Copy
        binding.copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ClipboardManager manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("text", binding.latitude.getText() + "," + binding.longitude.getText());
                manager.setPrimaryClip(clipData);
                Toast.makeText(GpsTrackerActivity.this, "Copied", Toast.LENGTH_SHORT).show();
            }
        });

        //Share
        binding.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!binding.latitude.getText().equals("") && !binding.longitude.getText().equals("")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?q=" + binding.latitude.getText() + "," + binding.longitude.getText()));
                    startActivity(intent);
                }
            }
        });
    }

    // Trigger new location updates at interval
    protected void startLocationUpdates() {

        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest,mLocationCallback, Looper.myLooper());
    }




    @Override
    public void onLocationChanged(Location location) {
        try {
            binding.latitude.setText(String.valueOf(location.getLatitude()));
            binding.longitude.setText(String.valueOf(location.getLongitude()));
            Log.d(TAG, "onLocationChanged: "+location.getLatitude()+" "+location.getLongitude());
            Toast.makeText(this, location.getLatitude()+" "+location.getLongitude(), Toast.LENGTH_SHORT).show();
        }
        catch (Exception e)
        {
            Log.e(TAG, "onLocationChanged: "+e.getMessage() );
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        if (checkPermissions()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            mMap = googleMap;
            googleMap.setMyLocationEnabled(true);
            gotoLocation();
        }
    }

    private boolean checkPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestPermissions();
            return false;
        }
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_FINE_LOCATION);
    }

    public void gotoLocation() {
        // Get last known recent location using new Google Play Services SDK (v11+)
        FusedLocationProviderClient locationClient = getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // GPS location can be null if GPS is switched off
                        if (location != null) {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()), 18));                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("MapDemoActivity", "Error trying to get last GPS location");
                        e.printStackTrace();
                    }
                });
    }

    /////////////// Check GPS is Enabled or not & then let the user to enable it ///////////////
    void isGpsEnabled(Context context)
    {
        if(!Constants.isLocationEnabled(context))
        {
            new MaterialAlertDialogBuilder(context)
                    .setTitle("Enable GPS Location")
                    .setMessage("GPS Location is needed to get device location")
                    .setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    })
                    .show();

        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
        getFusedLocationProviderClient(this).removeLocationUpdates(mLocationCallback);

    }

}

