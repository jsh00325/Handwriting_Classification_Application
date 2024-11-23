package com.classification.handwriting.presentation.select_model

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.classification.handwriting.R
import com.classification.handwriting.databinding.ActivitySelectModelBinding
import com.classification.handwriting.presentation.select_model.data.SelectModelUiState
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
        setupSelectButton()
        collectSelectModelState()
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

    private fun setupSelectButton() {
        binding.selectModelButton.setOnClickListener {
            viewModel.selectModel()
        }
    }

    private fun collectSelectModelState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.selectModelState.collect { state ->
                    when (state) {
                        is SelectModelUiState.Idle -> {}

                        is SelectModelUiState.SelectFail -> {
                            viewModel.resetSelectModelState()
                            Toast.makeText(
                                this@SelectModelActivity,
                                R.string.select_model_no_model_selected,
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        is SelectModelUiState.SelectSuccess -> {
                            val modelList = state.modelList
                            viewModel.resetSelectModelState()

                            // TODO: 카메라 화면으로 이동하기
                        }
                    }
                }
            }
        }
    }
}