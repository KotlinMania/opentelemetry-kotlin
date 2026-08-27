// port-lint: source baggage.rs
package io.github.kotlinmania.opentelemetry

import kotlinx.serialization.Serializable

public const val MAX_KEY_VALUE_PAIRS: Int = 64
public const val MAX_LEN_OF_ALL_PAIRS: Int = 8192

private fun isInvalidAsciiKeyChar(c: Char): Boolean =
    when (c) {
        '(', ')', ',', '/', ':', ';', '<', '=', '>', '?', '@', '[', '\\', ']', '{', '}', '"' -> true
        else -> false
    }

private val DEFAULT_BAGGAGE: Baggage by lazy { Baggage.new() }

public fun getDefaultBaggage(): Baggage = DEFAULT_BAGGAGE

public fun isKeyValid(key: String): Boolean {
    if (key.isEmpty()) return false
    for (i in 0 until key.length) {
        val c = key[i]
        if (c.code <= 32 || c.code >= 127 || isInvalidAsciiKeyChar(c)) {
            return false
        }
    }
    return true
}

public fun keyValueMetadataBytesSize(key: String, value: String, metadata: String): Int = key.length + value.length + metadata.length

public fun encode(s: String): String {
    val bytes = s.encodeToByteArray()
    val sb = StringBuilder(bytes.size)
    for (b in bytes) {
        val u = b.toInt() and 0xFF
        when (u) {
            in 0x61..0x7A, // a-z
            in 0x41..0x5A, // A-Z
            in 0x30..0x39, // 0-9
            0x2E, // .
            0x2D, // -
            0x5F, // _
            0x7E,
            -> sb.append(u.toChar()) // ~
            0x20 -> sb.append("%20") // space
            else -> {
                val hex = u.toString(16).uppercase()
                sb.append('%')
                if (hex.length == 1) sb.append('0')
                sb.append(hex)
            }
        }
    }
    return sb.toString()
}

/**
 * Metadata associated with a baggage entry.
 */
@Serializable
public data class BaggageMetadata(
    public val value: String = "",
) {
    public fun asStr(): String = value

    public fun fmt(): String = value

    override fun toString(): String = value

    public companion object {
        public val EMPTY: BaggageMetadata = BaggageMetadata("")

        public fun from(value: String): BaggageMetadata = BaggageMetadata(value.trim())
    }
}

/**
 * Key, value, and metadata tuple.
 */
@Serializable
public data class KeyValueMetadata(
    public val key: Key,
    public val value: StringValue,
    public val metadata: BaggageMetadata = BaggageMetadata.EMPTY,
) {
    public companion object {
        public fun new(key: Key, value: StringValue, metadata: BaggageMetadata): KeyValueMetadata =
            KeyValueMetadata(key, value, metadata)

        public fun new(key: String, value: String, metadata: String): KeyValueMetadata =
            KeyValueMetadata(Key(key), StringValue(value), BaggageMetadata(metadata))
    }
}

public typealias Item = Pair<Key, Pair<StringValue, BaggageMetadata>>

public class Iter internal constructor(
    private val iterator: Iterator<Item>,
) {
    public constructor() : this(emptyList<Item>().iterator())

    public fun next(): Item? = if (iterator.hasNext()) iterator.next() else null
}

public typealias IntoIter = Iter

/**
 * Internal wrapper for Context storage.
 */
public data class BaggageContextValue(
    public val baggage: Baggage,
)

/**
 * Methods for sorting and retrieving baggage data in a context.
 */
public interface BaggageExt {
    public fun withBaggage(baggage: Baggage): Context

    public fun currentWithBaggage(baggage: Baggage): Context

    public fun withClearedBaggage(): Context

    public fun baggage(): Baggage
}

/**
 * A set of name/value pairs describing user-defined properties.
 */
