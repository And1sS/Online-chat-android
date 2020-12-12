package com.and1ss.onlinechat.api.dto

import com.google.gson.annotations.SerializedName
import java.sql.Timestamp


data class GroupMessageRetrievalDTO(
    var id: String? = null,
    var author: AccountInfoRetrievalDTO? = null,

    @SerializedName("chat_id")
    var chatId: String? = null,
    var contents: String? = null,

    @SerializedName("created_at") val createdAt: Timestamp? = null
)