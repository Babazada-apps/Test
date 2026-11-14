package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exam_results")
data class ExamResultEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val testMode: String, // "Bölmə 1", "Bölmə 2", "Qarışıq Test", "İmtahan Rejimi", "Səhvlər Təkrarı"
    val totalQuestions: Int,
    val correctAnswers: Int,
    val scorePercentage: Int,
    val passed: Boolean,
    val durationSeconds: Long,
    val timestamp: Long = System.currentTimeMillis()
)
