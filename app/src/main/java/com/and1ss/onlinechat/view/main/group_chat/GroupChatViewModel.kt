package com.and1ss.onlinechat.view.main.group_chat

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.and1ss.onlinechat.api.dto.GroupMessageCreationDTO
import com.and1ss.onlinechat.api.dto.GroupMessageRetrievalDTO
import com.and1ss.onlinechat.api.model.AccountInfo
import com.and1ss.onlinechat.api.model.GroupChat
import com.and1ss.onlinechat.api.rest.rest_wrapper.RestWrapper
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
import java.util.Collections.synchronizedList

private const val TAG = "GroupChatViewModel"

class GroupChatViewModel
@ViewModelInject constructor(
    val restWrapper: RestWrapper,
    private val webSocketWrapper: WebSocketWrapper
) : ViewModel() {
    lateinit var chat: GroupChat
    var myAccount: AccountInfo = restWrapper.getMyAccount()

    var messageString = ""

    val chatMessages: MutableList<GroupMessageRetrievalDTO> = synchronizedList(mutableListOf())
    private val _notifier: MutableLiveData<Event> = MutableLiveData()
    val notifier: LiveData<Event>
        get() = _notifier

    private val observer = Observer<WebSocketEvent> { onNewWebSocketEvent(it) }

    fun connect() {
        webSocketWrapper.getEventBus().observeForever(observer)
    }

    fun send(message: String) {
        val webSocketMessage = WebSocketEvent.WebSocketMessage(
            messageType = WebSocketMessageType.GROUP_MESSAGE_CREATE,
            payload = GroupMessageCreationDTO(message, chat.id)
        )
        webSocketWrapper.send(webSocketMessageToJson(webSocketMessage))
    }

    suspend fun getAllMessages() = withContext(Dispatchers.IO) {
        val messages = try {
            restWrapper.getApi().getAllMessagesForGroupChat(chat.id)
        } catch (e: Exception) {
            null
        }

        messages?.let {
            withContext(Dispatchers.Default) {
                synchronized(chatMessages) {
                    chatMessages.clear()
                    chatMessages.addAll(
                        messages
                            .filter { it.createdAt?.time != null }
                            .sortedByDescending {
                                it.createdAt?.time ?: 0
                            }
                    )
                }
                _notifier.postValue(Event.LoadedInitial)
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
    class MessageAdd(val message: GroupMessageRetrievalDTO, val index: Int) : Event()
    class MessagePatch(val message: GroupMessageRetrievalDTO, val index: Int) : Event()
    object LoadedInitial : Event()
}