package com.dev175.liveearthmap.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCapture.OnImageCapturedCallback;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.extensions.HdrImageCaptureExtender;
import androidx.camera.view.CameraView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.Image;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.dev175.liveearthmap.R;
import com.dev175.liveearthmap.databinding.ActivityGpsMapCameraBinding;
import com.dev175.liveearthmap.utils.Constants;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GpsMapCameraActivity extends AppCompatActivity {

    //For Logs
    private static final String TAG = "GpsMapCameraActivity";

    //For Data binding
    private ActivityGpsMapCameraBinding binding;

    //Executor for taking Picture
    private Executor executor = Executors.newSingleThreadExecutor();

    //CameraSelector
    CameraSelector cameraSelector;

    //Request Codes for Permissions
    private final int REQUEST_CODE_PERMISSIONS = 1001;
    private final String[] REQUIRED_PERMISSIONS = new String[]
            {"android.permission.CAMERA",
                    "android.permission.WRITE_EXTERNAL_STORAGE"
                    , "android.permission.ACCESS_FINE_LOCATION"
                    , "android.permission.ACCESS_COARSE_LOCATION"};

    //FusedLocationProvider to get Location
    private FusedLocationProviderClient fusedLocationProviderClient;

    //Bitmap of address view
    private Bitmap addressBitmap;

    //Bitmap of Captured Image
    private Bitmap imageBitmap;

    //File directory to save image
    File directory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGpsMapCameraBinding.inflate(getLayoutInflater());
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


        //Initializing Views
        init();

        //Initialize Directory
       directory = new File(Environment.getExternalStorageDirectory()+"/GpsCam");

       //Check Directory exists or create new one
        if(!directory.exists()) {
            directory.mkdir();
        }

        //Check GPS Enable
        isGpsEnabled(this);

        //Initialize FusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(GpsMapCameraActivity.this);


        getCurrentLatLng();
        //Set Date time
        String currentDateTimeString = java.text.DateFormat.getDateTimeInstance().format(new Date());
        binding.dateTime.setText(currentDateTimeString);

        //Check permissions are granted or not
        if (allPermissionsGranted())
        {
            //If Granted
            startCamera();
        }
        else
        {
            //Request Permissions
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS,
                    REQUEST_CODE_PERMISSIONS);
        }

    }

    ////////////// Get Device LatLng and Get & Set Address From latitude & Longitude ////////////
    private void getCurrentLatLng() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        // Get Device Location
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
               try {

                    LatLng cameraPos = new LatLng(location.getLatitude(), location.getLongitude());
                    //Get & Set Address From latitude & Longitude
                    getAddress(cameraPos);

               }
               catch (Exception e)
               {
                   Log.e(TAG, "onSuccess: "+e.getMessage() );
               }
            }
        });

    }

    //////////////////// Overlay both Bitmaps(Address and Captured image) ////////////////////////
    ////////////////   bm1 = captured image, bm2 = address bitmap  ///////////////////////////////
    private   Bitmap overlay(Bitmap bmp1, Bitmap bmp2) {
        int height = 0;
        //Chech Which Camera is currently facing
       try
       {
           if (binding.cameraView.getCameraLensFacing()==CameraSelector.LENS_FACING_FRONT)
           {
               height = (int) (bmp1.getHeight()*(0.9));
               bmp2 = getResizedBitmap(bmp2,bmp1.getWidth(), bmp2.getHeight()*3);
               Log.d(TAG, "overlay: Front Camera");
           }
           else
           {
               height = (int) (bmp1.getHeight()*(0.9));
               bmp2 = getResizedBitmap(bmp2,bmp1.getWidth(), bmp2.getHeight()*4);
               Log.d(TAG, "overlay: Back Camera");
           }
       }
       catch (Exception e)
       {
           Log.e(TAG, "overlay: "+e.getMessage() );
       }


        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp1, new Matrix(), null);
        canvas.drawBitmap(bmp2, 0,height, null);
        bmp1.recycle();
        bmp2.recycle();
        return bmOverlay;
    }

    //////////////////////////////// Resize Image Bitmap ///////////////////////////////////////
    private Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    /////////////////////// Initializing views //////////////////////////////////////////////////
    private void init() {
        //Set Background of cardview
        binding.bottomLayout.setBackgroundResource(R.color.primaryVariant);

        //Set toolbar as Actionbar
        try {
            setSupportActionBar(binding.toolbar);
        }
        catch (Exception e)
        {
            Log.e(TAG, "onCreate: "+e.getMessage() );
        }

    }

    //////////////////////////// Inflate menu (toolbar) /////////////////////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_gallery,menu);
        return true;
    }

    //////////////////////////// Toolbar Options : Open Gallery /////////////////////////////////
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.open_gallery) {
            startActivity(new Intent(GpsMapCameraActivity.this, GpsMapGalleryActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    ////////////////////////////// Returns bitmap of the address View ///////////////////////////
    public Bitmap getBitmapOfView(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    //////////////////////////// Start Camera ///////////////////////////////////////////////////
    private void startCamera() {
        binding.cameraView.setFlash(ImageCapture.FLASH_MODE_AUTO);

        //Can set flash mode to auto,on,off...
        ImageCapture.Builder builder = new ImageCapture.Builder();

        //Vendor-Extensions (The CameraX extensions dependency in build.gradle)
        HdrImageCaptureExtender hdrImageCaptureExtender = HdrImageCaptureExtender.create(builder);

        // If has hdr (optional).
        if (hdrImageCaptureExtender.isExtensionAvailable(cameraSelector)) {
            // Enable hdr.
            hdrImageCaptureExtender.enableExtension(cameraSelector);
        }

        //Check Camera Permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        //Bind to lifecycle
        binding.cameraView.bindToLifecycle((LifecycleOwner) GpsMapCameraActivity.this);

        //Capture Image
        binding.btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.cameraView.setCaptureMode(CameraView.CaptureMode.IMAGE);
                binding.cameraView.takePicture(executor, new OnImageCapturedCallback() {
                    @Override
                    public void onCaptureSuccess(@NonNull ImageProxy image) {
                        imageBitmap = getBitmap(image);
                        addressBitmap = getBitmapOfView(binding.layoutAddress);
                        Bitmap res = overlay(imageBitmap,addressBitmap);
                        saveImage(res);
                        Log.d(TAG, "onCaptureSuccess: "+imageBitmap.toString());
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        super.onError(exception);
                        Log.e(TAG, "onError: "+exception.getMessage() );
                    }
                });

            } //onclick end
        });

        //Close camera
        binding.btnClose.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        //Switch Camera
        binding.btnLens.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ActivityCompat.checkSelfPermission(GpsMapCameraActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                //Change Camera
                if (binding.cameraView.hasCameraWithLensFacing(CameraSelector.LENS_FACING_FRONT)) {
                    binding.cameraView.toggleCamera();
                }
            }
        });


    }

    ////////////////// Save Bitmap Image to Device Internal Storage /////////////////////////////
    private void saveImage(Bitmap bitmap){

        ExecutorService executors = Executors.newSingleThreadExecutor();
        executors.execute(new Runnable() {
            @Override
            public void run() {
                //If directory does not exist,Create it
                if(!directory.exists()) {
                    directory.mkdirs();
                }

                SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
                try{

                    OutputStream fOut = null;
                    File file = new File(directory, mDateFormat.format(new Date()) + ".jpg");
                    Log.d(TAG, "save: "+file.toString());
                    fOut = new FileOutputStream(file);

                    bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
                    fOut.flush();
                    fOut.close();

                    MediaStore.Images.Media.insertImage(getContentResolver()
                            ,file.getAbsolutePath(),file.getName(),file.getName());

                }
                catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    //////////////////////// Get Bitmap of ImageProxy on Image Capture //////////////////////////
    private Bitmap getBitmap(ImageProxy image) {
        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        buffer.rewind();
        byte[] bytes = new byte[buffer.capacity()];
        buffer.get(bytes);
        byte[] clonedBytes = bytes.clone();
        return BitmapFactory.decodeByteArray(clonedBytes, 0, clonedBytes.length);
    }

    ///////////////////// Check Permissions are granted or not //////////////////////////////////
    public boolean allPermissionsGranted(){
        for(String permission : REQUIRED_PERMISSIONS){
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }

    ///////////////////// On Request Permissions Result /////////////////////////////////////////
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == REQUEST_CODE_PERMISSIONS){
            if(allPermissionsGranted()){
                startCamera();
            } else{
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                this.finish();
            }
        }
    }

    ////////////////////// Get & Set Address From latitude & Longitude //////////////////////////
    public void getAddress(LatLng location) {
        Geocoder geocoder = new Geocoder(GpsMapCameraActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1);
            Address obj = addresses.get(0);
            String address = obj.getAddressLine(0);
            binding.addressText.setText(address);
        }
        catch (IOException e)
        {
            Log.e(TAG, "getAddress: "+e.getMessage() );
        }
    }

    ////////////////// Check GPS is Enabled or not & then let the user to enable it /////////////
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


