package com.dev175.liveearthmap.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.dev175.liveearthmap.R;
import com.dev175.liveearthmap.databinding.ItemCompassBinding;
import com.dev175.liveearthmap.model.Compass;
import com.dev175.liveearthmap.myinterface.ICompassSelectListener;

import java.util.ArrayList;

//Adapter Class For Compass
public class CompassAdapter extends RecyclerView.Adapter<CompassAdapter.CompassViewHolder> {
    private ArrayList<Compass> mCompassList;
    private ICompassSelectListener mListener;

    public CompassAdapter(ArrayList<Compass> mCompassList, ICompassSelectListener mListener) {
        this.mCompassList = mCompassList;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public CompassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCompassBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_compass,parent,false);
        return new CompassViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CompassViewHolder holder, int position) {
        holder.bind(mCompassList.get(position),mListener);
    }

    @Override
    public int getItemCount() {
        return mCompassList.size();
    }

    public class CompassViewHolder extends RecyclerView.ViewHolder {
        final ItemCompassBinding binding;

        public CompassViewHolder(ItemCompassBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Compass currentCompass,ICompassSelectListener listener)
        {
            binding.setCompass(currentCompass);
            binding.setCompassItemClicked(listener);
            binding.executePendingBindings();
        }


    }
}
