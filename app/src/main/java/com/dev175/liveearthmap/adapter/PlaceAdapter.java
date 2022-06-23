package com.dev175.liveearthmap.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.dev175.liveearthmap.myinterface.IPlaceClickListener;
import com.dev175.liveearthmap.R;
import com.dev175.liveearthmap.databinding.ItemPlaceBinding;
import com.dev175.liveearthmap.model.Place;

import java.util.ArrayList;
import java.util.List;

//Adapter for Famous Places
public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder> {
    private List<Place> mPlaceList;
    private IPlaceClickListener mListener;
    private List<Place> copyList;

    public PlaceAdapter(List<Place> mPlaceList, IPlaceClickListener mListener) {
        this.mPlaceList = mPlaceList;
        this.mListener = mListener;
        copyList = new ArrayList<>();
        copyList.addAll(mPlaceList);
    }

    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPlaceBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_place,parent,false);
        return new PlaceViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int position) {
        holder.bind(mPlaceList.get(position),mListener);
    }

    @Override
    public int getItemCount() {
        return mPlaceList.size();
    }


    public void filter(String queryText)
    {
        mPlaceList.clear();

        if(queryText.isEmpty())
        {
            mPlaceList.addAll(copyList);
        }
        else
        {
            for (int i=0;i<copyList.size();i++)
            {
                String name = copyList.get(i).getName();
                if(name.toLowerCase().contains(queryText.toLowerCase()))
                {
                    mPlaceList.add(copyList.get(i));
                }
            }
        }

        notifyDataSetChanged();
    }

    public class PlaceViewHolder extends RecyclerView.ViewHolder {
        final ItemPlaceBinding binding;

        public PlaceViewHolder(ItemPlaceBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Place currentPlace, IPlaceClickListener clickListener)
        {
            //For each item, corresponding product object is passed to the binding
            binding.setPlace(currentPlace);

            binding.setPlaceItemClicked(clickListener);
            //This is to force bindings to execute right away
            binding.executePendingBindings();

        }


    }
}
