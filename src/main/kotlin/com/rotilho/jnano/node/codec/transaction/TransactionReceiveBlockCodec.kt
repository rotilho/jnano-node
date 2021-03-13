package com.rotilho.jnano.node.codec.transaction

import com.rotilho.jnano.commons.NanoAmount
import com.rotilho.jnano.commons.NanoHashes
import com.rotilho.jnano.node.transaction.BlockType
import com.rotilho.jnano.node.transaction.Transaction
import com.rotilho.jnano.node.transaction.BlockSubType
import com.rotilho.jnano.node.utils.flatMap
import com.rotilho.jnano.node.utils.toShortBigEndian
import java.math.BigInteger

class TransactionReceiveBlockCodec : TransactionCodec() {

    override fun hash(m: ByteArray): ByteArray {
        return NanoHashes.digest256(m.copyOfRange(0, BlockType.RECEIVE.blockSize))
    }

    override fun blockType(): BlockType {
        return BlockType.RECEIVE
    }

    override fun blockSubType(m: ByteArray): BlockSubType {
        return BlockSubType.RECEIVE
    }

    override fun accountVersion(m: ByteArray): BigInteger? {
        return null
    }

    override fun publicKey(m: ByteArray): ByteArray? {
        return null
    }

    override fun previous(m: ByteArray): ByteArray? {
        return m.copyOfRange(0, 32)
    }

    override fun representative(m: ByteArray): ByteArray? {
        return null
    }

    override fun balance(m: ByteArray): NanoAmount? {
        return null
    }

    override fun link(m: ByteArray): ByteArray? {
        return m.copyOfRange(32, 64)
    }

    override fun height(m: ByteArray): BigInteger? {
        return null
    }

    override fun signature(m: ByteArray): ByteArray {
        return m.copyOfRange(64, 128)
    }

    override fun work(m: ByteArray): ByteArray {
        return m.copyOfRange(128, 136)
    }

    override fun encode(protocolVersion: Int, o: Any): ByteArray? {
        if (o !is Transaction || o.blockType != BlockType.RECEIVE) return null

        return flatMap(toShortBigEndian(o.blockType.code), o.previous!!, o.link!!, o.signature, o.work)
    }
}