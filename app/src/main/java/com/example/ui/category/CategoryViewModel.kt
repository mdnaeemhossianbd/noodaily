package com.example.ui.category

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Category
import com.example.data.model.Post
import com.example.data.model.Subcategory
import com.example.data.repository.ContentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class CategoryUiState(
    val category: Category? = null,
    val subcategories: List<Subcategory> = emptyList(),
    val selectedSubcategoryId: String? = null,
    val posts: List<Post> = emptyList(),
    val isLoading: Boolean = true
)

class CategoryViewModel(application: Application) : AndroidViewModel(application) {
    private val contentRepository = ContentRepository(application)

    private val _uiState = MutableStateFlow(CategoryUiState())
    val uiState: StateFlow<CategoryUiState> = _uiState.asStateFlow()

    fun loadCategory(categoryId: String, initialSubcategoryId: String? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                selectedSubcategoryId = initialSubcategoryId
            )

            // Find category info
            val allCats = contentRepository.observeCategories().first()
            val cat = allCats.firstOrNull { it.id == categoryId }

            // Observe subcategories
            launch {
                contentRepository.observeSubcategories(categoryId).collect { subcategories ->
                    _uiState.value = _uiState.value.copy(subcategories = subcategories)
                }
            }

            // Observe posts
            launch {
                contentRepository.observePostsByCategory(categoryId).collect { posts ->
                    _uiState.value = _uiState.value.copy(
                        category = cat,
                        posts = posts,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun selectSubcategory(subcategoryId: String?) {
        _uiState.value = _uiState.value.copy(selectedSubcategoryId = subcategoryId)
    }
}
