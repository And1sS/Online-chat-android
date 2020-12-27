package com.and1ss.onlinechat.view.main.group_chat_list

import android.app.Application
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.and1ss.onlinechat.api.dto.GroupChatRetrievalDTO
import com.and1ss.onlinechat.api.dto.GroupMessageRetrievalDTO
import com.and1ss.onlinechat.api.rest.RestWrapper
import com.and1ss.onlinechat.api.ws.WebSocketEvent
import com.and1ss.onlinechat.api.ws.WebSocketMessageType
import com.and1ss.onlinechat.api.ws.WebSocketWrapper
import com.and1ss.onlinechat.util.fromJson
import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
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
        webSocketWrapper.getEventBus().observeForever(observer)
    }

    override fun onCleared() {
        super.onCleared()
        webSocketWrapper.getEventBus().removeObserver(observer)
    }

    suspend fun getChats() = withContext(Dispatchers.IO) {
        try {
            val chats = restWrapper
                .getApi()
                .getAllGroupChats()
                .filter { it.isCompleted }
                .sortedByDescending { it.lastMessage?.createdAt?.time ?: 0 }
            _chats.postValue(chats)
        } catch (e: Exception) {
        }
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
            WebSocketMessageType.GROUP_MESSAGE_CREATE -> handleNewGroupChatMessageCreation(message)

            else -> {
                Log.d(TAG, "onNewWebSocketEvent: $message")
            }
        }
    }

    private fun handleNewGroupChatMessageCreation(
        message: WebSocketEvent.WebSocketMessage<*>
    ) {
        try {
            val msg = fromJson<GroupMessageRetrievalDTO>(
                Gson().toJson(message.payload as LinkedTreeMap<*, *>)
            )

            if (!msg.isCompleted) {
                return
            }

            val chats = _chats.value
            if (chats != null) {
                updateChatsWithNewMessage(chats, msg)

                _chats.postValue(
                    chats.sortedByDescending { it.lastMessage?.createdAt?.time ?: 0 }
                )
            }
        } catch (e: Exception) {
        }
    }

    private fun updateChatsWithNewMessage(
        chats: List<GroupChatRetrievalDTO>,
        message: GroupMessageRetrievalDTO
    ) {
        fun messageMatchesChat(chat: GroupChatRetrievalDTO, message: GroupMessageRetrievalDTO) =
            chat.id != null && message.chatId != null && chat.id == message.chatId

        for (chat in chats) {
            if (messageMatchesChat(chat, message)) {
                chat.lastMessage = message
                break
            }
        }
    }
}