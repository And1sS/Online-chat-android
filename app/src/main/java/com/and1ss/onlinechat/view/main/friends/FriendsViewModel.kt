package com.and1ss.onlinechat.view.main.friends

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.and1ss.onlinechat.api.dto.FriendRetrievalDTO
import com.and1ss.onlinechat.api.rest.RestWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FriendsViewModel @ViewModelInject constructor(
    private val restWrapper: RestWrapper,
    application: Application
) : AndroidViewModel(application) {
    private val _friends: MutableLiveData<List<FriendRetrievalDTO>> = MutableLiveData(listOf())
    val friends: LiveData<List<FriendRetrievalDTO>>
        get() = _friends

    fun getFriends() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            try {
                val friends = restWrapper.getApi()
                    .getFriends(acceptedOnly = false)
                    .filter { it.isCompleted }
                _friends.postValue(friends)
            } catch (e: Exception) {
            }
        }
    }

    fun deleteFriend(friendId: String) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            try {
                restWrapper.getApi()
                    .deleteFriend(friendId)
                getFriends()
            } catch (e: Exception) {
            }
        }
    }

    fun acceptFriendRequest(friendId: String) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            try {
                restWrapper.getApi()
                    .acceptFriend(friendId)
                getFriends()
            } catch (e: Exception) {
            }
        }
    }
}