package com.rotilho.jnano.node.peer.handshake

import com.rotilho.jnano.commons.NanoAccounts
import com.rotilho.jnano.commons.NanoHelper
import com.rotilho.jnano.node.Node
import java.util.*

data class HandshakeAnswer(
    private val node: Node,
    val handshakeChallenge: HandshakeChallenge?,
    val publicKey: ByteArray,
    val signature: ByteArray
) : Handshake {

    override fun getNode(): Node = node

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as HandshakeAnswer

        if (node != other.node) return false
        if (handshakeChallenge != other.handshakeChallenge) return false
        if (!Arrays.equals(publicKey, other.publicKey)) return false
        if (!Arrays.equals(signature, other.signature)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = node.hashCode()
        result = 31 * result + (handshakeChallenge?.hashCode() ?: 0)
        result = 31 * result + Arrays.hashCode(publicKey)
        result = 31 * result + Arrays.hashCode(signature)
        return result
    }

    override fun toString(): String {
        return "HandshakeAnswer(node=$node, handshakeChallenge=$handshakeChallenge, publicKey=${
            NanoAccounts.createAccount(
                publicKey
            )
        }, signature=${NanoHelper.toHex(signature)})"
    }
}