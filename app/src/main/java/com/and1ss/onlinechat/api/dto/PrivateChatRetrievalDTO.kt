package com.and1ss.onlinechat.api.dto

import com.and1ss.onlinechat.api.model.PrivateChat
import com.google.gson.annotations.SerializedName

data class PrivateChatRetrievalDTO(
    var id: String? = null,
    @SerializedName("user_1")
    var user1: AccountInfoRetrievalDTO? = null,
    @SerializedName("user_2")
    var user2: AccountInfoRetrievalDTO? = null,

    @SerializedName("last_message")
    var lastMessage: PrivateMessageRetrievalDTO? = null
) {
    @Throws(NullPointerException::class)
    fun mapToPrivateChatOrThrow() =
        PrivateChat(
            id = id!!,
            user1 = if (user1?.isCompleted == true)
                user1!!.mapToAccountInfoOrThrow() else null,
            user2 = if (user2?.isCompleted == true)
                user2!!.mapToAccountInfoOrThrow() else null
        )

    private fun _isCompleted() = id != null &&
            ((user1 != null && user1!!.isCompleted)
                    || (user2 != null && user2!!.isCompleted))

    val isCompleted: Boolean
        get() = _isCompleted()
}