@Serializable
public class Baggage internal constructor(
    private val inner: LinkedHashMap<Key, Pair<StringValue, BaggageMetadata>>,
    private var kvContentLen: Int,
) {
    public constructor() : this(LinkedHashMap(), 0)

    public fun get(key: Key): StringValue? = inner[key]?.first

    public fun get(key: String): StringValue? = get(Key(key))

    public fun getWithMetadata(key: Key): Pair<StringValue, BaggageMetadata>? = inner[key]

    public fun getWithMetadata(key: String): Pair<StringValue, BaggageMetadata>? = getWithMetadata(Key(key))

    public fun getMetadata(key: Key): BaggageMetadata? = inner[key]?.second

    public fun getMetadata(key: String): BaggageMetadata? = getMetadata(Key(key))

    public fun insert(key: Key, value: StringValue): StringValue? =
        insertWithMetadata(key, value, BaggageMetadata.EMPTY)?.first

    public fun insert(key: String, value: String): StringValue? =
        insert(Key(key), StringValue(value))

    public fun insertWithMetadata(
        key: Key,
        value: StringValue,
        metadata: BaggageMetadata,
    ): Pair<StringValue, BaggageMetadata>? {
        val keyStr = key.name
        val valStr = value.value
        val metaStr = metadata.value

        val existing = inner[key]
        if (existing != null) {
            val prevLen = keyValueMetadataBytesSize(keyStr, existing.first.value, existing.second.value)
            val entryLen = keyValueMetadataBytesSize(keyStr, valStr, metaStr)
            val newContentLen = kvContentLen + entryLen - prevLen
            if (newContentLen > MAX_LEN_OF_ALL_PAIRS) {
                return null
            }
            kvContentLen = newContentLen
            inner[key] = value to metadata
            return existing
        } else {
            if (!isKeyValid(keyStr)) return null
            if (inner.size >= MAX_KEY_VALUE_PAIRS) return null
            val entryLen = keyValueMetadataBytesSize(keyStr, valStr, metaStr)
            val newContentLen = kvContentLen + entryLen
            if (newContentLen > MAX_LEN_OF_ALL_PAIRS) {
                return null
            }
            kvContentLen = newContentLen
            inner[key] = value to metadata
            return null
        }
    }

    public fun insertWithMetadata(
        key: String,
        value: String,
        metadata: String,
    ): Pair<StringValue, BaggageMetadata>? =
        insertWithMetadata(Key(key), StringValue(value), BaggageMetadata(metadata))

    public fun remove(key: Key): Pair<StringValue, BaggageMetadata>? {
        val removed = inner.remove(key)
        if (removed != null) {
            kvContentLen -= keyValueMetadataBytesSize(key.name, removed.first.value, removed.second.value)
        }
        return removed
    }

    public fun remove(key: String): Pair<StringValue, BaggageMetadata>? = remove(Key(key))

    public fun len(): Int = inner.size

    public fun size(): Int = inner.size

    public fun isEmpty(): Boolean = inner.isEmpty()

    public fun iter(): Iter = Iter(inner.entries.map { it.key to it.value }.iterator())

    public fun intoIter(): Iter = iter()

    public fun encode(s: String): String =
        io.github.kotlinmania.opentelemetry
            .encode(s)

    public fun fmt(): String = toString()

    override fun toString(): String {
        val items =
            inner.entries.map { (k, v) ->
                val encVal =
                    io.github.kotlinmania.opentelemetry
                        .encode(v.first.value)
                if (v.second.value.isNotEmpty()) {
                    "${k.name}=$encVal;${v.second.value}"
                } else {
                    "${k.name}=$encVal"
                }
            }
        return items.joinToString(",")
    }

    public companion object {
        public fun new(): Baggage = Baggage()

        public fun fromIter(entries: Iterable<Pair<Key, Pair<StringValue, BaggageMetadata>>>): Baggage {
            val baggage = Baggage()
            for ((key, pair) in entries) {
                baggage.insertWithMetadata(key, pair.first, pair.second)
            }
            return baggage
        }

        public fun fromKeyValues(entries: Iterable<KeyValue>): Baggage {
            val baggage = Baggage()
            for (kv in entries) {
                baggage.insert(kv.key, StringValue(kv.value.asStr()))
            }
            return baggage
        }
    }
}

public fun Context.withBaggage(baggage: Baggage): Context = this.withValue(BaggageContextValue(baggage))

public fun Context.withBaggage(entries: Iterable<KeyValue>): Context {
    val baggage = Baggage.fromKeyValues(entries)
    return this.withBaggage(baggage)
}

public fun Context.withClearedBaggage(): Context = this.withBaggage(Baggage.new())

public fun Context.baggage(): Baggage {
    val baggageVal = this.get(BaggageContextValue::class)
    return baggageVal?.baggage ?: getDefaultBaggage()
}

public fun currentWithBaggage(baggage: Baggage): Context = Context.mapCurrent { cx -> cx.withBaggage(baggage) }
