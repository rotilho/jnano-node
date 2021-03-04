package com.rotilho.jnano.node.utils

import org.joou.Unsigned
import java.nio.ByteBuffer
import java.nio.ByteOrder

// Move to jnano-commons?
fun fromLittleEndian(byteArray: ByteArray): Int {
    return Unsigned.ushort(ByteBuffer.wrap(byteArray).order(ByteOrder.LITTLE_ENDIAN).short).toInt()
}

fun fromShortBigEndian(byteArray: ByteArray): Short {
    return Unsigned.ushort(ByteBuffer.wrap(byteArray).order(ByteOrder.BIG_ENDIAN).short).toShort()
}

fun fromShortLittleEndian(byteArray: ByteArray): Short {
    return Unsigned.ushort(ByteBuffer.wrap(byteArray).order(ByteOrder.LITTLE_ENDIAN).short).toShort()
}

fun fromLongBigEndian(byteArray: ByteArray): Long {
    return Unsigned.ulong(ByteBuffer.wrap(byteArray).order(ByteOrder.BIG_ENDIAN).long).toLong()
}

fun fromLongLittleEndian(byteArray: ByteArray): Long {
    return Unsigned.ulong(ByteBuffer.wrap(byteArray).order(ByteOrder.LITTLE_ENDIAN).long).toLong()
}

fun toLittleEndian(value: Int): ByteArray {
    return ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(value.toShort()).array();
}

fun toShortBigEndian(value: Short): ByteArray {
    return ByteBuffer.allocate(2).order(ByteOrder.BIG_ENDIAN).putShort(value).array();
}

fun toLongBigEndian(value: Long): ByteArray {
    return ByteBuffer.allocate(8).order(ByteOrder.BIG_ENDIAN).putLong(value).array();
}

fun toLongLittleEndian(value: Long): ByteArray {
    return ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putLong(value).array();
}

fun isEmpty(byteArray: ByteArray): Boolean {
    return byteArray.all { it.toInt() == 0 }
}