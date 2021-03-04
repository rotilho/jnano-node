package com.rotilho.jnano.node.codec.block

import com.rotilho.jnano.node.block.BlockType
import com.rotilho.jnano.node.block.ReceiveBlock
import com.rotilho.jnano.node.utils.flatMap
import org.springframework.stereotype.Component

@Component
class ReceiveBlockCodec : BlockCodec {
    override fun getType(): BlockType = BlockType.RECEIVE

    override fun encode(protocolVersion: Int, o: Any): ByteArray? {
        if (o !is ReceiveBlock) {
            return null
        }
        return flatMap(o.getPrevious(), o.source);
    }

    override fun decode(protocolVersion: Int, m: ByteArray): ReceiveBlock? {
        val previous = m.copyOfRange(0, 32)
        val source = m.copyOfRange(32, 64)
        return ReceiveBlock(previous, source);
    }
}