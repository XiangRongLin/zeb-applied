package com.example.zebapplied.co2balance

import com.example.zebapplied.energysource.EnergySource
import com.example.zebapplied.energysource.EnergySourceClient
import com.example.zebapplied.scopehierarchy.ScopeService
import com.example.zebapplied.scopehierarchy.ScopeInternal
import org.springframework.stereotype.Service

@Service
class Co2BalanceService(
    private val energySourceClient: EnergySourceClient,
    private val scopeService: ScopeService
) {


  fun calculateCo2Balance(requests: List<Co2BalanceRequest>): List<Co2Balance> {
    val energySources = energySourceClient.getEnergySources()
    val scopeHierarchies = scopeService.getScopes()

    val leaves = requests.map { calculateCo2Balance(it, energySources, scopeHierarchies)}
    val tree = buildCo2BalanceTree(leaves)
    return convertTreeToCo2Balance(requests, tree)
  }

  fun calculateCo2Balance(request: Co2BalanceRequest, energySources: List<EnergySource>, scopeHierarchies: List<ScopeInternal>): Co2BalanceInternal {
    val energySource = energySources.find { it.energySourceId == request.energySourceId }
        ?: throw IllegalArgumentException("Energy source with id ${request.energySourceId} not found")
    val scope = scopeService.getParentScope(energySource.scopeId, scopeHierarchies)
    val energyInKwh = request.energyUsage * energySource.conversionFactor
    val emmissionFactor = request.emissionFactor ?: energySource.emissionFactor
    return Co2BalanceInternal(
        name = request.description,
        energy = energyInKwh,
        co2 = energyInKwh * emmissionFactor / 1000,
        scope = scope,
        label = "${energySource.name} ${request.description}"
    )
  }

  fun buildCo2BalanceTree(leaves: List<Co2BalanceInternal>): List<Co2BalanceInternal> {
    val groupedByBranches = leaves.groupBy { it.scope }
    val branches = groupedByBranches.map { (scope, leaves) ->
      Co2BalanceInternal(
          name = scope.name,
          energy = leaves.sumOf { it.energy.toDouble() }.toFloat(),
          co2 = leaves.sumOf { it.co2.toDouble() }.toFloat(),
          scope = scope,
          children = leaves)
    }
    val groupedByRoots: Map<ScopeInternal, List<Co2BalanceInternal>> = branches.groupBy { it.scope.parent!! } //TODO !!
    val roots = groupedByRoots.map { (scope, branches) ->
      Co2BalanceInternal(
          name = scope.name,
          energy = branches.sumOf { it.energy.toDouble() }.toFloat(),
          co2 = branches.sumOf { it.co2.toDouble() }.toFloat(),
          scope = scope,
          children = branches)
    }
    return roots
  }

  fun convertTreeToCo2Balance(request: List<Co2BalanceRequest>, tree: List<Co2BalanceInternal>): List<Co2Balance> {
    return tree.map { it.toCo2Balance(request) }
  }

  fun Co2BalanceInternal.toCo2Balance(requests: List<Co2BalanceRequest>): Co2Balance {
      return Co2Balance(
          name = name,
          label = label ?: scope.label,
          energy = energy,
          co2 = co2,
          children = children.map { it.toCo2Balance(requests) }
      )
  }

}

