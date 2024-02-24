package com.example.zebapplied.scopehierarchy

interface ScopeService {
  fun getScopes(): List<ScopeInternal>

  fun getParentScope(name: String, scopes: List<ScopeInternal>): ScopeInternal
}
