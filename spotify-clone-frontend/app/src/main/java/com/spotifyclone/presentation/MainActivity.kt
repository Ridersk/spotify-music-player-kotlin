package com.spotifyclone.presentation

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker
import com.spotifyclone.presentation.base.BaseActivity
import com.spotifyclone.presentation.home.HomeActivity
import com.spotifyclone.presentation.login.LoginActivity
import com.spotifyclone.presentation.playlist.LikedSongsActivity


class MainActivity : BaseActivity() {

    val context = this@MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        isStoragePermissionGranted()
        if (!isStoragePermissionGranted()) {
            // intent = activityRequestPermissions
        }

        super.onCreate(savedInstanceState)
    }

    override fun initComponents() {
        val intent: Intent = if (DEVELOPMENT) {
            getActivityBeingTested()
        } else {
            if (USER_LOGGED) {
                Intent(context, HomeActivity::class.java)
            } else {
                Intent(context, LoginActivity::class.java)
            }
        }

        context.startActivity(intent)
    }

    private fun isStoragePermissionGranted(): Boolean {
        if (Build.VERSION.SDK_INT >= 23) {

            return if (PermissionChecker.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PermissionChecker.PERMISSION_GRANTED
            ) {
                Log.v(ContentValues.TAG, "Permission is granted")
                true
            } else {

                Log.v(ContentValues.TAG, "Permission is revoked")
                ActivityCompat.requestPermissions(
                    this,
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

    private fun getActivityBeingTested(): Intent {
        return Intent(context, LikedSongsActivity::class.java)
    }

    companion object {
        private const val USER_LOGGED = true
        private const val DEVELOPMENT = true
    }
}