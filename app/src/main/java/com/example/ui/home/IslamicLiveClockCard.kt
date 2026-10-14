package com.example.ui.home

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.ViewCarousel
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ArabicFontFamily
import com.example.ui.theme.BengaliFontFamily
import com.example.ui.theme.GoldAccent
import com.example.util.ForbiddenTimeItem
import com.example.util.IslamicTimeManager
import com.example.util.LocationPrayerTimes
import com.example.util.PrayerTimeItem
import com.example.util.WorldLocation
import kotlinx.coroutines.delay

@Composable
fun IslamicLiveClockCard(
    currentLocation: WorldLocation,
    hijriOffset: Int,
    onSelectLocation: (WorldLocation) -> Unit,
    onResetToDhaka: () -> Unit,
    onUpdateHijriOffset: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Live ticking every 1 second
    var tick by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000L)
            tick = (tick + 1) % 60
        }
    }

    val liveTime = remember(currentLocation.timezoneId, tick) {
        IslamicTimeManager.getLiveTime(currentLocation.timezoneId)
    }

    val englishDate = remember(currentLocation.timezoneId, tick) {
        IslamicTimeManager.getEnglishDate(currentLocation.timezoneId)
    }

    val hijriDate = remember(currentLocation.timezoneId, hijriOffset, tick) {
        IslamicTimeManager.getHijriDate(currentLocation.timezoneId, hijriOffset)
    }

    val prayerTimes: LocationPrayerTimes = remember(currentLocation.lat, currentLocation.lng, currentLocation.timezoneId, tick) {
        IslamicTimeManager.calculatePrayerTimes(currentLocation)
    }

    // Modal dialog states
    var showLocationDialog by remember { mutableStateOf(false) }
    var showHijriAdjustmentDialog by remember { mutableStateOf(false) }
    var showFullPrayerScheduleDialog by remember { mutableStateOf(false) }
    var showFastingDialog by remember { mutableStateOf(false) }
    var selectedPrayerForDetail by remember { mutableStateOf<PrayerTimeItem?>(null) }

    // Today's completed prayers tracker (prayer id -> completed)
    val completedPrayers = remember { mutableStateMapOf<String, Boolean>() }

    // Toggle between auto-centering Horizontal Carousel and Complete 2x3 Grid View
    var isGridView by remember { mutableStateOf(false) }

    // Auto-scroll calculation: Automatically centers the active or next prayer card in the middle!
    val activePrayerIndex = remember(prayerTimes.allPrayers) {
        val curIdx = prayerTimes.allPrayers.indexOfFirst { it.isCurrent }
        if (curIdx != -1) curIdx else {
            val nextIdx = prayerTimes.allPrayers.indexOfFirst { it.isNext }
            if (nextIdx != -1) nextIdx else 0
        }
    }
    val prayerRowListState = rememberLazyListState()
    LaunchedEffect(activePrayerIndex) {
        if (activePrayerIndex in prayerTimes.allPrayers.indices) {
            prayerRowListState.animateScrollToItem(
                index = activePrayerIndex,
                scrollOffset = 0
            )
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("islamic_live_clock_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF06231C)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0D3B31),
                            Color(0xFF06231C)
                        )
                    )
                )
                .padding(14.dp)
        ) {
            // ==============================================================
            // 1. COMPACT, SPACE-EFFICIENT TIME & DATE HEADER
            // (Reduced vertical height as requested)
            // ==============================================================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Location Selector Chip
                Surface(
                    onClick = { showLocationDialog = true },
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF164E41),
                    modifier = Modifier.testTag("location_picker_chip")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = currentLocation.flagEmoji,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${currentLocation.cityBn}, ${currentLocation.countryBn}",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Change Location",
                                    tint = GoldAccent,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Text(
                                text = "${currentLocation.cityEn} • ${liveTime.timezoneOffsetString}",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = Color(0xFFA7F3D0)
                            )
                        }
                    }
                }

                // Compact Live Digital Clock with live pulsing green indicator
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (currentLocation.id != IslamicTimeManager.DEFAULT_LOCATION.id) {
                        TextButton(
                            onClick = onResetToDhaka,
                            colors = ButtonDefaults.textButtonColors(contentColor = GoldAccent),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.RestartAlt,
                                contentDescription = null,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "ঢাকা",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0x33000000),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x2634D399))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF10B981))
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = liveTime.time12Bn,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontFamily = BengaliFontFamily,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = Color.White
                                )
                                Text(
                                    text = liveTime.time12En,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = GoldAccent
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Compact Combined Date Strip (English & Hijri Date with Moon Adjustment button)
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0x2E000000),
                border = androidx.compose.foundation.BorderStroke(0.5.dp, Color(0x26FFFFFF)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 9.dp, vertical = 5.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // English Date
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${englishDate.formattedFullBn} (${englishDate.dayNameEn.take(3)})",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold, fontSize = 11.sp),
                            color = Color(0xFFE2E8F0),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Arabic / Hijri Date + Moon button
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.NightsStay,
                            contentDescription = null,
                            tint = GoldAccent,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = hijriDate.formattedFullBn,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp),
                            color = GoldAccent
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "(${hijriDate.formattedFullAr})",
                            fontFamily = ArabicFontFamily,
                            fontSize = 12.sp,
                            color = Color(0xFFD1FAE5)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color(0x26FFFFFF), thickness = 1.dp)
            Spacer(modifier = Modifier.height(10.dp))

            // ==============================================================
            // 2. PROMINENT, INTERACTIVE NAMAZ (PRAYER TIMES) SECTION
            // (Fully clickable, responsive with full schedule & details)
            // ==============================================================
            // Header with Mosque icon, Schedule link, and View Mode Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { showFullPrayerScheduleDialog = true }
                        .padding(vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Mosque,
                        contentDescription = null,
                        tint = GoldAccent,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "নামাজের সময়সূচি",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Full schedule button
                    Surface(
                        onClick = { showFullPrayerScheduleDialog = true },
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0x33FFFFFF)
                    ) {
                        Text(
                            text = "সকল ওয়াক্ত ℹ️",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.SemiBold),
                            color = Color(0xFFD1FAE5),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }

                    // Grid / Row View Toggle
                    IconButton(
                        onClick = { isGridView = !isGridView },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = if (isGridView) Icons.Default.ViewCarousel else Icons.Default.GridView,
                            contentDescription = "ভিউ পরিবর্তন করুন",
                            tint = Color(0xFFA7F3D0),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Dedicated Next Prayer Waqt Banner (Fully responsive, never cut off, clear countdown)
            Surface(
                onClick = { showFullPrayerScheduleDialog = true },
                shape = RoundedCornerShape(10.dp),
                color = Color(0x33B45309),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x80F59E0B)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("next_prayer_banner")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 7.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(Color(0xFFFBBF24), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "পরবর্তী নামাজ: ${prayerTimes.nextPrayerNameBn}",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFFFDE68A),
                            maxLines = 1
                        )
                        if (prayerTimes.nextPrayerTimeFormatted.isNotBlank()) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "(${prayerTimes.nextPrayerTimeFormatted})",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                color = Color(0xFFFEF3C7),
                                maxLines = 1
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0x5978350F)
                    ) {
                        Text(
                            text = prayerTimes.nextPrayerRemainingCompact.ifBlank { prayerTimes.nextPrayerRemaining },
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                            color = Color(0xFFFDE68A),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.5.dp),
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Prayer Card Reusable Composable Lambda for 100% Uniform Size
            val renderPrayerCard: @Composable (PrayerTimeItem, Modifier) -> Unit = { prayer, cardModifier ->
                val isCurrent = prayer.isCurrent
                val isNext = prayer.isNext
                val isPrayed = completedPrayers[prayer.id] == true

                Surface(
                    onClick = { selectedPrayerForDetail = prayer },
                    shape = RoundedCornerShape(12.dp),
                    color = when {
                        isCurrent -> Color(0xFF065F46)
                        isNext -> Color(0xFF78350F)
                        isPrayed -> Color(0xFF064E3B)
                        else -> Color(0x33000000)
                    },
                    border = androidx.compose.foundation.BorderStroke(
                        width = if (isCurrent || isNext) 2.dp else 0.5.dp,
                        color = when {
                            isCurrent -> Color(0xFF34D399)
                            isNext -> Color(0xFFFBBF24)
                            isPrayed -> Color(0xFF10B981)
                            else -> Color(0x26FFFFFF)
                        }
                    ),
                    modifier = cardModifier.height(134.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 8.dp, horizontal = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        val iconVector = when (prayer.id) {
                            "fajr" -> Icons.Default.NightsStay
                            "sunrise" -> Icons.Default.WbSunny
                            "dhuhr" -> Icons.Default.WbSunny
                            "asr" -> Icons.Default.WbTwilight
                            "maghrib" -> Icons.Default.WbTwilight
                            else -> Icons.Default.NightsStay
                        }

                        Icon(
                            imageVector = iconVector,
                            contentDescription = null,
                            tint = if (isCurrent || isNext) GoldAccent else Color(0xFFA7F3D0),
                            modifier = Modifier.size(18.dp)
                        )

                        Text(
                            text = prayer.nameBn,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            ),
                            color = if (isCurrent || isNext) Color.White else Color(0xFFE2E8F0),
                            maxLines = 1
                        )

                        Text(
                            text = prayer.nameAr,
                            fontFamily = ArabicFontFamily,
                            fontSize = 11.sp,
                            color = if (isCurrent || isNext) GoldAccent else Color(0xFF94A3B8),
                            maxLines = 1
                        )

                        Text(
                            text = prayer.timeFormatted,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            ),
                            color = if (isCurrent || isNext) GoldAccent else Color.White,
                            maxLines = 1
                        )

                        // Status pill (চলমান / পরবর্তী / আদায়কৃত / বিবরণ)
                        when {
                            isPrayed -> {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFF047857)
                                ) {
                                    Text(
                                        text = "✓ আদায়কৃত",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                                        color = Color(0xFFD1FAE5),
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                        maxLines = 1
                                    )
                                }
                            }
                            isCurrent -> {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFF059669)
                                ) {
                                    Text(
                                        text = "● চলমান",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                                        color = Color(0xFFECFDF5),
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                        maxLines = 1
                                    )
                                }
                            }
                            isNext -> {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFFD97706)
                                ) {
                                    Text(
                                        text = "★ পরবর্তী",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                                        color = Color(0xFFFFFBEB),
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                        maxLines = 1
                                    )
                                }
                            }
                            else -> {
                                Text(
                                    text = "বিবরণ ℹ️",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                    color = Color(0xFF94A3B8),
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }

            // 6 Waqt Prayer Timing Cards (Auto-centering active prayer in Middle or full 2x3 Grid View)
            if (isGridView) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        prayerTimes.allPrayers.take(3).forEach { prayer ->
                            renderPrayerCard(prayer, Modifier.weight(1f))
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        prayerTimes.allPrayers.drop(3).take(3).forEach { prayer ->
                            renderPrayerCard(prayer, Modifier.weight(1f))
                        }
                    }
                }
            } else {
                androidx.compose.foundation.layout.BoxWithConstraints(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val cardWidth = 114.dp
                    val horizontalPadding = maxOf(8.dp, (maxWidth - cardWidth) / 2)
                    LazyRow(
                        state = prayerRowListState,
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = horizontalPadding)
                    ) {
                        items(prayerTimes.allPrayers) { prayer ->
                            renderPrayerCard(prayer, Modifier.width(cardWidth))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Azan Audio Notification Test Bar (Authentic vocal Azan, original audio)
            Surface(
                onClick = {
                    com.example.util.PrayerNotificationManager.testAzanNotification(context)
                    Toast.makeText(context, "মধুর আযানের সুর বাজানো হচ্ছে ও নোটিফিকেশন চেক করা হচ্ছে...", Toast.LENGTH_SHORT).show()
                },
                shape = RoundedCornerShape(8.dp),
                color = Color(0x2E10B981),
                border = androidx.compose.foundation.BorderStroke(0.5.dp, Color(0x6634D399)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Azan Tone",
                        tint = GoldAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "আযানের সুর ও ওয়াক্ত নোটিফিকেশন টেস্ট করুন (অরিজিনাল আযান)",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFFD1FAE5)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Sahri & Iftar Quick Bar (CLICKABLE - Opens Fasting times and Duas)
            Surface(
                onClick = { showFastingDialog = true },
                shape = RoundedCornerShape(8.dp),
                color = Color(0x26000000),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("sahri_iftar_bar")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🌙 সাহরি শেষ: ${prayerTimes.fajr} (ফজর)",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFA7F3D0)
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "☀️ ইফতার: ${prayerTimes.maghrib}",
                            style = MaterialTheme.typography.labelSmall,
                            color = GoldAccent,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "• দুআ 📖",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = Color(0xFFFDE68A)
                        )
                    }
                }
            }

            // Quick adjustment tools (Location, Moon Adjustment & Full Schedule details)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Change location text button
                TextButton(
                    onClick = { showLocationDialog = true },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFA7F3D0)),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = null,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "জেলা/শহর পরিবর্তন (৬৪ জেলা)",
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                // Moon Adjustment button
                TextButton(
                    onClick = { showHijriAdjustmentDialog = true },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFA7F3D0)),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = null,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "চাঁদ সমন্বয় ${if (hijriOffset != 0) "(${if (hijriOffset > 0) "+$hijriOffset" else "$hijriOffset"})" else ""}",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }

    // ==============================================================
    // 3. INTERACTIVE DIALOGS & ACTION HANDLERS
    // ==============================================================

    // Individual Prayer Waqt Detail Dialog (Opened on tapping ANY prayer card)
    selectedPrayerForDetail?.let { prayer ->
        val isPrayed = completedPrayers[prayer.id] == true
        PrayerWaqtDetailDialog(
            prayer = prayer,
            isPrayed = isPrayed,
            onTogglePrayed = {
                val newState = !isPrayed
                completedPrayers[prayer.id] = newState
                Toast.makeText(
                    context,
                    if (newState) "${prayer.nameBn} নামাজ আদায় হিসেবে চিহ্নিত করা হয়েছে" else "${prayer.nameBn} নামাজের চিহ্ন উঠিয়ে নেওয়া হয়েছে",
                    Toast.LENGTH_SHORT
                ).show()
            },
            onDismiss = { selectedPrayerForDetail = null }
        )
    }

    // Full Prayer Schedule & Prohibited Times Dialog
    if (showFullPrayerScheduleDialog) {
        FullPrayerScheduleDialog(
            prayerTimes = prayerTimes,
            currentLocation = currentLocation,
            onDismiss = { showFullPrayerScheduleDialog = false }
        )
    }

    // Fasting Times, Niyyah & Iftar Dua Dialog
    if (showFastingDialog) {
        FastingTimesAndDuaDialog(
            prayerTimes = prayerTimes,
            onDismiss = { showFastingDialog = false }
        )
    }

    // World & Bangladesh Location Picker Dialog
    if (showLocationDialog) {
        WorldLocationPickerDialog(
            currentLocation = currentLocation,
            onDismiss = { showLocationDialog = false },
            onSelectLocation = { selected ->
                onSelectLocation(selected)
                Toast.makeText(context, "${selected.cityBn} এর নামাজের সময়সূচি সেট করা হয়েছে", Toast.LENGTH_SHORT).show()
                showLocationDialog = false
            },
            onResetToDhaka = {
                onResetToDhaka()
                Toast.makeText(context, "ডিফল্ট ঢাকা সেট করা হয়েছে", Toast.LENGTH_SHORT).show()
                showLocationDialog = false
            }
        )
    }

    // Hijri Moon Adjustment Dialog
    if (showHijriAdjustmentDialog) {
        AlertDialog(
            onDismissRequest = { showHijriAdjustmentDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.NightsStay, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("হিজরি তারিখ ও চাঁদ সমন্বয়")
                }
            },
            text = {
                Column {
                    Text(
                        text = "চাঁদ দেখার তারতম্য অনুযায়ী হিজরি তারিখ ১ বা ২ দিন এগিয়ে বা পিছিয়ে নিতে পারেন:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf(-2, -1, 0, 1, 2).forEach { offset ->
                            val isSelected = hijriOffset == offset
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    onUpdateHijriOffset(offset)
                                    Toast.makeText(context, "চাঁদ সমন্বয় আপডেট হয়েছে", Toast.LENGTH_SHORT).show()
                                },
                                label = {
                                    Text(
                                        text = when (offset) {
                                            0 -> "স্বাভাবিক"
                                            1 -> "+১ দিন"
                                            2 -> "+২ দিন"
                                            -1 -> "-১ দিন"
                                            else -> "-২ দিন"
                                        }
                                    )
                                }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showHijriAdjustmentDialog = false }) {
                    Text("সম্পন্ন")
                }
            }
        )
    }
}

