package com.classification.handwriting.presentation.model_result.uidata

import android.graphics.Bitmap

data class ImageUiItem(
    val originImage: Bitmap,
    val binarizedImage: Bitmap
)
