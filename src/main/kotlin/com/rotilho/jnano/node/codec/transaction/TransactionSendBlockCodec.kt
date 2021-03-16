package com.rotilho.jnano.node.codec.transaction

import com.rotilho.jnano.commons.NanoAmount
import com.rotilho.jnano.commons.NanoHashes
import com.rotilho.jnano.node.transaction.BlockType
import com.rotilho.jnano.node.transaction.TransactionSendBlock
import com.rotilho.jnano.node.utils.flatMap
import com.rotilho.jnano.node.utils.toShortBigEndian
import kotlin.reflect.KClass

class TransactionSendBlockCodec : TransactionCodec<TransactionSendBlock> {

    override fun getBlockType(): BlockType {
        return BlockType.SEND
    }

    override fun getTransactionClass(): KClass<TransactionSendBlock> {
        return TransactionSendBlock::class
    }

    override fun encodeTransaction(protocolVersion: Int, transaction: TransactionSendBlock): ByteArray {
        return flatMap(
            toShortBigEndian(transaction.getBlockType().code),
            transaction.getPrevious(),
            transaction.getLink(),
            transaction.getBalance().toByteArray(),
            transaction.getSignature(),
            transaction.getWork()
        )
    }


    override fun decodeTransaction(protocolVersion: Int, b: ByteArray): TransactionSendBlock? {
        val hash = NanoHashes.digest256(b.copyOfRange(0, BlockType.SEND.blockSize))
        val previous = b.copyOfRange(0, 32)
        val link = b.copyOfRange(32, 64)
        val balance = NanoAmount.ofByteArray(b.copyOfRange(64, 80))
        val signature = b.copyOfRange(80, 144)
        val work = b.copyOfRange(144, 152)

        val transaction = TransactionSendBlock(
            hash = hash,
            previous = previous,
            balance = balance,
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