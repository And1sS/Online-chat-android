package com.and1ss.onlinechat.api.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GroupChat(
    val id: String,
    val title: String,
    val about: String? = null,
    val creator: AccountInfo? = null,
    val participants: List<AccountInfo>? = mutableListOf()
) : Parcelable