/**
 * Individual Prayer Detail Dialog with Rakats breakdown, Hadith, and Done Checkbox
 */
@Composable
fun PrayerWaqtDetailDialog(
    prayer: PrayerTimeItem,
    isPrayed: Boolean,
    onTogglePrayed: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Mosque,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "${prayer.nameBn} সালাত (${prayer.nameEn})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = prayer.nameAr,
                            fontFamily = ArabicFontFamily,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Waqt timing card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "আউয়াল ওয়াক্ত (শুরু)",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = prayer.timeFormatted,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        if (prayer.endTimeFormatted.isNotEmpty()) {
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "ওয়াক্ত শেষ সময়",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = prayer.endTimeFormatted,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Rakats Breakdown
                if (prayer.rakatsDescription.isNotEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.FormatListNumbered,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "রাকাত সংখ্যা ও নিয়ম:",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = prayer.rakatsDescription,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Hadith / Virtue
                if (prayer.hadithVirtue.isNotEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0x1A10B981),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x4D10B981)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "✨ হাদিস ও ফজিলত:",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF047857)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = prayer.hadithVirtue,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Daily Prayer Tracker toggle button
                Surface(
                    onClick = onTogglePrayed,
                    shape = RoundedCornerShape(10.dp),
                    color = if (isPrayed) Color(0xFF047857) else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isPrayed) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                contentDescription = null,
                                tint = if (isPrayed) Color.White else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isPrayed) "আজকের ${prayer.nameBn} নামাজ আদায় সম্পন্ন হয়েছে" else "আজকের ${prayer.nameBn} নামাজ আদায় করেছি (চিহ্নিত করুন)",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isPrayed) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("বন্ধ করুন")
            }
        }
    )
}

