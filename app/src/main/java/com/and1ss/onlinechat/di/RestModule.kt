package com.and1ss.onlinechat.di

import com.and1ss.onlinechat.api.rest.RestWrapper
import com.and1ss.onlinechat.api.rest.RestWrapperImpl
import com.and1ss.onlinechat.util.shared_preferences.SharedPreferencesWrapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class RestModule {
    @Provides
    @Singleton
    fun getRestWrapper(sharedPreferencesWrapper: SharedPreferencesWrapper): RestWrapper
            = RestWrapperImpl(sharedPreferencesWrapper)
}