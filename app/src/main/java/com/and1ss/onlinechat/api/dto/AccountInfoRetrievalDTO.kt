package com.and1ss.onlinechat.api.dto

import com.and1ss.onlinechat.api.model.AccountInfo

data class AccountInfoRetrievalDTO(
    var id: String? = null,
    var name: String? = null,
    var login: String? = null,
    var surname: String? = null
) {
    @Throws(NullPointerException::class)
    fun mapToAccountInfoOrThrow() =
        AccountInfo(id!!, name!!, surname!!, login!!)

    fun isCompleted() = id != null && login != null && name != null && surname != null

    private fun _getInitials(): String {
        val nameLetter = name ?: ""
        val surnameLetter = surname ?: ""

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
}