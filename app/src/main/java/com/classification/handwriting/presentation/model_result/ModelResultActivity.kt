package com.classification.handwriting.presentation.model_result

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.classification.handwriting.R
import com.classification.handwriting.databinding.ActivityModelResultBinding
import com.classification.handwriting.domain.model.ModelItem
import com.classification.handwriting.presentation.IntentContract.CROPPED_IMAGE_EXTRA_NAME
import com.classification.handwriting.presentation.IntentContract.MODEL_LIST_EXTRA_NAME
import kotlinx.coroutines.launch

class ModelResultActivity : AppCompatActivity() {
    private val viewModel: ModelResultViewModel by viewModels()
    private lateinit var binding: ActivityModelResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityModelResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getSelectedModelList()
        getCroppedImage()
        collectHandwritingImage()
    }

    private fun getSelectedModelList() {
        intent.getParcelableArrayListExtra<ModelItem>(MODEL_LIST_EXTRA_NAME)?.let { selectedModel ->
            viewModel.updateSelectedModel(selectedModel)
        } ?: {
            Toast.makeText(
                this,
                R.string.model_result_model_list_not_found_error,
                Toast.LENGTH_SHORT
            ).show()
            finish()
        }
    }

    private fun getCroppedImage() {
        intent.getByteArrayExtra(CROPPED_IMAGE_EXTRA_NAME)?.let { byteArray ->
            val croppedImageBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            viewModel.updateHandwritingImage(croppedImageBitmap)
        } ?: {
            Toast.makeText(
                this,
                R.string.model_result_image_not_found_error,
                Toast.LENGTH_SHORT
            ).show()
            finish()
        }
    }

    private fun collectHandwritingImage() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.handwritingImage.collect { bitmap ->
                    bitmap?.let {
                        binding.modelResultImagePreviewImageView.setImageBitmap(bitmap)
                    }
                }
            }
        }
    }
}