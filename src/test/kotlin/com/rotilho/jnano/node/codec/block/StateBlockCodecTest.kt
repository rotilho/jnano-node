package com.rotilho.jnano.node.codec.block

import com.rotilho.jnano.commons.NanoHelper
import com.rotilho.jnano.node.Environment
import com.rotilho.jnano.node.Node
import com.rotilho.jnano.node.codec.network.PacketType
import com.rotilho.jnano.node.codec.network.TCPCodec
import com.rotilho.jnano.node.codec.transaction.TransactionCodec
import com.rotilho.jnano.node.transaction.Transaction
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.net.InetAddress
import java.net.InetSocketAddress
import java.util.stream.Stream

internal class StateBlockCodecTest {
    private val node = Node(InetSocketAddress(InetAddress.getLocalHost(), 7050), Environment.LIVE, 13)


    @Test
    fun `Should decode and encode request acknowledge`() {
        val codec = TCPCodec(PacketType.CONFIRM_ACKNOWLEDGE, node, TransactionCodec(VotedBlockCodec(StateBlockCodec())))
        val encoded =
            "52430D0D0D0500112298FAB7C61058E77EA554CB93EDEEDA0692CBFCC540AB213B2836B29029E23A2D1F34E9EEE8622B60CEC45603132321BE746FC9F30017F645A3EBCAF655A9BA3079D4CCEA1962A3EBE02FEC128FFA6F8A5DA7484CDD5DD4AA30BC4DD4C36304A3958CE90100000007DC8DD2DD828A3BF8BE3F935BB3F29DDCE44EA0DECE488B07A41536E042EB785243121212050011D9C693F86764E2502ECB9CA2050E7DA71BD1B310D0F2B53509831308DE2E2FF047415F2BCBD09B6D2C9F9E5BC32F9533DE8383F0BA629D763AD1FC49306EA50F791AB10DA42DC562A057C82D0EFDDF22095D756886D48A042EAF142BAA10FF0899220C0D000000002B8555FB72EA3502F776AF412A535A7979C4857CE024B76D07215B5DE411547252431212120500113464CF127BEADE0FABD86557394E7A330C90061E083E8A8E8D550C7058702947C8F3845DB66B64D0428DAF8B12C570EBBF85EECECAF0C820"

        val decoded = codec.decode(node.protocolVersion, NanoHelper.toByteArray(encoded))!! as Transaction<*>

        assertTrue(decoded.isValid())
        assertEquals(encoded, NanoHelper.toHex(codec.encode(node.protocolVersion, decoded)!!))
    }


    @ParameterizedTest
    @MethodSource("providers")
    fun `Should decode and encode`(type: PacketType, encoded: String) {
        val codec = TCPCodec(type, node, TransactionCodec(StateBlockCodec()))

        val decoded = codec.decode(node.protocolVersion, NanoHelper.toByteArray(encoded))!! as Transaction<*>

        assertTrue(decoded.isValid())
        assertEquals(encoded, NanoHelper.toHex(codec.encode(node.protocolVersion, decoded)!!))
    }

    companion object {
        @JvmStatic
        fun providers(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    PacketType.PUBLISH,
                    "52430D0D0D0300066FBB74B417E299DCE5CA0FF8EB1C128F08B1CC61CE4827798B2460444CC5ED9500000000000000000000000000000000000000000000000000000000000000002623534EF93A4ACDA7400D68F195EA05FE42A1B6A528162F34D2534AED15CEA700000000A18F07D736B90BE550000000EB774EBD36A252C0AC223AB73D739605D3B8A8FF4E1730B3C6800A5EDE32E30EAB98B074044C947CA649331C2E1147A9268DCF57586FA2AE50D5C06BD394D5907025992A97394FD20622B50340EAA635BC8065BCE136485103B1B4FC31775B0A1DFF9D96A26C5A95"
                ),
                Arguments.of(
                    PacketType.CONFIRM_REQUEST,
                    "52430D0D0D04000634AE2CF65E1847FF1076C0868F93C5B4AA8FE54E9067716B8DEE3EA7EDCF3EBA2112E28DB54F73BD65526158F3407A9F6740FA132C6E9CC62EFF078CFDF8E76B1243B4275D7CFF63E3229C7C40AEAE8C53D6F3233D439AF3177865751E9885F100000001431E0FAE6D7217CAA000000000000000000000000000000000000000000000000000000000000000000000002406004BDD8F0326A43AFE2ECD4B7773AE219912E1B03567D40C66EE282B08C65BFA1289F4413C189B4BCE41352A81AE9189500943AF3D2A5CB8A424F2CA8508A377CF3181B9E33B"
                )
            )
        }
    }
}