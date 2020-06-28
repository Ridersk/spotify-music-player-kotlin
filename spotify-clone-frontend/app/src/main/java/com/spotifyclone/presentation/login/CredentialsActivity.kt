package com.spotifyclone.presentation.login

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import com.spotifyclone.R
import com.spotifyclone.presentation.MainActivity
import com.spotifyclone.presentation.base.BaseActivity
import com.spotifyclone.presentation.base.ToolbarParameters
import kotlinx.android.synthetic.main.activity_credentials.*
import kotlinx.android.synthetic.main.activity_credentials.view.*
import kotlinx.android.synthetic.main.activity_login.view.loginButton
import kotlinx.android.synthetic.main.include_toolbar.*

class CredentialsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_credentials)

        setupToolbar(
            ToolbarParameters(
                toolbar = toolbarMain,
                title = getString(R.string.credentials_toolbar_title),
                option1 = Pair(R.drawable.ic_back, {})
            )
        )

        super.onCreate(savedInstanceState)
    }

    override fun initComponents() {
        val layout: ViewGroup = activityCredentials
        layout.labelEmailInput.text = getString(R.string.credentials_label_email)
        layout.labelPasswordInput.text = getString(R.string.credentials_label_password)
        layout.loginButton.text = getString(R.string.credentials_button_login)
        layout.forgotPassword.text = getString(R.string.credentials_button_forgot_password)

        layout.loginButton.setOnClickListener {
            val intent = Intent(this@CredentialsActivity, MainActivity::class.java)
            this@CredentialsActivity.startActivity(intent)
        }
    }

}
