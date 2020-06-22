package com.spotifyclone.tools.permissions

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker

class AppPermissions {
    companion object {
        fun isStoragePermissionGranted(
            context: Context,
            activity: Activity
        ): Boolean {
            if (Build.VERSION.SDK_INT >= 23) {
                return if (PermissionChecker.checkSelfPermission(
                        context,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PermissionChecker.PERMISSION_GRANTED
                ) {
                    Log.v(ContentValues.TAG, "Permission is granted")
                    true
                } else {
                    Log.v(ContentValues.TAG, "Permission is revoked")
                    ActivityCompat.requestPermissions(
                        activity,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        1
                    )
                    false
                }
            } else { //permission is automatically granted on sdk<23 upon installation
                Log.v(ContentValues.TAG, "Permission is granted")
                return true
            }
        }
    }
}
