package com.rotilho.jnano.node

import java.net.InetSocketAddress

interface PeerProvider {

    fun getPeers(): Set<Node>

    fun getVersion(socketAddress: InetSocketAddress): Int?

}