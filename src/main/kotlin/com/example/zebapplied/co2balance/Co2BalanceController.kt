package com.example.zebapplied.co2balance

import org.springframework.web.bind.annotation.RestController

@RestController
class Co2BalanceController(
    private val co2BalanceService: Co2BalanceService
) {

  fun getCo2Balance(): Co2Balance {
        TODO()
    }
}
