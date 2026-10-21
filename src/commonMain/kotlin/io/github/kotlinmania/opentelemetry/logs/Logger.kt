// port-lint: source logs/logger.rs
package io.github.kotlinmania.opentelemetry.logs

import io.github.kotlinmania.opentelemetry.InstrumentationScope

/**
 * The interface for emitting [LogRecord]s.
 */
public interface Logger {
    public fun createLogRecord(): LogRecord

    public fun emit(record: LogRecord)

    public fun eventEnabled(level: Severity, target: String, name: String? = null): Boolean = false
}

/**
 * Interfaces that can create [Logger] instances.
 */
public interface LoggerProvider {
    public fun loggerWithScope(scope: InstrumentationScope): Logger

    public fun logger(name: String): Logger {
        val scope = InstrumentationScope.builder(name).build()
        return loggerWithScope(scope)
    }
}
