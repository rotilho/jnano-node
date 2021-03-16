package com.rotilho.jnano.node.codec.vote

import com.rotilho.jnano.node.codec.ByteArrayCodecSupport
import com.rotilho.jnano.node.utils.*
import com.rotilho.jnano.node.vote.Vote
import java.util.stream.IntStream
import kotlin.streams.toList

class VoteCodec : ByteArrayCodecSupport<Vote> {

    override fun encode(protocolVersion: Int, o: Any): ByteArray? {
        if (o !is Vote) {
            return null
        }
        return flatMap(
            toShortBigEndian(o.voteType),
            o.representativePublicKey,
            o.signature,
            toLongLittleEndian(o.sequence),
            *o.hashes.toTypedArray()
        )
    }

    override fun decode(protocolVersion: Int, m: ByteArray): Vote? {
        return null // doesn't work
        val voteType = fromShortBigEndian(m.copyOfRange(0, 2)) // this is wrong
        val representativePublicKey = m.copyOfRange(2, 34)
        val signature = m.copyOfRange(34, 98)
        val sequence = fromLongLittleEndian(m.copyOfRange(98, 106)) //TODO this is probably wrong but I couldn't figure out how to decode it yet
        val hashes = m.copyOfRange(106, m.size)
        val hashList = IntStream.range(0, hashes.size / 32)
                .mapToObj { hashes.copyOfRange(it * 32, (it + 1) * 32) }
                .toList()
        return Vote(voteType, representativePublicKey, signature, sequence, hashList)
    }
}