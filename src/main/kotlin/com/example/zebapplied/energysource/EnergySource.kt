package com.example.zebapplied.energysource

data class EnergySource(
    val energySourceId: String,
    val scopeId: String,
    val name: String,
    val conversionFactor: String,
    val emissionFactor: String,
)
