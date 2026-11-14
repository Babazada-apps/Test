package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.ExamResultEntity
import com.example.data.local.InitialData
import com.example.data.local.QuestionEntity
import com.example.data.repository.AppThemeMode
import com.example.data.repository.QuizRepository
import com.example.data.repository.UserPreferencesRepository
import com.example.utils.QuestionParser
import com.example.utils.QuizLogic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class QuizMode(val displayName: String, val isExamStyle: Boolean) {
    SECTION_1("Bölmə 1 üzrə test", false),
    SECTION_2("Bölmə 2 üzrə test", false),
    SECTION_3("Bölmə 3 üzrə test", false),
    SECTION_4("Bölmə 4 üzrə test", false),
    MIXED("Qarışıq test", false),
    EXAM("İmtahan rejimi", true),
    WRONG_ANSWERS("Səhv cavabların təkrarı", false),
    FAVORITES("Seçilmişlər", false)
}

data class ActiveQuizState(
    val mode: QuizMode = QuizMode.MIXED,
    val questions: List<QuestionEntity> = emptyList(),
    val currentIndex: Int = 0,
    val userAnswers: Map<Int, Int> = emptyMap(), // questionId -> selectedIndex
    val isSubmitted: Boolean = false,
    val remainingSeconds: Long = QuizLogic.EXAM_DURATION_SECONDS, // 20 mins for exam mode
    val endTimeMillis: Long = 0L,
    val isTimerRunning: Boolean = false,
    val lastResult: ExamResultEntity? = null
)

class QuizViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val repository = QuizRepository(db.questionDao(), db.examResultDao())
    private val prefsRepository = UserPreferencesRepository(application)

    val themeMode: StateFlow<AppThemeMode> = prefsRepository.themeMode

    val allQuestions = repository.allQuestions
    val favoriteQuestions = repository.favoriteQuestions
    val wrongQuestions = repository.wrongQuestions
    val examResults = repository.examResults

    private val _activeQuiz = MutableStateFlow(ActiveQuizState())
    val activeQuiz: StateFlow<ActiveQuizState> = _activeQuiz.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<QuestionEntity>>(emptyList())
    val searchResults: StateFlow<List<QuestionEntity>> = _searchResults.asStateFlow()

    private var timerJob: Job? = null

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val needSync = prefsRepository.getSeedVersion() != InitialData.SEED_VERSION ||
                    repository.getQuestionCount() == 0
                if (needSync) {
                    repository.syncBuiltInQuestions(InitialData.getInitialQuestions(getApplication<Application>()))
                    prefsRepository.setSeedVersion(InitialData.SEED_VERSION)
                }
            }
        }
    }

    fun setThemeMode(mode: AppThemeMode) {
        prefsRepository.setThemeMode(mode)
    }

    fun startQuiz(mode: QuizMode, customQuestions: List<QuestionEntity>? = null) {
        viewModelScope.launch {
            val questionsToUse = if (customQuestions != null && customQuestions.isNotEmpty()) {
                customQuestions
            } else {
                when (mode) {
                    QuizMode.SECTION_1 -> repository.getQuestionsBySection(1).first().shuffled()
                    QuizMode.SECTION_2 -> repository.getQuestionsBySection(2).first().shuffled()
                    QuizMode.SECTION_3 -> repository.getQuestionsBySection(3).first().shuffled()
                    QuizMode.SECTION_4 -> repository.getQuestionsBySection(4).first().shuffled()
                    QuizMode.MIXED -> repository.allQuestions.first().shuffled()
                    QuizMode.EXAM -> repository.allQuestions.first().shuffled().take(QuizLogic.EXAM_QUESTION_COUNT)
                    QuizMode.WRONG_ANSWERS -> repository.wrongQuestions.first().shuffled()
                    QuizMode.FAVORITES -> repository.favoriteQuestions.first()
                }
            }

            // Variantların yerini qarışdır (düzgün cavab indeksi də buna uyğun yenilənir)
            val preparedQuestions = questionsToUse.map { QuizLogic.shuffleOptions(it) }

            timerJob?.cancel()
            _activeQuiz.value = ActiveQuizState(
                mode = mode,
                questions = preparedQuestions,
                currentIndex = 0,
                userAnswers = emptyMap(),
                isSubmitted = false,
                remainingSeconds = if (mode == QuizMode.EXAM) QuizLogic.EXAM_DURATION_SECONDS else 0L,
                endTimeMillis = if (mode == QuizMode.EXAM) {
                    System.currentTimeMillis() + QuizLogic.EXAM_DURATION_SECONDS * 1000L
                } else 0L,
                isTimerRunning = mode == QuizMode.EXAM
            )

            if (mode == QuizMode.EXAM) {
                startTimer()
            }
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_activeQuiz.value.isTimerRunning && !_activeQuiz.value.isSubmitted) {
                val remaining = QuizLogic.remainingSeconds(
                    _activeQuiz.value.endTimeMillis,
                    System.currentTimeMillis()
                )
                _activeQuiz.value = _activeQuiz.value.copy(remainingSeconds = remaining)
                if (remaining <= 0L) {
                    submitQuiz()
                    break
                }
                delay(500L)
            }
        }
    }

    fun selectOption(questionId: Int, optionIndex: Int) {
        if (_activeQuiz.value.isSubmitted && _activeQuiz.value.mode.isExamStyle) return

        val currentAnswers = _activeQuiz.value.userAnswers.toMutableMap()
        currentAnswers[questionId] = optionIndex
        _activeQuiz.value = _activeQuiz.value.copy(userAnswers = currentAnswers)

        // In practice modes, record answer immediately
        if (!_activeQuiz.value.mode.isExamStyle) {
            val q = _activeQuiz.value.questions.find { it.id == questionId }
            if (q != null) {
                val isCorrect = (optionIndex == q.duzgunCavabIndex)
                viewModelScope.launch {
                    repository.recordAnswer(questionId, isCorrect)
                }
            }
        }
    }

    fun goToQuestion(index: Int) {
        val total = _activeQuiz.value.questions.size
        if (index in 0 until total) {
            _activeQuiz.value = _activeQuiz.value.copy(currentIndex = index)
        }
    }

    fun nextQuestion() {
        goToQuestion(_activeQuiz.value.currentIndex + 1)
    }

    fun previousQuestion() {
        goToQuestion(_activeQuiz.value.currentIndex - 1)
    }

    fun toggleFavorite(question: QuestionEntity) {
        viewModelScope.launch {
            val newFav = !question.isFavorite
            repository.toggleFavorite(question.id, newFav)

            // Update local state list as well
            val updatedList = _activeQuiz.value.questions.map {
                if (it.id == question.id) it.copy(isFavorite = newFav) else it
            }
            _activeQuiz.value = _activeQuiz.value.copy(questions = updatedList)
        }
    }

    fun submitQuiz() {
        timerJob?.cancel()
        val currentState = _activeQuiz.value
        val questions = currentState.questions
        val userAnswers = currentState.userAnswers

        var correctCount = 0
        questions.forEach { q ->
            val selected = userAnswers[q.id]
            val isCorrect = (selected == q.duzgunCavabIndex)
            if (isCorrect) correctCount++

            // Save record in database
            if (selected != null) {
                viewModelScope.launch {
                    repository.recordAnswer(q.id, isCorrect)
                }
            }
        }

        val total = questions.size
        val percentage = QuizLogic.calculatePercentage(correctCount, total)
        val passed = QuizLogic.isPassed(percentage)
        val timeSpent = (QuizLogic.EXAM_DURATION_SECONDS - currentState.remainingSeconds).coerceAtLeast(0L)

        val resultEntity = ExamResultEntity(
            testMode = currentState.mode.displayName,
            totalQuestions = total,
            correctAnswers = correctCount,
            scorePercentage = percentage,
            passed = passed,
            durationSeconds = if (currentState.mode == QuizMode.EXAM) timeSpent else 0L
        )

        viewModelScope.launch {
            repository.saveExamResult(resultEntity)
        }

        _activeQuiz.value = currentState.copy(
            isSubmitted = true,
            isTimerRunning = false,
            lastResult = resultEntity
        )
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }
        viewModelScope.launch {
            repository.searchQuestions(query).collect { list ->
                _searchResults.value = list
            }
        }
    }

    fun addCustomQuestions(questions: List<QuestionEntity>) {
        viewModelScope.launch {
            repository.insertCustomQuestions(questions)
        }
    }

    fun resetStats() {
        viewModelScope.launch {
            repository.resetStatistics()
        }
    }

    fun parseTextToQuestions(text: String, section: Int): List<QuestionEntity> {
        return QuestionParser.parse(text, section)
    }
}
