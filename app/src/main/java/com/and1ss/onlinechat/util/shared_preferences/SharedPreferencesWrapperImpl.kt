package com.and1ss.onlinechat.util.shared_preferences

import android.app.Activity
import android.content.Context
import javax.inject.Inject


private const val ACCESS_TOKEN = "access_token"
private const val SHARED_PREFERENCES = "access_token"

class SharedPreferencesWrapperImpl
@Inject constructor(private val applicationContext: Context) :
    SharedPreferencesWrapper {
    override fun saveAccessToken(accessToken: String) =
        applicationContext.getSharedPreferences(
            SHARED_PREFERENCES, Activity.MODE_PRIVATE
        ).edit()
            .putString(ACCESS_TOKEN, accessToken)
            .apply()

    override fun getAccessToken(): String? =
        applicationContext.getSharedPreferences(
            SHARED_PREFERENCES, Activity.MODE_PRIVATE
        ).getString(ACCESS_TOKEN, null)
}

