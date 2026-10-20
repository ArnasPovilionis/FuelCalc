# FuelCalc – kelionės kuro kainos skaičiuoklė

**Autorius:** Arnas Povilionis
**Grupė:** [GRUPĖ]

Android programėlė (Kotlin + Jetpack Compose, Material 3), kuri apskaičiuoja,
kiek kuro reikės kelionei ir kiek ji kainuos.

> ⚠️ Prieš atsiskaitydami įrašykite savo grupę faile
> `app/src/main/java/com/example/fuelcalc/FuelCalculator.kt` (eilutė
> `const val AUTHOR_GROUP = "[GRUPĖ]"`). Ji automatiškai atsiras ir meniu „Autorius“, ir puslapyje „Apie“.

## Kaip paleisti

1. Išarchyvuokite `FuelCalc.zip`.
2. Android Studio (Ladybug 2024.2 ar naujesnė): **File → Open** → pasirinkite aplanką `FuelCalc`.
3. Palaukite, kol baigsis „Gradle Sync“ (pirmą kartą atsisiunčiamos bibliotekos, reikia interneto).
4. Pasirinkite emuliatorių arba prijunkite telefoną (Android 8.0 / API 26 ar naujesnį) ir spauskite **Run ▶**.

### Testo paleidimas

- Android Studio: atidarykite `app/src/test/java/com/example/fuelcalc/FuelCalculatorTest.kt`
  ir spauskite žalią ▶ prie klasės pavadinimo.
- Arba terminale: `./gradlew test` (Windows: `gradlew.bat test`).

## Kaip naudotis

1. **Skaičiuoklė** – įveskite atstumą (km), kuro sąnaudas (l/100 km) ir kuro kainą (€/l)
   (galima rašyti ir su kableliu, pvz. `6,5`). Pasirinkite kuro tipą, ar kelionė pirmyn ir atgal,
   ir slankikliu – keleivių skaičių (1–5). Paspauskite **Apskaičiuoti**.
   Rezultatas (litrai, kaina, kaina vienam žmogui) parodomas su animacija, o apačioje
   iššoka Snackbar pranešimas. Neteisingi laukai pažymimi raudonai su klaidos tekstu.
   **Išvalyti** – išvalo visus laukus.
2. **Istorija** – visi atlikti skaičiavimai (naujausi viršuje). Šiukšliadėžės mygtuku įrašą galima ištrinti.
   Apatinėje juostoje prie „Istorija“ rodomas įrašų skaičius.
3. **Apie** – autoriaus informacija ir programėlės aprašymas.
4. Viršutinėje juostoje (TopAppBar) – trijų taškų meniu → **Autorius** atidaro langą su autoriaus vardu, pavarde ir grupe.

## Formulės

- Atstumas = įvestas atstumas × 2, jei įjungta „Kelionė pirmyn ir atgal“
- Kuras (l) = atstumas × sąnaudos / 100
- Kaina (€) = kuras × kuro kaina
- Kaina vienam žmogui = kaina / keleivių skaičius

## Reikalavimų atitikimas

| Reikalavimas | Kur realizuota |
|---|---|
| Įvesties apdorojimas, skaičiavimai, tikrinimas | `FuelCalculator.kt`, `FuelViewModel.kt` |
| TextField, Button, OutlinedButton, Card, RadioButton, Switch, Slider, IconButton, Icon, HorizontalDivider, Badge | `CalculatorScreen.kt`, `HistoryScreen.kt`, `AboutScreen.kt` |
| Snackbar | `FuelCalcApp.kt` (`SnackbarHost`), kviečiamas skaičiuojant ir trinant |
| 3 puslapiai + Bottom Navigation (NavigationBar + NavHost) | `FuelCalcApp.kt` |
| Istorija su LazyColumn ir trynimu | `HistoryScreen.kt` |
| TopAppBar (CenterAlignedTopAppBar) su trijų taškų meniu (DropdownMenu) ir AlertDialog „Autorius“ | `FuelCalcApp.kt` |
| Autoriaus informacija „Apie“ puslapyje | `AboutScreen.kt` |
| Animacija | `AnimatedVisibility` rezultato kortelei (`CalculatorScreen.kt`), `animateItem()` istorijos sąraše |
| Material 3 tema (spalvos, tipografija, formos, šviesus/tamsus režimas) | `ui/theme/Theme.kt` |
| Kintami ekrano dydžiai | `MainActivity.kt` (`calculateWindowSizeClass`) → `CalculatorScreen.kt`: telefone elementai vienas po kito, plačiame ekrane (planšetė / horizontaliai) – įvestis kairėje, rezultatas dešinėje |
| Prieinamumas | `contentDescription` ikonoms, `selectable(role = Role.RadioButton)` kuro tipo pasirinkimui |
| Unit testas | `app/src/test/.../FuelCalculatorTest.kt` |

## Kodėl testuojamas būtent šis atvejis

Svarbiausia programėlės dalis – kainos skaičiavimas, todėl testuojama `FuelCalculator` logika
(ji atskirta nuo UI, todėl testas greitas ir nereikia emuliatoriaus).

**`calculate_oneWay_returnsCorrectLitersAndCost`** – patikrina pagrindinę formulę
(100 km × 6,5 l/100 km = 6,5 l; 6,5 l × 1,60 € = 10,40 €) ir kainą vienam keleiviui.
Jei ši formulė klaidinga – visa programėlė rodo neteisingas kainas.

## Projekto struktūra

```
app/src/main/java/com/example/fuelcalc/
├── MainActivity.kt        – programos įėjimo taškas, ekrano dydžio klasė
├── FuelCalcApp.kt         – Scaffold, TopAppBar, meniu, AlertDialog, Bottom Navigation, NavHost
├── CalculatorScreen.kt    – „Skaičiuoklė“ puslapis
├── HistoryScreen.kt       – „Istorija“ puslapis (LazyColumn)
├── AboutScreen.kt         – „Apie“ puslapis
├── FuelCalculator.kt      – skaičiavimo ir tikrinimo logika, autoriaus duomenys
├── FuelViewModel.kt       – ekrano būsena ir istorija
└── ui/theme/Theme.kt      – Material 3 spalvos, tipografija ir formos
app/src/test/java/com/example/fuelcalc/FuelCalculatorTest.kt – unit testas
```

Naudojama: Android Gradle Plugin 8.7.3, Gradle 8.11.1, Kotlin 2.0.21, Compose BOM 2024.12.01,
Navigation Compose 2.8.5, minSdk 26, targetSdk 35.
