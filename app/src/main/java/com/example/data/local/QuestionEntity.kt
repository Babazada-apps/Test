package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "questions")
data class QuestionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val sual: String,
    val variantA: String,
    val variantB: String,
    val variantC: String,
    val variantD: String,
    val duzgunCavabIndex: Int, // 0: A, 1: B, 2: C, 3: D
    val izah: String,
    val bolme: Int, // 1 or 2
    val paraqraf: String, // e.g. "§1.1"
    val cetinlik: String, // "Asan", "Orta", "Çətin"
    val sualTipi: String, // "4 Variantlı", "Doğru / Səhv", "Boşluğu Doldur", "Hansı Səhvdir?", "Situasiya"
    val isFavorite: Boolean = false,
    val lastAnsweredCorrectly: Boolean? = null,
    val timesAnswered: Int = 0,
    val timesCorrect: Int = 0
)
