package com.example.zebapplied.scopehierarchy

import org.springframework.stereotype.Service

@Service
class ScopeServiceImpl : ScopeService {

  private val scopeRegex = Regex("^\\d+\\.\\d")

  override fun getScopes(): List<ScopeInternal> {
    TODO("Not yet implemented")
  }

  override fun getParentScope(name: String, scopes: List<ScopeInternal>) : ScopeInternal{
    return scopes.find { it.name == scopeRegex.find(name)?.value }
        ?: throw IllegalArgumentException("No parent scope found for $name")
  }
}
