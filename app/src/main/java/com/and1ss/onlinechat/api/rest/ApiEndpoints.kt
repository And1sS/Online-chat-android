package com.and1ss.onlinechat.api.rest

import com.and1ss.onlinechat.api.dto.*
import retrofit2.http.*

private const val AUTH_SERVICE = "user-service/auth"
private const val FRIENDS_SERVICE = "user-service/friends"
private const val USER_SERVICE = "user-service"
private const val GROUP_CHAT_SERVICE = "group-chat-service/chats"
private const val PRIVATE_CHAT_SERVICE = "private-chat-service/chats"

interface ApiEndpoints {
    @PUT("$AUTH_SERVICE/login")
    suspend fun login(@Body loginCredentials: LoginInfoDTO): AccessTokenDTO

    @GET("$USER_SERVICE/account")
    suspend fun getMyAccount(): AccountInfoRetrievalDTO

    @GET("$USER_SERVICE/users")
    suspend fun getUsersWhoAreNotCurrentUserFriendsWithLoginLike(
        @Query("login_like") loginLike: String
    ): List<AccountInfoRetrievalDTO>

    @POST(GROUP_CHAT_SERVICE)
    suspend fun createGroupChat(@Body groupChatCreationDTO: GroupChatCreationDTO): GroupChatRetrievalDTO

    @GET(GROUP_CHAT_SERVICE)
    suspend fun getAllGroupChats(): List<GroupChatRetrievalDTO>

    @GET("$GROUP_CHAT_SERVICE/{chat_id}/messages")
    suspend fun getAllMessagesForGroupChat(@Path("chat_id") chatId: String): List<GroupMessageRetrievalDTO>

    @GET("$FRIENDS_SERVICE/all")
    suspend fun getFriends(@Query("accepted_only") acceptedOnly: Boolean): List<FriendRetrievalDTO>

    @GET("$FRIENDS_SERVICE/without_private_chat")
    suspend fun getFriendsWithoutPrivateChat(): List<AccountInfoRetrievalDTO>

    @DELETE(FRIENDS_SERVICE)
    suspend fun deleteFriend(@Query("user_id") userId: String)

    @PUT(FRIENDS_SERVICE)
    suspend fun acceptFriend(@Query("user_id") userId: String)

    @POST(FRIENDS_SERVICE)
    suspend fun sendFriendRequest(@Body friendCreationDTO: FriendCreationDTO): FriendRetrievalDTO

    @POST("$PRIVATE_CHAT_SERVICE")
    suspend fun createPrivateChat(@Body chatCreationDTO: PrivateChatCreationDTO): PrivateChatRetrievalDTO

    @GET(PRIVATE_CHAT_SERVICE)
    suspend fun getAllPrivateChats(): List<PrivateChatRetrievalDTO>

    @GET("$PRIVATE_CHAT_SERVICE/{chat_id}/messages")
    suspend fun getAllMessagesForPrivateChat(@Path("chat_id") chatId: String): List<PrivateMessageRetrievalDTO>
}