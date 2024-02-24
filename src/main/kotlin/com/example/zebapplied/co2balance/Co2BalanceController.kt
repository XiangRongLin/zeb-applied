package com.example.zebapplied.co2balance

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class Co2BalanceController(
    private val co2BalanceService: Co2BalanceService
) {

  @PostMapping("/co2balance")
  fun getCo2Balance(
      @RequestBody request: List<Co2BalanceRequest>
  ): List<Co2Balance> {
        return co2BalanceService.calculateCo2Balance(request)
    }
}
