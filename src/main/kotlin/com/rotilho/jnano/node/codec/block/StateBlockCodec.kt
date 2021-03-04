package com.rotilho.jnano.node.codec.block

import com.rotilho.jnano.commons.NanoAmount
import com.rotilho.jnano.node.block.BlockType
import com.rotilho.jnano.node.block.StateBlock
import com.rotilho.jnano.node.utils.flatMap
import org.springframework.stereotype.Component

@Component
class StateBlockCodec : BlockCodec {
    override fun getType(): BlockType = BlockType.STATE

    override fun encode(protocolVersion: Int, o: Any): ByteArray? {
        if (o !is StateBlock) {
            return null
        }
        return flatMap(o.getPublicKey(), o.getPrevious(), o.representative, o.balance.toByteArray(), o.link);
    }

    override fun decode(protocolVersion: Int, m: ByteArray): StateBlock? {
        val publicKey = m.copyOfRange(0, 32)
        val previous = m.copyOfRange(32, 64)
        val representative = m.copyOfRange(64, 96)
        val balance = m.copyOfRange(96, 112)
        val link = m.copyOfRange(112, 144)
        return StateBlock(publicKey, previous, representative, NanoAmount.ofByteArray(balance), link)
    }
}