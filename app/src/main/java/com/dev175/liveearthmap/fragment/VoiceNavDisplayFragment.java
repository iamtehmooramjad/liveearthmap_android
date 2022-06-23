package com.dev175.liveearthmap.fragment;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dev175.liveearthmap.databinding.FragmentVoiceNavigationDisplayBinding;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class VoiceNavDisplayFragment extends Fragment {
    private FragmentVoiceNavigationDisplayBinding binding;
    private ArrayList<String> voiceData;
    private static final String TAG = "VoiceNavDisplayFragment";



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentVoiceNavigationDisplayBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        String voiceNote = "";
        for (String text : voiceData) {
            voiceNote = voiceNote + text + " ";
        }
        binding.result.setText(voiceNote);
        binding.result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendIntentToMaps();
            }
        });
        return view;
    }

    private void sendIntentToMaps() {
        LatLng desAdd = getLatLng(binding.result.getText().toString());
        if (desAdd!=null)
        {
            Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("http://maps.google.com/maps?q="+desAdd.latitude+","+desAdd.longitude));
            startActivity(intent);
        }
        else {
            Toast.makeText(getContext(), "Incomplete Address", Toast.LENGTH_SHORT).show();
        }

    }

    private LatLng getLatLng(String address)
    {
        try {
            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
            List<Address> addressList = geocoder.getFromLocationName(address,1);
            if (addressList.size()>0)
            {
                android.location.Address  address1= addressList.get(0);
                return new LatLng(address1.getLatitude(),address1.getLongitude());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setData(ArrayList<String> result) {
        this.voiceData = result;
    }
}
