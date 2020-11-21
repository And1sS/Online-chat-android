package com.and1ss.onlinechat.view.main.group_chat_list

import android.app.Application
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.and1ss.onlinechat.api.dto.GroupChatRetrievalDTO
import com.and1ss.onlinechat.api.rest.rest_wrapper.RestWrapper
import com.and1ss.onlinechat.api.ws.WebSocketEvent
import com.and1ss.onlinechat.api.ws.WebSocketMessageType
import com.and1ss.onlinechat.api.ws.WebSocketWrapper
import com.and1ss.onlinechat.api.dto.GroupMessageRetrievalDTO
import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import com.and1ss.onlinechat.util.fromJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val TAG = "ChatsViewModel"

class GroupChatsViewModel @ViewModelInject constructor(
    private val restWrapper: RestWrapper,
    private val webSocketWrapper: WebSocketWrapper,
    application: Application
) : AndroidViewModel(application) {
    private val _chats: MutableLiveData<List<GroupChatRetrievalDTO>> = MutableLiveData(listOf())
    val chats: LiveData<List<GroupChatRetrievalDTO>>
        get() = _chats

    private val observer = Observer<WebSocketEvent> { onNewWebSocketEvent(it) }

    init {
        webSocketWrapper.eventBus.observeForever(observer)
    }

    override fun onCleared() {
        super.onCleared()
        webSocketWrapper.eventBus.removeObserver(observer)
    }

    suspend fun getChats() = withContext(Dispatchers.IO) {
        val chats = restWrapper.getApi().getAllGroupChats().sortedByDescending {
            it.lastMessage?.createdAt?.time ?: 0
        }
        _chats.postValue(chats)
    }

    private fun onNewWebSocketEvent(event: WebSocketEvent) {
        Log.d(TAG, "onNewWebSocketEvent: $event")
        when (event) {
            is WebSocketEvent.WebSocketMessage<*> -> handleWebSocketMessage(event)
            else -> Log.d(TAG, "unhandled: $event")
        }
    }

    private fun handleWebSocketMessage(message: WebSocketEvent.WebSocketMessage<*>) {
        when (message.messageType) {
            WebSocketMessageType.GROUP_MESSAGE_CREATE ->
                handleNewGroupChatMessageCreation(message)

            else -> {
                Log.d(TAG, "onNewWebSocketEvent: $message")
            }
        }
    }

    private fun handleNewGroupChatMessageCreation(
        message: WebSocketEvent.WebSocketMessage<*>
    ) {
        val msg = fromJson<GroupMessageRetrievalDTO>(
            Gson().toJson(message.payload as LinkedTreeMap<*, *>)
        )

        val list = _chats.value

        if (list != null) {
            list.forEach { groupChat ->
                if (groupChat.id != null
                    && msg.chatId != null
                    && groupChat.id == msg.chatId
                ) {
                    groupChat.lastMessage = msg
                }
            }

            _chats.postValue(
                list.sortedByDescending {
                    it.lastMessage?.createdAt?.time ?: 0
                }
            )
        }
    }
}