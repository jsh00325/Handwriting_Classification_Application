package com.classification.handwriting.presentation.select_model

import androidx.lifecycle.ViewModel
import com.classification.handwriting.domain.usecase.GetModelListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SelectModelViewModel @Inject constructor(
    getModelListUseCase: GetModelListUseCase
) : ViewModel() {

    private val _modelList = MutableStateFlow<List<SelectModelUiItem>>(emptyList())
    val modelList = _modelList.asStateFlow()

    init {
        _modelList.value = getModelListUseCase().map {
            SelectModelUiItem(it.modelName, true)
        }
    }

    fun updateModelList(position: Int, checkValue: Boolean) {
        _modelList.update { currentList ->
            currentList.toMutableList().apply {
                this[position].isSelected = checkValue
            }
        }
    }
}