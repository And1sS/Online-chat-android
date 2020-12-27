package com.and1ss.onlinechat.api.dto

import com.google.gson.annotations.SerializedName

data class PrivateMessageCreationDTO(
    val contents: String,

    @SerializedName("chat_id")
    val chatId: String
)