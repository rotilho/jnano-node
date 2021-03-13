package com.rotilho.jnano.node.codec.transaction

import com.rotilho.jnano.commons.NanoHelper
import com.rotilho.jnano.node.Environment
import com.rotilho.jnano.node.Node
import com.rotilho.jnano.node.codec.network.PacketType
import com.rotilho.jnano.node.codec.network.TCPCodec
import com.rotilho.jnano.node.transaction.Transaction
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.net.InetAddress
import java.net.InetSocketAddress

internal class TransactionReceiveBlockCodecTest {
    private val node = Node(InetSocketAddress(InetAddress.getLocalHost(), 7050), Environment.LIVE, 13)
    private val codec = TCPCodec(PacketType.CONFIRM_REQUEST, node, TransactionReceiveBlockCodec())

    @Test
    fun `Should decode and encode`() {
        val encoded =
            "52430D0D0D040003B0785957D427FC0D920B1FD10CDE669D8CC3F439006E0C6156C14042A816383D30F6492D09074C1CFAD7382B39964A0EE75F65AAA415AC5E4A3678142AC1EFC7F19FE5F969640F204D64B80EA996EA133F7EA5669BF5859B3848162E90DB133F5264CC21E5C5622BE54E564CCCBEF6D16ECEB230D6726A597F0CD93A2031BA080C181E54BF343D41"

        val decoded = codec.decode(node.protocolVersion, NanoHelper.toByteArray(encoded))!! as Transaction

        assertEquals(encoded, NanoHelper.toHex(codec.encode(node.protocolVersion, decoded)!!))
    }

}