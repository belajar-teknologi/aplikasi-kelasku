package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.*
import com.example.ui.theme.AsistenGuruTheme
import com.example.ui.viewmodel.AppNavDestination
import com.example.ui.viewmodel.TeacherViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AsistenGuruTheme {
                TeacherAppRoot()
            }
        }
    }
}

data class NavItem(
    val destination: AppNavDestination,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@Composable
fun TeacherAppRoot(viewModel: TeacherViewModel = viewModel()) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()

    val navItems = listOf(
        NavItem(
            destination = AppNavDestination.DASHBOARD,
            label = "Dasbor",
            selectedIcon = Icons.Filled.Dashboard,
            unselectedIcon = Icons.Outlined.Dashboard
        ),
        NavItem(
            destination = AppNavDestination.ATTENDANCE,
            label = "Presensi",
            selectedIcon = Icons.Filled.FactCheck,
            unselectedIcon = Icons.Outlined.FactCheck
        ),
        NavItem(
            destination = AppNavDestination.GRADES,
            label = "Nilai",
            selectedIcon = Icons.Filled.Assessment,
            unselectedIcon = Icons.Outlined.Assessment
        ),
        NavItem(
            destination = AppNavDestination.QUIZ,
            label = "Kuis",
            selectedIcon = Icons.Filled.Quiz,
            unselectedIcon = Icons.Outlined.Quiz
        ),
        NavItem(
            destination = AppNavDestination.HOMEWORK,
            label = "Tugas",
            selectedIcon = Icons.Filled.Assignment,
            unselectedIcon = Icons.Outlined.Assignment
        ),
        NavItem(
            destination = AppNavDestination.SCHEDULE,
            label = "Jadwal",
            selectedIcon = Icons.Filled.CalendarMonth,
            unselectedIcon = Icons.Outlined.CalendarMonth
        ),
        NavItem(
            destination = AppNavDestination.CHAT,
            label = "Chat",
            selectedIcon = Icons.Filled.Chat,
            unselectedIcon = Icons.Outlined.Chat
        ),
        NavItem(
            destination = AppNavDestination.REPORT,
            label = "Laporan",
            selectedIcon = Icons.Filled.Description,
            unselectedIcon = Icons.Outlined.Description
        )
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                tonalElevation = 6.dp,
                modifier = Modifier.testTag("main_navigation_bar")
            ) {
                // Show primary 5 tabs directly on compact bottom bar, other tabs accessible from Quick Actions / Navigation
                val primaryNavItems = listOf(
                    navItems[0], // Dasbor
                    navItems[1], // Presensi
                    navItems[2], // Nilai
                    navItems[3], // Kuis
                    navItems[4], // Tugas
                )

                primaryNavItems.forEach { item ->
                    val isSelected = currentScreen == item.destination
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.navigateTo(item.destination) },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.label
                            )
                        },
                        label = { Text(item.label) },
                        modifier = Modifier.testTag("nav_item_${item.destination.name.lowercase()}")
                    )
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Crossfade(
            targetState = currentScreen,
            modifier = Modifier.padding(innerPadding),
            label = "ScreenTransition"
        ) { screen ->
            when (screen) {
                AppNavDestination.DASHBOARD -> DashboardScreen(viewModel = viewModel)
                AppNavDestination.ATTENDANCE -> AttendanceScreen(viewModel = viewModel)
                AppNavDestination.GRADES -> GradesScreen(viewModel = viewModel)
                AppNavDestination.QUIZ -> QuizScreen(viewModel = viewModel)
                AppNavDestination.HOMEWORK -> HomeworkScreen(viewModel = viewModel)
                AppNavDestination.SCHEDULE -> ScheduleScreen(viewModel = viewModel)
                AppNavDestination.CHAT -> ChatScreen(viewModel = viewModel)
                AppNavDestination.REPORT -> ReportScreen(viewModel = viewModel)
            }
        }
    }
}
