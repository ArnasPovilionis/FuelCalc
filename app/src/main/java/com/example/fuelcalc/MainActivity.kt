package com.example.fuelcalc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import com.example.fuelcalc.ui.theme.FuelCalcTema

// Programėlės įėjimo taškas: atsidaro paleidus programėlę
class MainActivity : ComponentActivity() {

    // Duomenų saugykla (išlieka pasukus telefoną)
    private val duomenys: KuroViewModel by viewModels()

    // Vykdoma paleidus programėlę
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Turinys per visą ekraną (ir po būsenos juosta)
        enableEdgeToEdge()
        // Ekrano turinys
        setContent {
            // Programėlės tema: spalvos, šriftai, formos
            FuelCalcTema {
                // Ekrano dydis: telefonas ar platus ekranas
                val ekranoDydis = calculateWindowSizeClass(this)
                // Visa programėlė
                Programele(
                    duomenys = duomenys,
                    ekranoPlotis = ekranoDydis.widthSizeClass
                )
            }
        }
    }
}
