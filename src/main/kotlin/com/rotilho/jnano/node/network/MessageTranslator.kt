package com.rotilho.jnano.node.network

import com.rotilho.jnano.commons.NanoHelper.toHex
import com.rotilho.jnano.node.*
import com.rotilho.jnano.node.codec.TCPByteArrayCodecs
import com.rotilho.jnano.node.codec.network.PacketType
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.net.InetSocketAddress
import javax.annotation.PostConstruct

@Service
class MessageTranslator(
    val node: Node,
    val peerProvider: PeerProvider,
    val codecs: TCPByteArrayCodecs
) {
    private val logger = KotlinLogging.logger {}

    @PostConstruct
    fun initialize() {
        GlobalScope.launch {
            initializeBroadcastEvent()
            initializeOutboundEvent()
            initializeInboundMessage()
        }
    }

    private suspend fun initializeBroadcastEvent() {
        GlobalScope.launch {
            EventBus.listen(BroadcastEvent::class.java)
                .collect { event ->
                    val encoded = encode(event as Event<Any>)
                    if (encoded != null) {
                        peerProvider.getPeers()
                            .asSequence()
                            .map { node -> OutboundMessage(node.socketAddress, encoded, event) }
                            .forEach { publish(it) }
                    }
                }
        }
    }

    private suspend fun initializeOutboundEvent() {
        GlobalScope.launch {
            EventBus.listen(OutboundEvent::class.java)
                .collect { event ->
                    val encoded = encode(event as Event<Any>)
                    if (encoded != null) {
                        publish(OutboundMessage(event.socketAddress, encoded, event))
                    }
                }
        }
    }

    private suspend fun initializeInboundMessage() {
        GlobalScope.launch {
            MessageBus.listenInbound()
                .collect { message ->
                    val decoded = decode(message.socketAddress, message.content)
                    if (decoded != null) {
                        val event = InboundEvent(message.socketAddress, decoded)
                        EventBus.publish(event)
                    }
                }

        }
    }

    private fun publish(message : OutboundMessage) {
        logger.debug { "Sending ${message.source}" }
        MessageBus.publish(message)
    }

    private fun encode(event: Event<Any>): ByteArray? {
        return codecs.encode(node.protocolVersion, event.content)
    }

    private fun decode(socketAddress: InetSocketAddress, encoded: ByteArray): Any? {
        val protocolVersion = peerProvider.getVersion(socketAddress)

        if (protocolVersion == null && !isPublic(encoded)) {
            logger.warn { "Received non-public message from a unknown peer $socketAddress. Message ${toHex(encoded)}" }
            return null
        }

        ContextHolder.put("socketAddress", socketAddress)

        val decoded = codecs.decode(protocolVersion ?: extractVersion(encoded), encoded)?: return null

        logger.debug { "Decoded message from $socketAddress $decoded ${toHex(encoded)}" }

        return decoded
    }

    /**
     * This is a way to fail fast but I'm not confident this is the right place for this check
     */
    private fun isPublic(encoded: ByteArray): Boolean {
        return encoded.size >= 5 && PacketType.fromCode(encoded[5].toInt())?.public ?: false
    }

    private fun extractVersion(encoded: ByteArray): Int {
        if (encoded.size >= 3) {
            return encoded[3].toInt()
        }
        return 0
    }
}