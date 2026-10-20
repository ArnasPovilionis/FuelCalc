package com.example.fuelcalc

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

// „Istorija“ puslapis: visų skaičiavimų sąrašas
@Composable
fun IstorijosEkranas(duomenys: KuroViewModel, rodytiPranesima: (String) -> Unit) {
    if (duomenys.istorija.isEmpty()) {
        // Istorija tuščia: ikona ir tekstas ekrano centre
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Apskritimas su sąrašo ikona
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.List,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            // Tarpas
            Spacer(Modifier.size(16.dp))
            // Antraštė
            Text("Istorija tuščia", style = MaterialTheme.typography.titleLarge)
            // Paaiškinimas
            Text(
                "Atlikite skaičiavimą – jis atsiras čia.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        // Slenkamas sąrašas (naujausi įrašai viršuje)
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Po vieną kortelę kiekvienam įrašui
            items(duomenys.istorija, key = { it.id }) { irasas ->
                IstorijosKortele(
                    rezultatas = irasas.rezultatas,
                    // Paspaudus šiukšliadėžę – ištrina įrašą ir parodo pranešimą
                    istrinti = {
                        duomenys.istrintiIrasa(irasas)
                        rodytiPranesima("Įrašas ištrintas")
                    },
                    // Animacija trinant įrašą
                    modifier = Modifier.animateItem()
                )
            }
        }
    }
}

// Viena istorijos kortelė: raidė kairėje, duomenys viduryje, trynimo mygtukas dešinėje
@Composable
private fun IstorijosKortele(rezultatas: KelionesRezultatas, istrinti: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        // Elementai iš kairės į dešinę
        Row(
            modifier = Modifier.padding(start = 16.dp, top = 14.dp, bottom = 14.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Spalvos pagal kuro tipą: benzinas – oranžinė, dyzelinas – turkio
            val (zenkloFonas, zenkloSpalva) = when (rezultatas.kuroTipas) {
                KuroTipas.BENZINAS -> MaterialTheme.colorScheme.secondaryContainer to
                    MaterialTheme.colorScheme.onSecondaryContainer
                KuroTipas.DYZELINAS -> MaterialTheme.colorScheme.tertiaryContainer to
                    MaterialTheme.colorScheme.onTertiaryContainer
            }
            // Kvadratėlis su kuro tipo raide (B arba D)
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(zenkloFonas),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    rezultatas.kuroTipas.pavadinimas.take(1),
                    style = MaterialTheme.typography.titleLarge,
                    color = zenkloSpalva
                )
            }
            // Tarpas
            Spacer(Modifier.width(16.dp))
            // Įrašo duomenys vienas po kito
            Column(modifier = Modifier.weight(1f)) {
                // Bendra kaina
                Text(
                    "${rezultatas.bendraKaina.formatuoti2()} €",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                // Kuro tipas ir atstumas
                Text(
                    "${rezultatas.kuroTipas.pavadinimas} • ${rezultatas.visasAtstumasKm.formatuoti2()} km" +
                        if (rezultatas.pirmynIrAtgal) " (pirmyn ir atgal)" else "",
                    style = MaterialTheme.typography.bodyMedium
                )
                // Litrai, keleiviai ir kaina vienam žmogui
                Text(
                    "${rezultatas.litrai.formatuoti2()} l • ${rezultatas.keleiviai} kel. • " +
                        "${rezultatas.kainaVienam.formatuoti2()} € / žm.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            // Trynimo mygtukas (šiukšliadėžė)
            IconButton(onClick = istrinti) {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = "Ištrinti įrašą",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
