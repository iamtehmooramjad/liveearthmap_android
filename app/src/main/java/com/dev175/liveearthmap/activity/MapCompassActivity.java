package com.dev175.liveearthmap.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import com.dev175.liveearthmap.R;
import com.dev175.liveearthmap.databinding.ActivityMapCompassBinding;
import com.dev175.liveearthmap.utils.Constants;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MapCompassActivity extends AppCompatActivity implements SensorEventListener, OnMapReadyCallback, LocationListener, GoogleMap.OnCameraIdleListener {

    //For Logs
    private static final String TAG = "MapCompassActivity";

    //For Compass
    private final float[] mGravity = new float[3];
    private final float[] mGeomagnetic = new float[3];
    private float correctAzimuth = 0f;
    private SensorManager mSensorManager;

    //For View Binding
    private ActivityMapCompassBinding binding;

    //For Google Maps
    private static final int PERMISSION_REQUEST_CODE = 198;
    private static final int PLAY_SERVICES_ERROR_CODE = 199;
    private GoogleMap mMap;
    boolean mLocationPermissionGranted;

    //For Device's Location
    private FusedLocationProviderClient fusedLocationProviderClient;

    private UnifiedNativeAd nativeAd;
    private void populateUnifiedNativeAdView(UnifiedNativeAd nativeAd, UnifiedNativeAdView adView) {

        adView.setMediaView((MediaView) adView.findViewById(R.id.ad_media));
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        adView.getMediaView().setMediaContent(nativeAd.getMediaContent());

        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }


        adView.setNativeAd(nativeAd);


    }
    private void refreshAd() {
        AdLoader.Builder builder = new AdLoader.Builder(this, getString(R.string.ADMOB_NATIVE_ADVANCED));
        builder.forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
            // OnUnifiedNativeAdLoadedListener implementation.
            @Override
            public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                // You must call destroy on old ads when you are done with them,
                // otherwise you will have a memory leak.
                if (nativeAd != null) {
                    nativeAd.destroy();
                }
                nativeAd = unifiedNativeAd;
                FrameLayout frameLayout =
                        findViewById(R.id.fl_adplaceholder);
                UnifiedNativeAdView adView = (UnifiedNativeAdView) getLayoutInflater()
                        .inflate(R.layout.native_admob_advanced, null);
                populateUnifiedNativeAdView(unifiedNativeAd, adView);
                frameLayout.removeAllViews();
                frameLayout.addView(adView);
            }

        });

        AdLoader adLoader = builder.withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
            }

        }).build();
        adLoader.loadAd(new AdRequest.Builder().build());

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapCompassBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        refreshAd();

        //Set Title
        Objects.requireNonNull(getSupportActionBar()).setTitle("Map Compass");

        //Set Cardview Background
        binding.bottomLayoutCv.setBackgroundResource(R.drawable.cardview_bg_round);

        //Initializing Sensor Manager
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

        //Check GPS Enabled or not
        isGpsEnabled(this);

        initGoogleMap();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapCompass);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        //Initialize FusedLocation Provider
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MapCompassActivity.this);

        //OnClickListener
        onClickListener();
    }
    ///////////////////// On Click Listener For Copy and Share ////////////////////
    private void onClickListener() {

        binding.copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("text", binding.fullAddress.getText());
                manager.setPrimaryClip(clipData);
                Toast.makeText(MapCompassActivity.this, "Copied", Toast.LENGTH_SHORT).show();
            }
        });

        binding.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, binding.fullAddress.getText());
                startActivity(Intent.createChooser(intent, "Share via"));
            }
        });
    }

    ///////////////////// Register Listener ///////////////////////////////////////
    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this,mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this,mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME);
    }

    ///////////////////// Unregister Listener ///////////////////////////////////////
    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    ///////////////////// Sensor Event Listener ///////////////////////////////////////
    @Override
    public void onSensorChanged(SensorEvent event) {
        final float alpha = 0.97f;
        synchronized (this){
            if (event.sensor.getType()==Sensor.TYPE_ACCELEROMETER)
            {
                mGravity[0] = alpha*mGravity[0]+(1-alpha)*event.values[0];
                mGravity[1] = alpha*mGravity[1]+(1-alpha)*event.values[1];
                mGravity[2] = alpha*mGravity[2]+(1-alpha)*event.values[2];
            }
            if (event.sensor.getType()==Sensor.TYPE_MAGNETIC_FIELD)
            {
                mGeomagnetic[0] = alpha*mGeomagnetic[0]+(1-alpha)*event.values[0];
                mGeomagnetic[1] = alpha*mGeomagnetic[1]+(1-alpha)*event.values[1];
                mGeomagnetic[2] = alpha*mGeomagnetic[2]+(1-alpha)*event.values[2];
            }

            float[] R = new float[9];
            float[] I = new float[9];
            boolean success = SensorManager.getRotationMatrix(R,I,mGravity,mGeomagnetic);
            if (success)
            {
                float[] orientation = new float[3];
                SensorManager.getOrientation(R,orientation);
                float azimuth = (float) Math.toDegrees(orientation[0]);
                azimuth = (azimuth +360)%360;

                Animation anim = new RotateAnimation(-correctAzimuth,-azimuth,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
                correctAzimuth = azimuth;
                int correct = (int) correctAzimuth;
                anim.setDuration(500);
                anim.setRepeatCount(0);
                anim.setFillAfter(true);
                binding.compass.startAnimation(anim);
                String degree = String.valueOf(correct)+ (char) 0x00B0;
                binding.degree.setText(degree);

            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    ///////////////////// Initialize Google Maps //////////////////////////////////////////
    private void initGoogleMap() {
        if (isServicesOk()) {
            if (!checkLocationPermission()) {
                requestLocationPermissions();
            }
        }

    }

    ///////////////////// Check Location Permissions //////////////////////////////////////
    private boolean checkLocationPermission() {

        return ContextCompat.checkSelfPermission(MapCompassActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&   ContextCompat.checkSelfPermission(MapCompassActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    ///////////////////// Request location Permissions ////////////////////////////////////
    private void requestLocationPermissions() {
        if (ContextCompat.checkSelfPermission(MapCompassActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE);
            }
        }
    }

    ///////////////////// Check PlayServices are available or not /////////////////////////
    private boolean isServicesOk() {
        GoogleApiAvailability googleApi = GoogleApiAvailability.getInstance();
        int result = googleApi.isGooglePlayServicesAvailable(MapCompassActivity.this);
        //result can be:
        //1.Everything is ok
        //2.There is some problem but can be fixt
        //3. Problem can not be solved
        if (result == ConnectionResult.SUCCESS) {
            return true;
        } else if (googleApi.isUserResolvableError(result)) {
            Dialog dialog = googleApi.getErrorDialog(MapCompassActivity.this, result, PLAY_SERVICES_ERROR_CODE, task ->
                    Toast.makeText(MapCompassActivity.this, "Dialog is Cancelled by the User", Toast.LENGTH_SHORT).show());
            dialog.show();
        } else {
            Toast.makeText(MapCompassActivity.this, "Play Services are required by this application", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    ///////////////////// On Request permissions Result ////////////////////////////////////
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            //Locations Permission
            mLocationPermissionGranted = true;
            Toast.makeText(MapCompassActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MapCompassActivity.this, "Permission not Granted", Toast.LENGTH_SHORT).show();
        }
    }

    ///////////////////// On Google Map Ready /////////////////////////////////////////////
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnCameraIdleListener(this);

        if (ActivityCompat.checkSelfPermission(MapCompassActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapCompassActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                LatLng cameraPos = null;
                try
                {
                    cameraPos = new LatLng(location.getLatitude(), location.getLongitude());
                    getAddress(cameraPos);
                }
                catch (Exception e)
                {
                    Log.e(TAG, "onSuccess: "+e.getMessage() );
                }
                float zoomLevel = (float) 13.0;

                if (ActivityCompat.checkSelfPermission(MapCompassActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapCompassActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setAllGesturesEnabled(true);
                mMap.getUiSettings().setCompassEnabled(true);
                mMap.getUiSettings().setTiltGesturesEnabled(true);
                if (location!=null)
                {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cameraPos, zoomLevel));
                }
                else {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(48.858093,2.294694), zoomLevel));

                }

            }
        });
    }

    ///////////////////// Get Address from LatLng ///////////////////////////////////////
    public void getAddress(LatLng location) {
        Geocoder geocoder = new Geocoder(MapCompassActivity.this, Locale.getDefault());
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

    ///////////////////// Set Address to textfield //////////////////////////////////////
    private void setAddress(Address address) {
        if(address!=null)
        {
            if (address.getAddressLine(0)!=null)
            {
                binding.fullAddress.setText(address.getAddressLine(0));
            }

        }
    }

    ///////////////////// On Location Changed //////////////////////////////////////////
    @Override
    public void onLocationChanged(Location location) {
        Geocoder geocoder = new Geocoder(MapCompassActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            setAddress(addresses.get(0));
        }
        catch (IOException e)
        {
            Log.e(TAG, "getAddress: "+e.getMessage() );
        }
    }

    ///////////////////// On Camera Idle ///////////////////////////////////////////////
    @Override
    public void onCameraIdle() {
        Geocoder geocoder = new Geocoder(MapCompassActivity.this, Locale.getDefault());
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

    //// Check GPS is Enabled or Not and Let the user to enable it /////////////////////
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