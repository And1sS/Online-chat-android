package com.and1ss.onlinechat.view.auth.login

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.and1ss.onlinechat.api.dto.LoginInfoDTO
import com.and1ss.onlinechat.api.rest.RestWrapper

class LoginViewModel @ViewModelInject constructor(
    val restWrapper: RestWrapper
) : ViewModel() {
    enum class State { INITIAL, LOGGED_IN, FAILED }

    private val _loginConfirmation: MutableLiveData<State> =
        MutableLiveData()
    val loginConfirmation: LiveData<State>
        get() = _loginConfirmation

    var login: String = ""
    var password: String = ""

    suspend fun login() {
        try {
            restWrapper.login(
                LoginInfoDTO(
                    login, password
                )
            )
            _loginConfirmation.postValue(State.LOGGED_IN)
        } catch (e: Exception) {
            _loginConfirmation.postValue(State.FAILED)
        }
    }
}