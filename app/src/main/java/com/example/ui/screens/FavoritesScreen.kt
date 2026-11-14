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
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.components.ChipBadge
import com.example.ui.components.EmptyStateCard
import com.example.ui.components.ExplanationCard
import com.example.ui.components.FlightTopBar
import com.example.ui.viewmodel.QuizMode
import com.example.ui.viewmodel.QuizViewModel
import com.example.utils.PrintUtils

@Composable
fun FavoritesScreen(
    viewModel: QuizViewModel,
    onBackClick: () -> Unit,
    onStartTestWithCustomList: () -> Unit
) {
    val favorites by viewModel.favoriteQuestions.collectAsState(initial = emptyList())
    val context = LocalContext.current

    Scaffold(
        topBar = {
            FlightTopBar(
                title = "Seçilmiş Suallar",
                subtitle = "Ulduzladığınız ${favorites.size} sual",
                onBackClick = onBackClick,
                actions = {
                    if (favorites.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                com.example.utils.SoundManager.playClick()
                                PrintUtils.printQuestions(context, favorites, "Seçilmiş Suallar Siyahısı")
                            },
                            modifier = Modifier.testTag("print_favorites_topbar_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Print,
                                contentDescription = "Çap et / PDF Yüklə",
                                tint = Color.White
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .testTag("favorites_screen"),
            contentPadding = PaddingValues(16.dp)
        ) {
            if (favorites.isNotEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        Button(
                            onClick = {
                                com.example.utils.SoundManager.playClick()
                                viewModel.startQuiz(QuizMode.FAVORITES, favorites)
                                onStartTestWithCustomList()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("start_favorites_quiz_button")
                        ) {
                            Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Seçilmişlər üzrə Testə Başla (${favorites.size})")
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = {
                                com.example.utils.SoundManager.playClick()
                                PrintUtils.printQuestions(context, favorites, "Seçilmiş Suallar Siyahısı")
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("print_favorites_button")
                        ) {
                            Icon(imageVector = Icons.Filled.Print, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Sualları Çap Et / PDF kimi saxla (${favorites.size})")
                        }
                    }
                }
            }

            if (favorites.isEmpty()) {
                item {
                    EmptyStateCard(
                        title = "Seçilmiş sual yoxdur",
                        message = "Testlər zamanı vacib sualların sağ üst küncündəki ulduz düyməsinə toxunaraq onları bu siyahıya əlavə edə bilərsiniz.",
                        icon = Icons.Filled.Star
                    )
                }
            } else {
                items(favorites) { question ->
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
                                IconButton(onClick = { viewModel.toggleFavorite(question) }) {
                                    Icon(
                                        imageVector = Icons.Filled.Star,
                                        contentDescription = "Çıxar",
                                        tint = Color(0xFFEAB308)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
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
