package com.classification.handwriting.presentation.camera

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.classification.handwriting.databinding.ActivityCameraBinding
import com.classification.handwriting.domain.model.ModelItem

class CameraActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCameraBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val modelList: ArrayList<ModelItem>? = intent.getParcelableArrayListExtra("modelList")
    }
}