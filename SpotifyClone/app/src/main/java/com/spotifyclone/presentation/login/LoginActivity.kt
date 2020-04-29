package com.spotifyclone.presentation.login

import android.os.Bundle
import android.view.ViewGroup
import com.example.spotifyclone.R
import com.spotifyclone.presentation.base.BaseActivity
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        val layout: ViewGroup = activityLogin
        with(layout) {
            spotifyBrand.text = getString(R.string.login_spotify_brand)
            signUpButton.text = getString(R.string.login_button_sign_up)
            facebookButton.text = getString(R.string.login_button_facebook)
            loginButton.text = getString(R.string.login_button_login)
        }
    }
}