package com.rotilho.jnano.node.peer

import com.rotilho.jnano.node.Environment
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import java.net.InetSocketAddress


@Configuration
@ConfigurationProperties(prefix = "peer")
class PeerProperties {
    var expirationTimeInSeconds: Long? = null
    var defaultNodes: Set<String>? = null
}