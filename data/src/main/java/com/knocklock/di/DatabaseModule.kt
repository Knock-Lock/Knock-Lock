package com.knocklock.di

import android.content.Context
import androidx.room.Room
import com.knocklock.data.source.local.AppDatabase
import com.knocklock.data.source.local.AppDatabase.Companion.AppDatabaseName
import com.knocklock.data.source.local.AppDatabase_Impl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * @Created by 김현국 2022/12/12
 * @Time 5:31 PM
 */

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Singleton
    @Provides
    fun provideRoomDatabase(
        @ApplicationContext context: Context
    ): AppDatabase = Room
        .databaseBuilder(context, AppDatabase::class.java, AppDatabaseName)
        .build()
}
