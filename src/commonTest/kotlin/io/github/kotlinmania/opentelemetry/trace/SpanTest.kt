// port-lint: tests trace/span.rs
package io.github.kotlinmania.opentelemetry.trace

import kotlin.test.Test
import kotlin.test.assertTrue

class SpanTest {
    @Test
    fun statusOrder() {
        assertTrue(Status.Ok > Status.error(""))
        assertTrue(Status.error("") > Status.Unset)
    }
}
