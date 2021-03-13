package com.rotilho.jnano.node.transaction

class Vote(
    val hash: ByteArray,
    val representativePublicKey: ByteArray,
    val signature: ByteArray,
    val sequence: Long
)