package com.classification.handwriting.domain.usecase

import android.graphics.Bitmap
import com.classification.handwriting.domain.repository.ImageProcessingRepository
import javax.inject.Inject

class BinarizeImageUseCase @Inject constructor(
    private val imageProcessingRepository: ImageProcessingRepository
) {

    operator fun invoke(image: Bitmap): Bitmap =
        imageProcessingRepository.binarizeBitmapImage(image)
}