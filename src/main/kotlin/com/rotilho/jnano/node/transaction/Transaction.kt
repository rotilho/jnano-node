package com.rotilho.jnano.node.transaction

import com.rotilho.jnano.commons.NanoHelper
import com.rotilho.jnano.commons.NanoSignatures
import com.rotilho.jnano.commons.NanoWorks
import com.rotilho.jnano.node.block.Block
import com.rotilho.jnano.node.block.BlockType
import com.rotilho.jnano.node.block.VotedBlock
import com.rotilho.jnano.node.utils.isEmpty
import java.util.*


data class Transaction<out T : Block>(
    val block: T,
    val signature: ByteArray,
    val work: ByteArray
) {
    fun isValid(): Boolean {
        val hash = if (isEmpty(block.getPrevious())) block.getPublicKey()!! else block.getPrevious()
        if (!NanoWorks.isValid(hash, if (block.getType() == BlockType.STATE) NanoHelper.reverse(work) else work)) {
            return false;
        } else if (block.getPublicKey() == null) {
            return true;
        }
        return NanoSignatures.isValid(
            block.getPublicKey()!!,
            block.getHash(),
            signature
        ) && if (block is VotedBlock) block.isValid() else true
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Transaction<Block>

        if (block != other.block) return false
        if (!Arrays.equals(signature, other.signature)) return false
        if (!Arrays.equals(work, other.work)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = block.hashCode()
        result = 31 * result + Arrays.hashCode(signature)
        result = 31 * result + Arrays.hashCode(work)
        return result
    }

    override fun toString(): String {
        return "Transaction(block=$block, signature=${NanoHelper.toHex(signature)}, work=${NanoHelper.toHex(work)})"
    }
}
