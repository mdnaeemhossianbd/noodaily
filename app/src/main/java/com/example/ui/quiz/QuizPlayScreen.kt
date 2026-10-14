package com.example.ui.quiz

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.admob.AdMobManager
import com.example.admob.BannerAdView
import com.example.data.repository.QuizRepository
import com.example.data.seed.InitialDataSeed
import com.example.ui.theme.BengaliFontFamily
import com.example.ui.theme.GoldAccent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizPlayScreen(
    quizId: String,
    onNavigateBack: () -> Unit
) {
    BackHandler { onNavigateBack() }

    val context = LocalContext.current
    val activity = context as? Activity
    val quizRepository = remember { QuizRepository(context) }
    val repoQuestions by quizRepository.observeQuizQuestions(quizId).collectAsState(initial = emptyList())
    val questions = if (repoQuestions.isNotEmpty()) repoQuestions else remember(quizId) {
        val list = InitialDataSeed.quizQuestions.filter { it.quizId == quizId }
        if (list.isEmpty()) InitialDataSeed.quizQuestions else list
    }

    var currentIndex by remember { mutableIntStateOf(0) }
    var selectedOptionIndex by remember { mutableStateOf<Int?>(null) }
    var score by remember { mutableIntStateOf(0) }
    var isQuizCompleted by remember { mutableStateOf(false) }
    var hasBonusAchievement by remember { mutableStateOf(false) }

    val currentQuestion = if (questions.isNotEmpty() && currentIndex < questions.size) {
        questions[currentIndex]
    } else null

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ইসলামিক কুইজ") },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("quiz_play_back_button")
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
        if (isQuizCompleted) {
            // Result & Badge Screen
            QuizResultScreen(
                score = score,
                total = questions.size,
                hasBonusAchievement = hasBonusAchievement,
                onRestart = {
                    currentIndex = 0
                    selectedOptionIndex = null
                    score = 0
                    isQuizCompleted = false
                },
                onWatchRewardedAd = {
                    activity?.let { act ->
                        AdMobManager.showRewardedAd(
                            activity = act,
                            onUserEarnedReward = {
                                hasBonusAchievement = true
                                Toast.makeText(context, "বোনাস ব্যাজ অর্জিত হয়েছে!", Toast.LENGTH_LONG).show()
                            }
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        } else if (currentQuestion != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Progress
                val progress = (currentIndex + 1).toFloat() / questions.size.toFloat()
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primaryContainer
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "প্রশ্ন ${currentIndex + 1} / ${questions.size}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "স্কোর: $score",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = GoldAccent
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))
                // Question Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("quiz_question_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = currentQuestion.questionBn.ifEmpty { currentQuestion.question },
                            fontFamily = BengaliFontFamily,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                // Options
                val options = if (currentQuestion.optionsBn.isNotEmpty()) {
                    currentQuestion.optionsBn
                } else {
                    currentQuestion.options
                }

                options.forEachIndexed { index, optionText ->
                    val isSelected = selectedOptionIndex == index
                    val isCorrect = index == currentQuestion.correctAnswerIndex
                    val isAnswered = selectedOptionIndex != null

                    val borderColor = when {
                        !isAnswered -> MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        isCorrect -> Color(0xFF10B981)
                        isSelected -> Color(0xFFEF4444)
                        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    }

                    val containerColor = when {
                        !isAnswered -> MaterialTheme.colorScheme.surface
                        isCorrect -> Color(0xFFECFDF5)
                        isSelected -> Color(0xFFFEF2F2)
                        else -> MaterialTheme.colorScheme.surface
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
                            .clickable(enabled = !isAnswered) {
                                selectedOptionIndex = index
                                if (isCorrect) score++
                            }
                            .testTag("quiz_play_option_$index"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = containerColor),
                        border = BorderStroke(1.5.dp, borderColor)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = if (isAnswered && isCorrect) Color(0xFF10B981) else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    if (isAnswered && isCorrect) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                    } else if (isAnswered && isSelected) {
                                        Icon(Icons.Default.Close, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                                    } else {
                                        Text("${('ক'.code + index).toChar()}", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = optionText,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isSelected || (isAnswered && isCorrect)) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Explanation and Source Reference on answer
                if (selectedOptionIndex != null && (currentQuestion.explanation.isNotEmpty() || currentQuestion.sourceReference.isNotEmpty())) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            if (currentQuestion.explanation.isNotEmpty()) {
                                Text(
                                    text = "ব্যাখ্যা ও তথ্য:",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = currentQuestion.explanation,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            if (currentQuestion.sourceReference.isNotEmpty()) {
                                if (currentQuestion.explanation.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                                Text(
                                    text = "উৎস / রেফারেন্স: ${currentQuestion.sourceReference}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    }
                }

                // Next Button
                if (selectedOptionIndex != null) {
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = {
                            if (currentIndex + 1 < questions.size) {
                                currentIndex++
                                selectedOptionIndex = null
                            } else {
                                isQuizCompleted = true
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("quiz_next_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (currentIndex + 1 < questions.size) "পরবর্তী প্রশ্ন →" else "ফলাফল দেখুন 🏆",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuizResultScreen(
    score: Int,
    total: Int,
    hasBonusAchievement: Boolean,
    onRestart: () -> Unit,
    onWatchRewardedAd: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            shape = CircleShape,
            color = GoldAccent.copy(alpha = 0.15f),
            modifier = Modifier.size(90.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = GoldAccent,
                    modifier = Modifier.size(52.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "মাশাআল্লাহ! কুইজ সমাপ্ত হয়েছে",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "আপনার অর্জিত স্কোর: $score / $total",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(20.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = when {
                        score == total -> "অসাধারণ! আপনি প্রতিটি প্রশ্নের সঠিক উত্তর দিয়েছেন।"
                        score >= total / 2 -> "উত্তম প্রচেষ্টা! আরও বেশি অধ্যয়ন করে পূর্ণাঙ্গ জ্ঞান অর্জন করুন।"
                        else -> "ইনশাআল্লাহ নিয়মিত নূর ডেইলি পাঠের মাধ্যমে আপনার জ্ঞান আরও বৃদ্ধি পাবে।"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }

        if (hasBonusAchievement) {
            Spacer(modifier = Modifier.height(14.dp))
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFFEF3C7),
                border = BorderStroke(1.dp, GoldAccent)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Stars, contentDescription = null, tint = GoldAccent)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "বিশেষ শিক্ষণীয় ব্যাজ আনলক হয়েছে!",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF92400E)
                    )
                }
            }
        } else {
            Spacer(modifier = Modifier.height(14.dp))
            OutlinedButton(
                onClick = onWatchRewardedAd,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("rewarded_ad_bonus_button"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.VideoLibrary, contentDescription = null, tint = GoldAccent)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ঐচ্ছিক ভিডিও দেখে বোনাস ব্যাজ আনলক করুন",
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = onRestart,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Refresh, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("পুনরায় কুইজ দিন")
        }
    }
}
