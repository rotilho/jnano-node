package com.rotilho.jnano.node.peer

import com.rotilho.jnano.node.Node
import java.net.InetSocketAddress

data class KeepAlive(val node: Node, val neighbourNodes: List<InetSocketAddress>)