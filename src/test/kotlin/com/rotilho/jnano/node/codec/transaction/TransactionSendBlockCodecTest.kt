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

internal class TransactionSendBlockCodecTest {
    private val node = Node(InetSocketAddress(InetAddress.getLocalHost(), 7050), Environment.LIVE, 13)
    private val codec = TCPCodec(PacketType.CONFIRM_REQUEST, node, TransactionSendBlockCodec())

    @Test
    fun `Should decode and encode`() {
        val encoded =
            "52430D0D0D040002CAD765107C5CA45D4C217C4CEC68A1E467150D9FE5FBF019A5F755EECE73DA3423049DFE79C922A02F6881FB3B1ECD50BCA52802D54AF3E342F053D60C66DEAB000000000000000000000000000000000F0B52DB54929DB36E958ADC4673D44C22E8605F2F081A149027EED2C88EB42E01F516F233CBC9A03086B953FD6086DCEDEAF891AB4B6A274B86485D985B780B7437E808A3B8AA3E"

        val decoded = codec.decode(node.protocolVersion, NanoHelper.toByteArray(encoded))!! as Transaction

        assertEquals(encoded, NanoHelper.toHex(codec.encode(node.protocolVersion, decoded)!!))
    }

}