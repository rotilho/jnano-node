package com.rotilho.jnano.node.peer

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import com.rotilho.jnano.node.Node
import org.springframework.stereotype.Component
import java.net.InetSocketAddress
import java.util.concurrent.TimeUnit

@Component
class PeerCache(properties: PeerProperties) {
    private val cache: Cache<InetSocketAddress, Peer> = CacheBuilder.newBuilder()
        .expireAfterWrite(properties.expirationTimeInSeconds!!, TimeUnit.SECONDS)
        .build()

    fun save(socketAddress: InetSocketAddress, peer: Peer) {
        cache.put(socketAddress, peer)
    }

    fun count(): Long {
        return cache.size()
    }

    fun findAll(): Set<Peer> {
        return cache.asMap().values.toSet()
    }

    fun findById(socketAddress: InetSocketAddress): Peer? {
        return cache.getIfPresent(socketAddress)
    }

    fun existById(socketAddress: InetSocketAddress): Boolean {
        return cache.asMap().containsKey(socketAddress)
    }

    fun refreshById(socketAddress: InetSocketAddress): Boolean {
        val peer = findById(socketAddress) ?: return false
        save(socketAddress, peer)
        return true
    }

    fun getNodes(): List<Node> {
        return cache.asMap().values.asSequence()
            .map { it.node }
            .toList()
    }

}