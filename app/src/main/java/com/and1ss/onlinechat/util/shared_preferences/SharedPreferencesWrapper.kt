package com.and1ss.onlinechat.util.shared_preferences

interface SharedPreferencesWrapper {
    suspend fun saveAccessToken(accessToken: String)
    suspend fun getAccessToken(): String
}