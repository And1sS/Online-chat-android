package com.and1ss.onlinechat.api.dto


import com.and1ss.onlinechat.api.model.GroupChat
import com.google.gson.annotations.SerializedName


data class GroupChatRetrievalDTO(
    var id: String? = null,
    var title: String? = null,
    var about: String? = null,
    var creator: AccountInfoRetrievalDTO? = null,
    @SerializedName("last_message")
    var lastMessage: GroupMessageRetrievalDTO? = null
) {
    @Throws(NullPointerException::class)
    fun mapToGroupChatOrThrow() =
        GroupChat(
            id = id!!,
            title = title!!,
            about = about,
            creator = creator?.mapToAccountInfoOrThrow()
        )

    fun isCompleted() = id != null && title != null
}


