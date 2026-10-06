package com.example.fuelcalc

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class FuelCalculatorTest {

    private val delta = 0.001

    // Pagrindinis atvejis: kelionė į vieną pusę, vienas keleivis
    @Test
    fun calculate_oneWay_returnsCorrectLitersAndCost() {
        val result = FuelCalculator.calculate(
            distanceKm = 100.0,
            consumptionPer100Km = 6.5,
            pricePerLiter = 1.60,
            roundTrip = false,
            passengers = 1,
            fuelType = FuelType.PETROL
        )
        assertEquals(6.5, result.liters, delta)
        assertEquals(10.40, result.totalCost, delta)
        assertEquals(10.40, result.costPerPerson, delta)
    }

    // Switch "Kelionė pirmyn ir atgal" turi padvigubinti atstumą, kurą ir kainą
    @Test
    fun calculate_roundTrip_doublesDistanceAndCost() {
        val result = FuelCalculator.calculate(150.0, 5.0, 1.50, true, 1, FuelType.DIESEL)
        assertEquals(300.0, result.totalDistanceKm, delta)
        assertEquals(15.0, result.liters, delta)
        assertEquals(22.50, result.totalCost, delta)
    }

    // Slider keleiviams: bendra kaina nesikeičia, bet padalinama keleiviams
    @Test
    fun calculate_multiplePassengers_splitsCostPerPerson() {
        val result = FuelCalculator.calculate(200.0, 7.0, 1.70, false, 4, FuelType.PETROL)
        assertEquals(23.80, result.totalCost, delta)
        assertEquals(5.95, result.costPerPerson, delta)
    }

    // Visi parametrai kartu: pirmyn-atgal ir 5 keleiviai
    @Test
    fun calculate_roundTripWithFivePassengers() {
        val result = FuelCalculator.calculate(250.0, 8.0, 1.50, true, 5, FuelType.PETROL)
        assertEquals(40.0, result.liters, delta)
        assertEquals(60.0, result.totalCost, delta)
        assertEquals(12.0, result.costPerPerson, delta)
    }

    // Neteisinga įvestis turi būti atmesta su klaidos pranešimu
    @Test
    fun validate_invalidInput_returnsError() {
        assertNotNull(FuelCalculator.validate("", 100))
        assertNotNull(FuelCalculator.validate("abc", 100))
        assertNotNull(FuelCalculator.validate("0", 100))
        assertNotNull(FuelCalculator.validate("-5", 100))
        assertNotNull(FuelCalculator.validate("101", 100))
    }

    // Teisinga įvestis (ir su kableliu, kaip įprasta Lietuvoje) priimama
    @Test
    fun validate_validInput_returnsNull() {
        assertNull(FuelCalculator.validate("6.5", 100))
        assertNull(FuelCalculator.validate("6,5", 100))
        assertEquals(6.5, FuelCalculator.parse("6,5")!!, delta)
    }
}
