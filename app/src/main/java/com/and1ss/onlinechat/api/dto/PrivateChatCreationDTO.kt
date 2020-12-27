package com.and1ss.onlinechat.api.dto

import com.google.gson.annotations.SerializedName

data class PrivateChatCreationDTO(
    @SerializedName("user_id")
    val userId: String
)
