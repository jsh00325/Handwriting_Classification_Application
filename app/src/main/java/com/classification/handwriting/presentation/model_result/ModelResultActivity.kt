package com.classification.handwriting.presentation.model_result

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.classification.handwriting.databinding.ActivityModelResultBinding

class ModelResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityModelResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityModelResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}