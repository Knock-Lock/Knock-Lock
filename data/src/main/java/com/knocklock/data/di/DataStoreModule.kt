package com.knocklock.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import com.knocklock.data.source.local.lockscreen.LockScreenPreference
import com.knocklock.data.source.local.lockscreen.LockScreenPreferenceSerializer
import com.knocklock.data.source.local.userpreference.UserPreference
import com.knocklock.data.source.local.userpreference.UserPreferenceSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    fun providesUserPreferencesDataStore(
        @ApplicationContext context: Context,
    ): DataStore<UserPreference> {
        return DataStoreFactory.create(
            serializer = UserPreferenceSerializer()
        ) {
            File("${context.cacheDir.path}/${UserPreference.localPath}")
        }
    }

    @Provides
    @Singleton
    fun providesLockScreenPreferencesDataStore(
        @ApplicationContext context: Context,
    ): DataStore<LockScreenPreference> {
        return DataStoreFactory.create(
            serializer = LockScreenPreferenceSerializer()
        ) {
            File("${context.cacheDir.path}/${LockScreenPreference.localPath}")
        }
    }
}