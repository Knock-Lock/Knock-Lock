package com.knocklock.data.di

import android.content.Context
import androidx.room.Room
import com.knocklock.data.source.local.notification.AppDatabase
import com.knocklock.data.source.local.notification.dao.GroupDao
import com.knocklock.data.source.local.notification.dao.NotificationDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * @Created by 김현국 2023/03/06
 */

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {
    @Singleton
    @Provides
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase = Room.databaseBuilder(context, AppDatabase::class.java, DB_NAME).build()

    @Singleton
    @Provides
    fun provideGroupDao(appDatabase: AppDatabase): GroupDao = appDatabase.groupDao()

    @Singleton
    @Provides
    fun provideNotificationDao(appDatabase: AppDatabase): NotificationDao = appDatabase.notificationDao()

    companion object {
        const val DB_NAME = "knocklock.db"
    }
}
