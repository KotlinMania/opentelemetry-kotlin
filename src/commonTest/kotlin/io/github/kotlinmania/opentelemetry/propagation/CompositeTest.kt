// port-lint: tests propagation/composite.rs
package io.github.kotlinmania.opentelemetry.propagation

import io.github.kotlinmania.opentelemetry.Context
import io.github.kotlinmania.opentelemetry.KeyValue
import io.github.kotlinmania.opentelemetry.baggage
import io.github.kotlinmania.opentelemetry.withBaggage
import kotlin.test.Test
import kotlin.test.assertEquals

class CompositeTest {
    private class TestPropagator(
        val header: String,
        val fields: List<String> = listOf(header),
    ) : TextMapPropagator {
        override fun injectContext(cx: Context, injector: Injector) {
            when (header) {
                "custom-header" -> {
                    val value = cx.getValue("custom-key")
                    if (value != null) {
                        injector.set(header, value)
                    }
                }

                "baggage" -> {
                    val baggageStr = cx.baggage().toString()
                    injector.set(header, baggageStr)
                }
            }
        }

        override fun extractWithContext(cx: Context, extractor: Extractor): Context =
            when (header) {
                "custom-header" -> {
                    val value = extractor.get(header)
                    if (value != null) cx.withValue("custom-key", value) else cx
                }

                "baggage" -> {
                    val value = extractor.get(header)
                    if (value != null) cx.withBaggage(listOf(KeyValue("baggagekey", "value"))) else cx
                }

                else -> {
                    cx
                }
            }

        override fun fields(): FieldIter = FieldIter.new(fields)
    }

    private fun setup(): Context {
        var cx = Context.default()
        cx = cx.withValue("custom-key", "custom-value")
        return cx.withBaggage(listOf(KeyValue("baggagekey", "value")))
    }

    @Test
    fun zeroPropogatorsAreNoop() {
        val compositePropagator = TextMapCompositePropagator.new(emptyList())
        val cx = setup()

        val carrier = MapCarrier()
        compositePropagator.injectContext(cx, carrier)
        assertEquals(0, carrier.keys().size)

        val extractor = MapCarrier()
        extractor.set("custom-header", "custom-value")
        val extractedCx = compositePropagator.extract(extractor)
        assertEquals(Context.default().getValue("custom-key"), extractedCx.getValue("custom-key"))
    }

    @Test
    fun injectMultiplePropagators() {
        val compositePropagator =
            TextMapCompositePropagator.new(
                listOf(
                    TestPropagator("custom-header"),
                    TestPropagator("baggage"),
                ),
            )

        val cx = setup()
        val carrier = MapCarrier()
        compositePropagator.injectContext(cx, carrier)

        assertEquals("custom-value", carrier.get("custom-header"))
        assertEquals("baggagekey=value", carrier.get("baggage"))
    }

    @Test
    fun extractMultiplePropagators() {
        val compositePropagator =
            TextMapCompositePropagator.new(
                listOf(
                    TestPropagator("custom-header"),
                    TestPropagator("baggage"),
                ),
            )

        val extractor = MapCarrier()
        extractor.set("custom-header", "custom-value")
        extractor.set("baggage", "baggagekey=value")

        val cx = compositePropagator.extract(extractor)
        assertEquals("custom-value", cx.getValue("custom-key"))
        assertEquals("baggagekey=value", cx.baggage().toString())
    }

    @Test
    fun testGetFields() {
        val testCases =
            listOf(
                Pair(
                    listOf("custom-header", "baggage"),
                    listOf("baggage", "custom-header"),
                ),
            )

        for (testCase in testCases) {
            val propagators = testCase.first.map { name -> TestPropagator(name) }
            val compositePropagator = TextMapCompositePropagator.new(propagators)

            val fields = compositePropagator.fields().toList().sorted()
            assertEquals(testCase.second, fields)
        }
    }
}
