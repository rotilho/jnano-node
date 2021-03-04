package com.rotilho.jnano.node.codec.network

import com.rotilho.jnano.node.Environment
import com.rotilho.jnano.node.Node
import com.rotilho.jnano.node.codec.ByteArrayCodecSupport
import com.rotilho.jnano.node.utils.flatMap
import java.lang.Math.min

class TCPCodec<T : ByteArrayCodecSupport>(
    private val type: PacketType,
    private val node: Node,
    private val codec: T
) : ByteArrayCodecSupport {
    override fun encode(protocolVersion: Int, o: Any): ByteArray? {
        val body = codec.encode(protocolVersion, o) ?: return null;

        val message = ByteArray(6)

        System.arraycopy(node.environment.code.toByteArray(), 0, message, 0, 2)

        val currentProtocolVersion = node.protocolVersion.toByte()
        val minProtocolVersion = min(protocolVersion, node.protocolVersion).toByte();
        message[2] = currentProtocolVersion
        message[3] = currentProtocolVersion
        message[4] = minProtocolVersion
        message[5] = type.code.toByte()

        return flatMap(message, body);
    }

    // TODO: Check message size before try to deserialize. This will reduce the impact of DDoS, failing fast
    // TODO: One thing that should be considered is the protocol flexibility vs security impact. Have flexible message size could help with protocol evolution
    // TODO: BUT since we already support conditional deserialization / serialization we probably already have good things from both side. Let's see
    override fun decode(protocolVersion: Int, m: ByteArray): Any? {
        if (Environment.fromCode(String(m.copyOfRange(0, 2))) != node.environment) {
            return null
        }
        if (PacketType.fromCode(m[5].toInt()) != type) {
            return null
        }
        if (codec is TCPCodecSupport) {
            return codec.decode(protocolVersion, m);
        }
        return codec.decode(protocolVersion, m.copyOfRange(6, m.size));
    }
}

enum class PacketType(val code: Int, val public: Boolean) {
    KEEP_ALIVE(2, false),
    PUBLISH(3, false),
    CONFIRM_REQUEST(4, false),
    CONFIRM_ACKNOWLEDGE(5, false),
    BULK_PULL(
        6,
        false
    ), //Request pk Wallet.PublicKey, end Block.BlockHash, Response txs []Block.Transaction https://github.com/brokenbydefault/Nanollet/blob/9a6082c76d855ef0e9be2502e11ebe0416c87f0f/Node/Packets/bulkpull.go
    BULK_PUSH(7, false), //needed?
    FRONTIER_REQUEST(8, false), //needed?
    HANDSHAKE(10, true),
    BULK_PULL_ACCOUNT(
        11,
        false
    ); //Request pk Wallet.PublicKey, minAmount *Numbers.RawAmount, Response frontier Block.BlockHash, balance *Numbers.RawAmount, pending []Block.BlockHash https://github.com/brokenbydefault/Nanollet/blob/9a6082c76d855ef0e9be2502e11ebe0416c87f0f/Node/Packets/bulkpullaccount.go

    companion object {
        private val CODE_MAP = values().associateBy(PacketType::code)
        fun fromCode(code: Int): PacketType? {
            return CODE_MAP[code]
        }
    }
}