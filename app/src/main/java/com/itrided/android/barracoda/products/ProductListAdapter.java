package com.itrided.android.barracoda.products;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.itrided.android.barracoda.data.model.Product;
import com.itrided.android.barracoda.databinding.ItemProductListBinding;

import java.util.ArrayList;
import java.util.List;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ProductViewHolder> {

    private ArrayList<Product> products;

    public ProductListAdapter(@Nullable List<? extends Product> products) {
        this.products = new ArrayList<>();
        if (products != null) {
            this.products.addAll(products);
        }
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        final LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        final ItemProductListBinding binding = ItemProductListBinding.inflate(layoutInflater, viewGroup, false);

        return new ProductViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder productViewHolder, int position) {
        productViewHolder.bind(products.get(position));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void setItems(List<? extends Product> ingredientList) {
        products.clear();
        products.addAll(ingredientList);
        notifyDataSetChanged();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        private ItemProductListBinding binding;

        ProductViewHolder(ItemProductListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(@NonNull Product product) {
            binding.nameTv.setText(product.getName());
            binding.priceTv.setText(product.getWeight());
        }
    }
}
