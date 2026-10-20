package com.example.fuelcalc

// Autoriaus informacija (rodoma meniu "Autorius" ir puslapyje "Apie")
const val AUTHOR_NAME = "Arnas Povilionis"
const val AUTHOR_GROUP = "MKDF23/2"

// Kuro tipai ir jų pavadinimai ekrane
enum class FuelType(val label: String) {
    PETROL("Benzinas"),
    DIESEL("Dyzelinas")
}

// Skaičiavimo rezultatas: visi duomenys, kurie rodomi ekrane ir istorijoje
data class TripResult(
    val fuelType: FuelType,         // kuro tipas
    val totalDistanceKm: Double,    // visas atstumas (km)
    val roundTrip: Boolean,         // ar pirmyn ir atgal
    val liters: Double,             // kiek kuro reikės (l)
    val totalCost: Double,          // bendra kaina (€)
    val passengers: Int,            // keleivių skaičius
    val costPerPerson: Double       // kaina vienam žmogui (€)
)

// Skaičiavimo ir įvesties tikrinimo logika
object FuelCalculator {

    // Didžiausios leistinos reikšmės laukams
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
        // Keleivių turi būti nuo 1 iki 5
        require(passengers in 1..5) { "Keleivių skaičius turi būti nuo 1 iki 5" }
        // Jei pirmyn ir atgal – atstumas padvigubinamas
        val totalDistance = if (roundTrip) distanceKm * 2 else distanceKm
        // Kuras (l) = atstumas × sąnaudos / 100
        val liters = totalDistance * consumptionPer100Km / 100
        // Kaina (€) = kuras × kuro kaina
        val totalCost = liters * pricePerLiter
        // Grąžina visus rezultatus kartu
        return TripResult(
            fuelType = fuelType,
            totalDistanceKm = totalDistance,
            roundTrip = roundTrip,
            liters = liters,
            totalCost = totalCost,
            passengers = passengers,
            // Kaina vienam žmogui = kaina / keleivių skaičius
            costPerPerson = totalCost / passengers
        )
    }

    /** Leidžia įvesti skaičių ir su tašku, ir su kableliu (pvz. 6,5). */
    fun parse(input: String): Double? = input.trim().replace(',', '.').toDoubleOrNull()

    /** Grąžina klaidos tekstą arba null, jei įvestis teisinga. */
    fun validate(input: String, max: Int): String? {
        val value = parse(input)
        // Tikrina iš eilės; grąžina pirmą rastą klaidą
        return when {
            // Tuščias laukas
            input.isBlank() -> "Laukas negali būti tuščias"
            // Ne skaičius (pvz. raidės)
            value == null || !value.isFinite() -> "Įveskite skaičių"
            // Nulis arba neigiamas
            value <= 0 -> "Reikšmė turi būti didesnė už 0"
            // Per didelis
            value > max -> "Reikšmė per didelė (daugiausia $max)"
            // Viskas gerai – klaidos nėra
            else -> null
        }
    }
}

// Suapvalina skaičių iki 2 skaitmenų po kablelio (pvz. 10.4 → 10,40)
fun Double.format2(): String = "%.2f".format(this)
