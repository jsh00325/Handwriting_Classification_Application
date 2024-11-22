package com.classification.handwriting.presentation.select_model

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.classification.handwriting.databinding.ActivitySelectModelBinding

class SelectModelActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySelectModelBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectModelBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}