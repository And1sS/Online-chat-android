package com.and1ss.onlinechat.api.dto

import com.google.gson.annotations.SerializedName

enum class FriendshipStatus {
    accepted, pending
}

data class FriendRetrievalDTO(
    @SerializedName("request_issuer")
    var requestIssuer: AccountInfoRetrievalDTO? = null,

    @SerializedName("requestee")
    var requestee: AccountInfoRetrievalDTO? = null,

    @SerializedName("status")
    var status: FriendshipStatus? = null
) {
    fun isRequestee(id: String?): Boolean {
        return requestee?.id?.equals(id) ?: false
    }

    fun isRequestIssuer(id: String?): Boolean {
        return requestIssuer?.id?.equals(id) ?: false
    }

    fun isCompleted() =
        requestIssuer != null && requestIssuer!!.isCompleted()
                && requestee != null && requestee!!.isCompleted()
                && status != null
}
