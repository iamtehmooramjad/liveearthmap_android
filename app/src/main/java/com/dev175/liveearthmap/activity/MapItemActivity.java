package com.dev175.liveearthmap.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dev175.liveearthmap.R;
import com.dev175.liveearthmap.databinding.ActivityMapItemBinding;
import com.dev175.liveearthmap.fragment.EarthMapFragment;
import com.dev175.liveearthmap.fragment.VoiceNavigationFragment;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;

import java.util.ArrayList;
import java.util.List;

public class MapItemActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener {
    private static final String TAG = "MapItemActivity";
    private FragmentManager manager;
    private ActivityMapItemBinding binding;



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
        LayoutInflater inflater = LayoutInflater.from(MapItemActivity.this);
        // Inflate the Ad view.  The layout referenced is the one you created in the last step.
        adView = (LinearLayout) inflater.inflate(R.layout.facebook_native_ad, nativeAdLayout, false);
        nativeAdLayout.addView(adView);

        // Add the AdChoices icon
        RelativeLayout adChoicesContainer = adView.findViewById(R.id.ad_choices_container);
        AdOptionsView adOptionsView = new AdOptionsView(MapItemActivity.this, nativeBannerAd, nativeAdLayout);
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
        binding = ActivityMapItemBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        init();
        loadNativeAd();

    }

    private void init() {
        //Get title of the String
        String title= getIntent().getStringExtra("title");

        manager = getSupportFragmentManager();
        manager.addOnBackStackChangedListener(this);
        FragmentTransaction transaction = manager.beginTransaction();

        String tag ="";
        switch (title)
        {

            case "Earth Map":
            {
                tag="addEarthMapF";
                EarthMapFragment fragment = new EarthMapFragment();
                transaction.replace(binding.container.getId(),fragment,tag);
                break;
            }
            case "Voice Navigation":
            {
                tag="addVoiceNavigationF";
                VoiceNavigationFragment fragment = new VoiceNavigationFragment();
                transaction.replace(binding.container.getId(),fragment,tag);
                break;
            }
        }
        transaction.addToBackStack(tag);
        transaction.commit();
    }

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

    //onBackPress button
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


}