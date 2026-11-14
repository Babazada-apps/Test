package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.QuestionEntity
import com.example.ui.components.AnswerOptionItem
import com.example.ui.components.FlightTopBar
import com.example.ui.components.QuestionHeaderCard
import com.example.ui.viewmodel.QuizViewModel
import java.util.Locale

@Composable
fun ExamScreen(
    viewModel: QuizViewModel,
    onBackClick: () -> Unit,
    onExamSubmitted: () -> Unit
) {
    val activeState by viewModel.activeQuiz.collectAsState()
    val questions = activeState.questions
    val currentIndex = activeState.currentIndex.coerceIn(0, (questions.size - 1).coerceAtLeast(0))

    var showSubmitDialog by remember { mutableStateOf(false) }

    if (questions.isEmpty()) return

    val currentQuestion = questions[currentIndex]
    val selectedOptionIndex = activeState.userAnswers[currentQuestion.id]

    val minutes = activeState.remainingSeconds / 60
    val seconds = activeState.remainingSeconds % 60
    val timeFormatted = String.format(Locale.US, "%02d:%02d", minutes, seconds)
    val isTimerLow = activeState.remainingSeconds < 300

    Scaffold(
        topBar = {
            Column {
                FlightTopBar(
                    title = "Sınaq İmtahanı",
                    subtitle = "Cavablandırılıb: ${activeState.userAnswers.size} / ${questions.size}",
                    onBackClick = onBackClick,
                    actions = {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isTimerLow) Color(0xFFEF4444) else MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.padding(end = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Timer,
                                    contentDescription = "Geri sayım",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = timeFormatted,
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                            }
                        }
                    }
                )
                LinearProgressIndicator(
                    progress = { activeState.userAnswers.size.toFloat() / questions.size.toFloat() },
                    modifier = Modifier.fillMaxWidth().height(4.dp),
                    color = MaterialTheme.colorScheme.tertiary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                item {
                    QuestionHeaderCard(
                        question = currentQuestion,
                        currentIndex = currentIndex,
                        totalCount = questions.size,
                        onToggleFavorite = { viewModel.toggleFavorite(currentQuestion) }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    val options = listOf(
                        Pair("A", currentQuestion.variantA),
                        Pair("B", currentQuestion.variantB),
                        Pair("C", currentQuestion.variantC),
                        Pair("D", currentQuestion.variantD)
                    )

                    options.forEachIndexed { optIdx, pair ->
                        AnswerOptionItem(
                            optionLabel = pair.first,
                            optionText = pair.second,
                            isSelected = selectedOptionIndex == optIdx,
                            isCorrect = null,
                            isSubmitted = false,
                            onSelect = {
                                com.example.utils.SoundManager.playClick()
                                viewModel.selectOption(currentQuestion.id, optIdx)
                            }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Suallara cəld keçid:",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    ExamQuestionGrid(
                        questions = questions,
                        userAnswers = activeState.userAnswers,
                        currentIndex = currentIndex,
                        onSelectQuestion = {
                            com.example.utils.SoundManager.playClick()
                            viewModel.goToQuestion(it)
                        }
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = {
                        com.example.utils.SoundManager.playClick()
                        viewModel.previousQuestion()
                    },
                    enabled = currentIndex > 0,
                    modifier = Modifier.testTag("exam_prev_button")
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Əvvəlki")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Əvvəlki")
                }

                Button(
                    onClick = {
                        com.example.utils.SoundManager.playClick()
                        showSubmitDialog = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.testTag("exam_submit_trigger_button")
                ) {
                    Text("İmtahanı Bitir")
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(imageVector = Icons.Filled.Check, contentDescription = "Təsdiqlə")
                }

                Button(
                    onClick = {
                        com.example.utils.SoundManager.playClick()
                        viewModel.nextQuestion()
                    },
                    enabled = currentIndex < questions.size - 1,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    modifier = Modifier.testTag("exam_next_button")
                ) {
                    Text("Növbəti")
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Növbəti")
                }
            }
        }
    }

    if (showSubmitDialog) {
        val answeredCount = activeState.userAnswers.size
        val totalCount = questions.size

        AlertDialog(
            onDismissRequest = {
                com.example.utils.SoundManager.playClick()
                showSubmitDialog = false
            },
            title = { Text("İmtahanı bitirmək istəyirsiniz?") },
            text = {
                Text(
                    "Siz $totalCount sualdan $answeredCount dənəsini cavablandırmısınız. İmtahanı təhvil verdikdən sonra nəticələriniz hesablanacaq."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        com.example.utils.SoundManager.playClick()
                        showSubmitDialog = false
                        viewModel.submitQuiz()
                        onExamSubmitted()
                    },
                    modifier = Modifier.testTag("confirm_submit_exam_button")
                ) {
                    Text("Bəli, Təhvil Ver")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        com.example.utils.SoundManager.playClick()
                        showSubmitDialog = false
                    }
                ) {
                    Text("Davam Et")
                }
            }
        )
    }
}

@Composable
fun ExamQuestionGrid(
    questions: List<QuestionEntity>,
    userAnswers: Map<Int, Int>,
    currentIndex: Int,
    onSelectQuestion: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            questions.take(10).forEachIndexed { idx, q ->
                val isAnswered = userAnswers.containsKey(q.id)
                val isCurrent = idx == currentIndex

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isCurrent -> MaterialTheme.colorScheme.secondary
                                isAnswered -> MaterialTheme.colorScheme.primaryContainer
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            }
                        )
                        .clickable { onSelectQuestion(idx) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${idx + 1}",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = when {
                            isCurrent -> Color.White
                            isAnswered -> MaterialTheme.colorScheme.onPrimaryContainer
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }
        }
    }
}
