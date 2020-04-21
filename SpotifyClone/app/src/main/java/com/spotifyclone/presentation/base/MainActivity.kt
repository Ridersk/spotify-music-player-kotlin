package com.spotifyclone.presentation.base

import android.content.Intent
import android.os.Bundle
import com.example.spotifyclone.R
import com.spotifyclone.presentation.login.LoginActivity

class MainActivity: BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val intent = Intent(this@MainActivity, LoginActivity::class.java)
        this@MainActivity.startActivity(intent)
    }
}
