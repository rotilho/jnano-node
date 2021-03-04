package com.rotilho.jnano.node.codec.tcp

import com.rotilho.jnano.commons.NanoHelper
import com.rotilho.jnano.node.Environment
import com.rotilho.jnano.node.Node
import com.rotilho.jnano.node.codec.ByteArrayCodecSupport
import com.rotilho.jnano.node.codec.network.PacketType
import com.rotilho.jnano.node.codec.network.TCPCodec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.net.InetAddress
import java.net.InetSocketAddress
import java.util.stream.Stream


internal class TCPCodecTest {
    private val protocolVersion = 13
    private val targetObject = 1

    @ParameterizedTest
    @MethodSource("encodeProvider")
    fun `Should encode`(o: Int?, expected: String?) {
        // given
        val codecMock = mockk<ByteArrayCodecSupport>()
        every { codecMock.encode(protocolVersion, targetObject) } returns if (o != null) byteArrayOf(o.toByte()) else null

        val udpCodec = TCPCodec(PacketType.KEEP_ALIVE, Node(InetSocketAddress(InetAddress.getLocalHost(), 7050), Environment.LIVE, protocolVersion), codecMock)

        // when
        val encoded = udpCodec.encode(protocolVersion, targetObject)

        // then
        verify { codecMock.encode(protocolVersion, targetObject) }
        assertEquals(expected, if (encoded != null) NanoHelper.toHex(encoded) else null);
    }


    @ParameterizedTest
    @MethodSource("decodeProvider")
    fun `Should decode`(m: String, expected: Int?) {
        // given
        val codecMock = mockk<ByteArrayCodecSupport>()

        val message = NanoHelper.toByteArray(m)
        val messageBody = message.copyOfRange(6, message.size)
        every { codecMock.decode(protocolVersion, messageBody) } returns expected

        val udpCodec = TCPCodec(PacketType.KEEP_ALIVE, Node(InetSocketAddress(InetAddress.getLocalHost(), 7050), Environment.LIVE, protocolVersion), codecMock)

        // when
        val decoded = udpCodec.decode(protocolVersion, message)

        // then
        verify { codecMock.decode(protocolVersion, messageBody) }
        assertEquals(expected, decoded);
    }

    companion object {
        @JvmStatic
        fun encodeProvider(): Stream<Arguments> {
            return Stream.of(
                    Arguments.of(1, "52430D0D0D0201"),
                    Arguments.of(null, null)
            )
        }

        @JvmStatic
        fun decodeProvider(): Stream<Arguments> {
            return Stream.of(
                    Arguments.of("52430D0D0D0201", 1),
                    Arguments.of("52430D0D0D0201", null)
            )
        }
    }

}

