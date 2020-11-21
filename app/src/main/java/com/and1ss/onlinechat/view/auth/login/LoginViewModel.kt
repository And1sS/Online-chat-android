package com.and1ss.onlinechat.view.auth.login

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.and1ss.onlinechat.api.dto.LoginInfoDTO
import com.and1ss.onlinechat.api.rest.rest_wrapper.RestWrapper

class LoginViewModel @ViewModelInject constructor(
    val restWrapper: RestWrapper
) : ViewModel() {
    var login: String = ""
    var password: String = ""

    suspend fun login() {
        restWrapper.login(
            LoginInfoDTO(
                login, password
            )
        )
    }
}