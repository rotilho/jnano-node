package com.rotilho.jnano.node.codec.block

import com.rotilho.jnano.node.block.BlockType
import com.rotilho.jnano.node.block.OpenBlock
import com.rotilho.jnano.node.utils.flatMap
import org.springframework.stereotype.Component

@Component
class OpenBlockCodec : BlockCodec {
    override fun getType(): BlockType = BlockType.OPEN

    override fun encode(protocolVersion: Int, o: Any): ByteArray? {
        if (o !is OpenBlock) {
            return null
        }
        return flatMap(o.source, o.representative, o.getPublicKey());
    }

    override fun decode(protocolVersion: Int, m: ByteArray): OpenBlock? {
        val source = m.copyOfRange(0, 32)
        val representative = m.copyOfRange(32, 64)
        val publicKey = m.copyOfRange(64, 96)
        return OpenBlock(source, representative, publicKey);
    }
}