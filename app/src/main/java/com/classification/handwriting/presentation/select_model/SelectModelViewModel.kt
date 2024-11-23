package com.classification.handwriting.presentation.select_model

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SelectModelViewModel @Inject constructor() : ViewModel() {

    private val _modelList = MutableStateFlow<List<SelectModelUiItem>>(emptyList())
    val modelList = _modelList.asStateFlow()

    init {
        // 임시로 데이터 설정
        _modelList.value = listOf(
            SelectModelUiItem("Model 1", true),
            SelectModelUiItem("Model 2", true),
            SelectModelUiItem("Model 3", true)
        )
    }

    fun updateModelList(position: Int, checkValue: Boolean) {
        _modelList.update { currentList ->
            currentList.toMutableList().apply {
                this[position].isSelected = checkValue
            }
        }
    }
}