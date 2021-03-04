package com.rotilho.jnano.node.codec.block

import com.rotilho.jnano.node.block.BlockType
import com.rotilho.jnano.node.block.ChangeBlock
import com.rotilho.jnano.node.utils.flatMap
import org.springframework.stereotype.Component

@Component
class ChangeBlockCodec : BlockCodec {
    override fun getType(): BlockType = BlockType.CHANGE

    override fun encode(protocolVersion: Int, o: Any): ByteArray? {
        if (o !is ChangeBlock) {
            return null
        }
        return flatMap(o.getPrevious(), o.representative);
    }

    override fun decode(protocolVersion: Int, m: ByteArray): ChangeBlock? {
        val previous = m.copyOfRange(0, 32)
        val representative = m.copyOfRange(32, 64)
        return ChangeBlock(previous, representative);
    }
}