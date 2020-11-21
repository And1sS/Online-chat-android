package com.and1ss.onlinechat.api.rest.rest_wrapper

import com.and1ss.onlinechat.api.dto.LoginInfoDTO
import com.and1ss.onlinechat.api.model.AccountInfo
import com.and1ss.onlinechat.api.rest.ApiEndpoints

interface RestWrapper {
    suspend fun login(loginCredentials: LoginInfoDTO)
    fun getApi(): ApiEndpoints
    fun getMyAccount(): AccountInfo
    fun getAccessToken(): String
    suspend fun saveAccessToken(accessToken: String)
    fun saveMyAccount(accountInfo: AccountInfo)
}