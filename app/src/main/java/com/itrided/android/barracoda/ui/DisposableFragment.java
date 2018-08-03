package com.itrided.android.barracoda.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import static android.support.v4.app.FragmentManager.POP_BACK_STACK_INCLUSIVE;

public abstract class DisposableFragment extends Fragment {
    private FragmentManager fragmentManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentManager = getActivity().getSupportFragmentManager();
    }

    @Override
    public void onDestroy() {
        try {
            fragmentManager.popBackStack(getTag(), POP_BACK_STACK_INCLUSIVE);
        } catch (IllegalStateException e) {
            // do nothing
        }

        super.onDestroy();
    }
}
