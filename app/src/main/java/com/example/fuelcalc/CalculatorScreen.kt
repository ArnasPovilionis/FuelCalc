package com.example.fuelcalc

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun CalculatorScreen(
    viewModel: FuelViewModel,
    isWideScreen: Boolean,
    showSnackbar: (String) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val onCalculate = {
        focusManager.clearFocus()
        if (viewModel.calculate()) {
            showSnackbar("Skaičiavimas išsaugotas istorijoje")
        } else {
            showSnackbar("Patikrinkite įvestus duomenis")
        }
    }

    if (isWideScreen) {
        // Platus ekranas (planšetė, horizontali padėtis): įvestis kairėje, rezultatas dešinėje
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                InputSection(viewModel, onCalculate)
            }
            Column(modifier = Modifier.weight(1f)) {
                ResultSection(viewModel.result)
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            InputSection(viewModel, onCalculate)
            Spacer(Modifier.size(16.dp))
            ResultSection(viewModel.result)
        }
    }
}

@Composable
private fun InputSection(viewModel: FuelViewModel, onCalculate: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Kelionės duomenys", style = MaterialTheme.typography.titleMedium)
                NumberField(
                    value = viewModel.distance,
                    onValueChange = viewModel::onDistanceChange,
                    label = "Atstumas",
                    suffix = "km",
                    error = viewModel.distanceError,
                    imeAction = ImeAction.Next
                )
                NumberField(
                    value = viewModel.consumption,
                    onValueChange = viewModel::onConsumptionChange,
                    label = "Kuro sąnaudos",
                    suffix = "l/100 km",
                    error = viewModel.consumptionError,
                    imeAction = ImeAction.Next
                )
                NumberField(
                    value = viewModel.price,
                    onValueChange = viewModel::onPriceChange,
                    label = "Kuro kaina",
                    suffix = "€/l",
                    error = viewModel.priceError,
                    imeAction = ImeAction.Done
                )
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Kuro tipas", style = MaterialTheme.typography.titleMedium)
                Row(Modifier.selectableGroup()) {
                    FuelType.entries.forEach { type ->
                        Row(
                            modifier = Modifier
                                .selectable(
                                    selected = viewModel.fuelType == type,
                                    onClick = { viewModel.onFuelTypeChange(type) },
                                    role = Role.RadioButton
                                )
                                .padding(end = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = viewModel.fuelType == type, onClick = null)
                            Spacer(Modifier.width(8.dp))
                            Text(type.label)
                        }
                    }
                }

                HorizontalDivider(Modifier.padding(vertical = 8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Kelionė pirmyn ir atgal", modifier = Modifier.weight(1f))
                    Switch(
                        checked = viewModel.roundTrip,
                        onCheckedChange = viewModel::onRoundTripChange
                    )
                }

                HorizontalDivider(Modifier.padding(vertical = 8.dp))

                Text("Keleivių skaičius: ${viewModel.passengers}")
                Slider(
                    value = viewModel.passengersSlider,
                    onValueChange = viewModel::onPassengersChange,
                    valueRange = 1f..5f,
                    steps = 3
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onCalculate, modifier = Modifier.weight(1f)) {
                Icon(Icons.Filled.Check, contentDescription = null)
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("Apskaičiuoti")
            }
            OutlinedButton(onClick = viewModel::clearInputs, modifier = Modifier.weight(1f)) {
                Icon(Icons.Filled.Refresh, contentDescription = null)
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("Išvalyti")
            }
        }
    }
}

@Composable
private fun NumberField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    suffix: String,
    error: String?,
    imeAction: ImeAction
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        suffix = { Text(suffix) },
        isError = error != null,
        supportingText = { if (error != null) Text(error) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal,
            imeAction = imeAction
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun ResultSection(result: TripResult?) {
    // Prisimenamas paskutinis rezultatas, kad slėpimo animacija turėtų ką rodyti
    var lastResult by remember { mutableStateOf(result) }
    if (result != null) lastResult = result

    // Paprasta animacija: rezultato kortelė išsiskleidžia ir išryškėja
    AnimatedVisibility(
        visible = result != null,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        lastResult?.let { result ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("Rezultatas", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "${result.fuelType.label}, ${result.totalDistanceKm.format2()} km" +
                            if (result.roundTrip) " (pirmyn ir atgal)" else ""
                    )
                    Text("Reikės kuro: ${result.liters.format2()} l")
                    Text(
                        "Kelionės kaina: ${result.totalCost.format2()} €",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text("Vienam žmogui (${result.passengers} kel.): ${result.costPerPerson.format2()} €")
                }
            }
        }
    }
}
