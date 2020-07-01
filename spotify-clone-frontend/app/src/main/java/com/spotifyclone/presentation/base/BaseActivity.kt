package com.spotifyclone.presentation.base

import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.spotifyclone.R
import com.spotifyclone.components.dialogs.CustomDialog
import com.spotifyclone.tools.permissions.AppPermissions
import kotlinx.android.synthetic.main.include_toolbar.*
import kotlinx.android.synthetic.main.include_toolbar.view.*


abstract class BaseActivity : AppCompatActivity() {

    private var notGrantedPermissions = listOf<String>()
    private lateinit var requiredPermissionDialog: CustomDialog
    private var callInitComponentsWithoutPermission = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initComponents()
    }

    protected fun setupToolbar(toolbarArgs: ToolbarParameters) {
        setSupportActionBar(buildToolbar(toolbarArgs))
    }

    fun updateToolbar (toolbarArgs: ToolbarParameters) {
        buildToolbar(toolbarArgs)
    }

    private fun buildToolbar (toolbarArgs: ToolbarParameters): Toolbar {
        val toolbar: Toolbar = toolbarMain
        with(toolbar) {
            if (toolbarArgs.title != null && toolbarArgs.title.isNotEmpty()) {
                textToolbarTitle.visibility = View.VISIBLE
                textToolbarTitle.text = toolbarArgs.title
            } else {
                textToolbarTitle.visibility = View.GONE
            }

            if (toolbarArgs.subTitle != null && toolbarArgs.subTitle.isNotEmpty()) {
                textToolbarSubtitle.visibility = View.VISIBLE
                textToolbarSubtitle.text = toolbarArgs.subTitle
            } else {
                textToolbarSubtitle.visibility = View.GONE
            }

            if (toolbarArgs.option1 != null && toolbarArgs.option1.first > 0) {
                iconOption1.setImageDrawable(getDrawable(toolbarArgs.option1.first))
                iconOption1.setOnClickListener {
                    toolbarArgs.option1.second.invoke()
                }
                iconOption1.visibility = View.VISIBLE
            } else {
                iconOption1.setImageDrawable(null)
                iconOption1.setOnClickListener {}
                iconOption1.visibility = View.GONE
            }


            if (toolbarArgs.option2 != null && toolbarArgs.option2.first > 0) {
                iconOption2.setImageDrawable(getDrawable(toolbarArgs.option2.first))
                iconOption2.setOnClickListener {
                    toolbarArgs.option2.second.invoke()
                }
                iconOption2.visibility = View.VISIBLE
            } else {
                iconOption2.setImageDrawable(null)
                iconOption2.setOnClickListener {}
                iconOption2.visibility = View.GONE
            }

            if (toolbarArgs.option3 != null && toolbarArgs.option3.first > 0) {
                iconOption3.setImageDrawable(getDrawable(toolbarArgs.option3.first))
                iconOption3.setOnClickListener {
                    toolbarArgs.option3.second.invoke()
                }
                iconOption3.visibility = View.VISIBLE
            } else {
                iconOption3.setImageDrawable(null)
                iconOption3.setOnClickListener {}
                iconOption3.visibility = View.GONE
            }
        }
        return toolbar
    }

    fun addRequiredPermissionDialog(title: String, description: String) {
        this.requiredPermissionDialog = CustomDialog.Builder(this)
            .title(title)
            .description(description)
            .mainButton(getString(R.string.dialog_alert_btn_permissions_allow_storage_access)) {
                requestMultiplePermissionLauncher.launch(
                    Array(notGrantedPermissions.size) { i -> notGrantedPermissions[i] }
                )
            }
            .optionalButton(getString(R.string.dialog_alert_btn_permissions_cancel)) {
                if (callInitComponentsWithoutPermission) {
                    initComponents()
                }
            }
            .build()
    }

    private lateinit var callbackRequestPermission: () -> Unit

    fun requestPermissions(requiredPermissions: List<String>, callback: () -> Unit) {
        callbackRequestPermission = callback
        notGrantedPermissions = AppPermissions.checkMultiplePermissions(this, requiredPermissions)
        if (notGrantedPermissions.isNotEmpty()) {
            requestMultiplePermissionLauncher.launch(
                Array(notGrantedPermissions.size) { i -> notGrantedPermissions[i] }
            )
        } else  {
            callback.invoke()
        }
    }

    private val requestMultiplePermissionLauncher: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { resultList: MutableMap<String, Boolean> ->
            notGrantedPermissions = resultList.filterNot { permission -> permission.value }
                .map { permission -> permission.key }

            if (notGrantedPermissions.isEmpty()) {
                callbackRequestPermission.invoke()
            } else {
                if (this::requiredPermissionDialog.isInitialized) {
                    requiredPermissionDialog.show()
                }
            }
        }

    protected open fun initComponents() {}
}