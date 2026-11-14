package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.components.AnswerOptionItem
import com.example.ui.components.EmptyStateCard
import com.example.ui.components.ExplanationCard
import com.example.ui.components.FlightTopBar
import com.example.ui.components.QuestionHeaderCard
import com.example.ui.viewmodel.QuizViewModel

@Composable
fun TestScreen(
    viewModel: QuizViewModel,
    onBackClick: () -> Unit,
    onFinishQuiz: () -> Unit
) {
    val activeState by viewModel.activeQuiz.collectAsState()
    val questions = activeState.questions

    if (questions.isEmpty()) {
        Scaffold(
            topBar = {
                FlightTopBar(
                    title = activeState.mode.displayName,
                    onBackClick = onBackClick
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                EmptyStateCard(
                    title = "Sual tapılmadı",
                    message = "Seçilmiş rejimdə hələlik sual mövcud deyil və ya bütün səhv cavablar uğurla cavablandırılıb.",
                    icon = Icons.Filled.HelpOutline
                )
            }
        }
        return
    }

    val currentIndex = activeState.currentIndex.coerceIn(0, questions.size - 1)
    val currentQuestion = questions[currentIndex]
    val selectedOptionIndex = activeState.userAnswers[currentQuestion.id]
    val isAnswered = selectedOptionIndex != null

    Scaffold(
        topBar = {
            Column {
                FlightTopBar(
                    title = activeState.mode.displayName,
                    subtitle = "Sual ${currentIndex + 1} / ${questions.size}",
                    onBackClick = onBackClick
                )
                LinearProgressIndicator(
                    progress = { (currentIndex + 1).toFloat() / questions.size.toFloat() },
                    modifier = Modifier.fillMaxWidth().height(4.dp),
                    color = MaterialTheme.colorScheme.secondary,
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
                            isCorrect = if (isAnswered) optIdx == currentQuestion.duzgunCavabIndex else null,
                            isSubmitted = isAnswered,
                            onSelect = {
                                if (!isAnswered) {
                                    if (optIdx == currentQuestion.duzgunCavabIndex) {
                                        com.example.utils.SoundManager.playCorrect()
                                    } else {
                                        com.example.utils.SoundManager.playWrong()
                                    }
                                } else {
                                    com.example.utils.SoundManager.playClick()
                                }
                                viewModel.selectOption(currentQuestion.id, optIdx)
                            }
                        )
                    }
                }

                if (isAnswered) {
                    item {
                        ExplanationCard(explanation = currentQuestion.izah, question = currentQuestion)
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }

            // Bottom Navigation controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = {
                        com.example.utils.SoundManager.playClick()
                        viewModel.previousQuestion()
                    },
                    enabled = currentIndex > 0,
                    modifier = Modifier.testTag("test_prev_button")
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Əvvəlki")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Əvvəlki")
                }

                if (currentIndex == questions.size - 1) {
                    Button(
                        onClick = {
                            com.example.utils.SoundManager.playClick()
                            viewModel.submitQuiz()
                            onFinishQuiz()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.testTag("test_finish_button")
                    ) {
                        Text("Yekunlaşdır")
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(imageVector = Icons.Filled.Check, contentDescription = "Yekunlaşdır")
                    }
                } else {
                    Button(
                        onClick = {
                            com.example.utils.SoundManager.playClick()
                            viewModel.nextQuestion()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.testTag("test_next_button")
                    ) {
                        Text("Növbəti")
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Növbəti")
                    }
                }
            }
        }
    }
}
