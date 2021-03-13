package com.rotilho.jnano.node.codec.transaction

import com.rotilho.jnano.commons.NanoAmount
import com.rotilho.jnano.commons.NanoHashes
import com.rotilho.jnano.commons.NanoHelper.toByteArray
import com.rotilho.jnano.node.transaction.BlockType
import com.rotilho.jnano.node.transaction.BlockSubType
import com.rotilho.jnano.node.transaction.Transaction
import com.rotilho.jnano.node.utils.flatMap
import com.rotilho.jnano.node.utils.toShortBigEndian
import java.math.BigInteger

class TransactionStateBlockCodec : TransactionCodec() {
    private val preamble = toByteArray("0000000000000000000000000000000000000000000000000000000000000006")

    private val zeros = ByteArray(32)

    override fun hash(m: ByteArray): ByteArray {
        return NanoHashes.digest256(preamble, m.copyOfRange(0, BlockType.STATE.blockSize))
    }

    override fun blockType(): BlockType {
        return BlockType.STATE
    }

    override fun blockSubType(m: ByteArray): BlockSubType? {
        if (previous(m) == null) {
            return BlockSubType.OPEN
        }
        if (link(m) == null) {
            return BlockSubType.CHANGE
        }
        if ()

        return null
    }

    override fun accountVersion(m: ByteArray): BigInteger? {
        return null
    }

    override fun publicKey(m: ByteArray): ByteArray {
        return m.copyOfRange(0, 32)
    }

    override fun previous(m: ByteArray): ByteArray? {
        val previous = m.copyOfRange(32, 64)
        if (previous.all { it == 0.toByte() }) {
            return null
        }
        return previous
    }

    override fun representative(m: ByteArray): ByteArray? {
        return m.copyOfRange(64, 96)
    }

    override fun balance(m: ByteArray): NanoAmount {
        return NanoAmount.ofByteArray(m.copyOfRange(96, 112))
    }

    override fun link(m: ByteArray): ByteArray? {
        val link = m.copyOfRange(112, 144)
        if (link.all { it == 0.toByte() }) {
            return null
        }
        return link
    }

    override fun height(m: ByteArray): BigInteger? {
        return null
    }

    override fun signature(m: ByteArray): ByteArray {
        return m.copyOfRange(144, 208)
    }

    override fun work(m: ByteArray): ByteArray {
        return m.copyOfRange(208, 216)
    }

    override fun encode(protocolVersion: Int, o: Any): ByteArray? {
        if (o !is Transaction || o.blockType != BlockType.STATE) return null

        return flatMap(
            toShortBigEndian(o.blockType.code),
            o.publicKey!!,
            o.previous ?: zeros,
            o.representative!!,
            o.balance!!.toByteArray(),
            o.link ?: zeros,
            o.signature,
            o.work
        )
    }
}