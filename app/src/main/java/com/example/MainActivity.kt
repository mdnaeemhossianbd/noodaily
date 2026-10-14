package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.example.admob.AdMobManager
import com.example.data.local.PreferencesManager
import com.example.ui.navigation.AppNavigation
import com.example.ui.theme.NoorDailyTheme

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.lifecycleScope
import com.example.ui.navigation.Screen
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {

    private val requestNotificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                com.example.util.PrayerNotificationManager.createNotificationChannel(applicationContext)
                com.example.util.PrayerNotificationManager.scheduleAllPrayerAlarms(applicationContext)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Pre-create WebView cache directories to prevent Chromium simple_file_enumerator warnings
        try {
            val webViewCacheDir = java.io.File(cacheDir, "WebView/Default/HTTP Cache/Code Cache/wasm")
            if (!webViewCacheDir.exists()) {
                webViewCacheDir.mkdirs()
            }
        } catch (_: Exception) {}

        // Check and request notification permission for prayer Azan on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        // Initialize Google AdMob SDK
        AdMobManager.initialize(applicationContext)

        // Initialize Prayer Waqt Azan Notification Channel & Schedule Alarms
        com.example.util.PrayerNotificationManager.createNotificationChannel(applicationContext)
        com.example.util.PrayerNotificationManager.scheduleAllPrayerAlarms(applicationContext)

        setContent {
            val prefs = remember { PreferencesManager(applicationContext) }
            val themeMode by prefs.themeMode.collectAsState(initial = "system")

            val isDark = when (themeMode) {
                "dark" -> true
                "light" -> false
                else -> isSystemInDarkTheme()
            }

            NoorDailyTheme(darkTheme = isDark) {
                AppNavigation()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Gracefully attempt policy-compliant App Open ad on session resumption
        AdMobManager.showAppOpenAdIfAvailable(this)
    }
}
