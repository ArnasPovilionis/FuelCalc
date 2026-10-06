package com.example.fuelcalc

// Autoriaus informacija (rodoma meniu "Autorius" ir puslapyje "Apie")
const val AUTHOR_NAME = "Arnas Povilionis"
const val AUTHOR_GROUP = "MKDF23/2"

enum class FuelType(val label: String) {
    PETROL("Benzinas"),
    DIESEL("Dyzelinas")
}

data class TripResult(
    val fuelType: FuelType,
    val totalDistanceKm: Double,
    val roundTrip: Boolean,
    val liters: Double,
    val totalCost: Double,
    val passengers: Int,
    val costPerPerson: Double
)

object FuelCalculator {

    const val MAX_DISTANCE = 20000
    const val MAX_CONSUMPTION = 50
    const val MAX_PRICE = 10

    /**
     * Apskaičiuoja kelionės kuro kiekį ir kainą.
     * Kuras (l) = atstumas * sąnaudos / 100, kaina = kuras * kuro kaina.
     */
    fun calculate(
        distanceKm: Double,
        consumptionPer100Km: Double,
        pricePerLiter: Double,
        roundTrip: Boolean,
        passengers: Int,
        fuelType: FuelType
    ): TripResult {
        require(passengers in 1..5) { "Keleivių skaičius turi būti nuo 1 iki 5" }
        val totalDistance = if (roundTrip) distanceKm * 2 else distanceKm
        val liters = totalDistance * consumptionPer100Km / 100
        val totalCost = liters * pricePerLiter
        return TripResult(
            fuelType = fuelType,
            totalDistanceKm = totalDistance,
            roundTrip = roundTrip,
            liters = liters,
            totalCost = totalCost,
            passengers = passengers,
            costPerPerson = totalCost / passengers
        )
    }

    /** Leidžia įvesti skaičių ir su tašku, ir su kableliu (pvz. 6,5). */
    fun parse(input: String): Double? = input.trim().replace(',', '.').toDoubleOrNull()

    /** Grąžina klaidos tekstą arba null, jei įvestis teisinga. */
    fun validate(input: String, max: Int): String? {
        val value = parse(input)
        return when {
            input.isBlank() -> "Laukas negali būti tuščias"
            value == null || !value.isFinite() -> "Įveskite skaičių"
            value <= 0 -> "Reikšmė turi būti didesnė už 0"
            value > max -> "Reikšmė per didelė (daugiausia $max)"
            else -> null
        }
    }
}

fun Double.format2(): String = "%.2f".format(this)
