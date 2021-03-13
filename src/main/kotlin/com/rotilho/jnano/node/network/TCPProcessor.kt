package com.rotilho.jnano.node.network

import com.rotilho.jnano.commons.NanoHelper
import com.rotilho.jnano.node.Node
import com.rotilho.jnano.node.utils.toByteArray
import com.rotilho.jnano.node.utils.toHex
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import mu.KotlinLogging
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.netty.Connection
import reactor.netty.DisposableServer
import reactor.netty.tcp.TcpClient
import reactor.netty.tcp.TcpServer
import java.net.InetSocketAddress
import java.util.concurrent.ConcurrentHashMap
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy


@Service
class TCPProcessor(val node: Node) {
    private val logger = KotlinLogging.logger {}
    private val networkCode = node.environment.code.encodeToByteArray()

    private val connections = ConcurrentHashMap<InetSocketAddress, Connection>()

    private var disposableServer: DisposableServer? = null

    @PostConstruct
    fun initialize() {
        GlobalScope.launch {
            initializeServer()
            initializeOutboundMessage()
        }
    }

    private suspend fun initializeServer() {
        disposableServer = TcpServer.create()
            .host(node.socketAddress.hostName)
            .port(node.socketAddress.port)
            .doOnBind { logger.info { "TCP started on ${node.socketAddress}" } }
            .doOnConnection {
                val socketAddress = (it.channel().remoteAddress() as InetSocketAddress)
                logger.info { "New connection as a server $socketAddress" }
                updateConnection(socketAddress, it)
            }
            .bindNow()
    }

    private suspend fun initializeOutboundMessage() {
        GlobalScope.launch {
            MessageBus.listenOutbound()
                .collect {
                    GlobalScope.launch {
                        try {
                            val connection = getConnection(it.socketAddress)
                            send(it.socketAddress, connection, it)
                        } catch (e: Exception) {
                            logger.warn(e) { "It couldn't send $it" }
                        }
                    }
                }
        }
    }

    private suspend fun getConnection(socketAddress: InetSocketAddress): Connection {
        logger.info { "New connection as a client $socketAddress" }
        if (connections[socketAddress] != null) return connections[socketAddress]!!

        val connection = TcpClient.create()
            .host(socketAddress.hostName)
            .port(socketAddress.port)
            .connectNow()

        return updateConnection(socketAddress, connection)
    }

    private fun updateConnection(socketAddress: InetSocketAddress, connection: Connection): Connection {
        logger.info { "Connected to $socketAddress" }
        return connections.compute(socketAddress) { k, v ->
            v?.dispose()
            connection.inbound()
                .receive()
                .map { it.toByteArray() }
                .filter { isSameNetwork(socketAddress, connection, it) }
                .map { InboundMessage(socketAddress, it) }
                .doOnNext { logger.debug("Received from $socketAddress ${NanoHelper.toHex(it.content)}") }
                .subscribe { MessageBus.publish(it) }
            connection
        }!!
    }

    //TODO: change sending method to use a Flow of bytearray instead of single bytearray
    private suspend fun send(socketAddress: InetSocketAddress, connection: Connection, message: Message) {
        logger.debug { "Sending to $socketAddress ${NanoHelper.toHex(message.content)}" }
        connection.outbound()
            .sendByteArray(Mono.just(message.content))
            .then()
            .onErrorResume { t ->
                logger.info(t) { "Failed to send $message" }
                connections.remove(socketAddress)
                Mono.empty()
            }
            .block()
    }

    private fun isSameNetwork(socketAddress: InetSocketAddress, connection: Connection, byteArray: ByteArray): Boolean {
        if (byteArray.size >= 2 && networkCode[0] == byteArray[0] && networkCode[1] == byteArray[1]) {
            return true
        }
        logger.trace { "Received invalid message from $socketAddress ${byteArray.toHex()}" }
        connection.dispose()
        return false
    }


    @PreDestroy
    fun preDestroy() {
        disposableServer?.disposeNow()
    }
}