package com.example.zebapplied.scopehierarchy

interface ScopeService {
  fun getScopes(): List<ScopeInternal>

  fun getParentScope(id: String, scopes: List<ScopeInternal>): ScopeInternal
}
