package com.example.quiztap.ui.game.category

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quiztap.data.ResponseState
import com.example.quiztap.network.model.response.CategoriesListResponse
import com.example.quiztap.network.model.response.CategoryModel
import com.example.quiztap.network.utils.OperationsStateHandler
import com.example.quiztap.repository.QuizTapRepository
import com.example.quiztap.utils.CategoryIconMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CategoryListViewModel @Inject constructor(
    private val repository: QuizTapRepository
): ViewModel() {

    private val categoryListApiCall = OperationsStateHandler<CategoriesListResponse>(viewModelScope) {
        if (it.isSuccess()) {
            val categories = it.getSuccessResponse()?.categories?: emptyList()
            val iconMapper = CategoryIconMapper()
            categories.forEach { cat ->
                cat.icon = iconMapper.getIcon(cat.id)
            }
            allCategories.clear()
            allCategories.addAll(categories)
        }
        _categoryListLoadState.postValue(it)
    }
    private val _categoryListLoadState = MutableLiveData<ResponseState<CategoriesListResponse>>(ResponseState.Idle)
    val categoryListLoadState = _categoryListLoadState

    private var allCategories = ArrayList<CategoryModel>()

    private val _categoryList = MutableLiveData<List<CategoryModel>>()
    val categoryList = _categoryList

    init {
        loadCategoriesData()
    }

    fun loadCategories(searchKey: String? = null) {
        val result = if (!searchKey.isNullOrEmpty()) {
            allCategories.filter { it.name.contains(searchKey, ignoreCase = true) }
        } else {
            allCategories
        }
        _categoryList.value = result
    }

    private fun loadCategoriesData() {
        categoryListApiCall.load {
            repository.getAllCategories()
        }
    }



}