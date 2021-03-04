package com.rotilho.jnano.node.network

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import mu.KotlinLogging

object MessageBus {
    private val logger = KotlinLogging.logger {}

    private val bus = MutableSharedFlow<Message>()
    private val messages = bus.asSharedFlow()

    fun publish(message: Message) {
        GlobalScope.launch {
            bus.emit(message)
        }
    }

    fun listenInbound(): Flow<Message> {
        return messages
            .filter { it.javaClass == InboundMessage::class.java }
            .map { it as InboundMessage }
    }


    fun listenOutbound(): Flow<Message> {
        return messages
            .filter { it.javaClass == OutboundMessage::class.java }
            .map { it as OutboundMessage }
    }
}