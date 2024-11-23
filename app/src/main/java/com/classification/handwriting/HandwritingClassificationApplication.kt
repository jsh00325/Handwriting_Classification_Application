package com.classification.handwriting

import android.app.Application
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import dagger.hilt.android.HiltAndroidApp
import org.opencv.android.OpenCVLoader

@HiltAndroidApp
class HandwritingClassificationApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // 항상 다크모드 해제
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // OpenCV 라이브러리 로드
        if (OpenCVLoader.initDebug()) {
            Log.d(TAG, "OpenCVLoader.initDebug() success")
        } else {
            Log.e(TAG, "OpenCVLoader.initDebug() fail")
        }
    }

    companion object {
        private const val TAG = "HandwritingClassificationApplication"
    }
}