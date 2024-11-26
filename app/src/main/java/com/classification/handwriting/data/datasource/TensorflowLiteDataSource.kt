package com.classification.handwriting.data.datasource

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import com.classification.handwriting.data.entity.InferenceResultEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.gpu.GpuDelegate
import org.tensorflow.lite.nnapi.NnApiDelegate
import org.tensorflow.lite.support.image.TensorImage
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel
import javax.inject.Inject

class TensorflowLiteDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    fun createLoadedModel(modelPath: String) = loadModelFile(modelPath)

    // 모델 닫기
    fun closeInterpreter(interpreter: Interpreter) {
        interpreter.close()
    }

    // 모델 파일 로드
    private fun loadModelFile(modelPath: String): ByteBuffer {
        val assetManager = context.assets
        val fileDescriptor = assetManager.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    // CPU를 사용한 Interpreter 생성
    fun createCPUInterpreter(loadedModel: ByteBuffer, threadCount: Int = 4): Interpreter? = try {
        Interpreter(loadedModel, Interpreter.Options().apply {
            numThreads = threadCount
        })
    } catch (e: Throwable) {
        Log.e(TAG, "CPU Interpreter creation failed: ${e.message}", e)
        null
    }

    // GPU를 사용한 Interpreter 생성
    fun createGPUInterpreter(loadedModel: ByteBuffer): Interpreter? = try {
        Interpreter(loadedModel, Interpreter.Options().apply {
            addDelegate(GpuDelegate())
        })
    } catch (e: Throwable) {
        Log.e(TAG, "GPU Interpreter creation failed: ${e.message}", e)
        null
    }

    // NNAPI를 사용한 Interpreter 생성
    fun createNPUInterpreter(loadedModel: ByteBuffer): Interpreter? = try {
        Interpreter(loadedModel, Interpreter.Options().apply {
            addDelegate(NnApiDelegate())
        })
    } catch (e: Throwable) {
        Log.e(TAG, "NPU Interpreter creation failed: ${e.message}", e)
        null
    }

    fun runInference(interpreter: Interpreter, binarizeImage: Bitmap, isDebug: Boolean = false): InferenceResultEntity {

        val image = TensorImage.fromBitmap(binarizeImage)
        // 이진화된 이미지는 픽셀당 1채널(흑백)만 필요
        val inputArray =
            ByteBuffer.allocateDirect(MODEL_INPUT_WIDTH * MODEL_INPUT_HEIGHT * 1 * 4).apply {
                order(ByteOrder.nativeOrder())
            }

        // 이진화된 이미지의 경우 픽셀값이 0 또는 255
        for (y in 0 until MODEL_INPUT_HEIGHT) {
            for (x in 0 until MODEL_INPUT_WIDTH) {
                val pixel = binarizeImage.getPixel(x, y)
                // 흑백 이미지이므로 RGB 값 중 하나만 사용해도 됨
                // 255로 나누어 0 또는 1로 정규화
                inputArray.putFloat(if (pixel == Color.BLACK) 1f else 0f)
            }
        }

        val outputArray = Array(1) { LongArray(2) }

        val startTime = System.nanoTime()
        interpreter.run(inputArray, outputArray)
        val endTime = System.nanoTime()

        val inferenceTimeMs = (endTime - startTime) / 1_000_000 // 나노초를 밀리초로 변환
        if (isDebug) Log.d(TAG, "output: ${outputArray[0].contentToString()}")

        return InferenceResultEntity(
            predictGender = outputArray[0][0].toInt(),
            predictAge = outputArray[0][1].toInt(),
            inferenceTime = inferenceTimeMs
        )
    }

    companion object {
        private const val TAG = "jsh00325-datasource"
        private const val MODEL_INPUT_WIDTH = 180
        private const val MODEL_INPUT_HEIGHT = 76
    }
}