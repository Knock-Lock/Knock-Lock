package com.knocklock.data.repository

import androidx.datastore.core.DataStore
import com.knocklock.data.mapper.toDomain
import com.knocklock.data.source.local.userpreference.AuthenticationType
import com.knocklock.data.source.local.userpreference.UserPreference
import com.knocklock.domain.model.User
import com.knocklock.domain.repository.UserRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDataStore: DataStore<UserPreference>
) : UserRepository {
    override suspend fun getUser(): User =
        userDataStore.data.first().toDomain()

    override suspend fun updatedPassword(password: String) {
        userDataStore.updateData { userPreference ->
            userPreference.copy(password = password)
        }
    }

    override suspend fun changeMode(isPasswordMode: Boolean) {
        userDataStore.updateData { userPreference ->
            if (isPasswordMode) {
                userPreference.copy(authenticationType = AuthenticationType.PASSWORD)
            } else {
                userPreference.copy(authenticationType = AuthenticationType.GESTURE)
            }
        }
    }
}