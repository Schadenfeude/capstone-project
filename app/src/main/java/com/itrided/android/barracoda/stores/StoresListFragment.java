package com.itrided.android.barracoda.stores;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.itrided.android.barracoda.R;
import com.itrided.android.barracoda.databinding.FragmentStoreListBinding;
import com.itrided.android.barracoda.stores.store.FindStoreFragment;

public class StoresListFragment extends Fragment {

    public static final String TAG = StoresListFragment.class.getSimpleName();

    private FragmentStoreListBinding binding;

    public StoresListFragment() {
    }

    private static StoresListFragment INSTANCE = null;

    public static StoresListFragment getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new StoresListFragment();
        }
        return INSTANCE;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentStoreListBinding.inflate(inflater, container, false);

        binding.fabAddStore.setOnClickListener(getAddStoreClickListener());
//        productListAdapter = new ProductListAdapter(productListViewModel.getProducts().getValue());
//        binding.productsRv.setAdapter(productListAdapter);
//        binding.productsRv.addItemDecoration(new DividerItemDecoration(binding.productsRv.getContext(),
//                LinearLayoutManager.VERTICAL));

        return binding.getRoot();
    }

    private View.OnClickListener getAddStoreClickListener() {
        return v -> launchFindStoreFragment();
    }

    private void launchFindStoreFragment() {
        final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        final FindStoreFragment findStoreFragment = new FindStoreFragment();

        transaction.replace(R.id.fragment_placeholder, findStoreFragment, FindStoreFragment.TAG);
        transaction.addToBackStack(FindStoreFragment.TAG);
        transaction.commit();
    }
}
