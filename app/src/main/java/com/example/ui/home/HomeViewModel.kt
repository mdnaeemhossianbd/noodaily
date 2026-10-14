package com.example.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.admob.AdMobManager
import com.example.data.model.AppSettings
import com.example.data.model.Category
import com.example.data.model.Post
import com.example.data.model.Subcategory
import com.example.data.repository.ContentRepository
import com.example.data.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val categories: List<Category> = emptyList(),
    val subcategoriesByCategoryId: Map<String, List<Subcategory>> = emptyMap(),
    val featuredPosts: List<Post> = emptyList(),
    val latestPosts: List<Post> = emptyList(),
    val settings: AppSettings = AppSettings(),
    val currentLocation: com.example.util.WorldLocation = com.example.util.IslamicTimeManager.DEFAULT_LOCATION,
    val hijriOffset: Int = 0,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val contentRepository = ContentRepository(application)
    private val settingsRepository = SettingsRepository(application)
    private val preferencesManager = com.example.data.local.PreferencesManager(application)

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
        observeLocationPreferences()
    }

    private fun observeLocationPreferences() {
        viewModelScope.launch {
            preferencesManager.selectedLocationTimezone.collect { tz ->
                val cityBn = preferencesManager.selectedLocationCityBn
                val countryBn = preferencesManager.selectedLocationCountryBn
                launch {
                    cityBn.collect { city ->
                        val currentLoc = _uiState.value.currentLocation
                        val found = com.example.util.IslamicTimeManager.POPULAR_LOCATIONS.find {
                            it.timezoneId == tz && it.cityBn == city
                        } ?: currentLoc.copy(timezoneId = tz, cityBn = city)
                        _uiState.value = _uiState.value.copy(currentLocation = found)
                    }
                }
            }
        }
        viewModelScope.launch {
            preferencesManager.hijriOffset.collect { offset ->
                _uiState.value = _uiState.value.copy(hijriOffset = offset)
            }
        }
    }

    fun updateLocation(location: com.example.util.WorldLocation) {
        viewModelScope.launch {
            preferencesManager.setLocation(
                cityBn = location.cityBn,
                countryBn = location.countryBn,
                timezoneId = location.timezoneId,
                locationId = location.id
            )
            _uiState.value = _uiState.value.copy(currentLocation = location)
        }
    }

    fun resetLocationToDhaka() {
        viewModelScope.launch {
            preferencesManager.resetLocationToDhaka()
            _uiState.value = _uiState.value.copy(currentLocation = com.example.util.IslamicTimeManager.DEFAULT_LOCATION)
        }
    }

    fun updateHijriOffset(offset: Int) {
        viewModelScope.launch {
            preferencesManager.setHijriOffset(offset)
            _uiState.value = _uiState.value.copy(hijriOffset = offset)
        }
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                // Collect categories & their subcategories
                launch {
                    contentRepository.observeCategories().collect { categories ->
                        _uiState.value = _uiState.value.copy(categories = categories)
                        // Also load subcategories for each category
                        categories.forEach { cat ->
                            launch {
                                contentRepository.observeSubcategories(cat.id).collect { subs ->
                                    val currentMap = _uiState.value.subcategoriesByCategoryId.toMutableMap()
                                    currentMap[cat.id] = subs
                                    _uiState.value = _uiState.value.copy(subcategoriesByCategoryId = currentMap)
                                }
                            }
                        }
                    }
                }
                // Collect featured posts
                launch {
                    contentRepository.observeFeaturedPosts().collect { featured ->
                        _uiState.value = _uiState.value.copy(featuredPosts = featured)
                    }
                }
                // Collect latest posts
                launch {
                    contentRepository.observeLatestPosts(20).collect { latest ->
                        _uiState.value = _uiState.value.copy(
                            latestPosts = latest,
                            isLoading = false
                        )
                    }
                }
                // Collect remote settings
                launch {
                    settingsRepository.observeSettings().collect { settings ->
                        _uiState.value = _uiState.value.copy(settings = settings)
                        AdMobManager.updateSettings(settings, getApplication())
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.localizedMessage ?: "Failed to load content"
                )
            }
        }
    }
}
