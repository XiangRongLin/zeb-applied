package com.example.zebapplied.energysource

interface EnergySourceClient {

  fun getEnergySource(id: String): EnergySource
}
