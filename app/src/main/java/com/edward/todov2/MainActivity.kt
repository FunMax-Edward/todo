package com.edward.todov2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.edward.todov2.presentation.common.ViewModelFactory
import com.edward.todov2.presentation.problemgrid.ProblemGridScreen
import com.edward.todov2.presentation.settings.SettingsScreen
import com.edward.todov2.presentation.settings.SettingsViewModel
import com.edward.todov2.presentation.setup.SetupScreen
import com.edward.todov2.presentation.setup.SetupViewModel
import com.edward.todov2.presentation.statistics.StatisticsScreen
import com.edward.todov2.presentation.statistics.StatisticsViewModel
import com.edward.todov2.presentation.unitlist.HomeScreen
import com.edward.todov2.presentation.unitlist.UnitListViewModel
import com.edward.todov2.ui.theme.Todov2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Todov2Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainNavigation()
                }
            }
        }
    }
}

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Tabs definition
    val tabs = listOf("home" to "Grid", "setup" to "Setup", "stats" to "Stats", "settings" to "Settings")
    
    // Logic to hide bottom bar on detail screens
    val showBottomBar = currentRoute in tabs.map { it.first }

    // Create shared ViewModels at the NavHost level to ensure state persistence
    val unitListViewModel: UnitListViewModel = viewModel(factory = ViewModelFactory())
    val setupViewModel: SetupViewModel = viewModel(factory = ViewModelFactory())
    val statisticsViewModel: StatisticsViewModel = viewModel(factory = ViewModelFactory())
    val settingsViewModel: SettingsViewModel = viewModel(factory = ViewModelFactory())

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ) {
                    tabs.forEach { (route, title) ->
                        NavigationBarItem(
                            icon = {
                                when (route) {
                                    "home" -> Icon(Icons.Default.Home, contentDescription = title)
                                    "setup" -> Icon(Icons.Default.Add, contentDescription = title)
                                    "stats" -> Icon(Icons.Default.Info, contentDescription = title) // Placeholder for BarChart
                                    "settings" -> Icon(Icons.Default.Settings, contentDescription = title)
                                }
                            },
                            label = { Text(title) },
                            selected = currentRoute == route,
                            onClick = {
                                navController.navigate(route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = Color(0xFFE0E0E0),
                                selectedIconColor = Color.Black,
                                unselectedIconColor = Color.Gray,
                                selectedTextColor = Color.Black,
                                unselectedTextColor = Color.Gray
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            // 1. Grid / Home
            composable("home") {
                HomeScreen(
                    viewModel = unitListViewModel,
                    onUnitClick = { unitId ->
                        navController.navigate("problemGrid/$unitId")
                    },
                    onAddProjectClick = {
                        navController.navigate("setup")
                    }
                )
            }
            // 2. Setup
            composable("setup") {
                SetupScreen(
                    viewModel = setupViewModel
                )
            }
            // 3. Stats
            composable("stats") {
                StatisticsScreen(
                    viewModel = statisticsViewModel
                )
            }
            // 4. Settings
            composable("settings") {
                SettingsScreen(
                    viewModel = settingsViewModel
                )
            }
            
            // Detail Screen
            composable(
                "problemGrid/{unitId}",
                arguments = listOf(navArgument("unitId") { type = NavType.IntType })
            ) { backStackEntry ->
                val unitId = backStackEntry.arguments?.getInt("unitId") ?: 0
                ProblemGridScreen(
                    viewModel = viewModel(factory = ViewModelFactory(unitId = unitId)),
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
