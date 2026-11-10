package lt.vilniustech.gsk.mkdf_23_2.povilionis

import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import lt.vilniustech.gsk.mkdf_23_2.povilionis.ui.screens.ContactScreen
import lt.vilniustech.gsk.mkdf_23_2.povilionis.ui.screens.CvScreen
import lt.vilniustech.gsk.mkdf_23_2.povilionis.ui.screens.HomeScreen
import lt.vilniustech.gsk.mkdf_23_2.povilionis.ui.screens.PortfolioScreen

// Programėlės puslapiai
enum class Destination(val route: String, @StringRes val label: Int, val icon: ImageVector) {
    Home("home", R.string.nav_home, Icons.Filled.Home),
    Cv("cv", R.string.nav_cv, Icons.Filled.AccountBox),
    Portfolio("portfolio", R.string.nav_portfolio, Icons.Filled.Star),
    Contact("contact", R.string.nav_contact, Icons.Filled.Email)
}

// Programėlės karkasas: viršutinė juosta su kalbos meniu, navigacija ir puslapiai
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessCardApp(viewModel: CardViewModel, windowSizeClass: WindowSizeClass) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val current = Destination.entries.find { it.route == backStackEntry?.destination?.route }
        ?: Destination.Home

    // Siauras ekranas – apatinė juosta; platus (planšetė, horizontaliai) – šoninė juosta
    val isCompact = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact
    val isWide = !isCompact

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val showMessage: (String) -> Unit = { message ->
        scope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(message)
        }
    }
    val navigate: (Destination) -> Unit = { navController.navigateTo(it) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(current.label)) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                actions = { LanguageMenu() }
            )
        },
        bottomBar = {
            if (isCompact) {
                NavigationBar {
                    Destination.entries.forEach { destination ->
                        NavigationBarItem(
                            selected = current == destination,
                            onClick = { navigate(destination) },
                            icon = { Icon(destination.icon, contentDescription = null) },
                            label = { Text(stringResource(destination.label)) }
                        )
                    }
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Row(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            if (isWide) {
                NavigationRail(containerColor = MaterialTheme.colorScheme.background) {
                    Destination.entries.forEach { destination ->
                        NavigationRailItem(
                            selected = current == destination,
                            onClick = { navigate(destination) },
                            icon = { Icon(destination.icon, contentDescription = null) },
                            label = { Text(stringResource(destination.label)) }
                        )
                    }
                }
            }
            NavHost(
                navController = navController,
                startDestination = Destination.Home.route,
                modifier = Modifier.weight(1f)
            ) {
                composable(Destination.Home.route) {
                    HomeScreen(viewModel, isWide, navigate, showMessage)
                }
                composable(Destination.Cv.route) {
                    CvScreen(viewModel, showMessage)
                }
                composable(Destination.Portfolio.route) {
                    PortfolioScreen(viewModel, showMessage)
                }
                composable(Destination.Contact.route) {
                    ContactScreen(showMessage)
                }
            }
        }
    }
}

private fun NavHostController.navigateTo(destination: Destination) {
    navigate(destination.route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

// Kalbos perjungimas: tekstai imami iš values/strings.xml (anglų) arba values-lt/strings.xml (lietuvių)
@Composable
private fun LanguageMenu() {
    var expanded by remember { mutableStateOf(false) }
    val currentLanguage = LocalConfiguration.current.locales[0].language

    // Mygtukas rodo dabartinę kalbą (LT / EN)
    TextButton(onClick = { expanded = true }) {
        Text(currentLanguage.uppercase())
        Icon(Icons.Filled.ArrowDropDown, contentDescription = stringResource(R.string.menu_language))
    }
    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
        Text(
            stringResource(R.string.menu_language),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        listOf("lt" to R.string.language_lt, "en" to R.string.language_en).forEach { (tag, label) ->
            DropdownMenuItem(
                text = { Text(stringResource(label)) },
                leadingIcon = { RadioButton(selected = currentLanguage == tag, onClick = null) },
                onClick = {
                    expanded = false
                    // Pakeičia kalbą visai programėlei; Android ją prisimena ir paleidus iš naujo
                    AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(tag))
                }
            )
        }
    }
}
