package com.itrided.android.barracoda.permissions;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.itrided.android.barracoda.permissions.PermissionManager.Permission.CAMERA;
import static com.itrided.android.barracoda.permissions.PermissionManager.Permission.COARSE_LOCATION;
import static com.itrided.android.barracoda.permissions.PermissionManager.Permission.FINE_LOCATION;

public final class PermissionManager {

    // permission request codes need to be < 256
    public static final int RC_HANDLE_PERM = 100;

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
            CAMERA,
            COARSE_LOCATION,
            FINE_LOCATION
    })
    public @interface Permission {
        String CAMERA = Manifest.permission.CAMERA;
        String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
        String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    }

    private PermissionManager() {
    }

    public static boolean checkPermission(@NonNull final Context context,
                                          @NonNull @Permission final String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestPermission(@NonNull final Activity activity,
                                         @NonNull @Permission final String permission) {

        final boolean shouldShowDialog = ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);

        if (shouldShowDialog) {
            final String[] handlePermission = new String[]{permission};
            ActivityCompat.requestPermissions(activity, handlePermission, RC_HANDLE_PERM);
        }
    }

    public static void requestPermission(@NonNull final Fragment fragment,
                                         @NonNull @Permission final String permission) {

        final boolean shouldShowDialog = fragment.shouldShowRequestPermissionRationale(permission);

        if (shouldShowDialog) {
            final String[] handlePermission = new String[]{permission};
            fragment.requestPermissions(handlePermission, RC_HANDLE_PERM);
        }
    }
}
