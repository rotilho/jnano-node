package com.rotilho.jnano.node.block

import com.rotilho.jnano.commons.NanoAccounts
import com.rotilho.jnano.commons.NanoBlocks
import com.rotilho.jnano.commons.NanoHelper
import java.util.*

data class ChangeBlock(private val previous: ByteArray,
                       val representative: ByteArray) : Block {
    companion object {
        fun decode(message: ByteArray): ChangeBlock {
            val previous = message.copyOfRange(0, 32)
            val publicKey = message.copyOfRange(32, 64)
            return ChangeBlock(previous, publicKey);
        }
    }

    override fun getType(): BlockType = BlockType.CHANGE

    override fun getHash(): ByteArray = NanoBlocks.hashChangeBlock(previous, representative)

    override fun getPrevious(): ByteArray = previous

    override fun getPublicKey(): ByteArray? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ChangeBlock

        if (!Arrays.equals(previous, other.previous)) return false
        if (!Arrays.equals(representative, other.representative)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = Arrays.hashCode(previous)
        result = 31 * result + Arrays.hashCode(representative)
        return result
    }

    override fun toString(): String {
        return "ChangeBlock(previous=${NanoHelper.toHex(previous)}, representative=${NanoAccounts.createAccount(representative)})"
    }
}
