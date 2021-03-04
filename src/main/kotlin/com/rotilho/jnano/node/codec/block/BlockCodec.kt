package com.rotilho.jnano.node.codec.block

import com.rotilho.jnano.node.block.Block
import com.rotilho.jnano.node.block.BlockType
import com.rotilho.jnano.node.codec.ByteArrayCodecSupport

//TODO make block interface have generics of the block type and force the decode method match the type of getType
interface BlockCodec : ByteArrayCodecSupport {
    fun getType(): BlockType

    fun size(): Int {
        return getType().blockSize
    }

    override fun decode(protocolVersion: Int, m: ByteArray): Block?
}