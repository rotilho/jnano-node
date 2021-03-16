package com.rotilho.jnano.node.transaction

import com.rotilho.jnano.commons.NanoAccounts
import com.rotilho.jnano.commons.NanoAmount
import com.rotilho.jnano.commons.NanoSignatures
import com.rotilho.jnano.commons.NanoWorks
import com.rotilho.jnano.node.utils.toHex
import com.rotilho.jnano.node.utils.toShortBigEndian
import java.math.BigInteger

enum class TransactionStatus(val valid: Boolean) {
    RECEIVED(false), VALIDATED(true), CONFIRMED(true), CEMENTED(true);

    fun isValid(): Boolean {
        return valid
    }
}

enum class BlockSubType {
    OPEN, RECEIVE, SEND, CHANGE, EPOCH
}

enum class BlockType(val code: Short, val blockSize: Int, val byteArray: ByteArray) {
    OPEN(4, 96, toShortBigEndian(4)),
    CHANGE(5, 64, toShortBigEndian(5)),
    RECEIVE(3, 64, toShortBigEndian(3)),
    SEND(2, 80, toShortBigEndian(2)),
    STATE(6, 144, toShortBigEndian(6));

    companion object {
        private val CODE_MAP = values().associateBy(BlockType::code)
        fun fromCode(code: Short): BlockType? {
            return CODE_MAP[code]
        }
    }
}

interface Transaction {
    fun getHash(): ByteArray
    fun getStatus(): TransactionStatus
    fun setStatus(status: TransactionStatus)
    fun getVersion(): Int
    fun getBlockType(): BlockType
    fun getBlockSubType(): BlockSubType?
    fun setBlockSubType(blockSubType: BlockSubType)
    fun getAccountVersion(): BigInteger?
    fun setAccountVersion(accountVersion: BigInteger)
    fun getPublicKey(): ByteArray?
    fun setPublicKey(publicKey: ByteArray)
    fun getPrevious(): ByteArray?
    fun getRepresentative(): ByteArray?
    fun setRepresentative(representative: ByteArray)
    fun getBalance(): NanoAmount?
    fun setBalance(balance: NanoAmount)
    fun getLink(): ByteArray?
    fun getHeight(): BigInteger?
    fun setHeight(height: BigInteger)
    fun getSignature(): ByteArray
    fun getWork(): ByteArray
    fun isValid(): Boolean
}

data class VotedTransaction(
    val transaction: Transaction,
    val vote: TransactionVote
) : Transaction {
    override fun getHash() = transaction.getHash()
    override fun getStatus() = transaction.getStatus()
    override fun setStatus(status: TransactionStatus) {
        transaction.setStatus(status)
    }

    override fun getVersion() = transaction.getVersion()
    override fun getBlockType() = transaction.getBlockType()
    override fun getBlockSubType() = transaction.getBlockSubType()
    override fun setBlockSubType(blockSubType: BlockSubType) {
        transaction.setBlockSubType(blockSubType)
    }

    override fun getAccountVersion() = transaction.getAccountVersion()
    override fun setAccountVersion(accountVersion: BigInteger) {
        transaction.setAccountVersion(accountVersion)
    }

    override fun getPublicKey() = transaction.getPublicKey()
    override fun setPublicKey(publicKey: ByteArray) {
        transaction.setPublicKey(publicKey)
    }

    override fun getPrevious() = transaction.getPrevious()
    override fun getRepresentative() = transaction.getRepresentative()
    override fun setRepresentative(representative: ByteArray) {
        transaction.setRepresentative(representative)
    }

    override fun getBalance() = transaction.getBalance()
    override fun setBalance(balance: NanoAmount) {
        transaction.setBalance(balance)
    }

    override fun getLink() = transaction.getLink()
    override fun getHeight() = transaction.getHeight()
    override fun setHeight(height: BigInteger) {
        transaction.setHeight(height)
    }

    override fun getSignature() = transaction.getSignature()
    override fun getWork() = transaction.getWork()

    override fun isValid(): Boolean {
        return transaction.isValid() && vote.isValid()
    }

    override fun toString(): String {
        return "VotedTransaction(transaction=$transaction, vote=$vote)"
    }

}

