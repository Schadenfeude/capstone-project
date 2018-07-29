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
    public static final String TAG = ProductListFragment.class.getSimpleName();

    private FragmentProductListBinding binding;
    private ProductListViewModel productListViewModel;
    private ProductListAdapter productListAdapter;

    public ProductListFragment() {
    }

    private static ProductListFragment INSTANCE = null;

    public static ProductListFragment getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ProductListFragment();
        }
        return INSTANCE;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        productListViewModel = ViewModelProviders.of(getActivity()).get(ProductListViewModel.class);
        productListViewModel.getProducts().observe(this, productEntries -> {
            if (productListAdapter == null)
                return;

            productListAdapter.setItems(productEntries);
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProductListBinding.inflate(inflater, container, false);

        productListAdapter = new ProductListAdapter(productListViewModel.getProducts().getValue());
        binding.productsRv.setAdapter(productListAdapter);
        binding.productsRv.addItemDecoration(new DividerItemDecoration(binding.productsRv.getContext(),
                LinearLayoutManager.VERTICAL));

        return binding.getRoot();
    }
}
