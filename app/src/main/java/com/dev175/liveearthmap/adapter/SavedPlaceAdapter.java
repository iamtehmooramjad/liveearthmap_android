package com.dev175.liveearthmap.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;
import com.dev175.liveearthmap.R;
import com.dev175.liveearthmap.databinding.ItemSavedPlaceBinding;
import com.dev175.liveearthmap.model.SavePlace;
import com.dev175.liveearthmap.model.SavePlaceViewModel;
import java.util.List;

//Adapter for Saved Places
public class SavedPlaceAdapter extends RecyclerView.Adapter<SavedPlaceAdapter.MyViewHolder> {

    private List<SavePlace> savePlaces;
    private SavedPlaceAdapter.ISavePlaceItemClickListener listener;
    SavePlaceViewModel mSavePlaceViewModel;

    public SavedPlaceAdapter( ViewModelStoreOwner storeOwner, SavedPlaceAdapter.ISavePlaceItemClickListener savePlaceItemClickListener) {

        mSavePlaceViewModel= new ViewModelProvider(storeOwner).get(SavePlaceViewModel.class);
        this.listener = savePlaceItemClickListener;
    }

    public void setList(List<SavePlace> mSaveList)
    {
        this.savePlaces = mSaveList;
    }
    @NonNull
    @Override
    public SavedPlaceAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSavedPlaceBinding itemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_saved_place,parent,false);
        return new SavedPlaceAdapter.MyViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull SavedPlaceAdapter.MyViewHolder holder, int position) {

        //For each item, corresponding product object is passed to the binding
        holder.binding.setSaveplace(savePlaces.get(position));
        //Item Click Listener for each item
        holder.binding.setSavePlaceItemClick(listener);
        //Item Long Click Listener for delete Item
        //This is to force bindings to execute right away
        holder.binding.executePendingBindings();

    }

    @Override
    public int getItemCount() {

        return savePlaces.size();

    }

    public  class MyViewHolder extends RecyclerView.ViewHolder{

        private final ItemSavedPlaceBinding binding ;

        MyViewHolder(ItemSavedPlaceBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }

    public interface ISavePlaceItemClickListener
    {
        void onSavePlaceItemClick(SavePlace savePlace);

    }
}
