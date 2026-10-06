package com.example.fuelcalc

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AboutScreen() {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Filled.Person,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp)
                )
                Text("Autorius", style = MaterialTheme.typography.labelLarge)
                Text(AUTHOR_NAME, style = MaterialTheme.typography.headlineSmall)
                Text("Grupė: $AUTHOR_GROUP", style = MaterialTheme.typography.bodyLarge)
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Apie programėlę", style = MaterialTheme.typography.titleMedium)
                Text(
                    "FuelCalc apskaičiuoja, kiek kuro reikės kelionei ir kiek ji kainuos. " +
                        "Įveskite atstumą, kuro sąnaudas ir kuro kainą, pasirinkite kuro tipą, " +
                        "ar kelionė pirmyn ir atgal, bei keleivių skaičių – programėlė " +
                        "parodys bendrą kainą ir kainą vienam žmogui."
                )
                HorizontalDivider()
                Text("Formulė: kuras (l) = atstumas × sąnaudos / 100", style = MaterialTheme.typography.bodySmall)
                Text("Kaina (€) = kuras × kuro kaina", style = MaterialTheme.typography.bodySmall)
                Text("Versija 1.0", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
