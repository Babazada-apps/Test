package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionDao {
    @Query("SELECT * FROM questions ORDER BY id ASC")
    fun getAllQuestions(): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE bolme = :bolme ORDER BY id ASC")
    fun getQuestionsBySection(bolme: Int): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE isFavorite = 1 ORDER BY id ASC")
    fun getFavoriteQuestions(): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE lastAnsweredCorrectly = 0 ORDER BY id ASC")
    fun getWrongQuestions(): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE sual LIKE '%' || :query || '%' OR izah LIKE '%' || :query || '%' OR paraqraf LIKE '%' || :query || '%' ORDER BY id ASC")
    fun searchQuestions(query: String): Flow<List<QuestionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<QuestionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: QuestionEntity)

    /** Mövcud sətirlərə toxunmur (nəticədə -1 qaytarır), beləliklə statistika və seçilmişlər qorunur. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertQuestionsIgnore(questions: List<QuestionEntity>): List<Long>

    /** Yalnız sual məzmununu yeniləyir; isFavorite və statistika sahələrinə toxunmur. */
    @Query(
        "UPDATE questions SET sual = :sual, variantA = :variantA, variantB = :variantB, " +
            "variantC = :variantC, variantD = :variantD, duzgunCavabIndex = :duzgunCavabIndex, " +
            "izah = :izah, bolme = :bolme, paraqraf = :paraqraf, cetinlik = :cetinlik, " +
            "sualTipi = :sualTipi WHERE id = :id"
    )
    suspend fun updateContent(
        id: Int,
        sual: String,
        variantA: String,
        variantB: String,
        variantC: String,
        variantD: String,
        duzgunCavabIndex: Int,
        izah: String,
        bolme: Int,
        paraqraf: String,
        cetinlik: String,
        sualTipi: String
    )

    /** Fayldan silinmiş daxili sualları təmizləyir (istifadəçi suallarına toxunmur). */
    @Query("DELETE FROM questions WHERE id > :lastSeedId AND id < :customStart")
    suspend fun deleteBuiltInAfter(lastSeedId: Int, customStart: Int)

    @Query("SELECT MAX(id) FROM questions")
    suspend fun getMaxId(): Int?

    @Update
    suspend fun updateQuestion(question: QuestionEntity)

    @Query("UPDATE questions SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: Int, isFavorite: Boolean)

    @Query("UPDATE questions SET lastAnsweredCorrectly = :isCorrect, timesAnswered = timesAnswered + 1, timesCorrect = timesCorrect + (CASE WHEN :isCorrect THEN 1 ELSE 0 END) WHERE id = :id")
    suspend fun updateAnswerResult(id: Int, isCorrect: Boolean)

    @Query("UPDATE questions SET lastAnsweredCorrectly = NULL, timesAnswered = 0, timesCorrect = 0")
    suspend fun resetQuestionStats()

    @Query("SELECT COUNT(*) FROM questions")
    suspend fun getQuestionCount(): Int

    @Query("DELETE FROM questions")
    suspend fun deleteAllQuestions()
}
