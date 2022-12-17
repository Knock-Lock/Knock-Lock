package com.knocklock.domain.usecase.setting

import com.knocklock.domain.repository.UserRepository
import javax.inject.Inject

class UpdatePasswordUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(password: String) = userRepository.updatedPassword(password)
}