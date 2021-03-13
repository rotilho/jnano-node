package com.rotilho.jnano.node.transaction

import com.rotilho.jnano.node.Environment
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import java.net.InetSocketAddress


@Configuration
@ConfigurationProperties(prefix = "transaction")
class TransactionProperties {
    var cacheExpirationTimeInSeconds: Long? = null
    var hashCacheExpirationTimeInSeconds: Long? = null
}