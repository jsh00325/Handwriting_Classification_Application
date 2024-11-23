package com.classification.handwriting.data.repositoryImpl

import com.classification.handwriting.domain.model.ModelItem
import com.classification.handwriting.domain.repository.ModelRepository
import javax.inject.Inject

class ModelRepositoryImpl @Inject constructor() : ModelRepository {
    // TODO: 모델 구현 완료 후 수정 필요
    override fun getModelList(): List<ModelItem> = listOf(
        ModelItem("Temp Model 1", "model1.tflite"),
        ModelItem("Temp Model 2", "model2.tflite"),
        ModelItem("Temp Model 3", "model3.tflite")
    )
}