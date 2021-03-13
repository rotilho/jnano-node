package com.rotilho.jnano.node.codec.transaction

import com.rotilho.jnano.commons.NanoAmount
import com.rotilho.jnano.commons.NanoSignatures
import com.rotilho.jnano.node.transaction.BlockType
import com.rotilho.jnano.node.transaction.BlockSubType
import com.rotilho.jnano.node.transaction.Transaction
import com.rotilho.jnano.node.transaction.Vote
import com.rotilho.jnano.node.transaction.VotedTransaction
import com.rotilho.jnano.node.utils.flatMap
import com.rotilho.jnano.node.utils.fromLongLittleEndian
import com.rotilho.jnano.node.utils.toLongLittleEndian
import com.rotilho.jnano.node.utils.toShortBigEndian
import java.math.BigInteger

class VotedTransactionCodec(val transactionCodec: TransactionCodec) : TransactionCodec() {

    override fun hash(m: ByteArray): ByteArray {
        return transactionCodec.hash(m)
    }

    override fun blockType(): BlockType {
        return transactionCodec.blockType()
    }

    override fun blockSubType(m: ByteArray): BlockSubType? {
        return transactionCodec.blockSubType(m)
    }

    override fun accountVersion(m: ByteArray): BigInteger? {
        return transactionCodec.accountVersion(m)
    }

    override fun publicKey(m: ByteArray): ByteArray? {
        return transactionCodec.publicKey(m)
    }

    override fun previous(m: ByteArray): ByteArray? {
        return transactionCodec.previous(m)
    }

    override fun representative(m: ByteArray): ByteArray? {
        return transactionCodec.representative(m)
    }

    override fun balance(m: ByteArray): NanoAmount? {
        return transactionCodec.balance(m)
    }

    override fun link(m: ByteArray): ByteArray? {
        return transactionCodec.link(m)
    }

    override fun height(m: ByteArray): BigInteger? {
        return transactionCodec.height(m)
    }

    override fun signature(m: ByteArray): ByteArray {
        return transactionCodec.signature(m)
    }

    override fun work(m: ByteArray): ByteArray {
        return transactionCodec.work(m)
    }

    override fun encode(protocolVersion: Int, o: Any): ByteArray? {
        if (o !is VotedTransaction || o.blockType != BlockType.OPEN) return null

        val encoded = transactionCodec.encode(protocolVersion, o)?: return null

        return flatMap(
            toShortBigEndian(o.blockType.code),
            o.vote.representativePublicKey,
            o.vote.signature,
            toLongLittleEndian(o.vote.sequence),
            encoded.copyOfRange(2, encoded.size)
        )
    }

    override fun decode(protocolVersion: Int, m: ByteArray): Transaction? {
        val transaction = super.decode(protocolVersion, flatMap(m.copyOfRange(0, 2), m.copyOfRange(106, m.size))) ?: return null

        val byteArrayVote = m.copyOfRange(2, m.size)
        val representativePublicKey = byteArrayVote.copyOfRange(0, 32)
        val signature = byteArrayVote.copyOfRange(32, 96)
        val sequence = byteArrayVote.copyOfRange(96, 104)

        if (!NanoSignatures.isValid(
                representativePublicKey,
                flatMap(transaction.hash, sequence),
                signature
            )
        ) {
            return null
        }

        val vote = Vote(transaction.hash, representativePublicKey, signature, fromLongLittleEndian(sequence))

        return VotedTransaction(transaction, vote)

    }
}