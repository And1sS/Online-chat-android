package com.and1ss.onlinechat

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Singleton

@Singleton
@HiltAndroidApp
class OnlineChatApp: Application()