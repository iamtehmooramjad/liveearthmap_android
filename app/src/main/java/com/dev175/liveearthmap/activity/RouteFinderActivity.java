package com.dev175.liveearthmap.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.dev175.liveearthmap.myinterface.ISelectRouteFinder;
import com.dev175.liveearthmap.R;
import com.dev175.liveearthmap.databinding.ActivityRouteFinderBinding;
import com.dev175.liveearthmap.fragment.RouteFinderFragment;

import com.dev175.liveearthmap.model.Address;
import com.dev175.liveearthmap.utils.Constants;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSize;
import com.google.android.gms.ads.AdListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Objects;


public class RouteFinderActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener ,ISelectRouteFinder{

    //For Logs
    private static final String TAG = "RouteFinderActivity";

    //Fragment manager
    private FragmentManager manager;

    //For Source and Destination Address
    private Address objAddress;
    private FirebaseAnalytics mfirebaseanlytics;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityRouteFinderBinding binding = ActivityRouteFinderBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        mfirebaseanlytics = FirebaseAnalytics.getInstance(RouteFinderActivity.this);
        mfirebaseanlytics.setUserProperty("RouteFinderActivity ","RouteFinderActivity ");


        com.facebook.ads.AdView adView = new com.facebook.ads.AdView(RouteFinderActivity.this, getString(R.string.fb_placement_banner), AdSize.BANNER_HEIGHT_50);
        binding.bannerContainer.addView(adView);
        adView.loadAd(adView.buildLoadAdConfig().withAdListener(new com.facebook.ads.AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {


            }

            @Override
            public void onAdLoaded(Ad ad) {



            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {


            }
        }).build());






        //Check GPS Enable
        isGpsEnabled(this);

        //Initialize Fragment Manager
        manager = getSupportFragmentManager();

        //Add BackStackListner
        manager.addOnBackStackChangedListener(this);

        //Initialize Object of Addresss
        objAddress = new Address();

        //Change Toolbar title
        changeToolbarTitle();

        addRouteFinderFragment();
    }


    ////////////////////// Change Toolbar title //////////////////////////////////////////////////
    private void changeToolbarTitle() {
        try {
            Objects.requireNonNull(getSupportActionBar()).setTitle(Constants.ROUTE_FINDER);
        }
        catch (Exception e)
        {
            Log.e(TAG, "changeToolbarTitle: "+e.getMessage() );
        }
    }

Bundle bundle;
    ////////////////////// Add Route Finder Fragment ////////////////////////////////////////////
    private void addRouteFinderFragment()
    {

        bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "Route Finder Activity");
        bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "RouteFinderFragment Click");
        mfirebaseanlytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle);

        FragmentTransaction transaction = manager.beginTransaction();
        RouteFinderFragment fragment = new RouteFinderFragment();
        transaction.add(R.id.routeFinderContainer,fragment,"addRouteFinderF");
        transaction.addToBackStack("addRouteFinderF");
        transaction.commit();
    }


    ////////////////////// Back Stack Listener ///////////////////////////////////////////////////
    @Override
    public void onBackStackChanged() {
        int length = manager.getBackStackEntryCount();
        StringBuilder msg = new StringBuilder("");
        for (int i=length-1;i>=0;i--)
        {
            msg.append("Index ").append(i).append(" : ");
            msg.append(manager.getBackStackEntryAt(i).getName());
            msg.append("\n");
        }
        Log.i(TAG, "\n" + msg.toString() + "\n");
    }


    ////////////////////// On Back Pressed /////////////////////////////////////////////////////
    @Override
    public void onBackPressed() {

        if (manager.getBackStackEntryCount()>1)
        {
            manager.popBackStack();
        }
        else if (manager.getBackStackEntryCount()==1)
        {
            manager.popBackStack();
            super.onBackPressed();
        }
    }

    ////////////////////// On Pick Address :  save it in objAddress /////////////////////////////
    /////////////// false means Source Address : true means Destination Address /////////////////
    @Override
    public void pickAddress(String address, boolean source) {
        if (source)
        {
            objAddress.setDestinationAddress(address);

        }
        else {
            objAddress.setSourceAddress(address);
        }

    }

    ////////////////////// Get Object:  (From Route Finder Fragment) ////////////////////////////
    public Address getObjAddress()
    {
        return this.objAddress;
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

