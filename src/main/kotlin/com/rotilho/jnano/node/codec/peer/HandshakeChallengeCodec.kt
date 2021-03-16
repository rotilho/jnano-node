package com.rotilho.jnano.node.codec.peer

import com.rotilho.jnano.node.codec.network.TCPCodecSupport
import com.rotilho.jnano.node.peer.handshake.HandshakeChallenge
import com.rotilho.jnano.node.utils.fromLittleEndian
import com.rotilho.jnano.node.utils.toLittleEndian


class HandshakeChallengeCodec(private val nodeCodec: NodeCodec) : TCPCodecSupport<HandshakeChallenge> {
    override fun encode(protocolVersion: Int, o: Any): ByteArray? {
        if (o !is HandshakeChallenge) {
            return null
        }
        val message = ByteArray(34)
        System.arraycopy(toLittleEndian(1), 0, message, 0, 2)
        System.arraycopy(o.challenge, 0, message, 2, 32)
        return message
    }

    override fun decode(protocolVersion: Int, m: ByteArray): HandshakeChallenge? {
        val extensions = fromLittleEndian(m.copyOfRange(6, 8))
        return when (extensions) {
            1 -> {
                val node = nodeCodec.decode(protocolVersion, m) ?: return null
                val challenge = m.copyOfRange(8, 40)
                HandshakeChallenge(node, challenge)
            }
            else -> null
        }
    }

}