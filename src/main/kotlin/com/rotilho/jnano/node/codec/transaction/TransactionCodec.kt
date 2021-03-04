package com.rotilho.jnano.node.codec.transaction

import com.rotilho.jnano.node.block.Block
import com.rotilho.jnano.node.block.BlockType
import com.rotilho.jnano.node.codec.ByteArrayCodecSupport
import com.rotilho.jnano.node.codec.block.BlockCodec
import com.rotilho.jnano.node.transaction.Transaction
import com.rotilho.jnano.node.utils.flatMap
import com.rotilho.jnano.node.utils.fromShortBigEndian
import com.rotilho.jnano.node.utils.toShortBigEndian

class TransactionCodec constructor(private val blockCodec: BlockCodec) : ByteArrayCodecSupport {
    override fun encode(protocolVersion: Int, o: Any): ByteArray? {
        if (o !is Transaction<Block>) return null

        val block = o.block
        val encoded = blockCodec.encode(protocolVersion, o.block) ?: return null;
        return flatMap(toShortBigEndian(block.getType().code), encoded, o.signature, o.work);
    }

    override fun decode(protocolVersion: Int, m: ByteArray): Transaction<Block>? {
        val blockType = blockCodec.getType()
        if (BlockType.fromCode(fromShortBigEndian(m.copyOfRange(0, 2))) != blockType) {
            return null
        }
        val block = blockCodec.decode(protocolVersion, m.copyOfRange(2, m.size)) ?: return null
        val transactionIndex = blockCodec.size() + 2
        val signature = m.copyOfRange(transactionIndex, transactionIndex + 64)
        val work = m.copyOfRange(transactionIndex + signature.size, transactionIndex + signature.size + 8)
        return Transaction(block, signature, work);
    }
}