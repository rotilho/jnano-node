package com.rotilho.jnano.node.peer.handshake

import com.rotilho.jnano.commons.NanoHelper
import com.rotilho.jnano.node.Node
import java.security.SecureRandom
import java.time.LocalDateTime
import java.util.*

data class HandshakeChallenge(
    private val node: Node,
    val challenge: ByteArray,
    val dateTime: LocalDateTime = LocalDateTime.now()
) : Handshake {
    companion object {
        private val random: SecureRandom = SecureRandom.getInstanceStrong()
        fun create(node: Node): HandshakeChallenge {
            val challenge = ByteArray(32)
            random.nextBytes(challenge)
            return HandshakeChallenge(node, challenge)
        }
    }

    override fun getNode(): Node = node

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as HandshakeChallenge

        if (node != other.node) return false
        if (!Arrays.equals(challenge, other.challenge)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = node.hashCode()
        result = 31 * result + Arrays.hashCode(challenge)
        return result
    }

    override fun toString(): String {
        return "HandshakeChallenge(node=$node, challenge=${NanoHelper.toHex(challenge)}, dateTime=$dateTime)"
    }
}