data class TransactionOpenBlock(
    private val hash: ByteArray,
    private var status: TransactionStatus = TransactionStatus.RECEIVED,
    private val version: Int = 0,
    private var accountVersion: BigInteger? = null,
    private val publicKey: ByteArray,
    private var representative: ByteArray,
    private var balance: NanoAmount? = null,
    private val link: ByteArray,
    private val signature: ByteArray,
    private val work: ByteArray
) :
    Transaction {
    override fun getHash() = hash
    override fun getStatus() = status
    override fun setStatus(status: TransactionStatus) {
        this.status = status
    }

    override fun getVersion() = version
    override fun getBlockType() = BlockType.OPEN
    override fun getBlockSubType() = BlockSubType.OPEN
    override fun setBlockSubType(blockSubType: BlockSubType) {
        throw NotImplementedError()
    }

    override fun getAccountVersion() = accountVersion
    override fun setAccountVersion(accountVersion: BigInteger) {
        this.accountVersion = accountVersion
    }

    override fun getPublicKey() = publicKey
    override fun setPublicKey(publicKey: ByteArray) {
        throw NotImplementedError()
    }

    override fun getPrevious() = null
    override fun getRepresentative() = representative
    override fun setRepresentative(representative: ByteArray) {
        this.representative = representative
    }

    override fun getBalance() = balance
    override fun setBalance(balance: NanoAmount) {
        this.balance = balance
    }

    override fun getLink() = link
    override fun getHeight(): BigInteger {
        return BigInteger.ZERO
    }

    override fun setHeight(height: BigInteger) {
        throw NotImplementedError()
    }

    override fun getSignature() = signature
    override fun getWork() = work

    override fun isValid(): Boolean {
        return NanoWorks.isValid(publicKey, work, NanoWorks.THRESHOLD_EPOCH_1) && NanoSignatures.isValid(
            publicKey,
            hash,
            signature
        )
    }

    override fun toString(): String {
        return "TransactionOpenBlock(hash=${hash.toHex()}, accountVersion=$accountVersion, publicKey=${publicKey.toHex()}, representative=${representative.toHex()}, balance=$balance, link=${link.toHex()}, signature=${signature.toHex()}, work=${work.toHex()})"
    }


}

data class TransactionReceiveBlock(
    private val hash: ByteArray,
    private var status: TransactionStatus = TransactionStatus.RECEIVED,
    private val version: Int = 0,
    private var accountVersion: BigInteger? = null,
    private var publicKey: ByteArray? = null,
    private val previous: ByteArray,
    private var representative: ByteArray? = null,
    private var balance: NanoAmount? = null,
    private val link: ByteArray,
    private var height: BigInteger? = null,
    private val signature: ByteArray,
    private val work: ByteArray
) :
    Transaction {
    override fun getHash() = hash
    override fun getStatus() = status
    override fun setStatus(status: TransactionStatus) {
        this.status = status
    }

    override fun getVersion() = version
    override fun getBlockType() = BlockType.RECEIVE
    override fun getBlockSubType() = BlockSubType.RECEIVE
    override fun setBlockSubType(blockSubType: BlockSubType) {
        throw NotImplementedError()
    }

    override fun getAccountVersion() = accountVersion
    override fun setAccountVersion(accountVersion: BigInteger) {
        this.accountVersion = accountVersion
    }

    override fun getPublicKey() = publicKey
    override fun setPublicKey(publicKey: ByteArray) {
        this.publicKey = publicKey
    }

    override fun getPrevious() = previous
    override fun getRepresentative() = representative
    override fun setRepresentative(representative: ByteArray) {
        this.representative = representative
    }

    override fun getBalance() = balance
    override fun setBalance(balance: NanoAmount) {
        this.balance = balance
    }

    override fun getLink() = link
    override fun getHeight() = height
    override fun setHeight(height: BigInteger) {
        this.height = height
    }

    override fun getSignature() = signature
    override fun getWork() = work

    override fun isValid(): Boolean {
        if (!NanoWorks.isValid(previous, work, NanoWorks.THRESHOLD_EPOCH_1)) {
            return false
        }
        val publicKey = this.publicKey
        if (publicKey != null && !NanoSignatures.isValid(publicKey, hash, signature)) {
            return false
        }
        return true
    }

    override fun toString(): String {
        return "TransactionReceiveBlock(hash=${hash.toHex()}, accountVersion=$accountVersion, publicKey=${publicKey?.toHex()}, previous=${previous.toHex()}, representative=${representative?.toHex()}, balance=$balance, link=${link.toHex()}, height=$height, signature=${signature.toHex()}, work=${work.toHex()})"
    }

}

