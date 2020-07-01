package com.spotifyclone.tools.permissions

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.spotifyclone.components.dialogs.CustomDialog

class AppPermissions {
    companion object {

        fun checkMultiplePermissions(
            activity: Activity,
            permissions: List<String>,
            userNotificationDialog: CustomDialog? = null
        ): List<String> {
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

            return permissions.filterNot { permission -> grantedPermissions.contains(permission) }
        }

    }
}
