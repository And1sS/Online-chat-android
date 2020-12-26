package com.and1ss.onlinechat.di

import com.and1ss.onlinechat.api.rest.RestWrapper
import com.and1ss.onlinechat.api.ws.WebSocketWrapper
import com.and1ss.onlinechat.api.ws.WebSocketWrapperImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class WebSocketModule {
    @Provides
    @Singleton
    fun getClient(): OkHttpClient = OkHttpClient
        .Builder()
        .pingInterval(2, TimeUnit.SECONDS)
        .connectTimeout(5, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    fun getWebSocketWrapper(
        okHttpClient: OkHttpClient,
        restWrapper: RestWrapper
    ): WebSocketWrapper = WebSocketWrapperImpl(restWrapper, okHttpClient)
}