data class TransactionSendBlock(
    private val hash: ByteArray,
    private var status: TransactionStatus = TransactionStatus.RECEIVED,
    private val version: Int = 0,
    private var accountVersion: BigInteger? = null,
    private var publicKey: ByteArray? = null,
    private val previous: ByteArray,
    private var representative: ByteArray? = null,
    private val balance: NanoAmount,
    private val link: ByteArray,
    private var height: BigInteger? = null,
    private val signature: ByteArray,
    private val work: ByteArray
) :
    Transaction {
    override fun getHash() = hash
    override fun getStatus() = status
    override fun setStatus(status: TransactionStatus) {
        this.status = status
    }

    override fun getVersion() = version
    override fun getBlockType() = BlockType.SEND
    override fun getBlockSubType() = BlockSubType.SEND
    override fun setBlockSubType(blockSubType: BlockSubType) {
        throw NotImplementedError()
    }

    override fun getAccountVersion() = accountVersion
    override fun setAccountVersion(accountVersion: BigInteger) {
        this.accountVersion = accountVersion
    }

    override fun getPublicKey() = publicKey
    override fun setPublicKey(publicKey: ByteArray) {
        this.publicKey = publicKey
    }

    override fun getPrevious() = previous
    override fun getRepresentative() = representative
    override fun setRepresentative(representative: ByteArray) {
        this.representative = representative
    }

    override fun getBalance() = balance
    override fun setBalance(balance: NanoAmount) {
        throw NotImplementedError()
    }

    override fun getLink() = link
    override fun getHeight() = height
    override fun setHeight(height: BigInteger) {
        this.height = height
    }

    override fun getSignature() = signature
    override fun getWork() = work

    override fun isValid(): Boolean {
        if (!NanoWorks.isValid(previous, work, NanoWorks.THRESHOLD_EPOCH_1)) {
            return false
        }
        val publicKey = this.publicKey
        if (publicKey != null && !NanoSignatures.isValid(publicKey, hash, signature)) {
            return false
        }
        return true
    }

    override fun toString(): String {
        return "TransactionSendBlock(hash=${hash.toHex()}, accountVersion=$accountVersion, publicKey=${publicKey?.toHex()}, previous=${previous.toHex()}, representative=${representative?.toHex()}, balance=$balance, link=${link.toHex()}, height=$height, signature=${signature.toHex()}, work=${work.toHex()})"
    }

}

data class TransactionChangeBlock(
    private val hash: ByteArray,
    private var status: TransactionStatus = TransactionStatus.RECEIVED,
    private val version: Int = 0,
    private var accountVersion: BigInteger? = null,
    private var publicKey: ByteArray? = null,
    private val previous: ByteArray,
    private val representative: ByteArray,
    private var balance: NanoAmount? = null,
    private val link: ByteArray? = null,
    private val height: BigInteger? = null,
    private val signature: ByteArray,
    private val work: ByteArray
) :
    Transaction {
    override fun getHash() = hash
    override fun getStatus() = status
    override fun setStatus(status: TransactionStatus) {
        this.status = status
    }

    override fun getVersion() = version
    override fun getBlockType() = BlockType.CHANGE
    override fun getBlockSubType() = BlockSubType.CHANGE
    override fun setBlockSubType(blockSubType: BlockSubType) {
        throw NotImplementedError()
    }

    override fun getAccountVersion() = accountVersion
    override fun setAccountVersion(accountVersion: BigInteger) {
        this.accountVersion = accountVersion
    }

    override fun getPublicKey() = publicKey
    override fun setPublicKey(publicKey: ByteArray) {
        this.publicKey = publicKey
    }

    override fun getPrevious() = previous
    override fun getRepresentative() = representative
    override fun setRepresentative(representative: ByteArray) {
        throw NotImplementedError()
    }

    override fun getBalance() = balance
    override fun setBalance(balance: NanoAmount) {
        this.balance = balance
    }

    override fun getLink() = link
    override fun getHeight() = height
    override fun setHeight(height: BigInteger) {
        this.height
    }

    override fun getSignature() = signature
    override fun getWork() = work

    override fun isValid(): Boolean {
        if (!NanoWorks.isValid(previous, work, NanoWorks.THRESHOLD_EPOCH_1)) {
            return false
        }
        val publicKey = this.publicKey
        if (publicKey != null && !NanoSignatures.isValid(publicKey, hash, signature)) {
            return false
        }
        return true
    }

    override fun toString(): String {
        return "ChangeBlockTransaction(hash=${hash.toHex()}, accountVersion=$accountVersion, publicKey=${publicKey?.toHex()}, previous=${previous.toHex()}, representative=${representative.toHex()}, balance=$balance, link=${link?.toHex()}, height=$height, signature=${signature.toHex()}, work=${work.toHex()})"
    }

}


