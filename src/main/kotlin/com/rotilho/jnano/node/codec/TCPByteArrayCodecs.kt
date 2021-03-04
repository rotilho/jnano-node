package com.rotilho.jnano.node.codec

import com.rotilho.jnano.commons.NanoHelper
import com.rotilho.jnano.node.codec.network.TCPCodec
import mu.KotlinLogging
import org.springframework.stereotype.Component

@Component
class TCPByteArrayCodecs(private val codecs: List<TCPCodec<*>>) : ByteArrayCodecSupport {
    private val logger = KotlinLogging.logger {}

    override fun encode(protocolVersion: Int, o: Any): ByteArray? {
        val encoded = codecs.asSequence()
            .map { it.encode(protocolVersion, o) }
            .filter { it != null }
            .firstOrNull()
        if (encoded == null) {
            logger.error { "Can't encode $o" }
            return null
        }
        return encoded
    }

    override fun decode(protocolVersion: Int, byteArray: ByteArray): Any? {
        try {
            val decoded = codecs.asSequence()
                .map { it.decode(protocolVersion, byteArray) }
                .filter { it != null }
                .firstOrNull()
            if (decoded == null) {
                logger.warn { "Can't find decode to ${NanoHelper.toHex(byteArray)}" }
                return null
            }
            return decoded
        } catch (e: Exception) {
            logger.warn(e) { "Decoding failed to ${NanoHelper.toHex(byteArray)}" }
        }
        return null
    }
}
