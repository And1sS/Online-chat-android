package com.and1ss.onlinechat.api.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AccountInfo(
    var id: String,
    val name: String,
    val surname: String,
    val login: String
) : Parcelable {
    private fun _getInitials(): String {
        val nameLetter = name
        val surnameLetter = surname

        var initials = ""
        if (nameLetter.isNotBlank()) {
            initials += nameLetter[0] + " "
        }

        if (surnameLetter.isNotBlank()) {
            initials += surnameLetter[0]
        }

        return initials
    }

    val initials: String
        get() = _getInitials()

    private fun _getNameSurname() = "$name $surname"

    val nameSurname: String
        get() = _getNameSurname()
}