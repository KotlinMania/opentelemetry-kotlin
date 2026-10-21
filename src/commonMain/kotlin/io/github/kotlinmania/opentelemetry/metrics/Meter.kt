// port-lint: source metrics/meter.rs
package io.github.kotlinmania.opentelemetry.metrics

import io.github.kotlinmania.opentelemetry.InstrumentationScope
import io.github.kotlinmania.opentelemetry.metrics.instruments.AsyncInstrumentBuilder
import io.github.kotlinmania.opentelemetry.metrics.instruments.Counter
import io.github.kotlinmania.opentelemetry.metrics.instruments.Gauge
import io.github.kotlinmania.opentelemetry.metrics.instruments.Histogram
import io.github.kotlinmania.opentelemetry.metrics.instruments.HistogramBuilder
import io.github.kotlinmania.opentelemetry.metrics.instruments.InstrumentBuilder
import io.github.kotlinmania.opentelemetry.metrics.instruments.ObservableCounter
import io.github.kotlinmania.opentelemetry.metrics.instruments.ObservableGauge
import io.github.kotlinmania.opentelemetry.metrics.instruments.ObservableUpDownCounter
import io.github.kotlinmania.opentelemetry.metrics.instruments.UpDownCounter

/**
 * Provides access to named [Meter] instances.
 */
public interface MeterProvider {
    public fun meter(name: String): Meter {
        val scope = InstrumentationScope.builder(name).build()
        return meterWithScope(scope)
    }

    public fun meterWithScope(scope: InstrumentationScope): Meter
}

/**
 * Provides the ability to create instruments for recording measurements.
 */
public class Meter(
    public val instrumentProvider: InstrumentProvider,
) {
    public fun u64Counter(name: String): InstrumentBuilder<Counter<ULong>> =
        InstrumentBuilder(instrumentProvider, name)

    public fun f64Counter(name: String): InstrumentBuilder<Counter<Double>> =
        InstrumentBuilder(instrumentProvider, name)

    public fun u64ObservableCounter(
        name: String,
    ): AsyncInstrumentBuilder<ObservableCounter<ULong>, ULong> =
        AsyncInstrumentBuilder(instrumentProvider, name)

    public fun f64ObservableCounter(
        name: String,
    ): AsyncInstrumentBuilder<ObservableCounter<Double>, Double> =
        AsyncInstrumentBuilder(instrumentProvider, name)

    public fun i64UpDownCounter(name: String): InstrumentBuilder<UpDownCounter<Long>> =
        InstrumentBuilder(instrumentProvider, name)

    public fun f64UpDownCounter(name: String): InstrumentBuilder<UpDownCounter<Double>> =
        InstrumentBuilder(instrumentProvider, name)

    public fun i64ObservableUpDownCounter(
        name: String,
    ): AsyncInstrumentBuilder<ObservableUpDownCounter<Long>, Long> =
        AsyncInstrumentBuilder(instrumentProvider, name)

    public fun f64ObservableUpDownCounter(
        name: String,
    ): AsyncInstrumentBuilder<ObservableUpDownCounter<Double>, Double> =
        AsyncInstrumentBuilder(instrumentProvider, name)

    public fun u64Gauge(name: String): InstrumentBuilder<Gauge<ULong>> =
        InstrumentBuilder(instrumentProvider, name)

    public fun f64Gauge(name: String): InstrumentBuilder<Gauge<Double>> =
        InstrumentBuilder(instrumentProvider, name)

    public fun i64Gauge(name: String): InstrumentBuilder<Gauge<Long>> =
        InstrumentBuilder(instrumentProvider, name)

    public fun u64ObservableGauge(
        name: String,
    ): AsyncInstrumentBuilder<ObservableGauge<ULong>, ULong> =
        AsyncInstrumentBuilder(instrumentProvider, name)

    public fun f64ObservableGauge(
        name: String,
    ): AsyncInstrumentBuilder<ObservableGauge<Double>, Double> =
        AsyncInstrumentBuilder(instrumentProvider, name)

    public fun i64ObservableGauge(
        name: String,
    ): AsyncInstrumentBuilder<ObservableGauge<Long>, Long> =
        AsyncInstrumentBuilder(instrumentProvider, name)

    public fun u64Histogram(name: String): HistogramBuilder<Histogram<ULong>> =
        HistogramBuilder(instrumentProvider, name)

    public fun f64Histogram(name: String): HistogramBuilder<Histogram<Double>> =
        HistogramBuilder(instrumentProvider, name)

    public companion object {
        public fun new(instrumentProvider: InstrumentProvider): Meter =
            Meter(instrumentProvider)
    }
}
