package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.components.ChipBadge
import com.example.ui.components.DifficultyChip
import com.example.ui.components.EmptyStateCard
import com.example.ui.components.ExplanationCard
import com.example.ui.components.FlightTopBar
import com.example.ui.viewmodel.QuizMode
import com.example.ui.viewmodel.QuizViewModel

@Composable
fun WrongAnswersScreen(
    viewModel: QuizViewModel,
    onBackClick: () -> Unit,
    onStartTestWithCustomList: () -> Unit
) {
    val wrongQuestions by viewModel.wrongQuestions.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            FlightTopBar(
                title = "Səhv Cavabların Təkrarı",
                subtitle = "Əvvəlki sınaqlarda səhv edilən ${wrongQuestions.size} sual",
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .testTag("wrong_answers_screen"),
            contentPadding = PaddingValues(16.dp)
        ) {
            if (wrongQuestions.isNotEmpty()) {
                item {
                    Button(
                        onClick = {
                            viewModel.startQuiz(QuizMode.WRONG_ANSWERS, wrongQuestions)
                            onStartTestWithCustomList()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .testTag("start_wrong_quiz_button")
                    ) {
                        Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Səhvləri Təkrar Et və Məşq Et (${wrongQuestions.size})")
                    }
                }
            }

            if (wrongQuestions.isEmpty()) {
                item {
                    EmptyStateCard(
                        title = "Səhv cavab verilmiş sual yoxdur!",
                        message = "Sizin aktiv səhv cavabınız qalmayıb. Bütün suallara mükəmməl cavab vermisiniz!",
                        icon = Icons.Filled.ErrorOutline
                    )
                }
            } else {
                items(wrongQuestions) { question ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    ChipBadge(text = "Bölmə ${question.bolme}", color = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    ChipBadge(text = question.paraqraf, color = MaterialTheme.colorScheme.secondary)
                                }
                                DifficultyChip(difficulty = question.cetinlik)
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = question.sual,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            ExplanationCard(explanation = question.izah, question = question)
                        }
                    }
                }
            }
        }
    }
}
