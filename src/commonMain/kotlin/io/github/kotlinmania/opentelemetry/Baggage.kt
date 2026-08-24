// port-lint: source tmp/opentelemetry/src/baggage.rs
package io.github.kotlinmania.opentelemetry

import kotlinx.serialization.Serializable

/**
 * Metadata associated with a baggage entry.
 */
@Serializable
data class BaggageMetadata(
    val value: String = "",
) {
    override fun toString(): String = value

    companion object {
        val EMPTY: BaggageMetadata = BaggageMetadata("")

        fun from(value: String): BaggageMetadata = BaggageMetadata(value)
    }
}

/**
 * A set of name/value pairs describing user-defined properties.
 */
@Serializable
class Baggage private constructor(
    private val entries: MutableMap<Key, Pair<StringValue, BaggageMetadata>>,
) {
    constructor() : this(mutableMapOf())
    fun get(key: Key): StringValue? = entries[key]?.first

    fun get(key: String): StringValue? = get(Key(key))

    fun getMetadata(key: Key): BaggageMetadata? = entries[key]?.second

    fun getMetadata(key: String): BaggageMetadata? = getMetadata(Key(key))

    fun insert(key: Key, value: StringValue, metadata: BaggageMetadata = BaggageMetadata.EMPTY): Baggage {
        entries[key] = value to metadata
        return this
    }

    fun insert(key: String, value: String, metadata: String = ""): Baggage =
        insert(Key(key), StringValue(value), BaggageMetadata(metadata))

    fun remove(key: Key): StringValue? = entries.remove(key)?.first

    fun remove(key: String): StringValue? = remove(Key(key))

    fun clear() {
        entries.clear()
    }

    fun size(): Int = entries.size

    fun isEmpty(): Boolean = entries.isEmpty()

    fun toMap(): Map<String, String> = entries.map { (k, v) -> k.name to v.first.value }.toMap()

    companion object {
        fun new(): Baggage = Baggage()
    }
}
