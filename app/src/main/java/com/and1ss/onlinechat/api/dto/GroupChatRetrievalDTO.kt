package com.and1ss.onlinechat.api.dto


import com.google.gson.annotations.SerializedName
import java.util.*


data class GroupChatRetrievalDTO(
    var id: UUID? = null,
    var title: String? = null,
    var about: String? = null,
    var creator: AccountInfoRetrievalDTO? = null,
    var participants: List<AccountInfoRetrievalDTO>? = null,
    @SerializedName("last_message")
    var lastMessage: GroupMessageRetrievalDTO? = null
)


