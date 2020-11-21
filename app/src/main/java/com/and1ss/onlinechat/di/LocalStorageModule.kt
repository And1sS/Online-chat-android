package com.and1ss.onlinechat.di

import android.content.Context
import com.and1ss.onlinechat.util.shared_preferences.SharedPreferencesWrapperImpl
import com.and1ss.onlinechat.util.shared_preferences.SharedPreferencesWrapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class LocalStorageModule {
    @Provides
    @Singleton
    fun getSharedPreferencesWrapper(@ApplicationContext appContext: Context):
            SharedPreferencesWrapper = SharedPreferencesWrapperImpl(appContext)
}