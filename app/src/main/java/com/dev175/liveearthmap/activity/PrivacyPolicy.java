package com.dev175.liveearthmap.activity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dev175.liveearthmap.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;


public class PrivacyPolicy extends AppCompatActivity {


    SharedPreferences sharedPreferences;
    SharedPreferences.Editor  editor;


    public  static InterstitialAd mInterstitialAd;
    public void InterstitialAdmob() {

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.Interstitial));
        requesttoloadinterstitial();

    }

    public static void requesttoloadinterstitial() {
        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);
    }


public static AdRequest admobbanneradrequest;
    @Override
    protected void onStart() {
        super.onStart();
        InterstitialAdmob();
        admobbanneradrequest = new AdRequest.Builder().build();
        sharedPreferences=getSharedPreferences("GpsEarthMapMobi",MODE_PRIVATE);
        boolean privacychecked =sharedPreferences.getBoolean("Privacy_checked",false);

        if (privacychecked){

            Intent mprvacyintent = new Intent(PrivacyPolicy.this,PermissionsActivity.class);
            startActivity(mprvacyintent);
            finish();
        }



    }
    Animation btnAnim ;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.privacy);

        sharedPreferences=getSharedPreferences("GpsEarthMapMobi",MODE_PRIVATE);

        Button grantedPerm = (Button) findViewById(R.id.granted);

        btnAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.button_animation);

        grantedPerm.setAnimation(btnAnim);

        //Proceed button that goes to mainactivity only if the desired permissions are given
        grantedPerm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editor=sharedPreferences.edit();
                editor.putBoolean("Privacy_checked",true);
                editor.apply();

                startActivity(new Intent(PrivacyPolicy.this, PermissionsActivity.class));
                finish();

            }
        });



    }








}