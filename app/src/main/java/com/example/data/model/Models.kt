package com.example.data.model

import com.google.firebase.Timestamp

data class Category(
    val id: String = "",
    val name: String = "",
    val nameBn: String = "",
    val description: String = "",
    val icon: String = "menu_book",
    val order: Int = 0,
    val isActive: Boolean = true
)

data class Subcategory(
    val id: String = "",
    val categoryId: String = "",
    val name: String = "",
    val nameBn: String = "",
    val description: String = "",
    val order: Int = 0,
    val isActive: Boolean = true
)

data class Post(
    val id: String = "",
    val title: String = "",
    val titleBn: String = "",
    val subtitle: String = "",
    val arabicText: String = "",
    val bengaliText: String = "",
    val englishText: String = "",
    val transliteration: String = "",
    val mainContent: String = "",
    val categoryId: String = "",
    val subcategoryId: String = "",
    val featuredImage: String = "",
    val audioUrl: String = "",
    val sourceReference: String = "",
    val author: String = "",
    val tags: List<String> = emptyList(),
    val isFeatured: Boolean = false,
    val isPublished: Boolean = true,
    val viewCount: Long = 0L,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
)

data class Quiz(
    val id: String = "",
    val title: String = "",
    val titleBn: String = "",
    val description: String = "",
    val categoryId: String = "",
    val questionCount: Int = 5,
    val isActive: Boolean = true
)

data class QuizQuestion(
    val id: String = "",
    val quizId: String = "",
    val question: String = "",
    val questionBn: String = "",
    val options: List<String> = emptyList(),
    val optionsBn: List<String> = emptyList(),
    val correctAnswerIndex: Int = 0,
    val explanation: String = "",
    val sourceReference: String = ""
)

data class AppSettings(
    val id: String = "default",
    val admobBannerEnabled: Boolean = true,
    val admobBannerAdUnitId: String = "ca-app-pub-6596350151078116/3154086375",
    val admobInterstitialEnabled: Boolean = false, // Require verified ID before enabling
    val admobInterstitialAdUnitId: String = "",
    val admobRewardedEnabled: Boolean = true,
    val admobRewardedAdUnitId: String = "ca-app-pub-6596350151078116/2848807925",
    val admobAppOpenEnabled: Boolean = true,
    val admobAppOpenAdUnitId: String = "ca-app-pub-6596350151078116/5690593680",
    val interstitialMinIntervalMinutes: Int = 3,
    val interstitialMinActions: Int = 4,
    val interstitialMaxPerSession: Int = 5,
    val interstitialCooldownSeconds: Int = 60,
    val announcement: String = "",
    val privacyPolicyUrl: String = "",
    val updatedAt: Timestamp? = null
)

data class AdminProfile(
    val uid: String = "",
    val email: String = "",
    val role: String = "admin"
)
