package com.rotilho.jnano.node.network

import com.rotilho.jnano.commons.NanoHelper
import java.net.InetSocketAddress

open class Message(val socketAddress: InetSocketAddress, val content: ByteArray) {
    override fun toString(): String {
        return "Message(socketAddress=$socketAddress, content=${NanoHelper.toHex(content)})"
    }
}

class InboundMessage(socketAddress: InetSocketAddress, content: ByteArray) : Message(socketAddress, content) {
    override fun toString(): String {
        return "InboundMessage() ${super.toString()}"
    }
}

class OutboundMessage(socketAddress: InetSocketAddress, content: ByteArray, val source: Any) :
    Message(socketAddress, content) {
    override fun toString(): String {
        return "OutboundMessage(source=$source) ${super.toString()}"
    }
}