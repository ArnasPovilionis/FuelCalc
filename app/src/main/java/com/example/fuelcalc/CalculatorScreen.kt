package com.example.fuelcalc

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.Role
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
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
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
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            InputSection(viewModel, onCalculate)
            Spacer(Modifier.height(20.dp))
            ResultSection(viewModel.result)
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                title.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
            content()
        }
    }
}

@Composable
private fun InputSection(viewModel: FuelViewModel, onCalculate: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SectionCard(title = "Kelionės duomenys") {
            NumberField(
                value = viewModel.distance,
                onValueChange = viewModel::onDistanceChange,
                label = "Atstumas",
                suffix = "km",
                icon = Icons.Filled.Place,
                error = viewModel.distanceError,
                imeAction = ImeAction.Next
            )
            NumberField(
                value = viewModel.consumption,
                onValueChange = viewModel::onConsumptionChange,
                label = "Kuro sąnaudos",
                suffix = "l/100 km",
                icon = Icons.Filled.Settings,
                error = viewModel.consumptionError,
                imeAction = ImeAction.Next
            )
            NumberField(
                value = viewModel.price,
                onValueChange = viewModel::onPriceChange,
                label = "Kuro kaina",
                suffix = "€/l",
                icon = Icons.Filled.ShoppingCart,
                error = viewModel.priceError,
                imeAction = ImeAction.Done
            )
        }

        SectionCard(title = "Nustatymai") {
            Text("Kuro tipas", style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.selectableGroup(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FuelType.entries.forEach { type ->
                    FuelTypeOption(
                        type = type,
                        selected = viewModel.fuelType == type,
                        onClick = { viewModel.onFuelTypeChange(type) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Kelionė pirmyn ir atgal", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Atstumas bus padvigubintas",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = viewModel.roundTrip,
                    onCheckedChange = viewModel::onRoundTripChange
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Keleivių skaičius",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Person, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("${viewModel.passengers}", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
            Slider(
                value = viewModel.passengersSlider,
                onValueChange = viewModel::onPassengersChange,
                valueRange = 1f..5f,
                steps = 3
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = viewModel::clearInputs,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(Icons.Filled.Refresh, contentDescription = null)
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("Išvalyti")
            }
            Button(
                onClick = onCalculate,
                modifier = Modifier
                    .weight(2f)
                    .height(56.dp),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                )
            ) {
                Icon(Icons.Filled.Check, contentDescription = null)
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("Apskaičiuoti", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
private fun FuelTypeOption(
    type: FuelType,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor by animateColorAsState(
        if (selected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
        label = "fuelTypeContainer"
    )
    val borderColor by animateColorAsState(
        if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
        label = "fuelTypeBorder"
    )
    Surface(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton
            ),
        shape = MaterialTheme.shapes.medium,
        color = containerColor,
        border = BorderStroke(if (selected) 2.dp else 1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(selected = selected, onClick = null)
            Spacer(Modifier.width(8.dp))
            Text(type.label, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
private fun NumberField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    suffix: String,
    icon: ImageVector,
    error: String?,
    imeAction: ImeAction
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, contentDescription = null) },
        suffix = { Text(suffix) },
        isError = error != null,
        supportingText = if (error != null) {
            { Text(error) }
        } else {
            null
        },
        singleLine = true,
        shape = MaterialTheme.shapes.medium,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            errorContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            unfocusedBorderColor = Color.Transparent,
            unfocusedLeadingIconColor = MaterialTheme.colorScheme.primary,
            focusedLeadingIconColor = MaterialTheme.colorScheme.primary
        ),
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
                shape = MaterialTheme.shapes.extraLarge,
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Box(
                    modifier = Modifier.background(
                        Brush.linearGradient(
                            listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.tertiary)
                        )
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val onGradient = MaterialTheme.colorScheme.onPrimary
                        Text(
                            "KELIONĖS KAINA",
                            style = MaterialTheme.typography.labelSmall,
                            color = onGradient.copy(alpha = 0.8f)
                        )
                        Text(
                            "${result.totalCost.format2()} €",
                            style = MaterialTheme.typography.displaySmall,
                            color = onGradient
                        )
                        Text(
                            "${result.fuelType.label}" + if (result.roundTrip) " • pirmyn ir atgal" else "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = onGradient.copy(alpha = 0.8f)
                        )
                        Spacer(Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            StatTile("Atstumas", "${result.totalDistanceKm.format2()} km", Modifier.weight(1f))
                            StatTile("Kuro", "${result.liters.format2()} l", Modifier.weight(1f))
                            StatTile(
                                "1 žm. (${result.passengers})",
                                "${result.costPerPerson.format2()} €",
                                Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatTile(label: String, value: String, modifier: Modifier = Modifier) {
    val onGradient = MaterialTheme.colorScheme.onPrimary
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = onGradient.copy(alpha = 0.15f),
        contentColor = onGradient
    ) {
        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = onGradient.copy(alpha = 0.8f))
            Text(value, style = MaterialTheme.typography.titleSmall, maxLines = 1)
        }
    }
}
