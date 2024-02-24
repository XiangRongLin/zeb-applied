package com.example.zebapplied.co2balance

import com.example.zebapplied.scopehierarchy.ScopeInternal
import com.fasterxml.jackson.annotation.JsonIgnore

data class Co2Balance(
    val name: String,
    val label: String,
    // TODO format to 2 decimal places
    val energy: Float,
    // TODO format to 2 decimal places
    val co2: Float,
    /**
     * If no children are present an empty list is returned
     */
    val children: List<Co2Balance> = emptyList(),
)

data class Co2BalanceInternal(
    val name: String,
    val energySourceName: String,
    // TODO format to 2 decimal places
    val energy: Float,
    // TODO format to 2 decimal places
    val co2: Float,
    val scope : ScopeInternal,
    val children: List<Co2BalanceInternal> = emptyList(),
)
