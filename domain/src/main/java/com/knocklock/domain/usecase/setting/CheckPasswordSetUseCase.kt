package com.knocklock.domain.usecase.setting

import com.knocklock.domain.repository.UserRepository
import javax.inject.Inject

class CheckPasswordSetUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke() = userRepository.checkPasswordSet()
}