package com.spotifyclone.presentation.login

import android.os.Bundle
import android.view.ViewGroup
import com.example.spotifyclone.R
import com.spotifyclone.presentation.base.BaseActivity
import com.spotifyclone.presentation.base.ToolbarParameters
import kotlinx.android.synthetic.main.activity_credentials.*
import kotlinx.android.synthetic.main.include_toolbar.*

class CredentialsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_credentials)

        setupToolbar(
            ToolbarParameters(
                toolbar =  toolbarMain,
                titleIdRes =  R.string.credentials_toolbar_title,
                option1Idres =  R.drawable.ic_back
            )
        )

        val layout: ViewGroup = activityCredentials
        setForm(layout)
    }

    fun setForm(layout: ViewGroup) {
        with(layout) {
            labelEmailInput.text = getString(R.string.credentials_label_email)
            labelPasswordInput.text = getString(R.string.credentials_label_password)
            loginButton.text = getString(R.string.credentials_button_login)
            forgotPassword.text = getString(R.string.credentials_button_forgot_password)
        }
    }
}
