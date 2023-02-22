package com.knocklock.domain.usecase.lockscreen

import com.knocklock.domain.repository.LockScreenRepository
import javax.inject.Inject

class GetLockScreenUseCase @Inject constructor(
    private val lockScreenRepository: LockScreenRepository
) {
    operator fun invoke() = lockScreenRepository.getLockScreen()
}