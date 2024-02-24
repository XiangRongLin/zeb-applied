package com.example.zebapplied.scopehierarchy

data class Scope(
    val id: String,
    val name: String,
    val label: String,
    val subScopes: List<SubScope> = emptyList()
)

data class SubScope(
    val id: String,
    val name: String,
    val label: String
)

data class ScopeInternal(
    val id: String,
    val name: String,
    val label: String,
    val parent: ScopeInternal? = null
)
