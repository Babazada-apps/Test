package com.example

import com.example.utils.QuestionParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class QuestionParserTest {

    @Test
    fun validJsonArray_isParsed() {
        val json = """
            [{"sual":"Sual 1?","variantlar":["a","b","c","d"],"duzgun":2,"izah":"x","bolme":"3.1"},
             {"sual":"Sual 2?","variantlar":["a","b","c","d"],"duzgun":0}]
        """.trimIndent()
        val result = QuestionParser.parse(json, 1)
        assertEquals(2, result.size)
        assertEquals(2, result[0].duzgunCavabIndex)
        assertEquals(3, result[0].bolme)
        assertEquals(1, result[1].bolme)
    }

    @Test
    fun invalidJson_returnsEmpty_notGarbageQuestions() {
        assertTrue(QuestionParser.parse("[{\"sual\": \"yarımçıq\"", 1).isEmpty())
        assertTrue(QuestionParser.parse("{ bu json deyil }", 1).isEmpty())
    }

    @Test
    fun jsonWithBadAnswerIndex_isSkipped() {
        val json = """[{"sual":"S?","variantlar":["a","b","c","d"],"duzgun":7}]"""
        assertTrue(QuestionParser.parse(json, 1).isEmpty())
    }

    @Test
    fun plainText_generatesAtMostTenQuestions_withCorrectAnswerKept() {
        val text = (1..15).joinToString("\n") { "Bu təlimat cümləsi nömrə $it haqqındadır." }
        val result = QuestionParser.parse(text, 2)
        assertEquals(10, result.size)
        result.forEach { q ->
            val variants = listOf(q.variantA, q.variantB, q.variantC, q.variantD)
            assertEquals("Bəli, doğrudur", variants[q.duzgunCavabIndex])
            assertEquals(2, q.bolme)
        }
    }
}
