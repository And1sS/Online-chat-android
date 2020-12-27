package com.and1ss.onlinechat.view.auth.register

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.and1ss.onlinechat.api.dto.RegisterInfoDTO
import com.and1ss.onlinechat.api.rest.RestWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class RegisterViewModel @ViewModelInject constructor(
    private val restWrapper: RestWrapper,
    application: Application
) : AndroidViewModel(application) {
    enum class State { INITIAL, REGISTERED, FAILED }

    private val _registrationConfirmation: MutableLiveData<State> = MutableLiveData()
    val registrationConfirmation: LiveData<State>
        get() = _registrationConfirmation

    var login = ""
    var password = ""
    var name = ""
    var surname = ""

    fun register() = viewModelScope.launch {
        login = login.trim()
        password = password.trim()
        name = name.trim()
        surname = surname.trim()

        if (login.isEmpty()
            || password.isEmpty()
            || name.isEmpty()
            || surname.isEmpty()
        ) {
            _registrationConfirmation.postValue(State.FAILED)
            return@launch
        }

        withContext(Dispatchers.IO) {
            try {
                restWrapper.getApi()
                    .register(RegisterInfoDTO(login, password, name, surname))
                _registrationConfirmation.postValue(State.REGISTERED)
            } catch (e: Exception) {
            }
        }
    }
}