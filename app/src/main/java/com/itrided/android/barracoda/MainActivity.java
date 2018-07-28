package com.itrided.android.barracoda;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.itrided.android.barracoda.barcode.BarcodeScanFragment;
import com.itrided.android.barracoda.databinding.ActivityMainBinding;
import com.itrided.android.barracoda.databinding.AppBarMainBinding;
import com.itrided.android.barracoda.products.ProductListFragment;

import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.PublishSubject;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int NAV_ITEM_SCAN = 0;
    private static final long EXIT_TIMEOUT = 2000;
    private CompositeDisposable compositeDisposable;
    private PublishSubject<Boolean> backButtonClickSource = PublishSubject.create();

    private ActivityMainBinding activityMainBinding;
    private AppBarMainBinding appBarMainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        appBarMainBinding = activityMainBinding.appBarScan;

        activityMainBinding.navView.setNavigationItemSelectedListener(this);
        activityMainBinding.navView.getMenu().getItem(NAV_ITEM_SCAN).setChecked(true);
        setSupportActionBar(appBarMainBinding.toolbar);
        setupDrawerToggle();
        launchScanFragment();
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
        } else if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            backButtonClickSource.onNext(true);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_scan:
                replaceFragment(BarcodeScanFragment.getInstance());
                break;
            case R.id.nav_favorite_products:
                replaceFragment(ProductListFragment.getInstance());
                break;
            case R.id.nav_favorite_stores:
                break;
        }

        activityMainBinding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setupDrawerToggle() {
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, activityMainBinding.drawerLayout, appBarMainBinding.toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        activityMainBinding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void launchScanFragment() {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        final BarcodeScanFragment barcodeScanFragment = BarcodeScanFragment.getInstance();

        transaction.replace(R.id.fragment_placeholder, barcodeScanFragment, BarcodeScanFragment.TAG);
        transaction.commit();
    }

    private void replaceFragment(@NonNull Fragment fragment) {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.replace(R.id.fragment_placeholder, fragment, fragment.getTag());
        if (!BarcodeScanFragment.TAG.equals(fragment.getTag())) {
            Log.d(TAG, "replaceFragment: " + fragment.getTag());
            transaction.addToBackStack(null);
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
}
