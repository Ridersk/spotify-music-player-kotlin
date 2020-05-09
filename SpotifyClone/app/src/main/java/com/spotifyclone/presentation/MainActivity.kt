package com.spotifyclone.presentation

import android.content.Intent
import android.os.Bundle
import com.spotifyclone.presentation.base.BaseActivity
import com.spotifyclone.presentation.home.HomeActivity
import com.spotifyclone.presentation.login.LoginActivity
import com.spotifyclone.presentation.playlist.LikedSongsActivity


class MainActivity : BaseActivity() {

    val context = this@MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val intent: Intent
        val userLogged = false
        val development = true

        if (development) {
            intent = getActivityBeingTested()
        }
        else {
            intent = if (userLogged) {
                Intent(context, HomeActivity::class.java)
            } else {
                Intent(context, LoginActivity::class.java)
            }
        }

        context.startActivity(intent)
    }

    fun getActivityBeingTested() : Intent {
        return Intent(context, LikedSongsActivity::class.java)
    }
}
