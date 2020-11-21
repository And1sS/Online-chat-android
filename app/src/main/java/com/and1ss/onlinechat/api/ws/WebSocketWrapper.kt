package com.and1ss.onlinechat.api.ws

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.and1ss.onlinechat.api.rest.rest_wrapper.RestWrapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.*
import okio.ByteString
import java.util.concurrent.Executors
import javax.inject.Inject

private const val TAG = "WebSocketWrapper"

private const val NUM_OF_THREADS = 1
private const val REMOTE_HOST_URL = "ws://176.36.243.160:8080/api/ws"
private const val AUTHORIZATION_HEADER = "Authorization"
private const val BEARER_PREFIX = "Bearer "

class WebSocketWrapper @Inject constructor(
    private val restWrapper: RestWrapper,
    private val client: OkHttpClient
) {
    private lateinit var webSocket: WebSocket

    private val webSocketListener: WebSocketListener = ChatWebSocketListener()

    private val dispatcher = Executors.newFixedThreadPool(NUM_OF_THREADS).asCoroutineDispatcher()

    private val _eventBus: MutableLiveData<WebSocketEvent> = MutableLiveData()
    val eventBus: LiveData<WebSocketEvent>
        get() = _eventBus

    fun send(message: String) {
        Log.d(TAG, "WebSocketWrapper: sending $message")
        webSocket.send(ByteString.encodeString(message, Charsets.UTF_8))
    }

    fun connect() {
        if (this::webSocket.isInitialized) {
            webSocket.cancel()
        }

        val requestBuilder: Request.Builder = Request
            .Builder()
            .url(REMOTE_HOST_URL)
            .addHeader(
                AUTHORIZATION_HEADER,
                BEARER_PREFIX + restWrapper.getAccessToken()
            )

        webSocket = client.newWebSocket(requestBuilder.build(), webSocketListener)
    }

    private fun reconnect() {
        webSocket.cancel()
        _eventBus.postValue(WebSocketEvent.ReconnectionAttempt)

        Log.d(TAG, "reconnection attempt")
        CoroutineScope(dispatcher).launch {
            delay(5000)
            connect()
        }
    }

    inner class ChatWebSocketListener : WebSocketListener() {

        override fun onOpen(webSocket: WebSocket, response: Response) {
            Log.i(TAG, "onOpen: $webSocket")
            _eventBus.postValue(WebSocketEvent.ConnectionOpening)
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            Log.e(TAG, "onFailure: ${t.message} ${response.toString()}")
            reconnect()
            _eventBus.postValue(WebSocketEvent.ConnectionFailure)
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            try {
                val obj = webSocketMessageFromJson<Object>(bytes.string(Charsets.UTF_8))
                Log.i(TAG, "onMessage: $obj")
                _eventBus.postValue(obj)
            } catch (e: Exception) {
                Log.d(TAG, "onMessage ERROR: ${e.message}")
            }
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            Log.i(TAG, "onClosed: ")
            _eventBus.postValue(WebSocketEvent.ConnectionClosed)
        }
    }
}

