package com.rotilho.jnano.node.codec.transaction

import com.rotilho.jnano.commons.NanoHashes
import com.rotilho.jnano.node.transaction.BlockType
import com.rotilho.jnano.node.transaction.TransactionOpenBlock
import com.rotilho.jnano.node.utils.flatMap
import com.rotilho.jnano.node.utils.toShortBigEndian
import kotlin.reflect.KClass

class TransactionOpenBlockCodec : TransactionCodec<TransactionOpenBlock> {

    override fun getBlockType(): BlockType {
        return BlockType.OPEN
    }

    override fun getTransactionClass(): KClass<TransactionOpenBlock> {
        return TransactionOpenBlock::class
    }

    override fun encodeTransaction(protocolVersion: Int, transaction: TransactionOpenBlock): ByteArray {
        return flatMap(
            toShortBigEndian(transaction.getBlockType().code),
            transaction.getLink(),
            transaction.getRepresentative(),
            transaction.getPublicKey(),
            transaction.getSignature(),
            transaction.getWork()
        )
    }


    override fun decodeTransaction(protocolVersion: Int, b: ByteArray): TransactionOpenBlock? {
        val hash = NanoHashes.digest256(b.copyOfRange(0, BlockType.OPEN.blockSize))
        val link = b.copyOfRange(0, 32)
        val representative = b.copyOfRange(32, 64)
        val publicKey = b.copyOfRange(64, 96)
        val signature = b.copyOfRange(96, 160)
        val work = b.copyOfRange(160, 168)


        val transaction = TransactionOpenBlock(
            hash = hash,
            publicKey = publicKey,
            representative = representative,
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