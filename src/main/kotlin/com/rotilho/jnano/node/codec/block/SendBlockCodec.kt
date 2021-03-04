package com.rotilho.jnano.node.codec.block

import com.rotilho.jnano.node.block.BlockType
import com.rotilho.jnano.node.block.SendBlock
import com.rotilho.jnano.node.utils.flatMap
import org.springframework.stereotype.Component

@Component
class SendBlockCodec : BlockCodec {
    override fun getType(): BlockType = BlockType.SEND

    override fun encode(protocolVersion: Int, o: Any): ByteArray? {
        if (o !is SendBlock) {
            return null
        }
        return flatMap(o.getPrevious(), o.destination, o.balance);
    }

    override fun decode(protocolVersion: Int, m: ByteArray): SendBlock? {
        val previous = m.copyOfRange(0, 32)
        val destination = m.copyOfRange(32, 64)
        val balance = m.copyOfRange(64, 80)
        return SendBlock(previous, destination, balance);
    }
}