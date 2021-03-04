package com.rotilho.jnano.node.block

import com.rotilho.jnano.commons.NanoAccounts
import com.rotilho.jnano.commons.NanoBlocks
import com.rotilho.jnano.commons.NanoHelper
import java.util.*

data class OpenBlock(val source: ByteArray,
                     val representative: ByteArray,
                     private val account: ByteArray) : Block {
    companion object {
        fun decode(message: ByteArray): OpenBlock {
            val source = message.copyOfRange(0, 32)
            val representative = message.copyOfRange(32, 64)
            val publicKey = message.copyOfRange(64, 96)
            return OpenBlock(source, representative, publicKey);
        }
    }

    override fun getType(): BlockType = BlockType.OPEN;

    override fun getHash(): ByteArray {
        return NanoBlocks.hashOpenBlock(source, representative, account)
    }

    override fun getPrevious(): ByteArray = account

    override fun getPublicKey(): ByteArray = account

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as OpenBlock

        if (!Arrays.equals(source, other.source)) return false
        if (!Arrays.equals(representative, other.representative)) return false
        if (!Arrays.equals(account, other.account)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = Arrays.hashCode(source)
        result = 31 * result + Arrays.hashCode(representative)
        result = 31 * result + Arrays.hashCode(account)
        return result
    }

    override fun toString(): String {
        return "OpenBlock(source=${NanoHelper.toHex(source)}, representative=${NanoAccounts.createAccount(representative)}, account=${NanoAccounts.createAccount(account)})"
    }
}
