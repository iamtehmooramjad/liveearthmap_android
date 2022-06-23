package com.dev175.liveearthmap.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.dev175.liveearthmap.R;
import com.dev175.liveearthmap.databinding.ActivityFamousPlacesBinding;
import com.dev175.liveearthmap.myinterface.IPlaceClickListener;
import com.dev175.liveearthmap.adapter.PlaceAdapter;
import com.dev175.liveearthmap.model.Place;
import com.dev175.liveearthmap.utils.Constants;
import com.dev175.liveearthmap.utils.DataSource;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSize;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Objects;

public class FamousPlacesActivity extends AppCompatActivity implements IPlaceClickListener , SearchView.OnQueryTextListener {

    //Place Adapter for places with their Street View
    private PlaceAdapter adapter;
    private FirebaseAnalytics mfirebaseanlytics;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityFamousPlacesBinding binding = ActivityFamousPlacesBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        mfirebaseanlytics = FirebaseAnalytics.getInstance(FamousPlacesActivity.this);
        mfirebaseanlytics.setUserProperty("Famous Activity","Famous Activity");

        com.facebook.ads.AdView adView = new com.facebook.ads.AdView(FamousPlacesActivity.this, getString(R.string.fb_placement_banner), AdSize.BANNER_HEIGHT_50);
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



        //Change Toolbar text
        Objects.requireNonNull(getSupportActionBar()).setTitle(Constants.FAMOUS_PLACES);

        //Passing adapter list and listener
        adapter = new PlaceAdapter(DataSource.getPlaces(),this);

        //Set Adapter
        binding.placeRecyclerView.setAdapter(adapter);

        //Start Animations
        binding.placeRecyclerView.scheduleLayoutAnimation();

    }

    //////////////////// On Place Select: Open Streetview in Google maps ////////////////////
    @Override
    public void onPlaceSelect(Place place) {

            // Create a Uri from an intent string. Use the result to create an Intent.
            Uri gmmIntentUri = Uri.parse("google.streetview:cbll="+place.getLatitude()+","+place.getLongitude()+"");

            // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            // Make the Intent explicit by setting the Google Maps package
            mapIntent.setPackage("com.google.android.apps.maps");

            // Attempt to start an activity that can handle the Intent
            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            }

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    //////////////////// On Query Text Changed: Filter Results in Adapter ////////////////////
    @Override
    public boolean onQueryTextChange(String newText) {
        adapter.filter(newText);
        return false;
    }


    ////////////////////////////// Inflating Search Menu /////////////////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_search_menu,menu);

        //Find Menu Item
        MenuItem searchItem = menu.findItem(R.id.action_search);

        //Search View
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Search Place");
        searchView.setOnQueryTextListener(this);
        searchView.setIconified(false);

        return true;
    }




}