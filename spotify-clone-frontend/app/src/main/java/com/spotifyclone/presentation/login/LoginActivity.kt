package com.spotifyclone.presentation.login

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import com.spotifyclone.R
import com.spotifyclone.presentation.base.BaseActivity
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.view.*

class LoginActivity : BaseActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_login)

        super.onCreate(savedInstanceState)
    }

    override fun initComponents() {
        val layout: ViewGroup = activityLogin
        with(layout) {
            spotifyBrand.text = getString(R.string.login_spotify_brand)
            signUpButton.text = getString(R.string.login_button_sign_up)
            facebookButton.text = getString(R.string.login_button_facebook)
            loginButton.text = getString(R.string.login_button_login)
        }


        layout.loginButton.setOnClickListener{
            val intent = Intent(this@LoginActivity, CredentialsActivity::class.java)
            this@LoginActivity.startActivity(intent)
        }
    }
}