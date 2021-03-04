package com.rotilho.jnano.node.codec

// TODO
// One possibility here would be use something like ByteArrayView to avoid create multiple ByteArray
interface ByteArrayCodecSupport {

    fun encode(protocolVersion: Int, o: Any): ByteArray?

    fun decode(protocolVersion: Int, m: ByteArray): Any?
}