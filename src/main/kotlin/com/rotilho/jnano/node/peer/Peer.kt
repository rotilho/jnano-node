package com.rotilho.jnano.node.peer

import com.rotilho.jnano.commons.NanoAccounts
import com.rotilho.jnano.node.Event
import com.rotilho.jnano.node.Node
import java.util.*

data class Peer(
    val publicKey: ByteArray,
    val node: Node
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Peer

        if (!Arrays.equals(publicKey, other.publicKey)) return false
        if (node != other.node) return false

        return true
    }

    override fun hashCode(): Int {
        var result = Arrays.hashCode(publicKey)
        result = 31 * result + node.hashCode()
        return result
    }

    override fun toString(): String {
        return "Peer(publicKey=${NanoAccounts.createAccount(publicKey)}, node=$node)"
    }
}

class PeerAddedEvent(peer: Peer) : Event<Peer>(peer)