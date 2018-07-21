package com.itrided.android.barracoda;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.MenuItem;
import android.widget.ImageView;

import com.itrided.android.barracoda.databinding.ActivityScanBinding;
import com.itrided.android.barracoda.databinding.AppBarScanBinding;
import com.itrided.android.barracoda.databinding.ContentScanBinding;
import com.squareup.picasso.Picasso;

public class ScanActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ActivityScanBinding activityScanBinding;
    private AppBarScanBinding appBarScanBinding;
    private ContentScanBinding contentScanBinding;
    private BarcodeDetector barcodeDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityScanBinding = DataBindingUtil.setContentView(this, R.layout.activity_scan);
        appBarScanBinding = activityScanBinding.appBarScan;
        contentScanBinding = appBarScanBinding.contentScan;

        activityScanBinding.navView.setNavigationItemSelectedListener(this);
        setSupportActionBar(appBarScanBinding.toolbar);
        setupDrawerToggle();
        setupBarcodeDetector();

        appBarScanBinding.fab.setOnClickListener(view ->
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show());

        contentScanBinding.processBtn.setOnClickListener(v -> runBarcodeDemo());
    }

    private void setupBarcodeDetector() {
        barcodeDetector = new BarcodeDetector.Builder(getApplicationContext())
                .setBarcodeFormats(Barcode.DATA_MATRIX | Barcode.QR_CODE)
                .build();
        if (!barcodeDetector.isOperational()) {
            contentScanBinding.contentTv.setText(R.string.barcode_detector_error);
        }
    }

    private void runBarcodeDemo() {
        final Bitmap myBitmap = BitmapFactory.decodeResource(
                getApplicationContext().getResources(),
                R.drawable.puppy);

        final Frame frame = new Frame.Builder().setBitmap(myBitmap).build();
        final SparseArray<Barcode> barcodes = barcodeDetector.detect(frame);
        final Barcode barcode = barcodes.valueAt(0);

        contentScanBinding.contentTv.setText(barcode.rawValue);
        final String url = barcode.rawValue.replaceAll("^.*\\s", "");

        Picasso.get()
                .load(url)
                .into(contentScanBinding.barcodeIv);
    }

    private void setupDrawerToggle() {
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, activityScanBinding.drawerLayout, appBarScanBinding.toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        activityScanBinding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
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
}
