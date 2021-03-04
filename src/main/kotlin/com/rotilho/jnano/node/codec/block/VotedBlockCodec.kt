package com.rotilho.jnano.node.codec.block

import com.rotilho.jnano.node.block.BlockType
import com.rotilho.jnano.node.block.VotedBlock
import com.rotilho.jnano.node.utils.flatMap
import com.rotilho.jnano.node.utils.fromLongLittleEndian
import com.rotilho.jnano.node.utils.toLongLittleEndian

class VotedBlockCodec(private val blockCodec: BlockCodec) : BlockCodec {
    override fun getType(): BlockType = blockCodec.getType()

    override fun size(): Int {
        return blockCodec.size() + 104
    }

    override fun encode(protocolVersion: Int, o: Any): ByteArray? {
        if (o !is VotedBlock) {
            return null
        }
        val encoded = blockCodec.encode(protocolVersion, o.block) ?: return null
        return flatMap(o.representativePublicKey, o.signature, toLongLittleEndian(o.sequence), encoded)
    }

    override fun decode(protocolVersion: Int, m: ByteArray): VotedBlock? {
        val block = blockCodec.decode(protocolVersion, m.copyOfRange(104, m.size)) ?: return null
        val representativePublicKey = m.copyOfRange(0, 32)
        val signature = m.copyOfRange(32, 96)
        val sequence = fromLongLittleEndian(m.copyOfRange(96, 104)) //TODO this is probably wrong but I couldnt figure out how to decode it yet
        return VotedBlock(representativePublicKey, signature, sequence, block)
    }
}