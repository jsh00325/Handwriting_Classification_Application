package com.classification.handwriting.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ModelItem(
    val modelName: String,
    val modelPath: String
) : Parcelable