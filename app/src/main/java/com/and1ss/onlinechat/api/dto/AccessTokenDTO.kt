package com.and1ss.onlinechat.api.dto

import com.google.gson.annotations.SerializedName
import java.security.InvalidParameterException
import java.sql.Timestamp

data class AccessTokenDTO(
    @SerializedName("access_token")
    var accessToken: String? = null,

    @SerializedName("created_at")
    var createdAt: Timestamp? = null
) {
    fun mapToAccessTokenOrThrow(): String =
        accessToken ?: throw InvalidParameterException("Access token is null")

    fun _isCompleted() = accessToken != null && createdAt != null

    val isCompleted: Boolean
        get() = _isCompleted()
}