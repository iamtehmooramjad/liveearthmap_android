package com.dev175.liveearthmap.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.dev175.liveearthmap.R;
import com.dev175.liveearthmap.databinding.ActivityAddressFinderBinding;
import com.dev175.liveearthmap.databinding.ActivityGpsTrackerBinding;
import com.dev175.liveearthmap.utils.Constants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class AddressFinderActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener, GoogleMap.OnCameraIdleListener {

    //Tag for Logcats
    private static final String TAG = "GpsTrackerActivity";

    //For Data Binding
    private ActivityAddressFinderBinding binding;

    //Request Codes
    private static final int PERMISSION_REQUEST_CODE = 234;
    private static final int PLAY_SERVICES_ERROR_CODE = 159;

    //Google Map Object
    private GoogleMap mMap;

    //FusedLocationProvider to get Location
    private FusedLocationProviderClient fusedLocationProviderClient;
    private FirebaseAnalytics mfirebaseanlytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddressFinderBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);



        mfirebaseanlytics = FirebaseAnalytics.getInstance(AddressFinderActivity.this);
        mfirebaseanlytics.setUserProperty("Address Finder Activity","Address Finder Activity");


        //Check GPS Enable
        isGpsEnabled(this);

        //Change Text of toolbar
        Objects.requireNonNull(getSupportActionBar()).setTitle(Constants.ADDRESS_FINDER);

        //FusedLocationProvider
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(AddressFinderActivity.this);

        //Check Map Permissions
        initGoogleMap();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.gpsTrackerMap);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        //Set Background of Card view
        binding.bottomLayoutCv.setBackgroundResource(R.drawable.cardview_bg_round);

        //On Click Listener
        onClickListener();
    }
Bundle bundle;
    ////////////////////////// Click Listener For Copy and Share //////////////////////////////////
    private void onClickListener() {

        //Copy
        binding.copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("text", binding.fullAddress.getText());
                manager.setPrimaryClip(clipData);
                Toast.makeText(AddressFinderActivity.this, "Copied", Toast.LENGTH_SHORT).show();

                bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "Address Finder Activity");
                bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "copy Finder Click");
                mfirebaseanlytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle);

            }
        });

        //Share
        binding.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "Address Finder Activity");
                bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "share Finder Click");
                mfirebaseanlytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle);

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, binding.fullAddress.getText());
                startActivity(Intent.createChooser(intent, "Share via"));
            }
        });
    }

    /////////////////////////////////// On Map Ready //////////////////////////////////////////////
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Set On camera idle Listener
        mMap.setOnCameraIdleListener(this);

        //Check Permission
        if (ActivityCompat.checkSelfPermission(AddressFinderActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(AddressFinderActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        //Get Gps Location
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
               try
                {
                    LatLng cameraPos = new LatLng(location.getLatitude(), location.getLongitude());

                    //Get Address From location
                    getAddress(cameraPos);

                    //Zoom Level of Camera
                    float zoomLevel = (float) 13.0;

                    //Move Map Camera
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cameraPos, zoomLevel));
                }
                catch (Exception e)
                {
                    Log.e(TAG, "onSuccess: "+e.getMessage() );
                }

                    //Check Location Permission
                  if (ActivityCompat.checkSelfPermission(AddressFinderActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(AddressFinderActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                      return;
                  }

                  //Set My location enabled and other map gestures
                  mMap.setMyLocationEnabled(true);
                  mMap.getUiSettings().setAllGesturesEnabled(true);
                  mMap.getUiSettings().setCompassEnabled(false);
                  mMap.getUiSettings().setTiltGesturesEnabled(true);

            }
        });
    }


    ////////////////////////// Get Address From Latlng ///////////////////////////////////////////
    public void getAddress(LatLng location) {
        Geocoder geocoder = new Geocoder(AddressFinderActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1);
            Address obj = addresses.get(0);
            String address = obj.getAddressLine(0);
            binding.fullAddress.setText(address);
        }
        catch (IOException e)
        {
            Log.e(TAG, "getAddress: "+e.getMessage() );
        }
    }

    ////////////////////////// Set Address to TextField /////////////////////////////////////////
    private void setAddress(Address address) {
        if(address!=null)
        {
            if (address.getAddressLine(0)!=null)
            {
                binding.fullAddress.setText(address.getAddressLine(0));
            }

        }
    }

    ///////////////// Check Permissions and Play Services is available or not ////////////////////
    private void initGoogleMap() {
        //If Playservices are available
        if (isServicesOk()) {

            //Check Location Permissions
            if (checkLocationPermission())
            {
                Log.d(TAG, "initGoogleMap: Ready to Map");
            }
            else
            {
                //Request Location Permissions
                requestLocationPermissions();
            }
        }

    }

    ////////////////////////////// Check Location Permissions ////////////////////////////////////
    private boolean checkLocationPermission() {

        return ContextCompat.checkSelfPermission(AddressFinderActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&   ContextCompat.checkSelfPermission(AddressFinderActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    ////////////////////////////// Request Location Permissions //////////////////////////////////
    private void requestLocationPermissions() {
        if (ContextCompat.checkSelfPermission(AddressFinderActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE);
            }
        }
    }

    ////////////////////////////// Check Play Services are Available or not //////////////////////
    private boolean isServicesOk() {
        GoogleApiAvailability googleApi = GoogleApiAvailability.getInstance();
        int result = googleApi.isGooglePlayServicesAvailable(AddressFinderActivity.this);
        //result can be:
        //1.Everything is ok
        //2.There is some problem but can be fixed
        //3. Problem can not be solved
        if (result == ConnectionResult.SUCCESS) {
            return true;
        } else if (googleApi.isUserResolvableError(result)) {
            Dialog dialog = googleApi.getErrorDialog(AddressFinderActivity.this, result, PLAY_SERVICES_ERROR_CODE, task ->
                    Toast.makeText(AddressFinderActivity.this, "Dialog is Cancelled by the User", Toast.LENGTH_SHORT).show());
            dialog.show();
        } else {
            Toast.makeText(AddressFinderActivity.this, "Play Services are required by this application", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    ////////////////////////////// On Request Permissions Result /////////////////////////////////
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(AddressFinderActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(AddressFinderActivity.this, "Permission not Granted", Toast.LENGTH_SHORT).show();
        }
    }


    ////////////////////////////// On Location Changed //////////////////////////////////////////
    @Override
    public void onLocationChanged(@NonNull Location location) {

        Geocoder geocoder = new Geocoder(AddressFinderActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            setAddress(addresses.get(0));
        }
        catch (IOException e)
        {
            Log.e(TAG, "getAddress: "+e.getMessage() );
        }
    }


    ////////////////////////////// On Camera Idle: Get Camera Latlng ////////////////////////////
    @Override
    public void onCameraIdle() {
        Geocoder geocoder = new Geocoder(AddressFinderActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(mMap.getCameraPosition().target.latitude, mMap.getCameraPosition().target.longitude, 1);
           if (addresses!=null)
           {
               setAddress(addresses.get(0));
           }
        }
        catch (Exception e)
        {
            Log.e(TAG, "getAddress: "+e.getMessage() );
        }

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

}

