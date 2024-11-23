package com.classification.handwriting.presentation.model_result

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.classification.handwriting.domain.model.ModelItem
import com.classification.handwriting.domain.usecase.BinarizeImageUseCase
import com.classification.handwriting.presentation.model_result.uidata.ImageUiItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class ModelResultViewModel @Inject constructor(
    private val binarizeImage: BinarizeImageUseCase
) : ViewModel() {

    private val _selectedModel = MutableStateFlow<List<ModelItem>>(emptyList())
    private val _handwritingImage = MutableStateFlow<ImageUiItem?>(null)
    val handwritingImage = _handwritingImage

    fun updatePreStepData(selectedModelList: List<ModelItem>, handwritingBitmap: Bitmap) {
        _selectedModel.value = selectedModelList
        _handwritingImage.value = ImageUiItem(
            originImage = handwritingBitmap,
            binarizedImage = binarizeImage(handwritingBitmap)
        )
        runModels()
    }

    private fun runModels() {
        // 모델 실행
    }
}