// port-lint: tests common.rs
package io.github.kotlinmania.opentelemetry

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class CommonTest {
    fun hashHelper(item: Any): Long {
        return item.hashCode().toLong()
    }
    @Test
    fun kvFloatEquality() {
        val kv1 = KeyValue.new("key", 1.0)
        val kv2 = KeyValue.new("key", 1.0)
        assertEquals(kv1, kv2)

        val kv3 = KeyValue.new("key", 1.0)
        val kv4 = KeyValue.new("key", 1.01)
        assertNotEquals(kv3, kv4)

        val kvNan1 = KeyValue.new("key", Double.NaN)
        val kvNan2 = KeyValue.new("key", Double.NaN)
        assertNotEquals(kvNan1, kvNan2)

        val floatValues = listOf(
            Double.POSITIVE_INFINITY,
            Double.NEGATIVE_INFINITY,
            Double.MAX_VALUE,
            Double.MIN_VALUE,
        )
        for (floatVal in floatValues) {
            val a = KeyValue.new("key", floatVal)
            val b = KeyValue.new("key", floatVal)
            assertEquals(a, b)
        }

        for (i in 0 until 100) {
            val randomValue = Random.nextDouble()
            val a = KeyValue.new("key", randomValue)
            val b = KeyValue.new("key", randomValue)
            assertEquals(a, b)
        }
    }

    @Test
    fun kvFloatHash() {
        val floatValues = listOf(
            Double.NaN,
            Double.POSITIVE_INFINITY,
            Double.NEGATIVE_INFINITY,
            Double.MAX_VALUE,
            Double.MIN_VALUE,
        )
        for (floatVal in floatValues) {
            val kv1 = KeyValue.new("key", floatVal)
            val kv2 = KeyValue.new("key", floatVal)
            assertEquals(hashHelper(kv1), hashHelper(kv2))
        }

        for (i in 0 until 100) {
            val randomValue = Random.nextDouble()
            val kv1 = KeyValue.new("key", randomValue)
            val kv2 = KeyValue.new("key", randomValue)
            assertEquals(hashHelper(kv1), hashHelper(kv2))
        }
    }

    @Test
    fun instrumentationScopeEquality() {
        val scope1 = InstrumentationScope.builder("my-crate")
            .withVersion("v0.1.0")
            .withSchemaUrl("https://opentelemetry.io/schemas/1.17.0")
            .withAttributes(listOf(KeyValue.new("k", "v")))
            .build()
        val scope2 = InstrumentationScope.builder("my-crate")
            .withVersion("v0.1.0")
            .withSchemaUrl("https://opentelemetry.io/schemas/1.17.0")
            .withAttributes(listOf(KeyValue.new("k", "v")))
            .build()
        assertEquals(scope1, scope2)
    }

    @Test
    fun instrumentationScopeEqualityAttributesDiffOrder() {
        val scope1 = InstrumentationScope.builder("my-crate")
            .withVersion("v0.1.0")
            .withSchemaUrl("https://opentelemetry.io/schemas/1.17.0")
            .withAttributes(listOf(KeyValue.new("k1", "v1"), KeyValue.new("k2", "v2")))
            .build()
        val scope2 = InstrumentationScope.builder("my-crate")
            .withVersion("v0.1.0")
            .withSchemaUrl("https://opentelemetry.io/schemas/1.17.0")
            .withAttributes(listOf(KeyValue.new("k2", "v2"), KeyValue.new("k1", "v1")))
            .build()
        assertEquals(scope1, scope2)
        assertEquals(hashHelper(scope1), hashHelper(scope2))
    }

    @Test
    fun instrumentationScopeEqualityDifferentAttributes() {
        val scope1 = InstrumentationScope.builder("my-crate")
            .withVersion("v0.1.0")
            .withSchemaUrl("https://opentelemetry.io/schemas/1.17.0")
            .withAttributes(listOf(KeyValue.new("k1", "v1"), KeyValue.new("k2", "v2")))
            .build()
        val scope2 = InstrumentationScope.builder("my-crate")
            .withVersion("v0.1.0")
            .withSchemaUrl("https://opentelemetry.io/schemas/1.17.0")
            .withAttributes(listOf(KeyValue.new("k2", "v3"), KeyValue.new("k4", "v5")))
            .build()
        assertNotEquals(scope1, scope2)
        assertNotEquals(hashHelper(scope1), hashHelper(scope2))
    }
}
