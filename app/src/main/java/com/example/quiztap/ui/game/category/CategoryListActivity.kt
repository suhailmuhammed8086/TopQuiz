package com.example.quiztap.ui.game.category

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quiztap.R
import com.example.quiztap.adapter.CategoryListAdapter
import com.example.quiztap.base.QuizTapBaseActivity
import com.example.quiztap.data.ResponseState
import com.example.quiztap.databinding.ActivityCategoryListBinding
import com.example.quiztap.network.model.response.CategoryModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CategoryListActivity : QuizTapBaseActivity() {
    private lateinit var binding: ActivityCategoryListBinding
    private val viewModel: CategoryListViewModel by viewModels()
    private lateinit var categoryListAdapter: CategoryListAdapter
    private var categories = ArrayList<CategoryModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCategoryListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initView()
        observerViewModel()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observerViewModel() {
        with(viewModel) {
            categoryListLoadState.observe(this@CategoryListActivity) {
                when(it) {
                    ResponseState.Cancelled -> {
                        binding.progress.hide()
                    }
                    is ResponseState.Failed -> {
                        binding.progress.hide()
                    }
                    ResponseState.Idle -> {}
                    ResponseState.Loading -> {
                        binding.progress.show()
                    }
                    is ResponseState.Success -> {
                        binding.progress.hide()
                        viewModel.loadCategories()

                    }
                    is ResponseState.ValidationError -> {}
                }
            }

            categoryList.observe(this@CategoryListActivity) {
                categories.clear()
                categories.addAll(it)
                categoryListAdapter.notifyDataSetChanged()
                if (categories.isEmpty()) {

                } else {

                }
            }
        }
    }

    private fun initView() {
        categoryListAdapter = CategoryListAdapter(categories, ::onCategoryClick)
        binding.rvCategories.apply {
            adapter = categoryListAdapter
            layoutManager = GridLayoutManager(this@CategoryListActivity,2)
        }

        binding.etSearch.doAfterTextChanged {
            val text = it?.toString()
            if (text.isNullOrEmpty()) {
                viewModel.loadCategories()
            } else {
                viewModel.loadCategories(text)
            }
        }
    }

    private fun onCategoryClick(categoryModel: CategoryModel) {
        val result = Intent()
        result.putExtra(ARG_CATEGORY_ID, categoryModel.id)
        result.putExtra(ARG_CATEGORY_NAME, categoryModel.name)
        result.putExtra(ARG_CATEGORY_ICON, categoryModel.icon)
        setResult(RESULT_OK, result)
        finish()
    }

    companion object {
        private const val ARGS_PREVIOUS_SELECTED_CATEGORY_ID = "PREVIOUS_SELECTED_CATEGORY_ID"
        const val ARG_CATEGORY_ID = "ARG_CATEGORY_ID"
        const val ARG_CATEGORY_NAME = "ARG_CATEGORY_NAME"
        const val ARG_CATEGORY_ICON= "ARG_CATEGORY_ICON"

        @JvmStatic
        fun startForSelection(
            context: Context,
            categoryId: Int = -1,
            launcher: ActivityResultLauncher<Intent>
        ) {
            val starter = Intent(context, CategoryListActivity::class.java)
            starter.putExtra(ARGS_PREVIOUS_SELECTED_CATEGORY_ID, categoryId)
            launcher.launch(starter)
        }
    }


}