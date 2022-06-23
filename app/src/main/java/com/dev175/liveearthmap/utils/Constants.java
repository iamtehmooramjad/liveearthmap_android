package com.dev175.liveearthmap.utils;

import android.content.Context;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

public class Constants {

    //Home items
    public static final String ROUTE_FINDER="Route Finder";
    public static final String GPS_TRACKER="GPS Tracker";
    public static final String ADDRESS_FINDER="Address Finder";
    public static final String FAMOUS_PLACES="Famous Places";
    public static final String COMPASS="Compass";
    public static final String GPS_MAP_CAMERA="GPS Map Camera";
    public static final String SAVED_PLACES="Saved Places";
    public static final String EARTH_MAP="Earth Map";
    public static final String VOICE_NAVIGATION="Voice Navigation";

    //Compass Types
    public static final String STANDARD_COMPASS = "Standard Compass";
    public static final String MAP_COMPASS = "Map Compass";

    ///////////////////////////////// Check GPS is Enabled or not ///////////////////////////////
    public static Boolean isLocationEnabled(Context context)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        // This is new method provided in API 28
            LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            return lm.isLocationEnabled();
        }
        else
        {
            // This is Deprecated in API 28
            int mode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE, Settings.Secure.LOCATION_MODE_OFF);
            return  (mode != Settings.Secure.LOCATION_MODE_OFF);

        }
    }


}
