package com.and1ss.onlinechat.api.dto

import com.google.gson.annotations.SerializedName
import java.sql.Timestamp

data class PrivateMessageRetrievalDTO(
    var id: String? = null,
    var author: AccountInfoRetrievalDTO? = null,

    @SerializedName("chat_id")
    var chatId: String? = null,
    var contents: String? = null,

    @SerializedName("created_at")
    var createdAt: Timestamp? = null
) {
    private fun _isCompleted() = id != null && chatId != null
            && contents != null && createdAt != null

    val isCompleted: Boolean
        get() = _isCompleted()
}