package com.spotifyclone.components.dialogs

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.TextView
import com.spotifyclone.R
import kotlinx.android.synthetic.main.dialog_alert.*

class CustomDialog private constructor(
    private val builder: Builder,
    private val activity: Activity
) : Dialog(activity, R.style.customDialog), View.OnClickListener {

    private lateinit var txtTitle: TextView
    private lateinit var txtDescription: TextView
    private lateinit var btnMain: Button
    private lateinit var btnOptional: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.setContentView(R.layout.dialog_alert)
        super.onCreate(savedInstanceState)
        initComponents()
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.dialogBtnMain -> if (!builder.btnMainCloseDialog) {
                builder.btnMainCallback.invoke()
            } else {
                super.cancel()
                builder.btnMainCallback.invoke()
            }
            R.id.dialogBtnOptional -> if (!builder.btnOptionalCloseDialog) {
                builder.btnOptionalCallback.invoke()
            } else {
                super.cancel()
                builder.btnOptionalCallback.invoke()
            }
            else -> super.cancel()
        }
    }

    private fun initComponents() {
        txtTitle = dialogTxtTitle
        txtDescription = dialogTxtDescription
        btnMain = dialogBtnMain
        btnOptional = dialogBtnOptional

        txtTitle.text = builder.title

        if (!builder.description.isNullOrEmpty()) {
            txtDescription.text = builder.description
        } else {
            txtDescription.visibility = View.GONE
        }

        if (!builder.btnMainTxt.isNullOrEmpty()) {
            btnMain.text = builder.btnMainTxt
            btnMain.setOnClickListener(this)
        } else {
            btnMain.visibility = View.GONE
        }

        if (!builder.btnOptionalTxt.isNullOrEmpty()) {
            btnOptional.text = builder.btnOptionalTxt
            btnOptional.setOnClickListener(this)
        } else {
            btnOptional.visibility = View.GONE
        }
    }

    data class Builder(private val activity: Activity) {
        var title: String = ""
            private set
        var description: String? = null
            private set
        var btnMainTxt: String? = null
            private set
        var btnMainCloseDialog: Boolean = true
        var btnMainCallback: () -> Unit = {}
            private set
        var btnOptionalTxt: String? = null
            private set
        var btnOptionalCloseDialog: Boolean = true
        var btnOptionalCallback: () -> Unit = {}
            private set

        fun title(title: String) = apply { this.title = title }
        fun description(description: String) = apply { this.description = description }
        fun mainButton(txt: String, close: Boolean = true, callback: () -> Unit = {}) = apply {
            this.btnMainTxt = txt
            this.btnMainCloseDialog = close
            this.btnMainCallback = callback
        }

        fun optionalButton(txt: String, close: Boolean = true, callback: () -> Unit = {}) = apply {
            this.btnOptionalTxt = txt
            this.btnOptionalCloseDialog = close
            this.btnOptionalCallback = callback
        }

        fun build() = CustomDialog(this, activity)
    }
}
