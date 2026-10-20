package com.example.fuelcalc

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlin.math.roundToInt

// Vienas istorijos įrašas: unikalus numeris ir rezultatas
data class HistoryEntry(val id: Int, val result: TripResult)

// Visi ekrano duomenys ir veiksmai su jais (saugomi atmintyje, kol programėlė veikia)
class FuelViewModel : ViewModel() {

    // Įvesti laukai (tekstas)
    var distance by mutableStateOf("")
        private set
    var consumption by mutableStateOf("")
        private set
    var price by mutableStateOf("")
        private set

    // Klaidų tekstai laukams (null – klaidos nėra)
    var distanceError by mutableStateOf<String?>(null)
        private set
    var consumptionError by mutableStateOf<String?>(null)
        private set
    var priceError by mutableStateOf<String?>(null)
        private set

    // Pasirinktas kuro tipas
    var fuelType by mutableStateOf(FuelType.PETROL)
        private set
    // Ar kelionė pirmyn ir atgal
    var roundTrip by mutableStateOf(false)
        private set
    // Slankiklio reikšmė (1.0–5.0)
    var passengersSlider by mutableFloatStateOf(1f)
        private set
    // Keleivių skaičius sveikuoju skaičiumi
    val passengers: Int get() = passengersSlider.roundToInt()

    // Paskutinis rezultatas (null – nerodomas)
    var result by mutableStateOf<TripResult?>(null)
        private set

    // Istorijos sąrašas
    val history = mutableStateListOf<HistoryEntry>()
    // Kito įrašo numeris
    private var nextId = 0

    // Pakeitus bet kurią įvestį, senas rezultatas paslepiamas (jis jau nebeatitinka duomenų)

    // Pakeistas atstumas
    fun onDistanceChange(value: String) {
        distance = value
        distanceError = null
        result = null
    }

    // Pakeistos sąnaudos
    fun onConsumptionChange(value: String) {
        consumption = value
        consumptionError = null
        result = null
    }

    // Pakeista kuro kaina
    fun onPriceChange(value: String) {
        price = value
        priceError = null
        result = null
    }

    // Pakeistas kuro tipas
    fun onFuelTypeChange(value: FuelType) {
        fuelType = value
        result = null
    }

    // Perjungtas „pirmyn ir atgal“
    fun onRoundTripChange(value: Boolean) {
        roundTrip = value
        result = null
    }

    // Pakeistas keleivių skaičius
    fun onPassengersChange(value: Float) {
        passengersSlider = value
        result = null
    }

    /** Patikrina įvestį, apskaičiuoja ir įrašo į istoriją. Grąžina true, jei pavyko. */
    fun calculate(): Boolean {
        // Patikrina visus tris laukus
        distanceError = FuelCalculator.validate(distance, FuelCalculator.MAX_DISTANCE)
        consumptionError = FuelCalculator.validate(consumption, FuelCalculator.MAX_CONSUMPTION)
        priceError = FuelCalculator.validate(price, FuelCalculator.MAX_PRICE)

        // Jei yra bent viena klaida – neskaičiuoja
        if (distanceError != null || consumptionError != null || priceError != null) {
            result = null
            return false
        }

        // Paverčia tekstą skaičiais ir skaičiuoja
        val newResult = FuelCalculator.calculate(
            distanceKm = FuelCalculator.parse(distance)!!,
            consumptionPer100Km = FuelCalculator.parse(consumption)!!,
            pricePerLiter = FuelCalculator.parse(price)!!,
            roundTrip = roundTrip,
            passengers = passengers,
            fuelType = fuelType
        )
        // Parodo rezultatą
        result = newResult
        // Įrašo į istorijos pradžią (naujausias viršuje)
        history.add(0, HistoryEntry(nextId++, newResult))
        return true
    }

    // Išvalo visus laukus ir grąžina numatytuosius nustatymus
    fun clearInputs() {
        distance = ""
        consumption = ""
        price = ""
        distanceError = null
        consumptionError = null
        priceError = null
        fuelType = FuelType.PETROL
        roundTrip = false
        passengersSlider = 1f
        result = null
    }

    // Ištrina įrašą iš istorijos
    fun deleteEntry(entry: HistoryEntry) {
        history.remove(entry)
    }
}
