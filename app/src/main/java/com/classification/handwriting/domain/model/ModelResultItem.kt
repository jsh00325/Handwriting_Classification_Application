package com.classification.handwriting.domain.model

data class ModelResultItem(
    val modelName: String,
    val predictGender: String,
    val predictAge: String,
    val inferenceTime: Long
)