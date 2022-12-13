package com.knocklock.domain.repository

import com.knocklock.domain.model.User

interface UserRepository {
    suspend fun getUser(): User

    suspend fun updatedPassword(password: String)

    suspend fun changeMode(isPasswordMode: Boolean)
}