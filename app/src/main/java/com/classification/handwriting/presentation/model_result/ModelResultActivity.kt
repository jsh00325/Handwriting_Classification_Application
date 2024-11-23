package com.classification.handwriting.presentation.model_result

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.classification.handwriting.R
import com.classification.handwriting.databinding.ActivityModelResultBinding
import com.classification.handwriting.domain.model.ModelItem
import com.classification.handwriting.presentation.IntentContract.CROPPED_IMAGE_EXTRA_NAME
import com.classification.handwriting.presentation.IntentContract.MODEL_LIST_EXTRA_NAME
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ModelResultActivity : AppCompatActivity() {
    private val viewModel: ModelResultViewModel by viewModels()
    private lateinit var binding: ActivityModelResultBinding
    private lateinit var adapter: ModelResultAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityModelResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getPreStepData()
        setupRecyclerView()
        collectHandwritingImage()
    }

    private fun getPreStepData() {
        val selectedModel = intent.getParcelableArrayListExtra<ModelItem>(MODEL_LIST_EXTRA_NAME)
        val imageBitmap = intent.getByteArrayExtra(CROPPED_IMAGE_EXTRA_NAME)?.let {
            BitmapFactory.decodeByteArray(it, 0, it.size)
        }

        if (selectedModel == null) {
            Toast.makeText(
                this,
                R.string.model_result_model_list_not_found_error,
                Toast.LENGTH_SHORT
            ).show()
            finish()
            return
        } else if (imageBitmap == null) {
            Toast.makeText(
                this,
                R.string.model_result_image_not_found_error,
                Toast.LENGTH_SHORT
            ).show()
            finish()
            return
        } else {
            viewModel.updatePreStepData(selectedModel, imageBitmap)
        }
    }

    private fun setupRecyclerView() {
        adapter = ModelResultAdapter()

        binding.modelResultRecyclerView.apply {
            adapter = this@ModelResultActivity.adapter
            layoutManager = LinearLayoutManager(this@ModelResultActivity)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.modelResult.collect {
                    adapter.submitList(it)
                }
            }
        }
    }

    private fun collectHandwritingImage() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.handwritingImage.collect { imageItem ->
                    imageItem?.let { item ->
                        setupImagePreview(item.originImage, item.binarizedImage)
                    }
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupImagePreview(originImage: Bitmap, binarizedImage: Bitmap) {
        binding.modelResultImagePreviewImageView.setImageBitmap(originImage)
        binding.modelResultImagePreviewImageView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.modelResultImagePreviewImageView.setImageBitmap(binarizedImage)
                    true
                }

                MotionEvent.ACTION_UP -> {
                    binding.modelResultImagePreviewImageView.setImageBitmap(originImage)
                    true
                }

                else -> false
            }
        }
    }
}