data class TransactionStateBlock(
    private val hash: ByteArray,
    private var status: TransactionStatus = TransactionStatus.RECEIVED,
    private val version: Int = 0,
    private var blockSubType: BlockSubType? = null,
    private var accountVersion: BigInteger? = null,
    private val publicKey: ByteArray,
    private val previous: ByteArray? = null,
    private val representative: ByteArray,
    private val balance: NanoAmount,
    private val link: ByteArray? = null,
    private var height: BigInteger? = null,
    private val signature: ByteArray,
    private val work: ByteArray
) :
    Transaction {

    companion object {
        private val workMultiplier = mapOf(
            null to mapOf(
                null to NanoWorks.THRESHOLD_EPOCH_2_RECEIVE,
                BlockSubType.OPEN to NanoWorks.THRESHOLD_EPOCH_2_RECEIVE,
                BlockSubType.RECEIVE to NanoWorks.THRESHOLD_EPOCH_2_RECEIVE,
                BlockSubType.SEND to NanoWorks.THRESHOLD_EPOCH_1,
                BlockSubType.CHANGE to NanoWorks.THRESHOLD_EPOCH_1
            ),
            1 to mapOf(
                BlockSubType.OPEN to NanoWorks.THRESHOLD_EPOCH_1,
                BlockSubType.RECEIVE to NanoWorks.THRESHOLD_EPOCH_1,
                BlockSubType.SEND to NanoWorks.THRESHOLD_EPOCH_1,
                BlockSubType.CHANGE to NanoWorks.THRESHOLD_EPOCH_1
            ),
            2 to mapOf(
                BlockSubType.OPEN to NanoWorks.THRESHOLD_EPOCH_2_RECEIVE,
                BlockSubType.RECEIVE to NanoWorks.THRESHOLD_EPOCH_2_RECEIVE,
                BlockSubType.SEND to NanoWorks.THRESHOLD_EPOCH_2_DEFAULT,
                BlockSubType.CHANGE to NanoWorks.THRESHOLD_EPOCH_2_DEFAULT
            )
        )
    }


    override fun getHash() = hash
    override fun getStatus() = status
    override fun setStatus(status: TransactionStatus) {
        this.status = status
    }

    override fun getVersion() = version
    override fun getBlockType() = BlockType.STATE
    override fun getBlockSubType(): BlockSubType? {
        if (blockSubType != null) {
            return blockSubType
        }

        if (previous == null) {
            return BlockSubType.OPEN
        }

        if (link == null && NanoSignatures.isValid(publicKey, hash, signature)) {
            return BlockSubType.CHANGE
        }

        if (link == null && NanoSignatures.isValid(NanoAccounts.MAIN_NET_GENESIS_PUBLIC_KEY, hash, signature)) {
            return BlockSubType.EPOCH
        }

        return null
    }

    override fun setBlockSubType(blockSubType: BlockSubType) {
        this.blockSubType = blockSubType
    }

    override fun getAccountVersion() = accountVersion
    override fun setAccountVersion(accountVersion: BigInteger) {
        this.accountVersion = accountVersion
    }

    override fun getPublicKey() = publicKey
    override fun setPublicKey(publicKey: ByteArray) {
        throw NotImplementedError()
    }

    override fun getPrevious() = previous
    override fun getRepresentative() = representative
    override fun setRepresentative(representative: ByteArray) {
        throw NotImplementedError()
    }

    override fun getBalance() = balance
    override fun setBalance(balance: NanoAmount) {
        throw NotImplementedError()
    }

    override fun getLink() = link
    override fun getHeight(): BigInteger? {
        if (getBlockSubType() == BlockSubType.OPEN) {
            BigInteger.ZERO
        }
        return height
    }

    override fun setHeight(height: BigInteger) {
        if (getBlockSubType() != BlockSubType.OPEN) {
            this.height = height
        }
    }

    override fun getSignature() = signature
    override fun getWork() = work

    override fun isValid(): Boolean {
        val threshold = workMultiplier[getAccountVersion()]!![getBlockSubType()]!!

        if (!NanoWorks.isValid(previous ?: publicKey, work.reversedArray(), threshold)) {
            return false
        }

        if (!NanoSignatures.isValid(
                publicKey,
                hash,
                signature
            ) && !NanoSignatures.isValid(NanoAccounts.MAIN_NET_GENESIS_PUBLIC_KEY, hash, signature)
        ) {
            return false
        }

        return true
    }

    override fun toString(): String {
        return "TransactionStateBlock(hash=${hash.toHex()}, blockSubType=$blockSubType, accountVersion=$accountVersion, publicKey=${publicKey.toHex()}, previous=${previous?.toHex()}, representative=${representative.toHex()}, balance=$balance, link=${link?.toHex()}, height=$height, signature=${signature.toHex()}, work=${work.toHex()})"
    }

}


