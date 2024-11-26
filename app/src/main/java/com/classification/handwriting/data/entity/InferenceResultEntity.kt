package com.classification.handwriting.data.entity

data class InferenceResultEntity(
    val predictGender: Int,
    val predictAge: Int,
    val inferenceTime: Long
)