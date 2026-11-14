package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.ExamResultScreen
import com.example.ui.screens.ExamScreen
import com.example.ui.screens.FavoritesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.StatisticsScreen
import com.example.ui.screens.TestScreen
import com.example.ui.screens.TextImportScreen
import com.example.ui.screens.WrongAnswersScreen
import com.example.ui.theme.FlightOpsTheme
import com.example.ui.viewmodel.QuizMode
import com.example.ui.viewmodel.QuizViewModel

class MainActivity : ComponentActivity() {
    private val quizViewModel: QuizViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        com.example.utils.SoundManager.init(applicationContext)
        enableEdgeToEdge()

        setContent {
            val currentTheme by quizViewModel.themeMode.collectAsState()

            FlightOpsTheme(appThemeMode = currentTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FlightOpsApp(viewModel = quizViewModel)
                }
            }
        }
    }
}

@Composable
fun FlightOpsApp(viewModel: QuizViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                viewModel = viewModel,
                onStartTest = { mode ->
                    viewModel.startQuiz(mode)
                    if (mode == QuizMode.EXAM) {
                        navController.navigate("exam")
                    } else {
                        navController.navigate("test")
                    }
                },
                onNavigateToStats = { navController.navigate("statistics") },
                onNavigateToFavorites = { navController.navigate("favorites") },
                onNavigateToWrongAnswers = { navController.navigate("wrong_answers") },
                onNavigateToSearch = { navController.navigate("search") },
                onNavigateToImport = { navController.navigate("text_import") },
                onNavigateToSettings = { navController.navigate("settings") }
            )
        }

        composable("test") {
            TestScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onFinishQuiz = { navController.navigate("exam_result") }
            )
        }

        composable("exam") {
            ExamScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onExamSubmitted = { navController.navigate("exam_result") }
            )
        }

        composable("exam_result") {
            ExamResultScreen(
                viewModel = viewModel,
                onHomeClick = { navController.popBackStack("home", inclusive = false) },
                onRetryClick = {
                    val currentMode = viewModel.activeQuiz.value.mode
                    viewModel.startQuiz(currentMode)
                    if (currentMode == QuizMode.EXAM) {
                        navController.navigate("exam") {
                            popUpTo("home")
                        }
                    } else {
                        navController.navigate("test") {
                            popUpTo("home")
                        }
                    }
                }
            )
        }

        composable("statistics") {
            StatisticsScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("favorites") {
            FavoritesScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onStartTestWithCustomList = { navController.navigate("test") }
            )
        }

        composable("wrong_answers") {
            WrongAnswersScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onStartTestWithCustomList = { navController.navigate("test") }
            )
        }

        composable("search") {
            SearchScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("text_import") {
            TextImportScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("settings") {
            SettingsScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
