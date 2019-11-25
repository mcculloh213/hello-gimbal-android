package com.gimbal.hello_gimbal_android;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.gimbal.android.Gimbal;

class GimbalPermissionsManager {
    static final int REQUEST_COARSE_LOCATION_CODE = 0b100;
    static final int REQUEST_FINE_LOCATION_CODE = 0b010;
    static final int REQUEST_BLUETOOTH_CODE = 0b001;

    static final int REQUEST_PERMISSIONS_MASK = REQUEST_COARSE_LOCATION_CODE
            | REQUEST_FINE_LOCATION_CODE
            | REQUEST_BLUETOOTH_CODE;
    static int checkRequiredPermissions(Context context) {
        int mask = PackageManager.PERMISSION_GRANTED;
        mask |= mapPermissionToMask(context, Manifest.permission.ACCESS_COARSE_LOCATION, REQUEST_COARSE_LOCATION_CODE);
        mask |= mapPermissionToMask(context, Manifest.permission.ACCESS_FINE_LOCATION, REQUEST_FINE_LOCATION_CODE);
        mask |= mapPermissionToMask(context, Manifest.permission.BLUETOOTH, REQUEST_BLUETOOTH_CODE);
        return mask;
    }
    static void requestPermissions(Activity activity, int code) {
        ActivityCompat.requestPermissions(activity,
                new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.BLUETOOTH
                }, code);
    }
    private static int mapPermissionToMask(Context context, String permission, int code) {
        int mask = 0;
        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED) {
            mask |= code;
        }
        return mask;
    }
}
