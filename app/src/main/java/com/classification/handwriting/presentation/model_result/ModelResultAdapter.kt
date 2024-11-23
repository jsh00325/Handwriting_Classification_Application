package com.classification.handwriting.presentation.model_result

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.classification.handwriting.databinding.ItemModelResultBinding
import com.classification.handwriting.presentation.model_result.uidata.ModelResultUiItem

class ModelResultAdapter :
    ListAdapter<ModelResultUiItem, ModelResultAdapter.ResultViewHolder>(diffUtil) {

    inner class ResultViewHolder(private val binding: ItemModelResultBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ModelResultUiItem) {
            binding.modelResult = item
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ResultViewHolder(
        ItemModelResultBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ModelResultUiItem>() {
            override fun areItemsTheSame(
                oldItem: ModelResultUiItem,
                newItem: ModelResultUiItem,
            ): Boolean = oldItem === newItem

            override fun areContentsTheSame(
                oldItem: ModelResultUiItem,
                newItem: ModelResultUiItem,
            ): Boolean = oldItem == newItem
        }
    }
}