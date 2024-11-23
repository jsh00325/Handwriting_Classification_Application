package com.classification.handwriting.presentation.model_result.uidata

data class ModelResultUiItem(
    val modelName: String,
    val predictGender: String,
    val predictAge: String,
    val inferenceTime: String
)