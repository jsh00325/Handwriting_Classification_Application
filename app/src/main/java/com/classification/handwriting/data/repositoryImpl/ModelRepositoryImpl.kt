package com.classification.handwriting.data.repositoryImpl

import android.graphics.Bitmap
import android.util.Log
import com.classification.handwriting.data.datasource.TensorflowLiteDataSource
import com.classification.handwriting.data.entity.InferenceResultEntity
import com.classification.handwriting.domain.model.ModelItem
import com.classification.handwriting.domain.model.ModelResultItem
import com.classification.handwriting.domain.repository.ModelRepository
import org.tensorflow.lite.Interpreter
import javax.inject.Inject

class ModelRepositoryImpl @Inject constructor(
    private val dataSource: TensorflowLiteDataSource,
) : ModelRepository {
    // TODO: 모델 구현 완료 후 수정 필요
    override fun getModelList(): List<ModelItem> = listOf(
        ModelItem("Test Model 1", "test_model.tflite"),
        ModelItem("Test Model 2", "test_model.tflite"),
        ModelItem("Test Model 3", "test_model.tflite")
    )

    override fun classifyImage(binarizedImage: Bitmap, model: ModelItem): ModelResultItem {
        Log.d(TAG, "Model(${model.modelName}) classification start")
        val loadedModel = dataSource.createLoadedModel(model.modelPath)

        Log.d(TAG, "start inference with CPU")
        val cpuResult = processInterpreter(
            dataSource.createCPUInterpreter(loadedModel),
            binarizedImage
        )

        // TODO: GPU에서 반드시 실패하는 오류 수정 필요
        Log.d(TAG, "start inference with GPU")
        val gpuResult = processInterpreter(
            dataSource.createGPUInterpreter(loadedModel),
            binarizedImage
        )

        Log.d(TAG, "start inference with NPU")
        val npuResult = processInterpreter(
            dataSource.createNPUInterpreter(loadedModel),
            binarizedImage
        )

        Log.d(TAG, "")

        val predictGenderIndex = extractPredictGender(cpuResult, gpuResult, npuResult)
        val predictAgeIndex = extractPredictAge(cpuResult, gpuResult, npuResult)

        return ModelResultItem(
            model.modelName,
            predictGenderIndex,
            predictAgeIndex,
            cpuResult?.inferenceTime,
            gpuResult?.inferenceTime,
            npuResult?.inferenceTime
        )
    }

    private fun processInterpreter(
        interpreter: Interpreter?,
        binarizedImage: Bitmap
    ) = interpreter?.let {
        for (i in 0 until WARMUP_COUNT) {
            dataSource.runInference(it, binarizedImage)
        }

        val result = dataSource.runInference(it, binarizedImage, true)
        dataSource.closeInterpreter(it)
        result
    }

    private fun extractPredictGender(
        cpuResult: InferenceResultEntity?,
        gpuResult: InferenceResultEntity?,
        npuResult: InferenceResultEntity?
    ): String {
        val result = listOfNotNull(cpuResult, gpuResult, npuResult)
        if (result.isEmpty()) return GENDER_RESULT_FAIL

        val isAllSame = result.map { it.predictGender }.distinct().size == 1
        return if (isAllSame) GENDER_RESULT_INDEX[result.first().predictGender] else GENDER_RESULT_FAIL
    }

    private fun extractPredictAge(
        cpuResult: InferenceResultEntity?,
        gpuResult: InferenceResultEntity?,
        npuResult: InferenceResultEntity?
    ): String {
        val result = listOfNotNull(cpuResult, gpuResult, npuResult)
        if (result.isEmpty()) return AGE_RESULT_FAIL

        val isAllSame = result.map { it.predictAge }.distinct().size == 1
        return if (isAllSame) AGE_RESULT_INDEX[result.first().predictAge] else AGE_RESULT_FAIL
    }

    companion object {
        private const val TAG = "jsh00325-ModelRepository"
        private const val WARMUP_COUNT = 10
        private val GENDER_RESULT_INDEX = listOf("남성", "여성")
        private const val GENDER_RESULT_FAIL = "실패"
        private val AGE_RESULT_INDEX = listOf("10대", "20대", "30대", "40대", "50대", "실패")
        private const val AGE_RESULT_FAIL = "실패"
    }
}