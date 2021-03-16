package com.rotilho.jnano.node.codec.transaction

import com.rotilho.jnano.commons.NanoHashes
import com.rotilho.jnano.node.transaction.BlockType
import com.rotilho.jnano.node.transaction.TransactionReceiveBlock
import com.rotilho.jnano.node.utils.flatMap
import com.rotilho.jnano.node.utils.toShortBigEndian
import kotlin.reflect.KClass

class TransactionReceiveBlockCodec : TransactionCodec<TransactionReceiveBlock> {

    override fun getBlockType(): BlockType {
        return BlockType.RECEIVE
    }

    override fun getTransactionClass(): KClass<TransactionReceiveBlock> {
        return TransactionReceiveBlock::class
    }

    override fun encodeTransaction(protocolVersion: Int, transaction: TransactionReceiveBlock): ByteArray? {
        return flatMap(
            toShortBigEndian(transaction.getBlockType().code),
            transaction.getPrevious(),
            transaction.getLink(),
            transaction.getSignature(),
            transaction.getWork()
        )
    }

    override fun decodeTransaction(protocolVersion: Int, b: ByteArray): TransactionReceiveBlock? {
        val hash = NanoHashes.digest256(b.copyOfRange(0, BlockType.RECEIVE.blockSize))
        val previous = b.copyOfRange(0, 32)
        val link = b.copyOfRange(32, 64)
        val signature = b.copyOfRange(64, 128)
        val work = b.copyOfRange(128, 136)

        val transaction = TransactionReceiveBlock(
            hash = hash,
            previous = previous,
            link = link,
            signature = signature,
            work = work
        )

        if (transaction.isValid()) {
            return transaction
        }

        return null
    }

}