package com.example.ui.reader

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.BookmarkEntity
import com.example.data.local.HistoryEntity
import com.example.data.local.PreferencesManager
import com.example.data.model.Post
import com.example.data.repository.ContentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class PostDetailUiState(
    val post: Post? = null,
    val isBookmarked: Boolean = false,
    val arabicFontSize: Float = 24f,
    val bengaliFontSize: Float = 16f,
    val isLoading: Boolean = true
)

class PostDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val contentRepository = ContentRepository(application)
    private val localDb = AppDatabase.getInstance(application)
    private val preferencesManager = PreferencesManager(application)

    private val _uiState = MutableStateFlow(PostDetailUiState())
    val uiState: StateFlow<PostDetailUiState> = _uiState.asStateFlow()

    fun loadPost(postId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            // Load user font preferences
            val arabicSize = preferencesManager.arabicFontSize.first()
            val bengaliSize = preferencesManager.bengaliFontSize.first()
            _uiState.value = _uiState.value.copy(
                arabicFontSize = arabicSize,
                bengaliFontSize = bengaliSize
            )

            // Observe bookmark status
            launch {
                localDb.bookmarkDao().isBookmarked(postId).collect { isBookmarked ->
                    _uiState.value = _uiState.value.copy(isBookmarked = isBookmarked)
                }
            }

            // Observe post content
            contentRepository.observePostById(postId).collect { post ->
                _uiState.value = _uiState.value.copy(post = post, isLoading = false)

                // Record reading history locally
                post?.let {
                    localDb.historyDao().recordHistory(
                        HistoryEntity(
                            postId = it.id,
                            title = it.title,
                            titleBn = it.titleBn,
                            categoryName = it.sourceReference
                        )
                    )
                }
            }
        }
    }

    fun toggleBookmark() {
        val currentPost = _uiState.value.post ?: return
        viewModelScope.launch {
            if (_uiState.value.isBookmarked) {
                localDb.bookmarkDao().deleteBookmark(currentPost.id)
            } else {
                localDb.bookmarkDao().insertBookmark(
                    BookmarkEntity(
                        postId = currentPost.id,
                        title = currentPost.title,
                        titleBn = currentPost.titleBn,
                        arabicSnippet = currentPost.arabicText.take(60),
                        categoryName = currentPost.sourceReference
                    )
                )
            }
        }
    }

    fun updateArabicFontSize(newSize: Float) {
        _uiState.value = _uiState.value.copy(arabicFontSize = newSize)
        viewModelScope.launch {
            preferencesManager.setArabicFontSize(newSize)
        }
    }
}
