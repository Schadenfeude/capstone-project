package com.itrided.android.barracoda.product;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.itrided.android.barracoda.databinding.FragmentProductDetailBinding;

public class ProductDetailFragment extends Fragment {

    private FragmentProductDetailBinding binding;
    private ProductViewModel productViewModel;

    private static ProductDetailFragment INSTANCE = null;

    public static ProductDetailFragment getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ProductDetailFragment();
        }
        return INSTANCE;
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        setupViewModel();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentProductDetailBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    private void setupViewModel() {
        productViewModel = ViewModelProviders.of(getActivity()).get(ProductViewModel.class);

        productViewModel.getProduct().observe(this, product -> {
            if (product == null) {
                return;
            }
//            binding.image
            binding.name.setText(product.getName());
            binding.description.setText(product.getDescription());
            binding.weight.setText(product.getWeight());
        });

    }
}