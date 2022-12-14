package com.knocklock.data.di

import com.knocklock.data.repository.NotificationRepositoryImpl
import com.knocklock.data.repository.UserRepositoryImpl
import com.knocklock.domain.repository.NotificationRepository
import com.knocklock.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindUserRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun provideNotificationRepository(repositoryImpl: NotificationRepositoryImpl): NotificationRepository
}