package com.spotifyclone.tools.permissions

import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.spotifyclone.components.dialogs.CustomDialog

class AppPermissions {
    companion object {

        fun checkMultiplePermissions(
            activity: ComponentActivity,
            permissions: List<String>,
            onGranted: () -> Unit,
            onRevoke: () -> Unit,
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

            val notGrantedPermissions: List<String> =
                permissions.filterNot { permission -> grantedPermissions.contains(permission) }

            if (grantedPermissions.size == permissions.size) {
                onGranted.invoke()
            } else {
                requestPermissionLauncher(activity, notGrantedPermissions, onGranted, onRevoke)
            }
        }

        private fun requestPermissionLauncher(activity: ComponentActivity, notGrantedPermissionsInit: List<String>, onGranted: () -> Unit, onRevoke: () -> Unit) {
            var notGrantedPermissions = notGrantedPermissionsInit
            activity.registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { resultList: MutableMap<String, Boolean> ->
                notGrantedPermissions = resultList.filterNot { permission -> permission.value }
                    .map { permission -> permission.key }

                if (notGrantedPermissions.isEmpty()) {
                    onGranted.invoke()
                } else {
                    onRevoke.invoke()
                }
            }.launch(notGrantedPermissions.toTypedArray())
        }

    }
}
