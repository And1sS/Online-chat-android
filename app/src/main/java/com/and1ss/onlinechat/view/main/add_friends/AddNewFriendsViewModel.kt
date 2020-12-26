package com.and1ss.onlinechat.view.main.add_friends

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.and1ss.onlinechat.api.dto.FriendCreationDTO
import com.and1ss.onlinechat.api.model.AccountInfo
import com.and1ss.onlinechat.api.rest.RestWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddNewFriendsViewModel @ViewModelInject constructor(
    private val restWrapper: RestWrapper,
    application: Application
) : AndroidViewModel(application) {
    private val _friends: MutableLiveData<List<AccountInfo>> = MutableLiveData(listOf())
    val friends: LiveData<List<AccountInfo>>
        get() = _friends

    var loginLike = ""

    private fun getUsersWhoAreNotCurrentUserFriendsWithLoginLike(loginLike: String) =
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val friends = restWrapper.getApi()
                        .getUsersWhoAreNotCurrentUserFriendsWithLoginLike(loginLike)
                        .filter { it.isCompleted }
                        .map { it.mapToAccountInfoOrThrow() }
                    _friends.postValue(friends)
                } catch (e: Exception) {

                }
            }
        }

    fun getUsersWhoAreNotCurrentUserFriendsWithLoginLike() =
        getUsersWhoAreNotCurrentUserFriendsWithLoginLike(loginLike)

    fun sendFriendRequest(friendId: String) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            try {
                restWrapper.getApi()
                    .sendFriendRequest(FriendCreationDTO(friendId))
                getUsersWhoAreNotCurrentUserFriendsWithLoginLike(loginLike)
            } catch (e: Exception) {
            }
        }
    }
}