// port-lint: tests context.rs
package io.github.kotlinmania.opentelemetry

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

public data class ValueA(
    val value: Long,
)

public data class ValueB(
    val value: Long,
)

class ContextTest {
    @Test
    fun contextImmutable() {
        val cx = Context.new()
        assertNull(cx.get<ValueA>())
        assertNull(cx.get<ValueB>())

        val cxNew = cx.withValue(ValueA(1))

        assertNull(cx.get<ValueA>())
        assertNull(cx.get<ValueB>())

        assertEquals(ValueA(1), cxNew.get<ValueA>())

        val cxNewer = cxNew.withValue(ValueB(1))

        assertNull(cx.get<ValueA>())
        assertNull(cx.get<ValueB>())
        assertEquals(ValueA(1), cxNew.get<ValueA>())
        assertNull(cxNew.get<ValueB>())

        assertEquals(ValueA(1), cxNewer.get<ValueA>())
        assertEquals(ValueB(1), cxNewer.get<ValueB>())
    }

    @Test
    fun nestedContexts() {
        val outerGuard = Context.new().withValue(ValueA(1)).attach()
        try {
            val current = Context.current()
            assertEquals(ValueA(1), current.get<ValueA>())
            assertNull(current.get<ValueB>())

            val innerGuard = Context.currentWithValue(ValueB(42)).attach()
            try {
                val currentInner = Context.current()
                assertEquals(ValueA(1), currentInner.get<ValueA>())
                assertEquals(ValueB(42), currentInner.get<ValueB>())

                assertTrue(
                    Context.mapCurrent { cx ->
                        assertEquals(ValueA(1), cx.get<ValueA>())
                        assertEquals(ValueB(42), cx.get<ValueB>())
                        true
                    },
                )
            } finally {
                innerGuard.detach()
            }

            val currentRestored = Context.current()
            assertEquals(ValueA(1), currentRestored.get<ValueA>())
            assertNull(currentRestored.get<ValueB>())
        } finally {
            outerGuard.detach()
        }
    }

    @Test
    fun overlappingContexts() {
        val outerGuard = Context.new().withValue(ValueA(1)).attach()

        val current = Context.current()
        assertEquals(ValueA(1), current.get<ValueA>())
        assertNull(current.get<ValueB>())

        val innerGuard = Context.currentWithValue(ValueB(42)).attach()

        val currentInner = Context.current()
        assertEquals(ValueA(1), currentInner.get<ValueA>())
        assertEquals(ValueB(42), currentInner.get<ValueB>())

        outerGuard.detach()

        val currentMid = Context.current()
        assertEquals(ValueA(1), currentMid.get<ValueA>())
        assertEquals(ValueB(42), currentMid.get<ValueB>())

        innerGuard.detach()
    }

    @Test
    fun tooManyContexts() {
        val guards = mutableListOf<ContextGuard>()
        for (i in 1..10) {
            val cxGuard = Context.current().withValue(ValueB(i.toLong())).attach()
            assertEquals(ValueB(i.toLong()), Context.current().get<ValueB>())
            guards.add(cxGuard)
        }
        while (guards.isNotEmpty()) {
            guards.removeAt(guards.size - 1).detach()
        }
    }

    @Test
    fun testInitialCapacity() {
        val stack = ContextStack.default()
        assertEquals(0, stack.stack.size)
    }

    @Test
    fun testMapCurrentCx() {
        val stack = ContextStack.default()
        val res =
            stack.mapCurrentCx { cx ->
                cx.isTelemetrySuppressed()
            }
        assertFalse(res)
    }

    @Test
    fun testPopIdOutOfOrder() {
        val stack = ContextStack.default()
        val id1 = stack.push(Context.new().withValue("k1", "v1"))
        val id2 = stack.push(Context.new().withValue("k2", "v2"))
        stack.popId(id1)
        stack.popId(id2)
    }

    @Test
    fun testPopIdEdgeCases() {
        val stack = ContextStack.default()
        stack.popId(ContextStack.BASE_POS)
        stack.popId(ContextStack.MAX_POS)
    }

    @Test
    fun testPushOverflow() {
        val stack = ContextStack.default()
        val id = stack.push(Context.new())
        assertTrue(id > 0)
    }

    fun nestedOperation() {
        assertEquals(ValueA(42), Context.current().get<ValueA>())
        val cxWithBoth =
            Context
                .current()
                .withValue(ValueA(43))
                .withValue(ValueB(24))
        val guard = cxWithBoth.attach()
        try {
            assertEquals(ValueA(43), Context.current().get<ValueA>())
            assertEquals(ValueB(24), Context.current().get<ValueB>())
        } finally {
            guard.detach()
        }
    }

