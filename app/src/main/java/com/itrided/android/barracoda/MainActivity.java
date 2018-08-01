package com.itrided.android.barracoda;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;

import com.itrided.android.barracoda.barcode.BarcodeScanFragment;
import com.itrided.android.barracoda.databinding.ActivityMainBinding;
import com.itrided.android.barracoda.databinding.AppBarMainBinding;
import com.itrided.android.barracoda.products.ProductListFragment;
import com.itrided.android.barracoda.products.product.ProductDetailFragment;
import com.itrided.android.barracoda.stores.StoreListFragment;

import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.PublishSubject;

import static com.itrided.android.barracoda.widget.ProductWidget.EXTRA_WIDGET_REQUESTED_PRODUCT;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final long EXIT_TIMEOUT = 2000;

    private enum NAV_ITEMS {
        NAV_ITEM_SCAN,
        NAV_ITEM_PRODUCTS,
        NAV_ITEM_STORES
    }

    private CompositeDisposable compositeDisposable;
    private PublishSubject<Boolean> backButtonClickSource = PublishSubject.create();
    private FragmentManager fragmentManager = getSupportFragmentManager();

    private ActivityMainBinding activityMainBinding;
    private AppBarMainBinding appBarMainBinding;
    private ActionBarDrawerToggle drawerToggle;

    private boolean isBackFromDetails = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        appBarMainBinding = activityMainBinding.appBarScan;

        fragmentManager.addOnBackStackChangedListener(getBackStackChangedListener());
        activityMainBinding.navView.setNavigationItemSelectedListener(this);
        activityMainBinding.navView.getMenu().getItem(NAV_ITEMS.NAV_ITEM_SCAN.ordinal()).setChecked(true);
        setSupportActionBar(appBarMainBinding.toolbar);
        setupDrawerToggle();
        launchScanFragment();

        final Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_WIDGET_REQUESTED_PRODUCT)) {
            replaceFragment(new ProductListFragment(), ProductListFragment.TAG);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        createDoubleTapToExitListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        compositeDisposable.dispose();
    }

    @Override
    public void onBackPressed() {
        final DrawerLayout drawer = activityMainBinding.drawerLayout;

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (fragmentManager.getBackStackEntryCount() == 0) {
            backButtonClickSource.onNext(true);
        } else if (isBackFromDetails) {
            onBackFromProductDetails();
        } else {
            syncSelectedNavItem();
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_scan:
                replaceFragment(BarcodeScanFragment.getInstance(), BarcodeScanFragment.TAG);
                break;
            case R.id.nav_favorite_products:
                replaceFragment(new ProductListFragment(), ProductListFragment.TAG);
                break;
            case R.id.nav_favorite_stores:
                replaceFragment(new StoreListFragment(), StoreListFragment.TAG);
                break;
        }

        activityMainBinding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void syncSelectedNavItem() {
        @IdRes final int id = appBarMainBinding.fragmentPlaceholder.getId();

        switch (id) {
            case R.id.nav_scan:
                activityMainBinding.navView.getMenu().getItem(NAV_ITEMS.NAV_ITEM_SCAN.ordinal()).setChecked(true);
                break;
            case R.id.nav_favorite_products:
                activityMainBinding.navView.getMenu().getItem(NAV_ITEMS.NAV_ITEM_PRODUCTS.ordinal()).setChecked(true);
                break;
            case R.id.nav_favorite_stores:
                activityMainBinding.navView.getMenu().getItem(NAV_ITEMS.NAV_ITEM_STORES.ordinal()).setChecked(true);
                break;
        }
    }

    private void setupDrawerToggle() {
        drawerToggle = new ActionBarDrawerToggle(
                this, activityMainBinding.drawerLayout, appBarMainBinding.toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        activityMainBinding.drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
    }

    private void launchScanFragment() {
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        final BarcodeScanFragment barcodeScanFragment = BarcodeScanFragment.getInstance();

        transaction.replace(R.id.fragment_placeholder, barcodeScanFragment, BarcodeScanFragment.TAG);
        transaction.commit();
    }

    private void replaceFragment(@NonNull Fragment fragment, @Nullable String tag) {
        if (fragment.isAdded()) {
            return;
        }
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        }

        final FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
        transaction.replace(R.id.fragment_placeholder, fragment, tag);
        if (!BarcodeScanFragment.TAG.equals(tag)) {
            transaction.addToBackStack(tag);
        }
        transaction.commit();
    }

    private void createDoubleTapToExitListener() {
        compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(backButtonClickSource
                .debounce(100, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(event -> Toast.makeText(this, R.string.exit_toast, Toast.LENGTH_SHORT).show())
                .timeInterval(TimeUnit.MILLISECONDS)
                .skip(1)
                .filter(interval -> interval.time() < EXIT_TIMEOUT)
                .subscribe(interval -> finish()));
    }

    private FragmentManager.OnBackStackChangedListener getBackStackChangedListener() {
        return () -> {
            final int lastIndex = fragmentManager.getBackStackEntryCount() - 1;
            if (lastIndex < 0) {
                return;
            }
            final FragmentManager.BackStackEntry lastEntry = fragmentManager.getBackStackEntryAt(lastIndex);

            if (ProductDetailFragment.TAG.equals(lastEntry.getName())) {
                onProductDetailsOpen();
            }
        };
    }

    private void onProductDetailsOpen() {
        isBackFromDetails = true;
        animateBurgerToArrow(0, 1);
        appBarMainBinding.toolbar.setNavigationOnClickListener(v -> onBackFromProductDetails());
        activityMainBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    private void onBackFromProductDetails() {
        isBackFromDetails = false;
        super.onBackPressed();
        animateBurgerToArrow(1, 0);
        appBarMainBinding.toolbar.setNavigationOnClickListener(null);

        setupDrawerToggle();
        activityMainBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    private void animateBurgerToArrow(int start, int end) {
        final ValueAnimator anim = ValueAnimator.ofFloat(start, end);
        anim.addUpdateListener(valueAnimator -> {
            float slideOffset = (Float) valueAnimator.getAnimatedValue();
            drawerToggle.onDrawerSlide(activityMainBinding.drawerLayout, slideOffset);
        });
        anim.setInterpolator(new DecelerateInterpolator());
        anim.setDuration(500);
        anim.start();
    }
}
