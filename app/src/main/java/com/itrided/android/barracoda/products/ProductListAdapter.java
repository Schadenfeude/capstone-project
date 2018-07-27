package com.itrided.android.barracoda.products;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.itrided.android.barracoda.data.Product;

import java.util.ArrayList;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.IngredientViewHolder> {

    private ArrayList<Product> products;

    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

//    public ProductListAdapter(List<ProductPojo> ingredientsValue) {
//        products = new ArrayList<>(ingredientsValue.size());
//        products.addAll(ingredientsValue);
//    }
//
//    @NonNull
//    @Override
//    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
//        final LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
//        final ItemIngredientBinding binding = ItemIngredientBinding.inflate(layoutInflater, viewGroup, false);
//
//        return new ProductListAdapter.IngredientViewHolder(binding);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull IngredientViewHolder ingredientViewHolder, int position) {
//        ingredientViewHolder.bind(products.get(position));
//    }
//
//    @Override
//    public int getItemCount() {
//        return products.size();
//    }
//
//    public void setItems(List<ProductPojo> ingredientList) {
//        products.clear();
//        products.addAll(ingredientList);
//        notifyDataSetChanged();
//    }

    static class IngredientViewHolder extends RecyclerView.ViewHolder {
        public IngredientViewHolder(View itemView) {
            super(itemView);
        }
//        private ItemIngredientBinding binding;
//
//        IngredientViewHolder(ItemIngredientBinding binding) {
//            super(binding.getRoot());
//            this.binding = binding;
//        }
//
//        void bind(@NonNull ProductPojo product) {
//            final String amount = product.getQuantity() + " " + product.getMeasure();
//            binding.ingredientNameTv.setText(product.getName());
//            binding.ingredientAmountTv.setText(amount);
//        }
    }
}
