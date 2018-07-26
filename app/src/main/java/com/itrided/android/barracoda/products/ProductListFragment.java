package com.itrided.android.barracoda.products;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.itrided.android.barracoda.databinding.FragmentProductListBinding;

public class ProductListFragment extends Fragment {
    private FragmentProductListBinding binding;
//    private FragmentIngredientsBinding binding;
//    private RecipeViewModel recipeViewModel;
//    private ProductListAdapter productListAdapter;
//
//    public ProductListFragment() {
//    }
//
//    private static ProductListFragment INSTANCE = null;
//
//    public static ProductListFragment getInstance() {
//        if (INSTANCE == null) {
//            INSTANCE = new ProductListFragment();
//        }
//        return INSTANCE;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setRetainInstance(true);
//
//        // get the Recipe from the Activity
//        recipeViewModel = ViewModelProviders.of(getActivity()).get(RecipeViewModel.class);
//        recipeViewModel.getIngredients().observe(this, ingredients -> {
//            if (productListAdapter == null)
//                return;
//
//            productListAdapter.setItems(ingredients);
//        });
//    }
//
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        binding = FragmentIngredientsBinding.inflate(inflater, container, false);
//
//        // update the Recipe when a new View is created
//        productListAdapter = new ProductListAdapter(recipeViewModel.getIngredientsValue());
//        binding.ingredientsRv.setAdapter(productListAdapter);
//        binding.ingredientsRv.addItemDecoration(new DividerItemDecoration(binding.ingredientsRv.getContext(),
//                LinearLayoutManager.VERTICAL));
//
//        return binding.getRoot();
//    }
}
