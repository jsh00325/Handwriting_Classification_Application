package com.classification.handwriting.presentation.select_model

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.classification.handwriting.databinding.ItemSelectModelBinding
import com.classification.handwriting.presentation.select_model.data.SelectModelUiItem

class SelectModelAdapter(
    private val onCheckedChange: (Int, Boolean) -> Unit
) : ListAdapter<SelectModelUiItem, SelectModelAdapter.ModelViewHolder>(diffUtil) {

    inner class ModelViewHolder(private val binding: ItemSelectModelBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.itemSelectModelCheckBox.setOnCheckedChangeListener { _, isChecked ->
                onCheckedChange(bindingAdapterPosition, isChecked)
            }
        }

        fun bind(item: SelectModelUiItem) {
            binding.item = item
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModelViewHolder =
        ModelViewHolder(
            ItemSelectModelBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ModelViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<SelectModelUiItem>() {
            override fun areItemsTheSame(
                oldItem: SelectModelUiItem,
                newItem: SelectModelUiItem,
            ): Boolean = oldItem.modelName === newItem.modelName

            override fun areContentsTheSame(
                oldItem: SelectModelUiItem,
                newItem: SelectModelUiItem,
            ): Boolean = oldItem.modelName == newItem.modelName
        }
    }
}