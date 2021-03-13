package com.rotilho.jnano.node

import java.net.InetSocketAddress

interface PeerProvider {

    fun getPeers(): List<Node>

    fun getVersion(socketAddress: InetSocketAddress): Int?

}