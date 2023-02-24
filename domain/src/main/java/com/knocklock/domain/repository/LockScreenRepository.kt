package com.knocklock.domain.repository

import com.knocklock.domain.model.LockScreen
import kotlinx.coroutines.flow.Flow

interface LockScreenRepository {
    fun getLockScreen(): Flow<LockScreen>

    suspend fun clear()

    suspend fun saveWallPaperImage(imageUri: String?)
}
