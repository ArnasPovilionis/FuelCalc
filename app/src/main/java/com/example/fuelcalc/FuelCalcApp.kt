package com.example.fuelcalc

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch

// Programėlės puslapiai: adresas, pavadinimas ir ikona apatinėje juostoje
enum class Screen(val route: String, val title: String, val icon: ImageVector) {
    Calculator("calculator", "Skaičiuoklė", Icons.Filled.Home),
    History("history", "Istorija", Icons.AutoMirrored.Filled.List),
    About("about", "Apie", Icons.Filled.Info)
}

// Programėlės karkasas: viršutinė juosta, apatinė navigacija, puslapiai ir pranešimai
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FuelCalcApp(
    viewModel: FuelViewModel,
    widthSizeClass: WindowWidthSizeClass
) {
    // Valdo perėjimus tarp puslapių
    val navController = rememberNavController()
    // Kuris puslapis dabar atidarytas
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = Screen.entries.find { it.route == backStackEntry?.destination?.route }
        ?: Screen.Calculator

    // Snackbar pranešimų valdymas
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    // Parodo pranešimą apačioje (prieš tai paslepia seną)
    val showSnackbar: (String) -> Unit = { message ->
        scope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(message)
        }
    }

    // Ar atidarytas trijų taškų meniu
    var menuExpanded by remember { mutableStateOf(false) }
    // Ar rodomas langas „Autorius“
    var showAuthorDialog by remember { mutableStateOf(false) }

    // Ekrano karkasas: viršus, apačia, pranešimai ir turinys
    Scaffold(
        // Viršutinė juosta
        topBar = {
            CenterAlignedTopAppBar(
                // Pavadinimas centre: „FUELCALC“ ir puslapio pavadinimas vienas po kito
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "FUELCALC",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Text(currentScreen.title, style = MaterialTheme.typography.titleLarge)
                    }
                },
                // Juostos spalvos
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                // Dešinėje: trijų taškų mygtukas ir meniu
                actions = {
                    // Trijų taškų mygtukas – atidaro meniu
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "Meniu")
                    }
                    // Išskleidžiamas meniu
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        // Meniu punktas „Autorius“ – uždaro meniu ir atidaro langą
                        DropdownMenuItem(
                            text = { Text("Autorius") },
                            leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
                            onClick = {
                                menuExpanded = false
                                showAuthorDialog = true
                            }
                        )
                    }
                }
            )
        },
        // Apatinė navigacijos juosta
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                tonalElevation = 0.dp
            ) {
                // Po vieną mygtuką kiekvienam puslapiui
                Screen.entries.forEach { screen ->
                    NavigationBarItem(
                        selected = currentScreen == screen,
                        // Paspaudus – pereina į tą puslapį
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        // Ikona; prie „Istorija“ – ženkliukas su įrašų skaičiumi
                        icon = {
                            if (screen == Screen.History && viewModel.history.isNotEmpty()) {
                                BadgedBox(badge = { Badge { Text("${viewModel.history.size}") } }) {
                                    Icon(screen.icon, contentDescription = screen.title)
                                }
                            } else {
                                Icon(screen.icon, contentDescription = screen.title)
                            }
                        },
                        // Užrašas po ikona
                        label = { Text(screen.title) },
                        // Pasirinkto mygtuko spalvos
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            selectedTextColor = MaterialTheme.colorScheme.secondary,
                            indicatorColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    )
                }
            }
        },
        // Vieta Snackbar pranešimams
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    shape = MaterialTheme.shapes.medium,
                    containerColor = MaterialTheme.colorScheme.inverseSurface,
                    contentColor = MaterialTheme.colorScheme.inverseOnSurface
                )
            }
        },
        // Fono spalva
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        // Puslapių sąrašas: rodo tą, kuris pasirinktas (pradžioje – skaičiuoklė)
        NavHost(
            navController = navController,
            startDestination = Screen.Calculator.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Skaičiuoklės puslapis
            composable(Screen.Calculator.route) {
                CalculatorScreen(
                    viewModel = viewModel,
                    isWideScreen = widthSizeClass != WindowWidthSizeClass.Compact,
                    showSnackbar = showSnackbar
                )
            }
            // Istorijos puslapis
            composable(Screen.History.route) {
                HistoryScreen(viewModel = viewModel, showSnackbar = showSnackbar)
            }
            // „Apie“ puslapis
            composable(Screen.About.route) {
                AboutScreen()
            }
        }
    }

    // Langas „Autorius“ (rodomas paspaudus meniu punktą)
    if (showAuthorDialog) {
        AlertDialog(
            onDismissRequest = { showAuthorDialog = false },
            icon = { Icon(Icons.Filled.Person, contentDescription = null) },
            title = { Text("Autorius") },
            text = { Text("$AUTHOR_NAME\nGrupė: $AUTHOR_GROUP") },
            confirmButton = {
                TextButton(onClick = { showAuthorDialog = false }) { Text("Gerai") }
            }
        )
    }
}
