package com.dev175.liveearthmap.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import com.bumptech.glide.Glide;
import com.dev175.liveearthmap.adapter.MapItemAdapter;
import com.dev175.liveearthmap.R;
import com.dev175.liveearthmap.databinding.ActivityHomeBinding;
import com.dev175.liveearthmap.model.MapItem;
import com.dev175.liveearthmap.utils.Constants;
import com.dev175.liveearthmap.utils.DataSource;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;


public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,MapItemAdapter.IMapItemClickListener{

    private ActivityHomeBinding binding;
    private FirebaseAnalytics mfirebaseanlytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        binding.adViewCurrent.loadAd(PrivacyPolicy.admobbanneradrequest);
        binding.adViewCurrent.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {

                binding.adViewCurrent.setVisibility(View.VISIBLE);


            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }


        });

        mfirebaseanlytics = FirebaseAnalytics.getInstance(HomeActivity.this);
        mfirebaseanlytics.setUserProperty("Home Activity","Main Home");
        Glide.with(this).load(R.drawable.earthanimate).into(binding.earth);

        //Navigation View
        View headerView = binding.navigationView.getHeaderView(0);
        binding.navigationView.setItemIconTintList(null);
        setupNavigationDrawerMenu();


        //RecyclerView
        MapItemAdapter adapter = new MapItemAdapter(DataSource.getMapItems(),this);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);

    }
    private void setupNavigationDrawerMenu() {
        binding.navigationView.setNavigationItemSelectedListener(this);
        binding.toolbar.setNavigationIcon(R.drawable.nav);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this,
                binding.drawerLayout,
                binding.toolbar,
                R.string.drawer_open,
                R.string.drawer_close);

        binding.drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
    }

    //On nav items clicked
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId())
        {
            //Share application link to others
            case R.id.item_share:
             {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id="+getPackageName());
                sendIntent.setType("text/plain");
                Intent shareIntent = Intent.createChooser(sendIntent, "Choose From");
                startActivity(shareIntent);
                break;
            }

            //Visit more apps activity in Play store
            case R.id.item_more_apps:
            {
                try {

//                 startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("")));
             }

             catch (Exception e){

             }
                break;
            }


            case R.id.item_privacy:
            {

//                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(""));
//                try{
//                    startActivity(browserIntent);
//                }
//                catch (Exception e){
//
//
//                }
//                break;
            }

            //Rate this application
            case R.id.item_rate_us:
            {
                openAppInPlayStore();
                break;
            }

        }

        closeDrawer();
        return false;
    }

    private void openAppInPlayStore(){
        Uri uri = Uri.parse("market://details?id="+this.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + this.getPackageName())));
        }

    }
Bundle bundle;
    //Close the Drawer
    private void closeDrawer() {
        binding.drawerLayout.closeDrawer(GravityCompat.START);
    }

    //On Map Items CLick
    @Override
    public void onMapItemClick(MapItem mapItem) {

        switch (mapItem.getTitle()) {
            case Constants.ROUTE_FINDER:
            {

                bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "HomeActivity");
                bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "Route Finder Click");
                mfirebaseanlytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle);
                Intent intent = new Intent(HomeActivity.this, RouteFinderActivity.class);
                startActivity(intent);
                break;
            }
            case Constants.GPS_TRACKER:


            {
                bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "HomeActivity");
                bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "GPS_TRACKER Click");
                mfirebaseanlytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle);
                Intent intent = new Intent(HomeActivity.this, GpsTrackerActivity.class);
                startActivity(intent);
                break;
            }
            case Constants.ADDRESS_FINDER:
            {
                bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "HomeActivity");
                bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "ADDRESS_FINDER Click");
                mfirebaseanlytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle);
                Intent intent = new Intent(HomeActivity.this, AddressFinderActivity.class);
                startActivity(intent);
                break;
            }
            case Constants.FAMOUS_PLACES:
            {
                bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "HomeActivity");
                bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "FAMOUS_PLACES Click");
                mfirebaseanlytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle);
                Intent intent = new Intent(HomeActivity.this, FamousPlacesActivity.class);
                startActivity(intent);
                break;
            }
            case Constants.COMPASS:
            {
                bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "HomeActivity");
                bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "COMPASS Click");
                mfirebaseanlytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle);
                Intent intent = new Intent(HomeActivity.this, CompassActivity.class);
                startActivity(intent);
                break;
            }
            case Constants.GPS_MAP_CAMERA:
            {

                bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "HomeActivity");
                bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "GPS_MAP_CAMERA Click");
                mfirebaseanlytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle);
                if (PrivacyPolicy.mInterstitialAd.isLoaded()){
                    PrivacyPolicy.mInterstitialAd.show();
                }
                else {

                    Intent intent = new Intent(HomeActivity.this,GpsMapCameraActivity.class);
                    startActivity(intent);

                }
                PrivacyPolicy.mInterstitialAd.setAdListener(new AdListener(){

                    @Override
                    public void onAdClosed() {
                        super.onAdClosed();
                        Intent intent = new Intent(HomeActivity.this,GpsMapCameraActivity.class);
                        startActivity(intent);
                    }
                });

                break;
            }
            case Constants.SAVED_PLACES:
                {
                    bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "HomeActivity");
                    bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "SAVED_PLACES Click");

                    Intent intent = new Intent(HomeActivity.this,SavedPlacesActivity.class);
                startActivity(intent);
                break;
            }

            case Constants.EARTH_MAP:
            case Constants.VOICE_NAVIGATION:
            {
                bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "HomeActivity");
                bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "VOICE_NAVIGATION Click");

                Intent intent = new Intent(HomeActivity.this, MapItemActivity.class);
                intent.putExtra("title", mapItem.getTitle());
                startActivity(intent);
                break;
            }
        }


    }
}