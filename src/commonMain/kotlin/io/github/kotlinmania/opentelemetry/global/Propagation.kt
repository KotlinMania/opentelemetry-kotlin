// port-lint: source global/propagation.rs
package io.github.kotlinmania.opentelemetry.global

import io.github.kotlinmania.opentelemetry.propagation.TextMapPropagator
import io.github.kotlinmania.opentelemetry.trace.NoopTextMapPropagator

private val defaultPropagator: NoopTextMapPropagator = NoopTextMapPropagator.new()
private var globalPropagator: TextMapPropagator = defaultPropagator

internal fun defaultTextMapPropagator(): NoopTextMapPropagator = defaultPropagator

internal fun globalTextMapPropagator(): TextMapPropagator = globalPropagator

/**
 * Sets the given [TextMapPropagator] propagator as the current global propagator.
 */
public fun setTextMapPropagator(propagator: TextMapPropagator) {
    globalPropagator = propagator
}

/**
 * Executes a block with a reference to the current global [TextMapPropagator] propagator.
 */
public fun <T> getTextMapPropagator(f: (TextMapPropagator) -> T): T =
    f(globalPropagator)
