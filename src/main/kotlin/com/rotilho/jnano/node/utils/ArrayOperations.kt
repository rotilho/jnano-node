package com.rotilho.jnano.node.utils

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