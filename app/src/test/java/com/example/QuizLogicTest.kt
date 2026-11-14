package com.example

import com.example.data.local.QuestionEntity
import com.example.utils.QuizLogic
import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class QuizLogicTest {

    private fun question(
        a: String = "10000 kq",
        b: String = "10500 kq",
        c: String = "11100 kq",
        d: String = "13000 kq",
        correct: Int = 2
    ) = QuestionEntity(
        id = 1, sual = "Sual?", variantA = a, variantB = b, variantC = c, variantD = d,
        duzgunCavabIndex = correct, izah = "izah", bolme = 1, paraqraf = "1.1",
        cetinlik = "Orta", sualTipi = "4 Variantli"
    )

    private fun correctText(q: QuestionEntity) =
        listOf(q.variantA, q.variantB, q.variantC, q.variantD)[q.duzgunCavabIndex]

    @Test
    fun percentage_isCalculatedAndSafeForZero() {
        assertEquals(75, QuizLogic.calculatePercentage(15, 20))
        assertEquals(0, QuizLogic.calculatePercentage(0, 0))
        assertEquals(100, QuizLogic.calculatePercentage(20, 20))
    }

    @Test
    fun passThreshold_is75Percent() {
        assertTrue(QuizLogic.isPassed(75))
        assertFalse(QuizLogic.isPassed(74))
    }

    @Test
    fun remainingSeconds_neverNegative_andFollowsWallClock() {
        assertEquals(1200L, QuizLogic.remainingSeconds(1_200_000L, 0L))
        assertEquals(1L, QuizLogic.remainingSeconds(1_000L, 500L))
        assertEquals(0L, QuizLogic.remainingSeconds(1_000L, 5_000L))
    }

    @Test
    fun shuffle_keepsCorrectAnswerText() {
        val original = question()
        repeat(50) { seed ->
            val shuffled = QuizLogic.shuffleOptions(original, Random(seed))
            assertEquals("11100 kq", correctText(shuffled))
            assertEquals(
                setOf("10000 kq", "10500 kq", "11100 kq", "13000 kq"),
                setOf(shuffled.variantA, shuffled.variantB, shuffled.variantC, shuffled.variantD)
            )
        }
    }

    @Test
    fun shuffle_movesCorrectAnswerAround() {
        val positions = (0 until 100).map {
            QuizLogic.shuffleOptions(question(), Random(it)).duzgunCavabIndex
        }.toSet()
        assertEquals(setOf(0, 1, 2, 3), positions)
    }

    @Test
    fun shuffle_skipsPositionalVariants() {
        val q = question(d = "Yuxarıdakıların hamısı", correct = 3)
        assertFalse(QuizLogic.canShuffleOptions(q))
        assertSame(q, QuizLogic.shuffleOptions(q, Random(1)))

        val q2 = question(d = "A və B", correct = 3)
        assertFalse(QuizLogic.canShuffleOptions(q2))
    }

    @Test
    fun shuffle_ignoresInvalidCorrectIndex() {
        val q = question(correct = 9)
        assertSame(q, QuizLogic.shuffleOptions(q, Random(1)))
    }
}
