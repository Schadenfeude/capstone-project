package com.itrided.android.barracoda;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;

import com.itrided.android.barracoda.barcode.BarcodeCaptureController;
import com.itrided.android.barracoda.databinding.ActivityScanBinding;
import com.itrided.android.barracoda.databinding.AppBarScanBinding;
import com.itrided.android.barracoda.databinding.ContentScanBinding;

import static com.itrided.android.barracoda.barcode.BarcodeCaptureController.RC_HANDLE_CAMERA_PERM;

public class BarcodeScanActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = BarcodeScanActivity.class.getSimpleName();

    private ActivityScanBinding activityScanBinding;
    private AppBarScanBinding appBarScanBinding;
    private ContentScanBinding contentScanBinding;

    private BarcodeCaptureController captureController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityScanBinding = DataBindingUtil.setContentView(this, R.layout.activity_scan);
        appBarScanBinding = activityScanBinding.appBarScan;
        contentScanBinding = appBarScanBinding.contentScan;

        captureController = new BarcodeCaptureController(this, contentScanBinding);
        getLifecycle().addObserver(captureController);

        activityScanBinding.navView.setNavigationItemSelectedListener(this);
        setSupportActionBar(appBarScanBinding.toolbar);
        setupDrawerToggle();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return captureController.onTouchEvent(event);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        captureController.onRequestPermissionsResult(grantResults);
    }

    @Override
    public void onBackPressed() {
        final DrawerLayout drawer = activityScanBinding.drawerLayout;
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        activityScanBinding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setupDrawerToggle() {
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, activityScanBinding.drawerLayout, appBarScanBinding.toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        activityScanBinding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }
}
