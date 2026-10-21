// port-lint: source logs/noop.rs
package io.github.kotlinmania.opentelemetry.logs

import io.github.kotlinmania.opentelemetry.InstrumentationScope
import io.github.kotlinmania.opentelemetry.Key
import kotlin.time.Instant

/**
 * A no-op implementation of a [LoggerProvider].
 */
public class NoopLoggerProvider : LoggerProvider {
    override fun loggerWithScope(scope: InstrumentationScope): Logger = NoopLogger()

    public companion object {
        public fun new(): NoopLoggerProvider = NoopLoggerProvider()
    }
}

/**
 * A no-operation log record that implements the [LogRecord] interface.
 */
public class NoopLogRecord : LogRecord {
    override fun setEventName(name: String) {}

    override fun setTarget(target: String) {}

    override fun setTimestamp(timestamp: Instant) {}

    override fun setObservedTimestamp(timestamp: Instant) {}

    override fun setSeverityText(text: String) {}

    override fun setSeverityNumber(number: Severity) {}

    override fun setBody(body: AnyValue) {}

    override fun addAttributes(attributes: Iterable<Pair<Key, AnyValue>>) {}

    override fun addAttribute(key: Key, value: AnyValue) {}
}

/**
 * A no-op implementation of a [Logger].
 */
public class NoopLogger : Logger {
    override fun createLogRecord(): LogRecord = NoopLogRecord()

    override fun emit(record: LogRecord) {}

    override fun eventEnabled(level: Severity, target: String, name: String?): Boolean = false

    public companion object {
        public fun new(): NoopLogger = NoopLogger()
    }
}
