package com.and1ss.onlinechat.api.ws

enum class WebSocketMessageType {
    GROUP_MESSAGE_PATCH, GROUP_MESSAGE_CREATE, GROUP_MESSAGE_DELETE,
    PRIVATE_MESSAGE_PATCH, PRIVATE_MESSAGE_CREATE, PRIVATE_MESSAGE_DELETE
}