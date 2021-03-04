package com.rotilho.jnano.node.peer.handshake

import com.rotilho.jnano.commons.NanoSignatures
import com.rotilho.jnano.node.*
import com.rotilho.jnano.node.peer.*
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.launch
import mu.KotlinLogging
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.net.InetAddress
import java.net.InetSocketAddress
import javax.annotation.PostConstruct

@Service
class HandshakeService(
    val node: Node,
    val properties: PeerProperties,
    val handshakeCache: HandshakeCache,
    val peerCache: PeerCache
) {
    private val logger = KotlinLogging.logger {}

    @PostConstruct
    fun initialize() {
        GlobalScope.launch {
            initializeInboundChallengeListener()
            initializeInboundAnswerListener()
            initializeKeepAliveListener()
        }
    }

    private fun initializeInboundChallengeListener() {
        GlobalScope.launch {
            EventBus.listen(InboundEvent::class.java, HandshakeChallenge::class.java)
                .collect { challenge -> processHandshakeChallenge(challenge)}
        }
    }

    private fun initializeInboundAnswerListener() {
        GlobalScope.launch {
            EventBus.listen(InboundEvent::class.java, HandshakeAnswer::class.java)
                .collect { answer -> processHandshakeAnswer(answer)}
        }
    }

    @FlowPreview
    private fun initializeKeepAliveListener() {
        GlobalScope.launch {
            EventBus.listen(InboundEvent::class.java, KeepAlive::class.java)
                .flatMapConcat { keepAlive ->  keepAlive.neighbourNodes.asFlow() }
                .collect { socketAddress -> startHandshake(socketAddress) }
        }
    }


    @Scheduled(fixedRate = 60_000)
    fun defaultHandshake() {
        if (peerCache.count() > properties.defaultNodes!!.size) {
            return
        }

        GlobalScope.launch {
            properties.defaultNodes!!.forEach {
                val address = it.split(":")
                startHandshake(InetSocketAddress(InetAddress.getByName(address[0]), address[1].toInt()))
            }
        }
    }

    suspend fun startHandshake(socketAddress: InetSocketAddress) {
        if (socketAddress == node.socketAddress ||
            handshakeCache.existById(socketAddress) ||
            peerCache.existById(socketAddress)
        ) {
            return
        }

        val handshakeChallenge = createChallenge(socketAddress)

        EventBus.publish(OutboundEvent(socketAddress, handshakeChallenge))

        logger.info { "Started handshake with $socketAddress" }
    }

    suspend fun processHandshakeAnswer(handshakeAnswer: HandshakeAnswer) {
        val node = handshakeAnswer.getNode()

        val handshakeChallenge = handshakeCache.findById(node.socketAddress)
        if (handshakeChallenge == null) {
            logger.warn { "Not requested handshake answer (or too old) was received from $node" }
            return
        }

        if (!NanoSignatures.isValid(handshakeAnswer.publicKey, handshakeChallenge.challenge, handshakeAnswer.signature)) {
            logger.warn { "Invalid handshake answer was received $handshakeAnswer" }
            return
        }

        handshakeCache.removeById(node.socketAddress)

        EventBus.publish(PeerAddedEvent(Peer(handshakeAnswer.publicKey, node)))

        handshakeAnswer.handshakeChallenge?.let {
            processHandshakeChallenge(it, null)
        }
    }

    suspend fun processHandshakeChallenge(handshakeChallenge: HandshakeChallenge) {
        val handshakeChallenge = createChallenge(handshakeChallenge.getNode().socketAddress)

        processHandshakeChallenge(handshakeChallenge, handshakeChallenge)
    }

    suspend fun processHandshakeChallenge(handshakeChallenge: HandshakeChallenge, newHandshakeChallenge: HandshakeChallenge?) {
        val node = handshakeChallenge.getNode()

        val handshakeAnswer = HandshakeAnswer(
            node,
            newHandshakeChallenge,
            KeysProvider.getPublicKey(),
            NanoSignatures.sign(KeysProvider.getPrivateKey(), handshakeChallenge.challenge)
        )

        EventBus.publish(OutboundEvent(node.socketAddress, handshakeAnswer))
    }


    private fun createChallenge(socketAddress: InetSocketAddress): HandshakeChallenge {
        val handshakeChallenge = HandshakeChallenge.create(node)

        handshakeCache.save(socketAddress, handshakeChallenge)

        return handshakeChallenge
    }
}