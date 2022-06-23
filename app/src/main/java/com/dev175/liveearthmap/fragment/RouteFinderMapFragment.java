package com.dev175.liveearthmap.fragment;

import android.Manifest;
import android.app.Dialog;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.dev175.liveearthmap.activity.AddressFinderActivity;
import com.dev175.liveearthmap.myinterface.ISelectRouteFinder;
import com.dev175.liveearthmap.R;
import com.dev175.liveearthmap.activity.RouteFinderActivity;
import com.dev175.liveearthmap.databinding.FragmentRouteFinderMapBinding;
import com.dev175.liveearthmap.utils.Constants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class RouteFinderMapFragment extends Fragment implements OnMapReadyCallback, SearchView.OnQueryTextListener, LocationListener,  GoogleMap.OnCameraIdleListener {

    //Request Codes For Permissions
    private static final int PERMISSION_REQUEST_CODE = 142;
    private static final int PLAY_SERVICES_ERROR_CODE = 132;

    //For Logs
    private static final String TAG = "RouteFinderMapFragment";

    //For Databinding
    private FragmentRouteFinderMapBinding binding;

    //For Device Location
    private FusedLocationProviderClient fusedLocationProviderClient;

    //To check Whether it is source address or destination address
    private boolean sourceType;

    //For Google Maps
    private GoogleMap mMap;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRouteFinderMapBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        setHasOptionsMenu(true);


        //Initialize FusedlocationProvider
        if (getContext()!=null)
        {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        }

        //Check Map Permissions
        initGoogleMap();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.routeFinderMap);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        //OnClickListener
        onClickListener();

        //Set Area and Address
