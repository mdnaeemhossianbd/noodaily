package com.example.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "noor_daily_prefs")

class PreferencesManager(private val context: Context) {

    companion object {
        val KEY_THEME_MODE = stringPreferencesKey("theme_mode") // "system", "light", "dark"
        val KEY_LANGUAGE = stringPreferencesKey("language") // "bn", "en"
        val KEY_ARABIC_FONT_SIZE = floatPreferencesKey("arabic_font_size") // default 24f
        val KEY_BENGALI_FONT_SIZE = floatPreferencesKey("bengali_font_size") // default 16f
        val KEY_DAILY_NOTIFICATION = booleanPreferencesKey("daily_notification")
        val KEY_LOCATION_ID = stringPreferencesKey("location_id")
        val KEY_LOCATION_CITY_BN = stringPreferencesKey("location_city_bn")
        val KEY_LOCATION_COUNTRY_BN = stringPreferencesKey("location_country_bn")
        val KEY_LOCATION_TIMEZONE = stringPreferencesKey("location_timezone")
        val KEY_HIJRI_OFFSET = androidx.datastore.preferences.core.intPreferencesKey("hijri_offset")
    }

    val themeMode: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_THEME_MODE] ?: "system"
    }

    val language: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_LANGUAGE] ?: "bn"
    }

    val arabicFontSize: Flow<Float> = context.dataStore.data.map { prefs ->
        prefs[KEY_ARABIC_FONT_SIZE] ?: 24f
    }

    val bengaliFontSize: Flow<Float> = context.dataStore.data.map { prefs ->
        prefs[KEY_BENGALI_FONT_SIZE] ?: 16f
    }

    val dailyNotificationEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_DAILY_NOTIFICATION] ?: true
    }

    val selectedLocationTimezone: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_LOCATION_TIMEZONE] ?: "Asia/Dhaka"
    }

    val selectedLocationCityBn: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_LOCATION_CITY_BN] ?: "ঢাকা"
    }

    val selectedLocationCountryBn: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_LOCATION_COUNTRY_BN] ?: "বাংলাদেশ"
    }

    val hijriOffset: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[KEY_HIJRI_OFFSET] ?: 0
    }

    suspend fun setLocation(cityBn: String, countryBn: String, timezoneId: String, locationId: String = "") {
        context.dataStore.edit { prefs ->
            prefs[KEY_LOCATION_CITY_BN] = cityBn
            prefs[KEY_LOCATION_COUNTRY_BN] = countryBn
            prefs[KEY_LOCATION_TIMEZONE] = timezoneId
            if (locationId.isNotEmpty()) prefs[KEY_LOCATION_ID] = locationId
        }
    }

    suspend fun setHijriOffset(offset: Int) {
        context.dataStore.edit { prefs ->
            prefs[KEY_HIJRI_OFFSET] = offset
        }
    }

    suspend fun resetLocationToDhaka() {
        setLocation(
            cityBn = "ঢাকা",
            countryBn = "বাংলাদেশ",
            timezoneId = "Asia/Dhaka",
            locationId = "bd_dhaka"
        )
    }

    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { it[KEY_THEME_MODE] = mode }
    }

    suspend fun setLanguage(lang: String) {
        context.dataStore.edit { it[KEY_LANGUAGE] = lang }
    }

    suspend fun setArabicFontSize(size: Float) {
        context.dataStore.edit { it[KEY_ARABIC_FONT_SIZE] = size }
    }

    suspend fun setBengaliFontSize(size: Float) {
        context.dataStore.edit { it[KEY_BENGALI_FONT_SIZE] = size }
    }

    suspend fun setDailyNotification(enabled: Boolean) {
        context.dataStore.edit { it[KEY_DAILY_NOTIFICATION] = enabled }
    }
}
