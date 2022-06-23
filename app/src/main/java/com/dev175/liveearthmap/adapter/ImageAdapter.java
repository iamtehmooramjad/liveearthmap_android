package com.dev175.liveearthmap.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.dev175.liveearthmap.R;
import com.dev175.liveearthmap.model.Image;
import com.dev175.liveearthmap.myinterface.IOnImageClickListener;
import com.dev175.liveearthmap.utils.SpacingItemDecoration;

import java.util.ArrayList;

//Adapter for Gallery Images
public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MyViewHolder> {

    Context c;
    ArrayList<Image> mImageList;
    IOnImageClickListener listener;

    public ImageAdapter(Context c, ArrayList<Image> images,IOnImageClickListener clickListener) {
        this.c = c;
        this.mImageList = images;
        this.listener = clickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(c).inflate(R.layout.item_image,parent,false);
        return new MyViewHolder(v,listener);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {


        Image s=mImageList.get(position);


        Glide.with(c)
                .load(s.getUri())
                .placeholder(R.drawable.imageplaceholder)
                .into(holder.img);

        setAnimation(holder.itemView,position);
    }

    @Override
    public int getItemCount() {
        return mImageList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView img;

        public MyViewHolder(View itemView,IOnImageClickListener listener) {
            super(itemView);
            img= (ImageView) itemView.findViewById(R.id.image);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onImageClick(getAdapterPosition());
                }
            });
        }
    }

    private int lastPosition = -1;
    private boolean on_attach = true;

    private void setAnimation(View view, int position) {
        if (position > lastPosition) {
            SpacingItemDecoration.animateFadeIn(view, on_attach ? position : -1);
            lastPosition = position;
        }
    }


}

