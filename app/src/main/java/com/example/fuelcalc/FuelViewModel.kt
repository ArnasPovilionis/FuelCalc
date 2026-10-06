package com.example.fuelcalc

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlin.math.roundToInt

data class HistoryEntry(val id: Int, val result: TripResult)

class FuelViewModel : ViewModel() {

    var distance by mutableStateOf("")
        private set
    var consumption by mutableStateOf("")
        private set
    var price by mutableStateOf("")
        private set

    var distanceError by mutableStateOf<String?>(null)
        private set
    var consumptionError by mutableStateOf<String?>(null)
        private set
    var priceError by mutableStateOf<String?>(null)
        private set

    var fuelType by mutableStateOf(FuelType.PETROL)
        private set
    var roundTrip by mutableStateOf(false)
        private set
    var passengersSlider by mutableFloatStateOf(1f)
        private set
    val passengers: Int get() = passengersSlider.roundToInt()

    var result by mutableStateOf<TripResult?>(null)
        private set

    val history = mutableStateListOf<HistoryEntry>()
    private var nextId = 0

    // Pakeitus bet kurią įvestį, senas rezultatas paslepiamas (jis jau nebeatitinka duomenų)
    fun onDistanceChange(value: String) {
        distance = value
        distanceError = null
        result = null
    }

    fun onConsumptionChange(value: String) {
        consumption = value
        consumptionError = null
        result = null
    }

    fun onPriceChange(value: String) {
        price = value
        priceError = null
        result = null
    }

    fun onFuelTypeChange(value: FuelType) {
        fuelType = value
        result = null
    }

    fun onRoundTripChange(value: Boolean) {
        roundTrip = value
        result = null
    }

    fun onPassengersChange(value: Float) {
        passengersSlider = value
        result = null
    }

    /** Patikrina įvestį, apskaičiuoja ir įrašo į istoriją. Grąžina true, jei pavyko. */
    fun calculate(): Boolean {
        distanceError = FuelCalculator.validate(distance, FuelCalculator.MAX_DISTANCE)
        consumptionError = FuelCalculator.validate(consumption, FuelCalculator.MAX_CONSUMPTION)
        priceError = FuelCalculator.validate(price, FuelCalculator.MAX_PRICE)

        if (distanceError != null || consumptionError != null || priceError != null) {
            result = null
            return false
        }

        val newResult = FuelCalculator.calculate(
            distanceKm = FuelCalculator.parse(distance)!!,
            consumptionPer100Km = FuelCalculator.parse(consumption)!!,
            pricePerLiter = FuelCalculator.parse(price)!!,
            roundTrip = roundTrip,
            passengers = passengers,
            fuelType = fuelType
        )
        result = newResult
        history.add(0, HistoryEntry(nextId++, newResult))
        return true
    }

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

    fun deleteEntry(entry: HistoryEntry) {
        history.remove(entry)
    }
}
