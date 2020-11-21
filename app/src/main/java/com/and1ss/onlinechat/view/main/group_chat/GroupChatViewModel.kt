package com.and1ss.onlinechat.view.main.group_chat

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.and1ss.onlinechat.api.dto.AccountInfoRetrievalDTO
import com.and1ss.onlinechat.api.dto.GroupMessageCreationDTO
import com.and1ss.onlinechat.api.dto.GroupMessageRetrievalDTO
import com.and1ss.onlinechat.api.model.AccountInfo
import com.and1ss.onlinechat.api.rest.rest_wrapper.RestWrapper
import com.and1ss.onlinechat.api.ws.WebSocketEvent
import com.and1ss.onlinechat.api.ws.WebSocketMessageType
import com.and1ss.onlinechat.api.ws.WebSocketWrapper
import com.and1ss.onlinechat.api.ws.webSocketMessageToJson
import com.and1ss.onlinechat.util.fromJson
import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

private const val TAG = "GroupChatViewModel"

class GroupChatViewModel
@ViewModelInject constructor(
    private val restWrapper: RestWrapper,
    private val webSocketWrapper: WebSocketWrapper
) : ViewModel() {
    var chatId: String = ""
    var myAccount: AccountInfo = restWrapper.getMyAccount()

    var messageString = ""

    private val _chatMessages: MutableLiveData<List<GroupMessageRetrievalDTO>> =
        MutableLiveData(listOf())
    val reversedChatMessages: LiveData<List<GroupMessageRetrievalDTO>>
        get() = _chatMessages

    private val observer = Observer<WebSocketEvent> { onNewWebSocketEvent(it) }

    init {
        webSocketWrapper.eventBus.observeForever(observer)
    }

    fun send(message: String) {
        val webSocketMessage = WebSocketEvent.WebSocketMessage(
            messageType = WebSocketMessageType.GROUP_MESSAGE_CREATE,
            payload = GroupMessageCreationDTO(message, chatId)
        )
        webSocketWrapper.send(
            webSocketMessageToJson(webSocketMessage)
        )
    }

    suspend fun getAllMessages() = withContext(Dispatchers.IO) {
        val messages = try {
            restWrapper.getApi().getAllMessagesForGroupChat(chatId)
        } catch (e: Exception) {
            null
        }

        messages?.let {
            withContext(Dispatchers.Default) {
                _chatMessages.postValue(
                    messages.sortedByDescending {
                        it.createdAt?.time ?: 0
                    }
                )
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

        val list = _chatMessages.value

        if (list != null) {
            val mutableList = list.toMutableList()
            mutableList.add(msg)

            viewModelScope.launch(Dispatchers.Default) {
                _chatMessages.postValue(
                    mutableList.sortedByDescending {
                        it.createdAt?.time ?: 0
                    }
                )
            }
        }
    }
}