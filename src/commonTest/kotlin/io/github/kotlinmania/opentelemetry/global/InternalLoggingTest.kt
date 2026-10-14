// port-lint: tests global/internal_logging.rs
package io.github.kotlinmania.opentelemetry.global

import kotlin.test.Test

class InternalLoggingTest {
    @Test
    fun internalLoggingInvocationSucceeds() {
        otelInfo("test_info", "version" to "1.0.0")
        otelWarn("test_warn", "reason" to "degraded")
        otelDebug("test_debug", "detail" to 42)
        otelError("test_error", "code" to 500)
    }
}
