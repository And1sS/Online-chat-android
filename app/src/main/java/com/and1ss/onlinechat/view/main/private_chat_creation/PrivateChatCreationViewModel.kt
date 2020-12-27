package com.and1ss.onlinechat.view.main.private_chat_creation

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.and1ss.onlinechat.api.dto.PrivateChatCreationDTO
import com.and1ss.onlinechat.api.model.AccountInfo
import com.and1ss.onlinechat.api.rest.RestWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PrivateChatCreationViewModel @ViewModelInject constructor(
    private val restWrapper: RestWrapper,
    application: Application
) : AndroidViewModel(application) {
    private val _friends: MutableLiveData<List<AccountInfo>> = MutableLiveData(listOf())
    val friends: LiveData<List<AccountInfo>>
        get() = _friends

    private val _creationConfirmation: MutableLiveData<State> =
        MutableLiveData(State.INITIAL)
    val creationConfirmation: LiveData<State>
        get() = _creationConfirmation

    enum class State { INITIAL, CREATED, FAILED }

    var selected = 0

    fun loadFriends() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            try {
                val list = restWrapper.getApi()
                    .getFriendsWithoutPrivateChat()
                    .filter { it.isCompleted }
                    .mapNotNull {
                        try {
                            it.mapToAccountInfoOrThrow()
                        } catch (e: Exception) {
                            null
                        }
                    }

                _friends.postValue(list)
            } catch (e: Exception) {
            }
        }
    }

    fun createGroupChat() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            try {
                val userId = _friends.value!![selected].id
                restWrapper.getApi()
                    .createPrivateChat(PrivateChatCreationDTO(userId))

                _creationConfirmation.postValue(State.CREATED)
            } catch (e: Exception) {
                _creationConfirmation.postValue(State.FAILED)
            }
        }
    }
}