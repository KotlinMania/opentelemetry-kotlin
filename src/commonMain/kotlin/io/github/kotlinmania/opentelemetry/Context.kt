// port-lint: source context.rs
package io.github.kotlinmania.opentelemetry

import kotlin.reflect.KClass

public class IdHasher(
    private var value: Long = 0L,
) {
    public fun write(bytes: ByteArray) {}

    public fun writeU64(id: Long) {
        value = id
    }

    public fun finish(): Long = value
}

public class ContextGuard internal constructor(
    public val cxPos: Int,
    private val stack: ContextStack,
) : AutoCloseable {
    private var active = true

    public fun drop() {
        if (active) {
            active = false
            stack.popId(cxPos)
        }
    }

    public fun detach() {
        drop()
    }

    override fun close() {
        drop()
    }
}

public class ContextStack {
    public var currentCx: Context = Context()
    internal val stack: MutableList<Context?> = ArrayList(INITIAL_CAPACITY)

    public fun push(cx: Context): Int {
        val nextId = stack.size + 1
        if (nextId < MAX_POS) {
            val previous = currentCx
            stack.add(previous)
            currentCx = cx
            return nextId
        }
        return MAX_POS
    }

    public fun popId(pos: Int) {
        if (pos == BASE_POS || pos == MAX_POS) return
        val len = stack.size
        if (pos == len) {
            while (stack.isNotEmpty() && stack.last() == null) {
                stack.removeAt(stack.size - 1)
            }
            if (stack.isNotEmpty()) {
                val nextCx = stack.removeAt(stack.size - 1)
                if (nextCx != null) {
                    currentCx = nextCx
                }
            }
        } else if (pos < len) {
            stack[pos] = null
        }
    }

    public fun <T> mapCurrentCx(f: (Context) -> T): T = f(currentCx)

    public companion object {
        public const val BASE_POS: Int = 0
        public const val MAX_POS: Int = 65535
        public const val INITIAL_CAPACITY: Int = 8

        public fun default(): ContextStack = ContextStack()
    }
}

public typealias EntryMap = Map<String, Any>

/**
 * An execution-scoped collection of values.
 */
public class Context internal constructor(
    private val entries: Map<String, Any> = emptyMap(),
    private val suppressTelemetry: Boolean = false,
) {
    public fun isTelemetrySuppressed(): Boolean = suppressTelemetry

    public fun withTelemetrySuppressed(): Context = Context(entries, true)

    public fun <T : Any> get(clazz: KClass<T>): T? {
        @Suppress("UNCHECKED_CAST")
        return entries[clazz.simpleName ?: ""] as? T
    }

    public inline fun <reified T : Any> get(): T? = get(T::class)

    public fun <T : Any> withValue(value: T): Context {
        val updated = HashMap(entries)
        updated[value::class.simpleName ?: ""] = value
        return Context(updated, suppressTelemetry)
    }

    public fun withValue(key: String, value: Any): Context {
        val updated = HashMap(entries)
        updated[key] = value
        return Context(updated, suppressTelemetry)
    }

    public fun getValue(key: String): String? = entries[key]?.toString()

    public fun attach(): ContextGuard {
        val id = currentStack.push(this)
        return ContextGuard(id, currentStack)
    }

    public fun currentWithSynchronizedSpan(value: Any): Context = withValue("__span", value)

    public fun withSynchronizedSpan(value: Any): Context = withValue("__span", value)

    public fun fmt(): String = toString()

    override fun toString(): String = "Context(entries=${entries.size}, suppressTelemetry=$suppressTelemetry)"

    public companion object {
        private val currentStack: ContextStack = ContextStack()

        public fun new(): Context = Context()

        public fun default(): Context = Context()

        public fun current(): Context = currentStack.currentCx

        public fun <T : Any> currentWithValue(value: T): Context = current().withValue(value)

        public fun currentWithValue(key: String, value: String): Context = current().withValue(key, value)

        public fun <T> mapCurrent(f: (Context) -> T): T = currentStack.mapCurrentCx(f)

        public fun enterTelemetrySuppressedScope(): ContextGuard = current().withTelemetrySuppressed().attach()

        public fun isCurrentTelemetrySuppressed(): Boolean = current().isTelemetrySuppressed()

        public fun setCurrent(context: Context) {
            currentStack.currentCx = context
        }
    }
}
