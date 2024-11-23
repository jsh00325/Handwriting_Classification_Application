package com.classification.handwriting.domain.usecase

import com.classification.handwriting.domain.repository.ModelRepository
import javax.inject.Inject

class GetModelListUseCase @Inject constructor(private val modelRepository: ModelRepository) {

    operator fun invoke() = modelRepository.getModelList()
}