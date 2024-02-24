package com.example.zebapplied.co2balance

import com.example.zebapplied.energysource.EnergySourceClient
import com.example.zebapplied.scopehierarchy.ScopeHierarchyClient
import org.springframework.stereotype.Service

@Service
class Co2BalanceService(
    private val energySourceClient: EnergySourceClient,
    private val co2EmissionClient: ScopeHierarchyClient
) {

  fun calculateCo2Balance(request: Co2BalanceRequest): Co2Balance {
    TODO()
  }
}
