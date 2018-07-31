package com.itrided.android.barracoda.products;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.itrided.android.barracoda.BarraCodaApp;
import com.itrided.android.barracoda.data.model.db.ProductEntry;
import com.itrided.android.barracoda.databinding.FragmentProductListBinding;
import com.itrided.android.barracoda.ui.SwipeToDeleteCallback;

import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;

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
        final ItemTouchHelper ith = new ItemTouchHelper(getSwipeHandler(this.getContext()));

        ith.attachToRecyclerView(binding.productsRv);
        binding.productsRv.setAdapter(productListAdapter);
        binding.productsRv.addItemDecoration(new DividerItemDecoration(binding.productsRv.getContext(),
                LinearLayoutManager.VERTICAL));

        return binding.getRoot();
    }

    private SwipeToDeleteCallback getSwipeHandler(Context context) {
        return new SwipeToDeleteCallback(context) {
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                final ProductEntry product = productListViewModel.getProductsValue().get(position);
                deleteProduct(product);
            }
        };
    }

    private void deleteProduct(@NonNull ProductEntry product) {
        Completable.create(
                emitter -> {
                    BarraCodaApp.getDatabaseInstance().productModel().deleteProduct(product);
                    emitter.onComplete();
                })
                .subscribeOn(Schedulers.io())
                .subscribe();
    }
}
