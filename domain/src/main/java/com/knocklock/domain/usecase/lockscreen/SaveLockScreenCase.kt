package com.knocklock.domain.usecase.lockscreen

import com.knocklock.domain.model.LockScreen
import com.knocklock.domain.repository.LockScreenRepository
import javax.inject.Inject

class SaveLockScreenCase @Inject constructor(
    private val lockScreenRepository: LockScreenRepository
) {
    suspend operator fun invoke(lockScreen: LockScreen) = lockScreenRepository.saveLockScreen(lockScreen)
}