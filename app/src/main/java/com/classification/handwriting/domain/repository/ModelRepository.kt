package com.classification.handwriting.domain.repository

import com.classification.handwriting.domain.model.ModelItem

interface ModelRepository {
    fun getModelList(): List<ModelItem>
}