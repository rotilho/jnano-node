package com.rotilho.jnano.node.transaction

import com.rotilho.jnano.commons.NanoAmount
import com.rotilho.jnano.node.utils.toHex
import java.math.BigInteger

enum class TransactionStatus {
    PENDING, CONFIRMED, CEMENTED
}

enum class BlockSubType {
    OPEN, RECEIVE, SEND, CHANGE, EPOCH
}

enum class BlockType(val code: Short, val blockSize: Int) {
    OPEN(4, 96),
    CHANGE(5, 64),
    RECEIVE(3, 64),
    SEND(2, 80),
    STATE(6, 144);

    companion object {
        private val CODE_MAP = values().associateBy(BlockType::code)
        fun fromCode(code: Short): BlockType? {
            return CODE_MAP[code]
        }
    }
}

open class Transaction(
    val hash: ByteArray,
    val blockType: BlockType,
    val blockSubtype: BlockSubType?,
    val accountVersion: BigInteger?,
    val publicKey: ByteArray?,
    val previous: ByteArray?,
    val representative: ByteArray?,
    val balance: NanoAmount?,
    val link: ByteArray?,
    val height: BigInteger?,
    val signature: ByteArray,
    val work: ByteArray
) {
    override fun toString(): String {
        return "Transaction(hash=${hash.toHex()}, blockType=$blockType, blockSubtype=$blockSubtype, accountVersion=$accountVersion, publicKey=${publicKey?.toHex()}, previous=${previous?.toHex()}, representative=${representative?.toHex()}, balance=$balance, link=${link?.toHex()}, height=$height, signature=${signature.toHex()}, work=${work.toHex()})"
    }
}

open class VotedTransaction(
    transaction: Transaction,
    val vote: Vote
) : Transaction(
    transaction.hash,
    transaction.blockType,
    transaction.blockSubtype,
    transaction.accountVersion,
    transaction.publicKey,
    transaction.previous,
    transaction.representative,
    transaction.balance,
    transaction.link,
    transaction.height,
    transaction.signature,
    transaction.work
)