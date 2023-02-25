package com.knocklock.data.di

import android.content.Context
import androidx.room.Room
import com.knocklock.data.source.local.room.AppDatabase
import com.knocklock.data.source.local.room.dao.GroupNotificationDao
import com.knocklock.data.source.local.room.dao.NotificationDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * @Created by 김현국 2023/02/24
 */

@Module
@InstallIn(SingletonComponent::class)
class DataBaseModule {

    companion object {
        const val appDatabaseName = "appDatabase.db"
    }

    @Singleton
    @Provides
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase = Room.databaseBuilder(context, AppDatabase::class.java, appDatabaseName).build()

    @Singleton
    @Provides
    fun provideNotificationDao(appDatabase: AppDatabase): NotificationDao = appDatabase.notificationDao()

    @Singleton
    @Provides
    fun provideGroupNotificationDao(appDatabase: AppDatabase): GroupNotificationDao = appDatabase.groupNotificationDao()
}
