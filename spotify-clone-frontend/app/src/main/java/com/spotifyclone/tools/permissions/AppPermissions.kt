package com.spotifyclone.tools.permissions

import android.app.Activity
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.spotifyclone.components.dialogs.CustomDialog

class AppPermissions {
    companion object {
        fun checkPermission(
            activity: Activity,
            permission: String,
            onGranted: () -> Unit,
            onRevokedCallback: ActivityResultLauncher<String>,
            userNotificationDialog: CustomDialog? = null
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
                    userNotificationDialog?.let { dialog -> dialog.show() }
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
            onRevokedCallback: ActivityResultLauncher<Array<String>>,
            userNotificationDialog: CustomDialog? = null
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
                    ) -> {
                        userNotificationDialog?.let { dialog -> dialog.show() }
                    }
                }
            }

            if (grantedPermissions.size == permissions.size) {
                onGranted.invoke()
            } else {
                val notGrantedPermissions: List<String> =
                    permissions.filterNot { permission -> grantedPermissions.contains(permission) }
                onRevokedCallback.launch(
                    Array(notGrantedPermissions.size) { i -> notGrantedPermissions[i] }
                )
            }
        }

    }
}
