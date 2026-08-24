// port-lint: source tmp/opentelemetry/src/common.rs
package io.github.kotlinmania.opentelemetry

import kotlinx.serialization.Serializable

/**
 * The key part of attribute [KeyValue] pairs.
 */
@Serializable
data class Key(
    val name: String,
) : Comparable<Key> {
    fun asString(): String = name

    override fun compareTo(other: Key): Int = name.compareTo(other.name)

    override fun toString(): String = name

    companion object {
        fun from(name: String): Key = Key(name)
    }
}

/**
 * A string value wrapper for OpenTelemetry attributes.
 */
@Serializable
data class StringValue(
    val value: String,
) {
    fun asString(): String = value

    override fun toString(): String = value

    companion object {
        fun from(value: String): StringValue = StringValue(value)
    }
}

/**
 * An array value containing homogeneous types.
 */
@Serializable
sealed class ArrayValue {
    @Serializable
    data class Bool(val values: List<Boolean>) : ArrayValue()

    @Serializable
    data class I64(val values: List<Long>) : ArrayValue()

    @Serializable
    data class F64(val values: List<Double>) : ArrayValue()

    @Serializable
    data class StringList(val values: List<StringValue>) : ArrayValue()
}

/**
 * The value part of attribute [KeyValue] pairs.
 */
@Serializable
sealed class Value {
    @Serializable
    data class Bool(val value: Boolean) : Value()

    @Serializable
    data class I64(val value: Long) : Value()

    @Serializable
    data class F64(val value: Double) : Value()

    @Serializable
    data class StringVal(val value: StringValue) : Value()

    @Serializable
    data class ArrayVal(val value: ArrayValue) : Value()

    companion object {
        fun of(value: Boolean): Value = Bool(value)
        fun of(value: Long): Value = I64(value)
        fun of(value: Double): Value = F64(value)
        fun of(value: String): Value = StringVal(StringValue(value))
        fun of(value: StringValue): Value = StringVal(value)
        fun of(value: ArrayValue): Value = ArrayVal(value)
    }
}

/**
 * An attribute key-value pair.
 */
@Serializable
data class KeyValue(
    val key: Key,
    val value: Value,
) {
    constructor(key: String, value: Value) : this(Key(key), value)
    constructor(key: String, value: String) : this(Key(key), Value.of(value))
    constructor(key: String, value: Long) : this(Key(key), Value.of(value))
    constructor(key: String, value: Double) : this(Key(key), Value.of(value))
    constructor(key: String, value: Boolean) : this(Key(key), Value.of(value))

    companion object {
        fun new(key: String, value: Value): KeyValue = KeyValue(Key(key), value)
    }
}

/**
 * Information about a library or module providing instrumentation.
 */
@Serializable
data class InstrumentationScope(
    val name: String,
    val version: String? = null,
    val schemaUrl: String? = null,
    val attributes: List<KeyValue> = emptyList(),
) {
    companion object {
        fun builder(name: String): InstrumentationScopeBuilder = InstrumentationScopeBuilder(name)
    }
}

/**
 * Builder for [InstrumentationScope].
 */
class InstrumentationScopeBuilder(
    private val name: String,
) {
    private var version: String? = null
    private var schemaUrl: String? = null
    private val attributes: MutableList<KeyValue> = mutableListOf()

    fun withVersion(version: String): InstrumentationScopeBuilder {
        this.version = version
        return this
    }

    fun withSchemaUrl(schemaUrl: String): InstrumentationScopeBuilder {
        this.schemaUrl = schemaUrl
        return this
    }

    fun withAttributes(attributes: Iterable<KeyValue>): InstrumentationScopeBuilder {
        this.attributes.addAll(attributes)
        return this
    }

    fun build(): InstrumentationScope = InstrumentationScope(
        name = name,
        version = version,
        schemaUrl = schemaUrl,
        attributes = attributes.toList(),
    )
}
