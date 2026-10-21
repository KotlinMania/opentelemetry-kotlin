package io.github.kotlinmania.opentelemetry.metrics.instruments

import io.github.kotlinmania.opentelemetry.KeyValue
import io.github.kotlinmania.opentelemetry.metrics.InstrumentProvider

/**
 * An SDK implemented instrument that records measurements via callback.
 */
public interface AsyncInstrument<T> {
    public fun observe(measurement: T, attributes: List<KeyValue> = emptyList())
}

/**
 * An SDK implemented instrument that records measurements synchronously.
 */
public interface SyncInstrument<T> {
    public fun measure(measurement: T, attributes: List<KeyValue> = emptyList())
}

/**
 * A callback function registered with a [Meter] that makes observations.
 */
public typealias Callback<T> = (AsyncInstrument<T>) -> Unit

/**
 * Configuration for building a sync instrument.
 */
public class InstrumentBuilder<T>(
    public val instrumentProvider: InstrumentProvider,
    public val name: String,
    public var description: String? = null,
    public var unit: String? = null,
) {
    public fun withDescription(description: String): InstrumentBuilder<T> {
        this.description = description
        return this
    }

    public fun withUnit(unit: String): InstrumentBuilder<T> {
        this.unit = unit
        return this
    }
}

/**
 * Configuration for building a Histogram.
 */
public class HistogramBuilder<T>(
    public val instrumentProvider: InstrumentProvider,
    public val name: String,
    public var description: String? = null,
    public var unit: String? = null,
    public var boundaries: List<Double>? = null,
) {
    public fun withDescription(description: String): HistogramBuilder<T> {
        this.description = description
        return this
    }

    public fun withUnit(unit: String): HistogramBuilder<T> {
        this.unit = unit
        return this
    }

    public fun withBoundaries(boundaries: List<Double>): HistogramBuilder<T> {
        this.boundaries = boundaries
        return this
    }

    public fun build(): Histogram<T> = instrumentProvider.histogram(this)
}

/**
 * Configuration for building an async instrument.
 */
public class AsyncInstrumentBuilder<I, M>(
    public val instrumentProvider: InstrumentProvider,
    public val name: String,
    public var description: String? = null,
    public var unit: String? = null,
    public var callback: Callback<M>? = null,
) {
    public fun withDescription(description: String): AsyncInstrumentBuilder<I, M> {
        this.description = description
        return this
    }

    public fun withUnit(unit: String): AsyncInstrumentBuilder<I, M> {
        this.unit = unit
        return this
    }

    public fun withCallback(callback: Callback<M>): AsyncInstrumentBuilder<I, M> {
        this.callback = callback
        return this
    }
}
