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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.QuestionEntity
import com.example.ui.components.ChipBadge
import com.example.ui.components.ExplanationCard
import com.example.ui.components.FlightTopBar
import com.example.ui.viewmodel.QuizViewModel
import kotlinx.coroutines.launch

@Composable
fun TextImportScreen(
    viewModel: QuizViewModel,
    onBackClick: () -> Unit
) {
    var textInput by remember { mutableStateOf("") }
    var selectedSection by remember { mutableIntStateOf(1) }
    var generatedQuestions by remember { mutableStateOf<List<QuestionEntity>>(emptyList()) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            FlightTopBar(
                title = "Təlimat Mətni Analizi",
                subtitle = "Mətni daxil edin və avtomatik suallar hazırlayın",
                onBackClick = onBackClick
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .testTag("text_import_screen"),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                Text(
                    text = "Aşağıdakı xanaya Aviasiya Bilikləri üzrə Təlimatın mətnini daxil edin:",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .testTag("import_text_input"),
                    placeholder = {
                        Text(
                            "Məsələn: Uçuş zamanı kabində təzyiqin düşməsi zamanı heyət dərhal oksigen maskalarını taxmalıdır. Kapitan uçuş təhlükəsizliyinə birbaşa cavabdehdir."
                        )
                    },
                    shape = RoundedCornerShape(16.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        FilterChip(
                            selected = selectedSection == 1,
                            onClick = { selectedSection = 1 },
                            label = { Text("B1") }
                        )
                        FilterChip(
                            selected = selectedSection == 2,
                            onClick = { selectedSection = 2 },
                            label = { Text("B2") }
                        )
                        FilterChip(
                            selected = selectedSection == 3,
                            onClick = { selectedSection = 3 },
                            label = { Text("B3") }
                        )
                        FilterChip(
                            selected = selectedSection == 4,
                            onClick = { selectedSection = 4 },
                            label = { Text("B4") }
                        )
                    }

                    Button(
                        onClick = {
                            if (textInput.isNotBlank()) {
                                generatedQuestions = viewModel.parseTextToQuestions(textInput, selectedSection)
                                if (generatedQuestions.isEmpty()) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Mətndən sual yaradıla bilmədi. JSON formatını və ya mətnin uzunluğunu yoxlayın.")
                                    }
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        modifier = Modifier.testTag("analyze_text_button")
                    ) {
                        Icon(imageVector = Icons.Filled.AutoAwesome, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Təhlil et")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            if (generatedQuestions.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Hazırlanmış Suallar (${generatedQuestions.size}):",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        Button(
                            onClick = {
                                viewModel.addCustomQuestions(generatedQuestions)
                                generatedQuestions = emptyList()
                                scope.launch {
                                    snackbarHostState.showSnackbar("Suallar bazaya uğurla əlavə olundu!")
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.testTag("save_generated_questions_button")
                        ) {
                            Icon(imageVector = Icons.Filled.Save, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Bazaya Yadda Saxla")
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                items(generatedQuestions) { question ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                ChipBadge(text = "Bölmə ${question.bolme}", color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(6.dp))
                                ChipBadge(text = question.paraqraf, color = MaterialTheme.colorScheme.secondary)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = question.sual,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            ExplanationCard(explanation = question.izah)
                        }
                    }
                }
            }
        }
    }
}
