package com.classification.handwriting.data.repositoryImpl

import android.graphics.Bitmap
import com.classification.handwriting.domain.repository.ImageProcessingRepository
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc
import javax.inject.Inject

class ImageProcessingRepositoryImpl @Inject constructor() : ImageProcessingRepository {

    override fun binarizeBitmapImage(image: Bitmap): Bitmap {
        // Bitmap을 Mat으로 변환
        val src = Mat()
        Utils.bitmapToMat(image, src)

        // 그레이스케일 변환
        val gray = Mat()
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY)

        // Otsu 이진화 적용
        val binary = Mat()
        Imgproc.threshold(gray, binary, 0.0, 255.0,
            Imgproc.THRESH_BINARY or Imgproc.THRESH_OTSU)

        // 결과 Mat을 Bitmap으로 변환
        val resultBitmap = Bitmap.createBitmap(
            binary.cols(), binary.rows(),
            Bitmap.Config.ARGB_8888
        )
        Utils.matToBitmap(binary, resultBitmap)

        // 메모리 해제
        src.release()
        gray.release()
        binary.release()

        return resultBitmap
    }
}