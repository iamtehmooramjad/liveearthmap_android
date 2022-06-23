package com.dev175.liveearthmap.fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.dev175.liveearthmap.R;
import com.dev175.liveearthmap.databinding.FragmentEarthMapBinding;
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
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class EarthMapFragment extends Fragment implements OnMapReadyCallback {

    //For Logs
    private static final String TAG = "EarthFragment";

    //Request Codes For Map Permissions
    private static final int PERMISSION_REQUEST_CODE = 198;
    private static final int PLAY_SERVICES_ERROR_CODE = 199;

    //Google Map Object
    private GoogleMap mMap;

    //For Data binding
    private FragmentEarthMapBinding binding;

    //FusedLocationProvider to get Location
    private FusedLocationProviderClient fusedLocationProviderClient;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEarthMapBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        //Check GPS Enable
        isGpsEnabled(getContext());

        //Initialize FusedLocationProvider
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

        //Change Toolbar Title
        changeToolbarTitle();

        //Check Map Permissions & Play Services Availability
        initGoogleMap();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        //OnClick Listener
        onClickListener();


        return view;
    }

    //////////////////// Change Map Types, Enable/Disable Traffic & Share Address //////////////
    private void onClickListener() {

        //Hybrid Map
        binding.normalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            }
        });

        //Satellite Map
        binding.satelliteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            }
        });

        //Terrain Map
        binding.terrainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            }
        });

        //Night Map
        binding.nightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // Customise the styling of the base map using a JSON object defined
                    // in a raw resource file.
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    boolean success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.mapstyle));

                    if (!success) {
                        Log.e(TAG, "Style parsing failed.");
                    } else {
                        Log.d(TAG, "setNightStyle: Success");
                    }
                } catch (Resources.NotFoundException e) {
                    Log.e(TAG, "Can't find style. Error: ", e);
                }
            }
        });

        //Toggle Traffic
        binding.trafficCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mMap.setTrafficEnabled(true);
                } else {
                    mMap.setTrafficEnabled(false);
                }
            }
        });

        //Share Address
        binding.shareCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (getContext()!=null)
                    {
                        //Check Location Permission
                        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(),
                                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                    }

                    //Get GPS Location
                    fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                           try {
                               LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                               shareAddress(latLng);
                           }
                           catch (Exception e)
                           {
                               Log.e(TAG, "onSuccess: "+e.getMessage() );
                           }
                        }
                    });

                }
                catch (Exception e)
                {
                    Log.e(TAG, "onClick: "+e.getMessage() );
                }
            }
        });

    }

    //////////////////////////// Change Toolbar Title //////////////////////////////////////////
    private void changeToolbarTitle() {
        try {
            getActivity().setTitle("Earth Map");
        }
        catch (Exception e)
        {
            Log.e(TAG, "changeToolbarTitle: "+e.getMessage() );
        }
    }

    ///////////////// Check Permissions and Play Services is available or not ////////////////////
    private void initGoogleMap() {
        if (isServicesOk()) {
            if (!checkLocationPermission()) {
                requestLocationPermissions();
            }
        }

    }

    ////////////////////////////// Check Location Permissions ////////////////////////////////////
    private boolean checkLocationPermission() {

        if (getContext()!=null)
        {
           return ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&   ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    ////////////////////////////// Request Location Permissions //////////////////////////////////
    private void requestLocationPermissions() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE);
            }
        }
    }

    ////////////////////////////// Check Play Services are Available or not //////////////////////
    private boolean isServicesOk() {
        GoogleApiAvailability googleApi = GoogleApiAvailability.getInstance();
        int result = googleApi.isGooglePlayServicesAvailable(getContext());
        //result can be:
        //1.Everything is ok
        //2.There is some problem but can be fixt
        //3. Problem can not be solved
        if (result == ConnectionResult.SUCCESS) {
            return true;
        } else if (googleApi.isUserResolvableError(result)) {
            Dialog dialog = googleApi.getErrorDialog(getActivity(), result, PLAY_SERVICES_ERROR_CODE, task ->
                    Toast.makeText(getContext(), "Dialog is Cancelled by the User", Toast.LENGTH_SHORT).show());
            dialog.show();
        } else {
            Toast.makeText(getContext(), "Play Services are required by this application", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    ////////////////////////////// On Request Permissions Result ////////////////////////////////
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Permission not Granted", Toast.LENGTH_SHORT).show();
        }
    }


    ////////////////////////////// On Map Ready ////////////////////////////////////////////////
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Lahore and move the camera
        final LatLng locationLatLng = new LatLng(51.752022, -1.257677);

        mMap.addMarker(new MarkerOptions()
                .position(locationLatLng)
                .title("Marker"));

        float zoomLevel = (float) 13.0;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        if (getContext()!=null)
        {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }


        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setTiltGesturesEnabled(true);

           fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
               @Override
               public void onSuccess(Location location) {
                   LatLng currentLatLng = null;
                   try
                   {
                       currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                   }
                   catch (Exception e)
                   {
                       Log.e(TAG, "onSuccess: "+e.getMessage() );
                   }

                   if (location!=null)
                   {
                       mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, zoomLevel));
                   }
                   else {
                       mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationLatLng, zoomLevel));

                   }
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
                            Objects.requireNonNull(getActivity()).finish();
                        }
                    })
                    .show();

        }
    }

    /////////////// Get LatLng and share Address ///////////////////////////////////////////////
    private void shareAddress(LatLng cameraPos) {

        try {
            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(cameraPos.latitude, cameraPos.longitude, 1);
            if (addresses.get(0)!=null)
            {
                Address obj = addresses.get(0);
                String address = obj.getAddressLine(0);

                //Intent to share Address
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, address);
                startActivity(Intent.createChooser(intent, "Share via"));
            }

        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}