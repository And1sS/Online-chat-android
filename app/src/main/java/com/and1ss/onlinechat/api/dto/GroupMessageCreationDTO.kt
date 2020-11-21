package com.and1ss.onlinechat.api.dto;

import com.google.gson.annotations.SerializedName

data class GroupMessageCreationDTO(
    private val contents: String,

    @SerializedName("chat_id")
    private val chatId: String
)
