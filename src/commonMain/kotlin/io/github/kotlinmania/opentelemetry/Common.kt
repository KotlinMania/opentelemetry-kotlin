// port-lint: source common.rs
package io.github.kotlinmania.opentelemetry

import kotlinx.serialization.Serializable

/**
 * The key part of attribute [KeyValue] pairs.
 */
@Serializable
public data class Key(
    public val name: String,
) : Comparable<Key> {
    public fun asStr(): String = name
    public fun asString(): String = name
    public fun borrow(): String = name
    public fun asRef(): String = name
    public fun cmp(other: Key): Int = compareTo(other)
    public fun partialCmp(other: Key): Int? = compareTo(other)
    public fun eq(other: Any?): Boolean = this == other
    public fun hash(): Int = hashCode()
    public fun fmt(): String = toString()

    override fun compareTo(other: Key): Int = name.compareTo(other.name)

    override fun toString(): String = name

    public companion object {
        public fun from(name: String): Key = Key(name)
        public fun fromStaticStr(name: String): Key = Key(name)
        public fun new(name: String): Key = Key(name)
    }
}

/**
 * Internal string representation wrapper.
 */
@Serializable
public data class OtelString(
    public val value: String,
) : Comparable<OtelString> {
    public fun asStr(): String = value
    public fun cmp(other: OtelString): Int = value.compareTo(other.value)
    public fun partialCmp(other: OtelString): Int? = value.compareTo(other.value)
    public fun eq(other: Any?): Boolean = this == other
    public fun hash(): Int = hashCode()

    override fun compareTo(other: OtelString): Int = value.compareTo(other.value)
    override fun toString(): String = value
}

/**
 * A string value wrapper for OpenTelemetry attributes.
 */
@Serializable
public data class StringValue(
    public val value: String,
) {
    public fun asStr(): String = value
    public fun asString(): String = value
    public fun asRef(): String = value
    public fun fmt(): String = toString()

    override fun toString(): String = value

    public companion object {
        public fun from(value: String): StringValue = StringValue(value)
    }
}

/**
 * An array value containing homogeneous types.
 */
@Serializable
public sealed class Array {
    public abstract fun fmt(): String

    @Serializable
    public data class Bool(public val values: List<Boolean>) : Array() {
        override fun fmt(): String = displayArrayStr(values)
        override fun toString(): String = fmt()
    }

    @Serializable
    public data class I64(public val values: List<Long>) : Array() {
        override fun fmt(): String = displayArrayStr(values)
        override fun toString(): String = fmt()
    }

    @Serializable
    public data class F64(public val values: List<Double>) : Array() {
        override fun fmt(): String = displayArrayStr(values)
        override fun toString(): String = fmt()
    }

    @Serializable
    public data class StringList(public val values: List<StringValue>) : Array() {
        override fun fmt(): String {
            val join = values.joinToString(",") { "\"${it.value}\"" }
            return "[$join]"
        }
        override fun toString(): String = fmt()
    }
}

public fun <T> displayArrayStr(slice: List<T>): String {
    val join = slice.joinToString(",") { it.toString() }
    return "[$join]"
}

/**
 * Backward compatibility alias for Array.
 */
public typealias ArrayValue = Array

/**
 * The value part of attribute [KeyValue] pairs.
 */
@Serializable
public sealed class Value {
    public abstract fun asStr(): String
    public open fun fmt(): String = asStr()

    @Serializable
    public data class Bool(public val value: Boolean) : Value() {
        override fun asStr(): String = value.toString()
        override fun toString(): String = asStr()
    }

    @Serializable
    public data class I64(public val value: Long) : Value() {
        override fun asStr(): String = value.toString()
        override fun toString(): String = asStr()
    }

    @Serializable
    public data class F64(public val value: Double) : Value() {
        override fun asStr(): String = value.toString()
        override fun toString(): String = asStr()

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is F64) return false
            if (value.isNaN() || other.value.isNaN()) return false
            return value == other.value
        }

        override fun hashCode(): Int = value.toBits().hashCode()
    }

    @Serializable
    public data class StringVal(public val value: StringValue) : Value() {
        override fun asStr(): String = value.value
        override fun toString(): String = asStr()
    }

    @Serializable
    public data class ArrayVal(public val value: Array) : Value() {
        override fun asStr(): String = value.fmt()
        override fun toString(): String = asStr()
    }

    public companion object {
        public fun of(value: Boolean): Value = Bool(value)
        public fun of(value: Long): Value = I64(value)
        public fun of(value: Double): Value = F64(value)
        public fun of(value: String): Value = StringVal(StringValue(value))
        public fun of(value: StringValue): Value = StringVal(value)
        public fun of(value: Array): Value = ArrayVal(value)
    }
}

