package com.dev175.liveearthmap.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.dev175.liveearthmap.R;
import com.dev175.liveearthmap.databinding.ActivitySplashBinding;
import com.google.android.gms.ads.AdListener;

public class SplashActivity extends AppCompatActivity {

    private static int splashTimeOut = 5000;
    private ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       binding = ActivitySplashBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Animation myAnimation = AnimationUtils.loadAnimation(this,R.anim.mysplashscreen);
        binding.logo.setAnimation(myAnimation);


        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {

                if (PrivacyPolicy.mInterstitialAd.isLoaded()){
                    PrivacyPolicy.mInterstitialAd.show();
                }
                else {

                    Intent intent = new Intent(SplashActivity.this,HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                }
                PrivacyPolicy.mInterstitialAd.setAdListener(new AdListener(){
                    @Override
                    public void onAdClosed() {
                        super.onAdClosed();
                        Intent intent = new Intent(SplashActivity.this,HomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        PrivacyPolicy.requesttoloadinterstitial();
                    }
                });


            }


        },splashTimeOut);

    }
}