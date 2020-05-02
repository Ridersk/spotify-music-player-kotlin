package com.spotifyclone.presentation

import android.content.Intent
import android.os.Bundle
import com.spotifyclone.presentation.base.BaseActivity
import com.spotifyclone.presentation.home.HomeActivity
import com.spotifyclone.presentation.login.LoginActivity


class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent: Intent
        val userLogged = false

        intent = if(userLogged) {
            Intent(this@MainActivity, HomeActivity::class.java)
        } else {
            Intent(this@MainActivity, LoginActivity::class.java)
        }
        this@MainActivity.startActivity(intent)
    }
}
