package com.example.fuelcalc

import org.junit.Assert.assertEquals
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
}
