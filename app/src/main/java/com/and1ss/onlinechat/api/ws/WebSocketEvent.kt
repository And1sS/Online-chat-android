package com.and1ss.onlinechat.api.ws

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import java.io.StringReader

sealed class WebSocketEvent {
    object ConnectionOpening : WebSocketEvent()
    object ConnectionClosing : WebSocketEvent()
    object ConnectionOpened : WebSocketEvent()
    object ConnectionClosed : WebSocketEvent()
    object ConnectionFailure : WebSocketEvent()
    object ReconnectionAttempt : WebSocketEvent()

    data class WebSocketMessage<T>(
        var payload: T? = null,

        @SerializedName("message_type")
        var messageType: WebSocketMessageType? = null
    ) : WebSocketEvent()
}

inline fun <reified T> webSocketMessageFromJson(json: String): WebSocketEvent.WebSocketMessage<T> {
    val reader = JsonReader(StringReader(json)).apply {
        isLenient = true
    }

    return Gson().fromJson(
        reader,
        TypeToken.getParameterized(
            WebSocketEvent.WebSocketMessage::class.java,
            T::class.java
        ).type
    )
}

inline fun <reified T> webSocketMessageToJson(message: WebSocketEvent.WebSocketMessage<T>): String =
    Gson().toJson(
        message,
        TypeToken.getParameterized(
            WebSocketEvent.WebSocketMessage::class.java,
            T::class.java
        ).type
    )
