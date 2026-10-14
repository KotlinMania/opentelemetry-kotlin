// port-lint: tests context/future_ext.rs
package io.github.kotlinmania.opentelemetry.context

import io.github.kotlinmania.futures.Poll
import io.github.kotlinmania.futures.Sink
import io.github.kotlinmania.futures.SinkOutcome
import io.github.kotlinmania.futures.Stream
import io.github.kotlinmania.futures.TaskContext
import io.github.kotlinmania.futures.Yield
import io.github.kotlinmania.opentelemetry.Context
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FutureExtTest {
    private class ContextInspectingStream : Stream<String> {
        var observedContextValue: String? = null

        override fun pollNext(context: TaskContext): Poll<Yield<String>> {
            observedContextValue = Context.current().getValue("test-key")
            return Poll.Ready(Yield.Value("hello"))
        }
    }

    private class ContextInspectingSink : Sink<String, String> {
        var observedContextValueOnReady: String? = null
        var observedContextValueOnSend: String? = null
        var observedContextValueOnFlush: String? = null
        var observedContextValueOnClose: String? = null

        override fun pollReady(context: TaskContext): Poll<SinkOutcome<String>> {
            observedContextValueOnReady = Context.current().getValue("test-key")
            return Poll.Ready(SinkOutcome.Ready)
        }

        override fun startSend(item: String): SinkOutcome<String> {
            observedContextValueOnSend = Context.current().getValue("test-key")
            return SinkOutcome.Ready
        }

        override fun pollFlush(context: TaskContext): Poll<SinkOutcome<String>> {
            observedContextValueOnFlush = Context.current().getValue("test-key")
            return Poll.Ready(SinkOutcome.Ready)
        }

        override fun pollClose(context: TaskContext): Poll<SinkOutcome<String>> {
            observedContextValueOnClose = Context.current().getValue("test-key")
            return Poll.Ready(SinkOutcome.Ready)
        }
    }

    @Test
    fun streamWithContextAttachesContext() {
        val stream = ContextInspectingStream()
        val customCx = Context.default().withValue("test-key", "stream-cx")
        val streamWithCx = stream.withContext(customCx)

        val taskContext = TaskContext()
        val outcome = streamWithCx.pollNext(taskContext)

        assertTrue(outcome is Poll.Ready)
        assertEquals("stream-cx", stream.observedContextValue)
    }

    @Test
    fun sinkWithContextAttachesContext() {
        val sink = ContextInspectingSink()
        val customCx = Context.default().withValue("test-key", "sink-cx")
        val sinkWithCx = sink.withContext(customCx)

        val taskContext = TaskContext()
        sinkWithCx.pollReady(taskContext)
        assertEquals("sink-cx", sink.observedContextValueOnReady)

        sinkWithCx.startSend("item")
        assertEquals("sink-cx", sink.observedContextValueOnSend)

        sinkWithCx.pollFlush(taskContext)
        assertEquals("sink-cx", sink.observedContextValueOnFlush)

        sinkWithCx.pollClose(taskContext)
        assertEquals("sink-cx", sink.observedContextValueOnClose)
    }

    @Test
    fun withCurrentContextCapturesActiveContext() {
        val stream = ContextInspectingStream()
        val customCx = Context.default().withValue("test-key", "current-cx")
        val guard = customCx.attach()
        try {
            val streamWithCx = stream.withCurrentContext()
            val taskContext = TaskContext()
            streamWithCx.pollNext(taskContext)
            assertEquals("current-cx", stream.observedContextValue)
        } finally {
            guard.detach()
        }
    }
}
