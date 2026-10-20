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

// „Skaičiuoklė“ puslapis: įvesties laukai, mygtukai ir rezultatas
@Composable
fun SkaiciuoklesEkranas(
    duomenys: KuroViewModel,
    platusEkranas: Boolean,
    rodytiPranesima: (String) -> Unit
) {
    // Leidžia paslėpti klaviatūrą
    val fokusas = LocalFocusManager.current
    // Kas vyksta paspaudus „Apskaičiuoti“
    val kaiSkaiciuoti = {
        // Paslepia klaviatūrą
        fokusas.clearFocus()
        // Skaičiuoja ir parodo pranešimą: pavyko arba klaida
        if (duomenys.skaiciuoti()) {
            rodytiPranesima("Skaičiavimas išsaugotas istorijoje")
        } else {
            rodytiPranesima("Patikrinkite įvestus duomenis")
        }
    }

    if (platusEkranas) {
        // Platus ekranas (planšetė, horizontali padėtis): įvestis kairėje, rezultatas dešinėje
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Kairysis stulpelis: įvestis
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                IvestiesSekcija(duomenys, kaiSkaiciuoti)
            }
            // Dešinysis stulpelis: rezultatas
            Column(modifier = Modifier.weight(1f)) {
                RezultatoSekcija(duomenys.rezultatas)
            }
        }
    } else {
        // Telefonas: įvestis viršuje, rezultatas apačioje, galima slinkti
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            IvestiesSekcija(duomenys, kaiSkaiciuoti)
            Spacer(Modifier.height(20.dp))
            RezultatoSekcija(duomenys.rezultatas)
        }
    }
}

// Kortelė su antrašte, į kurią dedami kiti elementai
@Composable
private fun SekcijosKortele(
    pavadinimas: String,
    modifier: Modifier = Modifier,
    turinys: @Composable () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        // Kortelės turinys: elementai vienas po kito
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Antraštė didžiosiomis raidėmis
            Text(
                pavadinimas.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
            // Kortelės turinys, kurį perduoda kviečianti funkcija
            turinys()
        }
    }
}

