package com.rotilho.jnano.node.codec.peer

import com.rotilho.jnano.commons.NanoHelper
import com.rotilho.jnano.node.ContextHolder
import com.rotilho.jnano.node.Environment
import com.rotilho.jnano.node.Node
import com.rotilho.jnano.node.codec.network.PacketType
import com.rotilho.jnano.node.codec.network.TCPCodec
import com.rotilho.jnano.node.peer.handshake.HandshakeAnswer
import com.rotilho.jnano.node.peer.handshake.HandshakeChallenge
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.net.InetAddress
import java.net.InetSocketAddress
import java.util.stream.Stream

internal class HandshakeAnswerCodecTest {
    private val codec = TCPCodec(PacketType.HANDSHAKE, node, HandshakeAnswerCodec(NodeCodec()))

    @BeforeEach
    fun `Set up`() {
        ContextHolder.put("socketAddress", InetSocketAddress(InetAddress.getLocalHost(), 7050))
    }

    @ParameterizedTest
    @MethodSource("encodeProvider")
    fun `Should encode`(handshakeAnswer: HandshakeAnswer, expectedEncoded: String) {
        // when
        val encoded = codec.encode(7, handshakeAnswer)!!

        // then
        Assertions.assertEquals(expectedEncoded, NanoHelper.toHex(encoded))
    }

    @ParameterizedTest
    @MethodSource("decodeProvider")
    fun `Should decode`(encoded: String, expectedHandshakeAnswer: HandshakeAnswer) {
        // when
        val handshakeAnswer = codec.decode(7, NanoHelper.toByteArray(encoded))

        // then
        Assertions.assertEquals(expectedHandshakeAnswer, handshakeAnswer)
    }


    companion object {
        private val node = Node(InetSocketAddress(InetAddress.getLocalHost(), 7050), Environment.LIVE, 13)

        private const val encodedExtension3 =
            "52430D0D070A0300C47A5890B727C97323B0089E1F3D8CB559B4CB70107CA3CD0351C6B7B02A7E818DEC937B67722A7DE508905674DAF6F5F7E99B676B304F0A0FC11966A08213CCC211B16511A9B5005CD484C9447DFA001F6F7916958D8C9348A49D124EAD144D8188EF8C5C70B967FF865CAB826EA7CC53E85C6CEA075258D3BDEE7B32EC7709"
        private const val encodedExtension2 =
            "52430D0D070A020064C49362A7B0101F0434EC6AB06C8A28C93DAF9974F32A88405E220894AE2164498C9B66C11E5CD9AE492A0DBADC633BE2A8F375E51E51D0A6727B47F17F4E7489545204798D03E6D257A2A94A3C71387EBAE1F48C59D817D3CEDE54298AF600"

        val handshakeExtension3 = HandshakeAnswer(
            node,
            HandshakeChallenge(
                node,
                NanoHelper.toByteArray("C47A5890B727C97323B0089E1F3D8CB559B4CB70107CA3CD0351C6B7B02A7E81")
            ),
            NanoHelper.toByteArray("8DEC937B67722A7DE508905674DAF6F5F7E99B676B304F0A0FC11966A08213CC"),
            NanoHelper.toByteArray("C211B16511A9B5005CD484C9447DFA001F6F7916958D8C9348A49D124EAD144D8188EF8C5C70B967FF865CAB826EA7CC53E85C6CEA075258D3BDEE7B32EC7709")
        )

        val handshakeExtension2 = HandshakeAnswer(
            node,
            null,
            NanoHelper.toByteArray("64C49362A7B0101F0434EC6AB06C8A28C93DAF9974F32A88405E220894AE2164"),
            NanoHelper.toByteArray("498C9B66C11E5CD9AE492A0DBADC633BE2A8F375E51E51D0A6727B47F17F4E7489545204798D03E6D257A2A94A3C71387EBAE1F48C59D817D3CEDE54298AF600")
        )


        @JvmStatic
        fun encodeProvider(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(handshakeExtension3, encodedExtension3),
                Arguments.of(handshakeExtension2, encodedExtension2)
            )
        }

        @JvmStatic
        fun decodeProvider(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(encodedExtension3, handshakeExtension3),
                Arguments.of(encodedExtension2, handshakeExtension2)
            )
        }
    }
}