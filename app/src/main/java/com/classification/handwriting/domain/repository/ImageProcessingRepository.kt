package com.classification.handwriting.domain.repository

import android.graphics.Bitmap

interface ImageProcessingRepository {

    fun binarizeBitmapImage(image: Bitmap): Bitmap
}