package com.and1ss.onlinechat.view.auth.login

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.and1ss.onlinechat.api.dto.AccessTokenDTO
import com.and1ss.onlinechat.api.dto.LoginInfoDTO
import com.and1ss.onlinechat.api.model.AccountInfo
import com.and1ss.onlinechat.api.rest.rest_wrapper.RestWrapper
import java.lang.Exception

class LoginViewModel @ViewModelInject constructor(
    val restWrapper: RestWrapper
) : ViewModel() {
    var login: String = ""
    var password: String = ""

    suspend fun login() {
        val token = restWrapper.getApi().login(
            LoginInfoDTO(
                login = login,
                password = password
            )
        ).mapToAccessToken()
        restWrapper.saveAccessToken(token)
        restWrapper.saveMyAccount(restWrapper.getMyAccount())
    }
}