package com.example

import com.example.models.BigDecimalSerializer
import org.junit.Test
import kotlin.test.assertEquals
import java.math.BigDecimal
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.UseSerializers

@Serializable
data class TestModel(
    @Serializable(with = BigDecimalSerializer::class)
    val amount: BigDecimal
)

class KMongoBigDecimalTest {
    @Test
    fun testJsonSerialization() {
        val model = TestModel(BigDecimal("100.50"))
        val json = Json.encodeToString(model)
        assertEquals("{\"amount\":\"100.50\"}", json)
        val decoded = Json.decodeFromString<TestModel>(json)
        assertEquals(BigDecimal("100.50"), decoded.amount)
    }
}
