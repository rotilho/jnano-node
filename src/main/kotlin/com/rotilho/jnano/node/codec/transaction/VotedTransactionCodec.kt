package com.rotilho.jnano.node.codec.transaction

import com.rotilho.jnano.commons.NanoSignatures
import com.rotilho.jnano.node.transaction.BlockType
import com.rotilho.jnano.node.transaction.TransactionVote
import com.rotilho.jnano.node.transaction.VotedTransaction
import com.rotilho.jnano.node.utils.flatMap
import com.rotilho.jnano.node.utils.fromLongLittleEndian
import com.rotilho.jnano.node.utils.toLongLittleEndian
import com.rotilho.jnano.node.utils.toShortBigEndian
import kotlin.reflect.KClass

class VotedTransactionCodec(private val transactionCodec: TransactionCodec<*>) : TransactionCodec<VotedTransaction> {

    override fun getBlockType(): BlockType {
        return transactionCodec.getBlockType()
    }

    override fun getTransactionClass(): KClass<VotedTransaction> {
        return VotedTransaction::class
    }

    override fun encodeTransaction(protocolVersion: Int, votedTransaction: VotedTransaction): ByteArray? {
        val encoded = transactionCodec.encode(protocolVersion, votedTransaction.transaction) ?: return null

        return flatMap(
            toShortBigEndian(votedTransaction.getBlockType().code),
            votedTransaction.vote.representativePublicKey,
            votedTransaction.vote.signature,
            toLongLittleEndian(votedTransaction.vote.sequence),
            encoded.copyOfRange(2, encoded.size)
        )
    }


    override fun decodeTransaction(protocolVersion: Int, b: ByteArray): VotedTransaction? {
        val transaction =
            transactionCodec.decode(protocolVersion, flatMap(getBlockType().byteArray, b.copyOfRange(104, b.size)))
                ?: return null

        val representativePublicKey = b.copyOfRange(0, 32)
        val signature = b.copyOfRange(32, 96)
        val sequence = b.copyOfRange(96, 104)

        if (!NanoSignatures.isValid(representativePublicKey, flatMap(transaction.getHash(), sequence), signature)) {
            return null
        }

        val vote =
            TransactionVote(transaction.getHash(), representativePublicKey, signature, fromLongLittleEndian(sequence))

        return VotedTransaction(transaction, vote)
    }
}