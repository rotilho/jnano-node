package com.rotilho.jnano.node

import java.net.InetSocketAddress

open class Event<T>(val content: T) {
    override fun toString(): String {
        return "Event(content=$content)"
    }
}

class BroadcastEvent<T>(strategy : BroadcastStrategy, content: T) : Event<T>(content) {
    override fun toString(): String {
        return "BroadcastEvent() ${super.toString()}"
    }
}

class InboundEvent<T>(val socketAddress: InetSocketAddress, content: T) : Event<T>(content) {
    override fun toString(): String {
        return "InboundEvent(socketAddress=$socketAddress) ${super.toString()}"
    }
}

class OutboundEvent<T>(val socketAddress: InetSocketAddress, content: T) : Event<T>(content) {
    override fun toString(): String {
        return "OutboundEvent(socketAddress=$socketAddress) ${super.toString()}"
    }
}


enum class BroadcastStrategy(percentage : Int) {
    EVERYONE(100), MAJORITY(50), MINORITY(10)
}