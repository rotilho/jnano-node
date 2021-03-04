package com.rotilho.jnano.node.peer.handshake

import com.rotilho.jnano.node.Node

interface Handshake {
    fun getNode(): Node
}