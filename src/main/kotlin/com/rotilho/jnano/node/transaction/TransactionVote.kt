package com.rotilho.jnano.node.transaction

class TransactionVote(
    val hash: ByteArray,
    val representativePublicKey: ByteArray,
    val signature: ByteArray,
    val sequence: Long
) {

    fun isValid(): Boolean {
        return true
    }
}