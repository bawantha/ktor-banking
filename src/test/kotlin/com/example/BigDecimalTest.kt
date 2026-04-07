package com.example

import com.example.models.*
import org.junit.Test
import kotlin.test.assertEquals
import java.math.BigDecimal
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.UseSerializers

class BigDecimalTest {
    @Test
    fun testSerialization() {
        val money = BigDecimal("100.50")
        assertEquals(BigDecimal("100.50"), money)
    }
}
