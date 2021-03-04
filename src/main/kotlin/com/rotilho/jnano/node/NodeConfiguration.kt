package com.rotilho.jnano.node

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import java.net.InetAddress
import java.net.InetSocketAddress

@Configuration
@EnableScheduling
class NodeConfiguration {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Bean
    fun node(properties: NodeProperties) : Node {
        val socketAddress = InetSocketAddress(InetAddress.getLocalHost(), properties.port!!)
//        val socketAddress = InetSocketAddress("localhost", properties.port!!)
        val node = Node(socketAddress, properties.environment!!, 18)
        logger.info("Starting $node...")
        return node
    }
}