package com.and1ss.onlinechat.api.rest

import com.and1ss.onlinechat.api.dto.*
import retrofit2.http.*

private const val USER_SERVICE = "user-service/auth"
private const val GROUP_CHAT_SERVIVCE = "group-chat-service/chats"
interface ApiEndpoints {
    @PUT("$USER_SERVICE/login")
    suspend fun login(@Body loginCredentials: LoginInfoDTO): AccessTokenDTO

    @GET("$USER_SERVICE/account")
    suspend fun getMyAccount(): AccountInfoRetrievalDTO

    @GET("$GROUP_CHAT_SERVIVCE/all")
    suspend fun getAllGroupChats(): List<GroupChatRetrievalDTO>

    @GET("$GROUP_CHAT_SERVIVCE/{chat_id}/messages")
    suspend fun getAllMessagesForGroupChat(@Path("chat_id") chatId: String): List<GroupMessageRetrievalDTO>

    @GET("$GROUP_CHAT_SERVIVCE/all")
    suspend fun getAllPrivateChats(): List<GroupChatRetrievalDTO>

}