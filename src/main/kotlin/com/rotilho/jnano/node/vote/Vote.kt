package com.rotilho.jnano.node.vote

import com.rotilho.jnano.commons.NanoAccounts
import com.rotilho.jnano.commons.NanoHelper
import com.rotilho.jnano.commons.NanoSignatures
import com.rotilho.jnano.node.utils.flatMap
import com.rotilho.jnano.node.utils.toLongLittleEndian
import java.util.*

data class Vote(val voteType : Short, // TODO: I didn't figure out the types yet
                val representativePublicKey: ByteArray,
                val signature: ByteArray,
                val sequence: Long,
                val hashes: List<ByteArray>) {
    companion object {
        val votePrefix = "vote ".toByteArray()
    }

    fun isValid(): Boolean {
        return NanoSignatures.isValid(representativePublicKey, flatMap(votePrefix, *hashes.toTypedArray(), toLongLittleEndian(sequence)), signature)
    }

    override fun toString(): String {
        return "Vote(voteType=$voteType representativeAccount=${NanoAccounts.createAccount(representativePublicKey)}, signature=${NanoHelper.toHex(signature)}, sequence=$sequence, hashes=${hashes.map { NanoHelper.toHex(it) }})"
    }
}