    @Test
    fun testAsyncContextPropagation() {
        val parentCx = Context.new().withValue(ValueA(42))
        val guard = parentCx.attach()
        try {
            nestedOperation()
        } finally {
            guard.detach()
        }
        assertEquals(ValueA(42), parentCx.get<ValueA>())
        assertNull(parentCx.get<ValueB>())
    }

    fun createAFuture(): Context {
        assertEquals(ValueA(42), Context.current().get<ValueA>())
        return Context.current()
    }

    @Test
    fun testOutOfOrderContextDetachmentFutures() {
        val parentCx = Context.new().withValue(ValueA(42))
        val guard = parentCx.attach()
        try {
            val futureCtx = createAFuture()
            assertEquals(ValueA(42), futureCtx.get<ValueA>())
        } finally {
            guard.detach()
        }
        assertNull(Context.current().get<ValueA>())
        assertNull(Context.current().get<ValueB>())
    }

    @Test
    fun testIsTelemetrySuppressed() {
        val cx = Context.new()
        assertFalse(cx.isTelemetrySuppressed())

        val suppressed = cx.withTelemetrySuppressed()
        assertTrue(suppressed.isTelemetrySuppressed())
    }

    @Test
    fun testWithTelemetrySuppressed() {
        val cx = Context.new()
        assertFalse(cx.isTelemetrySuppressed())

        val suppressed = cx.withTelemetrySuppressed()
        assertFalse(cx.isTelemetrySuppressed())
        assertTrue(suppressed.isTelemetrySuppressed())

        val cxWithValue = cx.withValue(ValueA(42))
        val suppressedWithValue = cxWithValue.withTelemetrySuppressed()
        assertFalse(cxWithValue.isTelemetrySuppressed())
        assertTrue(suppressedWithValue.isTelemetrySuppressed())
        assertEquals(ValueA(42), suppressedWithValue.get<ValueA>())
    }

    @Test
    fun testEnterTelemetrySuppressedScope() {
        val resetGuard = Context.new().attach()
        try {
            assertFalse(Context.isCurrentTelemetrySuppressed())

            val cxWithValue = Context.current().withValue(ValueA(42))
            val guardWithValue = cxWithValue.attach()
            try {
                assertEquals(ValueA(42), Context.current().get<ValueA>())
                assertFalse(Context.isCurrentTelemetrySuppressed())

                val guard = Context.enterTelemetrySuppressedScope()
                try {
                    assertTrue(Context.isCurrentTelemetrySuppressed())
                    assertTrue(Context.current().isTelemetrySuppressed())
                    assertEquals(ValueA(42), Context.current().get<ValueA>())
                } finally {
                    guard.detach()
                }

                assertFalse(Context.isCurrentTelemetrySuppressed())
                assertFalse(Context.current().isTelemetrySuppressed())
                assertEquals(ValueA(42), Context.current().get<ValueA>())
            } finally {
                guardWithValue.detach()
            }
        } finally {
            resetGuard.detach()
        }
    }

    @Test
    fun testNestedSuppressionScopes() {
        val resetGuard = Context.new().attach()
        try {
            assertFalse(Context.isCurrentTelemetrySuppressed())

            val outer = Context.enterTelemetrySuppressedScope()
            try {
                assertTrue(Context.isCurrentTelemetrySuppressed())

                val inner = Context.current().withValue(ValueA(1)).attach()
                try {
                    assertTrue(Context.isCurrentTelemetrySuppressed())
                    assertEquals(ValueA(1), Context.current().get<ValueA>())
                } finally {
                    inner.detach()
                }

                val inner2 = Context.new().withValue(ValueA(1)).attach()
                try {
                    assertFalse(Context.isCurrentTelemetrySuppressed())
                    assertEquals(ValueA(1), Context.current().get<ValueA>())
                } finally {
                    inner2.detach()
                }

                assertTrue(Context.isCurrentTelemetrySuppressed())
            } finally {
                outer.detach()
            }

            assertFalse(Context.isCurrentTelemetrySuppressed())
        } finally {
            resetGuard.detach()
        }
    }

    @Test
    fun testAsyncSuppression() {
        val suppressedParent = Context.new().withTelemetrySuppressed()
        assertFalse(Context.isCurrentTelemetrySuppressed())

        val guard = suppressedParent.attach()
        try {
            assertTrue(Context.isCurrentTelemetrySuppressed())
            val cxAdditional = Context.current().withValue(ValueB(24))
            val innerGuard = cxAdditional.attach()
            try {
                assertEquals(ValueB(24), Context.current().get<ValueB>())
                assertTrue(Context.isCurrentTelemetrySuppressed())
            } finally {
                innerGuard.detach()
            }
        } finally {
            guard.detach()
        }

        assertTrue(suppressedParent.isTelemetrySuppressed())
        assertFalse(Context.isCurrentTelemetrySuppressed())
    }
}
