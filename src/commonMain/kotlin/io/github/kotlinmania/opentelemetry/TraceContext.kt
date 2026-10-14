// port-lint: source trace_context.rs
package io.github.kotlinmania.opentelemetry

/**
 * Flags that can be set on a SpanContext.
 */
public class TraceFlags(
    public val value: Byte,
) {
    public fun isSampled(): Boolean = (value.toInt() and SAMPLED.value.toInt()) == SAMPLED.value.toInt()

    public fun withSampled(sampled: Boolean): TraceFlags =
        if (sampled) {
            TraceFlags((value.toInt() or SAMPLED.value.toInt()).toByte())
        } else {
            TraceFlags((value.toInt() and SAMPLED.value.toInt().inv()).toByte())
        }

    public fun toByte(): Byte = value

    public fun toU8(): Int = value.toInt() and 0xFF

    public fun bitand(rhs: TraceFlags): TraceFlags = TraceFlags((value.toInt() and rhs.value.toInt()).toByte())

    public fun bitor(rhs: TraceFlags): TraceFlags = TraceFlags((value.toInt() or rhs.value.toInt()).toByte())

    public operator fun not(): TraceFlags = TraceFlags((value.toInt().inv()).toByte())

    override fun equals(other: Any?): Boolean = other is TraceFlags && value == other.value

    override fun hashCode(): Int = value.hashCode()

    override fun toString(): String {
        val unsigned = value.toInt() and 0xFF
        val hex = unsigned.toString(16)
        return if (hex.length == 1) "0$hex" else hex
    }

    public companion object {
        public typealias Output = TraceFlags

        public val NOT_SAMPLED: TraceFlags = TraceFlags(0x00)
        public val SAMPLED: TraceFlags = TraceFlags(0x01)

        public fun new(flags: Byte): TraceFlags = TraceFlags(flags)

        public fun default(): TraceFlags = NOT_SAMPLED
    }
}

/**
 * A 16-byte value which identifies a given trace.
 */
public class TraceId(
    bytes: ByteArray,
) {
    private val rawBytes: ByteArray =
        if (bytes.size == 16) {
            bytes.copyOf()
        } else {
            ByteArray(16).apply {
                val copyLen = minOf(bytes.size, 16)
                bytes.copyInto(this, destinationOffset = 16 - copyLen, startIndex = bytes.size - copyLen, endIndex = bytes.size)
            }
        }

    public fun toBytes(): ByteArray = rawBytes.copyOf()

    public fun isValid(): Boolean = rawBytes.any { it != 0.toByte() }

    override fun equals(other: Any?): Boolean = other is TraceId && rawBytes.contentEquals(other.rawBytes)

    override fun hashCode(): Int = rawBytes.contentHashCode()

    override fun toString(): String {
        val sb = StringBuilder(32)
        for (b in rawBytes) {
            val unsigned = b.toInt() and 0xFF
            val hex = unsigned.toString(16)
            if (hex.length == 1) sb.append('0')
            sb.append(hex)
        }
        return sb.toString()
    }

    public companion object {
        public val INVALID: TraceId = TraceId(ByteArray(16))

        public fun fromBytes(bytes: ByteArray): TraceId {
            require(bytes.size == 16) { "TraceId must be exactly 16 bytes" }
            return TraceId(bytes)
        }

        public fun fromHex(hex: String): TraceId {
            val trimmed = hex.trim()
            val padded =
                if (trimmed.length < 32) {
                    "0".repeat(32 - trimmed.length) + trimmed
                } else if (trimmed.length == 32) {
                    trimmed
                } else {
                    throw IllegalArgumentException("Hex string too long for TraceId: $hex")
                }
            val result = ByteArray(16)
            for (i in 0 until 16) {
                val byteStr = padded.substring(i * 2, i * 2 + 2)
                result[i] = byteStr.toInt(16).toByte()
            }
            return TraceId(result)
        }

        public fun fromLong(value: Long): TraceId {
            val bytes = ByteArray(16)
            for (i in 0 until 8) {
                bytes[15 - i] = ((value ushr (i * 8)) and 0xFF).toByte()
            }
            return TraceId(bytes)
        }
    }
}

/**
 * An 8-byte value which identifies a given span.
 */
public class SpanId(
    public val value: Long,
) {
    public fun toBytes(): ByteArray {
        val bytes = ByteArray(8)
        for (i in 0 until 8) {
            bytes[7 - i] = ((value ushr (i * 8)) and 0xFF).toByte()
        }
        return bytes
    }

    public fun isValid(): Boolean = value != 0L

    override fun equals(other: Any?): Boolean = other is SpanId && value == other.value

    override fun hashCode(): Int = value.hashCode()

    override fun toString(): String {
        val bytes = toBytes()
        val sb = StringBuilder(16)
        for (b in bytes) {
            val unsigned = b.toInt() and 0xFF
            val hex = unsigned.toString(16)
            if (hex.length == 1) sb.append('0')
            sb.append(hex)
        }
        return sb.toString()
    }

    public companion object {
        public val INVALID: SpanId = SpanId(0L)

        public fun fromBytes(bytes: ByteArray): SpanId {
            require(bytes.size == 8) { "SpanId must be exactly 8 bytes" }
            var result = 0L
            for (i in 0 until 8) {
                result = (result shl 8) or (bytes[i].toLong() and 0xFFL)
            }
            return SpanId(result)
        }

        public fun fromHex(hex: String): SpanId {
            val trimmed = hex.trim()
            val padded =
                if (trimmed.length < 16) {
                    "0".repeat(16 - trimmed.length) + trimmed
                } else if (trimmed.length == 16) {
                    trimmed
                } else {
                    throw IllegalArgumentException("Hex string too long for SpanId: $hex")
                }
            var result = 0L
            for (i in 0 until 8) {
                val byteStr = padded.substring(i * 2, i * 2 + 2)
                result = (result shl 8) or (byteStr.toInt(16).toLong() and 0xFFL)
            }
            return SpanId(result)
        }

        public fun from(value: Long): SpanId = SpanId(value)
    }
}
