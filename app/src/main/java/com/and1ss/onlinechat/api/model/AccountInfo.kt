package com.and1ss.onlinechat.api.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AccountInfo(
    var id: String,
    val name: String,
    val surname: String
) : Parcelable