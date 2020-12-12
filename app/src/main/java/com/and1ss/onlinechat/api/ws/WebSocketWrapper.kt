package com.and1ss.onlinechat.api.ws

import androidx.lifecycle.LiveData

interface WebSocketWrapper {
    fun isConnected(): Boolean
    fun connect()
    fun disconnect()
    fun getEventBus(): LiveData<WebSocketEvent>
    fun send(message: String)
}