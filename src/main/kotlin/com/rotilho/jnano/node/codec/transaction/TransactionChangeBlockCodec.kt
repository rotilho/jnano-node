package com.rotilho.jnano.node.codec.transaction

import com.rotilho.jnano.commons.NanoHashes
import com.rotilho.jnano.node.transaction.BlockType
import com.rotilho.jnano.node.transaction.TransactionChangeBlock
import com.rotilho.jnano.node.utils.flatMap
import com.rotilho.jnano.node.utils.toShortBigEndian
import kotlin.reflect.KClass

class TransactionChangeBlockCodec : TransactionCodec<TransactionChangeBlock> {

    override fun getBlockType(): BlockType {
        return BlockType.CHANGE
    }

    override fun getTransactionClass(): KClass<TransactionChangeBlock> {
        return TransactionChangeBlock::class
    }

    override fun encodeTransaction(protocolVersion: Int, transaction: TransactionChangeBlock): ByteArray? {
        return flatMap(
            toShortBigEndian(transaction.getBlockType().code),
            transaction.getPrevious(),
            transaction.getRepresentative(),
            transaction.getSignature(),
            transaction.getWork()
        )
    }

    override fun decodeTransaction(protocolVersion: Int, b: ByteArray): TransactionChangeBlock? {
        val hash = NanoHashes.digest256(b.copyOfRange(0, BlockType.CHANGE.blockSize))
        val previous = b.copyOfRange(0, 32)
        val representative = b.copyOfRange(32, 64)
        val signature = b.copyOfRange(64, 128)
        val work = b.copyOfRange(128, 136)


        val transaction = TransactionChangeBlock(
            hash = hash,
            previous = previous,
            representative = representative,
            signature = signature,
            work = work
        )

        if (transaction.isValid()) {
            return transaction
        }

        return null
    }
}