package com.classification.handwriting.presentation.select_model

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.classification.handwriting.databinding.ActivitySelectModelBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SelectModelActivity : AppCompatActivity() {
    private val viewModel: SelectModelViewModel by viewModels()
    private lateinit var binding: ActivitySelectModelBinding
    private lateinit var adapter: SelectModelAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectModelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        adapter = SelectModelAdapter(viewModel::updateModelList)

        binding.selectModelRecyclerView.apply {
            adapter = this@SelectModelActivity.adapter
            layoutManager = LinearLayoutManager(this@SelectModelActivity)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.modelList.collect {
                    adapter.submitList(it)
                }
            }
        }
    }
}