package com.classification.handwriting.domain.usecase

import android.graphics.Bitmap
import com.classification.handwriting.domain.model.ModelItem
import com.classification.handwriting.domain.repository.ModelRepository
import javax.inject.Inject

class ClassifyImageUseCase @Inject constructor(
    private val modelRepository: ModelRepository,
) {

    suspend operator fun invoke(image: Bitmap, model: ModelItem) =
        modelRepository.classifyImage(image, model)
}