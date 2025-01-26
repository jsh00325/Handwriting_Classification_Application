package com.classification.handwriting.data.repositoryImpl

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.core.content.ContextCompat.getString
import com.classification.handwriting.R
import com.classification.handwriting.data.datasource.TensorflowLiteDataSource
import com.classification.handwriting.data.entity.InferenceResultEntity
import com.classification.handwriting.domain.model.ModelItem
import com.classification.handwriting.domain.model.ModelResultItem
import com.classification.handwriting.domain.repository.ModelRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import org.tensorflow.lite.Interpreter
import javax.inject.Inject

class ModelRepositoryImpl @Inject constructor(
    private val dataSource: TensorflowLiteDataSource,
    @ApplicationContext private val context: Context
) : ModelRepository {
    // TODO: 모델 구현 완료 후 수정 필요
    override fun getModelList(): List<ModelItem> = listOf(
        ModelItem("EfficientNet Lite0", "git_effnet_el0.tflite"),
        ModelItem("MobileNet V3 Large", "MobileNetV3Large.tflite"),
        ModelItem("EfficientNet V2S", "EfficientNetV2S_gd_ag_model.tflite")
    )

    override fun classifyImage(binarizedImage: Bitmap, model: ModelItem): ModelResultItem {
        Log.d(TAG, "Model(${model.modelName}) classification start")
        val loadedModel = dataSource.createLoadedModel(model.modelPath)

        Log.d(TAG, "start inference with CPU")
        val cpuResult = processInterpreter(
            dataSource.createCPUInterpreter(loadedModel),
            binarizedImage
        )

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

        val predictGenderResIndex = extractPredictGender(cpuResult, gpuResult, npuResult)
        val predictAgeResIndex = extractPredictAge(cpuResult, gpuResult, npuResult)

        return ModelResultItem(
            model.modelName,
            getString(context, predictGenderResIndex),
            getString(context, predictAgeResIndex),
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
    ): Int {
        val result = listOfNotNull(cpuResult, gpuResult, npuResult)
        if (result.isEmpty()) return GENDER_RESULT_FAIL

        val isAllSame = result.map { it.predictGender }.distinct().size == 1
        return if (isAllSame) GENDER_RESULT_INDEX[result.first().predictGender] else GENDER_RESULT_FAIL
    }

    private fun extractPredictAge(
        cpuResult: InferenceResultEntity?,
        gpuResult: InferenceResultEntity?,
        npuResult: InferenceResultEntity?
    ): Int {
        val result = listOfNotNull(cpuResult, gpuResult, npuResult)
        if (result.isEmpty()) return AGE_RESULT_FAIL

        val isAllSame = result.map { it.predictAge }.distinct().size == 1
        return if (isAllSame) AGE_RESULT_INDEX[result.first().predictAge] else AGE_RESULT_FAIL
    }

    companion object {
        private const val TAG = "jsh00325-ModelRepository"
        private const val WARMUP_COUNT = 10
        private val GENDER_RESULT_INDEX = listOf(
            R.string.model_result_item_male,
            R.string.model_result_item_female
        )
        private val GENDER_RESULT_FAIL = R.string.model_result_item_fail
        private val AGE_RESULT_INDEX = listOf(
            R.string.model_result_item_age_10,
            R.string.model_result_item_age_20,
            R.string.model_result_item_age_30,
            R.string.model_result_item_age_40,
            R.string.model_result_item_age_50,
            R.string.model_result_item_fail
        )
        private val AGE_RESULT_FAIL = R.string.model_result_item_fail
    }
}