package com.rotilho.jnano.node.utils

import com.rotilho.jnano.commons.NanoAccounts
import com.rotilho.jnano.commons.NanoHelper
import io.netty.buffer.ByteBuf

fun flatMap(vararg byteArrays: ByteArray): ByteArray {
    val size = byteArrays.asSequence()
        .map { it.size }
        .reduce { a, b -> a + b }

    val finalByteArray = ByteArray(size)

    var index = 0;
    for (byteArray in byteArrays) {
        System.arraycopy(byteArray, 0, finalByteArray, index, byteArray.size)
        index += byteArray.size
    }
    return finalByteArray;
}

fun ByteBuf.toByteArray(): ByteArray {
    if (this.hasArray()) {
        return this.array()
    }

    val bytes = ByteArray(this.readableBytes())
    this.getBytes(this.readerIndex(), bytes)

    return bytes
}

fun ByteArray.toHex(): String {
    return NanoHelper.toHex(this)
}

fun ByteArray.toNullWhenEmpty(): ByteArray? {
    if (this.all { it == 0.toByte() }) {
        return null
    }
    return this
}

fun ByteArray.toAccount(): String {
    return NanoAccounts.createAccount(this)
}