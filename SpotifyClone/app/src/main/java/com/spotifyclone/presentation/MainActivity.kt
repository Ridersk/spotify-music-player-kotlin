package com.spotifyclone.presentation

import android.content.Intent
import android.os.Bundle
import com.example.spotifyclone.R
import com.spotifyclone.presentation.base.BaseActivity
import com.spotifyclone.presentation.home.HomeActivity
import com.spotifyclone.presentation.login.CredentialsActivity
import com.spotifyclone.presentation.login.LoginActivity

class MainActivity: BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val intent = Intent(this@MainActivity, LoginActivity::class.java)
//        this@MainActivity.startActivity(intent)
//        val intent = Intent(this@MainActivity, CredentialsActivity::class.java)
//        this@MainActivity.startActivity(intent)
        val intent = Intent(this@MainActivity, HomeActivity::class.java)
        this@MainActivity.startActivity(intent)
    }
}
