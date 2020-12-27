package com.and1ss.onlinechat.api.dto

import com.google.gson.annotations.SerializedName

data class GroupChatCreationDTO(
    @SerializedName("title")
    val title: String,
    @SerializedName("about")
    val about: String?,
    @SerializedName("participants")
    val participants: List<String>
)
