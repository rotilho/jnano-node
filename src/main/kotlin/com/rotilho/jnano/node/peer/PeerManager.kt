package com.rotilho.jnano.node.peer

import com.rotilho.jnano.node.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import mu.KotlinLogging
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.net.InetSocketAddress
import javax.annotation.PostConstruct

@Service
class PeerManager(val currentNode: Node, val properties: PeerProperties, val cache: PeerCache) : PeerProvider {
    private val logger = KotlinLogging.logger {}


    @PostConstruct
    fun initialize() {
        GlobalScope.launch {
            initializePeerAddedListener()
            initializeKeepAliveListener()
        }
    }

    private fun initializePeerAddedListener() {
        GlobalScope.launch {
            EventBus.listen(PeerAddedEvent::class.java, Peer::class.java)
                .collect {
                    val socketAddress = it.node.socketAddress
                    cache.save(socketAddress, it)
                    val keepAlive = createKeepAlive()
                    EventBus.publish(OutboundEvent(socketAddress, keepAlive))
                }
        }
    }

    private fun initializeKeepAliveListener() {
        GlobalScope.launch {
            EventBus.listen(InboundEvent::class.java, KeepAlive::class.java)
                .collect { keepAlive -> cache.refreshById(keepAlive.node.socketAddress) }
        }
    }

    @Scheduled(fixedRate = 60_000)
    fun sendKeepAlive() {
        val keepAlive = createKeepAlive()
        EventBus.publish(BroadcastEvent(BroadcastStrategy.EVERYONE, keepAlive))
    }

    private fun createKeepAlive(): KeepAlive {
        val sample = cache.findAll().asSequence()
            .map { it.node.socketAddress }
            .shuffled()
            .take(8)
            .toList()
        return KeepAlive(currentNode, sample)
    }

    override fun getPeers(): List<Node> {
        return cache.getNodes()
    }

    override fun getVersion(socketAddress: InetSocketAddress): Int? {
        return cache.findById(socketAddress)?.node?.protocolVersion
    }
}