/**
 * Full Prayer Schedule, Prohibited times & Azan Dua Dialog
 */
@Composable
fun FullPrayerScheduleDialog(
    prayerTimes: LocationPrayerTimes,
    currentLocation: WorldLocation,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Mosque, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "নামাজের পূর্ণাঙ্গ সময়সূচি",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(420.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        text = "স্থান: ${currentLocation.cityBn}, ${currentLocation.countryBn} (${currentLocation.timezoneId})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // 6 Waqt Table
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "আজকের সকল ওয়াক্তের সময়সূচি:",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            prayerTimes.allPrayers.forEach { p ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "${p.nameBn} (${p.nameEn})",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = if (p.isCurrent || p.isNext) FontWeight.Bold else FontWeight.Normal,
                                            color = if (p.isCurrent) Color(0xFF047857) else MaterialTheme.colorScheme.onSurface
                                        )
                                        if (p.isCurrent) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(color = Color(0xFF10B981), shape = RoundedCornerShape(4.dp)) {
                                                Text(
                                                    text = "চলমান",
                                                    fontSize = 9.sp,
                                                    color = Color.White,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                )
                                            }
                                        }
                                    }
                                    Text(
                                        text = p.timeFormatted,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (p.isNext) Color(0xFFB45309) else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                HorizontalDivider(color = Color(0x1A000000), thickness = 0.5.dp)
                            }
                        }
                    }
                }

                // Tahajjud & Ishraq
                item {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0x1F38BDF8),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "তাহাজ্জুদ শেষ: ${prayerTimes.tahajjudEnd}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "ইশরাক শুরু: ${prayerTimes.ishraqStart}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // 3 Prohibited / Makruh times
                item {
                    Text(
                        text = "নামাজের ৩টি নিষিদ্ধ সময় (মাকরূহ ওয়াক্ত):",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                items(prayerTimes.forbiddenTimes) { forbidden ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0x1AE11D48),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33E11D48)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = forbidden.titleBn,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFBE123C)
                                )
                                Text(
                                    text = forbidden.periodFormatted,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFBE123C)
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = forbidden.descriptionBn,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Dua after Azan
                item {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "আজানের পর পড়ার দোয়া (সহীহ বুখারী):",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "اللَّهُمَّ رَبَّ هَذِهِ الدَّعْوَةِ التَّامَّةِ، وَالصَّلَاةِ الْقَائِمَةِ، آتِ مُحَمَّدًا الْوَسِيلَةَ وَالْفَضِيلَةَ، وَابْعَثْهُ مَقَامًا مَحْمُودًا الَّذِي وَعَدْتَهُ",
                                fontFamily = ArabicFontFamily,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "উচ্চারণ: আল্লাহুম্মা রব্বা হাযিহিদ দাওয়াতিত তাম্মাহ, ওয়াস সালাতিল ক্বা-য়িমাহ, আতি মুহাম্মাদানিল ওয়াসীলাতা ওয়াল ফাদীলাহ, ওয়াব'আসহু মাক্বামাম মাহমুদানিল্লাযী ওয়া'আদতাহ।",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("বন্ধ করুন")
            }
        }
    )
}

