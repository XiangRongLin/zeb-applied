package com.example.zebapplied.co2balance

data class Co2BalanceRequest(
    val name: String,
    val id: String,
    val energyUsage: Float,
    val emissionFactor: Float? = null,
)
