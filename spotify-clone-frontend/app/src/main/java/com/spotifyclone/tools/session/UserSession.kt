package com.spotifyclone.tools.session

class UserSession {
    companion object {
        const val USER_LOGGED_OFF = 0
        const val USER_LOGGED = 1
        fun getUserStatus(): Int {
            return USER_LOGGED
        }
    }
}