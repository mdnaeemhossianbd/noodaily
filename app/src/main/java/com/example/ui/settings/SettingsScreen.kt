package com.example.ui.settings

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.admob.AdMobManager
import com.example.admob.BannerAdView
import com.example.data.local.PreferencesManager
import com.example.data.model.AppSettings
import com.example.data.repository.SettingsRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit
) {
    BackHandler { onNavigateBack() }

    val context = LocalContext.current
    val prefs = PreferencesManager(context)
    val settingsRepo = remember { SettingsRepository(context) }
    val scope = rememberCoroutineScope()

    val currentTheme by prefs.themeMode.collectAsState(initial = "system")
    val notificationsEnabled by prefs.dailyNotificationEnabled.collectAsState(initial = true)
    val appSettings by settingsRepo.observeSettings().collectAsState(initial = AppSettings())
    val selectedCityBn by prefs.selectedLocationCityBn.collectAsState(initial = "ঢাকা")
    val selectedCountryBn by prefs.selectedLocationCountryBn.collectAsState(initial = "বাংলাদেশ")
    val selectedTimezone by prefs.selectedLocationTimezone.collectAsState(initial = "Asia/Dhaka")
    val hijriOffset by prefs.hijriOffset.collectAsState(initial = 0)

    var showAboutDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showLocationDialog by remember { mutableStateOf(false) }
    var showMoonDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("সেটিংস ও তথ্য (Settings)") },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("settings_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            BannerAdView()
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // 1. Theme Setting
            Text(
                text = "অ্যাপ থিম ও মোড",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    ThemeOptionRow(
                        title = "সিস্টেম অনুযায়ী (System Default)",
                        isSelected = currentTheme == "system",
                        onSelect = { scope.launch { prefs.setThemeMode("system") } }
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    ThemeOptionRow(
                        title = "লাইট মোড (Light Mode)",
                        isSelected = currentTheme == "light",
                        onSelect = { scope.launch { prefs.setThemeMode("light") } }
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    ThemeOptionRow(
                        title = "ডার্ক মোড (Dark Mode)",
                        isSelected = currentTheme == "dark",
                        onSelect = { scope.launch { prefs.setThemeMode("dark") } }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            // 2. Location & Timezone Setting (World Countries, default Dhaka)
            Text(
                text = "স্থান ও সময় অঞ্চল (Location & Timezone)",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "বর্তমান নির্বাচিত স্থান:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$selectedCityBn, $selectedCountryBn",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "$selectedTimezone • ডিফল্ট: ঢাকা, বাংলাদেশ",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showLocationDialog = true },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Language, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("দেশ পরিবর্তন")
                        }
                        if (selectedTimezone != "Asia/Dhaka") {
                            Button(
                                onClick = { scope.launch { prefs.resetLocationToDhaka() } },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("ঢাকা রিসেট")
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            // 3. Notifications
            Text(
                text = "বিজ্ঞপ্তি ও অনুস্মারক",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "দৈনিক দুআ ও স্মারক বিজ্ঞপ্তি",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "প্রতিদিন সকালে এবং সন্ধ্যায় বরকতময় দুআর রিমাইন্ডার গ্রহণ করুন",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = { scope.launch { prefs.setDailyNotification(it) } },
                        modifier = Modifier.testTag("notification_switch")
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            // 3. Information & Support
            Text(
                text = "অ্যাপ তথ্য ও সহায়িকা",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    SettingNavRow(
                        icon = Icons.Default.Info,
                        title = "নূর ডেইলি সম্পর্কে (About Noor Daily)",
                        onClick = { showAboutDialog = true }
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    SettingNavRow(
                        icon = Icons.Default.Policy,
                        title = "গোপনীয়তা নীতিমালা (Privacy Policy)",
                        onClick = { showPrivacyDialog = true }
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    SettingNavRow(
                        icon = Icons.Default.Notifications,
                        title = "আযান সাউন্ড ও নোটিফিকেশন টেস্ট করুন",
                        onClick = {
                            com.example.util.PrayerNotificationManager.testAzanNotification(context)
                            Toast.makeText(context, "আযানের সুর ও নোটিফিকেশন বাজানো হচ্ছে...", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Noor Daily v1.0.0",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "বিশুদ্ধ কুরআন ও সুন্নাহর আলোকে পরিচালিত",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }

    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("নূর ডেইলি সম্পর্কে") },
            text = {
                Text(
                    "নূর ডেইলি (Noor Daily) একটি নির্ভরযোগ্য ও বিশুদ্ধ ইসলামিক শিক্ষা এবং প্রাত্যহিক আমল চর্চার আধুনিক প্ল্যাটফর্ম।\n\n" +
                    "এখানে প্রতিটি কুরআনিক আয়াত ও সহীহ হাদিসের রেফারেন্স উল্লেখপূর্বক নির্ভুলভাবে উপস্থাপন করা হয়। " +
                    "শিশুদের ইসলাম শিক্ষা, সহজ তাজবীদ নিয়ম, প্রাত্যহিক জীবনের দুআ ও আজকার সমৃদ্ধ এই প্ল্যাটফর্মটি " +
                    "প্রতিটি মুসলিমের দ্বীনি জ্ঞানার্জনে সহায়তাকল্পে তৈরি।"
                )
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("ঠিক আছে")
                }
            }
        )
    }

    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Policy,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("গোপনীয়তা নীতিমালা (Privacy Policy)")
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "নূর ডেইলি (Noor Daily) ব্যবহারকারীদের ব্যক্তিগত তথ্যের সুরক্ষাকে সর্বোচ্চ অগ্রাধিকার দেয়।",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                    Text(
                        text = "১. ব্যক্তিগত তথ্য সংগ্রহ:\n" +
                            "সাধারণ পাঠক বা শিক্ষার্থীদের কুরআন, হাদিস, দুআ ও আমল পড়ার জন্য কোনো অ্যাকাউন্ট রেজিস্ট্রেশন বা নাম, ফোন নম্বর ইত্যাদি প্রদানের প্রয়োজন নেই।\n\n" +
                            "২. অন-ডিভাইস ডেটা স্টোরেজ:\n" +
                            "আপনার বুকমার্ক, পড়ার অগ্রগতি, কুইজের স্কোর ও থিম সেটিংস সম্পূর্ণ আপনার ডিভাইসে সুরক্ষিতভাবে সংরক্ষিত থাকে।\n\n" +
                            "৩. বিজ্ঞাপন (Google AdMob):\n" +
                            "অ্যাপের পরিচালনা ব্যয় নির্বাহে গুগলের নীতিমালা মেনে শালীন বিজ্ঞাপন প্রদর্শিত হয়। অ্যাডমব সেবার অংশ হিসেবে গুগল তাদের প্রাইভেসি নীতি অনুযায়ী ডিভাইস আইডেন্টিফায়ার ব্যবহার করতে পারে।\n\n" +
                            "৪. শিশুদের সুরক্ষা (Families Policy):\n" +
                            "বাচ্চাদের দ্বীনি শিক্ষার জন্য আমাদের অ্যাপে প্রয়োজনীয় কনটেন্ট রয়েছে। আমরা ১৩ বছরের কম বয়সী শিশুদের কোনো ব্যক্তিগত তথ্য সংগ্রহ করি না।\n\n" +
                            "৫. ক্লাউড ডেটা নিরাপত্তা:\n" +
                            "ফায়ারবেস ক্লাউডের মাধ্যমে সহীহ টেক্সট ডেলিভারিতে সুরক্ষিত HTTPS এনক্রিপশন ব্যবহৃত হয়।\n\n" +
                            "৬. যোগাযোগ ও সহায়তা:\n" +
                            "যেকোনো অনুসন্ধান বা তথ্য মুছে ফেলার অনুরোধের জন্য ইমেইল করুন: habitsk50@gmail.com",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyDialog = false }) {
                    Text("বুঝেছি")
                }
            }
        )
    }

    if (showLocationDialog) {
        val currentLoc = remember(selectedCityBn, selectedTimezone) {
            com.example.util.IslamicTimeManager.POPULAR_LOCATIONS.find {
                it.timezoneId == selectedTimezone && it.cityBn == selectedCityBn
            } ?: com.example.util.IslamicTimeManager.DEFAULT_LOCATION.copy(
                cityBn = selectedCityBn,
                countryBn = selectedCountryBn,
                timezoneId = selectedTimezone
            )
        }
        com.example.ui.home.WorldLocationPickerDialog(
            currentLocation = currentLoc,
            onDismiss = { showLocationDialog = false },
            onSelectLocation = { loc ->
                scope.launch {
                    prefs.setLocation(loc.cityBn, loc.countryBn, loc.timezoneId, loc.id)
                }
                showLocationDialog = false
            },
            onResetToDhaka = {
                scope.launch {
                    prefs.resetLocationToDhaka()
                }
                showLocationDialog = false
            }
        )
    }
}

@Composable
private fun ThemeOptionRow(
    title: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, style = MaterialTheme.typography.bodyMedium)
        if (isSelected) {
            Icon(
                Icons.Default.Check,
                contentDescription = "Selected",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun SettingNavRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}
