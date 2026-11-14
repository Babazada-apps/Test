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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Score
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.components.EmptyStateCard
import com.example.ui.components.FlightTopBar
import com.example.ui.components.StatMetricCard
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.ErrorRedContainer
import com.example.ui.theme.SuccessGreen
import com.example.ui.viewmodel.QuizViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun StatisticsScreen(
    viewModel: QuizViewModel,
    onBackClick: () -> Unit
) {
    val examResults by viewModel.examResults.collectAsState(initial = emptyList())
    val allQuestions by viewModel.allQuestions.collectAsState(initial = emptyList())
    val wrongQuestions by viewModel.wrongQuestions.collectAsState(initial = emptyList())

    val totalExams = examResults.size
    val passedExams = examResults.count { it.passed }
    val averageScore = if (totalExams > 0) examResults.map { it.scorePercentage }.average().toInt() else 0
    val passRate = if (totalExams > 0) (passedExams * 100) / totalExams else 0

    Scaffold(
        topBar = {
            FlightTopBar(
                title = "Nəticələrin Statistikası",
                subtitle = "Ümumi performans və imtahan tarixçəsi",
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .testTag("statistics_screen"),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatMetricCard(
                        title = "Keçirilən İmtahan",
                        value = "$totalExams",
                        subtitle = "Müvəffəqiyyətli: $passedExams",
                        icon = Icons.Filled.Assignment,
                        accentColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    StatMetricCard(
                        title = "Orta Göstərici",
                        value = "$averageScore%",
                        subtitle = "Keçid Nisbəti: $passRate%",
                        icon = Icons.Filled.Score,
                        accentColor = SuccessGreen,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatMetricCard(
                        title = "Ümumi Suallar",
                        value = "${allQuestions.size}",
                        subtitle = "Təlimatın bütün bölmələri",
                        icon = Icons.Filled.Analytics,
                        accentColor = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.weight(1f)
                    )
                    StatMetricCard(
                        title = "Səhv Cavablar",
                        value = "${wrongQuestions.size}",
                        subtitle = "Təkrar tələb olunan",
                        icon = Icons.Filled.ErrorOutline,
                        accentColor = ErrorRed,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Keçirilmiş İmtahanların Tarixçəsi",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    if (totalExams > 0) {
                        Button(
                            onClick = { viewModel.resetStats() },
                            colors = ButtonDefaults.buttonColors(containerColor = ErrorRedContainer),
                            modifier = Modifier.testTag("reset_stats_button")
                        ) {
                            Icon(imageVector = Icons.Filled.Delete, contentDescription = "Təmizlə", tint = ErrorRed)
                            Text("Təmizlə", color = ErrorRed)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            if (examResults.isEmpty()) {
                item {
                    EmptyStateCard(
                        title = "Hələ ki, imtahan keçirilməyib",
                        message = "İmtahan rejimində və ya testlərdə nəticələriniz toplandıqca burada ətraflı statistikalarınız görünəcək.",
                        icon = Icons.Filled.Analytics
                    )
                }
            } else {
                items(examResults) { res ->
                    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("az"))
                    val dateString = sdf.format(Date(res.timestamp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = res.testMode,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "$dateString • ${res.correctAnswers} / ${res.totalQuestions} Düzgün",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Text(
                                text = "${res.scorePercentage}%",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = if (res.passed) SuccessGreen else ErrorRed
                            )
                        }
                    }
                }
            }
        }
    }
}
