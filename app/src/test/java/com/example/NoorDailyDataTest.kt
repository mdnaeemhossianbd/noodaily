package com.example

import com.example.admob.AdMobManager
import com.example.data.seed.InitialDataSeed
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class NoorDailyDataTest {

    @Test
    fun initialCategories_containRequiredFiveCategories() {
        val categories = InitialDataSeed.categories
        assertEquals(5, categories.size)
        assertTrue(categories.any { it.id == "cat_quran" })
        assertTrue(categories.any { it.id == "cat_hadith" })
        assertTrue(categories.any { it.id == "cat_kids" })
        assertTrue(categories.any { it.id == "cat_quiz" })
        assertTrue(categories.any { it.id == "cat_dua" })
    }

    @Test
    fun initialPosts_haveAuthenticArabicAndBengali() {
        val posts = InitialDataSeed.posts
        assertTrue(posts.isNotEmpty())
        posts.forEach { post ->
            assertTrue("Post ${post.id} must have a title", post.title.isNotBlank())
            assertTrue("Post ${post.id} must have an authentic source reference", post.sourceReference.isNotBlank())
        }
    }

    @Test
    fun admobConfig_usesConfiguredProductionIds() {
        assertEquals("ca-app-pub-6596350151078116/3154086375", AdMobManager.PROD_BANNER_AD_UNIT_ID)
        assertEquals("ca-app-pub-6596350151078116/2848807925", AdMobManager.PROD_REWARDED_AD_UNIT_ID)
        assertEquals("ca-app-pub-6596350151078116/5690593680", AdMobManager.PROD_APP_OPEN_AD_UNIT_ID)
    }

    @Test
    fun quizQuestions_haveValidCorrectAnswers() {
        val questions = InitialDataSeed.quizQuestions
        assertTrue(questions.isNotEmpty())
        questions.forEach { q ->
            assertTrue(q.correctAnswerIndex >= 0)
            assertTrue(q.correctAnswerIndex < (q.optionsBn.ifEmpty { q.options }).size)
            assertTrue("Question must have an explanation", q.explanation.isNotBlank())
        }
    }
}
