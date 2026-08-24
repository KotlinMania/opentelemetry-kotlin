// port-lint: tests tmp/opentelemetry/src/common.rs
package io.github.kotlinmania.opentelemetry

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CommonTest {
    @Test
    fun testKey() {
        val key1 = Key.from("service.name")
        val key2 = Key("service.name")
        assertEquals(key1, key2)
        assertEquals("service.name", key1.asString())
    }

    @Test
    fun testKeyValue() {
        val kvStr = KeyValue("http.status_code", 200L)
        assertEquals("http.status_code", kvStr.key.name)
        assertEquals(Value.of(200L), kvStr.value)

        val kvBool = KeyValue("success", true)
        assertEquals(Value.of(true), kvBool.value)
    }

    @Test
    fun testInstrumentationScope() {
        val scope = InstrumentationScope.builder("my-instrumentation")
            .withVersion("1.0.0")
            .withSchemaUrl("https://opentelemetry.io/schemas/1.21.0")
            .withAttributes(listOf(KeyValue("env", "prod")))
            .build()

        assertEquals("my-instrumentation", scope.name)
        assertEquals("1.0.0", scope.version)
        assertEquals("https://opentelemetry.io/schemas/1.21.0", scope.schemaUrl)
        assertEquals(1, scope.attributes.size)
    }

    @Test
    fun testBaggage() {
        val baggage = Baggage.new()
        baggage.insert("user_id", "12345", "role=admin")

        assertEquals(StringValue("12345"), baggage.get("user_id"))
        assertEquals(BaggageMetadata("role=admin"), baggage.getMetadata("user_id"))
        assertEquals(1, baggage.size())

        baggage.remove("user_id")
        assertNull(baggage.get("user_id"))
        assertTrue(baggage.isEmpty())
    }
}
