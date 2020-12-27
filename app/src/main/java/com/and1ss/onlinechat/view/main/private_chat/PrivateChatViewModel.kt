package com.and1ss.onlinechat.view.main.private_chat

import android.app.Application
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.and1ss.onlinechat.api.dto.PrivateMessageCreationDTO
import com.and1ss.onlinechat.api.dto.PrivateMessageRetrievalDTO
import com.and1ss.onlinechat.api.model.AccountInfo
import com.and1ss.onlinechat.api.model.PrivateChat
import com.and1ss.onlinechat.api.rest.RestWrapper
import com.and1ss.onlinechat.api.ws.WebSocketEvent
import com.and1ss.onlinechat.api.ws.WebSocketMessageType
import com.and1ss.onlinechat.api.ws.WebSocketWrapper
import com.and1ss.onlinechat.api.ws.webSocketMessageToJson
import com.and1ss.onlinechat.util.fromJson
import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

private const val TAG = "PrivateChatViewModel"

class PrivateChatViewModel @ViewModelInject constructor(
    private val restWrapper: RestWrapper,
    private val webSocketWrapper: WebSocketWrapper,
    application: Application
) : AndroidViewModel(application) {
    lateinit var chat: PrivateChat
    var myAccount: AccountInfo = restWrapper.getMyAccount()

    var messageString = ""

    val chatMessages: MutableList<PrivateMessageRetrievalDTO> =
        Collections.synchronizedList(mutableListOf())
    private val _notifier: MutableLiveData<Event> = MutableLiveData()
    val notifier: LiveData<Event>
        get() = _notifier

    private val observer = Observer<WebSocketEvent> { onNewWebSocketEvent(it) }

    fun connect() {
        webSocketWrapper.getEventBus().observeForever(observer)
    }

    fun send(message: String) {
        val webSocketMessage = WebSocketEvent.WebSocketMessage(
            messageType = WebSocketMessageType.PRIVATE_MESSAGE_CREATE,
            payload = PrivateMessageCreationDTO(message, chat.id)
        )
        webSocketWrapper.send(webSocketMessageToJson(webSocketMessage))
    }

    fun getAllMessages() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val messages = try {
                restWrapper.getApi().getAllMessagesForPrivateChat(chat.id)
            } catch (e: Exception) {
                null
            }

            messages?.let {
                withContext(Dispatchers.Default) {
                    synchronized(chatMessages) {
                        chatMessages.clear()
                        chatMessages.addAll(
                            messages
                                .filter { it.isCompleted }
                                .sortedByDescending { it.createdAt?.time ?: 0 }
                        )
                    }
                    _notifier.postValue(Event.LoadedInitial)
                }
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
            WebSocketMessageType.PRIVATE_MESSAGE_CREATE ->
                handleNewGroupChatMessageCreation(message)

            else -> {
                Log.d(TAG, "onNewWebSocketEvent: $message")
            }
        }
    }

    private fun handleNewGroupChatMessageCreation(
        message: WebSocketEvent.WebSocketMessage<*>
    ) {
        val msg = fromJson<PrivateMessageRetrievalDTO>(
            Gson().toJson(message.payload as LinkedTreeMap<*, *>)
        )

        viewModelScope.launch(Dispatchers.Default) {
            if (!msg.chatId.equals(chat.id)) {
                // TODO: make notifications? or smth
                return@launch
            }

            synchronized(chatMessages) {
                for (i in 0 until chatMessages.size) {
                    if (chatMessages[i].createdAt?.time ?: 0 > msg.createdAt?.time ?: 0) {
                        chatMessages.add(i + 1, msg)

                        _notifier.postValue(Event.MessageAdd(msg, i))
                        return@launch
                    }
                }

                chatMessages.add(0, msg)
            }
            _notifier.postValue(Event.MessageAdd(msg, 0))
        }
    }
}

sealed class Event {
    class MessageAdd(val message: PrivateMessageRetrievalDTO, val index: Int) : Event()
    class MessagePatch(val message: PrivateMessageRetrievalDTO, val index: Int) : Event()
    object LoadedInitial : Event()
}