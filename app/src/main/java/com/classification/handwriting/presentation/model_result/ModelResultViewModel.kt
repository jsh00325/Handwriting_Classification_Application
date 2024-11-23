package com.classification.handwriting.presentation.model_result

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.classification.handwriting.domain.model.ModelItem
import com.classification.handwriting.domain.usecase.BinarizeImageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class ModelResultViewModel @Inject constructor(
    private val binarizeImage: BinarizeImageUseCase
) : ViewModel() {

    private val _selectedModel = MutableStateFlow<List<ModelItem>>(emptyList())
    private val _handwritingImage = MutableStateFlow<Bitmap?>(null)
    val handwritingImage = _handwritingImage

    fun updateSelectedModel(selectedModelList: List<ModelItem>) {
        _selectedModel.value = selectedModelList
    }

    fun updateHandwritingImage(handwritingBitmap: Bitmap) {
        _handwritingImage.value = binarizeImage(handwritingBitmap)
    }
}