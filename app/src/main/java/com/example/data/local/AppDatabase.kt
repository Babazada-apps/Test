package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [QuestionEntity::class, ExamResultEntity::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun questionDao(): QuestionDao
    abstract fun examResultDao(): ExamResultDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * 2 -> 3: sxem dəyişmir. Köhnə versiya hər açılışda sualları silib yenidən yazırdı,
         * ona görə ID-lər "sürüşmüşdü". Sualları təmizləyirik ki, sabit ID-lərlə yenidən yüklənsin.
         */
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("DELETE FROM questions")
                db.execSQL("DELETE FROM sqlite_sequence WHERE name = 'questions'")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "flight_ops_quiz_database"
                )
                    .addMigrations(MIGRATION_2_3)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
