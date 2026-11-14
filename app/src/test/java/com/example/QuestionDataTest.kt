package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.InitialData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class QuestionDataTest {

    private val questions by lazy {
        InitialData.getInitialQuestions(ApplicationProvider.getApplicationContext<Context>())
    }

    @Test
    fun allQuestionsAreLoaded() {
        assertEquals(414, questions.size)
    }

    @Test
    fun idsAreSequentialAndStable() {
        assertEquals((1..questions.size).toList(), questions.map { it.id })
    }

    @Test
    fun everyQuestionIsWellFormed() {
        questions.forEach { q ->
            val variants = listOf(q.variantA, q.variantB, q.variantC, q.variantD)
            assertTrue("id=${q.id} boş sual", q.sual.isNotBlank())
            assertTrue("id=${q.id} boş variant", variants.all { it.isNotBlank() })
            assertEquals("id=${q.id} təkrar variant", 4, variants.toSet().size)
            assertTrue("id=${q.id} düzgün indeks", q.duzgunCavabIndex in 0..3)
            assertTrue("id=${q.id} bölmə", q.bolme in 1..4)
        }
    }
}
