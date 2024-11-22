package com.classification.handwriting

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class HandwritingClassificationApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // 항상 다크모드 해제
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}