package com.example.fuelcalc

// Autoriaus informacija (rodoma meniu "Autorius" ir puslapyje "Apie")
const val AUTORIAUS_VARDAS = "Arnas Povilionis"
const val AUTORIAUS_GRUPE = "MKDF23/2"

// Kuro tipai ir jų pavadinimai ekrane
enum class KuroTipas(val pavadinimas: String) {
    BENZINAS("Benzinas"),
    DYZELINAS("Dyzelinas")
}

// Skaičiavimo rezultatas: visi duomenys, kurie rodomi ekrane ir istorijoje
data class KelionesRezultatas(
    val kuroTipas: KuroTipas,        // kuro tipas
    val visasAtstumasKm: Double,     // visas atstumas (km)
    val pirmynIrAtgal: Boolean,      // ar pirmyn ir atgal
    val litrai: Double,              // kiek kuro reikės (l)
    val bendraKaina: Double,         // bendra kaina (€)
    val keleiviai: Int,              // keleivių skaičius
    val kainaVienam: Double          // kaina vienam žmogui (€)
)

// Skaičiavimo ir įvesties tikrinimo logika
object KuroSkaiciuokle {

    // Didžiausios leistinos reikšmės laukams
    const val MAX_ATSTUMAS = 20000
    const val MAX_SANAUDOS = 50
    const val MAX_KAINA = 10

    /**
     * Apskaičiuoja kelionės kuro kiekį ir kainą.
     * Kuras (l) = atstumas * sąnaudos / 100, kaina = kuras * kuro kaina.
     */
    fun apskaiciuoti(
        atstumasKm: Double,
        sanaudos100Km: Double,
        litroKaina: Double,
        pirmynIrAtgal: Boolean,
        keleiviai: Int,
        kuroTipas: KuroTipas
    ): KelionesRezultatas {
        // Keleivių turi būti nuo 1 iki 5
        require(keleiviai in 1..5) { "Keleivių skaičius turi būti nuo 1 iki 5" }
        // Jei pirmyn ir atgal – atstumas padvigubinamas
        val visasAtstumas = if (pirmynIrAtgal) atstumasKm * 2 else atstumasKm
        // Kuras (l) = atstumas × sąnaudos / 100
        val litrai = visasAtstumas * sanaudos100Km / 100
        // Kaina (€) = kuras × kuro kaina
        val bendraKaina = litrai * litroKaina
        // Grąžina visus rezultatus kartu
        return KelionesRezultatas(
            kuroTipas = kuroTipas,
            visasAtstumasKm = visasAtstumas,
            pirmynIrAtgal = pirmynIrAtgal,
            litrai = litrai,
            bendraKaina = bendraKaina,
            keleiviai = keleiviai,
            // Kaina vienam žmogui = kaina / keleivių skaičius
            kainaVienam = bendraKaina / keleiviai
        )
    }

    /** Paverčia tekstą skaičiumi; tinka ir taškas, ir kablelis (pvz. 6,5). */
    fun paversti(ivestis: String): Double? = ivestis.trim().replace(',', '.').toDoubleOrNull()

    /** Grąžina klaidos tekstą arba null, jei įvestis teisinga. */
    fun patikrinti(ivestis: String, maksimumas: Int): String? {
        val reiksme = paversti(ivestis)
        // Tikrina iš eilės; grąžina pirmą rastą klaidą
        return when {
            // Tuščias laukas
            ivestis.isBlank() -> "Laukas negali būti tuščias"
            // Ne skaičius (pvz. raidės)
            reiksme == null || !reiksme.isFinite() -> "Įveskite skaičių"
            // Nulis arba neigiamas
            reiksme <= 0 -> "Reikšmė turi būti didesnė už 0"
            // Per didelis
            reiksme > maksimumas -> "Reikšmė per didelė (daugiausia $maksimumas)"
            // Viskas gerai – klaidos nėra
            else -> null
        }
    }
}

// Suapvalina skaičių iki 2 skaitmenų po kablelio (pvz. 10.4 → 10,40)
fun Double.formatuoti2(): String = "%.2f".format(this)
