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

internal class TransactionChangeBlockCodecTest {
    private val node = Node(InetSocketAddress(InetAddress.getLocalHost(), 7050), Environment.LIVE, 13)
    private val codec = TCPCodec(PacketType.CONFIRM_REQUEST, node, TransactionChangeBlockCodec())

    @Test
    fun `Should decode and encode`() {
        val encoded =
            "52430D0D0D0400058F14F54A6A4EA572B3BDF11807B02B2889C22D86CF175F12F3E7A197E7B105D91793E59C41D19B79B66134E76129D53446FD3794882563788437482E356F0A8767DC1E1EA840AE0EC5AA8D2E769E17A00F22DA7E43411180088BEC4BA46989CA723C29471158E8112725D5120DF7E0D80BFF2A11E66E9AE4086FCAD244B2170E2DB272F171D5B286"

        val decoded = codec.decode(node.protocolVersion, NanoHelper.toByteArray(encoded))!! as Transaction

        assertEquals(encoded, NanoHelper.toHex(codec.encode(node.protocolVersion, decoded)!!))
    }

}