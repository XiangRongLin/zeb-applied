package com.example.zebapplied.co2balance

import com.example.zebapplied.energysource.EnergySource
import com.example.zebapplied.energysource.EnergySourceClient
import com.example.zebapplied.scopehierarchy.ScopeInternal
import com.example.zebapplied.scopehierarchy.ScopeService
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

/**
 * Tests are not quite clean, because I'm testing internal stuff.
 * Normally those methods should be private, but that would be a pain to test.
 * Also it's bare bones
 */
class Co2BalanceServiceTest : DescribeSpec({

  val energySourceClient = mockk<EnergySourceClient>(relaxed = true)
  val scopeService = mockk<ScopeService>(relaxed = true)
  val service = Co2BalanceService(energySourceClient, scopeService)

  describe("calculateCo2Balance") {
    val expecetedName = "1.1.1"
    val energySourceId = "1"
    val expectedEnergySourceName = "energySourceName1"
    val request = Co2BalanceRequest(name = expecetedName, energySourceId = energySourceId, energyUsage = 1.5f, emissionFactor = 2f)
    val energySources = listOf(EnergySource(energySourceId = energySourceId, scopeId = "SCOPE_2_1", name = expectedEnergySourceName, conversionFactor = 1500f, emissionFactor = 3f))

    it("should return correct name and energy source name") {
      val actual = service.calculateCo2Balance(request, energySources, emptyList())

      actual.name shouldBe expecetedName
      actual.energySourceName shouldBe expectedEnergySourceName
    }

    it("should calculate energy correctly") {
      val actual = service.calculateCo2Balance(request, energySources, emptyList())

      actual.energy shouldBe 2250f
    }

    it("should calculate co2 correctly if request emmissionFactor is NOT set") {
      val requestWithoutEmissionFactor = Co2BalanceRequest(name = expecetedName, energySourceId = energySourceId, energyUsage = 1.5f, emissionFactor = null)

      val actual = service.calculateCo2Balance(requestWithoutEmissionFactor, energySources, emptyList())

      actual.co2 shouldBe 6.75f
    }
    it("should calculate co2 correctly if request emmissionFactor is set") {
      val actual = service.calculateCo2Balance(request, energySources, emptyList())

      actual.co2 shouldBe 4.5f
    }

    it("should return the correct scope") {
      val scope = ScopeInternal("SCOPE_2_1", "1.1", "label1")
      every { scopeService.getParentScope(any(), any()) } returns scope

      val actual = service.calculateCo2Balance(request, energySources, emptyList())

      actual.scope shouldBe scope
    }
  }

  describe("buildCo2BalanceTree") {
    val root1Scope = ScopeInternal("SCOPE_1", "Scope 1", "label1")
    val root2Scope = ScopeInternal("SCOPE_2", "Scope 2", "label2")
    val branch11Scope = ScopeInternal("SCOPE_1_1", "1.1", "label1", parent = root1Scope)
    val branch12Scope = ScopeInternal("SCOPE_1_2", "1.2", "label2", root1Scope)
    val branch21Scope = ScopeInternal("SCOPE_2_1", "2.1", "label3", root2Scope)
    val leave111Balance = Co2BalanceInternal(name = "1.1.1", energySourceName = "energySourceName1", energy = 1f, co2 = 2f, scope = branch11Scope)
    val leave112Balance = Co2BalanceInternal(name = "1.1.2", energySourceName = "energySourceName1", energy = 3f, co2 = 4f, scope = branch11Scope)
    val leave121Balance = Co2BalanceInternal(name = "1.2.1", energySourceName = "energySourceName2", energy = 5f, co2 = 6f, scope = branch12Scope)
    val leave211Balance = Co2BalanceInternal(name = "2.1.1", energySourceName = "energySourceName3", energy = 7f, co2 = 8f, scope = branch21Scope)
    val leaves = listOf(leave111Balance, leave112Balance, leave121Balance, leave211Balance)

    it("should build tree correctly") {
      val tree = service.buildCo2BalanceTree(leaves)

      tree.size shouldBe 2
      val root1 = tree.find { it.name == root1Scope.name }.shouldNotBeNull()
      root1.children.size shouldBe 2
      val branch11 = root1.children.find { it.name == branch11Scope.name }.shouldNotBeNull()
      branch11.children.size shouldBe 2
      branch11.children.find { it.name == leave111Balance.name }.shouldNotBeNull()
      branch11.children.find { it.name == leave112Balance.name }.shouldNotBeNull()
      val branch12 = root1.children.find { it.name == branch12Scope.name }.shouldNotBeNull()
      branch12.children.size shouldBe 1
      branch12.children.find { it.name == leave121Balance.name }.shouldNotBeNull()
      val root2 = tree.find { it.name == root2Scope.name }.shouldNotBeNull()
      root2.children.size shouldBe 1
      val branch21 = root2.children.find { it.name == branch21Scope.name }.shouldNotBeNull()
      branch21.children.size shouldBe 1
      branch21.children.find { it.name == leave211Balance.name }.shouldNotBeNull()
    }

    it ("should sum up the energy correctly") {
      val tree = service.buildCo2BalanceTree(leaves)

      val root1 = tree.find { it.name == root1Scope.name }.shouldNotBeNull()
      root1.energy shouldBe 9f
      val branch11 = root1.children.find { it.name == branch11Scope.name }.shouldNotBeNull()
      branch11.energy shouldBe 4f
      val branch12 = root1.children.find { it.name == branch12Scope.name }.shouldNotBeNull()
      branch12.energy shouldBe 5f
      val root2 = tree.find { it.name == root2Scope.name }.shouldNotBeNull()
      root2.energy shouldBe 7f
      val branch21 = root2.children.find { it.name == branch21Scope.name }.shouldNotBeNull()
      branch21.energy shouldBe 7f
    }

    it("should sum up co2 correctly") {
      val tree = service.buildCo2BalanceTree(leaves)

      val root1 = tree.find { it.name == root1Scope.name }.shouldNotBeNull()
      root1.co2 shouldBe 12f
      val branch11 = root1.children.find { it.name == branch11Scope.name }.shouldNotBeNull()
      branch11.co2 shouldBe 6f
      val branch12 = root1.children.find { it.name == branch12Scope.name }.shouldNotBeNull()
      branch12.co2 shouldBe 6f
      val root2 = tree.find { it.name == root2Scope.name }.shouldNotBeNull()
      root2.co2 shouldBe 8f
      val branch21 = root2.children.find { it.name == branch21Scope.name }.shouldNotBeNull()
      branch21.co2 shouldBe 8f
    }

  }
})
