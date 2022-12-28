package com.knocklock.domain.repository

import com.knocklock.domain.model.AuthenticationType
import com.knocklock.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUser(): Flow<User>

    suspend fun updatedPassword(password: String)

    suspend fun changeMode(type: AuthenticationType)

    suspend fun activateLock(isActivated: Boolean)

    suspend fun checkPasswordSet()
}