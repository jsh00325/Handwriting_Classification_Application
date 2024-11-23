package com.classification.handwriting.presentation.select_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.classification.handwriting.domain.model.ModelItem
import com.classification.handwriting.domain.usecase.GetModelListUseCase
import com.classification.handwriting.presentation.select_model.data.SelectModelUiItem
import com.classification.handwriting.presentation.select_model.data.SelectModelUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectModelViewModel @Inject constructor(
    getModelListUseCase: GetModelListUseCase
) : ViewModel() {

    private val _modelList = MutableStateFlow<List<SelectModelUiItem>>(emptyList())
    val modelList = _modelList.asStateFlow()

    private val _selectModelState = MutableStateFlow<SelectModelUiState>(SelectModelUiState.Idle)
    val selectModelState = _selectModelState.asStateFlow()

    init {
        _modelList.value = getModelListUseCase().map {
            SelectModelUiItem(it.modelName, it.modelPath, true)
        }
    }

    fun updateModelList(position: Int, checkValue: Boolean) {
        _modelList.update { currentList ->
            currentList.toMutableList().apply {
                this[position].isSelected = checkValue
            }
        }
    }

    fun selectModel() {
        viewModelScope.launch(Dispatchers.IO) {
            if (isValidModelSelected()) {
                _selectModelState.value = SelectModelUiState.SelectSuccess(
                    modelList.value.filter { it.isSelected }.map {
                        ModelItem(it.modelName, it.modelPath)
                    }
                )
            } else {
                _selectModelState.value = SelectModelUiState.SelectFail
            }
        }
    }

    private fun isValidModelSelected(): Boolean = modelList.value.any { it.isSelected }

    fun resetSelectModelState() {
        _selectModelState.value = SelectModelUiState.Idle
    }
}