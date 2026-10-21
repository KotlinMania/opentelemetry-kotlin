// port-lint: source logs/record.rs
package io.github.kotlinmania.opentelemetry.logs

import io.github.kotlinmania.opentelemetry.Key
import io.github.kotlinmania.opentelemetry.SpanId
import io.github.kotlinmania.opentelemetry.StringValue
import io.github.kotlinmania.opentelemetry.TraceFlags
import io.github.kotlinmania.opentelemetry.TraceId
import kotlin.time.Instant

/**
 * SDK implemented interface for managing log records.
 */
public interface LogRecord {
    public fun setEventName(name: String)

    public fun setTarget(target: String)

    public fun setTimestamp(timestamp: Instant)

    public fun setObservedTimestamp(timestamp: Instant)

    public fun setSeverityText(text: String)

    public fun setSeverityNumber(number: Severity)

    public fun setBody(body: AnyValue)

    public fun addAttributes(attributes: Iterable<Pair<Key, AnyValue>>)

    public fun addAttribute(key: Key, value: AnyValue)

    public fun setTraceContext(
        traceId: TraceId,
        spanId: SpanId,
        traceFlags: TraceFlags?,
    ) {
    }
}

/**
 * Value types for representing arbitrary values in a log record.
 */
public sealed class AnyValue {
    public data class Int(
        public val value: Long,
    ) : AnyValue()

    public data class Double(
        public val value: kotlin.Double,
    ) : AnyValue()

    public data class String(
        public val value: StringValue,
    ) : AnyValue()

    public data class Boolean(
        public val value: kotlin.Boolean,
    ) : AnyValue()

    public class Bytes(
        public val value: ByteArray,
    ) : AnyValue() {
        override fun equals(other: Any?): kotlin.Boolean =
            this === other || (other is Bytes && value.contentEquals(other.value))

        override fun hashCode(): kotlin.Int = value.contentHashCode()
    }

    public data class ListAny(
        public val value: List<AnyValue>,
    ) : AnyValue()

    public data class Map(
        public val value: kotlin.collections.Map<Key, AnyValue>,
    ) : AnyValue()

    public companion object {
        public fun of(value: Long): AnyValue = Int(value)

        public fun of(value: kotlin.Int): AnyValue = Int(value.toLong())

        public fun of(value: kotlin.Double): AnyValue = Double(value)

        public fun of(value: kotlin.Float): AnyValue = Double(value.toDouble())

        public fun of(value: StringValue): AnyValue = String(value)

        public fun of(value: kotlin.String): AnyValue = String(StringValue(value))

        public fun of(value: kotlin.Boolean): AnyValue = Boolean(value)

        public fun of(value: ByteArray): AnyValue = Bytes(value)

        public fun of(value: List<AnyValue>): AnyValue = ListAny(value)

        public fun of(value: kotlin.collections.Map<Key, AnyValue>): AnyValue = Map(value)
    }
}

/**
 * A normalized severity value.
 */
public enum class Severity(
    public val number: kotlin.Int,
) {
    Trace(1),
    Trace2(2),
    Trace3(3),
    Trace4(4),
    Debug(5),
    Debug2(6),
    Debug3(7),
    Debug4(8),
    Info(9),
    Info2(10),
    Info3(11),
    Info4(12),
    Warn(13),
    Warn2(14),
    Warn3(15),
    Warn4(16),
    Error(17),
    Error2(18),
    Error3(19),
    Error4(20),
    Fatal(21),
    Fatal2(22),
    Fatal3(23),
    Fatal4(24),
    ;

    public fun severityName(): kotlin.String =
        when (this) {
            Trace -> "TRACE"
            Trace2 -> "TRACE2"
            Trace3 -> "TRACE3"
            Trace4 -> "TRACE4"
            Debug -> "DEBUG"
            Debug2 -> "DEBUG2"
            Debug3 -> "DEBUG3"
            Debug4 -> "DEBUG4"
            Info -> "INFO"
            Info2 -> "INFO2"
            Info3 -> "INFO3"
            Info4 -> "INFO4"
            Warn -> "WARN"
            Warn2 -> "WARN2"
            Warn3 -> "WARN3"
            Warn4 -> "WARN4"
            Error -> "ERROR"
            Error2 -> "ERROR2"
            Error3 -> "ERROR3"
            Error4 -> "ERROR4"
            Fatal -> "FATAL"
            Fatal2 -> "FATAL2"
            Fatal3 -> "FATAL3"
            Fatal4 -> "FATAL4"
        }
}
