package com.rotilho.jnano.node.codec.peer

import com.rotilho.jnano.commons.NanoHelper
import com.rotilho.jnano.node.ContextHolder
import com.rotilho.jnano.node.Environment
import com.rotilho.jnano.node.Node
import com.rotilho.jnano.node.codec.network.PacketType
import com.rotilho.jnano.node.codec.network.TCPCodec
import com.rotilho.jnano.node.peer.handshake.HandshakeChallenge
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.net.InetAddress
import java.net.InetSocketAddress
import java.util.stream.Stream

internal class HandshakeChallengeCodecTest {
    private val codec = TCPCodec(PacketType.HANDSHAKE, node, HandshakeChallengeCodec(NodeCodec()))

    @BeforeEach
    fun `Set up`() {
        ContextHolder.put("socketAddress", InetSocketAddress(InetAddress.getLocalHost(), 7050))
    }

    @ParameterizedTest
    @MethodSource("encodeProvider")
    fun `Should encode`(handshakeAnswer: HandshakeChallenge, expectedEncoded: String) {
        // when
        val encoded = codec.encode(node.protocolVersion, handshakeAnswer)!!

        // then
        Assertions.assertEquals(expectedEncoded, NanoHelper.toHex(encoded))
    }

    @ParameterizedTest
    @MethodSource("decodeProvider")
    fun `Should decode`(encoded: String, expectedHandshakeAnswer: HandshakeChallenge) {
        // when
        val handshakeAnswer = codec.decode(node.protocolVersion, NanoHelper.toByteArray(encoded))

        // then
        Assertions.assertEquals(expectedHandshakeAnswer, handshakeAnswer)
    }


    companion object {
        private val node = Node(InetSocketAddress(InetAddress.getLocalHost(), 7050), Environment.LIVE, 13)

        private const val encodedExtension1 =
            "52430D0D0D0A01007929DF5BEBB4A10C2BA5C05D3851A1D989C4071618599DA1249D9D2CFE420BFB"

        val handshakeExtension1 = HandshakeChallenge(
            node,
            NanoHelper.toByteArray("7929DF5BEBB4A10C2BA5C05D3851A1D989C4071618599DA1249D9D2CFE420BFB")
        )


        @JvmStatic
        fun encodeProvider(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(handshakeExtension1, encodedExtension1)
            )
        }

        @JvmStatic
        fun decodeProvider(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(encodedExtension1, handshakeExtension1)
            )
        }
    }
}