package com.rotilho.jnano.node

import com.rotilho.jnano.node.Environment
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration


@Configuration
@ConfigurationProperties(prefix = "node")
class NodeProperties {
    var environment: Environment? = null
    var port: Int? = null
}