package com.and1ss.onlinechat.view.main.group_chat_creation

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.and1ss.onlinechat.api.dto.FriendRetrievalDTO
import com.and1ss.onlinechat.api.dto.GroupChatCreationDTO
import com.and1ss.onlinechat.api.model.AccountInfo
import com.and1ss.onlinechat.api.rest.RestWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GroupChatCreationViewModel @ViewModelInject constructor(
    private val restWrapper: RestWrapper,
    application: Application
) : AndroidViewModel(application) {
    private val _participants: MutableLiveData<MutableList<Pair<AccountInfo, Boolean>>> =
        MutableLiveData(mutableListOf())
    val participants: LiveData<MutableList<Pair<AccountInfo, Boolean>>>
        get() = _participants

    private val _creationConfirmation: MutableLiveData<State> =
        MutableLiveData(State.INITIAL)
    val creationConfirmation: LiveData<State>
        get() = _creationConfirmation

    enum class State { INITIAL, CREATED, FAILED }

    val creationReady: Boolean
        get() = _isCreationReady()

    private val myAccountInfo = restWrapper.getMyAccount()
    var chatTitle = ""
    var chatAbout = ""
    private var selected = 0

    fun getAcceptedFriends() {
        if (_participants.value?.isNotEmpty() != false) {
            return
        }

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val friends = restWrapper.getApi()
                    .getFriends(acceptedOnly = true)
                    .filter { it.isCompleted }
                    .mapNotNull { mapFriendDtoToSelectedPair(it) }

                _participants.postValue(friends.toMutableList())
            }
        }
    }

    private fun _isCreationReady(): Boolean =
        chatTitle.isNotEmpty() && chatAbout.isNotEmpty() && selected > 0

    fun setSelection(pair: Pair<AccountInfo, Boolean>) {
        val index = _participants.value?.indexOfFirst { it.first.id == pair.first.id } ?: -1

        if (index >= 0) {
            participants.value?.let {
                participants.value!![index] = pair
                _participants.postValue(participants.value)
                selected += if (pair.second) 1 else -1
            }
        }
    }

    fun createGroupChat() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            try {
                val groupChatCreationDTO = GroupChatCreationDTO(
                    title = chatTitle,
                    about = chatAbout,
                    participants = _participants.value
                        ?.map { it.first.id } ?: listOf()
                )
                restWrapper.getApi().createGroupChat(groupChatCreationDTO)
                _creationConfirmation.postValue(State.CREATED)
            } catch (e: Exception) {
                _creationConfirmation.postValue(State.FAILED)
            }
        }
    }

    private fun mapFriendDtoToSelectedPair(friendDto: FriendRetrievalDTO):
            Pair<AccountInfo, Boolean>? =
        try {
            val friend = if (friendDto.isRequestIssuer(myAccountInfo.id)) {
                friendDto.requestee!!.mapToAccountInfoOrThrow()
            } else {
                friendDto.requestIssuer!!.mapToAccountInfoOrThrow()
            }
            Pair(friend, false)
        } catch (e: Exception) {
            null
        }
}