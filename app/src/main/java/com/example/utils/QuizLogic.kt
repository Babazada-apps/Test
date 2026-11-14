package com.example.utils

import com.example.data.local.QuestionEntity
import kotlin.random.Random

/** Test/imtahan məntiqinin saf (Android-dan asılı olmayan) hissəsi. Unit testlərlə yoxlanılır. */
object QuizLogic {
    const val PASS_PERCENTAGE = 75
    const val EXAM_DURATION_SECONDS = 1200L
    const val EXAM_QUESTION_COUNT = 20

    fun calculatePercentage(correct: Int, total: Int): Int =
        if (total > 0) (correct * 100) / total else 0

    fun isPassed(percentage: Int): Boolean = percentage >= PASS_PERCENTAGE

    /** Qalan saniyə (heç vaxt mənfi olmur). Divar saatına əsaslanır, ona görə fonda da düzgün işləyir. */
    fun remainingSeconds(endTimeMillis: Long, nowMillis: Long): Long =
        ((endTimeMillis - nowMillis + 999L) / 1000L).coerceAtLeast(0L)

    // Variantın mövqeyindən asılı ola bilən ifadələr ("hamısı", "A və B" və s.)
    private val positionalWords = listOf(
        "hamısı", "heç biri", "heç birini", "yuxarıdakı", "aşağıdakı", "hər ikisi", "hər üçü"
    )
    private val letterCombo = Regex("""(^|[\s(])[A-D]\)?\s*(və|və ya|,)\s*[A-D]($|[\s).])""")

    fun canShuffleOptions(q: QuestionEntity): Boolean {
        val variants = listOf(q.variantA, q.variantB, q.variantC, q.variantD)
        return variants.none { v ->
            positionalWords.any { v.contains(it, ignoreCase = true) } || letterCombo.containsMatchIn(v)
        }
    }

    /**
     * Variantların yerini qarışdırır, düzgün cavab indeksini uyğun yeniləyir.
     * Mövqeyə bağlı variantı olan suallar olduğu kimi qaytarılır.
     */
    fun shuffleOptions(q: QuestionEntity, random: Random = Random.Default): QuestionEntity {
        if (q.duzgunCavabIndex !in 0..3 || !canShuffleOptions(q)) return q
        val options = listOf(q.variantA, q.variantB, q.variantC, q.variantD)
        val order = (0..3).shuffled(random) // yeni mövqe i köhnə order[i] variantını alır
        val shuffled = order.map { options[it] }
        return q.copy(
            variantA = shuffled[0],
            variantB = shuffled[1],
            variantC = shuffled[2],
            variantD = shuffled[3],
            duzgunCavabIndex = order.indexOf(q.duzgunCavabIndex)
        )
    }
}
