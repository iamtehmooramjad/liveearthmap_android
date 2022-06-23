package com.dev175.liveearthmap.fragment;

import android.content.Intent;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import com.dev175.liveearthmap.R;
import com.dev175.liveearthmap.activity.RouteFinderActivity;
import com.dev175.liveearthmap.databinding.FragmentRouteFinderBinding;
import com.dev175.liveearthmap.model.Address;
import com.dev175.liveearthmap.model.SavePlace;
import com.dev175.liveearthmap.model.SavePlaceViewModel;
import com.google.android.gms.maps.model.LatLng;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class RouteFinderFragment extends Fragment {

    //For Logs
    private static final String TAG = "RouteFinderFragment";

    //For Data Binding
    private FragmentRouteFinderBinding binding;

    //Address Object
    private Address objAddress;

    //View Model
    private SavePlaceViewModel savePlaceViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRouteFinderBinding.inflate(inflater);
        View view = binding.getRoot();

        //Initialize View Model Object
        savePlaceViewModel = new ViewModelProvider(this).get(SavePlaceViewModel.class);

        //Get Object From Activity and set the objAddress
        setObjAddress();

        //Get Address and Set in textfields
        String sourceAdd = objAddress.getSourceAddress();
        String destinationAdd = objAddress.getDestinationAddress();

        if (sourceAdd != null && !sourceAdd.equals("")) {
            binding.selectSource.setText(sourceAdd);
        }

        if (destinationAdd != null && !destinationAdd.equals("")) {
            binding.selectDestination.setText(destinationAdd);
        }

        //On Click Listener
        onClickListener();

        return view;
    }

    //////////////////////////// OnClick Listener /////////////////////////////////////////////////
    private void onClickListener() {

        //For Source Address Pickup
        binding.selectSource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Select false for source location
                addRouterFinderMapFragment(false);
            }
        });

        //For Destination Address Pickup
        binding.selectDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Select true for destication location
                addRouterFinderMapFragment(true);
            }
        });

        //To FindRoute
        binding.findRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng srcAdd = null, desAdd = null;
                if (objAddress.getSourceAddress() != null && !objAddress.getSourceAddress().equals("")) {
                    srcAdd = getLatLng(objAddress.getSourceAddress());
                } else {
                    Toast.makeText(getContext(), "Please Select Source Location", Toast.LENGTH_SHORT).show();
                }
                if (objAddress.getDestinationAddress() != null && !objAddress.getDestinationAddress().equals("")) {
                    desAdd = getLatLng(objAddress.getDestinationAddress());
                } else {
                    Toast.makeText(getContext(), "Please Select Destination Location", Toast.LENGTH_SHORT).show();
                }

                if (Double.valueOf(desAdd.longitude)!=null && Double.valueOf(desAdd.latitude)!=null && Double.valueOf(srcAdd.latitude)!=null &&
                        Double.valueOf(srcAdd.longitude)!=null && objAddress.getSourceAddress()!=null && objAddress.getDestinationAddress()!=null) {
                    //Save in Database
                    SavePlace savePlace = new SavePlace(
                            objAddress.getSourceAddress(), srcAdd.latitude,srcAdd.longitude,
                            objAddress.getDestinationAddress(), desAdd.latitude,desAdd.longitude);

                    //Save in Database
                    savePlaceViewModel.insert(savePlace);

                    //Start intent to Maps
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse("http://maps.google.com/maps?saddr=" + srcAdd.latitude + "," + srcAdd.longitude + "&daddr=" +
                                    "" + desAdd.latitude + "," + desAdd.longitude + ""));
                    startActivity(intent);
                }
            }
        });
    }

    ////////////////////////// Get Latitude & Longitude From Address //////////////////////////////
    private LatLng getLatLng(String address) {

        try {
            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
            List<android.location.Address> addressList = geocoder.getFromLocationName(address, 1);
            if (addressList.size() > 0) {
                android.location.Address address1 = addressList.get(0);
                return new LatLng(address1.getLatitude(), address1.getLongitude());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //////////////////////// Add RouteFinderMapFragment ///////////////////////////////////////////
    private void addRouterFinderMapFragment(boolean sourceType) {
        //if sourceType=0 -> sourceLocation else destLocation
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        RouteFinderMapFragment fragment = new RouteFinderMapFragment();
        transaction.add(R.id.routeFinderContainer, fragment, "addRouterFinderMapF");
        fragment.setData(sourceType);
        transaction.addToBackStack("addRouterFinderMapF");
        transaction.commit();
    }

    ///////// Get Object Address from Activity and set the object of this fragment ////////////////
    public void setObjAddress() {
        try {
            this.objAddress = ((RouteFinderActivity) getActivity()).getObjAddress();
        }
        catch (Exception e)
        {
            Log.e(TAG, "setObjAddress: "+e.getMessage() );
        }
    }

}

