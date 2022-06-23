package com.dev175.liveearthmap.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dev175.liveearthmap.R;
import com.dev175.liveearthmap.adapter.SavedPlaceAdapter;
import com.dev175.liveearthmap.databinding.ActivitySavedPlacesBinding;
import com.dev175.liveearthmap.model.SavePlace;
import com.dev175.liveearthmap.model.SavePlaceViewModel;
import com.dev175.liveearthmap.utils.Constants;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SavedPlacesActivity extends AppCompatActivity implements SavedPlaceAdapter.ISavePlaceItemClickListener {

    //For Logs
    private static final String TAG = "SavedPlacesActivity";
    //For Data Binding
    ActivitySavedPlacesBinding binding;
    //Saved Places Adapter
    SavedPlaceAdapter adapter;
    //Save Place View Model
    SavePlaceViewModel savePlaceViewModel;


    private NativeAdLayout nativeAdLayout;
    private LinearLayout adView;
    private NativeAd nativeAd;
    private void loadNativeAd() {

        nativeAd = new NativeAd(this, getResources().getString(R.string.fb_placement_NativeAdvanced));
        NativeAdListener nativeAdListener = new NativeAdListener() {
            @Override
            public void onMediaDownloaded(Ad ad) {

            }

            @Override
            public void onError(Ad ad, AdError adError) {

            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Race condition, load() called again before last ad was displayed
                if (nativeAd == null || nativeAd != ad) {
                    return;
                }
                // Inflate Native Ad into Container
                inflateAd(nativeAd);
            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }

        };

        // Request an ad
        nativeAd.loadAd(
                nativeAd.buildLoadAdConfig()
                        .withAdListener(nativeAdListener)
                        .build());
    }
    private void inflateAd(NativeAd nativeBannerAd) {
        // Unregister last ad
        nativeBannerAd.unregisterView();
        // Add the Ad view into the ad container.
        nativeAdLayout = findViewById(R.id.native_ad_container);
        LayoutInflater inflater = LayoutInflater.from(SavedPlacesActivity.this);
        // Inflate the Ad view.  The layout referenced is the one you created in the last step.
        adView = (LinearLayout) inflater.inflate(R.layout.facebook_native_ad, nativeAdLayout, false);
        nativeAdLayout.addView(adView);

        // Add the AdChoices icon
        RelativeLayout adChoicesContainer = adView.findViewById(R.id.ad_choices_container);
        AdOptionsView adOptionsView = new AdOptionsView(SavedPlacesActivity.this, nativeBannerAd, nativeAdLayout);
        adChoicesContainer.removeAllViews();
        adChoicesContainer.addView(adOptionsView, 0);

        // Create native UI using the ad metadata.
        TextView nativeAdTitle = adView.findViewById(R.id.native_ad_title);
        TextView nativeAdSocialContext = adView.findViewById(R.id.native_ad_social_context);
        TextView sponsoredLabel = adView.findViewById(R.id.native_ad_sponsored_label);
        MediaView nativeAdIconView = adView.findViewById(R.id.native_icon_view);
        Button nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action);

        // Set the Text.
        nativeAdCallToAction.setText(nativeBannerAd.getAdCallToAction());
        nativeAdCallToAction.setVisibility(
                nativeBannerAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
        nativeAdTitle.setText(nativeBannerAd.getAdvertiserName());
        nativeAdSocialContext.setText(nativeBannerAd.getAdSocialContext());
        sponsoredLabel.setText(nativeBannerAd.getSponsoredTranslation());

        // Register the Title and CTA button to listen for clicks.
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(nativeAdTitle);
        clickableViews.add(nativeAdCallToAction);
        nativeBannerAd.registerViewForInteraction(adView, nativeAdIconView, clickableViews);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySavedPlacesBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        loadNativeAd();

        //Change Toolbar Text
        Objects.requireNonNull(getSupportActionBar()).setTitle(Constants.SAVED_PLACES);

        //Initializing Adapter
        adapter=new SavedPlaceAdapter( this,this);

        //Initializing ViewModel
        savePlaceViewModel = new ViewModelProvider(this).get(SavePlaceViewModel.class);

        //Retrieve Data from Database and Set Observer
        savePlaceViewModel.getAllData().observe(this, new Observer<List<SavePlace>>() {
            @Override
            public void onChanged(List<SavePlace> savePlaceList) {

                if (savePlaceList!=null ){

                    if (savePlaceList.size()>0){
                        binding.nodatasaved.setVisibility(View.GONE);

                    }
                    else{
                        binding.nodatasaved.setVisibility(View.VISIBLE);

                    }
                }

                adapter.setList(savePlaceList);




                //Set adatpter data


                //Set recyclerview adapter
                binding.savedPlacesRecyclerView.setAdapter(adapter);

                try
                {
                    //Notify adapter of data changed
                    adapter.notifyDataSetChanged();
                }
                catch (Exception e)
                {
                    Log.e(TAG, "onChanged: "+e.getMessage() );
                }

                //Start Animation
                binding.savedPlacesRecyclerView.scheduleLayoutAnimation();
            }
        });
    }

    //////////////////// On Place Clicked : Open Google Maps and show Route ///////////////////////
    @Override
    public void onSavePlaceItemClick(SavePlace savePlace) {

        new MaterialAlertDialogBuilder(SavedPlacesActivity.this)
                .setTitle("Choose From below")
                .setMessage("Select View to navigate or Delete to delete Place")
                .setPositiveButton("View", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if (savePlace!=null)
                        {
                            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                    Uri.parse("http://maps.google.com/maps?saddr=" + savePlace.getSrcLat()+ "," + savePlace.getSrcLng() + "&daddr=" +
                                            "" + savePlace.getDesLat() + "," + savePlace.getDesLng() + ""));
                            startActivity(intent);
                        }
                    }
                })
                .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        savePlaceViewModel.delete(savePlace.getPlaceId());
                    }
                })
                .show();
    }
}