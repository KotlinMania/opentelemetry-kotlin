// port-lint: source global/metrics.rs
package io.github.kotlinmania.opentelemetry.global

import io.github.kotlinmania.opentelemetry.InstrumentationScope
import io.github.kotlinmania.opentelemetry.metrics.Meter
import io.github.kotlinmania.opentelemetry.metrics.MeterProvider
import io.github.kotlinmania.opentelemetry.metrics.NoopMeterProvider

private var globalMeterProvider: MeterProvider = NoopMeterProvider.new()

/**
 * Sets the given [MeterProvider] instance as the current global meter provider.
 */
public fun setMeterProvider(newProvider: MeterProvider) {
    globalMeterProvider = newProvider
}

/**
 * Returns an instance of the currently configured global [MeterProvider].
 */
public fun meterProvider(): MeterProvider = globalMeterProvider

/**
 * Creates a named [Meter] via the currently configured global [MeterProvider].
 */
public fun meter(name: String): Meter =
    meterProvider().meter(name)

/**
 * Creates a [Meter] with the given instrumentation scope via the currently configured global [MeterProvider].
 */
public fun meterWithScope(scope: InstrumentationScope): Meter =
    meterProvider().meterWithScope(scope)
