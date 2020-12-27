package com.and1ss.onlinechat.api.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PrivateChat(
    val id: String,
    val user1: AccountInfo? = null,
    val user2: AccountInfo? = null
) : Parcelable {
    fun getTitle(currentUser: AccountInfo) = when {
        user1?.id.equals(currentUser.id) -> user2?.nameSurname ?: ""
        user2?.id.equals(currentUser.id) -> user1?.nameSurname ?: ""
        else -> ""
    }
}