/**
 * Fasting times, Niyyah and Iftar Dua Dialog
 */
@Composable
fun FastingTimesAndDuaDialog(
    prayerTimes: LocationPrayerTimes,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.NightsStay, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "সাহরি ও ইফতারের সময় ও দুআ",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Sahri and Iftar Times Card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "🌙 সাহরি শেষ সময়:", style = MaterialTheme.typography.labelSmall)
                            Text(
                                text = prayerTimes.fajr,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(text = "(ফজরের আউয়াল ওয়াক্ত)", style = MaterialTheme.typography.labelSmall)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "☀️ ইফতারের সময়:", style = MaterialTheme.typography.labelSmall)
                            Text(
                                text = prayerTimes.maghrib,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = GoldAccent
                            )
                            Text(text = "(মাগরিবের আউয়াল ওয়াক্ত)", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }

                // Fasting Niyyah
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "রোজার নিয়ত:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "نَوَيْتُ أَنْ أَصُومَ غَدًا مِنْ شَهْرِ رَمَضَانَ الْمُبَارَكِ فَرْضًا لَكَ يَا اللهُ فَتَقَبَّلْ مِنِّي",
                            fontFamily = ArabicFontFamily,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "উচ্চারণ: নাওয়াইতু আন আসূমা গাদাম মিন শাহরি রমাদানাল মুবারকি ফারদাল্লাকা ইয়া আল্লাহু ফাতাকাব্বাল মিন্নী।",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                // Iftar Dua
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0x1F10B981),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "ইফতারের দুআ (সহীহ আবু দাউদ):",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF047857)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "ذَهَبَ الظَّمَأُ وَابْتَلَّتِ الْعُرُوقُ وَثَبَتَ الأَجْرُ إِنْ شَاءَ اللَّهُ",
                            fontFamily = ArabicFontFamily,
                            fontSize = 14.sp,
                            color = Color(0xFF065F46)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "উচ্চারণ: যাহাবাজ জামা'উ ওয়াবতাল্লাতিল 'উEntity রূকু ওয়া সাবাতাল আজরু ইনশাআল্লাহ।\nঅর্থ: পিপাসা দূর হলো, শিরা-উপশিরা সিক্ত হলো এবং ইনশাআল্লাহ সওয়াবও নির্ধারিত হলো।",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("বন্ধ করুন")
            }
        }
    )
}

