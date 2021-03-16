package com.rotilho.jnano.node.transaction

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration


@Configuration
@ConfigurationProperties(prefix = "transaction")
class TransactionProperties {
    var entityCacheMaxSize: Long? = null
    var hashCacheExpirationTimeInSeconds: Long? = null
    var entityCacheExpirationTimeInSeconds: Long? = null
}