package com.dev175.liveearthmap.adapter;


import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.dev175.liveearthmap.model.MapItem;
import com.dev175.liveearthmap.R;
import com.dev175.liveearthmap.databinding.ItemGridHomeBinding;
import java.util.List;

//Adapter for Home Items
public class MapItemAdapter extends RecyclerView.Adapter<MapItemAdapter.MyViewHolder> {

    private final List<MapItem> mapItemList;
    private final IMapItemClickListener listener;

    public MapItemAdapter(List<MapItem> mapItemList, IMapItemClickListener mapItemClickListener) {
        this.mapItemList = mapItemList;
        this.listener = mapItemClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       ItemGridHomeBinding itemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_grid_home,parent,false);
        return new MyViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
//        holder.bind(mapItemList.get(position));

        //For each item, corresponding product object is passed to the binding
        holder.binding.setMapItem(mapItemList.get(position));

        //Item Click Listener for each item
        holder.binding.setMapItemClick(listener);

        //This is to force bindings to execute right away
        holder.binding.executePendingBindings();

    }

    @Override
    public int getItemCount() {
        return mapItemList.size();
    }

     public  class MyViewHolder extends RecyclerView.ViewHolder{

       private final ItemGridHomeBinding binding ;

         MyViewHolder(ItemGridHomeBinding binding) {
             super(binding.getRoot());
             this.binding = binding;
         }

    }

    public interface IMapItemClickListener
    {
        void onMapItemClick(MapItem mapItem);
    }
}
