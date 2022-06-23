package com.dev175.liveearthmap.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.dev175.liveearthmap.adapter.CompassAdapter;
import com.dev175.liveearthmap.databinding.ActivityCompassBinding;
import com.dev175.liveearthmap.model.Compass;
import com.dev175.liveearthmap.myinterface.ICompassSelectListener;
import com.dev175.liveearthmap.utils.DataSource;
import com.google.firebase.analytics.FirebaseAnalytics;

public class CompassActivity extends AppCompatActivity implements ICompassSelectListener {

    private ActivityCompassBinding binding;
    private static final String TAG = "CompassActivity";

    private FirebaseAnalytics mfirebaseanlytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCompassBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        mfirebaseanlytics = FirebaseAnalytics.getInstance(CompassActivity.this);
        mfirebaseanlytics.setUserProperty("Home Activity","Main Home");

        try {
            getSupportActionBar().setTitle("Compass");
        }catch (Exception e)
        {
            Log.e(TAG, "onCreate: "+e.getMessage());
        }

        CompassAdapter adapter = new CompassAdapter(DataSource.getCompassTypes(),this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2,GridLayoutManager.VERTICAL,false);
        binding.compassRecyclerView.setLayoutManager(gridLayoutManager);
        binding.compassRecyclerView.setItemAnimator(new DefaultItemAnimator());
        binding.compassRecyclerView.setAdapter(adapter);


    }

    Bundle bundle;
    @Override
    public void onSelectCompass(Compass compass) {
        if (compass.getType().equals("Map Compass"))
        {
            bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "Compass Activity");
            bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "MapCompassActivity Click");
            mfirebaseanlytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle);

            startActivity(new Intent(CompassActivity.this,MapCompassActivity.class));
        }
        else {

            bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "Compass Activity");
            bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "StandardCompassActivity Click");
            mfirebaseanlytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle);

            startActivity(new Intent(CompassActivity.this,StandardCompassActivity.class));
        }
    }
}