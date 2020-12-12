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
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

private const val TAG = "WebSocketWrapper"

private const val NUM_OF_THREADS = 1
private const val REMOTE_HOST_URL = "ws://176.36.243.160:8080/api/ws"
private const val AUTHORIZATION_HEADER = "Authorization"
private const val BEARER_PREFIX = "Bearer "

class WebSocketWrapperImpl @Inject constructor(
    private val restWrapper: RestWrapper,
    private val client: OkHttpClient
) : WebSocketWrapper {
    private lateinit var webSocket: WebSocket

    private var isConnected: AtomicBoolean = AtomicBoolean(false)

    @Volatile
    private var isReconnecting: Boolean = false

    private val webSocketListener: WebSocketListener = ChatWebSocketListener()

    private val dispatcher = Executors.newFixedThreadPool(NUM_OF_THREADS).asCoroutineDispatcher()

    private val eventBus: MutableLiveData<WebSocketEvent> = MutableLiveData()

    override fun send(message: String) {
        Log.d(TAG, "WebSocketWrapper: sending $message")
        webSocket.send(ByteString.encodeString(message, Charsets.UTF_8))
    }

    override fun isConnected(): Boolean = isConnected.get()

    override fun connect() {
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

    override fun disconnect() {
        if (this::webSocket.isInitialized) {
            webSocket.cancel()

            isConnected.set(false)
        }
    }

    override fun getEventBus(): LiveData<WebSocketEvent> = eventBus

    private fun CoroutineScope.reconnect() {
        launch {
            if (isReconnecting) {
                return@launch
            }

            delay(5000)

            eventBus.postValue(WebSocketEvent.ReconnectionAttempt)
            Log.d(TAG, "reconnection attempt")

            isReconnecting = true
            webSocket.cancel()
            isConnected.set(false)
            connect()
        }
    }

    inner class ChatWebSocketListener : WebSocketListener() {

        override fun onOpen(webSocket: WebSocket, response: Response) {
            Log.i(TAG, "onOpen: $webSocket")
            eventBus.postValue(WebSocketEvent.ConnectionOpening)
            isReconnecting = false
            isConnected.set(true)
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            Log.e(TAG, "onFailure: ${t.message} ${response.toString()}")
            CoroutineScope(dispatcher).reconnect()

            eventBus.postValue(WebSocketEvent.ConnectionFailure)
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            try {
                val obj = webSocketMessageFromJson<Any>(bytes.string(Charsets.UTF_8))
                Log.i(TAG, "onMessage: $obj")
                eventBus.postValue(obj)
            } catch (e: Exception) {
                Log.d(TAG, "onMessage ERROR: ${e.message}")
            }
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            Log.i(TAG, "onClosed: ")
            eventBus.postValue(WebSocketEvent.ConnectionClosed)
        }
    }
}

