package com.and1ss.onlinechat.view.main

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel

class MainActivityViewModel @ViewModelInject constructor(
    application: Application
) : AndroidViewModel(application) {
    enum class Screens {
        FRIENDS, GROUP_CHATS, PRIVATE_CHATS
    }

    var state = Screens.GROUP_CHATS
}