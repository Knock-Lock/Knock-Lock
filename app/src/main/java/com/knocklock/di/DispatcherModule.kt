package com.knocklock.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier


@Module
@InstallIn(SingletonComponent::class)
class DispatcherModule {

    @Provides
    @IODispatcher
    fun provideDispatcherIO(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @MainDispatcher
    fun provideDispatcherMain(): CoroutineDispatcher = Dispatchers.Main

    @Provides
    @DefaultDispatcher
    fun provideDispatcherDefault(): CoroutineDispatcher = Dispatchers.Default

    @Provides
    @MainImmediateDispatcher
    fun provideDispatcherMainImmediate(): CoroutineDispatcher = Dispatchers.Main.immediate
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IODispatcher

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MainDispatcher

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DefaultDispatcher

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MainImmediateDispatcher