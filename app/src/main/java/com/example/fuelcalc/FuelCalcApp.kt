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
enum class Puslapis(val marsrutas: String, val pavadinimas: String, val ikona: ImageVector) {
    Skaiciuokle("skaiciuokle", "Skaičiuoklė", Icons.Filled.Home),
    Istorija("istorija", "Istorija", Icons.AutoMirrored.Filled.List),
    Apie("apie", "Apie", Icons.Filled.Info)
}

// Programėlės karkasas: viršutinė juosta, apatinė navigacija, puslapiai ir pranešimai
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Programele(
    duomenys: KuroViewModel,
    ekranoPlotis: WindowWidthSizeClass
) {
    // Valdo perėjimus tarp puslapių
    val navigacija = rememberNavController()
    // Kuris puslapis dabar atidarytas
    val dabartinisIrasas by navigacija.currentBackStackEntryAsState()
    val dabartinisPuslapis = Puslapis.entries.find { it.marsrutas == dabartinisIrasas?.destination?.route }
        ?: Puslapis.Skaiciuokle

    // Snackbar pranešimų valdymas
    val pranesimuBusena = remember { SnackbarHostState() }
    val korutinos = rememberCoroutineScope()
    // Parodo pranešimą apačioje (prieš tai paslepia seną)
    val rodytiPranesima: (String) -> Unit = { pranesimas ->
        korutinos.launch {
            pranesimuBusena.currentSnackbarData?.dismiss()
            pranesimuBusena.showSnackbar(pranesimas)
        }
    }

    // Ar atidarytas trijų taškų meniu
    var meniuAtidarytas by remember { mutableStateOf(false) }
    // Ar rodomas langas „Autorius“
    var rodytiAutoriausLanga by remember { mutableStateOf(false) }

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
                        Text(dabartinisPuslapis.pavadinimas, style = MaterialTheme.typography.titleLarge)
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
                    IconButton(onClick = { meniuAtidarytas = true }) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "Meniu")
                    }
                    // Išskleidžiamas meniu
                    DropdownMenu(
                        expanded = meniuAtidarytas,
                        onDismissRequest = { meniuAtidarytas = false }
                    ) {
                        // Meniu punktas „Autorius“ – uždaro meniu ir atidaro langą
                        DropdownMenuItem(
                            text = { Text("Autorius") },
                            leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
                            onClick = {
                                meniuAtidarytas = false
                                rodytiAutoriausLanga = true
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
                Puslapis.entries.forEach { puslapis ->
                    NavigationBarItem(
                        selected = dabartinisPuslapis == puslapis,
                        // Paspaudus – pereina į tą puslapį
                        onClick = {
                            navigacija.navigate(puslapis.marsrutas) {
                                popUpTo(navigacija.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        // Ikona; prie „Istorija“ – ženkliukas su įrašų skaičiumi
                        icon = {
                            if (puslapis == Puslapis.Istorija && duomenys.istorija.isNotEmpty()) {
                                BadgedBox(badge = { Badge { Text("${duomenys.istorija.size}") } }) {
                                    Icon(puslapis.ikona, contentDescription = puslapis.pavadinimas)
                                }
                            } else {
                                Icon(puslapis.ikona, contentDescription = puslapis.pavadinimas)
                            }
                        },
                        // Užrašas po ikona
                        label = { Text(puslapis.pavadinimas) },
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
            SnackbarHost(pranesimuBusena) { pranesimoDuomenys ->
                Snackbar(
                    snackbarData = pranesimoDuomenys,
                    shape = MaterialTheme.shapes.medium,
                    containerColor = MaterialTheme.colorScheme.inverseSurface,
                    contentColor = MaterialTheme.colorScheme.inverseOnSurface
                )
            }
        },
        // Fono spalva
        containerColor = MaterialTheme.colorScheme.background
    ) { vidinisTarpas ->
        // Puslapių sąrašas: rodo tą, kuris pasirinktas (pradžioje – skaičiuoklė)
        NavHost(
            navController = navigacija,
            startDestination = Puslapis.Skaiciuokle.marsrutas,
            modifier = Modifier.padding(vidinisTarpas)
        ) {
            // Skaičiuoklės puslapis
            composable(Puslapis.Skaiciuokle.marsrutas) {
                SkaiciuoklesEkranas(
                    duomenys = duomenys,
                    platusEkranas = ekranoPlotis != WindowWidthSizeClass.Compact,
                    rodytiPranesima = rodytiPranesima
                )
            }
            // Istorijos puslapis
            composable(Puslapis.Istorija.marsrutas) {
                IstorijosEkranas(duomenys = duomenys, rodytiPranesima = rodytiPranesima)
            }
            // „Apie“ puslapis
            composable(Puslapis.Apie.marsrutas) {
                ApieEkranas()
            }
        }
    }

    // Langas „Autorius“ (rodomas paspaudus meniu punktą)
    if (rodytiAutoriausLanga) {
        AlertDialog(
            onDismissRequest = { rodytiAutoriausLanga = false },
            icon = { Icon(Icons.Filled.Person, contentDescription = null) },
            title = { Text("Autorius") },
            text = { Text("$AUTORIAUS_VARDAS\nGrupė: $AUTORIAUS_GRUPE") },
            confirmButton = {
                TextButton(onClick = { rodytiAutoriausLanga = false }) { Text("Gerai") }
            }
        )
    }
}
