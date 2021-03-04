package com.rotilho.jnano.node

import com.rotilho.jnano.node.peer.Peer
import java.net.InetSocketAddress

data class Node(
    val socketAddress: InetSocketAddress,
    val environment: Environment,
    val protocolVersion: Int
)