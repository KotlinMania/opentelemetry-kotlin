package io.github.kotlinmania.opentelemetry.metrics

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
 * SDK implemented interface for creating instruments.
 */
public interface InstrumentProvider {
    public fun u64Counter(builder: InstrumentBuilder<Counter<ULong>>): Counter<ULong> =
        Counter.new(NoopSyncInstrument.new())

    public fun f64Counter(builder: InstrumentBuilder<Counter<Double>>): Counter<Double> =
        Counter.new(NoopSyncInstrument.new())

    public fun u64ObservableCounter(
        builder: AsyncInstrumentBuilder<ObservableCounter<ULong>, ULong>,
    ): ObservableCounter<ULong> = ObservableCounter.new()

    public fun f64ObservableCounter(
        builder: AsyncInstrumentBuilder<ObservableCounter<Double>, Double>,
    ): ObservableCounter<Double> = ObservableCounter.new()

    public fun i64UpDownCounter(builder: InstrumentBuilder<UpDownCounter<Long>>): UpDownCounter<Long> =
        UpDownCounter.new(NoopSyncInstrument.new())

    public fun f64UpDownCounter(builder: InstrumentBuilder<UpDownCounter<Double>>): UpDownCounter<Double> =
        UpDownCounter.new(NoopSyncInstrument.new())

    public fun i64ObservableUpDownCounter(
        builder: AsyncInstrumentBuilder<ObservableUpDownCounter<Long>, Long>,
    ): ObservableUpDownCounter<Long> = ObservableUpDownCounter.new()

    public fun f64ObservableUpDownCounter(
        builder: AsyncInstrumentBuilder<ObservableUpDownCounter<Double>, Double>,
    ): ObservableUpDownCounter<Double> = ObservableUpDownCounter.new()

    public fun u64Gauge(builder: InstrumentBuilder<Gauge<ULong>>): Gauge<ULong> =
        Gauge.new(NoopSyncInstrument.new())

    public fun f64Gauge(builder: InstrumentBuilder<Gauge<Double>>): Gauge<Double> =
        Gauge.new(NoopSyncInstrument.new())

    public fun i64Gauge(builder: InstrumentBuilder<Gauge<Long>>): Gauge<Long> =
        Gauge.new(NoopSyncInstrument.new())

    public fun u64ObservableGauge(
        builder: AsyncInstrumentBuilder<ObservableGauge<ULong>, ULong>,
    ): ObservableGauge<ULong> = ObservableGauge.new()

    public fun f64ObservableGauge(
        builder: AsyncInstrumentBuilder<ObservableGauge<Double>, Double>,
    ): ObservableGauge<Double> = ObservableGauge.new()

    public fun i64ObservableGauge(
        builder: AsyncInstrumentBuilder<ObservableGauge<Long>, Long>,
    ): ObservableGauge<Long> = ObservableGauge.new()

    public fun u64Histogram(builder: HistogramBuilder<Histogram<ULong>>): Histogram<ULong> =
        Histogram.new(NoopSyncInstrument.new())

    public fun f64Histogram(builder: HistogramBuilder<Histogram<Double>>): Histogram<Double> =
        Histogram.new(NoopSyncInstrument.new())

    public fun <T> histogram(builder: HistogramBuilder<T>): Histogram<T> =
        Histogram.new(NoopSyncInstrument.new())
}
