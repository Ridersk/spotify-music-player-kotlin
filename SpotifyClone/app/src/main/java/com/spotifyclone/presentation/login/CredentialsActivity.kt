package com.spotifyclone.presentation.login

import android.os.Bundle
import com.example.spotifyclone.R
import com.spotifyclone.presentation.base.BaseActivity
import kotlinx.android.synthetic.main.activity_credentials.*

class CredentialsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_credentials)

        labelEmailInput.text = getString(R.string.credentials_label_email)
        labelPasswordInput.text = getString(R.string.credentials_label_password)
        loginButton.text = getString(R.string.credentials_button_login)
        forgotPassword.text = getString(R.string.credentials_button_forgot_password)
    }
}
