package com.knocklock.domain.usecase.setting

import com.knocklock.domain.model.AuthenticationType
import com.knocklock.domain.repository.UserRepository
import javax.inject.Inject

class ChangeAuthenticationTypeUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(type: AuthenticationType) = userRepository.changeMode(type)
}