package com.rotilho.jnano.node.codec.transaction

import com.rotilho.jnano.commons.NanoAmount
import com.rotilho.jnano.commons.NanoHashes
import com.rotilho.jnano.commons.NanoHelper.toByteArray
import com.rotilho.jnano.node.transaction.BlockType
import com.rotilho.jnano.node.transaction.TransactionStateBlock
import com.rotilho.jnano.node.utils.flatMap
import com.rotilho.jnano.node.utils.toNullWhenEmpty
import com.rotilho.jnano.node.utils.toShortBigEndian
import kotlin.reflect.KClass

class TransactionStateBlockCodec : TransactionCodec<TransactionStateBlock> {
    private val preamble = toByteArray("0000000000000000000000000000000000000000000000000000000000000006")

    private val zeros = ByteArray(32)


    override fun getBlockType(): BlockType {
        return BlockType.STATE
    }

    override fun getTransactionClass(): KClass<TransactionStateBlock> {
        return TransactionStateBlock::class
    }

    override fun encodeTransaction(protocolVersion: Int, transaction: TransactionStateBlock): ByteArray {
        return flatMap(
            toShortBigEndian(transaction.getBlockType().code),
            transaction.getPublicKey(),
            transaction.getPrevious() ?: zeros,
            transaction.getRepresentative(),
            transaction.getBalance().toByteArray(),
            transaction.getLink() ?: zeros,
            transaction.getSignature(),
            transaction.getWork()
        )
    }

    override fun decodeTransaction(protocolVersion: Int, b: ByteArray): TransactionStateBlock? {
        val hash = NanoHashes.digest256(preamble, b.copyOfRange(0, BlockType.STATE.blockSize))
        val publicKey = b.copyOfRange(0, 32)
        val previous = b.copyOfRange(32, 64).toNullWhenEmpty()
        val representative = b.copyOfRange(64, 96)
        val balance = NanoAmount.ofByteArray(b.copyOfRange(96, 112))
        val link = b.copyOfRange(112, 144).toNullWhenEmpty()
        val signature = b.copyOfRange(144, 208)
        val work = b.copyOfRange(208, 216)

        val transaction = TransactionStateBlock(
            hash = hash,
            publicKey = publicKey,
            previous = previous,
            representative = representative,
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