package com.and1ss.onlinechat.view.main.private_chat_list

import android.app.Application
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.and1ss.onlinechat.api.dto.PrivateChatRetrievalDTO
import com.and1ss.onlinechat.api.dto.PrivateMessageRetrievalDTO
import com.and1ss.onlinechat.api.model.AccountInfo
import com.and1ss.onlinechat.api.rest.RestWrapper
import com.and1ss.onlinechat.api.ws.WebSocketEvent
import com.and1ss.onlinechat.api.ws.WebSocketMessageType
import com.and1ss.onlinechat.api.ws.WebSocketWrapper
import com.and1ss.onlinechat.util.fromJson
import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "PrivateChatsViewModel"

class PrivateChatsViewModel @ViewModelInject constructor(
    private val restWrapper: RestWrapper,
    private val webSocketWrapper: WebSocketWrapper,
    application: Application
) : AndroidViewModel(application) {
    private val _chats: MutableLiveData<List<PrivateChatRetrievalDTO>> = MutableLiveData(listOf())
    val chats: LiveData<List<PrivateChatRetrievalDTO>>
        get() = _chats

    val myAccount: AccountInfo = restWrapper.getMyAccount()

    private val observer = Observer<WebSocketEvent> { onNewWebSocketEvent(it) }

    init {
        webSocketWrapper.getEventBus().observeForever(observer)
    }

    override fun onCleared() {
        super.onCleared()
        webSocketWrapper.getEventBus().removeObserver(observer)
    }

    fun getChats() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            try {
                val chats = restWrapper
                    .getApi()
                    .getAllPrivateChats()
                    .filter { it.isCompleted }
                    .sortedByDescending { it.lastMessage?.createdAt?.time ?: 0 }
                _chats.postValue(chats)
            } catch (e: Exception) {
            }
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
            WebSocketMessageType.PRIVATE_MESSAGE_CREATE -> handleNewPrivateChatMessageCreation(
                message
            )

            else -> {
                Log.d(TAG, "onNewWebSocketEvent: $message")
            }
        }
    }

    private fun handleNewPrivateChatMessageCreation(
        message: WebSocketEvent.WebSocketMessage<*>
    ) {
        try {
            val msg = fromJson<PrivateMessageRetrievalDTO>(
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
        chats: List<PrivateChatRetrievalDTO>,
        message: PrivateMessageRetrievalDTO
    ) {
        fun messageMatchesChat(
            chat: PrivateChatRetrievalDTO,
            message: PrivateMessageRetrievalDTO
        ) =
            chat.id != null && message.chatId != null && chat.id == message.chatId

        for (chat in chats) {
            if (messageMatchesChat(chat, message)) {
                chat.lastMessage = message
                break
            }
        }
    }
}