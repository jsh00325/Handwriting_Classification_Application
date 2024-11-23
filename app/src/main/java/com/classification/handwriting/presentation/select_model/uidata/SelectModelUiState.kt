package com.classification.handwriting.presentation.select_model.uidata

import com.classification.handwriting.domain.model.ModelItem

sealed class SelectModelUiState {
    data object Idle : SelectModelUiState()
    data object SelectFail : SelectModelUiState()
    data class SelectSuccess(val modelList: List<ModelItem>) : SelectModelUiState()
}