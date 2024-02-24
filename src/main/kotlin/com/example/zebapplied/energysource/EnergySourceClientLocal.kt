package com.example.zebapplied.energysource

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service

@Service
class EnergySourceClientLocal : EnergySourceClient{

  /**
   * Fetch energy sources from resouces folder and return them as a list
   */
  override fun getEnergySources(): List<EnergySource> {
    val mapper = jacksonObjectMapper()
    val file = ClassPathResource("json/energy-sources.json").file
    // jackson should handle string->float conversion
    return mapper.readValue(file)
  }
}
