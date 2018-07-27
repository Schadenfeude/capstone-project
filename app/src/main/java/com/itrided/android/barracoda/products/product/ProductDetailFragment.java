package com.itrided.android.barracoda.products.product;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.itrided.android.barracoda.R;
import com.itrided.android.barracoda.data.BarraCodaDb;
import com.itrided.android.barracoda.data.Product;
import com.itrided.android.barracoda.data.model.db.ProductEntry;
import com.itrided.android.barracoda.databinding.FragmentProductDetailBinding;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class ProductDetailFragment extends Fragment {

    public static final String TAG = ProductDetailFragment.class.getSimpleName();

    private FragmentProductDetailBinding binding;
    private ProductViewModel productViewModel;
    private CompositeDisposable disposable = new CompositeDisposable();

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupViewModel();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentProductDetailBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposable.dispose();
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
            setupFab();
        });
    }

    private void setupFab() {
        final Product product = productViewModel.getProduct().getValue();
        final BarraCodaDb barraCodaDb = BarraCodaDb.getInstance(getContext());

        binding.fabFavorite.setOnClickListener(getFavoriteClickListener(barraCodaDb, product));
        setupFabState(barraCodaDb, product);
    }

    private void setupFabState(BarraCodaDb barraCodaDb, Product product) {
        disposable.add(
                barraCodaDb.productModel().getProduct(product.getEan())
                        .subscribeOn(Schedulers.io())
                        .subscribe(
                                productEntry -> binding.fabFavorite.setImageResource(R.drawable.ic_favorite),
                                isNotFavorite -> binding.fabFavorite.setImageResource(R.drawable.ic_favorite_border)
                        ));
    }

    private View.OnClickListener getFavoriteClickListener(BarraCodaDb barraCodaDb, Product product) {
        return v -> barraCodaDb.productModel().getProduct(product.getEan())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        productEntry -> {
                            barraCodaDb.productModel().deleteProduct(productEntry);
                            binding.fabFavorite.setImageResource(R.drawable.ic_favorite_border);
                        },
                        isNotFavorite -> {
                            barraCodaDb.productModel().addProduct(new ProductEntry(product));
                            binding.fabFavorite.setImageResource(R.drawable.ic_favorite);
                        }
                );
    }
}
