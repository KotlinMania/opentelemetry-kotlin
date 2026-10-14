// port-lint: source context/future_ext.rs
@file:OptIn(kotlin.experimental.ExperimentalObjCRefinement::class)

package io.github.kotlinmania.opentelemetry.context

import io.github.kotlinmania.futures.Poll
import io.github.kotlinmania.futures.Sink
import io.github.kotlinmania.futures.SinkOutcome
import io.github.kotlinmania.futures.Stream
import io.github.kotlinmania.futures.TaskContext
import io.github.kotlinmania.futures.Yield
import io.github.kotlinmania.opentelemetry.Context
import kotlin.native.HiddenFromObjC

/**
 * Extension trait allowing futures, streams, and sinks to be traced with a span.
 */
@HiddenFromObjC
public interface FutureExt {
    public fun withContext(otelCx: Context): Any = this

    public fun withCurrentContext(): Any = withContext(Context.current())
}

/**
 * A wrapper that pairs an inner object with an OpenTelemetry [Context].
 */
@HiddenFromObjC
public class WithContext<T>(
    public val inner: T,
    public val otelCx: Context,
) {
    public typealias Output = Any
    public typealias Item = Any
    public typealias Error = Any

    public fun poll(taskCx: TaskContext): Poll<Any?> {
        val guard = otelCx.attach()
        return try {
            Poll.Ready(inner)
        } finally {
            guard.detach()
        }
    }
}

/**
 * A [Stream] that evaluates with an associated OpenTelemetry [Context].
 */
@HiddenFromObjC
public class StreamWithContext<T>(
    public val inner: Stream<T>,
    public val otelCx: Context,
) : Stream<T> {
    override fun pollNext(context: TaskContext): Poll<Yield<T>> {
        val guard = otelCx.attach()
        return try {
            inner.pollNext(context)
        } finally {
            guard.detach()
        }
    }
}

/**
 * A [Sink] that evaluates with an associated OpenTelemetry [Context].
 */
@HiddenFromObjC
public class SinkWithContext<Item, E>(
    public val inner: Sink<Item, E>,
    public val otelCx: Context,
) : Sink<Item, E> {
    override fun pollReady(context: TaskContext): Poll<SinkOutcome<E>> {
        val guard = otelCx.attach()
        return try {
            inner.pollReady(context)
        } finally {
            guard.detach()
        }
    }

    override fun startSend(item: Item): SinkOutcome<E> {
        val guard = otelCx.attach()
        return try {
            inner.startSend(item)
        } finally {
            guard.detach()
        }
    }

    override fun pollFlush(context: TaskContext): Poll<SinkOutcome<E>> {
        val guard = otelCx.attach()
        return try {
            inner.pollFlush(context)
        } finally {
            guard.detach()
        }
    }

    override fun pollClose(context: TaskContext): Poll<SinkOutcome<E>> {
        val guard = otelCx.attach()
        return try {
            inner.pollClose(context)
        } finally {
            guard.detach()
        }
    }
}

@HiddenFromObjC
public fun <T> Stream<T>.withContext(otelCx: Context): StreamWithContext<T> =
    StreamWithContext(this, otelCx)

@HiddenFromObjC
public fun <T> Stream<T>.withCurrentContext(): StreamWithContext<T> =
    StreamWithContext(this, Context.current())

@HiddenFromObjC
public fun <Item, E> Sink<Item, E>.withContext(otelCx: Context): SinkWithContext<Item, E> =
    SinkWithContext(this, otelCx)

@HiddenFromObjC
public fun <Item, E> Sink<Item, E>.withCurrentContext(): SinkWithContext<Item, E> =
    SinkWithContext(this, Context.current())
