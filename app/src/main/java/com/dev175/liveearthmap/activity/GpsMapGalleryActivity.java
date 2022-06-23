package com.dev175.liveearthmap.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.dev175.liveearthmap.R;
import com.dev175.liveearthmap.adapter.ImageAdapter;
import com.dev175.liveearthmap.databinding.ActivityGpsMapGalleryBinding;
import com.dev175.liveearthmap.model.Image;
import com.dev175.liveearthmap.myinterface.IOnImageClickListener;
import com.dev175.liveearthmap.utils.SpacingItemDecoration;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;

public class GpsMapGalleryActivity extends AppCompatActivity implements IOnImageClickListener {

    private static final String TAG = "GpsMapGalleryActivity";
    private  File imageFolder;
    ActivityGpsMapGalleryBinding binding;
    ImageAdapter adapter;
    ArrayList<Image> imageList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGpsMapGalleryBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setToolbarTitle();
        imageFolder = new File(Environment.getExternalStorageDirectory()+"/GpsCam");
        //If Directory does not exists, Create it
        if (!imageFolder.exists())
        {
            imageFolder.mkdir();
        }

        binding.rv.setLayoutManager(new GridLayoutManager(this, 3));
        binding.rv.addItemDecoration(new SpacingItemDecoration(3, SpacingItemDecoration.dpToPx(this, 2), true));

    }

    //Set toolbar title
    private void setToolbarTitle() {
        try
        {
            getSupportActionBar().setTitle("GPS Images");
        }
        catch (Exception e)
        {
            Log.e(TAG, "onCreate: "+e.getMessage() );
        }

    }

    //Get Images from Internal Storage
    private ArrayList<Image> getData()
    {
        ArrayList<Image> imagesList=new ArrayList<>();
        //TARGET FOLDER
        Image s;
        if(imageFolder.exists())
        {
            //GET ALL FILES IN Image Folder
            File[] files=imageFolder.listFiles();

            //LOOP THRU THOSE FILES GETTING NAME AND URI
            try
            {
                for (int i=0;i<files.length;i++)
                {
                    File file=files[i];

                    s=new Image();
                    s.setName(file.getName());
                    s.setUri(Uri.fromFile(file));

                    imagesList.add(s);
                }
            }
            catch (Exception e)
            {
                Log.e(TAG, "getData: "+e.getMessage() );
            }
        }
        return imagesList;
    }


    @Override
    public void onImageClick(int image) {
      Image obj =   imageList.get(image);

        Intent intent = new Intent(GpsMapGalleryActivity.this,FullImageActivity.class);
        intent.putExtra("uri",obj.getUri().toString());
        startActivity(intent);
    }


    @Override
    protected void onResume() {
        super.onResume();
        imageList = getData();
        adapter = new ImageAdapter(GpsMapGalleryActivity.this,imageList,this);
        binding.rv.setAdapter(adapter);

    }
}