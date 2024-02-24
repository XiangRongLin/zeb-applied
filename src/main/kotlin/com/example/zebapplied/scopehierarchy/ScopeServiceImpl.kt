package com.example.zebapplied.scopehierarchy

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service

@Service
class ScopeServiceImpl : ScopeService {

  override fun getScopes(): List<ScopeInternal> {
    val mapper = jacksonObjectMapper()
    val file = ClassPathResource("json/scopes.json").file
    // jackson should handle string->float conversion
    val scopes = mapper.readValue<List<Scope>>(file)
    return scopes.flatMap { scope ->
      val parent = ScopeInternal(scope.id, scope.name, scope.label)
      scope.subScopes.map { mapSubScope(it, parent) } + parent
    }
  }

  fun mapSubScope(subScope: SubScope, parent: ScopeInternal): ScopeInternal {
    return ScopeInternal(subScope.id, subScope.name, subScope.label, parent)
  }

  override fun getParentScope(id: String, scopes: List<ScopeInternal>): ScopeInternal {
    return scopes.find { it.id == id }
        ?: throw IllegalArgumentException("No parent scope found for id $id")
  }
}
