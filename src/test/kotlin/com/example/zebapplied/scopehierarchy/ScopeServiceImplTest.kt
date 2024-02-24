package com.example.zebapplied.scopehierarchy

import io.kotest.core.spec.style.DescribeSpec
import org.junit.jupiter.api.Assertions.*

class ScopeServiceImplTest: DescribeSpec({
    val scopeService = ScopeServiceImpl()

    describe("getParentScope") {
        it("should return the parent scope") {
            val name = "1.1.1"
            val expected = ScopeInternal("SCOPE_1_1", "1.1", "label1")
            val scopes = listOf(
                expected,
                ScopeInternal("SCOPE_1_2", "1.2", "label2"),
            )

            val actual = scopeService.getParentScope(name, scopes)

            assertEquals(expected, actual)
        }
    }
})
