package com.spotifyclone.tools.permissions

import android.app.Activity
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class AppPermissions {
    companion object {
        fun checkPermission(
            activity: Activity,
            permission: String,
            onGranted: () -> Unit,
            onRevokedCallback: ActivityResultLauncher<String>
        ) {
            when {
                ContextCompat.checkSelfPermission(
                    activity,
                    permission
                ) == PackageManager.PERMISSION_GRANTED -> {
                    onGranted.invoke()
                }
                ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    permission
                )
                -> {
                    println("Give the fuck permission")
                }
                else -> {
                    onRevokedCallback.launch(
                        permission
                    )
                }
            }
        }

        fun checkMultiplePermissions(
            activity: Activity,
            permissions: List<String>,
            onGranted: () -> Unit,
            onRevokedCallback: ActivityResultLauncher<Array<String>>
        ) {
            val grantedPermissions = mutableListOf<String>()
            for (permission in permissions) {
                when {
                    ContextCompat.checkSelfPermission(
                        activity,
                        permission
                    ) == PackageManager.PERMISSION_GRANTED -> {
                        grantedPermissions.add(permission)
                    }
                    ActivityCompat.shouldShowRequestPermissionRationale(
                        activity,
                        permission
                    )
                    -> {
                        println("Give the fuck permissions")
                    }
                }
            }

            if (grantedPermissions.size == permissions.size) {
                onGranted.invoke()
            } else {
                val notGrantedPermissions =
                    permissions.filterNot { permission -> grantedPermissions.contains(permission) }
                onRevokedCallback.launch(
                    Array(notGrantedPermissions.size) { i -> notGrantedPermissions[i] }
                )
            }
        }

    }
}
