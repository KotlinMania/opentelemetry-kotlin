// port-lint: source global/internal_logging.rs
package io.github.kotlinmania.opentelemetry.global

import io.github.kotlinmania.tracing.debug as tracingDebug
import io.github.kotlinmania.tracing.error as tracingError
import io.github.kotlinmania.tracing.info as tracingInfo
import io.github.kotlinmania.tracing.warn as tracingWarn

/**
 * Internal logging utilities for OpenTelemetry code.
 */
public object InternalLogging {
    public fun info(name: String, vararg attributes: Pair<String, Any?>) {
        tracingInfo("otel_info", "name" to name, *attributes)
    }

    public fun warn(name: String, vararg attributes: Pair<String, Any?>) {
        tracingWarn("otel_warn", "name" to name, *attributes)
    }

    public fun debug(name: String, vararg attributes: Pair<String, Any?>) {
        tracingDebug("otel_debug", "name" to name, *attributes)
    }

    public fun error(name: String, vararg attributes: Pair<String, Any?>) {
        tracingError("otel_error", "name" to name, *attributes)
    }
}

public fun otelInfo(name: String, vararg attributes: Pair<String, Any?>) {
    InternalLogging.info(name, *attributes)
}

public fun otelWarn(name: String, vararg attributes: Pair<String, Any?>) {
    InternalLogging.warn(name, *attributes)
}

public fun otelDebug(name: String, vararg attributes: Pair<String, Any?>) {
    InternalLogging.debug(name, *attributes)
}

public fun otelError(name: String, vararg attributes: Pair<String, Any?>) {
    InternalLogging.error(name, *attributes)
}
