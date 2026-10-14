// port-lint: source trace/span_context.rs
package io.github.kotlinmania.opentelemetry.trace

import io.github.kotlinmania.opentelemetry.SpanId
import io.github.kotlinmania.opentelemetry.TraceFlags
import io.github.kotlinmania.opentelemetry.TraceId
import io.github.kotlinmania.thiserror.StdError

/**
 * Error returned by TraceState operations.
 */
public sealed class TraceStateError(
    public val rawMessage: String,
) : Exception(rawMessage),
    StdError {
    public class Key(
        public val key: String,
    ) : TraceStateError("$key is not a valid key in TraceState, see https://www.w3.org/TR/trace-context/#key for more details")

    public class Value(
        public val value: String,
    ) : TraceStateError("$value is not a valid value in TraceState, see https://www.w3.org/TR/trace-context/#value for more details")

    public class List(
        public val member: String,
    ) : TraceStateError("$member is not a valid list member in TraceState, see https://www.w3.org/TR/trace-context/#list for more details")
}

/**
 * Key-value pair in a TraceState header.
 */
public class TraceStateEntry(
    public val key: String,
    public val value: String,
)

/**
 * TraceState carries system-specific configuration data, represented as a list of key-value pairs.
 */
public class TraceState internal constructor(
    entries: List<Pair<String, String>>? = null,
) {
    public constructor() : this(null)

    private val rawEntries: List<Pair<String, String>>? = entries?.takeIf { it.isNotEmpty() }

    public fun get(key: String): String? =
        rawEntries?.firstOrNull { it.first == key }?.second

    public fun insert(key: String, value: String): TraceState {
        if (!validKey(key)) throw TraceStateError.Key(key)
        if (!validValue(value)) throw TraceStateError.Value(value)

        val updated = mutableListOf<Pair<String, String>>()
        updated.add(key to value)
        rawEntries?.forEach { if (it.first != key) updated.add(it) }
        return TraceState(updated)
    }

    public fun delete(key: String): TraceState {
        if (!validKey(key)) throw TraceStateError.Key(key)
        val updated = rawEntries?.filter { it.first != key }
        return TraceState(updated)
    }

    public fun deleteFromDeque(key: String): TraceState = delete(key)

    public fun header(): String = headerDelimited("=", ",")

    public fun headerDelimited(entryDelimiter: String, listDelimiter: String): String =
        rawEntries?.joinToString(listDelimiter) { "${it.first}$entryDelimiter${it.second}" }.orEmpty()

    override fun equals(other: Any?): Boolean = other is TraceState && rawEntries == other.rawEntries

    override fun hashCode(): Int = rawEntries?.hashCode() ?: 0

    override fun toString(): String = "TraceState(${rawEntries ?: "None"})"

    public companion object {
        public typealias Err = TraceStateError

        public val NONE: TraceState = TraceState(null)

        public fun validKey(key: String): Boolean {
            if (key.isEmpty() || key.length > 256) return false
            var vendorStart: Int? = null
            for (i in 0 until key.length) {
                val b = key[i]
                val isLower = b in 'a'..'z'
                val isDigit = b in '0'..'9'
                val isAllowedSpecial = b == '_' || b == '-' || b == '*' || b == '/'
                if (!(isLower || isDigit || isAllowedSpecial || b == '@')) {
                    return false
                }
                if (i == 0 && !(isLower || isDigit)) {
                    return false
                } else if (b == '@') {
                    if (vendorStart != null || i + 14 < key.length) {
                        return false
                    }
                    vendorStart = i
                } else if (vendorStart != null && i == vendorStart + 1) {
                    if (!(isLower || isDigit)) return false
                }
            }
            return true
        }

        public fun validValue(value: String): Boolean {
            if (value.length > 256) return false
            return !value.contains(',') && !value.contains('=')
        }

        internal fun fromKeyValue(entries: Iterable<Pair<String, String>>): TraceState {
            val list = mutableListOf<Pair<String, String>>()
            for ((key, value) in entries) {
                if (!validKey(key)) throw TraceStateError.Key(key)
                if (!validValue(value)) throw TraceStateError.Value(value)
                list.add(key to value)
            }
            return TraceState(list)
        }

        public fun fromEntries(entries: Iterable<TraceStateEntry>): TraceState =
            fromKeyValue(entries.map { it.key to it.value })

        public fun fromStr(s: String): TraceState = fromString(s)

        public fun fromString(s: String): TraceState {
            val members = s.split(',').filter { it.isNotEmpty() }
            val pairs = mutableListOf<Pair<String, String>>()
            for (member in members) {
                val eqIdx = member.indexOf('=')
                if (eqIdx == -1) throw TraceStateError.List(member)
                val k = member.substring(0, eqIdx)
                val v = member.substring(eqIdx + 1)
                pairs.add(k to v)
            }
            return fromKeyValue(pairs)
        }
    }
}

public typealias TraceStateResult<T> = Result<T>

/**
 * Immutable portion of a Span which can be serialized and propagated.
 */
public class SpanContext(
    public val traceId: TraceId,
    public val spanId: SpanId,
    public val traceFlags: TraceFlags = TraceFlags.NOT_SAMPLED,
    public val isRemote: Boolean = false,
    public val traceState: TraceState = TraceState.NONE,
) {
    public fun isSampled(): Boolean = traceFlags.isSampled()

    public fun isValid(): Boolean = traceId.isValid() && spanId.isValid()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SpanContext) return false
        return traceId == other.traceId &&
            spanId == other.spanId &&
            traceFlags == other.traceFlags &&
            isRemote == other.isRemote &&
            traceState == other.traceState
    }

    override fun hashCode(): Int {
        var result = traceId.hashCode()
        result = 31 * result + spanId.hashCode()
        result = 31 * result + traceFlags.hashCode()
        result = 31 * result + isRemote.hashCode()
        result = 31 * result + traceState.hashCode()
        return result
    }

    override fun toString(): String =
        "SpanContext(traceId=$traceId, spanId=$spanId, traceFlags=$traceFlags, isRemote=$isRemote, traceState=$traceState)"

    public companion object {
        public val NONE: SpanContext =
            SpanContext(
                traceId = TraceId.INVALID,
                spanId = SpanId.INVALID,
                traceFlags = TraceFlags.NOT_SAMPLED,
                isRemote = false,
                traceState = TraceState.NONE,
            )

        public fun emptyContext(): SpanContext = NONE

        public fun new(
            traceId: TraceId,
            spanId: SpanId,
            traceFlags: TraceFlags = TraceFlags.NOT_SAMPLED,
            isRemote: Boolean = false,
            traceState: TraceState = TraceState.NONE,
        ): SpanContext = SpanContext(traceId, spanId, traceFlags, isRemote, traceState)
    }
}
