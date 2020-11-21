package com.and1ss.onlinechat.util.shared_preferences

interface SharedPreferencesWrapper {
    fun saveAccessToken(accessToken: String)
    fun getAccessToken(): String?
}