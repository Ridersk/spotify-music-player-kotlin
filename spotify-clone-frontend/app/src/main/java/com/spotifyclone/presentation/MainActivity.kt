package com.spotifyclone.presentation

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.spotifyclone.presentation.home.HomeActivity
import com.spotifyclone.presentation.login.LoginActivity
import com.spotifyclone.tools.permissions.AppPermissions
import com.spotifyclone.tools.session.UserSession
import com.spotifyclone.tools.utils.desenvolutils.DesenvolUtils


class MainActivity : AppCompatActivity() {
    val context = this@MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent: Intent = if (DesenvolUtils.appInDesenvol()) {
            DesenvolUtils.getActivityBeingTested(context)
        } else {
            if (UserSession.getUserStatus() == UserSession.USER_LOGGED) {
                Intent(context, HomeActivity::class.java)
            } else {
                Intent(context, LoginActivity::class.java)
            }
        }

        context.startActivity(intent)
    }
}
