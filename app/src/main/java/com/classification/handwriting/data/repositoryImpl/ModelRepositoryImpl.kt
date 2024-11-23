package com.classification.handwriting.data.repositoryImpl

import android.graphics.Bitmap
import com.classification.handwriting.domain.model.ModelItem
import com.classification.handwriting.domain.model.ModelResultItem
import com.classification.handwriting.domain.repository.ModelRepository
import kotlinx.coroutines.delay
import javax.inject.Inject

class ModelRepositoryImpl @Inject constructor() : ModelRepository {
    // TODO: 모델 구현 완료 후 수정 필요
    override fun getModelList(): List<ModelItem> = listOf(
        ModelItem("Temp Model 1", "model1.tflite"),
        ModelItem("Temp Model 2", "model2.tflite"),
        ModelItem("Temp Model 3", "model3.tflite")
    )

    override suspend fun classifyImage(binarizedImage: Bitmap, model: ModelItem): ModelResultItem {
        // TODO: 모델 구현 완료 후 수정 필요
        // 0.3에서 0.7초 사이의 랜덤한 시간이 걸린다고 가정
        val randomTime = (300..700).random().toLong()
        delay(randomTime)

        val randomGender = listOf("남성", "여성").random()
        val randomAge = listOf("10대", "20대", "30대", "40대", "50대").random()

        return ModelResultItem(
            modelName = model.modelName,
            predictGender = randomGender,
            predictAge = randomAge,
            inferenceTime = randomTime
        )
    }
}