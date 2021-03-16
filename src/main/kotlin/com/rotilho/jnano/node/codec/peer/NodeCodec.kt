package com.rotilho.jnano.node.codec.peer

import com.rotilho.jnano.node.ContextHolder
import com.rotilho.jnano.node.Environment
import com.rotilho.jnano.node.Node
import com.rotilho.jnano.node.codec.network.TCPCodecSupport
import org.springframework.stereotype.Component

import java.net.InetSocketAddress

@Component
class NodeCodec : TCPCodecSupport<Node> {
    override fun encode(protocolVersion: Int, o: Any): ByteArray? {
        throw UnsupportedOperationException("Node shouldn't be encoded")
    }

    override fun decode(protocolVersion: Int, m: ByteArray): Node? {
        val socketAddress = ContextHolder.get<InetSocketAddress>("socketAddress")!!
        val environment = Environment.fromCode(String(m.copyOfRange(0, 2))) ?: return null
        val newProtocolVersion = m[3].toInt()
        return Node(socketAddress, environment, newProtocolVersion)
    }

}