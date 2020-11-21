package com.and1ss.onlinechat.api.rest.rest_wrapper

import com.and1ss.onlinechat.api.model.AccountInfo
import com.and1ss.onlinechat.api.rest.ApiEndpoints

interface RestWrapper {
    fun getApi(): ApiEndpoints
    fun getMyAccount(): AccountInfo
    fun getAccessToken(): String
    fun saveAccessToken(accessToken: String)
    fun saveMyAccount(accountInfo: AccountInfo)
}