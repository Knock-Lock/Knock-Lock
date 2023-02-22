package com.knocklock.data.repository

import androidx.datastore.core.DataStore
import com.knocklock.data.mapper.toDomain
import com.knocklock.data.source.local.lockscreen.LockScreenBackground
import com.knocklock.data.source.local.lockscreen.LockScreenPreference
import com.knocklock.domain.model.LockScreen
import com.knocklock.domain.repository.LockScreenRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LockScreenRepositoryImpl @Inject constructor(
    private val lockScreenDataStore: DataStore<LockScreenPreference>
) : LockScreenRepository {
    override fun getLockScreen(): Flow<LockScreen> =
        lockScreenDataStore.data.map { it.toDomain() }

    override suspend fun clear() {
        lockScreenDataStore.updateData { LockScreenPreference.getDefaultInstance() }
    }

    override suspend fun saveWallPaperImage(imageUri: String?) {
        lockScreenDataStore.updateData { lockScreenDataStore ->
            val background =
                if (imageUri.isNullOrBlank()) LockScreenBackground.DefaultWallPaper else LockScreenBackground.LocalImage(imageUri)
            lockScreenDataStore.copy(
                background = background
            )
        }
    }
}
