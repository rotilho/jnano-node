package com.rotilho.jnano.node.peer.handshake

import com.rotilho.jnano.node.Environment
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import java.net.InetSocketAddress


@Configuration
@ConfigurationProperties(prefix = "handshake")
class HandshakeProperties {
    var expirationTimeInSeconds: Long? = null
}