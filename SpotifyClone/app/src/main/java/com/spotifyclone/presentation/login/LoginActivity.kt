package com.spotifyclone.presentation.login

import android.os.Bundle
import com.example.spotifyclone.R
import com.spotifyclone.presentation.base.BaseActivity
import kotlinx.android.synthetic.main.login_activity.*

class LoginActivity : BaseActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        spotifyBrand.text = getString(R.string.login_spotify_brand)
        signUpButton.text = getString(R.string.login_button_sign_up)
        facebookButton.text = getString(R.string.login_button_facebook)
        loginButton.text = getString(R.string.login_button_login)
    }
}