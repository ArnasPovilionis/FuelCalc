package com.example.fuelcalc

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlin.math.roundToInt

// Vienas istorijos įrašas: unikalus numeris ir rezultatas
data class IstorijosIrasas(val id: Int, val rezultatas: KelionesRezultatas)

// Visi ekrano duomenys ir veiksmai su jais (saugomi atmintyje, kol programėlė veikia)
class KuroViewModel : ViewModel() {

    // Įvesti laukai (tekstas)
    var atstumas by mutableStateOf("")
        private set
    var sanaudos by mutableStateOf("")
        private set
    var kaina by mutableStateOf("")
        private set

    // Klaidų tekstai laukams (null – klaidos nėra)
    var atstumoKlaida by mutableStateOf<String?>(null)
        private set
    var sanaudosKlaida by mutableStateOf<String?>(null)
        private set
    var kainosKlaida by mutableStateOf<String?>(null)
        private set

    // Pasirinktas kuro tipas
    var kuroTipas by mutableStateOf(KuroTipas.BENZINAS)
        private set
    // Ar kelionė pirmyn ir atgal
    var pirmynIrAtgal by mutableStateOf(false)
        private set
    // Slankiklio reikšmė (1.0–5.0)
    var keleiviuSlankiklis by mutableFloatStateOf(1f)
        private set
    // Keleivių skaičius sveikuoju skaičiumi
    val keleiviai: Int get() = keleiviuSlankiklis.roundToInt()

    // Paskutinis rezultatas (null – nerodomas)
    var rezultatas by mutableStateOf<KelionesRezultatas?>(null)
        private set

    // Istorijos sąrašas
    val istorija = mutableStateListOf<IstorijosIrasas>()
    // Kito įrašo numeris
    private var kitasId = 0

    // Pakeitus bet kurią įvestį, senas rezultatas paslepiamas (jis jau nebeatitinka duomenų)

    // Pakeistas atstumas
    fun keistiAtstuma(reiksme: String) {
        atstumas = reiksme
        atstumoKlaida = null
        rezultatas = null
    }

    // Pakeistos sąnaudos
    fun keistiSanaudas(reiksme: String) {
        sanaudos = reiksme
        sanaudosKlaida = null
        rezultatas = null
    }

    // Pakeista kuro kaina
    fun keistiKaina(reiksme: String) {
        kaina = reiksme
        kainosKlaida = null
        rezultatas = null
    }

    // Pakeistas kuro tipas
    fun keistiKuroTipa(reiksme: KuroTipas) {
        kuroTipas = reiksme
        rezultatas = null
    }

    // Perjungtas „pirmyn ir atgal“
    fun keistiPirmynIrAtgal(reiksme: Boolean) {
        pirmynIrAtgal = reiksme
        rezultatas = null
    }

    // Pakeistas keleivių skaičius
    fun keistiKeleivius(reiksme: Float) {
        keleiviuSlankiklis = reiksme
        rezultatas = null
    }

    /** Patikrina įvestį, apskaičiuoja ir įrašo į istoriją. Grąžina true, jei pavyko. */
    fun skaiciuoti(): Boolean {
        // Patikrina visus tris laukus
        atstumoKlaida = KuroSkaiciuokle.patikrinti(atstumas, KuroSkaiciuokle.MAX_ATSTUMAS)
        sanaudosKlaida = KuroSkaiciuokle.patikrinti(sanaudos, KuroSkaiciuokle.MAX_SANAUDOS)
        kainosKlaida = KuroSkaiciuokle.patikrinti(kaina, KuroSkaiciuokle.MAX_KAINA)

        // Jei yra bent viena klaida – neskaičiuoja
        if (atstumoKlaida != null || sanaudosKlaida != null || kainosKlaida != null) {
            rezultatas = null
            return false
        }

        // Paverčia tekstą skaičiais ir skaičiuoja
        val naujasRezultatas = KuroSkaiciuokle.apskaiciuoti(
            atstumasKm = KuroSkaiciuokle.paversti(atstumas)!!,
            sanaudos100Km = KuroSkaiciuokle.paversti(sanaudos)!!,
            litroKaina = KuroSkaiciuokle.paversti(kaina)!!,
            pirmynIrAtgal = pirmynIrAtgal,
            keleiviai = keleiviai,
            kuroTipas = kuroTipas
        )
        // Parodo rezultatą
        rezultatas = naujasRezultatas
        // Įrašo į istorijos pradžią (naujausias viršuje)
        istorija.add(0, IstorijosIrasas(kitasId++, naujasRezultatas))
        return true
    }

    // Išvalo visus laukus ir grąžina numatytuosius nustatymus
    fun isvalytiLaukus() {
        atstumas = ""
        sanaudos = ""
        kaina = ""
        atstumoKlaida = null
        sanaudosKlaida = null
        kainosKlaida = null
        kuroTipas = KuroTipas.BENZINAS
        pirmynIrAtgal = false
        keleiviuSlankiklis = 1f
        rezultatas = null
    }

    // Ištrina įrašą iš istorijos
    fun istrintiIrasa(irasas: IstorijosIrasas) {
        istorija.remove(irasas)
    }
}
