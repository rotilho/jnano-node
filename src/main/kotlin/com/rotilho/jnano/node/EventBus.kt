package com.rotilho.jnano.node

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import mu.KotlinLogging

object EventBus {
    private val logger = KotlinLogging.logger {}

    private val bus = MutableSharedFlow<Event<*>>()
    private val events = bus.asSharedFlow()

    fun <T> publish(event: Event<T>) {
        GlobalScope.launch {
            bus.emit(event)
        }
    }

    fun listen(): Flow<Event<*>> {
        return events
    }

    fun <E : Event<*>> listen(eventType: Class<E>): Flow<E> {
        return listen()
            .filter { eventType.isAssignableFrom(it.javaClass) }
            .map { eventType.cast(it) }
    }

    fun <T, E : Event<*>> listen(eventType: Class<E>, contentType: Class<T>): Flow<T> {
        return listen(eventType)
            .map { it.content!! }
            .filter { contentType.isAssignableFrom(it.javaClass) }
            .map { contentType.cast(it) }
    }

}