package com.itrided.android.barracoda.stores;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.itrided.android.barracoda.databinding.ItemStoreListBinding;

public class StoreListAdapter extends RecyclerView.Adapter<StoreListAdapter.StoreViewHolder> {

    private PlaceBuffer places;

    @NonNull
    @Override
    public StoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        final ItemStoreListBinding binding = ItemStoreListBinding.inflate(layoutInflater, parent, false);

        return new StoreViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreViewHolder storeViewHolder, int position) {
        storeViewHolder.bind(places.get(position));
    }

    @Override
    public int getItemCount() {
        return places == null ? 0 : places.getCount();
    }

    public void setPlaces(PlaceBuffer places) {
        this.places = places;
        notifyDataSetChanged();
    }

    static class StoreViewHolder extends RecyclerView.ViewHolder {
        private ItemStoreListBinding binding;

        public StoreViewHolder(ItemStoreListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(@NonNull Place place) {
            binding.nameTv.setText(place.getName());
            binding.priceLevelTv.setText(place.getAddress());
        }
    }
}
