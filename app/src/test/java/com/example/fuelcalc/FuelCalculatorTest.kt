package com.example.fuelcalc

import org.junit.Assert.assertEquals
import org.junit.Test

class FuelCalculatorTest {

    private val paklaida = 0.001

    // Pagrindinis atvejis: kelionė į vieną pusę, vienas keleivis
    @Test
    fun apskaiciuoti_iVienaPuse_teisingiLitraiIrKaina() {
        val rezultatas = KuroSkaiciuokle.apskaiciuoti(
            atstumasKm = 100.0,
            sanaudos100Km = 6.5,
            litroKaina = 1.60,
            pirmynIrAtgal = false,
            keleiviai = 1,
            kuroTipas = KuroTipas.BENZINAS
        )
        // Kuras (l) = atstumas × sąnaudos / 100 = 100 × 6,5 / 100 = 6,5 l
        assertEquals(6.5, rezultatas.litrai, paklaida)
        // Kaina (€) = kuras × kuro kaina = 6,5 × 1,60 = 10,40 €
        assertEquals(10.40, rezultatas.bendraKaina, paklaida)
        // Kaina vienam žmogui = kaina / keleivių skaičius = 10,40 / 1 = 10,40 €
        assertEquals(10.40, rezultatas.kainaVienam, paklaida)
    }
}
