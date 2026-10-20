package com.example.fuelcalc

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp

// „Apie“ puslapis: autoriaus informacija ir programėlės aprašymas
@Composable
fun AboutScreen() {
    // Visas puslapis: elementai vienas po kito, galima slinkti
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1 kortelė: autorius
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.extraLarge,
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            // Teksto spalva ant spalvoto fono (balta)
            val tekstoSpalva = MaterialTheme.colorScheme.onPrimary
            // Kortelės turinys su spalvų perėjimu (mėlyna → turkio), centruotas
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.tertiary)
                        )
                    )
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Apskritimas su žmogaus ikona
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .clip(CircleShape)
                        .background(tekstoSpalva.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.Person,
                        contentDescription = null,
                        modifier = Modifier.size(52.dp),
                        tint = tekstoSpalva
                    )
                }
                // Tarpas
                Spacer(Modifier.height(12.dp))
                // Užrašas „AUTORIUS“
                Text("AUTORIUS", style = MaterialTheme.typography.labelSmall, color = tekstoSpalva.copy(alpha = 0.8f))
                // Autoriaus vardas ir pavardė (iš FuelCalculator.kt)
                Text(AUTHOR_NAME, style = MaterialTheme.typography.headlineSmall, color = tekstoSpalva)
                // Tarpas
                Spacer(Modifier.height(8.dp))
                // Ovalus ženkliukas su grupe
                Surface(
                    shape = CircleShape,
                    color = tekstoSpalva.copy(alpha = 0.18f),
                    contentColor = tekstoSpalva
                ) {
                    Text(
                        "Grupė: $AUTHOR_GROUP",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // 2 kortelė: apie programėlę
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            // Kortelės turinys: elementai vienas po kito
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Antraštė
                Text(
                    "APIE PROGRAMĖLĘ",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                // Aprašymas
                Text(
                    "FuelCalc apskaičiuoja, kiek kuro reikės kelionei ir kiek ji kainuos. " +
                        "Įveskite atstumą, kuro sąnaudas ir kuro kainą, pasirinkite kuro tipą, " +
                        "ar kelionė pirmyn ir atgal, bei keleivių skaičių – programėlė " +
                        "parodys bendrą kainą ir kainą vienam žmogui.",
                    style = MaterialTheme.typography.bodyMedium
                )
                // Skiriamoji linija
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                // Formulės
                FormulesEilute("1", "Kuras (l) = atstumas × sąnaudos / 100")
                FormulesEilute("2", "Kaina (€) = kuras × kuro kaina")
                // Versija
                Text(
                    "Versija 1.0",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// Viena formulės eilutė: numeris apskritime + formulės tekstas
@Composable
private fun FormulesEilute(numeris: String, tekstas: String) {
    // Elementai iš kairės į dešinę
    Row(verticalAlignment = Alignment.CenterVertically) {
        // Oranžinis apskritimas su numeriu
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                numeris,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
        // Tarpas
        Spacer(Modifier.width(12.dp))
        // Formulės tekstas
        Text(tekstas, style = MaterialTheme.typography.bodyMedium)
    }
}
