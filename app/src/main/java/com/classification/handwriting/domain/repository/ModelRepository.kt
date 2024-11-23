package com.classification.handwriting.domain.repository

import android.graphics.Bitmap
import com.classification.handwriting.domain.model.ModelItem
import com.classification.handwriting.domain.model.ModelResultItem

interface ModelRepository {
    fun getModelList(): List<ModelItem>

    suspend fun classifyImage(binarizedImage: Bitmap, model: ModelItem): ModelResultItem
}