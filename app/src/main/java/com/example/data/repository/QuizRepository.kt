package com.example.data.repository

import com.example.data.local.ExamResultDao
import com.example.data.local.ExamResultEntity
import com.example.data.local.InitialData
import com.example.data.local.QuestionDao
import com.example.data.local.QuestionEntity
import kotlinx.coroutines.flow.Flow

class QuizRepository(
    private val questionDao: QuestionDao,
    private val examResultDao: ExamResultDao
) {
    val allQuestions: Flow<List<QuestionEntity>> = questionDao.getAllQuestions()
    val favoriteQuestions: Flow<List<QuestionEntity>> = questionDao.getFavoriteQuestions()
    val wrongQuestions: Flow<List<QuestionEntity>> = questionDao.getWrongQuestions()
    val examResults: Flow<List<ExamResultEntity>> = examResultDao.getAllResults()

    fun getQuestionsBySection(bolme: Int): Flow<List<QuestionEntity>> {
        return questionDao.getQuestionsBySection(bolme)
    }

    fun searchQuestions(query: String): Flow<List<QuestionEntity>> {
        return questionDao.searchQuestions(query)
    }

    suspend fun toggleFavorite(id: Int, isFavorite: Boolean) {
        questionDao.updateFavorite(id, isFavorite)
    }

    suspend fun recordAnswer(id: Int, isCorrect: Boolean) {
        questionDao.updateAnswerResult(id, isCorrect)
    }

    suspend fun saveExamResult(result: ExamResultEntity) {
        examResultDao.insertResult(result)
    }

    suspend fun insertQuestion(question: QuestionEntity) {
        questionDao.insertQuestion(question)
    }

    /** İstifadəçinin əlavə etdiyi suallar: ID-lər CUSTOM_ID_START-dan başlayır. */
    suspend fun insertCustomQuestions(questions: List<QuestionEntity>) {
        if (questions.isEmpty()) return
        var nextId = maxOf(questionDao.getMaxId() ?: 0, InitialData.CUSTOM_ID_START - 1) + 1
        questionDao.insertQuestions(questions.map { it.copy(id = nextId++) })
    }

    suspend fun getQuestionCount(): Int = questionDao.getQuestionCount()

    /**
     * Daxili sualları yükləyir/yeniləyir. Mövcud sualların yalnız məzmunu yenilənir,
     * seçilmişlər və statistika qorunur.
     */
    suspend fun syncBuiltInQuestions(seed: List<QuestionEntity>) {
        if (seed.isEmpty()) return
        val results = questionDao.insertQuestionsIgnore(seed)
        seed.forEachIndexed { index, q ->
            if (results.getOrNull(index) == -1L) {
                questionDao.updateContent(
                    id = q.id,
                    sual = q.sual,
                    variantA = q.variantA,
                    variantB = q.variantB,
                    variantC = q.variantC,
                    variantD = q.variantD,
                    duzgunCavabIndex = q.duzgunCavabIndex,
                    izah = q.izah,
                    bolme = q.bolme,
                    paraqraf = q.paraqraf,
                    cetinlik = q.cetinlik,
                    sualTipi = q.sualTipi
                )
            }
        }
        questionDao.deleteBuiltInAfter(seed.size, InitialData.CUSTOM_ID_START)
    }

    suspend fun resetStatistics() {
        examResultDao.clearAllResults()
        questionDao.resetQuestionStats()
    }
}
