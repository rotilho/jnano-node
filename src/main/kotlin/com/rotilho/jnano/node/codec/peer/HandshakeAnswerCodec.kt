package com.rotilho.jnano.node.codec.peer

import com.rotilho.jnano.node.codec.network.TCPCodecSupport
import com.rotilho.jnano.node.peer.handshake.HandshakeAnswer
import com.rotilho.jnano.node.peer.handshake.HandshakeChallenge
import com.rotilho.jnano.node.utils.fromLittleEndian
import com.rotilho.jnano.node.utils.toLittleEndian

class HandshakeAnswerCodec(private val nodeCodec: NodeCodec) : TCPCodecSupport<HandshakeAnswer> {
    override fun encode(protocolVersion: Int, o: Any): ByteArray? {
        if (o !is HandshakeAnswer) {
            return null
        }
        if (o.handshakeChallenge == null) {
            val message = ByteArray(98)
            System.arraycopy(toLittleEndian(2), 0, message, 0, 2)
            System.arraycopy(o.publicKey, 0, message, 2, 32)
            System.arraycopy(o.signature, 0, message, 34, 64)
            return message
        }
        val message = ByteArray(130)
        System.arraycopy(toLittleEndian(3), 0, message, 0, 2)
        System.arraycopy(o.handshakeChallenge.challenge, 0, message, 2, 32)
        System.arraycopy(o.publicKey, 0, message, 34, 32)
        System.arraycopy(o.signature, 0, message, 66, 64)
        return message;
    }

    override fun decode(protocolVersion: Int, m: ByteArray): HandshakeAnswer? {
        val extensions = fromLittleEndian(m.copyOfRange(6, 8))
        return when (extensions) {
            2 -> {
                val node = nodeCodec.decode(protocolVersion, m) ?: return null
                val publicKey = m.copyOfRange(8, 40)
                val signature = m.copyOfRange(40, 104)
                HandshakeAnswer(node, null, publicKey, signature);
            }
            3 -> {
                val node = nodeCodec.decode(protocolVersion, m) ?: return null
                val challenge = m.copyOfRange(8, 40)
                val publicKey = m.copyOfRange(40, 72)
                val signature = m.copyOfRange(72, 136)
                HandshakeAnswer(node, HandshakeChallenge(node, challenge), publicKey, signature);
            }
            else -> null
        }
    }

}