/**
 * World & Bangladesh 64 District Location Picker Dialog
 */
@Composable
fun WorldLocationPickerDialog(
    currentLocation: WorldLocation,
    onDismiss: () -> Unit,
    onSelectLocation: (WorldLocation) -> Unit,
    onResetToDhaka: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedRegionTab by remember { mutableStateOf("সব") }

    val regions = listOf(
        "সব",
        "ঢাকা",
        "চট্টগ্রাম",
        "সিলেট",
        "রাজশাহী",
        "খুলনা",
        "বরিশাল",
        "রংপুর",
        "ময়মনসিংহ",
        "মধ্যপ্রাচ্য",
        "ইউরোপ ও আমেরিকা"
    )

    val filteredLocations = remember(searchQuery, selectedRegionTab) {
        val searched = IslamicTimeManager.searchLocations(searchQuery)
        if (selectedRegionTab == "সব" || searchQuery.isNotEmpty()) {
            searched
        } else {
            searched.filter { it.region.contains(selectedRegionTab) }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "বাংলাদেশ ও বিশ্বের শহর নির্বাচন",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }
                Text(
                    text = "৬৪ জেলা ও বিশ্বের সকল প্রধান শহর অন্তর্ভুক্ত • Default: Dhaka",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(460.dp)
            ) {
                // Search Input
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("জেলা বা শহর খুঁজুন (যেমন: খুলনা, কুমিল্লা, মক্কা)...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("world_location_search_field"),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Default Dhaka Button
                Button(
                    onClick = onResetToDhaka,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("reset_dhaka_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (currentLocation.id == IslamicTimeManager.DEFAULT_LOCATION.id)
                            MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("🇧🇩")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ডিফল্ট ঢাকা সেট করুন (Dhaka, Bangladesh)",
                        color = if (currentLocation.id == IslamicTimeManager.DEFAULT_LOCATION.id)
                            MaterialTheme.colorScheme.onPrimaryContainer
                        else MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Region filter chips
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(regions) { region ->
                        FilterChip(
                            selected = selectedRegionTab == region && searchQuery.isEmpty(),
                            onClick = {
                                selectedRegionTab = region
                                searchQuery = ""
                            },
                            label = { Text(region, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(6.dp))

                // Location list
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(filteredLocations) { loc ->
                        val isSelected = loc.id == currentLocation.id ||
                            (loc.timezoneId == currentLocation.timezoneId && loc.cityBn == currentLocation.cityBn)

                        Surface(
                            onClick = { onSelectLocation(loc) },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = loc.flagEmoji,
                                        fontSize = 22.sp
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = "${loc.cityBn}, ${loc.countryBn}",
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                            )
                                            if (loc.isDefault) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Surface(
                                                    color = Color(0xFF10B981),
                                                    shape = RoundedCornerShape(4.dp)
                                                ) {
                                                    Text(
                                                        text = "Default",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = Color.White,
                                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                    )
                                                }
                                            }
                                        }
                                        Text(
                                            text = "${loc.cityEn}, ${loc.countryEn} • ${loc.region}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }

                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("বন্ধ করুন")
            }
        }
    )
}
