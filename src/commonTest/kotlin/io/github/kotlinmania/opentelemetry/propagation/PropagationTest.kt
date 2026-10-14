// port-lint: tests propagation/mod.rs
package io.github.kotlinmania.opentelemetry.propagation

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PropagationTest {
    @Test
    fun hashMapGet() {
        val carrier = MapCarrier()
        carrier.set("headerName", "value")

        assertEquals(
            "value",
            carrier.get("HEADERNAME"),
            "case insensitive extraction",
        )
    }

    @Test
    fun hashMapGetAll() {
        val carrier = MapCarrier()
        carrier.set("headerName", "value")

        assertEquals(
            listOf("value"),
            carrier.getAll("HEADERNAME"),
            "case insensitive get_all extraction",
        )
    }

    @Test
    fun hashMapGetAllMissingKey() {
        val carrier = MapCarrier()
        carrier.set("headerName", "value")

        assertNull(
            carrier.getAll("missing_key"),
            "case insensitive get_all extraction",
        )
    }

    @Test
    fun hashMapKeys() {
        val carrier = MapCarrier()
        carrier.set("headerName1", "value1")
        carrier.set("headerName2", "value2")

        val got = carrier.keys()
        assertEquals(2, got.size)
        assertTrue(got.contains("headername1"))
        assertTrue(got.contains("headername2"))
    }
}