/**
 * Wrapper for f64 hashing and bit equality.
 */
public data class F64Hashable(
    public val value: Double,
) {
    public fun eq(other: Any?): Boolean {
        if (other !is F64Hashable) return false
        return value.toBits() == other.value.toBits()
    }

    public fun hash(): Int = value.toBits().hashCode()

    override fun equals(other: Any?): Boolean = eq(other)
    override fun hashCode(): Int = hash()
}

/**
 * An attribute key-value pair.
 */
@Serializable
public data class KeyValue(
    public val key: Key,
    public val value: Value,
) {
    public constructor(key: String, value: Value) : this(Key(key), value)
    public constructor(key: String, value: String) : this(Key(key), Value.of(value))
    public constructor(key: String, value: Long) : this(Key(key), Value.of(value))
    public constructor(key: String, value: Double) : this(Key(key), Value.of(value))
    public constructor(key: String, value: Boolean) : this(Key(key), Value.of(value))

    public fun hash(): Int = hashCode()

    public companion object {
        public fun new(key: Key, value: Value): KeyValue = KeyValue(key, value)
        public fun new(key: String, value: Value): KeyValue = KeyValue(Key(key), value)
        public fun new(key: String, value: String): KeyValue = KeyValue(Key(key), Value.of(value))
        public fun new(key: String, value: Long): KeyValue = KeyValue(Key(key), Value.of(value))
        public fun new(key: String, value: Double): KeyValue = KeyValue(Key(key), Value.of(value))
        public fun new(key: String, value: Boolean): KeyValue = KeyValue(Key(key), Value.of(value))
    }
}

/**
 * Information about a library or module providing instrumentation.
 */
@Serializable
public data class InstrumentationScope(
    public val scopeName: String,
    public val scopeVersion: String? = null,
    public val scopeSchemaUrl: String? = null,
    public val scopeAttributes: List<KeyValue> = emptyList(),
) {
    public fun name(): String = scopeName
    public fun version(): String? = scopeVersion
    public fun schemaUrl(): String? = scopeSchemaUrl
    public fun attributes(): List<KeyValue> = scopeAttributes

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is InstrumentationScope) return false
        if (scopeName != other.scopeName) return false
        if (scopeVersion != other.scopeVersion) return false
        if (scopeSchemaUrl != other.scopeSchemaUrl) return false
        if (scopeAttributes.size != other.scopeAttributes.size) return false

        val selfSorted = scopeAttributes.sortedBy { it.key.name }
        val otherSorted = other.scopeAttributes.sortedBy { it.key.name }
        return selfSorted == otherSorted
    }

    override fun hashCode(): Int {
        var result = scopeName.hashCode()
        result = 31 * result + (scopeVersion?.hashCode() ?: 0)
        result = 31 * result + (scopeSchemaUrl?.hashCode() ?: 0)
        val selfSorted = scopeAttributes.sortedBy { it.key.name }
        for (attr in selfSorted) {
            result = 31 * result + attr.hashCode()
        }
        return result
    }

    public companion object {
        public fun builder(name: String): InstrumentationScopeBuilder = InstrumentationScopeBuilder(name)
    }
}

/**
 * Configuration options for [InstrumentationScope].
 */
public class InstrumentationScopeBuilder(
    private val name: String,
) {
    private var version: String? = null
    private var schemaUrl: String? = null
    private var attributes: List<KeyValue>? = null

    public fun withVersion(version: String): InstrumentationScopeBuilder {
        this.version = version
        return this
    }

    public fun withSchemaUrl(schemaUrl: String): InstrumentationScopeBuilder {
        this.schemaUrl = schemaUrl
        return this
    }

    public fun withAttributes(attributes: Iterable<KeyValue>): InstrumentationScopeBuilder {
        this.attributes = attributes.toList()
        return this
    }

    public fun build(): InstrumentationScope {
        return InstrumentationScope(
            scopeName = name,
            scopeVersion = version,
            scopeSchemaUrl = schemaUrl,
            scopeAttributes = attributes ?: emptyList(),
        )
    }
}

public fun hashHelper(item: Any): Long {
    return item.hashCode().toLong()
}
