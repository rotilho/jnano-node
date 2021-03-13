package com.rotilho.jnano.node.codec.transaction

import com.rotilho.jnano.commons.NanoHelper
import com.rotilho.jnano.node.Environment
import com.rotilho.jnano.node.Node
import com.rotilho.jnano.node.codec.network.PacketType
import com.rotilho.jnano.node.codec.network.TCPCodec
import com.rotilho.jnano.node.transaction.Transaction
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.net.InetAddress
import java.net.InetSocketAddress
import java.util.stream.Stream

internal class TransactionOpenBlockCodecTest {
    private val node = Node(InetSocketAddress(InetAddress.getLocalHost(), 7050), Environment.LIVE, 13)

    @ParameterizedTest
    @MethodSource("providers")
    fun `Should decode and encode`(type: PacketType, transactionCodec: TransactionCodec, encoded: String) {
        val codec = TCPCodec(type, node, transactionCodec)

        val decoded = codec.decode(node.protocolVersion, NanoHelper.toByteArray(encoded))!! as Transaction

        assertEquals(encoded, NanoHelper.toHex(codec.encode(node.protocolVersion, decoded)!!))
    }

    companion object {
        @JvmStatic
        fun providers(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    PacketType.CONFIRM_REQUEST,
                    TransactionOpenBlockCodec(),
                    "52430D0D0D040004FC27DAC3E3829B6D8706E9B0FB05A45844699783CD061D406DFE320B3A322867D95FEEEB8B08DA598821A72199141ED75D5860BCCB0CA4E041E1387207F9C993FF55E8960BFE4B805E031FD66FB2CA807891065D94CFF57E8E5B1B77E1F7C4EA93DD454C583CB348FBFFA5F463888CE8223AF707B4E4A3E35594AB1021589E1AD4B998E987E30BA3EA3017CF45D1EE3CDFB7385BDDFBF679DB376FEB47D5C804765C813011FE93A4"
                ),
                Arguments.of(
                    PacketType.CONFIRM_ACKNOWLEDGE,
                    VotedTransactionCodec(TransactionOpenBlockCodec()),
                    "52430D0D0D0500046A0E062152C332646EEF111628F109A02369E6CEC51F5DEF7DE9F3F6C46000D19C32796F587851A7174612E326590D3E907F72763D572A88589ADA4BFC30BDFD6EC80965B51E5BE50C2E5720697EF82D825E6190570521CF22B6915D74645B07DD9F888F00000000D16DFB74935160020AEA9F6B5FBAEA7CFD41D8703B597264E68A8424F36339872399A083C600AA0572F5E36247D978FCFC840405F8D4B6D33161C0066A55F4316B933E9E8ED9ACF8C48D786A21A45F84268CFB1DB268BD26761BE144DE245051C53D19EB1B19E3ED70A1183D0559E19609C197C9ED2750A57611B307ECC4A6ED4F10B2490E6D7F27B22045FFA2187673D2546BE46D17A62A51A8CFC038180A0402C51CC268B94137"
                )
            )
        }
    }

}