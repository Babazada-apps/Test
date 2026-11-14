package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.ui.components.StatMetricCard
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.SuccessGreen
import com.example.ui.viewmodel.QuizMode
import com.example.ui.viewmodel.QuizViewModel

@Composable
fun HomeScreen(
    viewModel: QuizViewModel,
    onStartTest: (QuizMode) -> Unit,
    onNavigateToStats: () -> Unit,
    onNavigateToFavorites: () -> Unit,
    onNavigateToWrongAnswers: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToImport: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val allQuestions by viewModel.allQuestions.collectAsState(initial = emptyList())
    val favoriteQuestions by viewModel.favoriteQuestions.collectAsState(initial = emptyList())
    val wrongQuestions by viewModel.wrongQuestions.collectAsState(initial = emptyList())
    val examResults by viewModel.examResults.collectAsState(initial = emptyList())

    val totalCount = allQuestions.size
    val section1Count = allQuestions.count { it.bolme == 1 }
    val section2Count = allQuestions.count { it.bolme == 2 }
    val section3Count = allQuestions.count { it.bolme == 3 }
    val section4Count = allQuestions.count { it.bolme == 4 }
    val favoritesCount = favoriteQuestions.size
    val wrongCount = wrongQuestions.size

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            HeroHeaderBanner(
                onSettingsClick = onNavigateToSettings,
                onSearchClick = onNavigateToSearch
            )
        }

        item {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Sınaq və İmtahan Rejimini Seçin",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(12.dp))

                QuizModeCard(
                    title = "Sınaq İmtahanı (20 dəq)",
                    subtitle = "Təlimat üzrə real vaxt məhdudiyyətli rəsmi imtahan rejimi",
                    icon = Icons.Filled.Timer,
                    badgeText = "SINAQ",
                    accentColor = MaterialTheme.colorScheme.tertiary,
                    testTag = "mode_exam_button",
                    onClick = {
                        com.example.utils.SoundManager.playClick()
                        onStartTest(QuizMode.EXAM)
                    }
                )

                QuizModeCard(
                    title = "Bölmə 1 üzrə Test",
                    subtitle = "Uçuş istismarının əsasları ($section1Count sual)",
                    icon = Icons.AutoMirrored.Filled.MenuBook,
                    badgeText = "BÖLMƏ 1",
                    accentColor = MaterialTheme.colorScheme.primary,
                    testTag = "mode_section1_button",
                    onClick = {
                        com.example.utils.SoundManager.playClick()
                        onStartTest(QuizMode.SECTION_1)
                    }
                )

                QuizModeCard(
                    title = "Bölmə 2 üzrə Test",
                    subtitle = "Xüsusi hallarda və qəza prosedurları ($section2Count sual)",
                    icon = Icons.AutoMirrored.Filled.MenuBook,
                    badgeText = "BÖLMƏ 2",
                    accentColor = MaterialTheme.colorScheme.secondary,
                    testTag = "mode_section2_button",
                    onClick = {
                        com.example.utils.SoundManager.playClick()
                        onStartTest(QuizMode.SECTION_2)
                    }
                )

                QuizModeCard(
                    title = "Bölmə 3 üzrə Test",
                    subtitle = "Mühərriklər, sistemlər və avadanlıqlar ($section3Count sual)",
                    icon = Icons.AutoMirrored.Filled.MenuBook,
                    badgeText = "BÖLMƏ 3",
                    accentColor = Color(0xFF8B5CF6),
                    testTag = "mode_section3_button",
                    onClick = {
                        com.example.utils.SoundManager.playClick()
                        onStartTest(QuizMode.SECTION_3)
                    }
                )

                QuizModeCard(
                    title = "Bölmə 4 üzrə Test",
                    subtitle = "Uçuş istismarı və prosedurlar ($section4Count sual)",
                    icon = Icons.AutoMirrored.Filled.MenuBook,
                    badgeText = "BÖLMƏ 4",
                    accentColor = Color(0xFFE11D48),
                    testTag = "mode_section4_button",
                    onClick = {
                        com.example.utils.SoundManager.playClick()
                        onStartTest(QuizMode.SECTION_4)
                    }
                )

                QuizModeCard(
                    title = "Qarışıq Test",
                    subtitle = "Təlimatın bütün bölmələrindən təsadüfi suallar ($totalCount sual)",
                    icon = Icons.Filled.Quiz,
                    badgeText = "QARIŞIQ",
                    accentColor = Color(0xFF0284C7),
                    testTag = "mode_mixed_button",
                    onClick = {
                        com.example.utils.SoundManager.playClick()
                        onStartTest(QuizMode.MIXED)
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Təkrarlar və Alətlər",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatMetricCard(
                        title = "Səhv Cavablar",
                        value = "$wrongCount",
                        subtitle = "Ayrıca Təkrar Et",
                        icon = Icons.Filled.ErrorOutline,
                        accentColor = ErrorRed,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                com.example.utils.SoundManager.playClick()
                                onNavigateToWrongAnswers()
                            }
                    )

                    StatMetricCard(
                        title = "Seçilmişlər",
                        value = "$favoritesCount",
                        subtitle = "Ulduzlanan Suallar",
                        icon = Icons.Filled.Star,
                        accentColor = Color(0xFFEAB308),
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                com.example.utils.SoundManager.playClick()
                                onNavigateToFavorites()
                            }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatMetricCard(
                        title = "Statistika",
                        value = "${examResults.size}",
                        subtitle = "İmtahan Tarixçəsi",
                        icon = Icons.Filled.Analytics,
                        accentColor = SuccessGreen,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                com.example.utils.SoundManager.playClick()
                                onNavigateToStats()
                            }
                    )

                    StatMetricCard(
                        title = "Mətn Analizi",
                        value = "Sual Yarat",
                        subtitle = "Təlimat Mətnini Təhlil Et",
                        icon = Icons.Filled.AutoAwesome,
                        accentColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                com.example.utils.SoundManager.playClick()
                                onNavigateToImport()
                            }
                    )
                }
            }
        }
    }
}

@Composable
fun HeroHeaderBanner(
    onSettingsClick: () -> Unit,
    onSearchClick: () -> Unit
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(210.dp)
            .background(MaterialTheme.colorScheme.primary)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data("file:///android_asset/hero_aviation.png")
                .crossfade(true)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.45f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.9f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.FlightTakeoff,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "OFFLINE TEST V1.0",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                }

                Row {
                    IconButton(onClick = onSearchClick) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Axtarış",
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Tənzimləmələr",
                            tint = Color.White
                        )
                    }
                }
            }

            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White.copy(alpha = 0.25f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.7f)),
                        modifier = Modifier.padding(bottom = 6.dp)
                    ) {
                        Box(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_mi17_helicopter),
                                contentDescription = "Ümumi Aviasiya Helikopteri",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }

                Text(
                    text = "Aviasiya Bilikləri üzrə Təlimat",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    ),
                    color = Color.White
                )
                Text(
                    text = "İmtahan və Biliklərin Yoxlanılması Sistemi",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
        }
    }
}

@Composable
fun QuizModeCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    badgeText: String,
    accentColor: Color,
    testTag: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .clickable { onClick() }
            .testTag(testTag),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = "Başla",
                tint = accentColor
            )
        }
    }
}
