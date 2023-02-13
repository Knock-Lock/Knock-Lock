package com.knocklock.data.di

import com.knocklock.data.repository.LockScreenRepositoryImpl
import com.knocklock.data.repository.UserRepositoryImpl
import com.knocklock.domain.repository.LockScreenRepository
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
    abstract fun bindLockScreenRepository(
        lockScreenRepositoryImpl: LockScreenRepositoryImpl
    ): LockScreenRepository
}