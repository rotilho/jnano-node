package com.rotilho.jnano.node.block

import com.rotilho.jnano.commons.NanoAccounts
import com.rotilho.jnano.commons.NanoHelper
import com.rotilho.jnano.commons.NanoSignatures
import com.rotilho.jnano.node.utils.flatMap
import com.rotilho.jnano.node.utils.toLongLittleEndian
import java.util.*

data class VotedBlock(val representativePublicKey: ByteArray,
                      val signature: ByteArray,
                      val sequence: Long,
                      val block: Block) : Block {
    fun isValid(): Boolean {
        return NanoSignatures.isValid(representativePublicKey, flatMap(block.getHash(), toLongLittleEndian(sequence)), signature)
    }

    override fun getType(): BlockType {
        return block.getType()
    }

    override fun getHash(): ByteArray {
        return block.getHash()
    }

    override fun getPrevious(): ByteArray {
        return block.getPrevious()
    }

    override fun getPublicKey(): ByteArray? {
        return block.getPublicKey()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as VotedBlock

        if (!Arrays.equals(representativePublicKey, other.representativePublicKey)) return false
        if (!Arrays.equals(signature, other.signature)) return false
        if (sequence != other.sequence) return false
        if (block != other.block) return false

        return true
    }

    override fun hashCode(): Int {
        var result = Arrays.hashCode(representativePublicKey)
        result = 31 * result + Arrays.hashCode(signature)
        result = 31 * result + sequence.hashCode()
        result = 31 * result + block.hashCode()
        return result
    }

    override fun toString(): String {
        return "VotedBlock(representativePublicKey=${NanoAccounts.createAccount(representativePublicKey)}, signature=${NanoHelper.toHex(signature)}, sequence=$sequence, block=$block)"
    }


}