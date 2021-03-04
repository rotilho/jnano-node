package com.rotilho.jnano.node.block

import com.rotilho.jnano.commons.NanoAccounts
import com.rotilho.jnano.commons.NanoAmount
import com.rotilho.jnano.commons.NanoBlocks
import com.rotilho.jnano.commons.NanoHelper
import com.rotilho.jnano.node.utils.isEmpty
import java.util.*

data class StateBlock(private val publicKey: ByteArray,
                      private val previous: ByteArray,
                      val representative: ByteArray,
                      val balance: NanoAmount,
                      val link: ByteArray) : Block {
    override fun getType(): BlockType = BlockType.STATE

    override fun getHash(): ByteArray {
        return NanoBlocks.hashStateBlock(publicKey, previous, representative, balance.toByteArray(), link)
    }

//    override fun getPrevious(): ByteArray = if (isEmpty(previous)) publicKey else previous
    override fun getPrevious(): ByteArray = previous

    override fun getPublicKey(): ByteArray = publicKey

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StateBlock

        if (!Arrays.equals(publicKey, other.publicKey)) return false
        if (!Arrays.equals(previous, other.previous)) return false
        if (!Arrays.equals(representative, other.representative)) return false
        if (balance != other.balance) return false
        if (!Arrays.equals(link, other.link)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = Arrays.hashCode(publicKey)
        result = 31 * result + Arrays.hashCode(previous)
        result = 31 * result + Arrays.hashCode(representative)
        result = 31 * result + balance.hashCode()
        result = 31 * result + Arrays.hashCode(link)
        return result
    }

    override fun toString(): String {
        return "StateBlock(account=${NanoAccounts.createAccount(publicKey)}, previous=${NanoHelper.toHex(previous)}, representative=${NanoAccounts.createAccount(representative)}, balance=$balance, linkHash=${NanoHelper.toHex(link)}, linkAccount=${NanoAccounts.createAccount(link)})"
    }
}