// Visi įvesties laukai, nustatymai ir mygtukai
@Composable
private fun IvestiesSekcija(duomenys: KuroViewModel, kaiSkaiciuoti: () -> Unit) {
    // Kortelės ir mygtukai vienas po kito
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // 1 kortelė: trys skaičių laukai
        SekcijosKortele(pavadinimas = "Kelionės duomenys") {
            // Atstumas (km)
            SkaiciausLaukas(
                reiksme = duomenys.atstumas,
                keistiReiksme = duomenys::keistiAtstuma,
                pavadinimas = "Atstumas",
                vienetai = "km",
                ikona = Icons.Filled.Place,
                klaida = duomenys.atstumoKlaida,
                klaviaturosVeiksmas = ImeAction.Next
            )
            // Kuro sąnaudos (l/100 km)
            SkaiciausLaukas(
                reiksme = duomenys.sanaudos,
                keistiReiksme = duomenys::keistiSanaudas,
                pavadinimas = "Kuro sąnaudos",
                vienetai = "l/100 km",
                ikona = Icons.Filled.Settings,
                klaida = duomenys.sanaudosKlaida,
                klaviaturosVeiksmas = ImeAction.Next
            )
            // Kuro kaina (€/l)
            SkaiciausLaukas(
                reiksme = duomenys.kaina,
                keistiReiksme = duomenys::keistiKaina,
                pavadinimas = "Kuro kaina",
                vienetai = "€/l",
                ikona = Icons.Filled.ShoppingCart,
                klaida = duomenys.kainosKlaida,
                klaviaturosVeiksmas = ImeAction.Done
            )
        }

        // 2 kortelė: kuro tipas, pirmyn ir atgal, keleiviai
        SekcijosKortele(pavadinimas = "Nustatymai") {
            // Kuro tipo antraštė
            Text("Kuro tipas", style = MaterialTheme.typography.titleMedium)
            // Kuro tipo pasirinkimai šalia vienas kito (Benzinas, Dyzelinas)
            Row(
                modifier = Modifier.selectableGroup(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                KuroTipas.entries.forEach { tipas ->
                    KuroTipoPasirinkimas(
                        tipas = tipas,
                        pasirinktas = duomenys.kuroTipas == tipas,
                        paspaudus = { duomenys.keistiKuroTipa(tipas) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Skiriamoji linija
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            // Eilutė: tekstas kairėje, jungiklis dešinėje
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Pavadinimas ir paaiškinimas vienas po kito
                Column(modifier = Modifier.weight(1f)) {
                    Text("Kelionė pirmyn ir atgal", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Atstumas bus padvigubintas",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                // Jungiklis „pirmyn ir atgal“
                Switch(
                    checked = duomenys.pirmynIrAtgal,
                    onCheckedChange = duomenys::keistiPirmynIrAtgal
                )
            }

            // Skiriamoji linija
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            // Eilutė: „Keleivių skaičius“ kairėje, ženkliukas su skaičiumi dešinėje
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Keleivių skaičius",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                // Ovalus ženkliukas: ikona ir keleivių skaičius
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
                        Text("${duomenys.keleiviai}", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
            // Slankiklis keleiviams (1–5)
            Slider(
                value = duomenys.keleiviuSlankiklis,
                onValueChange = duomenys::keistiKeleivius,
                valueRange = 1f..5f,
                steps = 3
            )
        }

        // Mygtukai šalia vienas kito
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            // Mygtukas „Išvalyti“
            OutlinedButton(
                onClick = duomenys::isvalytiLaukus,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(Icons.Filled.Refresh, contentDescription = null)
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("Išvalyti")
            }
            // Mygtukas „Apskaičiuoti“ (dvigubai platesnis)
            Button(
                onClick = kaiSkaiciuoti,
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

// Vienas kuro tipo pasirinkimas (rėmelis su RadioButton ir pavadinimu)
@Composable
private fun KuroTipoPasirinkimas(
    tipas: KuroTipas,
    pasirinktas: Boolean,
    paspaudus: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Fono spalva: pasirinkus – šviesiai mėlyna, kitaip – permatoma (keičiasi su animacija)
    val fonoSpalva by animateColorAsState(
        if (pasirinktas) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
        label = "fonoSpalva"
    )
    // Rėmelio spalva: pasirinkus – mėlyna, kitaip – pilka
    val remelioSpalva by animateColorAsState(
        if (pasirinktas) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
        label = "remelioSpalva"
    )
    // Paspaudžiamas rėmelis
    Surface(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .selectable(
                selected = pasirinktas,
                onClick = paspaudus,
                role = Role.RadioButton
            ),
        shape = MaterialTheme.shapes.medium,
        color = fonoSpalva,
        border = BorderStroke(if (pasirinktas) 2.dp else 1.dp, remelioSpalva)
    ) {
        // RadioButton ir pavadinimas šalia vienas kito
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(selected = pasirinktas, onClick = null)
            Spacer(Modifier.width(8.dp))
            Text(tipas.pavadinimas, style = MaterialTheme.typography.titleMedium)
        }
    }
}

// Vienas laukas skaičiui įvesti (su ikona, vienetais ir klaidos tekstu)
@Composable
private fun SkaiciausLaukas(
    reiksme: String,
    keistiReiksme: (String) -> Unit,
    pavadinimas: String,
    vienetai: String,
    ikona: ImageVector,
    klaida: String?,
    klaviaturosVeiksmas: ImeAction
) {
    OutlinedTextField(
        value = reiksme,
        onValueChange = keistiReiksme,
        // Lauko pavadinimas
        label = { Text(pavadinimas) },
        // Ikona kairėje
        leadingIcon = { Icon(ikona, contentDescription = null) },
        // Vienetai dešinėje (km, l/100 km, €/l)
        suffix = { Text(vienetai) },
        // Jei yra klaida – laukas raudonas ir po juo rodomas klaidos tekstas
        isError = klaida != null,
        supportingText = if (klaida != null) {
            { Text(klaida) }
        } else {
            null
        },
        // Tik viena eilutė
        singleLine = true,
        shape = MaterialTheme.shapes.medium,
        // Lauko spalvos
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            errorContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            unfocusedBorderColor = Color.Transparent,
            unfocusedLeadingIconColor = MaterialTheme.colorScheme.primary,
            focusedLeadingIconColor = MaterialTheme.colorScheme.primary
        ),
        // Skaičių klaviatūra; mygtukas „Toliau“ arba „Baigta“
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal,
            imeAction = klaviaturosVeiksmas
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

// Rezultato kortelė (rodoma tik po skaičiavimo)
@Composable
private fun RezultatoSekcija(rezultatas: KelionesRezultatas?) {
    // Prisimenamas paskutinis rezultatas, kad slėpimo animacija turėtų ką rodyti
    var paskutinisRezultatas by remember { mutableStateOf(rezultatas) }
    if (rezultatas != null) paskutinisRezultatas = rezultatas

    // Paprasta animacija: rezultato kortelė išsiskleidžia ir išryškėja
    AnimatedVisibility(
        visible = rezultatas != null,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        paskutinisRezultatas?.let { rodomas ->
            // Rezultato kortelė
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.extraLarge,
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                // Fonas su spalvų perėjimu (mėlyna → turkio)
                Box(
                    modifier = Modifier.background(
                        Brush.linearGradient(
                            listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.tertiary)
                        )
                    )
                ) {
                    // Rezultato tekstai vienas po kito
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Teksto spalva ant spalvoto fono (balta)
                        val tekstoSpalva = MaterialTheme.colorScheme.onPrimary
                        // Užrašas „KELIONĖS KAINA“
                        Text(
                            "KELIONĖS KAINA",
                            style = MaterialTheme.typography.labelSmall,
                            color = tekstoSpalva.copy(alpha = 0.8f)
                        )
                        // Bendra kaina dideliu šriftu
                        Text(
                            "${rodomas.bendraKaina.formatuoti2()} €",
                            style = MaterialTheme.typography.displaySmall,
                            color = tekstoSpalva
                        )
                        // Kuro tipas ir ar pirmyn ir atgal
                        Text(
                            rodomas.kuroTipas.pavadinimas + if (rodomas.pirmynIrAtgal) " • pirmyn ir atgal" else "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = tekstoSpalva.copy(alpha = 0.8f)
                        )
                        // Tarpas
                        Spacer(Modifier.height(12.dp))
                        // Trys langeliai šalia vienas kito: atstumas, kuras, kaina vienam žmogui
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            StatistikosLangelis("Atstumas", "${rodomas.visasAtstumasKm.formatuoti2()} km", Modifier.weight(1f))
                            StatistikosLangelis("Kuro", "${rodomas.litrai.formatuoti2()} l", Modifier.weight(1f))
                            StatistikosLangelis(
                                "1 žm. (${rodomas.keleiviai})",
                                "${rodomas.kainaVienam.formatuoti2()} €",
                                Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

// Mažas langelis rezultate: pavadinimas viršuje, reikšmė apačioje
@Composable
private fun StatistikosLangelis(pavadinimas: String, reiksme: String, modifier: Modifier = Modifier) {
    val tekstoSpalva = MaterialTheme.colorScheme.onPrimary
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = tekstoSpalva.copy(alpha = 0.15f),
        contentColor = tekstoSpalva
    ) {
        // Pavadinimas ir reikšmė vienas po kito
        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
            Text(pavadinimas, style = MaterialTheme.typography.labelSmall, color = tekstoSpalva.copy(alpha = 0.8f))
            Text(reiksme, style = MaterialTheme.typography.titleSmall, maxLines = 1)
        }
    }
}
