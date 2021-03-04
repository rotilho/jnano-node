package com.rotilho.jnano.node.block

import com.rotilho.jnano.commons.NanoAccounts
import com.rotilho.jnano.commons.NanoBlocks
import com.rotilho.jnano.commons.NanoHelper
import java.util.*


data class SendBlock(private val previous: ByteArray,
                      val destination: ByteArray,
                      val balance: ByteArray) : Block {

    override fun getType(): BlockType = BlockType.SEND

    override fun getHash(): ByteArray = NanoBlocks.hashSendBlock(previous, destination, balance)

    override fun getPrevious(): ByteArray = previous

    override fun getPublicKey(): ByteArray? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SendBlock

        if (!Arrays.equals(previous, other.previous)) return false
        if (!Arrays.equals(destination, other.destination)) return false
        if (!Arrays.equals(balance, other.balance)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = Arrays.hashCode(previous)
        result = 31 * result + Arrays.hashCode(destination)
        result = 31 * result + Arrays.hashCode(balance)
        return result
    }

    override fun toString(): String {
        return "SendBlock(previous=${NanoHelper.toHex(previous)}, destination=${NanoAccounts.createAccount(destination)}, balance=${NanoHelper.toBigInteger(balance)})"
    }


}
