// port-lint: source trace/tracer_provider.rs
package io.github.kotlinmania.opentelemetry.trace

import io.github.kotlinmania.opentelemetry.InstrumentationScope

/**
 * Types that can create instances of [Tracer].
 */
public interface TracerProvider {
    /**
     * Returns a new tracer with the given name.
     */
    public fun tracer(name: String): Tracer {
        val scope = InstrumentationScope.builder(name).build()
        return tracerWithScope(scope)
    }

    /**
     * Returns a new versioned tracer with the given instrumentation scope.
     */
    public fun tracerWithScope(scope: InstrumentationScope): Tracer
}
