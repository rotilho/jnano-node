package com.rotilho.jnano.node.peer.handshake

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import org.springframework.stereotype.Component
import java.net.InetSocketAddress
import java.util.concurrent.TimeUnit

@Component
class HandshakeCache(properties: HandshakeProperties) {
    var cache: Cache<InetSocketAddress, HandshakeChallenge> = CacheBuilder.newBuilder()
        .expireAfterWrite(properties.expirationTimeInSeconds!!, TimeUnit.SECONDS)
        .build()

    fun save(socketAddress: InetSocketAddress, challenge: HandshakeChallenge) {
        cache.put(socketAddress, challenge)
    }

    fun count(): Long {
        return cache.size()
    }

    fun findAll(): Set<HandshakeChallenge> {
        return cache.asMap().values.toSet()
    }

    fun findById(inetSocketAddress: InetSocketAddress): HandshakeChallenge? {
        return cache.getIfPresent(inetSocketAddress)
    }

    fun existById(inetSocketAddress: InetSocketAddress): Boolean {
        return cache.asMap().containsKey(inetSocketAddress)
    }

    fun removeById(inetSocketAddress: InetSocketAddress) {
        cache.invalidate(inetSocketAddress)
    }

}