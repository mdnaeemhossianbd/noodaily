package com.example.ui.dua

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.admob.BannerAdView
import com.example.ui.theme.ArabicFontFamily
import com.example.ui.theme.BengaliFontFamily
import com.example.ui.theme.GoldAccent

data class DuaItem(
    val id: String,
    val category: String,
    val title: String,
    val arabic: String,
    val transliteration: String,
    val translation: String,
    val reference: String,
    val targetCount: Int = 1
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyDuaScreen(
    onNavigateBack: () -> Unit
) {
    BackHandler { onNavigateBack() }

    val categories = listOf("সবগুলো", "সকাল-সন্ধ্যা", "ঘুম ও জাগরণ", "খাবার-দাবার", "সফর ও যাত্রা", "দৈনন্দিন যিকির")
    var selectedCategory by remember { mutableStateOf("সবগুলো") }

    val duas = remember {
        listOf(
            DuaItem(
                id = "d1",
                category = "সকাল-সন্ধ্যা",
                title = "সকালের বরকতময় দুআ",
                arabic = "أَصْبَحْنَا وَأَصْبَحَ الْمُلْكُ لِلَّهِ، وَالْحَمْدُ لِلَّهِ",
                transliteration = "Asbahna wa asbahal-mulku lillah, wal-hamdu lillah",
                translation = "আমরা সকালে উপনীত হয়েছি এবং সমস্ত রাজত্বও আল্লাহর জন্য সকালে উপনীত হয়েছে, সমস্ত প্রশংসা একমাত্র আল্লাহর।",
                reference = "সহীহ মুসলিম: ২৭২৩",
                targetCount = 1
            ),
            DuaItem(
                id = "d2",
                category = "সকাল-সন্ধ্যা",
                title = "সাইয়্যিদুল ইস্তিগফার (শ্রেষ্ঠ ক্ষমা প্রার্থনা)",
                arabic = "اللَّهُمَّ أَنْتَ رَبِّي لَا إِلَهَ إِلَّا أَنْتَ، خَلَقْتَنِي وَأَنَا عَبْدُكَ",
                transliteration = "Allahumma Anta Rabbi la ilaha illa Anta, khalaqtani wa ana 'abduka",
                translation = "হে আল্লাহ! আপনি আমার প্রতিপালক, আপনি ছাড়া সত্য কোনো উপাস্য নেই। আপনি আমাকে সৃষ্টি করেছেন এবং আমি আপনার দাস।",
                reference = "সহীহ বুখারী: ৬৩০৬",
                targetCount = 1
            ),
            DuaItem(
                id = "d3",
                category = "ঘুম ও জাগরণ",
                title = "ঘুমানোর পূর্বে পাঠ করার সুন্নাত দুআ",
                arabic = "بِاسْمِكَ اللَّهُمَّ أَمُوتُ وَأَحْيَا",
                transliteration = "Bismika Allahumma amootu wa ahyaa",
                translation = "হে আল্লাহ! আপনারই নামে আমি মৃত্যুবরণ (ঘুমাই) করি এবং আপনারই নামে পুনর্জীবিত হই।",
                reference = "সহীহ বুখারী: ৬৩২৪",
                targetCount = 1
            ),
            DuaItem(
                id = "d4",
                category = "ঘুম ও জাগরণ",
                title = "ঘুম থেকে জাগ্রত হওয়ার পর দুআ",
                arabic = "الْحَمْدُ لِلَّهِ الَّذِي أَحْيَانَا بَعْدَ مَا أَمَاتَنَا وَإِلَيْهِ النُّشُورُ",
                transliteration = "Al-hamdu lillahil-ladhi ahyana ba'da ma amatana wa ilayhin-nushoor",
                translation = "সমস্ত প্রশংসা সেই আল্লাহর যিনি আমাদের মৃত্যুর পর জীবিত করলেন এবং তাঁরই কাছে সকলের প্রত্যাবর্তন।",
                reference = "সহীহ বুখারী: ৬৩১২",
                targetCount = 1
            ),
            DuaItem(
                id = "d5",
                category = "খাবার-দাবার",
                title = "আহার শুরু করার দুআ",
                arabic = "بِسْمِ اللَّهِ وَعَلَى بَرَكَةِ اللَّهِ",
                transliteration = "Bismillahi wa 'ala barakatillah",
                translation = "আল্লাহর নামে এবং আল্লাহর বরকতের সাথে শুরু করছি।",
                reference = "আবু দাউদ ও তিরমিযী",
                targetCount = 1
            ),
            DuaItem(
                id = "d6",
                category = "সফর ও যাত্রা",
                title = "যানবাহনে আরোহণের দুআ",
                arabic = "سُبْحَانَ الَّذِي سَخَّرَ لَنَا هَٰذَا وَمَا كُنَّا لَهُ مُقْرِنِينَ وَإِنَّا إِلَىٰ رَبِّنَا لَمُنقَلِبُونَ",
                transliteration = "Subhanal-ladhi sakh-khara lana hadha wa ma kunna lahu muqrinin, wa inna ila Rabbina lamunqaliboon",
                translation = "পবিত্র সেই সত্তা যিনি এটিকে আমাদের বশীভূত করে দিয়েছেন, অথচ আমরা এটিকে বশ করতে সক্ষম ছিলাম না। এবং নিশ্চয়ই আমরা আমাদের রবের দিকেই প্রত্যাবর্তনকারী।",
                reference = "সূরা যুখরুফ: ১৩-১৪ / তিরমিযী",
                targetCount = 1
            ),
            DuaItem(
                id = "d7",
                category = "দৈনন্দিন যিকির",
                title = "তাসবীহ ও তাহমীদ (সুবহানাল্লাহ ও আলহামদুলিল্লাহ)",
                arabic = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ ، سُبْحَانَ اللَّهِ الْعَظِيمِ",
                transliteration = "Subhanallahi wa bihamdihi, Subhanallahil-Azim",
                translation = "আল্লাহ অতি পবিত্র এবং সমস্ত প্রশংসা তাঁরই। মহান আল্লাহ অতি পবিত্র। (দয়াময় আল্লাহর নিকট অত্যন্ত প্রিয় দুটি বাক্য)।",
                reference = "সহীহ বুখারী: ৬৪০৬",
                targetCount = 33
            )
        )
    }

    val filteredDuas = if (selectedCategory == "সবগুলো") {
        duas
    } else {
        duas.filter { it.category == selectedCategory }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("দৈনিক দুআ ও আজকার") },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("dua_back_button")
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
        ) {
            // Category Filter Row
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }

            // Duas List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 4.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(filteredDuas) { item ->
                    DuaInteractiveCard(item = item)
                }
            }
        }
    }
}

@Composable
private fun DuaInteractiveCard(item: DuaItem) {
    var count by remember { mutableIntStateOf(0) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("dua_card_${item.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                ) {
                    Text(
                        text = item.reference,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Text(
                        text = item.arabic,
                        fontFamily = ArabicFontFamily,
                        fontSize = 20.sp,
                        lineHeight = 32.sp,
                        textAlign = TextAlign.Right,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "উচ্চারণ: ${item.transliteration}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "অর্থ: ${item.translation}",
                fontFamily = BengaliFontFamily,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Tasbih / Counter section
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "পাঠ সংখ্যা: ",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "$count",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (count >= item.targetCount) GoldAccent else MaterialTheme.colorScheme.primary
                    )
                    if (item.targetCount > 1) {
                        Text(
                            text = " / ${item.targetCount} বার",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (count > 0) {
                        IconButton(
                            onClick = { count = 0 },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = "Reset Counter",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .clickable { count++ }
                            .testTag("tasbih_counter_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "গণনা",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
