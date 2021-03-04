package com.rotilho.jnano.node.codec.peer

import com.rotilho.jnano.commons.NanoHelper
import com.rotilho.jnano.node.ContextHolder
import com.rotilho.jnano.node.Environment
import com.rotilho.jnano.node.Node
import com.rotilho.jnano.node.codec.network.PacketType
import com.rotilho.jnano.node.codec.network.TCPCodec
import com.rotilho.jnano.node.peer.KeepAlive
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.net.InetAddress
import java.net.InetSocketAddress
import java.util.stream.Stream

internal class KeepAliveCodecTest {
    private val codec = TCPCodec(PacketType.KEEP_ALIVE, node, KeepAliveCodec(NodeCodec()))

    @BeforeEach
    fun `Set up`() {
        ContextHolder.put("socketAddress", InetSocketAddress(InetAddress.getLocalHost(), 7050))
    }

    @ParameterizedTest
    @MethodSource("encodeProvider")
    fun `Should encode`(keepAlive: KeepAlive, expectedEncoded: String) {
        // when
        val encoded = codec.encode(node.protocolVersion, keepAlive)!!

        // then
        Assertions.assertEquals(expectedEncoded, NanoHelper.toHex(encoded))
    }

    @ParameterizedTest
    @MethodSource("decodeProvider")
    fun `Should decode`(encoded: String, expectedKeepAlive: KeepAlive) {
        // when
        val handshakeAnswer = codec.decode(node.protocolVersion, NanoHelper.toByteArray(encoded))

        // then
        Assertions.assertEquals(expectedKeepAlive, handshakeAnswer)
    }

    companion object {
        private val node = Node(InetSocketAddress(InetAddress.getLocalHost(), 7050), Environment.BETA, 13)

        private const val encoded =
            "52420D0D0D02000000000000000000000000FFFF5741A00FF0D200000000000000000000FFFFB92D717CF0D200000000000000000000FFFFB2809596000400000000000000000000FFFF5019A0D9F0D200000000000000000000FFFF5AE5C774F0D200000000000000000000FFFF51A9F35AF0D200000000000000000000FFFF4D14FE3B726100000000000000000000FFFF0D3BA266F0D2"

        val keepAlive = KeepAlive(
            node,
            listOf(
                parse("87.65.160.15:54000"),
                parse("185.45.113.124:54000"),
                parse("178.128.149.150:1024"),
                parse("80.25.160.217:54000"),
                parse("90.229.199.116:54000"),
                parse("81.169.243.90:54000"),
                parse("77.20.254.59:24946"),
                parse("13.59.162.102:54000")
            )
        )

        @JvmStatic
        fun encodeProvider(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(keepAlive, encoded)
            )
        }

        @JvmStatic
        fun decodeProvider(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(encoded, keepAlive)
            )
        }

        private fun parse(peer: String): InetSocketAddress {
            val values = peer.split(":");
            val host = InetAddress.getByName(values[0])
            val port = values[1].toInt()
            return InetSocketAddress(host, port)
        }
    }
}