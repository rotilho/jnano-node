package com.rotilho.jnano.node.codec.transaction

import com.rotilho.jnano.commons.NanoAmount
import com.rotilho.jnano.commons.NanoHelper
import com.rotilho.jnano.commons.NanoSignatures
import com.rotilho.jnano.commons.NanoWorks
import com.rotilho.jnano.node.transaction.BlockType
import com.rotilho.jnano.node.codec.ByteArrayCodecSupport
import com.rotilho.jnano.node.transaction.BlockSubType
import com.rotilho.jnano.node.transaction.Transaction
import com.rotilho.jnano.node.utils.fromShortBigEndian
import java.math.BigInteger

abstract class TransactionCodec : ByteArrayCodecSupport {
    private val transactionId = 2
    private val transactionSize = 72

    abstract fun hash(m: ByteArray): ByteArray
    abstract fun blockType(): BlockType
    abstract fun blockSubType(m: ByteArray): BlockSubType?
    abstract fun accountVersion(m: ByteArray): BigInteger?
    abstract fun publicKey(m: ByteArray): ByteArray?
    abstract fun previous(m: ByteArray): ByteArray?
    abstract fun representative(m: ByteArray): ByteArray?
    abstract fun balance(m: ByteArray): NanoAmount?
    abstract fun link(m: ByteArray): ByteArray?
    abstract fun height(m: ByteArray): BigInteger?
    abstract fun signature(m: ByteArray): ByteArray
    abstract fun work(m: ByteArray): ByteArray

    override fun decode(protocolVersion: Int, m: ByteArray): Transaction? {
        if (m.size != blockType().blockSize + transactionId + transactionSize) {
            return null;
        }

        if (BlockType.fromCode(fromShortBigEndian(m.copyOfRange(0, transactionId))) != blockType()) {
            return null
        }

        val byteArrayTransaction = m.copyOfRange(2, m.size)

        val publicKey = publicKey(byteArrayTransaction)
        val previous = previous(byteArrayTransaction)
        val work = work(byteArrayTransaction)

        if (!NanoWorks.isValid(
                (previous ?: publicKey)!!,
                if (blockType() == BlockType.STATE) NanoHelper.reverse(work) else work
            )
        ) {
            return null
        }

        val hash = hash(byteArrayTransaction)
        val signature = signature(byteArrayTransaction)

        if (publicKey != null && !NanoSignatures.isValid(publicKey, hash, signature)) {
            return null
        }


        return Transaction(
            hash,
            blockType(),
            blockSubType(byteArrayTransaction),
            accountVersion(byteArrayTransaction),
            publicKey,
            previous,
            representative(byteArrayTransaction),
            balance(byteArrayTransaction),
            link(byteArrayTransaction),
            height(byteArrayTransaction),
            signature,
            work
        )
    }
}