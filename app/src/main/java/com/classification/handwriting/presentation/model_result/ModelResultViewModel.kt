package com.classification.handwriting.presentation.model_result

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.classification.handwriting.domain.model.ModelItem
import com.classification.handwriting.domain.model.ModelResultItem
import com.classification.handwriting.domain.usecase.BinarizeImageUseCase
import com.classification.handwriting.domain.usecase.ClassifyImageUseCase
import com.classification.handwriting.presentation.model_result.uidata.ImageUiItem
import com.classification.handwriting.presentation.model_result.uidata.ModelResultUiItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ModelResultViewModel @Inject constructor(
    private val binarizeImage: BinarizeImageUseCase,
    private val classifyImage: ClassifyImageUseCase
) : ViewModel() {

    private val _selectedModel = MutableStateFlow<List<ModelItem>>(emptyList())
    private val _handwritingImage = MutableStateFlow<ImageUiItem?>(null)
    val handwritingImage = _handwritingImage

    private val _modelResult = MutableStateFlow<List<ModelResultUiItem>>(emptyList())
    val modelResult = _modelResult

    fun updatePreStepData(selectedModelList: List<ModelItem>, handwritingBitmap: Bitmap) {
        val binarizedImage = binarizeImage(handwritingBitmap)
        _selectedModel.value = selectedModelList
        _handwritingImage.value = ImageUiItem(
            originImage = handwritingBitmap,
            binarizedImage = binarizedImage
        )
        runModels(binarizedImage)
    }

    private fun runModels(image: Bitmap) {
        viewModelScope.launch(Dispatchers.IO) {
            _selectedModel.value.forEach { model ->
                val result = classifyImage(image, model)
                _modelResult.emit(_modelResult.value + result.toUiData())
            }
        }
    }

    private fun ModelResultItem.toUiData() = ModelResultUiItem(
        modelName = modelName,
        predictGender = predictGender,
        predictAge = predictAge,
        inferenceTime = "${inferenceTime}ms"
    )
}