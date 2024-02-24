package com.example.zebapplied.co2balance

data class Co2BalanceRequest(
    val description: String,
    val energySourceId: String,
    val energyUsage: Float,
    val emissionFactor: Float? = null,
)
