// port-lint: source propagation/mod.rs
package io.github.kotlinmania.opentelemetry.propagation

/**
 * Injector provides an interface for adding fields from an underlying struct like Map.
 */
public interface Injector {
    /**
     * Add a key and value to the underlying data.
     */
    public fun set(key: String, value: String)
}

/**
 * Extractor provides an interface for removing fields from an underlying struct like Map.
 */
public interface Extractor {
    /**
     * Get a value from a key from the underlying data.
     */
    public fun get(key: String): String?

    /**
     * Collect all the keys from the underlying data.
     */
    public fun keys(): List<String>

    /**
     * Get all values from a key from the underlying data.
     */
    public fun getAll(key: String): List<String>? = get(key)?.let { listOf(it) }
}

/**
 * Map-backed carrier implementing both [Injector] and [Extractor] with case-insensitive keys.
 */
public class MapCarrier internal constructor(
    internal val map: MutableMap<String, String>,
) : Injector,
    Extractor {
    public constructor() : this(mutableMapOf())

    override fun set(key: String, value: String) {
        map[key.lowercase()] = value
    }

    override fun get(key: String): String? = map[key.lowercase()]

    override fun keys(): List<String> = map.keys.toList()

    override fun getAll(key: String): List<String>? = get(key)?.let { listOf(it) }
}

/**
 * Wraps a mutable map as an [Injector].
 */
internal class MapInjector(
    private val map: MutableMap<String, String>,
) : Injector {
    override fun set(key: String, value: String) {
        map[key.lowercase()] = value
    }
}

/**
 * Wraps a map as an [Extractor].
 */
internal class MapExtractor(
    private val map: Map<String, String>,
) : Extractor {
    override fun get(key: String): String? = map[key.lowercase()]

    override fun keys(): List<String> = map.keys.toList()

    override fun getAll(key: String): List<String>? = get(key)?.let { listOf(it) }
}

internal fun MutableMap<String, String>.asInjector(): Injector = MapInjector(this)

internal fun Map<String, String>.asExtractor(): Extractor = MapExtractor(this)
