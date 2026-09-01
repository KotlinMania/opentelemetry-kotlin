// port-lint: tests baggage.rs
package io.github.kotlinmania.opentelemetry

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class BaggageTest {
    @Test
    fun insertNonAsciiKey() {
        val baggage = Baggage.new()
        baggage.insert("\uD83D\uDEAB", "not ascii key")
        assertEquals(0, baggage.len(), "did not insert invalid key")
    }

    @Test
    fun testAsciiValues() {
        val string1 = "test_ 123"
        val string2 = "Hello123"
        val string3 = "This & That = More"
        val string4 = "Unicode: \uD83D\uDE0A"
        val string5 = "Non-ASCII: \u00E1\u00E9\u00ED\u00F3\u00FA"
        val string6 = "Unsafe: ~!@#$%^&*()_+{}[];:'\\\"<>?,./"
        val string7 = "\uD83D\uDE80Unicode:"
        val string8 = "\u0391\u0392\u0393"

        assertEquals("test_%20123", encode(string1))
        assertEquals("Hello123", encode(string2))
        assertEquals("This%20%26%20That%20%3D%20More", encode(string3))
        assertEquals("Unicode%3A%20%F0%9F%98%8A", encode(string4))
        assertEquals("Non-ASCII%3A%20%C3%A1%C3%A9%C3%AD%C3%B3%C3%BA", encode(string5))
        assertEquals("Unsafe%3A%20~%21%40%23%24%25%5E%26%2A%28%29_%2B%7B%7D%5B%5D%3B%3A%27%5C%22%3C%3E%3F%2C.%2F", encode(string6))
        assertEquals("%F0%9F%9A%80Unicode%3A", encode(string7))
        assertEquals("%CE%91%CE%92%CE%93", encode(string8))
    }

    @Test
    fun insertTooMuchBaggage() {
        val baggage = Baggage.new()
        val overLimit = MAX_KEY_VALUE_PAIRS + 1
        for (i in 0 until overLimit) {
            baggage.insert("key$i", "key$i")
        }
        assertEquals(MAX_KEY_VALUE_PAIRS, baggage.len())
    }

    @Test
    fun insertPairsLengthExceed() {
        val baggage = Baggage.new()
        val letters = listOf('a', 'b', 'c', 'd')
        for (letter in letters) {
            val key = letter.toString().repeat(MAX_LEN_OF_ALL_PAIRS / 3)
            baggage.insert(key, "")
        }
        assertEquals(3, baggage.len())
    }

    @Test
    fun serializeBaggageAsString() {
        val b0 = Baggage.new()
        assertEquals("", b0.toString())

        val b1 = Baggage.new()
        b1.insert("foo", "")
        assertEquals("foo=", b1.toString())

        val b2 = Baggage.new()
        b2.insert("foo", "1")
        assertEquals("foo=1", b2.toString())

        val b3 = Baggage.new()
        b3.insert("foo", "1=1")
        assertEquals("foo=1%3D1", b3.toString())

        val b4 = Baggage.new()
        b4.insertWithMetadata("foo", "", "red;state=on")
        assertEquals("foo=;red;state=on", b4.toString())

        val b5 = Baggage.new()
        b5.insertWithMetadata("foo", "1", "red;state=on;z=z=z")
        assertEquals("foo=1;red;state=on;z=z=z", b5.toString())

        val b6 = Baggage.new()
        b6.insertWithMetadata("foo", "1", "red;state=on")
        b6.insertWithMetadata("bar", "2", "yellow")
        val str = b6.toString()
        assertTrue(str.contains("bar=2;yellow"))
        assertTrue(str.contains("foo=1;red;state=on"))
    }

    @Test
    fun replaceExistingKey() {
        val halfMinus2 = "x".repeat(MAX_LEN_OF_ALL_PAIRS / 2 - 2)

        val b = Baggage.new()
        b.insert("a", halfMinus2)
        b.insert("b", halfMinus2)
        b.insert("c", ".")
        assertNotNull(b.get("a"))
        assertNotNull(b.get("b"))
        assertNotNull(b.get("c"))
        assertNull(b.insert("c", ".."))
        val replaced = b.insert("c", "!")
        assertEquals(StringValue("."), replaced)
    }

    @Test
    fun testCrudOperations() {
        val baggage = Baggage.new()
        assertTrue(baggage.isEmpty())

        baggage.insert("foo", "1")
        assertEquals(1, baggage.len())

        assertEquals(StringValue("1"), baggage.get("foo"))

        baggage.insert("foo", "2")
        assertEquals(StringValue("2"), baggage.get("foo"))

        baggage.remove("foo")
        assertTrue(baggage.isEmpty())
    }

    @Test
    fun testInsertInvalidKey() {
        val baggage = Baggage.new()

        baggage.insert("", "1")
        assertTrue(baggage.isEmpty())

        baggage.insert("Gr\u00FC\u00DFe", "1")
        assertTrue(baggage.isEmpty())

        baggage.insert("(example)", "1")
        assertTrue(baggage.isEmpty())
    }

    @Test
    fun testContextClearBaggage() {
        val ctx = Context.new()
        val ctxWithBaggage = ctx.withBaggage(listOf(KeyValue.new("foo", 1L)))
        val guard = ctxWithBaggage.attach()

        try {
            val currentCtx = Context.current()
            val baggage = currentCtx.baggage()
            assertEquals(1, baggage.len())

            val clearedCtx = currentCtx.withClearedBaggage()
            val innerGuard = clearedCtx.attach()
            try {
                val currentCleared = Context.current()
                val clearedBaggage = currentCleared.baggage()
                assertEquals(0, clearedBaggage.len())
            } finally {
                innerGuard.detach()
            }
        } finally {
            guard.detach()
        }
    }
}
