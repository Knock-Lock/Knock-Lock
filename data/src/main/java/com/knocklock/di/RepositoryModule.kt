package com.knocklock.di

import com.knocklock.data.repository.NotificationRepositoryImpl
import com.knocklock.domain.repository.NotificationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * @Created by 김현국 2022/12/12
 * @Time 5:41 PM
 */

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Binds
    fun provideNotificationRepository(
        repositoryImpl: NotificationRepositoryImpl
    ): NotificationRepository
}
