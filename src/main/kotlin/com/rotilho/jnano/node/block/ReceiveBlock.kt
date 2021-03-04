package com.rotilho.jnano.node.block

import com.rotilho.jnano.commons.NanoBlocks
import com.rotilho.jnano.commons.NanoHelper
import java.util.*


data class ReceiveBlock(private val previous: ByteArray,
                        val source: ByteArray) : Block {
    override fun getType(): BlockType = BlockType.RECEIVE

    override fun getHash(): ByteArray {
        return NanoBlocks.hashReceiveBlock(previous, source)
    }

    override fun getPrevious(): ByteArray = previous

    override fun getPublicKey(): ByteArray? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ReceiveBlock

        if (!Arrays.equals(previous, other.previous)) return false
        if (!Arrays.equals(source, other.source)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = Arrays.hashCode(previous)
        result = 31 * result + Arrays.hashCode(source)
        return result
    }

    override fun toString(): String {
        return "ReceiveBlock(previous=${NanoHelper.toHex(previous)}, source=${NanoHelper.toHex(source)})"
    }


}
