package com.example.zebapplied.co2balance

import com.example.zebapplied.energysource.EnergySource
import com.example.zebapplied.energysource.EnergySourceClient
import com.example.zebapplied.scopehierarchy.ScopeClient
import com.example.zebapplied.scopehierarchy.ScopeInternal
import org.springframework.stereotype.Service

@Service
class Co2BalanceService(
    private val energySourceClient: EnergySourceClient,
    private val scopeClient: ScopeClient
) {

  private val scopeRegex = Regex("^\\d+\\.\\d")

  fun calculateCo2Balance(requests: List<Co2BalanceRequest>): List<Co2Balance> {
    val energySources = energySourceClient.getEnergySources()
    val scopeHierarchies = scopeClient.getScopes()

    val leaves = requests.map { calculateCo2Balance(it, energySources, scopeHierarchies)}
    return buildCo2BalanceTree(leaves)
  }

  fun buildCo2BalanceTree(leaves: List<Co2BalanceInternal>): List<Co2Balance> {
    val groupedByBranches = leaves.groupBy { it.scope }
    val branches = groupedByBranches.map { (scope, leaves) ->
      Co2BalanceInternal(
          name = scope.name,
          energySourceName = scope.label,
          energy = leaves.sumOf { it.energy.toDouble() }.toFloat(),
          co2 = leaves.sumOf { it.co2.toDouble() }.toFloat(),
          scope = scope,
          children = leaves)
    }
    val groupedByRoots: Map<ScopeInternal, List<Co2BalanceInternal>> = branches.groupBy { it.scope.parent!! } //TODO !!
    val roots = groupedByRoots.map { (scope, branches) ->
      Co2BalanceInternal(
          name = scope.name,
          energySourceName = scope.label,
          energy = branches.sumOf { it.energy.toDouble() }.toFloat(),
          co2 = branches.sumOf { it.co2.toDouble() }.toFloat(),
          scope = scope,
          children = branches)
    }
    return roots.map {
      it.toCo2Balance()
    }
  }

  fun Co2BalanceInternal.toCo2Balance(): Co2Balance {
    return Co2Balance(
        name = name,
        label = energySourceName,
        energy = energy,
        co2 = co2,
        children = children.map { it.toCo2Balance() }
    )
  }

  fun calculateCo2Balance(request: Co2BalanceRequest, energySources: List<EnergySource>, scopeHierarchies: List<ScopeInternal>): Co2BalanceInternal {
    val energySource = energySources.find { it.energySourceId == request.energySourceId }
        ?: throw IllegalArgumentException("Energy source with id ${request.energySourceId} not found")
    val scope = getParentScope(request.name, scopeHierarchies)
    val energyInKwh = request.energyUsage * energySource.conversionFactor
    val emmissionFactor = request.emissionFactor ?: energySource.emissionFactor
    return Co2BalanceInternal(
        name = request.name,
        energySourceName = energySource.name,
        energy = energyInKwh,
        co2 = energyInKwh * emmissionFactor / 1000,
        scope = scope
    )
  }

  fun getParentScope(name: String, scopes: List<ScopeInternal>): ScopeInternal {
    return scopes.find { it.name == scopeRegex.find(name)?.value }
        ?: throw IllegalArgumentException("No parent scope found for $name")
  }
}
