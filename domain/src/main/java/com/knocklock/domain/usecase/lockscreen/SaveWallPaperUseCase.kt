package com.knocklock.domain.usecase.lockscreen

import com.knocklock.domain.repository.LockScreenRepository
import javax.inject.Inject

class SaveWallPaperUseCase @Inject constructor(
    private val lockScreenRepository: LockScreenRepository
) {
    suspend operator fun invoke(imageUri: String?) = lockScreenRepository.saveWallPaperImage(imageUri)
}