package com.and1ss.onlinechat.view.main.add_friends

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.and1ss.onlinechat.api.dto.AccountInfoRetrievalDTO
import com.and1ss.onlinechat.api.dto.FriendCreationDTO
import com.and1ss.onlinechat.api.rest.RestWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddNewFriendsViewModel @ViewModelInject constructor(
    private val restWrapper: RestWrapper,
    application: Application
) : AndroidViewModel(application) {
    private val _friends: MutableLiveData<List<AccountInfoRetrievalDTO>> = MutableLiveData(listOf())
    val friends: LiveData<List<AccountInfoRetrievalDTO>>
        get() = _friends

    var loginLike = ""

    private fun getUsersWhoAreNotCurrentUserFriendsWithLoginLike(loginLike: String) =
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val friends = restWrapper.getApi()
                    .getUsersWhoAreNotCurrentUserFriendsWithLoginLike(loginLike)
                    .filter { it.isCompleted() }
                _friends.postValue(friends)
            }
        }

    fun getUsersWhoAreNotCurrentUserFriendsWithLoginLike() =
        getUsersWhoAreNotCurrentUserFriendsWithLoginLike(loginLike)

    fun sendFriendRequest(friendId: String) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            restWrapper.getApi()
                .sendFriendRequest(FriendCreationDTO(friendId))
            getUsersWhoAreNotCurrentUserFriendsWithLoginLike(loginLike)
        }
    }
}