data class TransactionStateBlock2(
    private val hash: ByteArray,
    private var status: TransactionStatus = TransactionStatus.RECEIVED,
    private val version: Int = 0,
    private var blockSubType: BlockSubType,
    private var accountVersion: BigInteger,
    private val publicKey: ByteArray,
    private val previous: ByteArray? = null,
    private val representative: ByteArray,
    private val balance: NanoAmount,
    private val link: ByteArray,
    private var height: BigInteger,
    private val signature: ByteArray,
    private val work: ByteArray
) :
    Transaction {

    companion object {
        private val workMultiplier = mapOf(
            2 to mapOf(
                BlockSubType.OPEN to NanoWorks.THRESHOLD_EPOCH_2_RECEIVE,
                BlockSubType.RECEIVE to NanoWorks.THRESHOLD_EPOCH_2_RECEIVE,
                BlockSubType.SEND to NanoWorks.THRESHOLD_EPOCH_2_DEFAULT,
                BlockSubType.CHANGE to NanoWorks.THRESHOLD_EPOCH_2_DEFAULT
            )
        )
    }


    override fun getHash() = hash
    override fun getStatus() = status
    override fun setStatus(status: TransactionStatus) {
        this.status = status
    }

    override fun getVersion() = version
    override fun getBlockType() = BlockType.STATE
    override fun getBlockSubType(): BlockSubType {
        return blockSubType
    }

    override fun setBlockSubType(blockSubType: BlockSubType) {
        throw NotImplementedError()
    }

    override fun getAccountVersion() = accountVersion
    override fun setAccountVersion(accountVersion: BigInteger) {
        throw NotImplementedError()
    }

    override fun getPublicKey() = publicKey
    override fun setPublicKey(publicKey: ByteArray) {
        throw NotImplementedError()
    }

    override fun getPrevious() = previous
    override fun getRepresentative() = representative
    override fun setRepresentative(representative: ByteArray) {
        throw NotImplementedError()
    }

    override fun getBalance() = balance
    override fun setBalance(balance: NanoAmount) {
        throw NotImplementedError()
    }

    override fun getLink() = link
    override fun getHeight(): BigInteger {
        return height
    }

    override fun setHeight(height: BigInteger) {
        throw NotImplementedError()
    }

    override fun getSignature() = signature
    override fun getWork() = work

    override fun isValid(): Boolean {
        val threshold = workMultiplier[getAccountVersion()]!![getBlockSubType()]!!

        if (!NanoWorks.isValid(previous ?: publicKey, work.reversedArray(), threshold)) {
            return false
        }

        if (blockSubType != BlockSubType.EPOCH && !NanoSignatures.isValid(publicKey, hash, signature)) {
            return false
        }

        if (blockSubType == BlockSubType.EPOCH && !NanoSignatures.isValid(
                NanoAccounts.MAIN_NET_GENESIS_PUBLIC_KEY,
                hash,
                signature
            )
        ) {
            return false
        }

        return true
    }

    override fun toString(): String {
        return "TransactionStateBlock2(hash=${hash.contentToString()}, status=$status, version=$version, blockSubType=$blockSubType, accountVersion=$accountVersion, publicKey=${publicKey.contentToString()}, previous=${previous?.contentToString()}, representative=${representative.contentToString()}, balance=$balance, link=${link.contentToString()}, height=$height, signature=${signature.contentToString()}, work=${work.contentToString()})"
    }
}