//        getAndSetAddress();
        return view;
    }

    //////////// On Click Listener for pick location and Change Map Type ////////////////////////
    private void onClickListener() {
        //On Pick Location
        binding.pickLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = binding.address.getText().toString();

                try {
                    ISelectRouteFinder listener = (RouteFinderActivity) getActivity();
                    listener.pickAddress(address, sourceType);
                } catch (Exception e) {
                    Log.e(TAG, "onClick: " + e.getMessage());
                }
                //Reload current fragment
                RouteFinderFragment frg = (RouteFinderFragment) getActivity().getSupportFragmentManager().findFragmentByTag("addRouteFinderF");
                if (frg != null) {
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.detach(frg);
                    ft.attach(frg);
                    ft.commit();
                }
                Objects.requireNonNull(getActivity()).onBackPressed();
            }
        });

        //On Change Map Type
        binding.mapType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mMap.getMapType()) {
                    case GoogleMap.MAP_TYPE_NONE: {
                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        break;
                    }
                    case GoogleMap.MAP_TYPE_NORMAL: {
                        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                        break;
                    }
                    case GoogleMap.MAP_TYPE_SATELLITE: {
                        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                        break;
                    }
                    case GoogleMap.MAP_TYPE_TERRAIN: {
                        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                        break;
                    }
                    case GoogleMap.MAP_TYPE_HYBRID: {
                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        break;
                    }
                }
            }
        });
    }


    //////////////// Setting Data from RouterFinderFragment /////////////////////////////////////
    public void setData(boolean sourceType) {
        this.sourceType = sourceType;
    }

    ///////////////// Check Permissions and Play Services is available or not ////////////////////
    private void initGoogleMap() {
        //If Play Services are OK
        if (isServicesOk()) {
            //Check Location Permissions
            if (!checkLocationPermission()) {
                requestLocationPermissions();
            }
        }

    }

    ////////////////////////////// Check Location Permissions ////////////////////////////////////
    private boolean checkLocationPermission() {
        if (getContext()!=null)
        {
            return ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    ////////////////////////////// Request Location Permissions //////////////////////////////////
    private void requestLocationPermissions() {
        if (getContext()!=null)
        {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE);
                }
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

    ////////////////////////////// On Request Permissions Result /////////////////////////////////
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Permission not Granted", Toast.LENGTH_SHORT).show();
        }
    }

    /////////////////////////////////// On Map Ready //////////////////////////////////////////////
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Set On camera idle Listener
        mMap.setOnCameraIdleListener(this);

        //Check Permission
        if (getContext()!=null)
        {
            if (ActivityCompat.checkSelfPermission(getContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) !=
                            PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        //Get Gps Location
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                try
                {
                    LatLng cameraPos = new LatLng(location.getLatitude(), location.getLongitude());

                    //Get Address From location
                    getAndSetAddress(cameraPos);

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
                if (getContext()!=null)
                {
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(),
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                }


                //Set My location enabled and other map gestures
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setAllGesturesEnabled(true);
                mMap.getUiSettings().setCompassEnabled(false);
                mMap.getUiSettings().setTiltGesturesEnabled(true);

            }
        });
    }

    ////////////////////////// Get & Set Address From Latlng ///////////////////////////////////////////
    public void getAndSetAddress(LatLng location) {
        try {
            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());

            List<Address> addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1);
            Address address = addresses.get(0);

            binding.areaName.setText(address.getFeatureName());
            binding.address.setText(address.getAddressLine(0));
        }
        catch (IOException e) {
            Log.e(TAG, "getAddress: " + e.getMessage());
        }
    }

    ////////////////////////////// Create Menu Options //////////////////////////////////////////
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu,@NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.toolbar_search_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_IF_ROOM);

        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(this);
    }

    ////////////////////////////// On Query Text Change ////////////////////////////////////////
    @Override
    public boolean onQueryTextChange(String query) {
        return false;
    }

    ////////////////////////////// On Query Text Submit :  /////////////////////////////////////
    @Override
    public boolean onQueryTextSubmit(String query) {
        geoLocate(query);
        return false;
    }

    //////////////////////////// Get Query Text and find its address ///////////////////////////
    private void geoLocate(String location) {
       try {
           Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
           List<Address> addressList =  geocoder.getFromLocationName(location,1);
            if (addressList.size()>0)
            {
                Address address = addressList.get(0);
                gotoLocation(address.getLatitude(),address.getLongitude());
                if (mMap!=null)
                {
                    binding.areaName.setText(address.getFeatureName());
                    binding.address.setText(address.getAddressLine(0));
                    mMap.addMarker(new MarkerOptions().position(new LatLng(address.getLatitude(),address.getLongitude())));
                    Log.d(TAG, "geoLocate: Addressline = "+address.getAddressLine(0));
                    Log.d(TAG, "geoLocate: AdminArea   = "+address.getAdminArea());
                    Log.d(TAG, "geoLocate: Locality    = "+address.getLocality());
                    Log.d(TAG, "geoLocate: FeatureName = "+address.getFeatureName());
                    Log.d(TAG, "geoLocate: Premises    = "+address.getPremises());
                }
            }
       }
       catch (IOException e)
       {
           Log.e(TAG, "geoLocate: "+e.getMessage() );
       }
    }


    /////////////////////// Move Camera to the Location with LatLng ////////////////////////////
    private void gotoLocation(double latitude, double longitude) {
        LatLng latLng = new LatLng(latitude,longitude);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng,18);
        mMap.animateCamera(cameraUpdate);

    }


    ////////////////////////////// On Location Changed //////////////////////////////////////////
    @Override
    public void onLocationChanged(@NonNull Location location) {
        try
        {
            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            Address address = addresses.get(0);
            binding.areaName.setText(address.getFeatureName());
            binding.address.setText(address.getAddressLine(0));
        }
        catch (IOException e)
        {
            Log.e(TAG, "getAddress: "+e.getMessage() );
        }

    }



    ////////////////////////////// On Camera Idle: Get Camera Latlng ////////////////////////////
    @Override
    public void onCameraIdle() {
        try {
            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(mMap.getCameraPosition().target.latitude, mMap.getCameraPosition().target.longitude, 1);
            //Set Address & Area

            Address address = addresses.get(0);
            binding.areaName.setText(address.getFeatureName());
            binding.address.setText(address.getAddressLine(0));

            Log.d(TAG, "onCameraIdle: "+addresses.get(0).getFeatureName());
            Log.d(TAG, "onCameraIdle: "+addresses.get(0).getAdminArea());
            Log.d(TAG, "onCameraIdle: "+addresses.get(0).getAddressLine(0));
            Log.d(TAG, "onCameraIdle: "+addresses.get(0).getLocality());
        }
        catch (Exception e)
        {
            Log.e(TAG, "getAddress: "+e.getMessage() );
        }

    }


}
