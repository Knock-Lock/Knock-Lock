package com.knocklock.domain.usecase.setting

import com.knocklock.domain.repository.UserRepository
import javax.inject.Inject

class ActivateLockUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(checked: Boolean) = userRepository.activateLock(checked)
}