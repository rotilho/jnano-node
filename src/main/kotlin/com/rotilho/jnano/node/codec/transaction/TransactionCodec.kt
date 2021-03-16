package com.rotilho.jnano.node.codec.transaction

import com.rotilho.jnano.node.codec.ByteArrayCodecSupport
import com.rotilho.jnano.node.transaction.BlockType
import com.rotilho.jnano.node.transaction.Transaction
import kotlin.reflect.KClass

interface TransactionCodec<T : Transaction> : ByteArrayCodecSupport<T> {
    private val transactionPrefixSize: Int
        get() = 2
    private val transactionSize: Int
        get() = 72

    fun getBlockType(): BlockType
    fun getTransactionClass(): KClass<T>

    override fun encode(protocolVersion: Int, o: Any): ByteArray? {
        if (o::class != getTransactionClass()) return null

        return encodeTransaction(protocolVersion, o as T)
    }

    fun encodeTransaction(protocolVersion: Int, transaction: T): ByteArray?

    override fun decode(protocolVersion: Int, m: ByteArray): T? {
        if (!shouldDecode(getBlockType(), m)) {
            return null
        }
        return decodeTransaction(protocolVersion, m.copyOfRange(2, m.size))
    }

    fun decodeTransaction(protocolVersion: Int, m: ByteArray): T?

    fun shouldDecode(blockType: BlockType, m: ByteArray): Boolean {
        if (m.size < blockType.blockSize + transactionPrefixSize + transactionSize) {
            return false
        }

        if (!m.copyOfRange(0, transactionPrefixSize).contentEquals(blockType.byteArray)) {
            return false
        }

        return true
    }
}